package com.olav.logolicious.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import androidx.collection.LruCache;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.olav.logolicious.R;
import com.olav.logolicious.billingv3.AppExecutors;
import com.olav.logolicious.billingv3.Constants;
import com.olav.logolicious.billingv3.billing.BillingClientLifecycle;
import com.olav.logolicious.billingv3.data.DataRepository;
import com.olav.logolicious.billingv3.data.disk.AppDatabase;
import com.olav.logolicious.billingv3.data.disk.LocalDataSource;
import com.olav.logolicious.billingv3.data.network.WebDataSource;
import com.olav.logolicious.billingv3.data.network.firebase.FakeServerFunctions;
import com.olav.logolicious.billingv3.data.network.firebase.ServerFunctions;
import com.olav.logolicious.billingv3.data.network.firebase.ServerFunctionsImpl;
import com.olav.logolicious.customize.datamodel.ImageExif;
import com.olav.logolicious.util.cacher.DiskLruImageCache;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ReportsCrashes(formKey = "", // will not be used
        mailTo = "helpdesk@thelaughingdutchmen.com", // support email here
        mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class GlobalClass extends Application {

    public static SQLiteHelper sqLiteHelper;
    public static Bitmap baseBitmap;
    public static ImageExif baseImageExif = new ImageExif();
    public static int origBitmapheight;
    public static int origBitmapwidth;
    public static int screenCropperWidth;
    public static int screenCropperHeigth;
    public static int APP_MODE = 0;
    public static int MODE_PRO = 1;
    public static int MODE_PUBLISH = 2;
    public static ImageLoader imageLoader = ImageLoader.getInstance(); // Get
    // singleton
    // instance

    public static final String App_Files_location = ".Logolicious";
    public static String picturePath; //this is changing path
    public static String logoPath;
    public static String log_path = null;

    public static DiskLruImageCache diskCache;
    public static LruCache<String, Bitmap> mMemoryCache;

    public static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
    //public static boolean ARFirst = true;
    public static String AR = "";
    public static String ARLast = "";
    public static boolean isFreeChoosenAR = false;
    public static boolean subscriptionOkToShow = false;

    public static int PICK_FONT_RESULT_CODE = 1002;

    // new code
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    public static native void dlmalloc();

    public static native void malloc(int bytes);

    public static native void freeMem();

    public static native void freeMemSize(int bytes);

    private static Context mContext = null;

    public static Context getAppContext() {
        return mContext;
    }

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public static boolean pendingShowMemAlert = false;

    //Acra
    public static String LOGO_UPLOADED = "LOGO_UPLOADED_";
    public static int LOGO_UPLOADED_COUNT = 0;
    public static String LOGO_SELECTED = "LOGO_SELECTED_";
    public static int LOGO_SELECTED_COUNT = 0;
    public static String LOGO_APPLY_TEMPLATE = "LOGO_FROM_TEMPLATE_";
    public static int LOGO_APPLY_TEMPLATE_COUNT = 0;
    public static String PICTURE_SIZE = "PICTURE_SIZE";
    public static String ABOVEOREQUAL_15MB_WARNING_RAISED = "ABOVEOREQUAL_15MB_WARNING_RAISED";
    public static String GENERATED_DATE = "GENERATED_DATE";
    public static String MEM_LOW_WARNING_RAISED = "MEM_LOW_WARNING_RAISED";
    public static String APP_AVAILABLE_MEM_SIZE = "APP_AVAILABLE_MEM_SIZE";

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        ACRA.getErrorReporter().putCustomData(GlobalClass.GENERATED_DATE, LogoliciousApp.getFullTimeStamp());
        ACRA.getErrorReporter().putCustomData(GlobalClass.MEM_LOW_WARNING_RAISED, "No");
        APP_MODE = MODE_PUBLISH;
        super.onCreate();

        mContext = getApplicationContext();
        //malloc(100 * 1024 * 1024);
        File root = Environment.getExternalStorageDirectory();
        String fs = File.separator;
        log_path = root + fs + App_Files_location + fs + "LogoLiciousLog.txt";
        if (null != log_path) {
            FileUtil.deleteDirectoryFiles(new File(log_path));
            FileUtil.fileWrite(log_path, "Starting Logolicious", true);
        }
        // initialize only if the activity call is not from the cropper.
        if (LogoliciousApp.verifyStoragePermissionsWithoutPrompt(this))
            initDiskCache(this);
        initMemCache(this);
        sqLiteHelper = new SQLiteHelper(this);
        Log.i("xxx", "xxx Database Path " + sqLiteHelper.getReadableDatabase().getPath());
        //SQLiteHelper.exportDatabase(this);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    public static void initDiskCache(Context ctx) {
//		FileUtil.fileWrite(log_path, "GlobalClass.java Initializing Cacher", true);	
        String currentDateandTime = df.format(new Date());
        diskCache = new DiskLruImageCache(ctx, "LogoliciousCache_" + currentDateandTime);
    }

    public static void initMemCache(Context ctx) {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//	    FileUtil.fileWrite(log_path, "->>maxMemory " + maxMemory, true);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                int byteCount = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    byteCount = bitmap.getByteCount();
                } else {
                    byteCount = bitmap.getRowBytes() * bitmap.getHeight();
                }
                return byteCount / 1024;
            }
        };
    }

    public static String getAR() {

        if (null == GlobalClass.baseBitmap)
            return "";

        int x = GlobalClass.baseBitmap.getWidth();
        int y = GlobalClass.baseBitmap.getHeight();

        if (LogoliciousApp.strIsNullOrEmpty(AR)) {
            String finalAR;

            if (x > 0 && y > 0) {
                finalAR = "_1:";
                if (x > y) {
                    double a = (double) x / (double) y;
                    String[] arr1 = String.valueOf(String.format(Locale.US, "%.1f", a)).split("\\.");
                    if (String.valueOf(a).contains(".") && arr1.length > 1) {
                        finalAR = finalAR + arr1[0] + "." + arr1[1];
                    } else
                        finalAR = finalAR + arr1[0];
                } else {
                    double a = (double) y / (double) x;
                    String[] arr1 = String.valueOf(String.format(Locale.US, "%.1f", a)).split("\\.");
                    if (String.valueOf(a).contains(".") && arr1.length > 1) {
                        finalAR = finalAR + arr1[0] + "." + arr1[1];
                    } else
                        finalAR = finalAR + arr1[0];
                    Log.i("xxx", "xxx y>x " + a);
                }
                return finalAR;
            }
            return "";
        } else {
            Map<String, Integer> ars = new HashMap<>();
            ars.put("1:1", 1);
            ars.put("2:3", 2);
            ars.put("3:2", 3);
            ars.put("3:4", 4);
            ars.put("4:3", 5);
            ars.put("9:16", 6);
            ars.put("16:9", 7);
            if (ars.containsKey(AR)) {
                return "_" + AR;
            }
        }
        return "";
    }

    @Override
    public void onTrimMemory(final int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) { // Works for Activity
            // Get called every-time when application went to background.
        } else if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) { // Works for FragmentActivty
        } else if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            ACRA.getErrorReporter().putCustomData("MEM_LOW_WARNING_RAISED", "Yes");
            System.gc();
            if (null != getCurrentActivity()) {
                if (null != LogoliciousApp.subsDialog) {
                    pendingShowMemAlert = true;
                } else {
                    LogoliciousApp.showMessageOK(getCurrentActivity(), getString(R.string.MemoryLowAlertMessageV2), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                }
            }
        }
    }

    private final AppExecutors executors = new AppExecutors();

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public LocalDataSource getLocalDataSource() {
        return LocalDataSource.getInstance(executors, getDatabase());
    }

    public ServerFunctions getServerFunctions() {
        if (Constants.USE_FAKE_SERVER) {
            return FakeServerFunctions.getInstance();
        } else {
            return ServerFunctionsImpl.getInstance();
        }
    }

    public WebDataSource getWebDataSource() {
        return WebDataSource.getInstance(executors, getServerFunctions());
    }

    public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository
                .getInstance(getLocalDataSource(), getWebDataSource(), getBillingClientLifecycle());
    }

}