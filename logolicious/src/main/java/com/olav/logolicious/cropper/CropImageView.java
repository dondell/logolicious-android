/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.olav.logolicious.cropper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.olav.logolicious.R;
import com.olav.logolicious.cropper.cropwindow.CropOverlayView;
import com.olav.logolicious.cropper.cropwindow.edge.Edge;
import com.olav.logolicious.cropper.util.ImageViewUtil;
import com.olav.logolicious.screens.activities.ActivityImageCropNew;
import com.olav.logolicious.screens.activities.ActivityImageCropNew.AspectRatioPair;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.image.ImageHelper;

import static com.olav.logolicious.screens.activities.ActivityMainEditor.DEVICE_HEIGHT;
import static com.olav.logolicious.screens.activities.ActivityMainEditor.DEVICE_WIDTH;


/**
 * Custom view that provides cropping capabilities to an image.
 */
public class CropImageView extends FrameLayout {

    //region: Fields and Consts

    private static final Rect EMPTY_RECT = new Rect();

    // Sets the default image guidelines to show when resizing
    public static final int DEFAULT_GUIDELINES = 1;

    public static final boolean DEFAULT_FIXED_ASPECT_RATIO = true;

    public static final int DEFAULT_ASPECT_RATIO_X = 1;

    public static final int DEFAULT_ASPECT_RATIO_Y = 1;

    public static final int DEFAULT_SCALE_TYPE_INDEX = 0;

    public static final int DEFAULT_CROP_SHAPE_INDEX = 0;

    private static final int DEFAULT_IMAGE_RESOURCE = 0;

    private static final ImageView.ScaleType[] VALID_SCALE_TYPES = new ImageView.ScaleType[]{ImageView.ScaleType.CENTER_INSIDE, ImageView.ScaleType.FIT_CENTER};

    private static final CropShape[] VALID_CROP_SHAPES = new CropShape[]{CropShape.RECTANGLE, CropShape.OVAL};

    private static final String DEGREES_ROTATED = "DEGREES_ROTATED";

    public ImageView mImageView;

    private CropOverlayView mCropOverlayView;

    public Bitmap mBitmap;
    public  static  int bW = 0;
    public  static  int bH = 0;

    private int mDegreesRotated = 0;

    private int mLayoutWidth;

    private int mLayoutHeight;

    /**
     * Instance variables for customizable attributes
     */
    private int mGuidelines = DEFAULT_GUIDELINES;

    private boolean mFixAspectRatio = DEFAULT_FIXED_ASPECT_RATIO;

    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_X;

    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_Y;

    private int mImageResource = DEFAULT_IMAGE_RESOURCE;

    private ImageView.ScaleType mScaleType = VALID_SCALE_TYPES[DEFAULT_SCALE_TYPE_INDEX];

    /**
     * The shape of the cropping area - rectangle/circular.
     */
    private CropImageView.CropShape mCropShape;

    /**
     * The URI that the image was loaded from (if loaded from URI)
     */
    private Uri mLoadedImageUri;

    /**
     * The sample size the image was loaded by if was loaded by URI
     */
    private int mLoadedSampleSize = 1;
    //endregion
    public Bitmap bitmapRotate = null;

    public CropImageView(Context context) {
        super(context);
        if(!isInEditMode()){
        	init(context);	
        }
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(!isInEditMode()){
        	TypedArray ta = context.obtainStyledAttributes(attrs,  R.styleable.CropImageView, 0, 0);
            try {
                mGuidelines = ta.getInteger(R.styleable.CropImageView_guidelines, DEFAULT_GUIDELINES);
                mFixAspectRatio = ta.getBoolean(R.styleable.CropImageView_fixAspectRatio, DEFAULT_FIXED_ASPECT_RATIO);
                mAspectRatioX = ta.getInteger(R.styleable.CropImageView_aspectRatioX, DEFAULT_ASPECT_RATIO_X);
                mAspectRatioY = ta.getInteger(R.styleable.CropImageView_aspectRatioY, DEFAULT_ASPECT_RATIO_Y);
                mImageResource = ta.getResourceId(R.styleable.CropImageView_imageResource, DEFAULT_IMAGE_RESOURCE);
                mScaleType = VALID_SCALE_TYPES[ta.getInt(R.styleable.CropImageView_scaleType, DEFAULT_SCALE_TYPE_INDEX)];
                mCropShape = VALID_CROP_SHAPES[ta.getInt(R.styleable.CropImageView_cropShape, DEFAULT_CROP_SHAPE_INDEX)];
            } finally {
                ta.recycle();
            }

            init(context);
        }
        
    }

    /**
     * Set the scale type of the image in the crop view
     */
    public ImageView.ScaleType getScaleType() {
        return mScaleType;
    }

    /**
     * Set the scale type of the image in the crop view
     */
    public void setScaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
        if (mImageView != null)
            mImageView.setScaleType(mScaleType);
    }

    /**
     * The shape of the cropping area - rectangle/circular.
     */
    public CropImageView.CropShape getCropShape() {
        return mCropShape;
    }

    /**
     * The shape of the cropping area - rectangle/circular.
     */
    public void setCropShape(CropImageView.CropShape cropShape) {
        if (cropShape != mCropShape) {
            mCropShape = cropShape;
            mCropOverlayView.setCropShape(cropShape);
        }
    }

    /**
     * Sets whether the aspect ratio is fixed or not; true fixes the aspect ratio, while
     * false allows it to be changed.
     *
     * @param fixAspectRatio Boolean that signals whether the aspect ratio should be
     * maintained.
     */
    public void setFixedAspectRatio(boolean fixAspectRatio) {
        mCropOverlayView.setFixedAspectRatio(fixAspectRatio);
    }

    /**
     * Sets the guidelines for the CropOverlayView to be either on, off, or to show when
     * resizing the application.
     *
     * @param guidelines Integer that signals whether the guidelines should be on, off, or
     * only showing when resizing.
     */
    public void setGuidelines(int guidelines) {
        mCropOverlayView.setGuidelines(guidelines);
    }

    /**
     * Sets the both the X and Y values of the aspectRatio.
     *
     * @param aspectRatioX int that specifies the new X value of the aspect ratio
     * @param aspectRatioY int that specifies the new Y value of the aspect ratio
     */
    public void setAspectRatio(int aspectRatioX, int aspectRatioY) {
        mAspectRatioX = aspectRatioX;
        mCropOverlayView.setAspectRatioX(mAspectRatioX);

        mAspectRatioY = aspectRatioY;
        mCropOverlayView.setAspectRatioY(mAspectRatioY);
    }
    
    public void flipAspectRation(int arIndex, ActivityImageCropNew.AspectRatioPair ar){
    	int tempAspectX = ar.X;
    	int tempAspectY = ar.Y;
    	
    	mAspectRatioX = tempAspectY;
    	mCropOverlayView.setAspectRatioX(mAspectRatioX);

    	mAspectRatioY = tempAspectX;
    	mCropOverlayView.setAspectRatioY(mAspectRatioY);

    	ActivityImageCropNew.AspectRatioPair newAR = new AspectRatioPair(mAspectRatioX, mAspectRatioY,
    			// reverse drawable
    			ar.drawableReverse, ar.drawableMain);
    	ActivityImageCropNew.arrAspects.set(arIndex, newAR);
    }

    /**
     * Returns the integer of the imageResource
     */
    public int getImageResource() {
        return mImageResource;
    }

    /**
     * Sets a Bitmap as the content of the CropImageView.
     *
     * @param bitmap the Bitmap to set
     */
    public void setImageBitmap(Bitmap bitmap) {

        if(null != mBitmap) {
            bW = mBitmap.getWidth();
            bH = mBitmap.getHeight();
        }
    	
        if(mBitmap == bitmap) {
            return;
        }

        // if we allocated the bitmap, release it as fast as possible
        if (mBitmap != null && (mImageResource > 0 || mLoadedImageUri != null) && !mBitmap.isRecycled()) {
//            LogoliciousApp.recycleBitmap(mBitmap);
            mBitmap = null;
        }

        // clean the loaded image flags for new image
        mImageResource = 0;
        mLoadedImageUri = null;
        mLoadedSampleSize = 1;
        mDegreesRotated = 0;

        mBitmap = bitmap;
        
        if(bitmap != null){
        	mImageView.setImageBitmap(bitmap);
//        	GlobalClass.imageLoader.displayImage(Uri.parse("file:/" + GlobalClass.picturePath).toString(), mImageView);

            mImageView.getViewTreeObserver().addOnPreDrawListener(new   ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    GlobalClass.screenCropperHeigth = mImageView.getHeight();
                    GlobalClass.screenCropperWidth = mImageView.getWidth();

                    return false;
                }
            });
        }
        
        if (mCropOverlayView != null) {
            mCropOverlayView.resetCropOverlayView();
        }
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    /**
     * Sets a Bitmap and initializes the image rotation according to the EXIT data.<br>
     * <br>
     * The EXIF can be retrieved by doing the following:
     * <code>ExifInterface exif = new ExifInterface(path);</code>
     *
     * @param bitmap the original bitmap to set; if null, this
     * @param exif the EXIF information about this bitmap; may be null
     */
    public void setImageBitmap(Bitmap bitmap, ExifInterface exif) {
        if (bitmap != null && exif != null) {
            ImageViewUtil.RotateBitmapResult result = ImageViewUtil.rotateBitmapByExif(bitmap, exif);
            bitmap = result.bitmap;
            mDegreesRotated = result.degrees;
        }
        setImageBitmap(bitmap);
    }

    /**
     * Sets a Drawable as the content of the CropImageView.
     *
     * @param resId the drawable resource ID to set
     */
    public void setImageResource(int resId) {
        if (resId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            if(bitmap != null){
            	setImageBitmap(bitmap);	
            }
            mImageResource = resId;
        }
    }

    /**
     * Sets a bitmap loaded from the given Android URI as the content of the CropImageView.<br>
     * Can be used with URI from gallery or camera source.<br>
     * Will rotate the image by exif data.<br>
     *
     * @param uri the URI to load the image from
     */
    public void setImageUri(Uri uri) {
        if (uri != null) {

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            double densityAdj = metrics.density > 1 ? 1 / metrics.density : 1;

            int width = (int) (metrics.widthPixels * densityAdj);
            int height = (int) (metrics.heightPixels * densityAdj);
            ImageViewUtil.DecodeBitmapResult decodeResult =
                    ImageViewUtil.decodeSampledBitmap(getContext(), uri, width, height);

            ImageViewUtil.RotateBitmapResult rotateResult =
                    ImageViewUtil.rotateBitmapByExif(getContext(), decodeResult.bitmap, uri);

            setImageBitmap(rotateResult.bitmap);

            mLoadedImageUri = uri;
            mLoadedSampleSize = decodeResult.sampleSize;
            mDegreesRotated = rotateResult.degrees;
        }
    }

    /**
     * Gets the crop window's position relative to the source Bitmap (not the image
     * displayed in the CropImageView).
     *
     * @return a RectF instance containing cropped area boundaries of the source Bitmap
     */
    public Rect getActualCropRect() {
        if (GlobalClass.baseBitmap != null) {
            final Rect displayedImageRect = ImageViewUtil.getBitmapRect(GlobalClass.baseBitmap, mImageView, mScaleType);

            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for width.
            final float actualImageWidth = GlobalClass.baseBitmap.getWidth();
            final float displayedImageWidth = displayedImageRect.width();
            final float scaleFactorWidth = actualImageWidth / displayedImageWidth;

            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for height.
            final float actualImageHeight = GlobalClass.baseBitmap.getHeight();
            final float displayedImageHeight = displayedImageRect.height();
            final float scaleFactorHeight = actualImageHeight / displayedImageHeight;

            // Get crop window position relative to the displayed image.
            final float displayedCropLeft = Edge.LEFT.getCoordinate() - displayedImageRect.left;
            final float displayedCropTop = Edge.TOP.getCoordinate() - displayedImageRect.top;
            final float displayedCropWidth = Edge.getWidth();
            final float displayedCropHeight = Edge.getHeight();

            // Scale the crop window position to the actual size of the Bitmap.
            float actualCropLeft = displayedCropLeft * scaleFactorWidth;
            float actualCropTop = displayedCropTop * scaleFactorHeight;
            float actualCropRight = actualCropLeft + displayedCropWidth * scaleFactorWidth;
            float actualCropBottom = actualCropTop + displayedCropHeight * scaleFactorHeight;

            // Correct for floating point errors. Crop rect boundaries should not exceed the source Bitmap bounds.
            actualCropLeft = Math.max(0f, actualCropLeft);
            actualCropTop = Math.max(0f, actualCropTop);
            actualCropRight = Math.min(GlobalClass.baseBitmap.getWidth(), actualCropRight);
            actualCropBottom = Math.min(GlobalClass.baseBitmap.getHeight(), actualCropBottom);

            return new Rect((int) actualCropLeft, (int) actualCropTop, (int) actualCropRight, (int) actualCropBottom);
        } else {
            return null;
        }
    }

    /**
     * Gets the crop window's position relative to the source Bitmap (not the image
     * displayed in the CropImageView) and the original rotation.
     *
     * @return a RectF instance containing cropped area boundaries of the source Bitmap
     */
    public Rect getActualCropRectNoRotation() {
        if (GlobalClass.baseBitmap != null) {
            Rect rect = getActualCropRect();
            int rotateSide = mDegreesRotated / 90;
            if (rotateSide == 1) {
                rect.set(rect.top, GlobalClass.baseBitmap.getWidth() - rect.right, rect.bottom, GlobalClass.baseBitmap.getWidth() - rect.left);
            } else if (rotateSide == 2) {
                rect.set(GlobalClass.baseBitmap.getWidth() - rect.right, GlobalClass.baseBitmap.getHeight() - rect.bottom, GlobalClass.baseBitmap.getWidth() - rect.left, GlobalClass.baseBitmap.getHeight() - rect.top);
            } else if (rotateSide == 3) {
                rect.set(GlobalClass.baseBitmap.getHeight() - rect.bottom, rect.left, GlobalClass.baseBitmap.getHeight() - rect.top, rect.right);
            }
            rect.set(rect.left * mLoadedSampleSize, rect.top * mLoadedSampleSize, rect.right * mLoadedSampleSize, rect.bottom * mLoadedSampleSize);
            return rect;
        } else {
            return null;
        }
    }

    /**
     * Rotates image by the specified number of degrees clockwise. Cycles from 0 to 360
     * degrees.
     *
     * @param degrees Integer specifying the number of degrees to rotate.
     */
    public void rotateImage(int degrees) {
        Log.i("xxx", "xxx  Correcting orientation to " + degrees + " degree");
        if (GlobalClass.baseBitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            GlobalClass.baseBitmap = Bitmap.createBitmap(GlobalClass.baseBitmap, 0, 0, GlobalClass.baseBitmap.getWidth(), GlobalClass.baseBitmap.getHeight(), matrix, true);
            bitmapRotate = ImageHelper.scaleWithRespectToAspectRatio(GlobalClass.baseBitmap, DEVICE_WIDTH, DEVICE_HEIGHT);
            setImageBitmap(bitmapRotate);

            mDegreesRotated += degrees;
            mDegreesRotated = mDegreesRotated % 360;
        }
    }

    public boolean isRotated = false;
    public Bitmap getRotatedBitmap(){
    	return bitmapRotate;
    }
    
    public Bitmap getNoCropBitmap(){
    	return mBitmap;
    }

    /**
     * Gets the cropped image based on the current crop window.
     *
     * @return a new Bitmap representing the cropped image
     */
    public Bitmap getCroppedImage() {
        return getCroppedImage(0, 0);
    }
    
    /**
     * 
     * @author dondell
     * @return Return the bitmap crop outside the bitmap 
     */
    public Bitmap getCroppedImage(String x){
    	this.buildDrawingCache();
    	Bitmap b = Bitmap.createBitmap(mCropOverlayView.RIGHT - mCropOverlayView.LEFT - 10, 
    			mCropOverlayView.BOTTOM - mCropOverlayView.TOP, Config.ARGB_8888);
    	if (mBitmap != null) {
        	Canvas c = new Canvas(b);
        	c.drawBitmap(Bitmap.createBitmap(
        			this.getDrawingCache(), 
        			// plus 5 to to hide the overlay corner
        			mCropOverlayView.LEFT + 5, 
        			mCropOverlayView.TOP + 5, 
        			mCropOverlayView.RIGHT - (mCropOverlayView.LEFT + 10), 
        			mCropOverlayView.BOTTOM - (mCropOverlayView.TOP + 5)), 
        			0, 0, null);
    	}
    	return b;
    }

    /**
     * Gets the cropped image based on the current crop window.<br>
     * If image loaded from URI will use sample size to fir the requested width and height.
     *
     * @return a new Bitmap representing the cropped image
     */
    public Bitmap getCroppedImage(int reqWidth, int reqHeight) {
        if (GlobalClass.baseBitmap != null) {
            if (mLoadedImageUri != null && mLoadedSampleSize > 1) {
                Rect rect = getActualCropRectNoRotation();
                reqWidth = reqWidth > 0 ? reqWidth : rect.width();
                reqHeight = reqHeight > 0 ? reqHeight : rect.height();
                ImageViewUtil.DecodeBitmapResult result =
                        ImageViewUtil.decodeSampledBitmapRegion(getContext(), mLoadedImageUri, rect, reqWidth, reqHeight);

                Bitmap bitmap = result.bitmap;
                if (mDegreesRotated > 0) {
                    bitmap = ImageViewUtil.rotateBitmap(bitmap, mDegreesRotated);
                }

                return bitmap;
            } else {
                Rect rect = getActualCropRect();
                return Bitmap.createBitmap(GlobalClass.baseBitmap, rect.left, rect.top, rect.width(), rect.height());
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the cropped circle image based on the current crop selection.
     *
     * @return a new Circular Bitmap representing the cropped image
     */
    public Bitmap getCroppedOvalImage() {
        if (mBitmap != null) {
            Bitmap cropped = getCroppedImage();

            int width = cropped.getWidth();
            int height = cropped.getHeight();
            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);

            RectF rect = new RectF(0, 0, width, height);
            canvas.drawOval(rect, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(cropped, 0, 0, paint);

            return output;
        } else {
            return null;
        }
    }

    //region: Private methods

    @Override
    public Parcelable onSaveInstanceState() {

        final Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt(DEGREES_ROTATED, mDegreesRotated);

        return bundle;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            final Bundle bundle = (Bundle) state;

            if (mBitmap != null) {
                // Fixes the rotation of the image when orientation changes.
                mDegreesRotated = bundle.getInt(DEGREES_ROTATED);
                int tempDegrees = mDegreesRotated;
                rotateImage(mDegreesRotated);
                mDegreesRotated = tempDegrees;
            }

            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (mBitmap != null) {
            final Rect bitmapRect = ImageViewUtil.getBitmapRect(mBitmap, this, mScaleType);
            mCropOverlayView.setBitmapRect(bitmapRect);
        } else {
        	if(!isInEditMode())
        		mCropOverlayView.setBitmapRect(EMPTY_RECT);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Bypasses a baffling bug when used within a ScrollView, where
            // heightSize is set to 0.
            if (heightSize == 0) {
                heightSize = mBitmap.getHeight();
            }

            int desiredWidth;
            int desiredHeight;

            double viewToBitmapWidthRatio = Double.POSITIVE_INFINITY;
            double viewToBitmapHeightRatio = Double.POSITIVE_INFINITY;

            // Checks if either width or height needs to be fixed
            if (widthSize < mBitmap.getWidth()) {
                viewToBitmapWidthRatio = (double) widthSize / (double) mBitmap.getWidth();
            }
            if (heightSize < mBitmap.getHeight()) {
                viewToBitmapHeightRatio = (double) heightSize / (double) mBitmap.getHeight();
            }

            // If either needs to be fixed, choose smallest ratio and calculate
            // from there
            if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY) {
                if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                    desiredWidth = widthSize;
                    desiredHeight = (int) (mBitmap.getHeight() * viewToBitmapWidthRatio);
                } else {
                    desiredHeight = heightSize;
                    desiredWidth = (int) (mBitmap.getWidth() * viewToBitmapHeightRatio);
                }
            }

            /**
             *  Otherwise, the picture is within frame layout bounds. Desired
             *  width is simply picture size
             */
            
            else {
                desiredWidth = mBitmap.getWidth();
                desiredHeight = mBitmap.getHeight();
            }

            int width = getOnMeasureSpec(widthMode, widthSize, desiredWidth);
            int height = getOnMeasureSpec(heightMode, heightSize, desiredHeight);

            mLayoutWidth = width;
            mLayoutHeight = height;

            final Rect bitmapRect = ImageViewUtil.getBitmapRect(
            		mBitmap.getWidth(),
                    mBitmap.getHeight(),
                    mLayoutWidth,
                    mLayoutHeight,
                    mScaleType);
            mCropOverlayView.setBitmapRect(bitmapRect);

            // MUST CALL THIS
            setMeasuredDimension(mLayoutWidth, mLayoutHeight);

        } else {

            if(!isInEditMode())
            	mCropOverlayView.setBitmapRect(EMPTY_RECT);
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            // Gets original parameters, and creates the new parameters
            final ViewGroup.LayoutParams origparams = this.getLayoutParams();
            origparams.width = mLayoutWidth;
            origparams.height = mLayoutHeight;
            setLayoutParams(origparams);
        }
    }

    private void init(final Context context) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.crop_image_view	, this, true);

        mImageView = (ImageView) v.findViewById(R.id.ImageView_image);
        mImageView.setScaleType(mScaleType);
        mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				findDoubleClick();
				if(mHasDoubleClicked){
                    LogoliciousApp.toast(context, "double click detected", Toast.LENGTH_LONG);
				}
			}
        });

        setImageResource(mImageResource);

        mCropOverlayView = (CropOverlayView) v.findViewById(R.id.CropOverlayView);
        mCropOverlayView.setInitialAttributeValues(mGuidelines, mFixAspectRatio, mAspectRatioX, mAspectRatioY);
        mCropOverlayView.setCropShape(mCropShape);
    }

    /**
     * Determines the specs for the onMeasure function. Calculates the width or height
     * depending on the mode.
     *
     * @param measureSpecMode The mode of the measured width or height.
     * @param measureSpecSize The size of the measured width or height.
     * @param desiredSize The desired size of the measured width or height.
     * @return The final size of the width or height.
     */
    private static int getOnMeasureSpec(int measureSpecMode, int measureSpecSize, int desiredSize) {

        // Measure Width
        int spec;
        if (measureSpecMode == MeasureSpec.EXACTLY) {
            // Must be this size
            spec = measureSpecSize;
        } else if (measureSpecMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...; match_parent value
            spec = Math.min(desiredSize, measureSpecSize);
        } else {
            // Be whatever you want; wrap_content
            spec = desiredSize;
        }

        return spec;
    }
    //endregion

    //region: Inner class: CropShape

    /**
     * The possible cropping area shape
     */
    public static enum CropShape {
        RECTANGLE,
        OVAL
    }
    //endregion
    
    private static final long DOUBLE_PRESS_INTERVAL = 250; // in millis
    private long lastPressTime;
    public static boolean mHasDoubleClicked = false;
    private boolean findDoubleClick() {
        // Get current time in nano seconds.
    long pressTime = System.currentTimeMillis();
        // If double click...
        if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
            mHasDoubleClicked = true;           

            // double click event....
        } else { // If not double click....
            mHasDoubleClicked = false;
        }
        lastPressTime = pressTime;
        return mHasDoubleClicked;
    }

}
