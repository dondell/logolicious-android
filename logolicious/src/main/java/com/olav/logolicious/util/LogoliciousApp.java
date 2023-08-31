package com.olav.logolicious.util;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.olav.logolicious.screens.activities.ActivityMainEditor.act;
import static com.olav.logolicious.screens.activities.ActivityMainEditor.billingHelper;
import static com.olav.logolicious.screens.activities.ActivityMainEditor.skuDetailsList;
import static com.olav.logolicious.screens.activities.ActivityMainEditor.store;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.olav.logolicious.R;
import com.olav.logolicious.billingv3.Constants;
import com.olav.logolicious.customize.adapters.AdapterFontDetails;
import com.olav.logolicious.customize.adapters.AlbumDetails;
import com.olav.logolicious.customize.widgets.LayersContainerView;
import com.olav.logolicious.screens.activities.ActivityImageCropNew;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.screens.activities.Activity_Tip;
import com.olav.logolicious.util.SubscriptionUtil.AppStatitics;
import com.olav.logolicious.util.camera.ScreenDimensions;
import com.olav.logolicious.util.image.ImageHelper;

import org.acra.ACRA;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ASUS on 2/15/2017.
 */

public class LogoliciousApp {

    private static Toast toast;
    private static AlertDialog alert = null;

    public static boolean isLive = false;
    // Fonts
    public static String selectedFontPath = null;

    public static String selected_template_name = "";

    public static final int SAVE = 0;
    public static final int SHARE = 1;

    public final static String TYPE_HR_PNG = "HR_PNG";
    public final static String TYPE_JPG_HQ = "JPG_HQ";
    public final static String TYPE_JPG_L = "JPG_L";
    public static String SAVING_TYPE = TYPE_HR_PNG;

    public ArrayList<AdapterFontDetails> arrayFonts = new ArrayList<>();

    private static String[] prefab_logos = {
            "logolicious_black_txt",
            "logolicious_white_txt",
            "prefab_allrightsreserved_black",
            "prefab_allrightsreserved_white",
            "prefab_logo_copyright_black",
            "prefab_logo_copyright_white",
            "prefab_logo_registered_black",
            "prefab_logo_registered_white",
            "prefab_logo_tm_black",
            "prefab_logo_tm_white"
    };

    public static void showAlertOnUpLoadLogo(final Context context, int layout, String strTitle, String strMsg,
                                             final boolean bCloseActivityOnOk) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);

        LayoutInflater li = LayoutInflater.from(context);

        LinearLayout someLayout = (LinearLayout) li.inflate(layout, null);
        dlg.setView(someLayout);
        dlg.setCancelable(true);

        dlg.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (bCloseActivityOnOk) {
                    dialog.dismiss();
                }
                return;
            }
        });

        dlg.show();
    }

    public static void showYesNoAlert(Context context, String strTitle, String strMsg, String strYesText, String strNoText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle(strTitle);
        dlg.setMessage(strMsg);

        dlg.setPositiveButton(strYesText, listener);
        dlg.setNegativeButton(strNoText, listener);

        dlg.show();
    }

    public static void toast(Context context, String msg, int length) {
        if (null != toast)
            toast.cancel();
        toast = Toast.makeText(context, msg, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * @param view The view to be check
     * @return This method will check if the View has no bitmap or drawable
     */
    public static boolean isBaseImageNull(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        // check 1
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }
        // check 2
        Drawable background = view.getBackground();

        if (hasImage || background != null) {
            return false;
        } else {
            return true;
        }
    }

    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 2;
    public static final int REQUEST_ACCESS_CAMERA = 3;
    public static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 4;

    public static boolean hasPermissionNeeded(Activity act) {
        //int permissionLocation = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionTakePicture = ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA);
        int storagePermission1 = ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<String>();
//        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
        if (storagePermission1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            int storagePermission2 = ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (storagePermission2 != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (permissionTakePicture != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(act, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return true;
        }
        return false;
    }

    public static boolean isStorageAllowed(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else
            return true;
    }

    public static boolean verifyStoragePermissionsWithoutPrompt(Context context) {
        // Check if we have write permission
        boolean bRet = true;
        int permission1 = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission1 != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {
            bRet = false;
        }
        return bRet;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity, String[] PERMISSIONS_STORAGE, int REQUEST_CODE) {
        // Check if we have write permission
        boolean bRet = true;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_CODE);
            bRet = false;
        }
        return bRet;
    }

    private static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static boolean verifyCameraPermissions(Activity activity, String[] PERMISSIONS_CAMERA, int REQUEST_CODE) {
        // Check if we have write permission
        boolean bRet = true;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA, REQUEST_CODE);
            bRet = false;
        }
        return bRet;
    }

    public static boolean verifyCameraPermission(Activity activity, int REQUEST_CODE) {
        // Assume thisActivity is the current activity
        boolean bRet = true;
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
            bRet = false;
        }
        return bRet;
    }

    public static void setTextViewVisibility(Activity act, int resId, boolean isVisible) {
        TextView v = (TextView) act.findViewById(resId);
        v.setVisibility(isVisible == true ? View.VISIBLE : View.INVISIBLE);
    }

    public static void setViewVisibility(Activity context, int resId, boolean isVisible) {
        View v = (View) context.findViewById(resId);
        v.setVisibility(isVisible == true ? View.VISIBLE : View.INVISIBLE);
    }

    public static void setViewVisibilityOrGone(View parent, int resId, boolean isVisible) {
        View v = (View) parent.findViewById(resId);
        v.setVisibility(isVisible == true ? View.VISIBLE : View.GONE);
    }

    public static void setOnClickListener(Activity context, int nID) {
        View view = context.findViewById(nID);
        if (null != view)
            view.setOnClickListener((OnClickListener) context);
    }

    public static void setOnTouchListener(Activity context, int nID, final GestureDetector gestureDetector) {
        View view = context.findViewById(nID);
        if (null != view)
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    ActivityImageCropNew.doubleTapView = v.getId();
                    return gestureDetector.onTouchEvent(e);
                }
            });
    }

    public static void setVisible(Activity context, int nID, int nVisibility) {
        View view = context.findViewById(nID);
        if (null != view)
            view.setVisibility(nVisibility);
    }

    public static void setButtonText(Activity context, int nID, String strText, boolean bVisible) {
        Button bv = (Button) context.findViewById(nID);
        if (null == bv)
            return;

        if (bVisible)
            bv.setVisibility(View.VISIBLE);
        else
            bv.setVisibility(View.INVISIBLE);

        if (null != strText)
            bv.setText(strText);
    }

//	public static Bitmap getbitmap() {
//		return b;
//	}

//	public static void setbitmap(Bitmap bm) {
//		b = bm;
//	}

    public static void setImageViewTint(Activity context, int resId, int color) {
        ImageView imb = (ImageView) context.findViewById(resId);
//		imb.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        imb.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static void setImageViewImageAndRotate(Activity context, int resId, int drawable) {
        ImageView iv = (ImageView) context.findViewById(resId);
        iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), drawable));
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static boolean strIsNullOrEmpty(String str) {
        if (null == str || str.length() <= 0 || str.matches(""))
            return true;
        else
            return false;
    }

    public static void showMessageOK(Activity context, String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder dialogAlert = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!context.isDestroyed() && null != alert && alert.isShowing()) {
                alert.cancel();
            }
        } else {
            if (null != alert && alert.isShowing()) {
                alert.cancel();
            }
        }

        dialogAlert = new AlertDialog.Builder(context);
        dialogAlert.setMessage(message)
                .setPositiveButton("OK", okListener);

        alert = dialogAlert.create();
        alert.show();
    }

    public static void showYesNoAlertWithoutTitle(Context context, String strMsg, String strYesText, String strNoText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setMessage(strMsg);

        dlg.setPositiveButton(strYesText, listener);
        dlg.setNegativeButton(strNoText, listener);

        dlg.show();
    }

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static void appTrialMonitoring(Activity context) {
        SharedPreferences preferences = context.getPreferences(context.MODE_PRIVATE);
        String installDate = preferences.getString("InstalledDate", null);
        if (installDate == null) {
            // First run, so save the current date
            SharedPreferences.Editor editor = preferences.edit();
            Date now = new Date();
            String dateString = formatter.format(now);
            editor.putString("InstalledDate", dateString);
            // Commit the edits!
            editor.commit();
        } else {
            // This is not the 1st run, check install date
            Date before = null;
            try {
                before = (Date) formatter.parse(installDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date now = new Date();
            long diff = now.getTime() - before.getTime();
            long days = diff / ONE_DAY;
            toast(context, "Trial expires at 30 days. You have " + (30 - days) + " remaining days.", 1);
            if (days > 30) { // More than 30 days?
                // Expired !!!
                showTrialExpiryMessage(context);
            }
        }

    }

    private static void showTrialExpiryMessage(Context context) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.trial_option);
        d.setTitle("30 days Trial has expired");
        d.setCancelable(false);

        Button goToGooglePlay = (Button) d.findViewById(R.id.goToGooglePlay);
        Button closeApp = (Button) d.findViewById(R.id.closeApp);

        goToGooglePlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

        closeApp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
                System.exit(0);
            }
        });

        d.show();
    }

    public static boolean isKindleFire() {
        final String AMAZON = "Amazon";
//        final String KINDLE_FIRE = "Kindle Fire";

        return (Build.MANUFACTURER.equals(AMAZON)); //&& Build.MODEL.contains(KINDLE_FIRE) ) || Build.MODEL.startsWith("KF")
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            GlobalClass.mMemoryCache.put(key, bitmap);
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return GlobalClass.mMemoryCache.get(key);
    }

    @SuppressLint("NewApi")
    public static ScreenDimensions getScreenDimensions(Activity context, int orientation, double ar) {

        ScreenDimensions screen = null;

        Display display = context.getWindowManager().getDefaultDisplay();
        if (null != display) {
            screen = new ScreenDimensions();

            int mSurfaceHeight = 0;
            int mSurfaceWidth = 0;
            int mdisplayWidth = 0;
            int mdisplayHeight = 0;

            Point size = new Point();
            display.getSize(size);

            mdisplayWidth = size.x;
            mdisplayHeight = size.y;

            if (Configuration.ORIENTATION_LANDSCAPE == orientation) {

                if (mdisplayWidth < mdisplayHeight) {
                    int temp = mdisplayHeight;
                    mdisplayHeight = mdisplayWidth;
                    mdisplayWidth = temp;
                }
                /*
                 * Surface view should be like this
                 * 		___________________________
                 * 	    |		    16			   |
                 * 	    |						   |
                 *      |						   | 9
                 *      |						   |
                 *      |						   |
                 *      |__________________________|
                 */

                if (ar <= ((double) mdisplayWidth) / ((double) mdisplayHeight)) {
                    mSurfaceWidth = mdisplayWidth;
                    mSurfaceHeight = (int) ((double) mSurfaceWidth / ar);
                } else {
                    mSurfaceHeight = mdisplayHeight;
                    mSurfaceWidth = (int) ((double) mSurfaceHeight * ar);
                }

                screen.setDisplayHeight(mSurfaceHeight);
                screen.setDisplayWidth(mSurfaceWidth);
                screen.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
            } else {

                if (mdisplayWidth > mdisplayHeight) {
                    int temp = mdisplayHeight;
                    mdisplayHeight = mdisplayWidth;
                    mdisplayWidth = temp;
                }

                /*
                 * Surface view should be like this
                 * 		_______________
                 * 	    |		9  	   |
                 * 	    |			   |
                 *      |			   |
                 *      |			   |
                 *      |			   |
                 *      |			   | 16
                 *      |			   |
                 *      |			   |
                 *      |			   |
                 *      |			   |
                 *      |______________|
                 */

                if (ar >= ((double) mdisplayHeight / (double) mdisplayWidth)) {
                    mSurfaceWidth = mdisplayWidth;
                    mSurfaceHeight = (int) ((double) mSurfaceWidth * ar);
                } else {
                    mSurfaceHeight = mdisplayHeight;
                    mSurfaceWidth = (int) ((double) mSurfaceHeight / ar);
                }

                screen.setDisplayHeight(mSurfaceHeight);
                screen.setDisplayWidth(mSurfaceWidth);
                screen.setOrientation(Configuration.ORIENTATION_PORTRAIT);
            }
        }

        screen.aspectratio = ar;

        return screen;
    }

    public static void showSavingOptions(Activity context,
                                         SharedPreferences preferences,
                                         final SharedPreferences.Editor editor,
                                         final Handler mHandler,
                                         int DEVICE_WIDTH,
                                         final int nextAction) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_saving_setting);
        if (nextAction == SAVE)
            dialog.setTitle("Saving Preference");
        else if (nextAction == SHARE)
            dialog.setTitle("Sharing Preference");
        else
            dialog.setTitle("Image Preference");

        final CheckBox type1 = (CheckBox) dialog.findViewById(R.id.type1);
        final CheckBox type2 = (CheckBox) dialog.findViewById(R.id.type2);
        final CheckBox type3 = (CheckBox) dialog.findViewById(R.id.type3);
        final CheckBox promptalways = (CheckBox) dialog.findViewById(R.id.promptalways);

        final EditText fileName = (EditText) dialog.findViewById(R.id.fileName);

        final Button save = (Button) dialog.findViewById(R.id.save);

        String type = preferences.getString("SavingType", "JPG_HQ");
        SAVING_TYPE = type;
        if (type.matches(TYPE_HR_PNG)) {
            type1.setChecked(true);
            type2.setChecked(false);
            type3.setChecked(false);
        } else if (type.matches(TYPE_JPG_HQ)) {
            type1.setChecked(false);
            type2.setChecked(true);
            type3.setChecked(false);
        } else if (type.matches(TYPE_JPG_L)) {
            type1.setChecked(false);
            type2.setChecked(false);
            type3.setChecked(true);
        }

        type1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                type1.setChecked(true);
                type2.setChecked(false);
                type3.setChecked(false);
                SAVING_TYPE = TYPE_HR_PNG;
                editor.putString("SavingType", TYPE_HR_PNG);
                editor.commit();
            }
        });

        type2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                type1.setChecked(false);
                type2.setChecked(true);
                type3.setChecked(false);
                SAVING_TYPE = TYPE_JPG_HQ;
                editor.putString("SavingType", TYPE_JPG_HQ);
                editor.commit();
            }
        });

        type3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                type1.setChecked(false);
                type2.setChecked(false);
                type3.setChecked(true);
                SAVING_TYPE = TYPE_JPG_L;
                editor.putString("SavingType", TYPE_JPG_L);
                editor.commit();
            }
        });

        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (SAVE == nextAction) {
                    Message msg = mHandler.obtainMessage(ActivityMainEditor.MESSAGE_SAVING_IMAGE);
                    mHandler.sendMessage(msg);
                } else {
                    // Send a saving progress notification
                    Message msg = mHandler.obtainMessage(ActivityMainEditor.MESSAGE_SHARING_IMAGE);
                    mHandler.sendMessage(msg);
                }
            }
        });

        promptalways.setChecked(preferences.getBoolean("dontAskMeAgain", false));
        //set initial data
        if (preferences.getBoolean("dontAskMeAgain", false)) {
            editor.putBoolean("dontAskMeAgain", true);
            editor.commit();
        } else {
            editor.putBoolean("dontAskMeAgain", false);
            editor.commit();
        }

        promptalways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton cb, boolean flag) {
                if (flag == true) {
                    editor.putBoolean("dontAskMeAgain", true);
                    editor.commit();
                } else {
                    editor.putBoolean("dontAskMeAgain", false);
                    editor.commit();
                }
            }
        });

        String fName = preferences.getString("ImageFilename", null);
        if (!LogoliciousApp.strIsNullOrEmpty(fName)) {
            fileName.setText(fName);
        } else
            fileName.setText("");

        fileName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                editor.putString("ImageFilename", fileName.getText().toString().trim());
                editor.commit();
            }
        });

        dialog.getWindow().setLayout((int) (DEVICE_WIDTH * .9), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void showTip(Activity context) {
        int hint = GlobalClass.sqLiteHelper.isShowHint();
        if (hint == 1 || hint == -1) {
            Intent intentTip = new Intent(context.getApplicationContext(), Activity_Tip.class);
            context.startActivity(intentTip);
        }
    }

    public static void showRateDialog(final Activity context, final int id, final int saveCount) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.rate);
        d.setTitle("Rate Us");
        d.setCancelable(false);
        RatingBar ratingBar = (RatingBar) d.findViewById(R.id.ratingBar1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                AppStatitics.sharedPreferenceSet(act, "RATED", 1);
                // check if the rating is >4
                if (rating >= 4) {
                    d.dismiss();
                    GlobalClass.sqLiteHelper.updateSaveCount(id, saveCount, 1);

                    // link them to google play to rate
                    Uri uri = Uri.parse("market://details?id=" + context.getApplicationContext().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market back stack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    try {
                        context.startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getApplicationContext().getPackageName())));
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "Thanks for your feedback", Toast.LENGTH_LONG).show();
                    d.dismiss();
                    GlobalClass.sqLiteHelper.updateSaveCount(id, saveCount, 1);
                }
            }
        });

        d.show();
    }

    public static void logoOption(final Activity context, final String logoPath) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.logo_option);
        d.setTitle("My Logos");
        d.setCancelable(true);

        Button deleteLogo = (Button) d.findViewById(R.id.deleteLogo);
        Button closeLogoOption = (Button) d.findViewById(R.id.closeLogoOption);

        deleteLogo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogoliciousApp.toast(context.getApplicationContext(), FileUtil.deleteAFile(logoPath) == true ? "Logo Deleted!" : "Logo Error Deletion!", 1);
                ActivityMainEditor.removePopupFunnySelection();
                d.dismiss();
            }
        });

        closeLogoOption.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    public static void templateOption(final Activity context, final LayersContainerView layeredLogos, final ImageView backgroundImage, final String template_name, final Handler mHandler) {
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.template_option);
        //d.setTitle("Template - " + template_name);
        d.setCancelable(true);

        //Button renameTemplate = (Button) d.findViewById(R.id.renameTemplate);
        TextView title_template = (TextView) d.findViewById(R.id.title_template);
        title_template.setText("Template - " + template_name);

        Button deleteTemplate = (Button) d.findViewById(R.id.deleteTemplate);
        Button useAsTemplate = (Button) d.findViewById(R.id.useAsTemplate);
        ImageView cancelSaving = (ImageView) d.findViewById(R.id.cancelSaving);
        cancelSaving.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

//        renameTemplate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Dialog dRenaming = new Dialog(context);
////                LinearLayout ll=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.template_rename, null);
////                dRenaming.setContentView(ll);
////                dRenaming.setCancelable(true);
////                dRenaming.show();
//
//                d.dismiss();
//
//                AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                final String tPrefix = template_name.substring(template_name.indexOf("_"), template_name.length());
//                alert.setTitle(template_name.replace(tPrefix, ""));
//                alert.setMessage("Change To:");
//
//                // Set an EditText view to get user input
//                final EditText etInput = new EditText(context);
//                alert.setView(etInput);
//                etInput.setText(template_name.replace(tPrefix, ""));
//
//                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        if(LogoliciousApp.strIsNullOrEmpty(etInput.getText().toString())) {
//                            LogoliciousApp.toast(context, "Please enter template name", Toast.LENGTH_SHORT);
//                            return;
//                        }
//
//                        LogoliciousApp.toast(context, "zzz " + etInput.getText() + tPrefix, Toast.LENGTH_SHORT);
//                        GlobalClass.sqLiteHelper.updateTemplateName(template_name, etInput.getText() + tPrefix);
//                    }
//                });
//
//                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                    }
//                });
//
//                alert.show();
//            }
//        });

        useAsTemplate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (null == backgroundImage.getDrawable() && !LogoliciousApp.isLive) {
                    Message msg = mHandler.obtainMessage(ActivityMainEditor.MESSAGE_APPLY_TEMPLATE_ERROR);
                    mHandler.sendMessage(msg);
                    ActivityMainEditor.removePopupFunnySelection();
                    d.dismiss();
                    return;
                }

                // remove first the existing items
                layeredLogos.removeAllItems();
                layeredLogos.invalidate();

//                layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(template_name), backgroundImage, template_name, false);

                if (GlobalClass.getAR().replace("_", "").equals(LogoliciousApp.getARName(template_name))) {
                    layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(template_name), backgroundImage, false);
                    ActivityMainEditor.removePopupFunnySelection();
                } else {
                    LogoliciousApp.showYesNoAlert(context, "Template size unmatched", context.getString(R.string.TemplateApplyErrorMessage), "YES", "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(template_name), backgroundImage, true);
                                    ActivityMainEditor.removePopupFunnySelection();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    });
                }
                d.dismiss();
            }
        });

        deleteTemplate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalClass.sqLiteHelper.deleteTemplate(template_name);
                GlobalClass.sqLiteHelper.deleteTemplatePreview(template_name);

                ActivityMainEditor.removePopupFunnySelection();
                d.dismiss();
            }
        });

        d.show();
    }

    public static void callCropper(Activity context, RelativeLayout listRight, ImageView backgroundImage, int DEVICE_WIDTH) {
        // calling to crop section
        Intent intent = new Intent(context.getApplicationContext(), ActivityImageCropNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("D_W", DEVICE_WIDTH - listRight.getWidth());
        intent.putExtra("D_H", backgroundImage.getHeight());
        context.startActivity(intent);
    }

    public static void initPrefabLogos(Activity context) {
        // move the prefab logos into the sdcard logolicious folder
        // Check if we have write permissioninitPrefabLogos
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            for (String logos : prefab_logos) {
                File fLogo = new File(ActivityMainEditor.logoDir, logos);
                // delete first
                fLogo.delete();
                // copy to .logos folder
                FileUtil.copyFileTo(context, "drawable/" + logos, ActivityMainEditor.logoDir, "drawable");
            }
        }

    }

    private static final long MEGABYTE = 1024L * 1024L;

    /**
     * returns the bytesize of the give bitmap
     */
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static long bytesToMeg(long bytes) {
        Log.i("xxx bytesToMeg", "xxx bytes / MEGABYTE = " + (bytes / MEGABYTE));
        return bytes / MEGABYTE;
    }

    public static int fileSizeInMbInt(String strFile) {
        if (null == strFile || strFile.length() <= 0)
            return 0;

        File f = new File(strFile);
        // Get length of file in bytes
        long fileSizeInBytes = f.length();
        Log.d("LogoliciousApp", "fileSizeInBytes = " + fileSizeInBytes);
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        float fileSizeInKB = (float) fileSizeInBytes / (float) 1024;
        Log.d("LogoliciousApp", "fileSizeInKB = " + fileSizeInKB);
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;
        Log.d("LogoliciousApp", "fileSizeInMB = " + String.format("%.0f", fileSizeInMB));

        if (fileSizeInMB < 1.0)
            return Integer.parseInt(String.format("%.0f", fileSizeInMB));

        return Integer.parseInt(String.format("%.0f", fileSizeInMB));
    }

    public static String fileSizeInMb(String strFile) {
        if (null == strFile || strFile.length() <= 0)
            return "unknown";

        File f = new File(strFile);
        // Get length of file in bytes
        long fileSizeInBytes = f.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        float fileSizeInKB = (float) fileSizeInBytes / (float) 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;

        if (fileSizeInMB < 1.0)
            return String.format("%.2f", fileSizeInKB) + "kb";

        return String.format("%.2f", fileSizeInMB) + "mb";
    }

    public static long fileSizeInBytes(String strFile) {
        if (null == strFile || strFile.length() <= 0)
            return 0;

        File f = new File(strFile);
        // Get length of file in bytes
        long fileSizeInBytes = f.length();

        return fileSizeInBytes;
    }

    /**
     * @param data
     * @return Returns the mb size of bitmap. Note this functions is memory intensive because it will load bitmap to memory.
     * Use the function fileSizeInMb();
     */
    public static int sizeOfBitmap(Bitmap data) {
        int nRet = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            nRet = data.getRowBytes() * data.getHeight();
        } else {
            nRet = data.getByteCount();
        }
        /*
        } else if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
            nRet = data.getByteCount();
            Log.i("xxx sizeOfBitmap","xxx b bitmapSize = " + nRet);
        }
         */
        recycleBitmap(data);
        return (int) bytesToMeg(nRet);
    }

    public static String getARName(String templateName) {
        int searchLastColonPos = templateName.lastIndexOf("_");
        if (searchLastColonPos <= 0) {
            return null;
        }
        String ar = templateName.substring(searchLastColonPos + 1, templateName.length());
        return ar;
    }

    public static int getAvailableMemMB(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;

        //Percentage can be calculated for API 16+
        //double percentAvail = mi.availMem / (double)mi.totalMem;
        ACRA.getErrorReporter().putCustomData(GlobalClass.APP_AVAILABLE_MEM_SIZE, "" + (int) availableMegs + "mb");
        Log.i("xxx", "xxx getAvailableMemMB = " + (int) availableMegs);
        return (int) availableMegs;
    }

    public static void malloc(Context context, int sizeToAlloc) {
        if (getAvailableMemMB(context) > sizeToAlloc) //allowance 50MB memory for other apps when LL is minimize.
            GlobalClass.malloc(sizeToAlloc);
    }

    public static void startActivity(Activity act, Class<?> cls) {
        Intent intent = new Intent(act, cls);
        act.startActivity(intent);
    }

    public static void startActivity(Activity act, Class<?> cls, String strKey, String strValue) {
        Intent intent = new Intent(act, cls);
        intent.putExtra(strKey, strValue);
        act.startActivity(intent);
    }

    public static void startActivity(Activity act, Class<?> cls, String strKey, int nValue) {
        Intent intent = new Intent(act, cls);
        intent.putExtra(strKey, nValue);
        act.startActivity(intent);
    }

    public static void startActivity(Activity act, Class<?> cls, String strKey1, int nValue1, String strKey2, int nValue2) {
        Intent intent = new Intent(act, cls);
        intent.putExtra(strKey1, nValue1);
        intent.putExtra(strKey2, nValue2);
        act.startActivity(intent);
    }

    public static void startActivity(Activity act, Class<?> cls, String strKey1, String sValue1, String strKey2, ArrayList<AlbumDetails> albumDetailse) {
        Intent intent = new Intent(act, cls);
        intent.putExtra(strKey1, sValue1);
        intent.putExtra(strKey2, albumDetailse);
        act.startActivity(intent);
    }

    public static String reverseString(String str) {
        // The big works ;)
        StringBuffer buffer = new StringBuffer(str);
        buffer.reverse();
        return buffer.toString();
    }

    public static InputFilter filterSpecialChars() {
        //additional characters to block "~#^$*";
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                char[] chars = {'\'', '"', '%', '&', '!', '|', '*', '"', ';'};
                for (int i = start; i < end; i++) {
                    if (new String(chars).contains(String.valueOf(source.charAt(i)))) {
                        return "";
                    }
                }
                return null;
            }
        };
        return filter;
    }

    /**
     * @return calculate real scale factor of the matrix.
     */
    public float getScaleMatrixFactor(Matrix m) {
        float[] values = new float[9];
        m.getValues(values);
        float scalex = values[Matrix.MSCALE_X];
        float skewy = values[Matrix.MSKEW_Y];
        float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
        return rScale;
    }

    public static boolean isSubBtnClick = false;
    public static Dialog subsDialog;

    private static boolean okToClose = false;

    public static void showSubscription(final Activity act, final int count) {

        subsDialog = new Dialog(act);
        subsDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        subsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        subsDialog.setContentView(R.layout.subscription);

        if (null == subsDialog) {
            return;
        }

        final TextView tvTimer = (TextView) subsDialog.findViewById(R.id.tvTimer);
        final ImageView button_maybe_later = (ImageView) subsDialog.findViewById(R.id.button_maybe_later);
        final ImageView subscribe = (ImageView) subsDialog.findViewById(R.id.buttonSubscribe);
        final Button promoCode = (Button) subsDialog.findViewById(R.id.promoCode);
        //add shadow to buy button
        final Bitmap src = BitmapFactory.decodeResource(act.getResources(), R.drawable.buy_button);
        final Bitmap shadow = ImageHelper.addShadow(src, src.getHeight(), src.getWidth(), act.getResources().getColor(R.color.style_grey700), 3, 1, 3);
        subscribe.setImageBitmap(shadow);

        final int deviceWidth = act.getWindowManager().getDefaultDisplay().getWidth();
        subsDialog.getWindow().setLayout((int) (deviceWidth * .9), ViewGroup.LayoutParams.WRAP_CONTENT);
        subsDialog.setCancelable(false);

        int milleSecDelay = (int) (count * 1000 * 0.33);
        Log.i("xxx", "xxx milleSecDelay " + milleSecDelay);

        button_maybe_later.setEnabled(false);
        button_maybe_later.postDelayed(new Runnable() {
            @Override
            public void run() {
                button_maybe_later.setEnabled(true);
            }
        }, milleSecDelay);

        //accumulated timer
        new CountDownTimer(milleSecDelay, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.format(Locale.US, "%d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                okToClose = true;
                tvTimer.setText("");
            }
        }.start();

        button_maybe_later.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (okToClose && null != subsDialog) {
                    subsDialog.cancel();
                    subsDialog = null;

                    isSubBtnClick = false;
                    GlobalClass.subscriptionOkToShow = false;
                    Log.i("xxx", "xxx close subscription");
                }

                if (GlobalClass.pendingShowMemAlert) {
                    ActivityMainEditor.showMemError();
                }
            }
        });

        subscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isSubBtnClick = true;
                //ActivityMainEditor.bp.subscribe(act, SubscriptionUtil.SUBSCRIPTION_SKU);

                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                if (skuDetailsList.size() > 0) {
                    BillingFlowParams billingFlowParams =
                            BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetailsList.get(0))
                                    .build();
                    int responseCode = billingHelper.billingClient.launchBillingFlow(act, billingFlowParams).getResponseCode();
                    store.setInt(Constants.KEY_PURCHASE_CODE, responseCode);
                } else {
                    Toast.makeText(GlobalClass.getAppContext(), "Unable to load subscription.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        promoCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPromoCode(act);
            }
        });

        subsDialog.show();
        Log.i("xxx", "xxx showing subscription");
    }

    public static void showSubscriptionFirstPopup(final ActivityMainEditor act, final int count) {

        subsDialog = new Dialog(act);
        subsDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        subsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        subsDialog.setContentView(R.layout.subscription);

        if (null == subsDialog) {
            return;
        }

        final ImageView button_maybe_later = (ImageView) subsDialog.findViewById(R.id.button_maybe_later);
        final ImageView subscribe = (ImageView) subsDialog.findViewById(R.id.buttonSubscribe);
        final Button promoCode = (Button) subsDialog.findViewById(R.id.promoCode);
        //add shadow to buy button
        final Bitmap src = BitmapFactory.decodeResource(act.getResources(), R.drawable.buy_button);
        final Bitmap shadow = ImageHelper.addShadow(src, src.getHeight(), src.getWidth(), act.getResources().getColor(R.color.style_grey700), 3, 1, 3);
        subscribe.setImageBitmap(shadow);

        final int deviceWidth = act.getWindowManager().getDefaultDisplay().getWidth();
        subsDialog.getWindow().setLayout((int) (deviceWidth * .9), ViewGroup.LayoutParams.WRAP_CONTENT);
        subsDialog.setCancelable(false);

        button_maybe_later.setEnabled(false);
        button_maybe_later.postDelayed(new Runnable() {
            @Override
            public void run() {
                button_maybe_later.setEnabled(true);
            }
        }, 2000);

        button_maybe_later.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != subsDialog) {
                    subsDialog.cancel();
                    subsDialog = null;
                }
                isSubBtnClick = false;
                GlobalClass.subscriptionOkToShow = false;
                Log.i("xxx", "xxx close subscription");

                if (GlobalClass.pendingShowMemAlert) {
                    ActivityMainEditor.showMemError();
                }
            }
        });

        subscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isSubBtnClick = true;
                //ActivityMainEditor.bp.subscribe(act, SubscriptionUtil.SUBSCRIPTION_SKU);
                if (skuDetailsList.size() > 0) {
                    BillingFlowParams billingFlowParams = null;
                    if (AppStatitics.sharedPreferenceGet(act, "oldSubscriber", 0) == 1) {
                        for (SkuDetails sd : skuDetailsList) {
                            if (sd.getSku().equals(Constants.COM_OLAV_LOGOLICIOUS_SUBSCRIPTION)) {
                                billingFlowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(sd)
                                        .build();
                            }
                        }
                    } else {
                        for (SkuDetails sd : skuDetailsList) {
                            if (sd.getSku().equals(Constants.ADDYOURLOGOAPP_2022)) {
                                billingFlowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(sd)
                                        .build();
                            }
                        }
                    }
                    int responseCode = billingHelper.billingClient.launchBillingFlow(act, billingFlowParams).getResponseCode();
                    store.setInt(Constants.KEY_PURCHASE_CODE, responseCode);
                }
            }
        });

        promoCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPromoCode(act);
            }
        });

        subsDialog.show();
        Log.i("xxx", "xxx showing subscription");
    }

    public static void enterPromoCode(final Activity act) {
        Dialog d = new Dialog(act);
        d.setTitle("Promo Code");
        d.setContentView(R.layout.enter_promocode);
        d.setCancelable(true);
        final EditText editTextPromoCode = (EditText) d.findViewById(R.id.editTextPromoCode);
        final Button savePromo = (Button) d.findViewById(R.id.savePromo);

        savePromo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String rCode = "";
                    if (null != editTextPromoCode) {
                        if (LogoliciousApp.strIsNullOrEmpty(editTextPromoCode.getText().toString())) {
                            LogoliciousApp.showMessageOK(act, "Please enter promo code.", null);
                            return;
                        }
                        rCode = editTextPromoCode.getText().toString();
                    }
                    final Uri redeemUri = Uri.parse("https://play.google.com/redeem?code=" + rCode);
                    final Intent redeemIntent = new Intent(Intent.ACTION_VIEW, redeemUri);
                    ActivityMainEditor.act.startActivityForResult(redeemIntent, ActivityMainEditor.PROMO_CODE);
                } catch (ActivityNotFoundException e) {
                    LogoliciousApp.showMessageOK(act, "Play Store app is not installed on your device.", null);
                }

            }
        });
        d.show();
    }

    public static SharedPreferences sharedPreferenceGet(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean sharedPreferenceExist(Context context, String strKey) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return false;
        return spf.contains(strKey);
    }

    public static int sharedPreferenceGet(Context context, String strKey, int nDefaultValue) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return nDefaultValue;

        int nRet = spf.getInt(strKey, nDefaultValue);
        return nRet;
    }

    public static String sharedPreferenceGet(Context context, String strKey, String strDefaultValue) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return strDefaultValue;

        String strRet = spf.getString(strKey, strDefaultValue);
        return strRet;
    }

    public static boolean sharedPreferenceGet(Context context, String strKey, boolean bolDefaultValue) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return bolDefaultValue;

        boolean bolRet = spf.getBoolean(strKey, bolDefaultValue);
        return bolRet;
    }

    public static void sharedPreferenceSet(Context context, String strKey, boolean bValue) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return;

        SharedPreferences.Editor ed = spf.edit();
        ed.putBoolean(strKey, bValue);
        ed.apply();
    }

    public static void sharedPreferenceSet(Context context, String strKey, int nValue) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return;

        SharedPreferences.Editor ed = spf.edit();
        ed.putInt(strKey, nValue);
        ed.commit();
    }

    public static void sharedPreferenceSet(Context context, String strKey, String strValue) {
        SharedPreferences spf = sharedPreferenceGet(context);
        if (null == spf)
            return;

        SharedPreferences.Editor ed = spf.edit();
        ed.putString(strKey, strValue);
        ed.commit();
    }

    //screen type constants
    public static final String SCREEN_TYPE_PHONE = "phone";
    public static final String SCREEN_TYPE_7_INCH_TABLET = "7-inch-tablet";
    public static final String SCREEN_TYPE_10_INCH_TABLET = "10-inch-tablet";

    //determine of tablet or not
    public static boolean isTablet(Context context) {

		/*return (context.getResources().getConfiguration().screenLayout
		            & Configuration.SCREENLAYOUT_SIZE_MASK)
		            >= Configuration.SCREENLAYOUT_SIZE_LARGE;*/

        String screenType = getScreenType(context);

        return screenType.equals(SCREEN_TYPE_10_INCH_TABLET) || screenType.equals(SCREEN_TYPE_7_INCH_TABLET);

    }

    public static String getScreenType(Context context) {

        return context.getString(R.string.screen_type);

    }

    public static boolean isMemoryLow(Context context) {
        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory(context);

        if (memoryInfo.lowMemory)
            return true;
        else
            return false;
    }

    // Get a MemoryInfo object for the device's current memory status.
    public static ActivityManager.MemoryInfo getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public static Calendar getCurrentDate() {
        //If we need to deal with TimeZone, that is where we need to set it.
        //If we need a current Date object, just new Date(). By default, it is current Date and time.
        Calendar ca = Calendar.getInstance();
        return ca;
    }

    public static String getFullTimeStamp() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        return sdf.format(LogoliciousApp.getCurrentDate().getTime());

    }

    public static void recycleBitmap(Bitmap b) {
        if (null != b && !b.isRecycled()) {
            Log.d("LogoliciousApp", "recycleBitmap");
            b.recycle();
            b = null;
        }
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    public static Bitmap flip(Bitmap src, int type) {
        Matrix matrix = new Matrix();

        if (type == FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == FLIP_HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static void setEditTextMaxLength(EditText editText, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(FilterArray);
    }


}