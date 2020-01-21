package com.olav.logolicious.util.image;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;

import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapSaver {

	public static final String TAG = "BitmapSaver";
	public static Bitmap myCanvasBitmap = null;

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			if (null != cursor && cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				return cursor.getString(column_index);
			}
		} catch (Exception e) {
			Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
			return contentUri.getPath();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return "";
	}

    public static String getImagePathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri); // context needed
                File photoFile = createTemporalFileFrom(inputStream);

                filePath = photoFile.getPath();

            } catch (FileNotFoundException e) {
                // log
            } catch (IOException e) {
                // log
            }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(InputStream inputStream) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile();
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private static File createTemporalFile() {
        return new File(GlobalClass.getAppContext().getExternalCacheDir(), "LogoLiciousTempFile.jpg"); // context needed
    }

	public static Bitmap exifBitmapOrientationCorrector(Context context, Uri uri){

		Bitmap realImage = null;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is != null) {
            realImage = BitmapFactory.decodeStream(is);
        }

        ExifInterface exif = null;
        try {
            String strRealPath = getRealPathFromURI(context, uri);
            //Safe check if the retrieving of image fail for cloud apps
            if(null == strRealPath) {
                strRealPath = getImagePathFromInputStreamUri(context, uri);
                realImage = BitmapFactory.decodeFile(strRealPath);
            }
            exif = new ExifInterface(strRealPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
        LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 0);
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){

			realImage= ImageHelper.rotateImage(realImage, 90);
			LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 90);
			Log.i("xxx","xxx exifBitmapOrientationCorrector 90");
		}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
			realImage= ImageHelper.rotateImage(realImage, 270);
			LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 270);
            Log.i("xxx","xxx exifBitmapOrientationCorrector 270");
		}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
			realImage= ImageHelper.rotateImage(realImage, 180);
			LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 180);
            Log.i("xxx","xxx exifBitmapOrientationCorrector 180");
		}
		return realImage;
	}

	/**
	 *
	 * @param context
	 * @param path
	 * @return Orientation corrector for Photo from Camera. It will decrease quality to avoid memory issue
	 */
	public static Bitmap exifBitmapOrientationCorrector(Context context, String path){
		Bitmap realImage = ImageHelper.decodeBitmapPath(path); //this will decrease memory issue if OOM error

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(null == exif)
			return realImage;

		Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
		LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 0);
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){

			realImage= ImageHelper.rotateImage(realImage, 90);
			LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 90);
			Log.i("xxx","xxx exifBitmapOrientationCorrector 90");
		}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
			realImage= ImageHelper.rotateImage(realImage, 270);
			LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 270);
			Log.i("xxx","xxx exifBitmapOrientationCorrector 270");
		}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
			realImage= ImageHelper.rotateImage(realImage, 180);
			LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 180);
			Log.i("xxx","xxx exifBitmapOrientationCorrector 180");
		}
		return realImage;
	}

	//same function of exifBitmapOrientationCorrector
	public static Bitmap exifLogoBitmapOrientationCorrector(Context context, String path){
		Bitmap realImage = ImageHelper.decodeBitmapPath(path);

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(null == exif)
			return realImage;

		Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){

			realImage= ImageHelper.rotateImage(realImage, 90);
		}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
			realImage= ImageHelper.rotateImage(realImage, 270);
		}else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
			realImage= ImageHelper.rotateImage(realImage, 180);
		}
		return realImage;
	}
	
	public static String saveBitmape(SharedPreferences sp, String dir, String filename, Bitmap bitmap){

	    if(null == bitmap)
	        return "";

		myCanvasBitmap = bitmap;
	    File myDir = new File(dir);    
	    myDir.mkdirs();
	    String fname = "Image-"+ filename +"." + FileUtil.getImageType(sp);
	    File file = new File (myDir, fname);
	    if (file.exists ()) file.delete (); 
	    try {
	           FileOutputStream out = new FileOutputStream(file);
	           if(null == sp)
	           		return "";
	           myCanvasBitmap.compress(FileUtil.getImageCompressType(sp), 100, out);
	           out.flush();
	           out.close();

	    } catch (Exception e) {
	           e.printStackTrace();
	    }
	    
	    return dir + fname;
	}

    /**
     *
     * @param pathFile
     * @param bitmap
     * @return This will be use for logo upload to handle logo with transparent background.
     */
	public static String saveLogoBitmape(String pathFile, Bitmap bitmap){
		myCanvasBitmap = bitmap;
		File file = new File (pathFile);
		if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file);
            if(pathFile.endsWith(".PNG") || pathFile.endsWith(".png") || pathFile.endsWith(".GIF") || pathFile.endsWith(".gif"))
                myCanvasBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            else
                myCanvasBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pathFile;
	}

	public static void saveBitmap(String filename, String pathToSave, View view){
		myCanvasBitmap = view.getDrawingCache().copy(Config.ARGB_8888, false);
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + pathToSave);
		myDir.mkdirs();
//	    Random generator = new Random();
//	    int n = 10000;
//	    n = generator.nextInt(n);
		String fname = "Image-"+ filename +".jpg";
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file);
			myCanvasBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String saveBitmapeLowRes(SharedPreferences sp, String dir, String filename, Bitmap bitmap){
		myCanvasBitmap = bitmap;
		File myDir = new File(dir);
		myDir.mkdirs();
		String fname = "Image-"+ filename +"." + FileUtil.getImageType(sp);
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file);
			myCanvasBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dir + fname;
	}
	
	public static Uri addImageToGallery(Context context, String filepath, String title, String description) {    
		String root = Environment.getExternalStorageDirectory().toString();
		ContentValues values = new ContentValues();
	    values.put(Media.TITLE, title);
	    values.put(Media.DESCRIPTION, description); 
	    values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
	    values.put(Images.Media.MIME_TYPE, "image/jpeg");
	    values.put(MediaStore.MediaColumns.DATA, root + filepath);

	    return context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
	}
	
}
