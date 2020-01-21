package com.olav.logolicious.screens.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.InputFilter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.olav.logolicious.R;
import com.olav.logolicious.customize.adapters.AdapterGridLogos;
import com.olav.logolicious.customize.adapters.ArrayHolderLogos;
import com.olav.logolicious.customize.adapters.TemplateListAdapter;
import com.olav.logolicious.customize.datamodel.ImageExif;
import com.olav.logolicious.customize.widgets.DynamicImageView;
import com.olav.logolicious.customize.widgets.LayersContainerView;
import com.olav.logolicious.supertooltips.ToolTip;
import com.olav.logolicious.supertooltips.ToolTipRelativeLayout;
import com.olav.logolicious.supertooltips.ToolTipView;
import com.olav.logolicious.util.BillingService;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.SubscriptionUtil.AppStatitics;
import com.olav.logolicious.util.SubscriptionUtil.SubscriptionUtil;
import com.olav.logolicious.util.camera.CameraUtils;
import com.olav.logolicious.util.camera.ScreenDimensions;
import com.olav.logolicious.util.image.BitmapSaver;
import com.olav.logolicious.util.image.ImageHelper;

import org.acra.ACRA;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.olav.logolicious.util.GlobalClass.PICK_FONT_RESULT_CODE;
import static com.olav.logolicious.util.GlobalClass.sqLiteHelper;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivityMainEditor extends Activity implements
        OnTouchListener,
        ToolTipView.OnToolTipViewClickedListener,
        OnClickListener,
        BillingProcessor.IBillingHandler {

    public static BillingProcessor bp;
    public static TransactionDetails td;
    public static PurchaseInfo pInfo;
    private static final String TAG = "ActivityMainEditor";
    public static final String App_Files_location = ".Logolicious";
    public static Activity act = null;

    public static String logoDir = null;
    // this is where I saved crop images
    public static String tempDir = null;
    // this is where the shared picture is saved
    public static String tempShareDir = null;
    // this is where the save to device pictures is saved
    public static String tempSavedPics = null;
    // this is where the designed logos
    public static String designedLogos = null;
    // this is where I saved live medias
    public static String liveDir = null;
    // this is where the user fonts will be save
    public static String fontsDir = null;
    // EraserView Additional Width for the image to fit
    public static Bitmap bmp2;
    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;
    // Request codes for activity results
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_CHOOSE_FROM_GALLERY = 2;
    private static final int REQUEST_CODE_UPLOAD_LOGOS = 3;
    private static final int REQUEST_SHARE_ACTION = 4;
    private static final int REQUEST_CODE_CHOOSE_LOGO_FROM_GAL = 5;
    private static final int REQUEST_CODE_BROWSE_FILES = 6;

    /**
     * Draggable ImageView for funny images
     */
    public static LayersContainerView layeredLogos;
    public static int seekBarTransparent;

    /**
     * ImageButtons for the main functions
     */
    public static ImageButton buttonCam, buttonTrashcan;
    private boolean isShowItems = false;
    public static SeekBar seekbarTrans;
    // Tooltip Funny election
    private static ToolTipView mLogosView, mSavedTemplate, mPrefabsLogosView;
    private ToolTipRelativeLayout mToolTipFrameLayout;
    ToolTipView selectedToolTipView;
    private static View viewAboutInfo, viewColor;
    private static boolean isAbountInfoShown = false;

    // these matrices will be used to move and zoom image
    public static Matrix mMatrix = new Matrix();

    public static LinearLayout bottomSlidersContainer;
    public static RelativeLayout resultingScreen, listRight;
    public static ImageView backgroundImage;
    private static ImageView ivSOG;

    private DynamicImageView imageViewLogo;
    private static ArrayList<ArrayHolderLogos> mylogoItems = new ArrayList<ArrayHolderLogos>();
    private static ArrayList<ArrayHolderLogos> prefablogoItems = new ArrayList<ArrayHolderLogos>();
    // handler
    public static final int MESSAGE_SAVING_IMAGE = 0;
    public static final int MESSAGE_SHARING_IMAGE = 2;
    public static final int MESSAGE_SHARING_IMAGE2 = 3;
    public static final int MESSAGE_SHARING_IMAGE_ERROR = 4;
    public static final int MESSAGE_APPLY_TEMPLATE_ERROR = 5;
    public static final int MESSAGE_SAVING_IMAGE_ERROR = 6;
    public static final int MESSAGE_APPLY_TEMPLATE = 7;
    private static File fileToSave = null;
    private GridView gridview;
    private ListView templateLV;
    private AdapterGridLogos adapterFunnyGridItems;
    // Hint
    private View hintLayout;
    private static int what;
    public static boolean isSomeActivityIsRunning = false;

    public static float bW, bH;
    private int mAboutFragDialog = 0;

    public static SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //available keys : SavingType, dontAskMeAgain

    //rating
    private int isRated = 0;
    private int saveCount = 0;
    private int id = 0; // rating id in logolicious table

    // Live Button Feature
    Camera mCamera;
    SurfaceView mPreview;
    SurfaceHolder surfaceHolder;

    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;

    private Button start, stop, capture;

    private View live_panel;

    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        Log.i("xxx", "xxx onProductPurchased Subscription Success!");
        AppStatitics.sharedPreferenceSet(act, "isSubscribed", 1);
        if (null != LogoliciousApp.subsDialog) {
            LogoliciousApp.subsDialog.cancel();
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
        Log.i("xxx", "xxx Subscription Failed!");
        AppStatitics.sharedPreferenceSet(act, "isSubscribed", 0);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
//        Toast.makeText(this, "Subscription Has been Restored!", Toast.LENGTH_SHORT).show();
        Log.i("xxx", "xxx Subscription Restored!");
    }

    public enum Live_Camera {
        LIVE_PICTURE,
        LIVE_VIDEO
    }

    private static int LIVE_SELECTED = -1;
    private static int LIVE_VIDEO = 1;
    private static int LIVE_CAMERA = 2;

    // camera LIVE
    RelativeLayout.LayoutParams fullScreenParams;
    public static int fullWidth = 0;
    public static int fullHeight = 0;
    int left_margins = 0;
    int top_margins = 0;

    private com.olav.logolicious.util.camera.CameraUtils mCamUtils = null;
    private RelativeLayout cameraLayout = null;

    private LinearLayout parentView;

    public static int mOrientation;
    private static Resources res;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 2;
    public static final int PROMO_CODE = 143;

    File root;

    /**
     * Action to be performed when image is capture is clicked
     */
    @SuppressWarnings("unused")
    private OnClickListener OnCapture = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamUtils.clickPicture();
        }
    };

    private static final int PREFAB = 1;
    private static final int MYLOGOS = 2;
    private EditText editTextResizeVal;
    public static boolean isMinimized = false;
    public static String picturePath = "";

    protected GlobalClass gc;

    /**
     * Callback called by the camera Utils when image file has been created
     */
    private CameraUtils.ImageClicked onImageClick = new CameraUtils.ImageClicked() {
        @Override
        public void imageClicked(File pictureFile) {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{pictureFile.getPath()}, null, null);
        }

        @Override
        public void flashSet(String flashMode) {
        }

        @Override
        public void hideFlipButton() {
        }

        @Override
        public void enableFlashButton(boolean flag) {
        }

        @Override
        public void CameraUnAvailable() {
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("xxx", "xxx onCreate");
        /*http://stackoverflow.com/questions/19545889/app-restarts-rather-than-resumes
         * TFS #39401 - App get closed after user minimize - resume app.
         * Intent.CATEGORY_LAUNCHER category and Intent.ACTION_MAIN action in the intent
         * that starts the initial Activity. If those two flags are present and the Activity
         * is not at the root of the task (meaning the app was already running),
         * then I call finish() on the initial Activity.
         */
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }
        setContentView(R.layout.main_editor);

        gc = (GlobalClass) this.getApplicationContext();

        if (null == bp) {
            bp = new BillingProcessor(this, SubscriptionUtil.base64EncodedPublicKey, this);
            bp.initialize();
            // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
            // See below on why this is a useful alternative
        }

        res = getResources();
        act = ActivityMainEditor.this;
        preferences = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        ivSOG = (ImageView) findViewById(R.id.buttonSnapOnGrid);
        layeredLogos = (LayersContainerView) findViewById(R.id.layeredLogos);
        imageViewLogo = (DynamicImageView) findViewById(R.id.imageViewLogo);
        bottomSlidersContainer = (LinearLayout) findViewById(R.id.bottomSlidersContainer);
        listRight = (RelativeLayout) findViewById(R.id.menuRight);
        resultingScreen = (RelativeLayout) findViewById(R.id.photo);
        seekbarTrans = (SeekBar) findViewById(R.id.seekBarTrans);
        buttonTrashcan = (ImageButton) findViewById(R.id.buttonTrashcan);

        // Live Feature UI
        live_panel = (View) findViewById(R.id.live_include);
        parentView = (LinearLayout) findViewById(R.id.picture_content_parent_view_host);
        cameraLayout = (RelativeLayout) findViewById(R.id.full_camera_content);

        //set initial data
        if (null != preferences) {
            if (preferences.getBoolean("dontAskMeAgain", false)) {
                editor.putBoolean("dontAskMeAgain", true);
                editor.commit();
            } else {
                editor.putBoolean("dontAskMeAgain", false);
                editor.commit();
            }
        }

        // Popup Lips selection setup
        mToolTipFrameLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipframelayout);

        initPaths();

        final GestureDetector logoGestureDetector = new GestureDetector(getApplicationContext(), new LogoGestureListener());
        imageViewLogo.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent ev) {
                logoGestureDetector.onTouchEvent(ev);
                return false;
            }
        });

        imageViewLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showMemoryDetails();
                return false;
            }
        });

        // get the height of the slider container to determine the size of the thumb to set
        bottomSlidersContainer.post(new Runnable() {

            @Override
            public void run() {
                Drawable thumbTransparent = ImageHelper.resizeDrawable(getResources(), getResources().getDrawable(R.drawable.slider_indicator_01), seekbarTrans.getHeight() + 5, (seekbarTrans.getHeight() + 5) > 45 ? 45: (seekbarTrans.getHeight() + 5));
                thumbTransparent.setBounds(0, 2, thumbTransparent.getIntrinsicWidth(), thumbTransparent.getIntrinsicHeight());
                seekbarTrans.setThumb(thumbTransparent);
                seekbarTrans.setThumbOffset(5);
                seekbarTrans.setProgress(252);
            }
        });

        layeredLogos.setDrawingCacheEnabled(true);
        // bind the matrix here to FunnyDrawingPanel to listen matrix changes
        layeredLogos.setMyMatrix(mMatrix);

        resultingScreen.setDrawingCacheEnabled(true);
        final int deviceWidth = getWindowManager().getDefaultDisplay().getWidth();
        DEVICE_WIDTH = deviceWidth;
        int deviceHeight = getWindowManager().getDefaultDisplay().getHeight();
        DEVICE_HEIGHT = deviceHeight;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // load using the API for better image caching
            if (!LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
                if (null != GlobalClass.baseBitmap) {
                    Bitmap bi = ImageHelper.scaleWithRespectToAspectRatio(GlobalClass.baseBitmap, DEVICE_WIDTH, DEVICE_HEIGHT);
                    GlobalClass.freeMem();
                    LogoliciousApp.malloc(getApplicationContext(), (int) LogoliciousApp.fileSizeInBytes(GlobalClass.picturePath));
                    if (null != bi) {
                        Drawable d1 = new BitmapDrawable(getResources(), bi);
                        // load first to get the dimensions
                        backgroundImage.setImageDrawable(d1);
                        // ensure the base image will show up after putting drawable
                        FileUtil.fileWrite(GlobalClass.log_path, "MainEditor: showing the base image", true);
                        if (GlobalClass.subscriptionOkToShow && (backgroundImage.getDrawable() != null && !LogoliciousApp.isLive) || !LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
                            isShowSubscription();
                        }
                    } else {
                        FileUtil.fileWrite(GlobalClass.log_path, "MainEditor: error showing the base image", true);
                    }
                }
            }
            adjustLayerView();
        }

        // image selection and tip
        if (extras == null) {
            selectImageOption(null);
            LogoliciousApp.showTip(ActivityMainEditor.this);
        } else if (extras.size() == 1) {
            // some device has default bundle.size = 1
            LogoliciousApp.showTip(ActivityMainEditor.this);
        }

        // from other class
        layeredLogos.setOnTouchListener(this);
        layeredLogos.setOnClickListener(this);

        final GestureDetector recycleBinDetector = new GestureDetector(getApplicationContext(), new RecycleBinGestureListener());

        buttonTrashcan.setHapticFeedbackEnabled(true);
        buttonTrashcan.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                return recycleBinDetector.onTouchEvent(event);
            }
        });

        // ensure items parent directory
        FileUtil.createDirs(new String[]{logoDir, tempDir, tempShareDir, tempSavedPics, liveDir, designedLogos, fontsDir});

        // insert default values for App table
        if (GlobalClass.sqLiteHelper.checkAppTable().getCount() == 0)
            GlobalClass.sqLiteHelper.insertAppTableDefaultValues();

        // get the last font selected
        Cursor c = GlobalClass.sqLiteHelper.getFontSelected();
        LogoliciousApp.selectedFontPath = null;
        if (c.getCount() > 0) {
            c.moveToNext();
            LogoliciousApp.selectedFontPath = c.getString(0);
        }

        mOrientation = getApplicationContext().getResources().getConfiguration().orientation;

        // on-click listeners
        LogoliciousApp.setOnClickListener(this, R.id.buttonShowMyLogos);
        LogoliciousApp.setOnClickListener(this, R.id.buttonPrefab);
        LogoliciousApp.setOnClickListener(this, R.id.savedTemplates);
        LogoliciousApp.setOnClickListener(this, R.id.buttonAddText);
        LogoliciousApp.setOnClickListener(this, R.id.buttonDoneLive);
        LogoliciousApp.setOnClickListener(this, R.id.save);
        LogoliciousApp.setOnClickListener(this, R.id.sharePic);
        LogoliciousApp.setOnClickListener(this, R.id.flipCamera);
        LogoliciousApp.setOnClickListener(this, R.id.buttonTextColor);
        LogoliciousApp.setOnClickListener(this, R.id.loadLogo);
        LogoliciousApp.setOnClickListener(this, R.id.noLogo);
        LogoliciousApp.setOnClickListener(this, R.id.cppText);
        seekbarTrans.setOnSeekBarChangeListener(onSeekBarTransparentListener);
        LogoliciousApp.setViewVisibility(this, R.id.buttonDoneLive, false);
        LogoliciousApp.setViewVisibility(this, R.id.flipCamera, false);
        LogoliciousApp.setOnClickListener(this, R.id.buttonSnapOnGrid);
        LogoliciousApp.setOnClickListener(this, R.id.buttonGallery);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new LogoliciousApp.LoadFontsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new LogoliciousApp.LoadFontsTask().execute();

        LogoliciousApp.initPrefabLogos(act);

        // initialize saving type
        LogoliciousApp.SAVING_TYPE = preferences.getString("SavingType", "JPG_HQ");

        // initialize snap grid
        if (null != editor) {
            editor.putBoolean("SnapOnGrid", false);
            editor.commit();
        }
        AppStatitics.sharedPreferenceSet(act, "subscription_countdown", 0);

        LogoliciousApp.getAvailableMemMB(this);

        if (!LogoliciousApp.hasPermissionNeeded(ActivityMainEditor.this))
            recievedImageFromOtherApps();

        if (!LogoliciousApp.sharedPreferenceExist(this, "delete_old_fonts")) {
            LogoliciousApp.sharedPreferenceSet(this, "delete_old_fonts", 0);
        }

        if (LogoliciousApp.sharedPreferenceGet(this, "delete_old_fonts", 0) == 0) {
            sqLiteHelper.deleteAllUserFonts();
            LogoliciousApp.sharedPreferenceSet(this, "delete_old_fonts", 1);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        recievedImageFromOtherApps();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("xxx", "xxx onPause");
        isSomeActivityIsRunning = true;
        FileUtil.updateSavingType(LogoliciousApp.SAVING_TYPE);
        isMinimized = true;
        System.gc();
        GlobalClass.freeMem();
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
        Log.i(TAG, "xxx onDestroy");
        GlobalClass.diskCache.clearCache();
        System.gc();
        GlobalClass.freeMem();
        AppStatitics.sharedPreferenceSet(act, "subscription_countdown", 0);
        if (null != editor) {
            editor.putBoolean("ProceedEvenNoMemAvailable", false);
            editor.commit();
        }
        AppStatitics.sharedPreferenceSet(ActivityMainEditor.act, "hasOOM", 0);
        LogoliciousApp.isSubBtnClick = false;
        LogoliciousApp.subsDialog = null;

        //clear activity reference
        if (null == gc)
            gc = (GlobalClass) this.getApplicationContext();
        if (null != gc) {
            Activity currActivity = gc.getCurrentActivity();
            if (null != currActivity) {
                if (this.equals(currActivity))
                    gc.setCurrentActivity(null);
            }
        }

        GlobalClass.pendingShowMemAlert = false;
    }

    private void checkSubscription() {
        //Statistics
        AppStatitics.initializeSaveCount(ActivityMainEditor.this);
        //Check Subscription
        bp.loadOwnedPurchasesFromGoogle();
        td = bp.getSubscriptionTransactionDetails(SubscriptionUtil.SUBSCRIPTION_SKU);
        if (null != td) {
            //get purchased info
            pInfo = td.purchaseInfo;
            if (null != pInfo) {
                if (!pInfo.purchaseData.autoRenewing) {
                    Log.i("", "You have cancelled your Subscription.");
                    AppStatitics.sharedPreferenceSet(act, "isSubscribed", 0);
                } else {
                    AppStatitics.sharedPreferenceSet(act, "isSubscribed", 1);
                }
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "xxx onStart");
        GlobalClass.freeMem();
        checkSubscription();

        /*
        https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
        If your app targets API 24+, and you still want/need to use file:// instead of content:// intents,
        you can use hacky way to disable the runtime check:
         */
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "xxx onResume");
        bp = new BillingProcessor(this, SubscriptionUtil.base64EncodedPublicKey, this);
        checkSubscription();
        isSomeActivityIsRunning = false;
        mOrientation = this.getResources().getConfiguration().orientation;
        isMinimized = false;
        gc.setCurrentActivity(this);
    }

    private void initPaths() {
        root = Environment.getExternalStorageDirectory();
        String fs = File.separator;
        logoDir = root + fs + App_Files_location + fs + ".logos" + fs;
        tempDir = root + fs + App_Files_location + fs + ".temp" + fs;
        tempShareDir = root + fs + App_Files_location + fs + ".temp" + fs + "sharedPictures";
        liveDir = root + fs + App_Files_location + fs + ".live" + fs;
        tempSavedPics = root.getAbsolutePath() + fs + "LogoLicious";
        designedLogos = root + fs + App_Files_location + fs + ".designedLogos";
        fontsDir = root + fs + App_Files_location + fs + ".fonts" + fs;
    }

    private class RecycleBinGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            layeredLogos.removePerItem();
            layeredLogos.invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }
    }

    private class LogoGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mAboutFragDialog++;
            isSomeActivityIsRunning = true;

            //Create and show the dialog.
            LogoliciousApp.startActivity(ActivityMainEditor.this, AboutSaveSettings.class);
            return true;
        }
    }

    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ensure items parent directory
                    initPaths();
                    FileUtil.createDirs(new String[]{GlobalClass.log_path, logoDir, tempDir, tempShareDir, tempSavedPics, liveDir});
                } else {

                }
                break;
            case LogoliciousApp.REQUEST_ID_MULTIPLE_PERMISSIONS:
                initPaths();
                FileUtil.createDirs(new String[]{GlobalClass.log_path, logoDir, tempDir, tempShareDir, tempSavedPics, liveDir});
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SHARE_ACTION) {
                isSomeActivityIsRunning = false;
            }
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {

                if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this)) {
                    LogoliciousApp.showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    onResultFromCamera();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.cancel();
                                    break;
                            }
                        }
                    });
                } else {
                    onResultFromCamera();
                }
            } else if (requestCode == REQUEST_CODE_CHOOSE_FROM_GALLERY) {

                if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this)) {
                    LogoliciousApp.showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    onResultFromGallery(data);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.cancel();
                                    break;
                            }
                        }
                    });
                } else {
                    onResultFromGallery(data);
                }
            } else if (requestCode == REQUEST_CODE_CHOOSE_LOGO_FROM_GAL) {
                GlobalClass.logoPath = null;
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                if (null == c) {
                    LogoliciousApp.showMessageOK(ActivityMainEditor.this, getString(R.string.MessageErrorOnLogoUpload), null);
                    return;
                }
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                GlobalClass.logoPath = "";
                GlobalClass.logoPath = c.getString(columnIndex);
                c.close();
                try {
                    if (FileUtil.getFileSize(GlobalClass.logoPath) > FileUtil.PREFERRED_LOGO_SIZE) {
                        Log.i(TAG, "xxx logo is above recommended size");
                        LogoliciousApp.showYesNoAlert(ActivityMainEditor.this, "Preferred Logo size exceeded:",
                                getString(R.string.logoSizeExceedsPreferred),
                                "Continue", "Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                new UploadLogoTask().execute(GlobalClass.logoPath);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                d.dismiss();
                                                d.cancel();
                                                break;
                                        }
                                    }
                                });
                    } else {
                        Log.i(TAG, "xxx on an recommended logo size");
                        new UploadLogoTask().execute(GlobalClass.logoPath);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == BillingService.RESULT_CODE_PURCHASE) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    LogoliciousApp.toast(getApplicationContext(), "You have subsribed to the " + sku + ". Excellent choice adventurer", Toast.LENGTH_SHORT);
                } catch (JSONException e) {
                    LogoliciousApp.toast(getApplicationContext(), "Failed to parse subscription data.", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_UPLOAD_LOGOS) {
                if (data == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoLogoSelected), Toast.LENGTH_LONG).show();
                    return;
                }

                String selectedLogo = data.getStringExtra("SelectedLogo");
                if (LogoliciousApp.strIsNullOrEmpty(selectedLogo))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoLogoSelected), Toast.LENGTH_LONG).show();
                else
                    new UploadLogoTask().execute(selectedLogo);
            } else if (requestCode == PICK_FONT_RESULT_CODE) {
                if (null == data)
                    return;

                Uri content_describer = data.getData();
                String filePath = data.getData().getPath();
                Log.d("xxx", "xxx filePath " + filePath);
                Log.d("xxx", "xxx name " + content_describer.getLastPathSegment());
                //get the path
                Log.d("Path???", content_describer.getPath());
                Log.d("Path???", FileUtil.getPath(this, content_describer));
                try {
                    FileUtil.copyFile(new FileInputStream(content_describer.getPath()), new FileOutputStream(fontsDir + content_describer.getLastPathSegment()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                if (!TextUtils.isEmpty(filePath)) {
//                    String[] pathSplits = filePath.split("/");
//                    if (pathSplits.length > 0 && pathSplits[pathSplits.length - 1].contains(".ttf")) {
//                        Log.i("xxx", "xxx This is font file");
//                        GlobalClass.sqLiteHelper.insertFont(filePath);
//                        LogoliciousApp.showMessageOK(this, pathSplits[pathSplits.length - 1] + " successfully added.", null);
//                    } else {
//                        Log.i("xxx", "xxx This is not font file");
//                        LogoliciousApp.showMessageOK(this, "Sorry, the file is not supported.", null);
//                    }
//                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        removePopupFunnySelection();
        //image.onTouch(image, event);
        if (v.getId() == R.id.layeredLogos) {
            if (null != layeredLogos && null != layeredLogos.targetSelected)
                layeredLogos.onTouch(v, event);
            //Log.i("xxx","xxx view id: " + getResources().getResourceEntryName(v.getId()));
        }
        return false;
    }

    /**
     * @return This will set the funny image to the selected and delete
     * current item and reset the
     * parts that is erase by the eraser.
     */
    private void addLogoToScreen(int itemPosition, int logoType) {
        ArrayList<ArrayHolderLogos> arr = new ArrayList<ArrayHolderLogos>();
        if (logoType == PREFAB)
            arr = prefablogoItems;
        else
            arr = mylogoItems;

        if (null == arr)
            return;

        if (backgroundImage.getDrawable() == null && !LogoliciousApp.isLive) {
            LogoliciousApp.showAlertOnUpLoadLogo(this, R.layout.upload_logo_alert, "Oops", "", true);
        } else {
            seekbarTrans.setProgress(252);
            layeredLogos.setVisibility(View.VISIBLE);
            Log.i("addLogoToScreen ", "bW " + bW + " bH " + bH + " logoItems.get(itemPosition).getItemPath() " + arr.get(itemPosition).getItemPath());
            // resize logo before putting it on screen

            GlobalClass.LOGO_SELECTED_COUNT = GlobalClass.LOGO_SELECTED_COUNT + 1;
            ACRA.getErrorReporter().putCustomData(GlobalClass.LOGO_SELECTED + GlobalClass.LOGO_SELECTED_COUNT, LogoliciousApp.fileSizeInMb(arr.get(itemPosition).getItemPath()));
            Bitmap logoSelected = ImageHelper.decodeSampledBitmapFromPath(arr.get(itemPosition).getItemPath(), DEVICE_WIDTH, DEVICE_HEIGHT);
            bmp2 = layeredLogos.computeLogoOptimizeDimension(
                    logoSelected,
                    arr.get(itemPosition).getItemPath(),
                    DEVICE_WIDTH,
                    DEVICE_HEIGHT);

            if (null == bmp2) {
                LogoliciousApp.toast(getApplicationContext(), "The image you are trying to upload is big in file size.", Toast.LENGTH_LONG);
            }

            ImageHelper.clearBitmap(logoSelected);

            //add image to layer
            layeredLogos.addItem(bmp2.copy(Bitmap.Config.ARGB_8888, true), layeredLogos, arr.get(itemPosition).getItemPath());

            adjustLayerView();
        }

    }

    private void addFirstLogoSelectedToScreen(String logoPath) {
        seekbarTrans.setProgress(252);
        layeredLogos.setVisibility(View.VISIBLE);

        Bitmap mBitmapTmp = ImageHelper.decodeSampledBitmapFromPath(logoPath, DEVICE_WIDTH, DEVICE_HEIGHT);
        if (mBitmapTmp == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.LogoUploadErrorMessage), Toast.LENGTH_LONG).show();
            return;
        }

        bmp2 = layeredLogos.computeLogoOptimizeDimension(
                mBitmapTmp
                , logoPath
                , (int) bW, (int) bH);
        layeredLogos.addItem(bmp2.copy(Bitmap.Config.ARGB_8888, true), layeredLogos, logoPath);
        layeredLogos.invalidate();
        resultingScreen.invalidate();

        if (bmp2 != null) {
            bmp2.recycle();
            bmp2 = null;
        }
    }

    /**
     * @return This will remove the current tooltip windows.
     */
    public static void removePopupFunnySelection() {
        if (mLogosView != null) {
            mLogosView.remove();
            mLogosView = null;
        }
        if (mPrefabsLogosView != null) {
            mPrefabsLogosView.remove();
            mPrefabsLogosView = null;
        }
        if (mSavedTemplate != null) {
            mSavedTemplate.remove();
            mSavedTemplate = null;
        }
        if (isAbountInfoShown == true) {
            viewAboutInfo.setVisibility(View.GONE);
            isAbountInfoShown = false;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isSomeActivityIsRunning = true;
            finish();
            System.exit(0);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    OnSeekBarChangeListener onSeekBarTransparentListener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2) {
            if (arg2 == true) {
                if (progress < 5) {
                    seekBar.setProgress(5); // magic solution, ha
                    progress = 5;
                } else if (progress > 252) {
                    seekBar.setProgress(252); // magic solution, ha
                    progress = 252;
                }
                seekBarTransparent = progress;
                if (layeredLogos.targetSelected != null) {
                    layeredLogos.adjustTransparency(seekBarTransparent);
                    layeredLogos.targetSelected.seekBarTransparent = progress;
                }
            }
        }
    };

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        if (mLogosView == toolTipView) {
            mLogosView = null;
        }
        if (mPrefabsLogosView == toolTipView) {
            mPrefabsLogosView = null;
        }
        if (mSavedTemplate == null) {
            mSavedTemplate = null;
        }
        if (isAbountInfoShown == true) {
            viewAboutInfo.setVisibility(View.GONE);
            isAbountInfoShown = false;
        }
    }

    private void hideTips() {
        if (hintLayout != null) {
            hintLayout.setVisibility(View.GONE);
        }
    }

    public void saveTemplate(View v) {
        if (LogoliciousApp.isLive) {
            LogoliciousApp.toast(getApplicationContext(), res.getString(R.string.SavingTemplateMessage), 1);
            return;
        }

        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.template_save_name);
        d.setTitle("NEW TEMPLATE");
        d.setCancelable(true);

        final EditText templateName = (EditText) d.findViewById(R.id.templateName);
        //Filter special characters
        templateName.setFilters(new InputFilter[]{LogoliciousApp.filterSpecialChars()});

        Button saveTemplate = (Button) d.findViewById(R.id.saveTemplate);
        Button cancelSaving = (Button) d.findViewById(R.id.cancelSaving);

        saveTemplate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("", "xxx templateName " + templateName.getText().toString() + GlobalClass.getAR());
                //LogoliciousApp.showMessageOK(ActivityMainEditor.this, "Template Name is: " + templateName.getText().toString() + GlobalClass.getAR(), null);
                if (GlobalClass.sqLiteHelper.checkIfTemplateExist(templateName.getText().toString() + GlobalClass.getAR())) {
                    LogoliciousApp.showYesNoAlertWithoutTitle(ActivityMainEditor.this, "You are about to overwrite an existing template with the same name.", "YES", "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case Dialog.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                                case Dialog.BUTTON_POSITIVE:
                                    d.dismiss();
                                    GlobalClass.sqLiteHelper.deleteTemplate(templateName.getText().toString() + GlobalClass.getAR());
                                    GlobalClass.sqLiteHelper.deleteTemplatePreview(templateName.getText().toString() + GlobalClass.getAR());
                                    layeredLogos.saveAsTemplate(getApplicationContext(), templateName.getText().toString() + GlobalClass.getAR());
                                    break;
                            }
                        }
                    });
                    return;
                }

                if (templateName.getText().toString().contains(".")) {
                    LogoliciousApp.toast(getApplicationContext(), "Must not contain period in template name.", Toast.LENGTH_SHORT);
                    return;
                }

                layeredLogos.saveAsTemplate(getApplicationContext(), templateName.getText().toString() + GlobalClass.getAR());
                d.dismiss();
            }
        });

        cancelSaving.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        if (!layeredLogos.isLayerEmpty())
            d.show();
        else
            LogoliciousApp.toast(getApplicationContext(), "No Template to save.", 1);

    }

    /**
     * @param deleteAfterSaving true if you want to delete after saving.
     * @param isForSharing      Set true if you want to share to social media. False if you only want to save the image.
     * @return Method for saving and sharing the image.
     */

    Uri photoUri = null;

    @SuppressLint("SimpleDateFormat")
    private String saveFinalImage(final boolean deleteAfterSaving, final boolean isForSharing) {
        String fileNameFromPref = preferences.getString("ImageFilename", null);
        String savingType = FileUtil.getImageQualityType(preferences);
        Bitmap result = null;
        String strResult = "";
        if (null == GlobalClass.baseBitmap) {
            return "";
        } else {
            //check if available memory is still enough to allocate
            Log.i("xxx", (int) LogoliciousApp.fileSizeInBytes(GlobalClass.picturePath) + "bytes app mem = " + LogoliciousApp.getAvailableMemMB(ActivityMainEditor.this) + "mb" + " base image = " + (int) LogoliciousApp.fileSizeInMbInt(GlobalClass.picturePath) + "mb");
            if ((LogoliciousApp.getAvailableMemMB(ActivityMainEditor.this) < (int) LogoliciousApp.fileSizeInMbInt(GlobalClass.picturePath))
                    && !preferences.getBoolean("ProceedEvenNoMemAvailable", false)) {
                return "not enough memory";
            }
            //If enough memory then proceed here.
            LogoliciousApp.malloc(getApplicationContext(), (int) LogoliciousApp.fileSizeInBytes(GlobalClass.picturePath));

            result = layeredLogos.saveBitmapLayers(
                    GlobalClass.baseBitmap,
                    GlobalClass.baseBitmap.getWidth(),
                    GlobalClass.baseBitmap.getHeight(),
                    getApplicationContext(),
                    true);
        }

        /*
         * This method is highly recommended because its public directory and compatible to attaching via email.
         */
        String dirToSave;
        if (isForSharing == false) {
            // save to Pictures Directory if save to device device
            dirToSave = tempSavedPics;
            File pictureDir = new File(dirToSave);
            if (!pictureDir.exists()) {
                pictureDir.mkdirs();
            }
        } else {
            dirToSave = tempShareDir;
        }

        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = timestamp.format(new Date());

        String fname;
        if (!LogoliciousApp.strIsNullOrEmpty(fileNameFromPref)) {
            fname = fileNameFromPref;
        } else
            fname = "LogoLicious_" + currentDateandTime;

        fileToSave = new File(dirToSave, fname + "." + FileUtil.getImageType(preferences));
        if (fileToSave.exists()) {
            fileToSave = new File(dirToSave, fname + currentDateandTime + "." + FileUtil.getImageType(preferences));
        }
        try {
            FileOutputStream out = new FileOutputStream(fileToSave);
            if (savingType.contains(LogoliciousApp.TYPE_HR_PNG))
                result.compress(Bitmap.CompressFormat.PNG, 100, out);
            else if (savingType.contains(LogoliciousApp.TYPE_JPG_HQ))
                result.compress(Bitmap.CompressFormat.JPEG, 100, out);
            else if (savingType.contains(LogoliciousApp.TYPE_JPG_L))
                result.compress(Bitmap.CompressFormat.JPEG, 40, out);

            Log.i("xxx", "xxx FileUtil.getImageCompressType(preferences) ->" + FileUtil.getImageCompressType(preferences));
            out.flush();
            out.close();
            //Save Exif
            GlobalClass.baseImageExif.save(fileToSave);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        // refresh gallery so that the picture saved will show in gallery
        if (deleteAfterSaving == false) {
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{fileToSave.toString()},
                    new String[]{"image/" + FileUtil.getImageType(preferences)}, null);
            FileUtil.fileWrite(GlobalClass.log_path, "Image Saved Path: -> " + fileToSave.toString(), true);
        }

        // check if Image is to share
        if (isForSharing == true) {
            if (fileToSave.getAbsolutePath() != null || fileToSave.getAbsolutePath() != "") {
                File pngFile = new File(fileToSave.getAbsolutePath());
                // http://stackoverflow.com/questions/3570914/android-how-do-i-attach-a-temporary-generated-image-to-an-email
                // Save file encoded as PNG

                photoUri = Uri.fromFile(pngFile);
                File file = new File(photoUri.getPath());
                if (file.exists()) {
                    // file create success
                    // then no need to change the photoUri since it is good
                } else {
                    // file create fail
                    photoUri = Uri.parse("file:///" + fileToSave.getAbsolutePath());
                }

            }
        }

        if (null != result) {
            result.recycle();
            result = null;
        }

        System.gc();
        Runtime.getRuntime().gc();

        return strResult;
    }

    /**
     * @return Retrieve logos from specified location in SDCard
     */
    private class RetrieveLogosTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FileUtil.fileWrite(GlobalClass.log_path, "Retrieving Logo", true);
            String[] children = new File(logoDir).list();
            if (null != children) {
                if (children.length == 0)
                    LogoliciousApp.initPrefabLogos(act);
            }
        }

        @Override
        protected String doInBackground(String... arg0) {

            // retrieve my logos except prefab logos
            File f = new File(logoDir);
            File file[] = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean bRet = true;
                    if (name.toLowerCase(Locale.US).contains("prefab_")
                            || name.toLowerCase(Locale.US).contains("logo_tm_")) {
                        bRet = false;
                    }
                    return bRet;
                }
            });
            mylogoItems.clear();

            if (file != null) {
                for (File dataFiles : file) {
                    // add to items details holder
                    mylogoItems.add(new ArrayHolderLogos(dataFiles.getName(), logoDir + dataFiles.getName()));
                    publishProgress(dataFiles.getName());
                }
            }

            return "";
        }

        protected void onProgressUpdate(String... progress) {
            FileUtil.fileWrite(GlobalClass.log_path, "Retrieving Logo -> " + progress[0], true);
        }

        @Override
        protected void onPostExecute(String result) {
            FileUtil.fileWrite(GlobalClass.log_path, "Done getting All Logos. Refreshing View Adapter", true);
            adapterFunnyGridItems.notifyDataSetChanged();
            FileUtil.fileWrite(GlobalClass.log_path, "End of My Logo function", true);
        }

    }

    private class RetrievePrefabLogosTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FileUtil.fileWrite(GlobalClass.log_path, "Retrieving PrefabLogo", true);
            String[] children = new File(logoDir).list();
            if (null != children) {
                if (children.length == 0)
                    LogoliciousApp.initPrefabLogos(act);
            }
        }

        @Override
        protected String doInBackground(String... arg0) {

            // retrieve my logos except prefab logos
            File f = new File(logoDir);
            File file[] = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean bRet = false;
                    if (name.toLowerCase(Locale.US).contains("prefab_")) {
                        bRet = true;
                    }
                    return bRet;
                }
            });
            prefablogoItems.clear();

            if (file != null) {
                for (File dataFiles : file) {
                    // add to items details holder
                    prefablogoItems.add(new ArrayHolderLogos(dataFiles.getName(), logoDir + dataFiles.getName()));
                    publishProgress(dataFiles.getName());
                }
            }

            return "";
        }

        protected void onProgressUpdate(String... progress) {
            FileUtil.fileWrite(GlobalClass.log_path, "Retrieving PrefabLogo -> " + progress[0], true);
        }

        @Override
        protected void onPostExecute(String result) {
            FileUtil.fileWrite(GlobalClass.log_path, "Done getting All PrefabLogos. Refreshing View Adapter", true);
            adapterFunnyGridItems.notifyDataSetChanged();
            FileUtil.fileWrite(GlobalClass.log_path, "End of Prefab function", true);
        }

    }

    private class SaveFinalImageTask extends AsyncTask<Integer, Integer, String> {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this))
                LogoliciousApp.showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), null);

            forSharing = false;
            GlobalClass.freeMem();

            mDialog = new ProgressDialog(ActivityMainEditor.this);
            mDialog.setMessage("Saving your file. \nGive us a moment while we save this in " + FileUtil.getImageQualityTypeDescription(preferences) + ".");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        protected String doInBackground(Integer... task) {
            return saveFinalImage(false, false);
        }

        protected void onPostExecute(String result) {
            mDialog.dismiss();

            if (result.equalsIgnoreCase("not enough memory")) {
                LogoliciousApp.showYesNoAlertWithoutTitle(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), "Continue", "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                editor.putBoolean("ProceedEvenNoMemAvailable", true);
                                editor.commit();
                                new SaveFinalImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.cancel();
                                editor.putBoolean("ProceedEvenNoMemAvailable", false);
                                editor.commit();
                                break;
                        }
                    }
                });
                return;
            } else {
                rateApp();
                System.gc();
                Runtime.getRuntime().gc();
                AppStatitics.addSaveShareCount(ActivityMainEditor.this);
                // success saved message
                if (!layeredLogos.isLayerEmpty()) {
                    LogoliciousApp.toast(getApplicationContext(), getString(R.string.AfterSavingMessage), Toast.LENGTH_LONG);
                }

                if (1 == AppStatitics.sharedPreferenceGet(ActivityMainEditor.this, "hasOOM", 0)) {
                    LogoliciousApp.showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessageForLogo), null);
                }

                GlobalClass.freeMem();
                editor.putBoolean("ProceedEvenNoMemAvailable", false);
                editor.commit();
            }
        }
    }

    private void isShowSubscription() {
        boolean bRet = false;
        //check user already subscribed
        checkSubscription();
        if (0 == AppStatitics.sharedPreferenceGet(act, "isSubscribed", 0)) {
            bRet = true;
            AppStatitics.showSubscription(ActivityMainEditor.this, AppStatitics.sharedPreferenceGet(ActivityMainEditor.this, "STAT_SAVE_SHARE_COUNT", 0));
            Log.i("xxx", "xxx b isShowSubscription ");
        }

    }

    private void rateApp() {
        Cursor cur = GlobalClass.sqLiteHelper.getSaveCount();
        try {
            while (cur.moveToNext()) {
                saveCount = cur.getInt(0);
                id = cur.getInt(1);
                isRated = cur.getInt(2);
                if (AppStatitics.sharedPreferenceGet(this, "STAT_SAVE_SHARE_COUNT", 0) >= 5 && 0 == AppStatitics.sharedPreferenceGet(this, "RATED", 0)) {
                    LogoliciousApp.showRateDialog(act, id, saveCount);
                    Toast.makeText(getApplicationContext(), "Your Photo has been succesfully saved.", Toast.LENGTH_LONG).show();
                } else {
                    // increment count
                    saveCount = saveCount + 1;// increment count
                    GlobalClass.sqLiteHelper.updateSaveCount(id, saveCount, isRated);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
                cur = null;
            }
        }
    }

    public static boolean forSharing = false;

    private class SharingFinalImageTask extends AsyncTask<String, Integer, String> {
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this))
                LogoliciousApp.showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), null);

            forSharing = true;
            mDialog = new ProgressDialog(ActivityMainEditor.this);
            mDialog.setMessage("Sharing your file. \nGive us a moment while we share this in " + FileUtil.getImageQualityTypeDescription(preferences) + ".");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        protected String doInBackground(String... param) {
            return saveFinalImage(false, true);
        }

        protected void onPostExecute(String picturePath) {
            mDialog.dismiss();

            if (picturePath.equalsIgnoreCase("not enough memory")) {
                LogoliciousApp.showYesNoAlertWithoutTitle(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), "Continue", "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                editor.putBoolean("ProceedEvenNoMemAvailable", true);
                                editor.commit();
                                new SharingFinalImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.cancel();
                                editor.putBoolean("ProceedEvenNoMemAvailable", false);
                                editor.commit();
                                break;
                        }
                    }
                });
                return;
            } else {
                rateApp();
                System.gc();
                Runtime.getRuntime().gc();
                AppStatitics.addSaveShareCount(ActivityMainEditor.this);
                ActivityMainEditor.picturePath = picturePath;

                //Show share list.
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("image/" + FileUtil.getImageType(preferences)); // text/plain
                String shareText = getResources().getString(R.string.label_sharetext);
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "LogoLicious");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
                shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
                startActivityForResult(Intent.createChooser(shareIntent, "Share Your LogoLicious"), REQUEST_SHARE_ACTION);

                editor.putBoolean("ProceedEvenNoMemAvailable", false);
                editor.commit();
            }
        }
    }

    public static void showShareIntent(String absPath) {
        Uri photoUri;
        File pngFile = new File(absPath); //fileToSave.getAbsolutePath()
        // http://stackoverflow.com/questions/3570914/android-how-do-i-attach-a-temporary-generated-image-to-an-email
        // Save file encoded as PNG

        photoUri = Uri.fromFile(pngFile);
        File file = new File(photoUri.getPath());
        if (file.exists()) {
            // file create success
            // then no need to change the photoUri since it is good
        } else {
            // file create fail
            photoUri = Uri.parse("file:///" + fileToSave.getAbsolutePath());
        }

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/" + FileUtil.getImageType(preferences)); // text/plain
        String shareText = act.getResources().getString(R.string.label_sharetext);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "LogoLicious");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        act.startActivityForResult(Intent.createChooser(shareIntent, "Share Your LogoLicious"), REQUEST_SHARE_ACTION);
    }

    private class UploadLogoTask extends AsyncTask<String, String, String> {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.freeMem();

            mDialog = new ProgressDialog(ActivityMainEditor.this);
            mDialog.setMessage("Uploading Logo");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... logopath) {
            if (!LogoliciousApp.strIsNullOrEmpty(logopath[0])) {

                if (FileUtil.getFileSize(logopath[0]) > FileUtil.PREFERRED_LOGO_SIZE)
                    return decodeUploadedLogo(logopath[0]);
                else
                    return decodeUploadedLogo(logopath[0]);
            }
            return logopath[0];
        }

        protected void onPostExecute(final String result) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDialog.dismiss();
                    // Add the first logo to the screen
                    if (!LogoliciousApp.strIsNullOrEmpty(result)) {
                        addFirstLogoSelectedToScreen(result);
                    }
                }
            }, 1000);

            GlobalClass.freeMem();
        }

    }

    private String decodeUploadedLogo(String logopath) {
        // this is called when I want to create a very large buffer in native memory
        //temporary logo path to correct image rotation
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = timestamp.format(new Date());
        LogoliciousApp.malloc(getApplicationContext(), (int) LogoliciousApp.fileSizeInBytes(logopath));
        GlobalClass.LOGO_UPLOADED_COUNT = GlobalClass.LOGO_UPLOADED_COUNT + 1;
        ACRA.getErrorReporter().putCustomData(GlobalClass.LOGO_UPLOADED + GlobalClass.LOGO_UPLOADED_COUNT, LogoliciousApp.fileSizeInMb(logopath));
        logopath = BitmapSaver.saveLogoBitmape(
                tempDir + "uploaded_" + currentDateandTime + ".png",
                BitmapSaver.exifLogoBitmapOrientationCorrector(ActivityMainEditor.this, logopath)
        );
        /**
         * This will return black background
         * logopath = BitmapSaver.saveBitmape(preferences, tempDir, "logo_selected", BitmapSaver.exifLogoBitmapOrientationCorrector(ActivityMainEditor.this, logopath));
         */

        try {
            //Correct orientation
            ImageExif.updateExif(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL, logopath);
            //Parse Exif
            GlobalClass.baseImageExif.parse(logopath);
        } catch (Exception e) {
            Log.i("xxx", "xxx There's problem in parsing Exif.");
        }
        FileUtil.copyFileTo(getApplicationContext()
                , logopath
                , logoDir, "sdcard");
        return logopath;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cppText:
                LogoliciousApp.toast(getApplicationContext(), "malloc() called. Available mem = " + LogoliciousApp.getAvailableMemMB(getApplicationContext()), Toast.LENGTH_SHORT);
                break;
            case R.id.buttonShowMyLogos:
                if (LogoliciousApp.verifyStoragePermissions(this, LogoliciousApp.PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)) {
                    Log.i(TAG, "Permission Storage Granted.");
                } else {
                    return;
                }

                if (mLogosView == null) {
                    addMyLogosTooltipView();
                } else {
                    mLogosView.remove();
                    mLogosView = null;
                }

                selectedToolTipView = mLogosView;
                break;
            case R.id.buttonPrefab:
                if (LogoliciousApp.verifyStoragePermissions(this, LogoliciousApp.PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)) {
                } else {
                    return;
                }

                if (mPrefabsLogosView == null) {
                    addPrefabTooltipView();
                } else {
                    mPrefabsLogosView.remove();
                    mPrefabsLogosView = null;
                }

                selectedToolTipView = mPrefabsLogosView;
                break;
            case R.id.savedTemplates:
                System.gc();
                Runtime.getRuntime().gc();

                if (mSavedTemplate == null) {
                    addSavedTemplatesTooltipView();
                } else {
                    mSavedTemplate.remove();
                    mSavedTemplate = null;
                }
                break;
            case R.id.buttonAddText:
                LogoliciousApp.addText(act, backgroundImage, layeredLogos, false, false);
                break;
            case R.id.buttonTextColor:
                LogoliciousApp.showColorPicker(act, layeredLogos, listRight);
                break;
            case R.id.save: {
                System.gc();
                Runtime.getRuntime().gc();

                hideTips();
                // Send a saving progress notification
                if (LogoliciousApp.isLive) {
                    LogoliciousApp.toast(getApplicationContext(), res.getString(R.string.SavingImageWhileonLiveMessage), 1);
                    break;
                }

                if ((backgroundImage.getDrawable() == null && !LogoliciousApp.isLive) || LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
                    LogoliciousApp.showAlertOnUpLoadLogo(this, R.layout.upload_logo_alert, "Oops", "", true);
                    break;
                }

                // check whether to show the saving preference always
                if (!preferences.getBoolean("dontAskMeAgain", false)) {
                    LogoliciousApp.showSavingOptions(act, preferences, editor, mHandler, DEVICE_WIDTH, LogoliciousApp.SAVE);
                    break;
                }

                Message msg = mHandler.obtainMessage(ActivityMainEditor.MESSAGE_SAVING_IMAGE);
                mHandler.sendMessage(msg);
            }
            break;
            case R.id.sharePic:
                isSomeActivityIsRunning = true;
                hideTips();
                if (LogoliciousApp.isLive) {
                    LogoliciousApp.toast(getApplicationContext(), res.getString(R.string.SharingImageWhileonLiveMessage), 1);
                    break;
                }

                if ((backgroundImage.getDrawable() == null && !LogoliciousApp.isLive) || LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
                    LogoliciousApp.showAlertOnUpLoadLogo(this, R.layout.upload_logo_alert, "Oops", "", true);
                    break;
                }

                // check whether to show the saving preference always
                if (!preferences.getBoolean("dontAskMeAgain", false)) {
                    LogoliciousApp.showSavingOptions(act, preferences, editor, mHandler, DEVICE_WIDTH, LogoliciousApp.SHARE);
                    break;
                }

                // Send a saving progress notification
                Message msg = mHandler.obtainMessage(ActivityMainEditor.MESSAGE_SHARING_IMAGE);
                mHandler.sendMessage(msg);
                break;
            case R.id.loadLogo:
                callGalerryToSelectLogo(v);
                break;
            case R.id.buttonDoneLive:
                if (LIVE_SELECTED == LIVE_CAMERA) {
                    mCamUtils.clickPicture();
                } else {
                }

                // off the blink the live button
                ImageView img = (ImageView) findViewById(R.id.buttonLive);
                img.setImageResource(R.drawable.live);

                LogoliciousApp.setViewVisibility(this, R.id.buttonDoneLive, false);
                LogoliciousApp.setViewVisibility(this, R.id.flipCamera, false);

                LogoliciousApp.isLive = false;
                break;
            case R.id.flipCamera:
                mCamUtils.flipCamera();
                break;
            case R.id.noLogo:
                mAboutFragDialog++;
                isSomeActivityIsRunning = true;

                //Create and show the dialog.
                LogoliciousApp.startActivity(ActivityMainEditor.this, AboutSaveSettings.class);

                break;
            case R.id.buttonSnapOnGrid:
                snapOnGridToggle();
                break;
            case R.id.buttonGallery:
                selectImageOption(v);
                break;
            default:
                break;
        }
    }

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SAVING_IMAGE:
                    new SaveFinalImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case MESSAGE_SHARING_IMAGE:
                    new SharingFinalImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case MESSAGE_SAVING_IMAGE_ERROR:
                    LogoliciousApp.toast(getApplicationContext(), res.getString(R.string.SavingImageMessage), 1);
                    break;
                case MESSAGE_SHARING_IMAGE_ERROR:
                    LogoliciousApp.toast(getApplicationContext(), res.getString(R.string.SharingImageMessage), 1);
                    break;
                case MESSAGE_APPLY_TEMPLATE_ERROR:
                    LogoliciousApp.showAlertOnUpLoadLogo(act, R.layout.upload_logo_alert, "Oops", "", true);
                    break;
                case MESSAGE_APPLY_TEMPLATE:
                    LogoliciousApp.setViewVisibility(ActivityMainEditor.this, R.id.templateProgress, true);
                    resetAcraLogoApplyTempate();
                    layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(LogoliciousApp.selected_template_name), backgroundImage, false);
                    if (LayersContainerView.geteMissingFonts().size() > 0) {
                        LogoliciousApp.showMessageOK(getApplicationContext(),
                                "Your template used a font that lacked capital letters which lead to complaints from our users. "
                                        + "It was therefore removed. Please recreate your template with a new font.\n"
                                        + "Fonts: " + LayersContainerView.geteMissingFonts().toString().replace("[", "").replace("]", "") + "\n"
                                , null);
                    }

                    layeredLogos.refreshMe();
                    LogoliciousApp.setViewVisibility(ActivityMainEditor.this, R.id.templateProgress, false);
                    break;
            }
        }
    };

    public void callGalerry(View v) {
        if (LogoliciousApp.verifyStoragePermissions(this, LogoliciousApp.PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)) {
            freeUnneededMemory();
            resetTranparentSeeker();
            resetSnapOnGrid();
            resetAcraData();

            LogoliciousApp.isLive = false;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_FROM_GALLERY);
        }

    }

    public void callCamera(View v) {
        if (LogoliciousApp.verifyCameraPermissions(this, LogoliciousApp.PERMISSIONS_CAMERA, REQUEST_CAMERA)) {
            Log.i(TAG, "Permission Storage Granted.");
            LogoliciousApp.isLive = false;

            freeUnneededMemory();
            resetTranparentSeeker();
            resetSnapOnGrid();
            resetAcraData();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // set orientation as portrait
            File f = new File(tempDir, res.getString(R.string.PictureViaCamFName));
            //We change from Uri.fromFile(f) to FileProvider.getUriForFile because of error android.os.FileUriExposedException since we target to api 27 from 24.
            //We now need to use 'content://' instead of 'file://'
            //we put back Uri.fromFile(f); because //FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".com.olav.logolicious.fileprovider", f); will have error on camera
            Uri photoURI = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    public void callGalerryToSelectLogo(View v) {
        if ((LogoliciousApp.isBaseImageNull(backgroundImage) == true && !LogoliciousApp.isLive) || GlobalClass.baseBitmap == null) {
            LogoliciousApp.showAlertOnUpLoadLogo(this, R.layout.upload_logo_alert, "Oops", "", true);
            System.out.println("Base ImageView has no background!");
        } else {
            System.out.println("Base ImageView has background!");
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_LOGO_FROM_GAL);
        }
    }

    /**
     * Determines if the current device can handle an image capture action.
     *
     * @return true if the device can handle an image capture action. False if it cannot.
     * https://developer.amazon.com/public/solutions/devices/fire-tablets/specifications/01-device-and-feature-specifications
     */
    protected boolean canHandleCameraIntent() {
        final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final List<ResolveInfo> results = getPackageManager().queryIntentActivities(intent, 0);
        return (results.size() > 0);
    }

    public void selectImageOption(View v) {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.select_image);
        d.setTitle("SELECT IMAGE");
        d.setCancelable(true);

        Button buttonSelectPhoto = (Button) d.findViewById(R.id.buttonSelectPhoto);
        Button buttonLiveTakePhoto = (Button) d.findViewById(R.id.buttonLiveTakePhoto);
        Button buttonSelectVideo = (Button) d.findViewById(R.id.buttonSelectVideo);
        Button buttonSelectBatchPhoto = (Button) d.findViewById(R.id.buttonSelectBatchPhoto);
        Button buttonQuit = (Button) d.findViewById(R.id.buttonQuit);

        buttonSelectPhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                if (LogoliciousApp.verifyStoragePermissions(ActivityMainEditor.this, LogoliciousApp.PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE))
                    callGalerry(v);
            }
        });

        buttonSelectBatchPhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                LogoliciousApp.startActivity(ActivityMainEditor.this, GalleryViewerActivity.class);
            }
        });

        buttonLiveTakePhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                callCamera(v);
            }
        });

        buttonSelectVideo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        buttonQuit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                finish();
                System.exit(0);
            }
        });

        d.show();
    }

    public void selectLiveOption(View v) {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.select_live);
        d.setTitle("SELECT LIVE");
        d.setCancelable(true);

        Button buttonLiveRecVid = (Button) d.findViewById(R.id.buttonLiveRecVid);
        Button buttonLiveTakePhoto = (Button) d.findViewById(R.id.buttonLiveTakePhoto);

        buttonLiveRecVid.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                if (LogoliciousApp.verifyCameraPermission(ActivityMainEditor.this, LogoliciousApp.REQUEST_ACCESS_CAMERA))
                    start_camera(v, Live_Camera.LIVE_VIDEO);
            }
        });

        buttonLiveTakePhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                if (LogoliciousApp.verifyCameraPermission(ActivityMainEditor.this, LogoliciousApp.REQUEST_ACCESS_CAMERA))
                    start_camera(v, Live_Camera.LIVE_PICTURE);
            }
        });

        d.show();
    }

    private void addMyLogosTooltipView() {
        FileUtil.fileWrite(GlobalClass.log_path, "Click showMyLogos", true);
        isShowItems = true;
        FileUtil.fileWrite(GlobalClass.log_path, "Preparing MyLogos Layout", true);
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLogos = inflater.inflate(R.layout.grid_mylogos, null);
        gridview = (GridView) viewLogos.findViewById(R.id.gridviewLogos);

        mylogoItems.clear();
        adapterFunnyGridItems = new AdapterGridLogos(getApplicationContext(), mylogoItems, R.layout.grid_logoitem);
        gridview.setAdapter(adapterFunnyGridItems);
        FileUtil.fileWrite(GlobalClass.log_path, "Create Grid Shadow", true);
        ToolTip toolTip = new ToolTip()
                .withText("My Logos")
                .withContentView(viewLogos)
                .withColor(getResources().getColor(R.color.GrayLogoPanel))
                .withAnimationType(ToolTip.AnimationType.NONE).withShadow();

        mLogosView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.buttonShowMyLogos));
        mLogosView.setOnToolTipViewClickedListener(ActivityMainEditor.this);
        new RetrieveLogosTask().execute();

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int itemPosition, long id) {
                removePopupFunnySelection();
                addLogoToScreen(itemPosition, MYLOGOS);
            }
        });

        gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                LogoliciousApp.logoOption(act, adapterFunnyGridItems.getItem(position).toString());
                return true;
            }
        });

        System.gc();
        Runtime.getRuntime().gc();
    }

    private void addPrefabTooltipView() {
        FileUtil.fileWrite(GlobalClass.log_path, "Click PrefabLogos", true);
        isShowItems = true;
        FileUtil.fileWrite(GlobalClass.log_path, "Preparing MyLogos Layout", true);
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewG = inflater.inflate(R.layout.grid_mylogos, null);
        gridview = (GridView) viewG.findViewById(R.id.gridviewLogos);

        prefablogoItems.clear();
        adapterFunnyGridItems = new AdapterGridLogos(getApplicationContext(), prefablogoItems, R.layout.grid_logoitem);
        gridview.setAdapter(adapterFunnyGridItems);
        FileUtil.fileWrite(GlobalClass.log_path, "Create Grid Shadow", true);
        ToolTip toolTip = new ToolTip()
                .withContentView(viewG)
                .withText("PreFabs")
                .withColor(getResources().getColor(R.color.GrayLogoPanel))
                .withAnimationType(ToolTip.AnimationType.NONE).withShadow();

        mPrefabsLogosView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.buttonPrefab));
        mPrefabsLogosView.setOnToolTipViewClickedListener(ActivityMainEditor.this);
        new RetrievePrefabLogosTask().execute();

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int itemPosition, long id) {
                removePopupFunnySelection();
                addLogoToScreen(itemPosition, PREFAB);
            }
        });

        gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                LogoliciousApp.logoOption(act, adapterFunnyGridItems.getItem(position).toString());
                return true;
            }
        });

        System.gc();
        Runtime.getRuntime().gc();
    }

    public void addSavedTemplatesTooltipView() {
        isShowItems = true;
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewG = inflater.inflate(R.layout.template_list, null);
        templateLV = (ListView) viewG.findViewById(R.id.templateLV);

        BaseAdapter adapter = new TemplateListAdapter(
                ActivityMainEditor.this,
                new String[]{"1:1", "2:3", "4:3", "16:9", "OTHER SIZES"}
                , backgroundImage, mHandler, layeredLogos
        );
        templateLV.setAdapter(adapter);
        ToolTip toolTip = new ToolTip()
                .withContentView(viewG)
                .withColor(getResources().getColor(R.color.GrayLogoPanel))
                .withAnimationType(ToolTip.AnimationType.NONE).withShadow();

        mSavedTemplate = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.savedTemplates));
        mSavedTemplate.setOnToolTipViewClickedListener(ActivityMainEditor.this);

    }

    public void rotateLogo0(View v) {
        layeredLogos.rotateLogo0();
    }

    public void rotateLogo90(View v) {
        layeredLogos.rotateLogo90();
    }

    private void recievedImageFromOtherApps() {
        // http://developer.android.com/training/sharing/receive.html
        // Get intent, action and MIME type
        Intent intent = getIntent();
        if (null == intent)
            return;
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                //	            handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSentImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //	            handleSendMultipleImages(intent); // Handle multiple images being sent
                res.getString(R.string.ErrorMultipleBaseImgSelect);
            }
        }
    }

    void handleSentImage(Intent intent) {
        GlobalClass.picturePath = null;
        final Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        try {
            if (imageUri != null) {
                try {
                    freeUnneededMemory();
                    resetTranparentSeeker();
                    resetSnapOnGrid();
                    resetAcraData();

                    String originaImagePath = null;
                    if ("content".equals(imageUri.getScheme()))
                        originaImagePath = BitmapSaver.getImagePathFromInputStreamUri(ActivityMainEditor.this, imageUri);
                    else
                        originaImagePath = BitmapSaver.getRealPathFromURI(ActivityMainEditor.this, imageUri);

                    if (LogoliciousApp.getAvailableMemMB(ActivityMainEditor.this) < LogoliciousApp.fileSizeInMbInt(originaImagePath)) {
                        showMemError();
                        return;
                    }

                    // Save Custom variables in ACRA reports.
                    if (!LogoliciousApp.strIsNullOrEmpty(originaImagePath))
                        ACRA.getErrorReporter().putCustomData(GlobalClass.PICTURE_SIZE, LogoliciousApp.fileSizeInMb(originaImagePath));

                    //Check if baseImage is greater of equal to 15mb. If true we continue decoding. Otherwise dismiss.
                    if (LogoliciousApp.fileSizeInMbInt(originaImagePath) >= 15) {
                        final String finalOriginaImagePath = originaImagePath;
                        LogoliciousApp.showYesNoAlert(ActivityMainEditor.this, getString(R.string.Warning), getString(R.string.BaseImageGreaterThan15MBWarningMessage), getString(R.string.Continue), getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        ACRA.getErrorReporter().putCustomData(GlobalClass.ABOVEOREQUAL_15MB_WARNING_RAISED, "Yes");
                                        LogoliciousApp.malloc(ActivityMainEditor.this, (int) LogoliciousApp.fileSizeInBytes(finalOriginaImagePath));
                                        GlobalClass.picturePath = BitmapSaver.saveBitmape(preferences, tempDir, "fromOtherApps", BitmapSaver.exifBitmapOrientationCorrector(ActivityMainEditor.this, imageUri));
                                        continueReceivingFromOtherApp(finalOriginaImagePath);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialogInterface.dismiss();
                                        break;
                                }
                            }
                        });
                    } else {
                        LogoliciousApp.malloc(ActivityMainEditor.this, (int) LogoliciousApp.fileSizeInBytes(originaImagePath));
                        GlobalClass.picturePath = BitmapSaver.saveBitmape(preferences, tempDir, "fromOtherApps", BitmapSaver.exifBitmapOrientationCorrector(ActivityMainEditor.this, imageUri));
                        continueReceivingFromOtherApp(originaImagePath);
                    }


                } catch (Exception ex) {
                    Log.d(TAG, "Error: " + ex.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void continueReceivingFromOtherApp(String originaImagePath) {
        try {
            //Save Exif to new path
            GlobalClass.baseImageExif.copyExif(originaImagePath, GlobalClass.picturePath);
            //Correct orientation
            ImageExif.updateExif(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL, GlobalClass.picturePath);
            //Parse Exif
            GlobalClass.baseImageExif.parse(GlobalClass.picturePath);
        } catch (Exception e) {
            Log.i("xxx", "xxx There's problem in parsing Exif.");
        }

        Bitmap fromOtherApp = BitmapFactory.decodeFile(GlobalClass.picturePath);

        if (null != fromOtherApp) {
            GlobalClass.diskCache.put("BaseImage", fromOtherApp);
            GlobalClass.mMemoryCache.put("BaseImage", fromOtherApp);
            GlobalClass.baseBitmap = fromOtherApp;
            Log.d(TAG, "Handling image from other apps");
        }

        Log.d(TAG, "Picture original path = " + originaImagePath);
        Log.d(TAG, "Picture created path = " + GlobalClass.picturePath);
        LogoliciousApp.callCropper(act, listRight, backgroundImage, DEVICE_WIDTH);
        Log.d(TAG, "Call Cropper");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        adjustLayerView();
    }

    public static void hideLiveAndShowDefaults() {
        LogoliciousApp.setViewVisibility(act, R.id.live_include, false);
        LogoliciousApp.setViewVisibility(act, R.id.backgroundImage, true);
    }

    public static void adjustLayerView() {
        if (backgroundImage.getDrawable() != null) {
            Matrix m = new Matrix();
            m.set(backgroundImage.getImageMatrix());
            float[] values = new float[9];
            m.getValues(values);
            float width = values[Matrix.MSCALE_X] * backgroundImage.getDrawable().getIntrinsicWidth();
            float height = values[Matrix.MSCALE_Y] * backgroundImage.getDrawable().getIntrinsicHeight();

            //centering params
            //1
            android.view.ViewGroup.LayoutParams layoutParams = layeredLogos.getLayoutParams();
            layoutParams.width = (int) width;
            layoutParams.height = (int) height;
            layeredLogos.setLayoutParams(layoutParams);
            backgroundImage.setAdjustViewBounds(true);

            bW = width;
            bH = height;
            layeredLogos.invalidate();
        }
    }

    private void calculateLayoutParams(final ScreenDimensions screen) {

        resultingScreen.post(new Runnable() {

            @Override
            public void run() {
                fullHeight = screen.getDisplayHeight();
                fullWidth = screen.getDisplayWidth();

                ScreenDimensions max = LogoliciousApp.getScreenDimensions(ActivityMainEditor.this, screen.orientation, (double) 16 / (double) 9);

                if (screen.aspectratio < max.aspectratio) {
                    if (Configuration.ORIENTATION_LANDSCAPE == screen.orientation) {
                        fullHeight = max.getDisplayHeight();
                        fullWidth = (int) (screen.aspectratio * (double) fullHeight);
                        left_margins = max.getDisplayWidth() - fullWidth;

                    } else if (Configuration.ORIENTATION_PORTRAIT == screen.orientation) {
                        fullWidth = max.getDisplayWidth();
                        fullHeight = (int) (screen.aspectratio * (double) fullWidth);
                        top_margins = max.getDisplayHeight() - fullHeight;
                    }
                }
                fullScreenParams = new RelativeLayout.LayoutParams(fullWidth, fullHeight);
                fullScreenParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                fullScreenParams.setMargins(left_margins, top_margins, 0, 0);
            }
        });
    }

    public void start_camera(View v, Live_Camera type) {
        layeredLogos.removeAllItems();
        if (Live_Camera.LIVE_PICTURE == type) {
            // blink the live button
            ImageView img = (ImageView) findViewById(R.id.buttonLive);
            img.setImageResource(R.drawable.live_blink);
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getDrawable();
            frameAnimation.start();

            LogoliciousApp.setViewVisibility(this, R.id.backgroundImage, false);
            live_panel.setVisibility(View.VISIBLE);
            LogoliciousApp.setViewVisibility(this, R.id.buttonDoneLive, true);
            LogoliciousApp.setViewVisibility(this, R.id.flipCamera, true);

            ScreenDimensions fullscreen = LogoliciousApp.getScreenDimensions(ActivityMainEditor.this, mOrientation, (double) 16 / (double) 9);
            calculateLayoutParams(fullscreen);
            mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout);
            cameraLayout.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
            parentLayout.gravity = Gravity.CENTER;
            parentView.setLayoutParams(parentLayout);
            parentView.setGravity(Gravity.CENTER);

            /* Hide flip camera button if only one camera a available */
            mCamUtils.handleFlipVisibility();

            if (mCamUtils != null) {
                mCamUtils.resetCamera();
                resultingScreen.post(new Runnable() {

                    @Override
                    public void run() {

                        mCamUtils.setPreviewLayoutParams(fullScreenParams);
                        mCamUtils.setFlashParams(mCamUtils.getFlashMode());
                        mCamUtils.setCameraDisplayOrientation(ActivityMainEditor.this);

                        Size cSize = mCamUtils.mCamera.getParameters().getPictureSize();

                        int new_width = 0;
                        int new_height = 0;
                        double a = 16.0 / 9.0;
                        double b = ((double) resultingScreen.getHeight() / a);
                        new_width = (int) b;
                        new_height = resultingScreen.getHeight();

                        // set the image live dimension here
                        LinearLayout.LayoutParams parentLayout2 = new LinearLayout.LayoutParams(new_width, new_height);
                        parentLayout2.gravity = Gravity.CENTER;
                        parentView.setLayoutParams(parentLayout2);
                        parentView.setGravity(Gravity.CENTER);

                        //adjust layered logo
                        android.view.ViewGroup.LayoutParams layoutParams = layeredLogos.getLayoutParams();
                        layoutParams.width = new_width;
                        layoutParams.height = new_height;
                        layeredLogos.setLayoutParams(layoutParams);

                        bW = new_width;
                        bH = new_height;
                    }
                });

            }

            LIVE_SELECTED = LIVE_CAMERA;
        } else {
            LIVE_SELECTED = LIVE_VIDEO;
        }
        LogoliciousApp.isLive = true;
    }

    private void initSnapOnGrid() {
        editor.putBoolean("SnapOnGrid", false);
        editor.commit();
        boolean sogActivated = preferences.getBoolean("SnapOnGrid", false);
        if (sogActivated)
            ivSOG.setImageResource(R.drawable.snap_on_grid_activated);
        else {
            ivSOG.setImageResource(R.drawable.snap_on_grid);
        }
    }

    public void undo(View v) {
        layeredLogos.undo(v, ActivityMainEditor.this);
    }

    public void redo(View v) {
        layeredLogos.redo(v, ActivityMainEditor.this);
    }

    private void freeUnneededMemory() {
        //Free as much memory
        if (null != GlobalClass.baseBitmap) {
            GlobalClass.baseBitmap = null;
            backgroundImage.setImageDrawable(null);
        }
        GlobalClass.baseBitmap = null;
        GlobalClass.picturePath = null;
        if (null != GlobalClass.diskCache)
            GlobalClass.diskCache.clearCache();
        //Clear logos in canvas
        if (null != layeredLogos)
            layeredLogos.removeAllItems();
        System.gc();
        GlobalClass.freeMem();
    }

    /**
     * Release memory when the UI becomes hidden or when system resources become low.
     *
     * @param level the memory-related event that was raised.
     *              The onTrimMemory() callback was added in Android 4.0 (API level 14). For earlier versions,
     *              you can use the onLowMemory(), which is roughly equivalent to the TRIM_MEMORY_COMPLETE event
     */
    public void onTrimMemory(int level) {
    }

    private void onResultFromCamera() {
        File f = new File(tempDir);
        for (File temp : f.listFiles()) {
            if (temp.getName().contains(res.getString(R.string.PictureViaCamFName))) {
                f = temp;
                GlobalClass.picturePath = f.getAbsolutePath();
                Log.i(TAG, "REQUEST_CODE_TAKE_PHOTO = " + GlobalClass.picturePath);

                //Compress picture from Camera (Has been discussed before) due to OOM issue.
                //LogoliciousApp.malloc(getApplicationContext(), (int) LogoliciousApp.fileSizeInBytes(GlobalClass.picturePath));
                GlobalClass.picturePath = BitmapSaver.saveBitmape(preferences, tempDir, "picture_taken", BitmapSaver.exifBitmapOrientationCorrector(ActivityMainEditor.this, GlobalClass.picturePath));
                try {
                    /**
                     * No need to copy since this path is the final when taking picture.
                     * //Save Exif to new path
                     GlobalClass.baseImageExif.copyExif(GlobalClass.picturePath, GlobalClass.picturePath);
                     */
                    //Correct orientation
                    ImageExif.updateExif(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL, GlobalClass.picturePath);
                    //Parse Exif
                    GlobalClass.baseImageExif.parse(GlobalClass.picturePath);
                } catch (Exception e) {
                    Log.i("xxx", "xxx There's problem in parsing Exif.");
                }
                break;
            }
        }

        GlobalClass.baseBitmap = BitmapFactory.decodeFile(GlobalClass.picturePath); //ImageHelper.decodeBitmapPath(GlobalClass.picturePath); //ImageHelper.correctBitmapRotation(GlobalClass.picturePath, ImageHelper.decodeBitmapPath(GlobalClass.picturePath));

        // watch onWindowFocusChanged below
        // get the width and height of the resulting screen
//        backgroundImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                backgroundImage.getViewTreeObserver().removeOnPreDrawListener(this);
//
//                bH = backgroundImage.getHeight();
//                bW = backgroundImage.getWidth();

        if (LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
            LogoliciousApp.toast(ActivityMainEditor.this, res.getString(R.string.ErrorAfterCameraCapture), Toast.LENGTH_LONG);
            return;
        }

        Log.d(TAG, "Picture retrieve path = " + GlobalClass.picturePath);
        LogoliciousApp.callCropper(act, listRight, backgroundImage, DEVICE_WIDTH);
//                return false;
//            }
//        });
    }

    private void onResultFromGallery(Intent data) {
        try {
            final Uri imageUri = data.getData();
            String url = data.getData().toString();
            String originaImagePath = null;
            if ("content".equals(imageUri.getScheme()))
                originaImagePath = BitmapSaver.getImagePathFromInputStreamUri(ActivityMainEditor.this, imageUri);
            else
                originaImagePath = BitmapSaver.getRealPathFromURI(ActivityMainEditor.this, imageUri);

            if (LogoliciousApp.getAvailableMemMB(ActivityMainEditor.this) < LogoliciousApp.fileSizeInMbInt(originaImagePath)) {
                showMemError();
                return;
            }

            // Save Custom variables in ACRA reports.
            if (!LogoliciousApp.strIsNullOrEmpty(originaImagePath))
                ACRA.getErrorReporter().putCustomData(GlobalClass.PICTURE_SIZE, LogoliciousApp.fileSizeInMb(originaImagePath));

            //Check if baseImage is greater of equal to 15mb. If true we continue decoding. Otherwise dismiss.
            if (LogoliciousApp.fileSizeInMbInt(originaImagePath) >= 15) {
                final String finalOriginaImagePath = originaImagePath;
                LogoliciousApp.showYesNoAlert(ActivityMainEditor.this, getString(R.string.Warning), getString(R.string.BaseImageGreaterThan15MBWarningMessage), getString(R.string.Continue), getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                ACRA.getErrorReporter().putCustomData(GlobalClass.ABOVEOREQUAL_15MB_WARNING_RAISED, "Yes");
                                LogoliciousApp.malloc(ActivityMainEditor.this, (int) LogoliciousApp.fileSizeInBytes(finalOriginaImagePath));
                                GlobalClass.picturePath = BitmapSaver.saveBitmape(preferences, tempDir, "fromAppGallery", BitmapSaver.exifBitmapOrientationCorrector(ActivityMainEditor.this, imageUri));
                                continueReceivingFromGallery(finalOriginaImagePath);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                });
            } else {
                LogoliciousApp.malloc(ActivityMainEditor.this, (int) LogoliciousApp.fileSizeInBytes(originaImagePath));
                GlobalClass.picturePath = BitmapSaver.saveBitmape(preferences, tempDir, "fromAppGallery", BitmapSaver.exifBitmapOrientationCorrector(ActivityMainEditor.this, imageUri));
                continueReceivingFromGallery(originaImagePath);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void continueReceivingFromGallery(String originaImagePath) {
        try {
            //Save Exif to new path
            GlobalClass.baseImageExif.copyExif(originaImagePath, GlobalClass.picturePath);
            //Correct orientation
            ImageExif.updateExif(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL, GlobalClass.picturePath);
            //Parse Exif
            GlobalClass.baseImageExif.parse(GlobalClass.picturePath);
        } catch (Exception e) {
            Log.i("xxx", "xxx There's problem in parsing Exif.");
        }

        //add memory allocation for gallery picture
        try {
            LogoliciousApp.malloc(getApplicationContext(), (int) LogoliciousApp.fileSizeInBytes(GlobalClass.picturePath));
            GlobalClass.baseBitmap = BitmapFactory.decodeFile(GlobalClass.picturePath);

            Log.d(TAG, "Picture original path = " + originaImagePath);
            Log.d(TAG, "Picture created path = " + GlobalClass.picturePath);
            LogoliciousApp.callCropper(act, listRight, backgroundImage, DEVICE_WIDTH);
        } catch (Exception ex) {
            Toast.makeText(ActivityMainEditor.this, "Picture cannot be read. Please use default Gallery app as image source.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showMemError() {
        LogoliciousApp.showMessageOK(ActivityMainEditor.act, ActivityMainEditor.act.getString(R.string.MemoryLowAlertMessageV2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                GlobalClass.pendingShowMemAlert = false;
            }
        });
        ACRA.getErrorReporter().putCustomData("MEM_LOW_WARNING_RAISED", "Yes");
    }

    private void showMemoryDetails() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.memory_details);
        d.setTitle("Memory Details");
        d.setCancelable(true);

        TextView totalMem = (TextView) d.findViewById(R.id.totalMemory);
        TextView availableMem = (TextView) d.findViewById(R.id.availableMemory);

        double dTotalMem = (int) (LogoliciousApp.getAvailableMemory(ActivityMainEditor.this).totalMem / 0x100000L);
        totalMem.setText(String.format("%d%s", (int) dTotalMem, "mb"));
        availableMem.setText(String.format("%d%s", LogoliciousApp.getAvailableMemMB(ActivityMainEditor.this), "mb"));
        d.show();
    }

    private void resetTranparentSeeker() {
        if (null != seekbarTrans)
            seekbarTrans.setProgress(252);
    }

    private void resetSnapOnGrid() {
        ivSOG.setImageResource(R.drawable.snap_on_grid);
        editor.putBoolean("SnapOnGrid", false);
        editor.commit();
    }

    public static void resetAcraLogoUpload() {
        for (int i = 0; i <= 30; i++) {
            ACRA.getErrorReporter().removeCustomData(GlobalClass.LOGO_UPLOADED + i);
        }
        GlobalClass.LOGO_UPLOADED_COUNT = 0;
    }

    public static void resetAcraLogoSelected() {
        for (int i = 0; i <= 30; i++) {
            ACRA.getErrorReporter().removeCustomData(GlobalClass.LOGO_SELECTED + i);
        }
        GlobalClass.LOGO_SELECTED_COUNT = 0;
    }

    public static void resetAcraLogoApplyTempate() {
        for (int i = 0; i <= 30; i++) {
            ACRA.getErrorReporter().removeCustomData(GlobalClass.LOGO_APPLY_TEMPLATE + i);
        }
        GlobalClass.LOGO_APPLY_TEMPLATE_COUNT = 0;
    }

    private void resetAcraData() {
        resetAcraLogoUpload();
        resetAcraLogoSelected();
        resetAcraLogoApplyTempate();
        ACRA.getErrorReporter().removeCustomData(GlobalClass.ABOVEOREQUAL_15MB_WARNING_RAISED);
        ACRA.getErrorReporter().removeCustomData(GlobalClass.PICTURE_SIZE);
        ACRA.getErrorReporter().removeCustomData(GlobalClass.ABOVEOREQUAL_15MB_WARNING_RAISED);
        ACRA.getErrorReporter().removeCustomData(GlobalClass.MEM_LOW_WARNING_RAISED);
        ACRA.getErrorReporter().removeCustomData(GlobalClass.GENERATED_DATE);
        ACRA.getErrorReporter().removeCustomData(GlobalClass.APP_AVAILABLE_MEM_SIZE);
    }

    private void snapOnGridToggle() {
        boolean sogActivated = preferences.getBoolean("SnapOnGrid", false);
        if (sogActivated) {
            ivSOG.setImageResource(R.drawable.snap_on_grid);
            editor.putBoolean("SnapOnGrid", false);
            editor.commit();
        } else {
            ivSOG.setImageResource(R.drawable.snap_on_grid_activated);
            editor.putBoolean("SnapOnGrid", true);
            editor.commit();
        }
        layeredLogos.invalidate();
    }
}