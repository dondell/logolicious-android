package com.olav.logolicious.customize.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DynamicImageView extends ImageView {
	
	public static int measureWidth = 0;
	public static int measureHeight = 0;
	
//	http://stackoverflow.com/questions/13992535/android-imageview-scale-smaller-image-to-width-with-flexible-height-without-crop
	public DynamicImageView(Context context) {
        super(context);
    }
	
	public DynamicImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }
    
    public DynamicImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    int parentWidth;
    int parentHeight;
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
        	measureWidth = width;
        	measureHeight = height;
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

//        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
//        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(parentWidth / 2, parentHeight / 2);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}