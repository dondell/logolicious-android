package com.olav.logolicious.util.cacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.olav.logolicious.BuildConfig;
import com.olav.logolicious.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruImageCache {
	//http://stackoverflow.com/questions/10185898/using-disklrucache-in-android-4-0-does-not-provide-for-opencache-method#
	File diskCacheDir = null;
	SharedPreferences preferences;
    private DiskLruCache mDiskCache;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 10MB
    private static final String TAG = "DiskLruImageCache";

    public DiskLruImageCache( Context context,String uniqueName) {
    	preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            File tmp = getDiskCacheDir(context, uniqueName );
               if(null == tmp)
                   return;
            diskCacheDir = tmp;
            mDiskCache = DiskLruCache.open( diskCacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE );
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
        throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), Utils2.IO_BUFFER_SIZE );
            return bitmap.compress(
            		FileUtil.getImageCompressType(preferences), 
            		FileUtil.getCompressQuality(preferences), 
            		out);
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

    // Check if media is mounted or storage is built-in, if so, try and use external cache dir
    // otherwise use internal cache dir
        final String cachePath =
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                    !Utils2.isExternalStorageRemovable() ?
                    		Utils2.getExternalCacheDir(context).getPath() :
                    context.getCacheDir().getPath();

        File tempFile = new File(cachePath + File.separator + uniqueName);
        if(null == tempFile)
            tempFile = new File(cachePath + File.separator + uniqueName);

        return tempFile;
    }

    public boolean put( String key, Bitmap data ) {
        if(null == mDiskCache)
            return false;

    	boolean bRet = true;
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit( key );
            if ( editor == null ) {
                return false;
            }

            if( writeBitmapToFile( data, editor ) ) {               
                mDiskCache.flush();
                editor.commit();
                if ( BuildConfig.DEBUG ) {
                   Log.d( "cache_test_DISK_", "image put on disk cache " + key );
                }
            } else {
                editor.abort();
                if ( BuildConfig.DEBUG ) {
                    Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                    bRet = false;
                }
            }   
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
                Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                bRet = false;
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }           
        }

        return bRet;
    }

    public Bitmap getBitmap( String key ) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get( key );
            if ( snapshot == null ) {
                return null;
            }
            final InputStream in = snapshot.getInputStream( 0 );
            if ( in != null ) {
                final BufferedInputStream buffIn = 
                new BufferedInputStream( in, Utils2.IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream( buffIn );              
            }   
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
        }

        return bitmap;

    }

    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }
    
    public void clearCache(){
        if(null == mDiskCache)
            return;

    	try {
			DiskLruCache.deleteContents(diskCacheDir);
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }

    public void deleteCache() {
        if(null == mDiskCache)
            return;

        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

}