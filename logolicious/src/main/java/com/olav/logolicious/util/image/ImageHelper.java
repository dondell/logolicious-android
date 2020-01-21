package com.olav.logolicious.util.image;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.SubscriptionUtil.AppStatitics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageHelper {
	
	public static boolean isPortrait(float w, float h){
    	boolean flag = false;
    	if(w < h){
    		flag = true;
    	}
    	return flag;
    }

	/**
	 * 
	 * @param bitmap
	 *            The bitmap to clear
	 */
	public static void clearBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}
	}

	private static int truncate(int value) {
		if (value < 0) {
			return 0;
		} else if (value > 255) {
			return 255;
		}

		return value;
	}

	public static Bitmap bitmap(int angle, Bitmap bitmapSrc, Matrix matrix) {

		matrix.postRotate(angle);
		return Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(),
				bitmapSrc.getHeight(), matrix, true);
	}

	/**
	 * 
	 * @param bitmap
	 *            The bitmap to scale.
	 * @param MIN_WIDTH
	 *            The requested width of the bitmap.
	 * @param MIN_HEIGHT
	 *            The requested height of the bitmap.
	 * @return This will scale the bitmap to the requested width and height
	 *         without violating the aspect ration of the bitmap.
	 */
	public static Bitmap scaleWithRespectToAspectRatio(Bitmap bitmap, float MIN_WIDTH, float MIN_HEIGHT) {
		Matrix m = new Matrix();
		m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, MIN_WIDTH, MIN_HEIGHT), Matrix.ScaleToFit.CENTER);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),	bitmap.getHeight(), m, false);
	}
	
	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options); 
	}
	
	public static Bitmap decodeSampledBitmapFromPathb(String path, int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    options.inMutable = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options); 
	}
	
	public static Bitmap decodeLogo(String path, int insample, int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = false;
	    options.inScaled = true;
	    options.inSampleSize = 2;
	    options.inDensity = reqWidth;
	    options.inTargetDensity =  reqWidth * options.inSampleSize;

	    return BitmapFactory.decodeFile(path, options); 
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	public static Bitmap decodeBitmapPath(String bitmapPath){
		BitmapFactory.Options options;
		try {
		  Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
		  return bitmap;
		} catch (OutOfMemoryError e) {
			Log.i("", "xxx outofmemory in ImageHelper decodeBitmapPath");
			FileUtil.fileWrite(GlobalClass.log_path, "OutOfMemoryError in ImageHelper function decodeBitmapPath", true);
			FileUtil.fileWrite(GlobalClass.log_path, "->>Now using the alternative inSampleSize = 3 in order to avoid OOM", true);
            AppStatitics.sharedPreferenceSet(ActivityMainEditor.act, "hasOOM", 1);
		  try {
		    options = new BitmapFactory.Options();
		    options.inSampleSize = 2;
		    Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		    FileUtil.fileWrite(GlobalClass.log_path, "->>Success using the alternative", true);
		    return bitmap;
		  } catch(Exception ex) {
			  FileUtil.fileWrite(GlobalClass.log_path, "->>Error using the alternative", true);
		    ex.printStackTrace();
		  }
		}
		return null;
	}

	public static Drawable resizeDrawable(Resources res, Drawable image, int h,
			int w) {
		Bitmap b = ((BitmapDrawable) image).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(b, w, h, false);
		return new BitmapDrawable(res, bitmapResized);
	}
    
    /**
     * d
     * @param photoPath
     * @return
     */
    public static Bitmap correctBitmapRotation(String photoPath, Bitmap bitmap){
    	ExifInterface ei = null;
		try {
			ei = new ExifInterface(photoPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(null == ei)
		    return bitmap;

    	int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
    	                                     ExifInterface.ORIENTATION_NORMAL);

    	switch(orientation) {

    	    case ExifInterface.ORIENTATION_ROTATE_90:
    	    	Log.i("", "xxx camera ORIENTATION_ROTATE_90");
    	        return rotateImage(bitmap, 90);
    	    case ExifInterface.ORIENTATION_ROTATE_180:
    	    	Log.i("", "xxx camera ORIENTATION_ROTATE_180");
    	    	return rotateImage(bitmap, 180);
    	    case ExifInterface.ORIENTATION_ROTATE_270:
    	    	Log.i("", "xxx camera ORIENTATION_ROTATE_270");
    	    	return rotateImage(bitmap, 270);
    	    case ExifInterface.ORIENTATION_NORMAL:
    	    	Log.i("", "xxx camera ORIENTATION_NORMAL");
    	    	return bitmap;
    	    default:
    	    	Log.i("", "xxx camera Default");
    	    	return bitmap;
    	    	
    	}
    }
    
    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    public static Bitmap putLockedOnBitmap(Context ctx, Bitmap bitmap) {
        Bitmap bitmapWithLocked = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Bitmap bitmapLocked = scaleWithRespectToAspectRatio(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.lock),
                (bitmapWithLocked.getHeight() / 2), (bitmapWithLocked.getWidth() / 2));

        Canvas mCanvas = new Canvas(bitmapWithLocked);
        mCanvas.drawBitmap(bitmap, 0, 0, null);
        int wCenter = (bitmap.getWidth() / 2) - (bitmapLocked.getWidth() / 2);
        int hCenter = (bitmap.getHeight() / 2) - (bitmapLocked.getHeight() / 2);

        // create a paint instance with alpha
        Paint alphaPaint = new Paint();
        mCanvas.drawBitmap(bitmapLocked, wCenter , hCenter , alphaPaint);
        return bitmapWithLocked;
    }

	public static Bitmap addShadow(final Bitmap bm, final int dstHeight, final int dstWidth, int color, int size, float dx, float dy) {
		final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Config.ALPHA_8);

		final Matrix scaleToFit = new Matrix();
		final RectF src = new RectF(0, 0, bm.getWidth(), bm.getHeight());
		final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
		scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

		final Matrix dropShadow = new Matrix(scaleToFit);
		dropShadow.postTranslate(dx, dy);

		final Canvas maskCanvas = new Canvas(mask);
		final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		maskCanvas.drawBitmap(bm, scaleToFit, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
		maskCanvas.drawBitmap(bm, dropShadow, paint);

		final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
		paint.reset();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setMaskFilter(filter);
		paint.setFilterBitmap(true);

		final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Config.ARGB_8888);
		final Canvas retCanvas = new Canvas(ret);
		retCanvas.drawBitmap(mask, 0,  0, paint);
		retCanvas.drawBitmap(bm, scaleToFit, null);
		mask.recycle();
		return ret;
	}

	/**
     *
     * Starting this line are the old functions we use from the previous versions which are not use.
     * /

    //	public Bitmap autoRotate(Bitmap bitmap) {
//
//		if (null == bitmap)
//			return null;
//
//		int cameraInfoRotation = -1;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			cameraInfoRotation = mCamUtils.getRotation(Still.this);
//		}
//		// Adjust display mode
//		Matrix matrix = new Matrix();
//		Display display = ((WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		int nRotation = display.getRotation();
//		if(cameraInfoRotation != -1) {
//			nRotation = cameraInfoRotation;
//			mDeviceRotation = nRotation;
//		}
//
//		/*
//		 * do not rotate and adjust the orientation of the image when on tablet
//		 * since the natural orientation of tablet is in landscape and
//		 * consistent with PocketTracker's perspective and user's perspective
//		 */
//		//During our testing, somehow on phones, it is always 90 degree off. So here if it is phone, we rotate 90 degree back.
//		if(!this.isTablet) {
//			if(90 == nRotation)
//				nRotation = nRotation - 90;
//		}
//
//		boolean hasUserPref = false;
//		//If user has its own value, it will return user's value. Otherwise, it will return this value.
//		if(0 == mDeviceRotation) {
//			nRotation = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_0_ROTATION, nRotation);
//			hasUserPref = true;
//		} else if(90 == mDeviceRotation) {
//			nRotation = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_90_ROTATION, nRotation);
//			hasUserPref = true;
//		} else if(180 == mDeviceRotation) {
//			nRotation = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_180_ROTATION, nRotation);
//			hasUserPref = true;
//		}
//		nRotation = nRotation % 360;
//		//Check if the image needs further orientation correction
//		if(nRotation == mDeviceRotation && !hasUserPref)
//			return bitmap;
//
//		matrix.postRotate(nRotation);
//		Bitmap checkBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//		//check the final bitmap for 90 degree orientation
//		int w = 0, h = 0;
//		if(90 == mDeviceRotation && !hasUserPref) {
//			w = checkBitmap.getWidth();
//			h = checkBitmap.getHeight();
//			//result not 90degree so we add 90 degree
//			if(w > h) {
//				matrix.postRotate(nRotation + 90);
//				checkBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//			}
//		}
//		return checkBitmap;
//	}
//
//	public boolean rotateImage(String strImagePath, int nRotation,  boolean bSaveToPerference)
//	{
//		if(!SystemInfo.fileExist(strImagePath))
//			return false;
//
//		try
//		{
//			Bitmap bitmap = BitmapFactory.decodeFile(strImagePath);
//
//			Matrix matrix = new Matrix();
//			matrix.postRotate(nRotation);
//
//			Bitmap bitNew = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//			FileOutputStream fOut;
//			String strTempFileName = strImagePath + ".temp";
//
//			fOut = new FileOutputStream(strTempFileName);
//			bitNew.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
//			fOut.flush();
//			fOut.close();
//
//			SystemInfo.fileCopy(strTempFileName, strImagePath);
//			SystemInfo.fileDelete(strTempFileName);
//
//			if(bSaveToPerference)
//			{
//				if(0 == mDeviceRotation) {
//					int nCurSetting = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_0_ROTATION, 0);
//					SystemInfo.sharedPreferenceSet(this, Still.PREF_IMAGE_0_ROTATION, (nCurSetting + nRotation) % 360);
//				} else if(90 == mDeviceRotation) {
//					int nCurSetting = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_90_ROTATION, 0);
//					SystemInfo.sharedPreferenceSet(this, Still.PREF_IMAGE_90_ROTATION, (nCurSetting + nRotation) % 360);
//				} else if(180 == mDeviceRotation) {
//					int nCurSetting = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_180_ROTATION, 0);
//					SystemInfo.sharedPreferenceSet(this, Still.PREF_IMAGE_180_ROTATION, (nCurSetting + nRotation) % 360);
//				}
////	        	else {
////	        		int nCurSetting = SystemInfo.sharedPreferenceGet(this, Still.PREF_IMAGE_AUTO_ROTATION, 0);
////		        	SystemInfo.sharedPreferenceSet(this, Still.PREF_IMAGE_AUTO_ROTATION, (nCurSetting + nRotation) % 360);
////	        	}
//			}
//			return true;
//
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle, source.getWidth() / 2, source.getHeight() / 2);
        Bitmap finalB = null;
//		if(angle == 90){
//			matrix.postTranslate(100, 100);
        finalB = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//		} else {
//			finalB = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//		}

        return finalB;
    }

    public static Bitmap replaceBitmapColor(Bitmap bitmap) {
        // Remove the black bg to transparent
        int[] allpixels = new int[bitmap.getHeight() * bitmap.getWidth()];

        bitmap.getPixels(allpixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < allpixels.length; i++) {
            if (allpixels[i] == Color.BLACK) {
                allpixels[i] = Color.TRANSPARENT;
            }
        }
        bitmap.setPixels(allpixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        return bitmap;
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }

    public static Bitmap createBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height);
        return resizedBitmap;
    }

    public static int FLIP_VERTICAL = 1;
    public static int FLIP_HORIZONTAL = 2;
    public static Bitmap flip(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if(type == FLIP_HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap decodeLogo(String bitmapPath){
        BitmapFactory.Options options;
        try {
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
                Log.i("", "xxx decoding logo in ImageHelper decodeLogo path = " + bitmapPath);
                return bitmap;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            Log.i("", "xxx outofmemory in ImageHelper decodeLogo");
            FileUtil.appendLog(GlobalClass.log_path, "OutOfMemoryError in ImageHelper function decodeLogo");
            FileUtil.appendLog(GlobalClass.log_path, "Now using the alternative inSampleSize = 4");
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
                return bitmap;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap decodeBitmapPathLowRes(String bitmapPath){
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
            FileUtil.fileWrite(GlobalClass.log_path, "->>Success using the alternative", true);
            return bitmap;
        } catch(Exception ex) {
            FileUtil.fileWrite(GlobalClass.log_path, "->>Error using the alternative", true);
            ex.printStackTrace();
        }
        return null;
    }

    public static Bitmap scaleWithRespectToAspectRatio(Bitmap bitmap, Matrix m, float MIN_WIDTH, float MIN_HEIGHT) {
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp,
                                                        float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[] { contrast, 0, 0, 0,
                brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast, 0,
                brightness, 0, 0, 0, 1, 0 });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
                bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }

    public static Bitmap cutBackground(Context context, Bitmap bitmap,
                                       int deviceWidth, int deviceHeight) {
        // Toast.makeText(context, "bitmap width " + bitmap.getWidth(),
        // Toast.LENGTH_SHORT).show();
        // Toast.makeText(context, "measureWidth  " +
        // DynamicImageView.measureWidth, Toast.LENGTH_SHORT).show();

        // Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
        // bitmap.getHeight());

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,
                // dialog assume size W & H
                deviceWidth, deviceHeight - 2, false);

        Canvas canvas = new Canvas(scaled);
        canvas.drawBitmap(scaled, 0, 0, null);
        // canvas.drawBitmap(bitmap, 0, 0, null);
        return scaled;
    }

    public static Bitmap cutOverlay(Context context, Bitmap bitmap,
                                    int widthToFit, int deviceWidth, int deviceHeight) {
        // crop the bitmap
        /**
         * the bitmap is larger than the layrPersonPhoto so we get the
         * bitmap-withToFit to get the amount to deduct from bitmap width to
         * exact the width of the bitmap
         */

        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, widthToFit,
                bitmap.getHeight());

        Bitmap scaled = Bitmap.createScaledBitmap(cropped, deviceWidth,
                deviceHeight, false);

        Canvas canvas = new Canvas(scaled);
        // canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(scaled, 0, 0, null);
        return scaled;
    }

    public static Bitmap copyImageBottomPartToMainImage(Bitmap bitmap1,
                                                        Bitmap bitmap2) {

        return null;
    }

    // public BufferedImage getCroppedImage(BufferedImage source, double
    // tolerance) {
    // // Get our top-left pixel color as our "baseline" for cropping
    // int baseColor = source.getRGB(0, 0);
    //
    // int width = source.getWidth();
    // int height = source.getHeight();
    //
    // int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
    // int bottomY = -1, bottomX = -1;
    // for(int y=0; y<height; y++) {
    // for(int x=0; x<width; x++) {
    // if (colorWithinTolerance(baseColor, source.getRGB(x, y), tolerance)) {
    // if (x < topX) topX = x;
    // if (y < topY) topY = y;
    // if (x > bottomX) bottomX = x;
    // if (y > bottomY) bottomY = y;
    // }
    // }
    // }
    //
    // BufferedImage destination = new BufferedImage( (bottomX-topX+1),
    // (bottomY-topY+1), BufferedImage.TYPE_INT_ARGB);
    //
    // destination.getGraphics().drawImage(source, 0, 0,
    // destination.getWidth(), destination.getHeight(),
    // topX, topY, bottomX, bottomY, null);
    //
    // return destination;
    // }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap scaleAndRotateImage(float scale, float a, float b,
                                             int angle, Bitmap bmp, Matrix matrix) {
        matrix.postRotate(angle);
        matrix.postScale(scale, scale, a, b);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
    }

    public static int dpToPx(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public static Bitmap adjustContrast(Bitmap src, int value) {
        // src image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap with original size
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.red(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.red(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    public static Bitmap applyContrast(Bitmap image, int contrastVal) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final Bitmap contrastedImage = Bitmap.createBitmap(width, height,
                image.getConfig());

        int A, R, G, B;
        int pixel;

        double contrast = Math.pow((100 + contrastVal) / 100, 2);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = image.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                R = truncate(R);

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                G = truncate(G);

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                B = truncate(B);

                contrastedImage.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return contrastedImage;
    }

    public static Bitmap applySaturationFilter(Bitmap source, float level) {
        // get original image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        // get pixel array from source image
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through all pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[1] *= 0.2;
                HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
                // take color back
                pixels[index] = Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(pixels, pixels, pixels, pixels);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap drawCornerLineBitmap(Bitmap bitmap, int color,
                                              int cornerDips, int borderDips, Context context) {

        System.out.println("in rounded corner bitmap: " + color);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth() + 10,
                bitmap.getHeight() + 10, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth() + borderDips,
                bitmap.getHeight() + borderDips);
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, 3, 3, paint);

        // draw line
        // canvas.drawLine(10, 50, 10, 50, paint);

        // draw border
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        // //left, top, right, bottom, paint
        canvas.drawRect(400, 400, 100, 100, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap changeImageViewDrawableColor(Bitmap bitmap, String color) {
        // change bitmap color
        // http://stackoverflow.com/questions/5699810/how-to-change-bitmap-image-color-in-android
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.parseColor(color), Mode.MULTIPLY);// LightingColorFilter(color, 0)
        p.setColorFilter(filter);
        p.setAlpha(200); // you can set your transparent value here 256 is the max
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, 0, 0, p);
        // log
        FileUtil.appendLog(GlobalClass.log_path,
                "changeImageViewDrawableColor function in ImageHelper Class");
        return mutableBitmap;
    }

}