package com.olav.logolicious.screens.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.cropper.CropImageView;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.image.BitmapSaver;
import com.olav.logolicious.util.image.ImageHelper;

import java.util.ArrayList;

import static com.olav.logolicious.screens.activities.ActivityMainEditor.DEVICE_HEIGHT;
import static com.olav.logolicious.screens.activities.ActivityMainEditor.DEVICE_WIDTH;

public class ActivityImageCropNew extends Activity implements OnClickListener, OnTouchListener{

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int ON_TOUCH = 1;

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    public static String CAMERA_CAPTURE_IN_LANDSCAPE = "landscape";

    Bitmap croppedImage;
    Bitmap nonCropped;
    static CropImageView cropImageView;
    GestureDetector gestureDetector;
    public static int doubleTapView;
    public static ArrayList<AspectRatioPair> arrAspects = new ArrayList<ActivityImageCropNew.AspectRatioPair>();
    protected GlobalClass gc;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static Rect getActualRect(){
        return cropImageView.getActualCropRect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.crop_section);
        gc = (GlobalClass) this.getApplicationContext();
        // Initialize components of the app
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setScaleType(ScaleType.FIT_CENTER);
        cropImageView.setFixedAspectRatio(true);

        if(GlobalClass.baseBitmap != null){
            Bitmap bi = ImageHelper.scaleWithRespectToAspectRatio(GlobalClass.baseBitmap, DEVICE_WIDTH, DEVICE_HEIGHT);
            cropImageView.setImageBitmap(bi);
            //cropImageView.rotateImage(LogoliciousApp.sharedPreferenceGet(ActivityImageCropNew.this, "BaseImgOrientation", 0));
            GlobalClass.origBitmapwidth = GlobalClass.baseBitmap.getWidth();
            GlobalClass.origBitmapheight = GlobalClass.baseBitmap.getHeight();

            FileUtil.fileWrite(GlobalClass.log_path, "Cropper: Image successfully Shown.", true);
        } else {
            FileUtil.fileWrite(GlobalClass.log_path, "Cropper: The base image is too large.", true);
        }

        // creating new gesture detector
        gestureDetector = new GestureDetector(ActivityImageCropNew.this, new GestureListener());

        // Sets initial aspect ratio
        cropImageView.post(new Runnable() {
            @Override
            public void run() {
//		    	 check if landscape or portrait

                // if camera capture and is rotated
                Bundle extras = getIntent().getExtras();
                if (extras != null) {

                    if(ImageHelper.isPortrait(GlobalClass.origBitmapwidth, GlobalClass.origBitmapheight)){
                        // portrait
                        mAspectRatioX = (int) extras.getInt("D_W");
                        mAspectRatioY = (int) extras.getInt("D_H");
                    } else {
                        // landscape
                        mAspectRatioX = (int) extras.getInt("D_H");
                        mAspectRatioY = (int) extras.getInt("D_W");
                    }
                }

                if(cropImageView != null){
                    cropImageView.setFixedAspectRatio(true);
                    cropImageView.setAspectRatio(16, 9);
                }

                cropImageView.invalidate();
            }
        });

        //Sets the rotate button
        final ImageView rotateButton = (ImageView) findViewById(R.id.Button_rotate);
        rotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cropImageView.isRotated = true;

                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
                FileUtil.fileWrite(GlobalClass.log_path, "Cropper: click rotateButton", true);
                Log.i("Cropper", "xxx Rotate button");
            }
        });

        final ImageView cropButton = (ImageView) findViewById(R.id.Button_crop);
        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                croppedImage = cropImageView.getCroppedImage();

                if (null == croppedImage) {
                    LogoliciousApp.showMessageOK(ActivityImageCropNew.this, "Can't Crop image.", null);
                }

                GlobalClass.picturePath = BitmapSaver.saveBitmape(LogoliciousApp.sharedPreferenceGet(ActivityImageCropNew.this), ActivityMainEditor.tempDir, "crop", croppedImage);
                GlobalClass.baseBitmap = ImageHelper.decodeBitmapPath(GlobalClass.picturePath);

                //Re-initialized cache if it is null
                if (null == GlobalClass.diskCache)
                    GlobalClass.initDiskCache(ActivityImageCropNew.this);

                if (null == GlobalClass.mMemoryCache)
                    GlobalClass.initMemCache(ActivityImageCropNew.this);

                if (null != GlobalClass.diskCache)
                    GlobalClass.diskCache.put("BaseImage", croppedImage);
                if (null != GlobalClass.mMemoryCache)
                    GlobalClass.mMemoryCache.put("BaseImage", croppedImage);
                FileUtil.fileWrite(GlobalClass.log_path, "Cropper: click cropButton", true);
                Log.i("Cropper", "xxx Crop button");

                startMainEditor();
            }
        });

        final ImageView nocropButton = (ImageView) findViewById(R.id.Button_nocrop);
        nocropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(cropImageView.isRotated) {
                    GlobalClass.baseBitmap = cropImageView.getNoCropBitmap();
                    GlobalClass.picturePath = BitmapSaver.saveBitmape(LogoliciousApp.sharedPreferenceGet(ActivityImageCropNew.this), ActivityMainEditor.tempDir, "rotated", cropImageView.getRotatedBitmap());
                    Log.i("xxx rotated bitmap", "xxx cropImageView.getRotatedBitmap()");
                } else {
                    GlobalClass.picturePath = BitmapSaver.saveBitmape(LogoliciousApp.sharedPreferenceGet(ActivityImageCropNew.this), ActivityMainEditor.tempDir, "crop", cropImageView.getNoCropBitmap());
                }

                if(GlobalClass.diskCache != null) {
                    if(cropImageView.isRotated)
                        GlobalClass.diskCache.put("BaseImage", cropImageView.getRotatedBitmap());
                    else
                        GlobalClass.diskCache.put("BaseImage", cropImageView.getNoCropBitmap());
                }
                if(GlobalClass.mMemoryCache != null) {
                    if(cropImageView.isRotated)
                        GlobalClass.mMemoryCache.put("BaseImage", cropImageView.getRotatedBitmap());
                    else
                        GlobalClass.mMemoryCache.put("BaseImage", cropImageView.getNoCropBitmap());
                }
                FileUtil.fileWrite(GlobalClass.log_path, "Cropper: click nocropButton", true);
                Log.i("Cropper", "xxx No-Crop button");

                //Check AR of the picture when no AR selected
                float bW, bH;
                if(cropImageView.isRotated) {
                    bW = (float)cropImageView.getRotatedBitmap().getWidth();
                    bH = (float)cropImageView.getRotatedBitmap().getHeight();
                } else {
                    bW = (float)cropImageView.getNoCropBitmap().getWidth();
                    bH = (float)cropImageView.getNoCropBitmap().getHeight();
                }

                Log.i("xxx","xxx (bW) " + bW);
                Log.i("xxx","xxx (bH) " + bH);
                //AR 9:16
                if((int)bW == (int)(bH * 0.5625)) {
                    GlobalClass.AR = "9:16";
                    GlobalClass.ARLast = "9:16";
                    Log.i("xxx","xxx AR is 9:16");
                    //AR 16:9
                } else if((int)bW == (int)(bH * 1.778)) {
                    GlobalClass.AR = "16:9";
                    GlobalClass.ARLast = "16:9";
                    Log.i("xxx","xxx AR is 16:9");
                    //AR 3:4
                } else if((int)bW == (int)(bH * 0.75)) {
                    GlobalClass.AR = "3:4";
                    GlobalClass.ARLast = "3:4";
                    Log.i("xxx","xxx AR is 3:4");
                    //AR 4:3
                } else if((int)bW == (int)(bH * 1.333)) {
                    GlobalClass.AR = "4:3";
                    GlobalClass.ARLast = "4:3";
                    Log.i("xxx","xxx AR is 4:3");
                } else {
                    GlobalClass.AR = "";
                    GlobalClass.ARLast = "";
                    Log.i("xxx","xxx AR is Free Transform");
                }

                startMainEditor();
            }
        });

        LogoliciousApp.setOnClickListener(this, R.id.aspectRatio11);
        LogoliciousApp.setOnClickListener(this, R.id.aspectRatio23);
        LogoliciousApp.setOnClickListener(this, R.id.aspectRatio43);
        LogoliciousApp.setOnClickListener(this, R.id.aspectRatio169);

        LogoliciousApp.setOnTouchListener(this, R.id.aspectRatio11, gestureDetector);
        LogoliciousApp.setOnTouchListener(this, R.id.aspectRatio23, gestureDetector);
        LogoliciousApp.setOnTouchListener(this, R.id.aspectRatio43, gestureDetector);
        LogoliciousApp.setOnTouchListener(this, R.id.aspectRatio169, gestureDetector);

        //Construct Aspects
        arrAspects.add(new AspectRatioPair(1, 1, 0, 0));
        arrAspects.add(new AspectRatioPair(4, 3, R.drawable.rotate_ratio4_3, R.drawable.rotate_ratio3_4));
        arrAspects.add(new AspectRatioPair(16, 9, R.drawable.rotate_ratio16_9,  R.drawable.rotate_ratio9_16));
        arrAspects.add(new AspectRatioPair(3, 2, R.drawable.rotate_ratio3_2,  R.drawable.rotate_ratio2_3));

        //Initially click ratio 16:9
        AspectRatioPair ar1 = null;
        clickARatio16_9(ar1);
    }

    private void startMainEditor(){
        finish();
        Intent intent = new Intent(ActivityImageCropNew.this, ActivityMainEditor.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String strName = "HIDE_IMAGE_OPTION";
        intent.putExtra("STRING_I_NEED", strName);
        intent.putExtra("isFromCropper", true);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        gc.setCurrentActivity(this);
        //Reset 16:9 default settings
        if(null != arrAspects)
            arrAspects.get(2).wasFirstClick = false;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // free memory
        /**
         * We commented this in order to prevent error "trying to use a recycled bitmap" in main screen when saving.
         * This bitmap is use when we rotate bitmap in cropper window->hit no crop.
         */
//        if (cropImageView.bitmapRotate != null && !cropImageView.bitmapRotate.isRecycled()) {
//            cropImageView.bitmapRotate.recycle();
//            cropImageView.bitmapRotate = null;
//        }

        System.gc();
        GlobalClass.freeMem();
        GlobalClass.subscriptionOkToShow = true;

//        cropImageView.recycleBitmap();

        //clear activity reference
        Activity currActivity = gc.getCurrentActivity();
        if (this.equals(currActivity))
            gc.setCurrentActivity(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // TODO Auto-generated method stub
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
//			finish();
//			Intent backToMain = new Intent(getApplicationContext(), ActivityMainEditor.class);
//			startActivity(backToMain);
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * Sets the font on all TextViews in the ViewGroup. Searches recursively for
     * all inner ViewGroups as well. Just add a check for any other views you
     * want to set as well (EditText, etc.)
     */
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof ImageButton) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, font);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("unused")
    private void selectAspectRatioOption() {
        final Dialog d = new Dialog(ActivityImageCropNew.this);
        d.setContentView(R.layout.select_aspect_ratio);
        d.setTitle("ASPECT RATIO");
        d.setCancelable(true);

        Button aspectRatio169 = (Button) d.findViewById(R.id.aspectRatio169);
        Button aspectRatio43 = (Button) d.findViewById(R.id.aspectRatio43);
        Button aspectRatio11 = (Button) d.findViewById(R.id.aspectRatio11);

        aspectRatio169.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.dismiss();
                cropImageView.setAspectRatio(16, 9);
                LogoliciousApp.setButtonText(ActivityImageCropNew.this, R.id.aspectRatio, "16:9", true);
            }
        });

        aspectRatio43.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.dismiss();
                cropImageView.setAspectRatio(4, 3);
                LogoliciousApp.setButtonText(ActivityImageCropNew.this, R.id.aspectRatio, "4:3", true);
            }
        });

        aspectRatio11.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.dismiss();
                cropImageView.setAspectRatio(1, 1);
                LogoliciousApp.setButtonText(ActivityImageCropNew.this, R.id.aspectRatio, "1:1", true);
            }
        });

        d.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.aspectRatio11) {
            cropImageView.setFixedAspectRatio(true);
            cropImageView.setAspectRatio(1, 1);

            LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio11, getResources().getColor(R.color.Green));
            LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio43, getResources().getColor(R.color.Transparent));
            LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio169, getResources().getColor(R.color.Transparent));
        }
    }

    private class GestureListener  implements
            GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("Double Tap", "xxx onDoubleTapEvent");
//			switch(doubleTapView){
//			case R.id.rotate43:
//				cropImageView.setFixedAspectRatio(true);
//				cropImageView.flipAspectRation(1, arrAspects.get(1));
//				GlobalClass.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio43, arrAspects.get(1).drawableMain);
//				break;
//			case R.id.rotate169:
//				cropImageView.setFixedAspectRatio(true);
//				cropImageView.flipAspectRation(2, arrAspects.get(2));
//				GlobalClass.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio169, arrAspects.get(2).drawableMain);
//				break;
//			}
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent arg0) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("Double Tap", "xxx onSingleTapConfirmed");
            AspectRatioPair ar1 = null;

            switch (doubleTapView) {
                case R.id.aspectRatio11:
                    clickARatio1_1();
                    break;
                case R.id.aspectRatio23:
                    clickARation2_3(ar1);
                    break;
                case R.id.aspectRatio43:
                    clickARation4_3(ar1);
                    break;
                case R.id.aspectRatio169:
                    clickARatio16_9(ar1);
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent arg0) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent arg0) {
        }

        @Override
        public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent arg0) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent arg0) {
            return false;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    public static class AspectRatioPair {
        public int X;
        public int Y;
        public int drawableMain;
        public int drawableReverse;
        public boolean wasFirstClick = false;
        public String ARString = "";

        public AspectRatioPair(int x, int y, int d1, int d2) {
            this.X = x;
            this.Y = y;
            this.drawableMain = d1;
            this.drawableReverse = d2;
            this.ARString = x + ":" + y;
        }
    }

    private void clickARatio1_1(){
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setAspectRatio(1, 1);
        GlobalClass.AR = "1:1";
        GlobalClass.ARLast = "1:1";

        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio11, getResources().getColor(R.color.Green));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio23, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio43, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio169, getResources().getColor(R.color.Transparent));

        AspectRatioPair ar1 = arrAspects.get(3);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio23, ar1.drawableMain);
        ar1 = arrAspects.get(1);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio43, ar1.drawableMain);
        ar1 = arrAspects.get(2);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio169, ar1.drawableMain);
    }

    private void clickARation2_3(AspectRatioPair ar1){
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio11, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio23, getResources().getColor(R.color.Green));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio43, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio169, getResources().getColor(R.color.Transparent));

        ar1 = arrAspects.get(1);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio43, ar1.drawableMain);
        ar1 = arrAspects.get(2);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio169, ar1.drawableMain);

        ar1 = arrAspects.get(3);

        cropImageView.setFixedAspectRatio(true);
        if(ar1.wasFirstClick) {
            ar1.wasFirstClick = false;
            cropImageView.setAspectRatio(2, 3);
            GlobalClass.AR = "2:3";
            GlobalClass.ARLast = "2:3";
            LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio23, ar1.drawableReverse);
        }
        else {
            ar1.wasFirstClick = true;
            cropImageView.setAspectRatio(3, 2);
            GlobalClass.AR = "3:2";
            GlobalClass.ARLast = "3:2";
            LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio23, ar1.drawableMain);
        }
    }

    private void clickARation4_3(AspectRatioPair ar1){
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio43, getResources().getColor(R.color.Green));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio23, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio169, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio11, getResources().getColor(R.color.Transparent));

        ar1 = arrAspects.get(3);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio23, ar1.drawableMain);
        ar1 = arrAspects.get(2);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio169, ar1.drawableMain);

        ar1 = arrAspects.get(1);

        cropImageView.setFixedAspectRatio(true);
        if(ar1.wasFirstClick) {
            ar1.wasFirstClick = false;
            cropImageView.setAspectRatio(3, 4);
            GlobalClass.AR = "3:4";
            GlobalClass.ARLast = "3:4";
            LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio43, ar1.drawableReverse);
        }
        else {
            ar1.wasFirstClick = true;
            cropImageView.setAspectRatio(4, 3);
            GlobalClass.AR = "4:3";
            GlobalClass.ARLast = "4:3";
            LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio43, ar1.drawableMain);
        }
    }

    private void clickARatio16_9(AspectRatioPair ar1){
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio169, getResources().getColor(R.color.Green));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio23, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio43, getResources().getColor(R.color.Transparent));
        LogoliciousApp.setImageViewTint(ActivityImageCropNew.this, R.id.aspectRatio11, getResources().getColor(R.color.Transparent));

        ar1 = arrAspects.get(3);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio23, ar1.drawableMain);
        ar1 = arrAspects.get(1);
        ar1.wasFirstClick = false;
        LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio43, ar1.drawableMain);

        ar1 = arrAspects.get(2);

        cropImageView.setFixedAspectRatio(true);
        if(ar1.wasFirstClick) {
            ar1.wasFirstClick = false;
            cropImageView.setAspectRatio(9, 16);
            GlobalClass.AR = "9:16";
            GlobalClass.ARLast = "9:16";
            LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio169, ar1.drawableReverse);
        }
        else {
            ar1.wasFirstClick = true;
            cropImageView.setAspectRatio(16, 9);
            GlobalClass.AR = "16:9";
            GlobalClass.ARLast = "16:9";
            LogoliciousApp.setImageViewImageAndRotate(ActivityImageCropNew.this, R.id.aspectRatio169, ar1.drawableMain);
        }
    }

}