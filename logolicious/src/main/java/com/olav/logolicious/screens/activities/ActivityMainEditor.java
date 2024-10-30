package com.olav.logolicious.screens.activities;

import static android.os.Build.VERSION.SDK_INT;
import static com.olav.logolicious.util.GlobalClass.PICK_FONT_RESULT_CODE;
import static com.olav.logolicious.util.GlobalClass.sqLiteHelper;
import static com.olav.logolicious.util.LogoliciousApp.isLive;
import static com.olav.logolicious.util.LogoliciousApp.selectedFontPath;
import static com.olav.logolicious.util.LogoliciousApp.showMessageOK;
import static com.olav.logolicious.util.LogoliciousApp.toast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.olav.logolicious.BuildConfig;
import com.olav.logolicious.R;
import com.olav.logolicious.billingv4.MyBillingImpl;
import com.olav.logolicious.customize.adapters.AdapterFontDetails;
import com.olav.logolicious.customize.adapters.AdapterFonts;
import com.olav.logolicious.customize.adapters.AdapterGridLogos;
import com.olav.logolicious.customize.adapters.ArrayHolderLogos;
import com.olav.logolicious.customize.adapters.ColorPickerAdapter;
import com.olav.logolicious.customize.adapters.CustomColorAdapter;
import com.olav.logolicious.customize.adapters.TemplateListAdapter;
import com.olav.logolicious.customize.datamodel.ImageExif;
import com.olav.logolicious.customize.widgets.DynamicImageView;
import com.olav.logolicious.customize.widgets.LayersContainerView;
import com.olav.logolicious.customize.widgets.MarginDecoration;
import com.olav.logolicious.screens.fragments.TipFontFeature;
import com.olav.logolicious.supertooltips.ToolTip;
import com.olav.logolicious.supertooltips.ToolTipRelativeLayout;
import com.olav.logolicious.supertooltips.ToolTipView;
import com.olav.logolicious.util.ClickColorListener;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.FileUtils;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.HexColorValidator;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.PrefStore;
import com.olav.logolicious.util.SQLiteHelper;
import com.olav.logolicious.util.SavingTemplateListener;
import com.olav.logolicious.util.SubscriptionUtil.AppStatitics;
import com.olav.logolicious.util.camera.CameraUtils;
import com.olav.logolicious.util.camera.ScreenDimensions;
import com.olav.logolicious.util.image.BitmapSaver;
import com.olav.logolicious.util.image.ImageHelper;
import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import org.acra.ACRA;

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

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ActivityMainEditor extends AppCompatActivity implements
        OnTouchListener,
        ToolTipView.OnToolTipViewClickedListener,
        OnClickListener,
        SavingTemplateListener {

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
    private static final int REQUEST_BROWSE_FONT = 6;
    private static final int REQUEST_MANAGE_ALL_FILES_PERM = 7;

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

    public LinearLayout bottomSlidersContainer;
    public RelativeLayout resultingScreen, listRight;
    public static ImageView backgroundImage;
    private ImageView ivSOG;

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

    // Live Button Feature
    Camera mCamera;
    SurfaceView mPreview;
    SurfaceHolder surfaceHolder;

    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;

    private Button start, stop, capture;

    private View live_panel;
    private ArrayList<String> customColorsArray = new ArrayList<>();
    private CustomColorAdapter customColorAdapter;
    public String colorSelected;
    public String currentText = "";
    public int colorSelectedText = Color.parseColor("#fffdff");
    private static int mStackLevel = 0;
    private boolean initColor = true;
    private Handler handlerColorPickerDetector = new Handler();
    public static PrefStore store;

    //Billing implementation
    public static MyBillingImpl billingHelper = null;
    public static List<ProductDetails> skuDetailsList = new ArrayList<>();
    public static List<Purchase> purchasesList = new ArrayList<>();
    ProgressDialog mProgressDialog;
    AlertDialog mDialog;
    private static final int UPDATE_APP_REQUEST_CODE = 1001;
    public static final String IS_DONE_SHOWING_UPDATE_PROMPT = "IS_DONE_SHOWING_UPDATE_PROMPT";
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;
    public ArrayList<AdapterFontDetails> arrayFonts = new ArrayList<>();
    private AdapterFonts adapterFonts;
    private AlertDialog alert = null;
    private ActivityResultLauncher<Intent> updateLauncher;

    @Override
    public void onSuccessSavingTemplate() {
        if (null != mDialog && mDialog.isShowing() && !isFinishing()) {
            layeredLogos.invalidate(0, 0, 0, 0);
        }
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
    private HexColorValidator colorValidator = new HexColorValidator();

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

    public interface PermissionCallback {
        void permGranted();

        void permDenied();
    }

    public PermissionCallback permCallback;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // Registers a photo picker activity launcher in single-select mode.
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                freeUnneededMemory();
                resetTranparentSeeker();
                resetSnapOnGrid();
                resetAcraData();
                isLive = false;

                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this)) {
                        showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        onResultFromGallery(null, uri);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialogInterface.cancel();
                                        break;
                                }
                            }
                        });
                    } else {
                        onResultFromGallery(null, uri);
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

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
        store = new PrefStore(this);

        gc = (GlobalClass) this.getApplicationContext();

        /*if (null == bp) {
            bp = new BillingProcessor(this, SubscriptionUtil.base64EncodedPublicKey, this);
            bp.initialize();
            // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
            // See below on why this is a useful alternative
        }*/

        res = getResources();
        act = ActivityMainEditor.this;
        preferences = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        backgroundImage = findViewById(R.id.backgroundImage);
        ivSOG = findViewById(R.id.buttonSnapOnGrid);
        layeredLogos = findViewById(R.id.layeredLogos);
        layeredLogos.setLongClickInterface(new TextLongClickForEdit() {
            @Override
            public void onLongClick() {
                updateText(ActivityMainEditor.this, backgroundImage, layeredLogos, false);
            }
        });
        imageViewLogo = findViewById(R.id.imageViewLogo);
        bottomSlidersContainer = findViewById(R.id.bottomSlidersContainer);
        listRight = findViewById(R.id.menuRight);
        resultingScreen = findViewById(R.id.photo);
        seekbarTrans = findViewById(R.id.seekBarTrans);
        buttonTrashcan = findViewById(R.id.buttonTrashcan);

        // Live Feature UI
        live_panel = findViewById(R.id.live_include);
        parentView = findViewById(R.id.picture_content_parent_view_host);
        cameraLayout = findViewById(R.id.full_camera_content);

        //set initial data
        if (null != preferences) {
            if (preferences.getBoolean("dontAskMeAgain", false)) {
                editor.putBoolean("dontAskMeAgain", true);
                editor.apply();
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
                Drawable thumbTransparent = ImageHelper.resizeDrawable(getResources(), getResources().getDrawable(R.drawable.slider_indicator_01), seekbarTrans.getHeight() + 5, (seekbarTrans.getHeight() + 5) > 45 ? 45 : (seekbarTrans.getHeight() + 5));
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
        DEVICE_WIDTH = getWindowManager().getDefaultDisplay().getWidth();
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
                        /*if (GlobalClass.subscriptionOkToShow && (backgroundImage.getDrawable() != null && !LogoliciousApp.isLive) || !LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
                            isShowSubscription(this);
                        }*/
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
        selectedFontPath = null;
        if (c.getCount() > 0) {
            c.moveToNext();
            selectedFontPath = c.getString(0);
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
            new LoadFontsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new LoadFontsTask().execute();

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

        if (!LogoliciousApp.sharedPreferenceExist(this, "delete_old_fonts")) {
            LogoliciousApp.sharedPreferenceSet(this, "delete_old_fonts", 0);
        }

        if (LogoliciousApp.sharedPreferenceGet(this, "delete_old_fonts", 0) == 0) {
            sqLiteHelper.deleteAllUserFonts();
            LogoliciousApp.sharedPreferenceSet(this, "delete_old_fonts", 1);
        }

        if (!LogoliciousApp.hasPermissionNeeded(ActivityMainEditor.this)) {
            recievedImageFromOtherApps();
        }

        billingHelper = new MyBillingImpl(this, purchasesList);
        checkAppVersionUpdate();
    }

    public void appCheckSelfPermission(String[] perms, PermissionCallback permCallback) {
        this.permCallback = permCallback;
        ActivityCompat.requestPermissions(this, perms, 99);
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
    protected void onStop() {
        super.onStop();
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    public void onDestroy() {
        /*if (bp != null) {
            bp.release();
        }*/
        super.onDestroy();
        Log.i(TAG, "xxx onDestroy");
        if (null != GlobalClass.diskCache)
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
        store.setBoolean(IS_DONE_SHOWING_UPDATE_PROMPT, false);
    }

    private static void checkSubscription(Activity act) {
        //Statistics
        AppStatitics.initializeSaveCount(act);
        //Check Subscription
        /*bp.loadOwnedPurchasesFromGoogle();
        td = bp.getSubscriptionTransactionDetails(SUBSCRIPTION_SKU);
        if (null != td) {
            Log.i(TAG, "xxx transaction " + td.toString());
            //get purchased info
            pInfo = td.purchaseInfo;
            if (null != pInfo) {
                if (!pInfo.purchaseData.autoRenewing) {
                    Log.i("xxx", "xxx You have cancelled your Subscription.");
                    AppStatitics.sharedPreferenceSet(act, "isSubscribed", 0);
                } else if (pInfo.purchaseData.purchaseState.name().equals(PurchaseState.PurchasedSuccessfully.name())) {
                    Log.i("xxx", "xxx You have your Subscription.");
                    AppStatitics.sharedPreferenceSet(act, "isSubscribed", 1);
                }
            }
        }*/

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "xxx onStart");
        GlobalClass.freeMem();
        checkSubscription(this);

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
        checkSubscription(this);
        isSomeActivityIsRunning = false;
        mOrientation = this.getResources().getConfiguration().orientation;
        isMinimized = false;
        gc.setCurrentActivity(this);

        if (null != billingHelper && billingHelper.billingClient.isReady()) {
            billingHelper.queryPurchases();
        }
        newVersionHasDownloaded();
    }

    private void initPaths() {
        root = new File(GlobalClass.getAppContext().getExternalFilesDir(null).getAbsolutePath(), "LogoLicious");
        String fs = File.separator;
        logoDir = root + fs + App_Files_location + fs + ".logos" + fs;
        tempDir = root + fs + App_Files_location + fs + ".temp" + fs;
        tempShareDir = root + fs + App_Files_location + fs + ".temp" + fs + "sharedPictures";
        liveDir = root + fs + App_Files_location + fs + ".live" + fs;
        tempSavedPics = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/LogoLicious/"; //root.getAbsolutePath() + fs + "LogoLicious";
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
        boolean permGrantedBool = false;
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ensure items parent directory
                    initPaths();
                    FileUtil.createDirs(new String[]{GlobalClass.log_path, logoDir, tempDir, tempShareDir, tempSavedPics, liveDir});
                }
                break;
            case LogoliciousApp.REQUEST_ID_MULTIPLE_PERMISSIONS:
                initPaths();
                FileUtil.createDirs(new String[]{GlobalClass.log_path, logoDir, tempDir, tempShareDir, tempSavedPics, liveDir});
                break;
            case 99:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        /*showDialogMessageAndOkButton("Please allow needed permissions",
                                "Okay",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (null != alertDialog && !isFinishing())
                                            alertDialog.dismiss();

                                        Intent intent = new Intent(
                                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });*/
                        permGrantedBool = false;
                        break;
                    } else if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permGrantedBool = true;
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool)
                        permCallback.permGranted();
                    else
                        permCallback.permDenied();
                }
                break;
            case 2296:
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // perform action when allow permission success
                        permCallback.permGranted();
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                        permCallback.permDenied();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        /*if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SHARE_ACTION) {
                isSomeActivityIsRunning = false;
            }
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {

                if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this)) {
                    showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    onResultFromCamera(data);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.cancel();
                                    break;
                            }
                        }
                    });
                } else {
                    onResultFromCamera(data);
                }
            } else if (requestCode == REQUEST_CODE_CHOOSE_FROM_GALLERY) {

                if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this)) {
                    showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    onResultFromGallery(data, null);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.cancel();
                                    break;
                            }
                        }
                    });
                } else {
                    onResultFromGallery(data, null);
                }
            } else if (requestCode == REQUEST_CODE_CHOOSE_LOGO_FROM_GAL) {
                GlobalClass.logoPath = null;
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                if (null == c) {
                    showMessageOK(ActivityMainEditor.this, getString(R.string.MessageErrorOnLogoUpload), null);
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
                try {
                    FileUtil.copyFile(new FileInputStream(content_describer.getPath()), new FileOutputStream(fontsDir + content_describer.getLastPathSegment()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == REQUEST_MANAGE_ALL_FILES_PERM) {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        addFont();
                    } else {
                        toast(this, "MANAGE_EXTERNAL_STORAGE permission is needed" +
                                " in order to copy font file to app directory.", Toast.LENGTH_SHORT);
                    }
                }
            }

            if (requestCode == REQUEST_BROWSE_FONT) {
                FileUtils fileUtils = new FileUtils(this);
                Uri currFileURI = data.getData();
                String path = fileUtils.getPath(currFileURI);
                File font_file = new File(path);
                toast(this, "Font path " + path, Toast.LENGTH_SHORT);

                if (path.contains(".ttf")) {
                    appCheckSelfPermission(new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            new PermissionCallback() {
                                @Override
                                public void permGranted() {

                                    //Check if font already exist
                                    if (sqLiteHelper.checkIfFontExist(path)) {
                                        toast(ActivityMainEditor.this,
                                                "This font already exist on your font list.", Toast.LENGTH_SHORT);
                                        return;
                                    }

//                                    try {
//                                        File file_copy = new File(ActivityMainEditor.fontsDir + font_file.getName());
//                                        if (!file_copy.exists()) {
//                                            FileUtil.copyFile(new FileInputStream(path),
//                                                    new FileOutputStream(
//                                                            ActivityMainEditor.fontsDir + font_file.getName()));
                                    sqLiteHelper.insertFont(path);
//                                        }
                                    new LoadFontsTask().execute();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
                                }

                                @Override
                                public void permDenied() {

                                }
                            });
                } else {
                    toast(this,
                            "Incorrect file type selected. \n" +
                                    "LogoLicious only supports .ttf\nfont files", Toast.LENGTH_SHORT);
                }
            }
        }

        if (requestCode == UPDATE_APP_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("xxx", "xxx onActivityResult: app download failed");
                // If the update is cancelled or fails,
                // you can request to start the update again.
                checkAppVersionUpdate();
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return "";
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        removePopupFunnySelection();
        //image.onTouch(image, event);
        if (v.getId() == R.id.layeredLogos) {
            if (null != layeredLogos && null != layeredLogos.targetSelected)
                layeredLogos.onTouch(v, event);
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

        if (backgroundImage.getDrawable() == null && !isLive) {
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
                toast(getApplicationContext(), "The image you are trying to upload is big in file size.", Toast.LENGTH_LONG);
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

    public void addFont() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            appCheckSelfPermission(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
                @Override
                public void permGranted() {
                    browseFont();
                }

                @Override
                public void permDenied() {

                }
            });
        } else {
            appCheckSelfPermission(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
                @Override
                public void permGranted() {
                    browseFont();
                }

                @Override
                public void permDenied() {

                }
            });
        }
    }

    private void browseFont() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Browse Font"), REQUEST_BROWSE_FONT);
    }

    public void saveTemplate(View v) {
        if (isLive) {
            toast(getApplicationContext(), res.getString(R.string.SavingTemplateMessage), 1);
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

                                    showMessage("Template Saved");
                                    layeredLogos.saveAsTemplate(ActivityMainEditor.this, templateName.getText().toString() + GlobalClass.getAR(), ActivityMainEditor.this);
                                    break;
                            }
                        }
                    });
                    return;
                }

                if (templateName.getText().toString().contains(".")) {
                    toast(ActivityMainEditor.this, "Must not contain period in template name.", Toast.LENGTH_SHORT);
                    return;
                }

                d.dismiss();
                showMessage("Template Saved");
                layeredLogos.saveAsTemplate(ActivityMainEditor.this, templateName.getText().toString() + GlobalClass.getAR(), ActivityMainEditor.this);
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
            toast(getApplicationContext(), "No Template to save.", 1);

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
        if (!isForSharing) {
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

        // set new date time
        if (null != fileToSave)
            fileToSave.setLastModified(SystemClock.currentThreadTimeMillis());
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

            //Add to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(fileToSave);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);


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
            File[] file = f.listFiles(new FilenameFilter() {
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this))
                showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), null);

            forSharing = false;
            GlobalClass.freeMem();

            showProgress("Saving your file. \nGive us a moment while we save this in " + FileUtil.getImageQualityTypeDescription(preferences) + ".");
        }

        protected String doInBackground(Integer... task) {
            return saveFinalImage(false, false);
        }

        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();

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
            } else {
                rateApp();
                System.gc();
                Runtime.getRuntime().gc();
                AppStatitics.addSaveShareCount(ActivityMainEditor.this);
                // success saved message
                if (!layeredLogos.isLayerEmpty()) {
                    toast(getApplicationContext(), getString(R.string.AfterSavingMessage) + fileToSave.toString(), Toast.LENGTH_LONG);
                }

                if (1 == AppStatitics.sharedPreferenceGet(ActivityMainEditor.this, "hasOOM", 0)) {
                    showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessageForLogo), null);
                }

                GlobalClass.freeMem();
                editor.putBoolean("ProceedEvenNoMemAvailable", false);
                editor.commit();
            }
        }
    }

    public static void isShowSubscription(Activity act) {
        //check user already subscribed
        checkSubscription(act);
        if (0 == AppStatitics.sharedPreferenceGet(act, "isSubscribed", 0)) {
            AppStatitics.showSubscription(act, AppStatitics.sharedPreferenceGet(act, "STAT_SAVE_SHARE_COUNT", 0));
            Log.i("xxx", "xxx b isShowSubscription ");
        }

    }

    private void rateApp() {
        Cursor cur = GlobalClass.sqLiteHelper.getSaveCount();
        try {
            while (cur.moveToNext()) {
                int saveCount = cur.getInt(0);
                // rating id in logolicious table
                int id = cur.getInt(1);
                //rating
                int isRated = cur.getInt(2);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (LogoliciousApp.isMemoryLow(ActivityMainEditor.this))
                showMessageOK(ActivityMainEditor.this, getString(R.string.MemoryLowAlertMessage), null);

            forSharing = true;
            showProgress("Sharing your file. \nGive us a moment while we share this in " + FileUtil.getImageQualityTypeDescription(preferences) + ".");
        }

        protected String doInBackground(String... param) {
            return saveFinalImage(false, true);
        }

        protected void onPostExecute(String picturePath) {
            mProgressDialog.dismiss();

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
            } else {
                rateApp();
                System.gc();
                Runtime.getRuntime().gc();
                AppStatitics.addSaveShareCount(ActivityMainEditor.this);
                ActivityMainEditor.picturePath = picturePath;

                //Show share list.
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                //shareIntent.setType("image/" + FileUtil.getImageType(preferences)); // text/plain
                shareIntent.setType("message/rfc822");
                String shareText = getResources().getString(R.string.label_sharetext);
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "LogoLicious");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
                Uri uri = FileProvider.getUriForFile(ActivityMainEditor.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(photoUri.getPath()));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(uri, getContentResolver().getType(uri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                //shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.freeMem();

            showProgress("Uploading Logo");
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
                    mProgressDialog.dismiss();
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
                toast(getApplicationContext(), "malloc() called. Available mem = " + LogoliciousApp.getAvailableMemMB(getApplicationContext()), Toast.LENGTH_SHORT);
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
                addText(act, backgroundImage, layeredLogos, false, false);
                break;
            case R.id.buttonTextColor:
                showColor();
                break;
            case R.id.save: {
                System.gc();
                Runtime.getRuntime().gc();

                hideTips();
                // Send a saving progress notification
                if (isLive) {
                    toast(getApplicationContext(), res.getString(R.string.SavingImageWhileonLiveMessage), 1);
                    break;
                }

                if ((backgroundImage.getDrawable() == null && !isLive) || LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
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
                if (isLive) {
                    toast(getApplicationContext(), res.getString(R.string.SharingImageWhileonLiveMessage), 1);
                    break;
                }

                if ((backgroundImage.getDrawable() == null && !isLive) || LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
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
                }
                // off the blink the live button
                ImageView img = (ImageView) findViewById(R.id.buttonLive);
                img.setImageResource(R.drawable.live);

                LogoliciousApp.setViewVisibility(this, R.id.buttonDoneLive, false);
                LogoliciousApp.setViewVisibility(this, R.id.flipCamera, false);

                isLive = false;
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
                    toast(getApplicationContext(), res.getString(R.string.SavingImageMessage), 1);
                    break;
                case MESSAGE_SHARING_IMAGE_ERROR:
                    toast(getApplicationContext(), res.getString(R.string.SharingImageMessage), 1);
                    break;
                case MESSAGE_APPLY_TEMPLATE_ERROR:
                    LogoliciousApp.showAlertOnUpLoadLogo(act, R.layout.upload_logo_alert, "Oops", "", true);
                    break;
                case MESSAGE_APPLY_TEMPLATE:
                    LogoliciousApp.setViewVisibility(ActivityMainEditor.this, R.id.templateProgress, true);
                    resetAcraLogoApplyTempate();
                    layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(LogoliciousApp.selected_template_name), backgroundImage, false);
                    if (LayersContainerView.geteMissingFonts().size() > 0) {
                        showMessageOK(ActivityMainEditor.this,
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

    public void callGallery(View v) {
        if (LogoliciousApp.verifyStoragePermissions(this, LogoliciousApp.PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)) {
            freeUnneededMemory();
            resetTranparentSeeker();
            resetSnapOnGrid();
            resetAcraData();

            isLive = false;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_FROM_GALLERY);
        }

    }

    String currentPhotoPath, currentPhotoDirPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = res.getString(R.string.PictureViaCamFName); //"JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        currentPhotoDirPath = image.getParent();
        return image;
    }

    public void callCamera(View v) {
        if (LogoliciousApp.verifyCameraPermissions(this, LogoliciousApp.PERMISSIONS_CAMERA, REQUEST_CAMERA)) {
            Log.i(TAG, "Permission Storage Granted.");
            isLive = false;

            freeUnneededMemory();
            resetTranparentSeeker();
            resetSnapOnGrid();
            resetAcraData();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    currentPhotoPath = photoFile.getPath();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    showMessageOK(this, "Error creating file " + ex.getMessage(), null);
                }

                Uri photoURI = FileProvider.getUriForFile(ActivityMainEditor.this,
                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    public void callGalerryToSelectLogo(View v) {
        if ((LogoliciousApp.isBaseImageNull(backgroundImage) == true && !isLive) || GlobalClass.baseBitmap == null) {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // Include only one of the following calls to launch(), depending on the types
                    // of media that you want to let the user choose from.

                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                } else {
                    if (LogoliciousApp.verifyStoragePermissions(ActivityMainEditor.this, LogoliciousApp.PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE))
                        callGallery(v);
                }
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
        ListView templateLV = (ListView) viewG.findViewById(R.id.templateLV);

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
        isLive = true;
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

    private void onResultFromCamera(Intent data) {
        GlobalClass.picturePath = currentPhotoPath;
        Log.i(TAG, "REQUEST_CODE_TAKE_PHOTO = " + GlobalClass.picturePath);

        //Compress picture from Camera (Has been discussed before) due to OOM issue.
        //LogoliciousApp.malloc(getApplicationContext(), (int) LogoliciousApp.fileSizeInBytes(GlobalClass.picturePath));
        GlobalClass.picturePath = BitmapSaver.saveBitmape(preferences, currentPhotoDirPath + "/", "picture_taken", BitmapSaver.exifBitmapOrientationCorrector(ActivityMainEditor.this, GlobalClass.picturePath));
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

        GlobalClass.baseBitmap = BitmapFactory.decodeFile(GlobalClass.picturePath); //ImageHelper.decodeBitmapPath(GlobalClass.picturePath); //ImageHelper.correctBitmapRotation(GlobalClass.picturePath, ImageHelper.decodeBitmapPath(GlobalClass.picturePath));

        if (LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
            toast(ActivityMainEditor.this, res.getString(R.string.ErrorAfterCameraCapture), Toast.LENGTH_LONG);
            return;
        }

        Log.d(TAG, "Picture retrieve path = " + GlobalClass.picturePath);
        LogoliciousApp.callCropper(act, listRight, backgroundImage, DEVICE_WIDTH);
    }

    private void onResultFromGallery(Intent data, Uri pickerUri) {
        try {
            final Uri imageUri = (pickerUri != null) ? pickerUri : data.getData();
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
        showMessageOK(ActivityMainEditor.act, ActivityMainEditor.act.getString(R.string.MemoryLowAlertMessageV2), new DialogInterface.OnClickListener() {
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

        TextView totalMem = d.findViewById(R.id.totalMemory);
        TextView availableMem = d.findViewById(R.id.availableMemory);

        double dTotalMem = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            dTotalMem = (int) (LogoliciousApp.getAvailableMemory(ActivityMainEditor.this).totalMem / 0x100000L);
        }
        totalMem.setText(String.format(Locale.US, "%d%s", (int) dTotalMem, "mb"));
        availableMem.setText(String.format(Locale.US, "%d%s", LogoliciousApp.getAvailableMemMB(ActivityMainEditor.this), "mb"));
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

    private void showColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View pickerContainer = inflater.inflate(R.layout.color_picker, null);

        builder.setView(pickerContainer);
        final AlertDialog d = builder.create();

        RecyclerView gridview = pickerContainer.findViewById(R.id.gridViewColors);
        RecyclerView gridviewCustomColors = pickerContainer.findViewById(R.id.gridViewCustomColors);
        Button newColor = pickerContainer.findViewById(R.id.newColor);
        ImageView close = pickerContainer.findViewById(R.id.close);
        gridview.addItemDecoration(new MarginDecoration(this));
        gridview.setHasFixedSize(true);
        gridview.setAdapter(new ColorPickerAdapter(getApplicationContext(), new ClickColorListener() {
            @Override
            public void onColorSelect(String colorCode) {
                if (layeredLogos.targetSelected != null && layeredLogos.targetSelected.isTextMode) {
                    layeredLogos.targetSelected.changeTextColor(Color.parseColor(colorCode));
                    layeredLogos.invalidate();
                }
                d.dismiss();
            }
        }));

        customColorsArray.clear();
        Cursor colorCursor = GlobalClass.sqLiteHelper.getCustomColors();
        while (colorCursor.moveToNext()) {
            customColorsArray.add(colorCursor.getString(colorCursor.getColumnIndex(SQLiteHelper.COLOR_CODE)));
        }
        customColorAdapter = new CustomColorAdapter(this, customColorsArray, new ClickColorListener() {
            @Override
            public void onColorSelect(String colorCode) {
                if (!TextUtils.isEmpty(colorCode) && layeredLogos.targetSelected != null && layeredLogos.targetSelected.isTextMode) {
                    layeredLogos.targetSelected.changeTextColor(Color.parseColor(colorCode));
                    layeredLogos.invalidate();
                    d.dismiss();
                } else {
                    d.dismiss();
                    addColor();
                }
            }
        });
        gridviewCustomColors.addItemDecoration(new MarginDecoration(this));
        gridviewCustomColors.setHasFixedSize(true);
        gridviewCustomColors.setAdapter(customColorAdapter);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        d.show();
        newColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                addColor();
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
        ((AlertDialog) d).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
        d.getWindow().setLayout((int) LogoliciousApp.convertDpToPixel(30 * 9, this), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    boolean changesFromKeyListener = false;
    boolean enableColorInput = false;

    private void addColor() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View pickerContainer = inflater.inflate(R.layout.add_custom_color, null);
        final EditText etColorCode = pickerContainer.findViewById(R.id.etColorCode);
        LogoliciousApp.setEditTextMaxLength(etColorCode, 7);
        final ColorPickerView colorPickerView = pickerContainer.findViewById(R.id.colorPickerView);
        AlphaSlideBar alphaSlideBar = pickerContainer.findViewById(R.id.alphaSlideBar);
        Button apply = pickerContainer.findViewById(R.id.apply);
        BrightnessSlideBar brightnessSlide = pickerContainer.findViewById(R.id.brightnessSlide);
        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                String strColor = envelope.getHexCode();
                Log.i(TAG, "xxx onColorSelected " + strColor);
                if (changesFromKeyListener) {
                    etColorCode.setBackgroundColor(envelope.getColor());
                    colorSelected = String.format("#%s", strColor);
                } else {
                    etColorCode.setText(String.format("#%s", strColor.substring(2)));
                }
                changesFromKeyListener = false;
            }
        });
        colorPickerView.attachAlphaSlider(alphaSlideBar);
        colorPickerView.attachBrightnessSlider(brightnessSlide);
        colorPickerView.setPreferenceName("MyColorPicker");
        colorPickerView.setActionMode(ActionMode.ALWAYS);
        colorPickerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            colorPickerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            colorPickerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });

        initColor = true;
        builder.setView(pickerContainer);
        final AlertDialog d = builder.create();

        apply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInvalidColorVal = false;
                try {
                    Color.parseColor(etColorCode.getText().toString());
                    colorPickerView.selectByHsv(Color.parseColor(etColorCode.getText().toString()));

                    if (!TextUtils.isEmpty(colorSelected) && !colorSelected.equals("#FFFFFFFF")) {
                        Cursor colorCursor = GlobalClass.sqLiteHelper.getCustomColors();
                        if (colorCursor.getCount() >= 11) {
                            showMessageOK(ActivityMainEditor.this, "You can only add up to 11 custom colors.", null);
                        } else {
                            sqLiteHelper.addCustomColor(colorSelected);
                            showColor();
                        }
                    }
                    isInvalidColorVal = false;
                } catch (IllegalArgumentException iae) {
                    isInvalidColorVal = true;
                    toast(ActivityMainEditor.this, "Invalid Color", Toast.LENGTH_SHORT);
                }

                if (!isInvalidColorVal) {
                    d.dismiss();
                }
            }
        });

        handlerColorPickerDetector.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "xxx color detector");
                if (etColorCode.getText().length() == 7) {
                    boolean isInvalidColorVal = false;
                    try {
                        Color.parseColor(etColorCode.getText().toString());
                        colorPickerView.selectByHsv(Color.parseColor(etColorCode.getText().toString()));
                        isInvalidColorVal = false;
                        changesFromKeyListener = true;
                    } catch (IllegalArgumentException iae) {
                        isInvalidColorVal = true;
                        toast(ActivityMainEditor.this, "Invalid Color", Toast.LENGTH_SHORT);
                    }
                }

                if (!d.isShowing()) {
                    handlerColorPickerDetector.removeCallbacks(null);
                } else {
                    handlerColorPickerDetector.postDelayed(this, 1000);
                }
            }
        }, 1000);

        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handlerColorPickerDetector.removeCallbacks(null);
            }
        });
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                handlerColorPickerDetector.removeCallbacks(null);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        d.show();
        d.getWindow().setLayout((int) LogoliciousApp.convertDpToPixel(30 * 9, this), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showProgress(String message) {
        if (null != mProgressDialog && mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = new ProgressDialog(ActivityMainEditor.this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void showMessage(String message) {
        if (null != mDialog && mDialog.isShowing() && !isFinishing()) {
            mDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainEditor.this);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        mDialog = builder.create();
        mDialog.show();
    }

    // Create a listener to track request state updates.
    private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            // (Optional) Provide a download progress bar.
            if (state.installStatus() == InstallStatus.DOWNLOADING) {
                long bytesDownloaded = state.bytesDownloaded();
                long totalBytesToDownload = state.totalBytesToDownload();
                // Implement progress bar.
            }
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate();
                }
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                if (appUpdateManager != null) {
                    appUpdateManager.unregisterListener(installStateUpdatedListener);
                }

            } else {
                Log.i("xxx", "InstallStateUpdatedListener: state: " + state.installStatus());
            }
        }
    };

    private void checkAppVersionUpdate() {
        if (appUpdateManager == null) {
            appUpdateManager = AppUpdateManagerFactory.create(this);
            appUpdateManager.registerListener(installStateUpdatedListener);
            appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        }

        // Checks that the platform will allow the specified type of update.
        //if (!baseActivity.store.getBoolean(IS_DONE_SHOWING_UPDATE_PROMPT)) {
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) &&
                    appUpdateInfo.installStatus() != InstallStatus.INSTALLED) {
                if (mDialog != null)
                    mDialog.dismiss();

                showSimpleDialog("Update Available",
                        "There is a new version of Logolicious available. It is recommended to update to the latest version.",
                        "Update now", "", v -> {
                            editor.putBoolean(IS_DONE_SHOWING_UPDATE_PROMPT, true);
                            editor.apply();
                            mDialog.dismiss();
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                        appUpdateInfo,
                                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                        AppUpdateType.IMMEDIATE,
                                        // The current activity making the update request.
                                        this,
                                        // Include a request code to later monitor this update request.
                                        UPDATE_APP_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }, null, null,
                        true,
                        false,
                        true,
                        false,
                        false);
            }

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
                    appUpdateInfo.installStatus() != InstallStatus.INSTALLED) {
                // If an in-app update is already running, resume the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            UPDATE_APP_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED &&
                    appUpdateInfo.installStatus() != InstallStatus.INSTALLED) {
                if (mDialog != null)
                    mDialog.dismiss();
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate();
                }
            }
        });
        //}
    }

    private void newVersionHasDownloaded() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            UPDATE_APP_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {

                                }
                            }
                        });

    }

    public void showSimpleDialog(String title, String message, String yesLabel, String mainButtonLabel,
                                 View.OnClickListener positiveButtonListener,
                                 View.OnClickListener negativeButtonListener,
                                 View.OnClickListener mainButtonListener,
                                 boolean withTitle, boolean enableNo, boolean enableYes, boolean enableMainButton,
                                 boolean cancellable) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_custom_view, null);
        dialogBuilder.setCancelable(cancellable);
        dialogBuilder.setView(dialogView);
        TextView titleTV = dialogView.findViewById(R.id.titleTv);
        titleTV.setVisibility(withTitle ? View.VISIBLE : View.GONE);
        TextView messageTV = dialogView.findViewById(R.id.messageTV);
        TextView cancelTV = dialogView.findViewById(R.id.cancelTV);
        TextView leaveTV = dialogView.findViewById(R.id.leaveTV);
        Button positiveBT = dialogView.findViewById(R.id.positiveBT);
        titleTV.setText(title);
        messageTV.setText(message);
        cancelTV.setVisibility(enableNo ? View.VISIBLE : View.GONE);
        leaveTV.setVisibility(enableYes ? View.VISIBLE : View.GONE);
        positiveBT.setVisibility(enableMainButton ? View.VISIBLE : View.GONE);
        cancelTV.setText("No");
        leaveTV.setText(yesLabel);
        mDialog = dialogBuilder.create();
        if (!isFinishing())
            mDialog.show();

        if (!TextUtils.isEmpty(mainButtonLabel)) {
            positiveBT.setText(mainButtonLabel);
        }

        cancelTV.setOnClickListener(negativeButtonListener);
        leaveTV.setOnClickListener(positiveButtonListener);
        positiveBT.setOnClickListener(mainButtonListener);
    }

    public void addText(final Activity act,
                        final ImageView backgroundImage,
                        final LayersContainerView layeredView,
                        boolean hasFontSelected,
                        boolean isExternalFont) {
        if (LogoliciousApp.isBaseImageNull(backgroundImage) && !isLive) {
            LogoliciousApp.showAlertOnUpLoadLogo(act, R.layout.upload_logo_alert, "Oops", "", true);
            System.out.println("Base ImageView has no background!");
        } else {
            final Dialog d = new Dialog(act);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.add_text);
            //d.setTitle("ADD TEXT");
            d.setCancelable(true);

            final TextView textViewFontPreview = (TextView) d.findViewById(R.id.textViewFontPreview);
            Button buttonAddText = (Button) d.findViewById(R.id.buttonAddText);
            Button buttonFonts = (Button) d.findViewById(R.id.buttonFonts);
            ImageView cancelSaving = (ImageView) d.findViewById(R.id.cancelSaving);
            final CheckBox checkboxShadow = (CheckBox) d.findViewById(R.id.checkboxShadow);

            final EditText textToAdd = (EditText) d.findViewById(R.id.textToAdd);
            Typeface type = null;

            if (hasFontSelected) {
                // add the default font
                if (isExternalFont) {
                    type = Typeface.createFromFile(selectedFontPath.replace("/mimetype//", ""));
                } else {
                    type = Typeface.createFromAsset(act.getAssets(), selectedFontPath);
                }
                Log.i("ActivityMainEditor", "db selectedFontPath " + selectedFontPath);
            } else {
                if (layeredView.targetSelected != null) {
                    currentText = layeredView.targetSelected.text == null ? "" : layeredView.targetSelected.text;
                    selectedFontPath = layeredView.targetSelected.font_style == null ? "new_fonts/Nobile-Regular.ttf" : layeredView.targetSelected.font_style;
                    if (selectedFontPath.contains("new_fonts/"))
                        type = Typeface.createFromAsset(act.getAssets(), selectedFontPath);
                    else
                        type = Typeface.createFromFile(selectedFontPath);
                    Log.i("ActivityMainEditor", "from layer selectedFontPath " + selectedFontPath);
                }
            }

            textToAdd.setText(currentText);
            textViewFontPreview.setTypeface(type);
            textViewFontPreview.setText(currentText);
            textViewFontPreview.invalidate();

            textToAdd.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    textViewFontPreview.setText(textToAdd.getText().toString().trim());
                }
            });

            buttonFonts.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    currentText = textToAdd.getText().toString().trim();
                    d.dismiss();
                    showFonts(act, backgroundImage, layeredView, false);
                }
            });

            buttonAddText.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ActivityMainEditor.seekbarTrans.setProgress(252);
                    String textEntered = textToAdd.getText().toString().trim();
                    if (textEntered.matches("") || textEntered == null || textEntered.equals("")) {
                        toast(act.getApplicationContext(), act.getString(R.string.AddTextAlertMessage), 1);
                        return;
                    }
                    layeredView.addItem(textEntered, v, colorSelectedText, selectedFontPath, checkboxShadow.isChecked());
                    currentText = textEntered;
                    d.dismiss();
                }
            });

            cancelSaving.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });

            d.show();
        }
    }

    public void updateText(final Activity act,
                           final ImageView backgroundImage,
                           final LayersContainerView layeredView,
                           boolean hasFontSelected) {
        if (LogoliciousApp.isBaseImageNull(backgroundImage) == true && !isLive) {
            LogoliciousApp.showAlertOnUpLoadLogo(act, R.layout.upload_logo_alert, "Oops", "", true);
            System.out.println("Base ImageView has no background!");
        } else {
            final Dialog d = new Dialog(act);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.add_text);
            d.setCancelable(true);

            TextView addTextTitle = (TextView) d.findViewById(R.id.AddTextTitle);
            if (null != addTextTitle) {
                addTextTitle.setText("UPDATE TEXT");
            }
            final TextView textViewFontPreview = (TextView) d.findViewById(R.id.textViewFontPreview);
            Button buttonAddText = (Button) d.findViewById(R.id.buttonAddText);
            Button buttonFonts = (Button) d.findViewById(R.id.buttonFonts);
            ImageView cancelSaving = (ImageView) d.findViewById(R.id.cancelSaving);
            final CheckBox checkboxShadow = (CheckBox) d.findViewById(R.id.checkboxShadow);
            checkboxShadow.setChecked(layeredView.targetSelected.shadow);

            final EditText textToAdd = (EditText) d.findViewById(R.id.textToAdd);
            Typeface type = null;

            if (selectedFontPath.contains(".Logolicious/.fonts")) { //Android 9.0 above. From user uploaded fonts
                type = Typeface.createFromFile(selectedFontPath);
            } else if (selectedFontPath.contains("new_fonts")) { //From Prefabs fonts
                type = Typeface.createFromAsset(act.getAssets(), selectedFontPath);
            } else {
                type = Typeface.createFromFile(selectedFontPath); //From user uploaded fonts
            }

            if (!hasFontSelected) {
                if (layeredView.targetSelected != null) {
                    currentText = layeredView.targetSelected.text == null ? "" : layeredView.targetSelected.text;
                    selectedFontPath = layeredView.targetSelected.font_style == null ? "new_fonts/Nobile-Regular.ttf" : layeredView.targetSelected.font_style;
                    Log.i("ActivityMainEditor", "from layer selectedFontPath " + selectedFontPath);
                }
            }

            textToAdd.setText(currentText);
            textViewFontPreview.setTypeface(type);
            textViewFontPreview.setText(currentText);
            textViewFontPreview.invalidate();

            textToAdd.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    textViewFontPreview.setText(textToAdd.getText().toString().trim());
                }
            });

            buttonFonts.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    currentText = textToAdd.getText().toString().trim();
                    d.dismiss();
                    showFonts(act, backgroundImage, layeredView, true);
                }
            });

            buttonAddText.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ActivityMainEditor.seekbarTrans.setProgress(252);
                    String textEntered = textToAdd.getText().toString().trim();
                    if (textEntered.matches("") || textEntered == null || textEntered.equals("")) {
                        toast(act.getApplicationContext(), act.getString(R.string.AddTextAlertMessage), 1);
                        return;
                    }
                    layeredView.targetSelected.color = colorSelectedText;
                    layeredView.targetSelected.font_style = selectedFontPath;
                    layeredView.targetSelected.shadow = checkboxShadow.isChecked();
                    layeredView.targetSelected.changeText(textToAdd.getText().toString().trim());
                    layeredView.invalidate();
                    currentText = textEntered;
                    d.dismiss();
                }
            });

            cancelSaving.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });

            d.show();
        }
    }

    public void showFonts(final Activity act, final ImageView backgroundImage, final LayersContainerView layeredView, final boolean forUpdate) {
        final Dialog dialog = new Dialog(act);
        dialog.setContentView(R.layout.fonts);
        dialog.setTitle("Select Font Style");

        final ListView fontsList = (ListView) dialog.findViewById(R.id.fontsList);
        Button buttonAddFont = (Button) dialog.findViewById(R.id.buttonAddFont);
        ImageButton fontTip = (ImageButton) dialog.findViewById(R.id.fontTip);

        fontTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mStackLevel++;

                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = act.getFragmentManager().beginTransaction();
                Fragment prev = act.getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = TipFontFeature.newInstance("FontTipFragment", mStackLevel, R.layout.tip_add_font);
                newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
                newFragment.show(ft, "dialog");
            }
        });

        adapterFonts = new AdapterFonts(act.getApplicationContext(), R.layout.fonts_item, arrayFonts);
        fontsList.setAdapter(adapterFonts);
        fontsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int itemPosition, long id) {
                LogoliciousApp.selectedFontPath = !arrayFonts.get(itemPosition).isExternal() ? arrayFonts.get(itemPosition).getFontType() : arrayFonts.get(itemPosition).getFontSDCardPath().replace("/mimetype//", "");
                dialog.dismiss();
                if (forUpdate) {
                    updateText(act, backgroundImage, layeredView, true);
                } else
                    addText(act, backgroundImage, layeredView, true, arrayFonts.get(itemPosition).isExternal());
            }
        });

        buttonAddFont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mDialog && mDialog.isShowing())
                    mDialog.dismiss();

                if (SDK_INT >= 30) {
                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();

                    if (!Environment.isExternalStorageManager()) {
                        showSimpleDialog("Permission",
                                "In order to locate fonts on your phone, LogoLicious needs to be allowed access to browse files." +
                                        "No worries, everything is local on your device only and no information will be shared online.",
                                "Agree",
                                "",
                                v -> {
                                    if (mDialog != null && mDialog.isShowing())
                                        mDialog.dismiss();

                                    Intent getPermission = new Intent();
                                    getPermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivityForResult(getPermission, REQUEST_MANAGE_ALL_FILES_PERM);
                                },
                                view1 -> {
                                    if (mDialog != null && mDialog.isShowing())
                                        mDialog.dismiss();
                                },
                                null,
                                true,
                                true,
                                true,
                                false,
                                false);
                    } else {
                        addFont();
                    }
                } else {
                    addFont();
                }
            }
        });

        final Button buttonSelectFont = (Button) dialog.findViewById(R.id.buttonSelectFont);
        buttonSelectFont.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (forUpdate)
                    updateText(act, backgroundImage, layeredView, true);
                else
                    addText(act, backgroundImage, layeredView, true, false);
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public class LoadFontsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // new fonts from Mics
            arrayFonts.clear();
            arrayFonts.add(new AdapterFontDetails("Display", "new_fonts/Display.ttf"));
            arrayFonts.add(new AdapterFontDetails("IdolWild", "new_fonts/Idolwild.ttf"));
            arrayFonts.add(new AdapterFontDetails("Manteka", "new_fonts/Manteka.ttf"));
            arrayFonts.add(new AdapterFontDetails("Nexa Bold", "new_fonts/Nexa Bold.otf"));
            arrayFonts.add(new AdapterFontDetails("Nexa Light", "new_fonts/Nexa Light.otf"));
            arrayFonts.add(new AdapterFontDetails("Oswald-Bold", "new_fonts/Oswald-Bold.ttf"));
            arrayFonts.add(new AdapterFontDetails("Oswald-ExtraLight", "new_fonts/Oswald-ExtraLight.ttf"));
            arrayFonts.add(new AdapterFontDetails("Oswald-Regular", "new_fonts/Oswald-Regular.ttf"));
            arrayFonts.add(new AdapterFontDetails("Variane Script", "new_fonts/Variane Script.ttf"));
            // 2016-10-22
            arrayFonts.add(new AdapterFontDetails("Ansley Display-Outline", "new_fonts/Ansley Display-Outline.ttf"));
            arrayFonts.add(new AdapterFontDetails("Ansley Display-Regular", "new_fonts/Ansley Display-Regular.ttf"));
            arrayFonts.add(new AdapterFontDetails("Cornerstone", "new_fonts/Cornerstone.ttf"));
            //			arrayFonts.add(new AdapterFontDetails("Hamurz Free Version", "new_fonts/Hamurz Free Version.ttf"));
            arrayFonts.add(new AdapterFontDetails("Lulo Clean", "new_fonts/Lulo Clean 1.ttf"));
            arrayFonts.add(new AdapterFontDetails("Mosk Semi-Bold 600", "new_fonts/Mosk Semi-Bold 600.ttf"));
            arrayFonts.add(new AdapterFontDetails("Mosk Thin 100", "new_fonts/Mosk Thin 100.ttf"));
            arrayFonts.add(new AdapterFontDetails("Mosk Ultra-Bold 900", "new_fonts/Mosk Ultra-Bold 900.ttf"));
            arrayFonts.add(new AdapterFontDetails("ShellaheraLiteScript", "new_fonts/ShellaheraLiteScript.otf"));
            //2016-12-08
            arrayFonts.add(new AdapterFontDetails("AgreloyInT3", "new_fonts/AgreloyInT3.ttf"));
            arrayFonts.add(new AdapterFontDetails("AgreloyS1", "new_fonts/AgreloyS1.ttf"));
            arrayFonts.add(new AdapterFontDetails("Berry Rotunda", "new_fonts/Berry Rotunda.ttf"));
            arrayFonts.add(new AdapterFontDetails("Bimbo_JVE", "new_fonts/Bimbo_JVE.ttf"));
            arrayFonts.add(new AdapterFontDetails("Dyer Arts and Crafts", "new_fonts/Dyer Arts and Crafts.ttf"));
            arrayFonts.add(new AdapterFontDetails("Essays1743", "new_fonts/Essays1743.ttf"));
            arrayFonts.add(new AdapterFontDetails("JWerd", "new_fonts/JWerd.ttf"));
            arrayFonts.add(new AdapterFontDetails("Lemon Tuesday", "new_fonts/Lemon Tuesday.otf"));
            arrayFonts.add(new AdapterFontDetails("LiberationSans-Bold", "new_fonts/LiberationSans-Bold.ttf"));
            arrayFonts.add(new AdapterFontDetails("LiberationSans-Regular", "new_fonts/LiberationSans-Regular.ttf"));
            arrayFonts.add(new AdapterFontDetails("Nobile-Bold", "new_fonts/Nobile-Bold.ttf"));
            arrayFonts.add(new AdapterFontDetails("Nobile-Italic", "new_fonts/Nobile-Italic.ttf"));
            arrayFonts.add(new AdapterFontDetails("Nobile-Regular", "new_fonts/Nobile-Regular.ttf"));
            arrayFonts.add(new AdapterFontDetails("OstrichSans-Heavy", "new_fonts/OstrichSans-Heavy.otf"));
            arrayFonts.add(new AdapterFontDetails("Pacifico", "new_fonts/Pacifico.ttf"));
            arrayFonts.add(new AdapterFontDetails("Portmanteau Regular", "new_fonts/Portmanteau Regular.ttf"));
            arrayFonts.add(new AdapterFontDetails("Ubuntu-Title", "new_fonts/Ubuntu-Title.ttf"));

            //Load User Fonts
            Cursor fontCursor = GlobalClass.sqLiteHelper.getFonts();
            while (fontCursor.moveToNext()) {
                String fontPath = fontCursor.getString(fontCursor.getColumnIndex("path"));
                Log.i("xxx", "xxx fontPath " + fontPath);
                if (!TextUtils.isEmpty(fontPath)) {
                    String[] pathSplits = fontPath.split("/");
                    if (pathSplits.length > 0 && pathSplits[pathSplits.length - 1].contains(".ttf")) {
                        arrayFonts.add(new AdapterFontDetails(pathSplits[pathSplits.length - 1].replace(".ttf", ""),
                                "",
                                fontPath,
                                true));
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (null != adapterFonts)
                adapterFonts.notifyDataSetChanged();
        }
    }

    public class SyncFontTask extends AsyncTask<String, String, String> {

        private Activity context;
        int count_new_fonts_added = 0;
        private File[] files;
        private StringBuilder sb;

        public SyncFontTask(Activity act) {
            this.context = act;
            this.count_new_fonts_added = 0;
        }

        public void traverse(File dir) {
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; ++i) {
                        File file = files[i];
                        if (file.isDirectory()) {
                            traverse(file);
                        } else {
                            if (file.getName().contains(".ttf")) {
                                Log.d(this.getClass().getSimpleName(), "xxx " + file.getAbsolutePath());
                                sb.append(file.getName()).append("\n");
                                try {
                                    File file_copy = new File(ActivityMainEditor.fontsDir + file.getName());
                                    if (!file_copy.exists()) {
                                        FileUtil.copyFile(new FileInputStream(file.getAbsoluteFile()), new FileOutputStream(ActivityMainEditor.fontsDir + file.getName()));
                                        GlobalClass.sqLiteHelper.insertFont(ActivityMainEditor.fontsDir + file.getName());
                                        count_new_fonts_added = count_new_fonts_added + 1;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            sb = new StringBuilder();
            files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            toast(act, "Files count " + files.length, Toast.LENGTH_SHORT);
            showMessageOK(context,
                    String.format(Locale.US, GlobalClass.getAppContext().getString(R.string.SuccessFontSync) +
                                    ".\n\nWhen adding new fonts:" +
                                    "\n1. Add new fonts on your sdcard download folder" +
                                    "\n2. Make sure it is .tff file extension so our scanner will recognize it." +
                                    "\n3. Done." +
                                    "\n\n\n" +
                                    "Scanned Directory: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                                    "\n\n%d fonts added.\n\n" +
                                    "Found following fonts:\n" + sb.toString(),
                            count_new_fonts_added),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (alert != null) {
                                alert.dismiss();
                            }
                        }
                    });
            new LoadFontsTask().execute();
        }
    }

    public interface TextLongClickForEdit {
        void onLongClick();
    }

}