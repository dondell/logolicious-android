package com.olav.logolicious.customize.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.gesture.MoveGestureDetector;
import com.olav.logolicious.util.gesture.MoveGestureDetector.SimpleOnMoveGestureListener;
import com.olav.logolicious.util.gesture.RotateGestureDetector;
import com.olav.logolicious.util.gesture.RotateGestureDetector.SimpleOnRotateGestureListener;
import com.olav.logolicious.util.image.ImageHelper;

public class Layer {
	Context ctx;
	public Matrix matrix = new Matrix();
	Matrix inverse = new Matrix();
	public Matrix newMatrix = new Matrix();
	RectF 			bounds;
	View 			parent;
	public Bitmap 	bitmap;
	public String 	bitmapPath = null;
	public String 	text = null;
	public int 		color;
	public String 	font_style = null;
	public boolean shadow = false;
	private MoveGestureDetector mgd;
	private ScaleGestureDetector sgd;
	private RotateGestureDetector rgd;
	private static Canvas 	mCanvas;
	public float 	firstTouchX, firstTouchY, transX, transY;
	public float 	bX, bY;
	public float 	scaleFactor = 1.f;
	public Bitmap 	bmOverlay;
	// rotating and scaling
	public float 	getFocusX, getFocusY;
	private Paint 	paintText;
	public boolean 	isColorChanged = false;
	public String 	colorToChange = "";
	public int 		seekBarTransparent = 255;
	private View 	logoParentView;
	public float 	rotation = 0;
	public boolean 	isTextMode = false;
	public boolean 	isTwoFinger = false;
	boolean 		isScaling = false;
	public boolean isLock = false;
	BitmapFactory.Options opt;
	// resulting screen
	float screenImageW = ActivityMainEditor.bW;
	float screenImageH = ActivityMainEditor.bH;
	public boolean touch = false;
	float scaleWidth = 0, scaleHeight = 0;
	float scaleOriginal = 0; //this is the scale after the item is first place in the canvas.
	public int uniqueID = 0;

	/**
	 *
	 * @param ctx Context from main Activity.
	 * @param parentView The parent View of each layer.
	 * @param b The bitmap to be created in the layer.
	 * @param v The view in which the parent View of the layer will be put.
	 */
	public Layer(Context ctx, View parentView, Bitmap b, String bitmapPath, View v) {
		this.ctx = ctx;
		this.parent = parentView;
		this.logoParentView = v;
		bitmap = b;
		this.bitmapPath = bitmapPath;
		bounds = new RectF(0, 0, b.getWidth(), b.getHeight());

		initialize();
		putBitmapToCenter();
		scaleOriginal = getScaleMatrixFactor();
	}

	/**
	 *
	 * @param ctx Context from main Activity.
	 * @param parentView The parent View of each layer.
	 * @param text The text to be created in the layer.
	 * @param v The view in which the parent View of the layer will be put.
	 */
	public Layer(Context ctx, View parentView, String text, View v, int color, String font_style, boolean shadow) {
		this.ctx = ctx;
		if(!LogoliciousApp.strIsNullOrEmpty(text))
			this.text = text.replaceAll("41411","").replaceAll("51511","");
		else
			this.text = text;
		this.color = color;
		this.parent = parentView;
		this.logoParentView = v;
		this.font_style = font_style;
		this.isTextMode = true;
		this.shadow = shadow;

		paintText = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		paintText.setAntiAlias(true);
		paintText.setTextAlign(Paint.Align.LEFT);
		paintText.setStyle(Paint.Style.FILL_AND_STROKE);
		if(this.shadow)
			paintText.setShadowLayer(2.0f, 5.0f, 5.0f, Color.BLACK);

		createText(this.color, font_style);
		bounds = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

		initialize();
		putBitmapToCenter();
		scaleOriginal = getScaleMatrixFactor();
	}

	private void initialize(){
		opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = false;
		opt.inPreferQualityOverSpeed = true;

		mgd = new MoveGestureDetector(ctx, mgl);
		sgd = new ScaleGestureDetector(ctx, sgl);
		rgd = new RotateGestureDetector(ctx, rgl);
	}

	public boolean contains(MotionEvent event) {
		matrix.invert(inverse);
		float[] pts = {event.getX(), event.getY()};
		inverse.mapPoints(pts);
		if (bounds.contains(pts[0], pts[1])) {
			return true;
		} else {
			try{
				return Color.alpha(bitmap.getPixel((int) pts[0], (int) pts[1])) != 0;
			}catch(Exception ex){
			}
		}
		return false;
	}

	/**
	 *
	 * @param x The X location of the cursor in FunnyDrawingPanel.
	 * @param y The Y location of the cursor in FunnyDrawingPanel.
	 * @return This is only used to detect if the cursor is touching the Bitmap Image.
	 */
	public boolean contains(float x, float y) {
		matrix.invert(inverse);
		float[] pts = {x, y};
		inverse.mapPoints(pts);
		if (bounds.contains(pts[0], pts[1])) {
			return true;
		} else {
			try{
				return Color.alpha(bitmap.getPixel((int) pts[0], (int) pts[1])) != 0;
			}catch(Exception ex){
			}
		}
		return false;
	}

	/**
	 *
	 * @param mCanvas The canvas we received from {@link LayersContainerView}}
	 * @param paint The paint we received from {@link LayersContainerView}
	 * @param isSaving Flag when deciding to save or not while drawing.
	 */
	public void draw(Canvas mCanvas, Paint paint, boolean isSaving, boolean saveAsTemplate) {
		// apply transparency
		paint.setAlpha(this.seekBarTransparent);
		if(isSaving){

			// scale to fit the resulting screen or scale to fit the screen
			Bitmap baseImg;
			if(saveAsTemplate == true) {
				Log.i("", "xxx saveAsTemplate = " + saveAsTemplate);
				baseImg = GlobalClass.diskCache.getBitmap("TemplateBaseImage");
			} else {
				baseImg = GlobalClass.baseBitmap;
				if(null == baseImg) {
					//This should be move to LayersContainerView.java to avoid decoding in each layer.
					baseImg = BitmapFactory.decodeFile(GlobalClass.picturePath);
				}
			}

			int bW = baseImg.getWidth();
			int bH = baseImg.getHeight();

				scaleWidth =  bW / screenImageW;
				scaleHeight = bH / screenImageH;
				if(isTextMode == false){
					// original logo dimension
					// this will result in out of memory ->> BitmapFactory.decodeFile(bitmapPath, opt);
					Bitmap origLogo;
//					if(FileUtil.getFileSize(bitmapPath) > FileUtil.PREFERRED_LOGO_SIZE) {
						origLogo = ImageHelper.decodeBitmapPath(bitmapPath);
//					} else {
//						origLogo = BitmapFactory.decodeFile(bitmapPath, opt);
//					}

					// user rotate
					newMatrix.setRotate(rotation);
					// scale original logo to the match on resize logo.
					newMatrix.postScale((float)bitmap.getWidth()/origLogo.getWidth(), (float)bitmap.getHeight()/origLogo.getHeight(), 0, 0);
					// user scale factor
					newMatrix.postScale(getScaleMatrixFactor(), getScaleMatrixFactor(), 0, 0);

					float[] values = new float[9];
					matrix.getValues(values);
					float globalX = values[Matrix.MTRANS_X];
					float globalY = values[Matrix.MTRANS_Y];

					newMatrix.postTranslate(globalX, globalY);
					newMatrix.postScale(scaleWidth, scaleHeight, 0, 0);
					// apply the original bitmap origBitmap
					paint.setAlpha(this.seekBarTransparent);
					mCanvas.drawBitmap(origLogo, newMatrix, paint);

				} else {
					Log.i("Layer", "Save as text");
					// adjust the matrix to the original image size
					Matrix newMatrix = new Matrix();
					newMatrix.set(matrix);
					newMatrix.postScale(scaleWidth, scaleHeight, 0, 0);
					paint.setAlpha(this.seekBarTransparent);
					mCanvas.drawBitmap(bitmap, newMatrix, paint);
				}
				FileUtil.fileWrite(GlobalClass.log_path, "checking trans = " + seekBarTransparent, true);
		} else {
			// add red edge when touch
			if(touch){
				Paint p = new Paint();
				//new ColorMatrixColorFilter(getColorMatrix()
				p.setColorFilter(new LightingColorFilter(ctx.getResources().getColor(R.color.LogoFilter), 0));
                if(isLock)
				    mCanvas.drawBitmap(ImageHelper.putLockedOnBitmap(ctx, bitmap), matrix, p);
                else
                    mCanvas.drawBitmap(bitmap, matrix, p);
			} else{
                if(isLock)
                    mCanvas.drawBitmap(ImageHelper.putLockedOnBitmap(ctx, bitmap), matrix, paint);
                else
                    mCanvas.drawBitmap(bitmap, matrix, paint);
			}

		}
	}

	public void setColor(String color){
		colorToChange = color;
		isColorChanged = true;
	}

    public float getFitTextSize(TextPaint paint, float width, String text) {
        float nowWidth = paint.measureText(text);
        float newSize = (float) width / nowWidth * paint.getTextSize();
        return newSize;
    }

	/**
	 * Sets the text size for a Paint object so a given string of text will be a
	 * given width.
	 *
	 * @param paint
	 *            the Paint to set the text size for
	 * @param desiredWidth
	 *            the desired width
	 * @param text
	 *            the text that should be that width
	 */
	public int textSizeOriginal = 0;
    public int textSizeFromDevice = 0;
	private void setTextSizeForWidth(TextPaint paint, float desiredWidth, String text) {
		textSizeFromDevice = ctx.getResources().getDimensionPixelSize(R.dimen.TextSizeOnCanvas);
		Log.i("xxx","xxx TextSize " + textSizeFromDevice);

//        paint.setTextSize(getFitTextSize(paint, desiredWidth, text));

		// Pick a reasonably large value for the test. Larger values produce
		// more accurate results, but may cause problems with hardware
		// acceleration. But there are workarounds for that, too; refer to
		// http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
		final float testTextSize = 20f;

		// Get the bounds of the text, using our testTextSize.
//        if(textSizeOriginal > 0) {
//            paint.setTextSize(textSizeOriginal); //
//        } else {
            paint.setTextSize(testTextSize); //
//        }

		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		// Calculate the desired size as a proportion of our testTextSize.
		float desiredTextSize = testTextSize * desiredWidth / bounds.width();
		Log.i("setTextSizeForWidth ", "xxx desiredTextSize = " + desiredTextSize + " === testTextSize = " + testTextSize + " * desiredWidth = " + desiredWidth + "/ bounds.width() = " + bounds.width());
		// Set the paint for that size.
		// subtract a bit of textsize so the last letter will not be move on nextline
		int finalTextSize = (int) (desiredTextSize * 0.8);
//        if(textSizeOriginal > 0) {
//            paint.setTextSize(textSizeOriginal); //
//        } else {
            paint.setTextSize(finalTextSize); //
//            textSizeOriginal = finalTextSize; //finalTextSize
//        }

	}

	public boolean isTextChange = false;
	private void modifyText(){
		// support multiple lines
		TextPaint textPaint = new TextPaint(paintText);
//        if(!isTextChange) {
            setTextSizeForWidth(textPaint, screenImageW, text);
//        }
        int calculatedTextWidth = (int)textPaint.measureText(text, 0, text.length());
		StaticLayout sl = new StaticLayout(text, textPaint, calculatedTextWidth, Layout.Alignment.ALIGN_CENTER, 1, 1, true);
		/*
		 * use the computed textSize from function setTextSizeForWidth
		 * to be additional padding for the width and height.
		 */
		int width = (int) (sl.getWidth() + textPaint.getTextSize()); //screen width less 16 padding
		int height = (int) (sl.getHeight() + textPaint.getTextSize() / 2);
        Log.i("xxx","text width " + width + " height " + height);
		if(width <= 0 || height <= 0)
			return;

		bmOverlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		if(width > 2048 || height > 2048) {
			Log.i("xxx","xxx texture is too large");
		}
		mCanvas = new Canvas(bmOverlay);

		// Text to display
//		mCanvas.drawColor(ctx.getResources().getColor(R.color.darkred));
		mCanvas.save();
		mCanvas.translate(	(mCanvas.getWidth() / 2) - (sl.getWidth() / 2), 0);
		sl.draw(mCanvas);
		mCanvas.restore();
		if(isTextChange) {
            //center text if it doesn't appear in the screen. Normally text has only 1 character
            if(text.length() == 1) {
                RectF drawableRect = new RectF(0, 0, bmOverlay.getWidth(), bmOverlay.getHeight());
                RectF viewRect = new RectF(0, 0, screenImageW, screenImageH);
                matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
            }
        }
		bitmap = bmOverlay; // update the main bitmap
	}

	private void createText(int color, String font_style){
		Typeface typeface;

		if (font_style.contains("new_fonts/"))
			typeface = Typeface.createFromAsset(ctx.getAssets(), font_style);
		else
			typeface = Typeface.createFromFile(font_style);

		paintText.setTypeface(typeface);
		paintText.setColor(color);
		modifyText();
	}

	public void changeTextColor(int color){
		this.color = color;
		paintText.setColor(color);
		modifyText();
	}

    public void changeText(String newText){
        this.text = newText;
        this.isTextChange = true;
		Typeface typeface;

		if (font_style.contains("new_fonts/"))
			typeface = Typeface.createFromAsset(ctx.getAssets(), font_style);
		else
			typeface = Typeface.createFromFile(font_style);

		paintText.setTypeface(typeface);
		if(this.shadow)
			paintText.setShadowLayer(2.0f, 5.0f, 5.0f, Color.BLACK);
		else
			paintText.setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
		modifyText();
    }

	public boolean onTouchEvent(MotionEvent event) {
        if(!isLock) {
            mgd.onTouchEvent(event);
            sgd.onTouchEvent(event);
            rgd.onTouchEvent(event);
        }
		return false;
	}

	SimpleOnMoveGestureListener mgl = new SimpleOnMoveGestureListener() {
		@Override
		public boolean onMove(MoveGestureDetector detector) {
			PointF delta = detector.getFocusDelta();

			// update matrix translation here
			matrix.postTranslate(delta.x, delta.y);
			transX = transX + delta.x;
			transY = transY + delta.y;
			return true;
		}
	};

	SimpleOnScaleGestureListener sgl = new SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			isScaling = true;
			scaleFactor = detector.getScaleFactor();
			matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY()); //,firstTouchX, firstTouchY
			isScaling = false;
			getFocusX = detector.getFocusX();
			getFocusY = detector.getFocusY();
			return true;
		}
	};

	SimpleOnRotateGestureListener rgl = new SimpleOnRotateGestureListener() {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			Matrix m = new Matrix();
			m.preRotate(-detector.getRotationDegreesDelta(), (bitmap.getWidth())/2 , (bitmap.getHeight()) /2 );
			matrix.preConcat(m);
			rotation = rotation + -detector.getRotationDegreesDelta();
			return true;
		};
	};

	@SuppressWarnings("unused")
	private void putBitmapTopR(){
		/**
		 *  put bitmap on top left of the view.
		 *  10px is the space from the border
		 */
		float x = (logoParentView.getWidth() - (bitmap.getWidth() * getScaleMatrixFactor() + 30));
		int y = 30;//(logoParentView.getHeight() / 2) - (bitmap.getHeight() / 2);
		matrix.postTranslate(x, y);

		// initial focus
		getFocusX = x + (bitmap.getWidth() / 2 );
		getFocusY = y + (bitmap.getHeight() / 2 );
	}

    public void centerMismatchTemplate(){
        Log.i("xxx", "xxx centerMismatchTemplate");
        Matrix m = new Matrix();
        float centerX = 0;
        float centerY = 0;
        float sf = 0.7f;
        if(!isTextMode) {
            m.postScale(0.7f, 0.7f, 0, 0);
            centerX = ((-bitmap.getWidth() * sf) / 2) + (ActivityMainEditor.bW /2);
            centerY = ((-bitmap.getHeight() * sf) / 2) + (ActivityMainEditor.bH /2);
            m.postTranslate(centerX, centerY);
        } else {
            //scale only if it is text because image is already scaled.
            centerX = ((-bitmap.getWidth() * sf) / 2) + (screenImageW /2);
            centerY = ((-bitmap.getHeight() * sf) / 2) + (screenImageH /2);
            m.postScale(sf,sf);
            m.postTranslate(centerX, centerY);
        }
        m.postConcat(matrix);
        matrix.set(m); // apply new matrix to old matrix
    }

	public void putBitmapToCenter(){
		Matrix m = new Matrix();
		float centerX = 0;//((-bitmap.getWidth() * 0.7f) / 2) + (screenImageW /2);
		float centerY = 0;//((-bitmap.getHeight() * 0.7f)/ 2) + (screenImageH /2);
		float sf = 0.7f;
		if(!isTextMode) {
			m.postScale(0.7f, 0.7f, 0, 0);
			centerX = ((-bitmap.getWidth() * sf) / 2) + (screenImageW /2);
			centerY = ((-bitmap.getHeight() * sf) / 2) + (screenImageH /2);
			m.postTranslate(centerX, centerY);
            Log.i("xxx", "xxx centerX " + centerX + " centerY " + centerY);
		} else {
			//scale only if it is text because image is already scaled.
			centerX = ((-bitmap.getWidth() * sf) / 2) + (screenImageW /2);
			centerY = ((-bitmap.getHeight() * sf) / 2) + (screenImageH /2);
			m.postScale(sf,sf);
			m.postTranslate(centerX, centerY);
			Log.i("xxx", "xxx centerX " + centerX + " centerY " + centerY);
		}
		m.postConcat(matrix);
		matrix.set(m); // apply new matrix to old matrix
		Log.i("Scalefactor", "getScaleMatrixFactor " + getScaleMatrixFactor());
	}

	public void rotate0(){
        Log.i("xxx","xxx rotate0 " + -rotation);
		Matrix m = new Matrix();
		m.setRotate(-rotation, (bitmap.getWidth())/2 , (bitmap.getHeight()) /2 );
		matrix.preConcat(m);
		rotation = 0;
	}

	public void rotate90(){
		rotation = rotation + 90;
		Matrix m = new Matrix();
		m.preRotate(90, (bitmap.getWidth())/2 , (bitmap.getHeight()) /2 );
		matrix.preConcat(m);
	}

	/**
	 *
	 * @return calculate real scale factor of the matrix.
	 */
	public float getScaleMatrixFactor(){
		float[] values = new float[9];
		matrix.getValues(values);
		float scalex = values[Matrix.MSCALE_X];
		float skewy = values[Matrix.MSKEW_Y];
		float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
		return rScale;
	}

	public void setResizeValue(float val) {

        RectF r = new RectF();
        matrix.mapRect(r);

        matrix.reset();
		Matrix m2 = new Matrix();
		float centerX;
		float centerY;
		float sf = (val-30) / 100; //we subtract 30  of the current value(base on 100) because we scale 0.7 in the first placement of canvas (0.7f).
		centerX = ((-bitmap.getWidth() * sf) / 2) + (screenImageW / 2);
		centerY = ((-bitmap.getHeight() * sf) / 2) + (screenImageH / 2);

        //set scale
		m2.postScale(sf, sf);
		m2.postTranslate(centerX, centerY);
		matrix.preConcat(m2);

        //set rotation
        Matrix m3 = new Matrix();
        m3.preRotate(rotation, (bitmap.getWidth())/2 , (bitmap.getHeight()) /2 );
        matrix.preConcat(m3);
	}

}