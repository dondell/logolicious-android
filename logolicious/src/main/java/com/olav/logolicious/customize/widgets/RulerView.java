package com.olav.logolicious.customize.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RulerView extends View {
//	http://www.mxtutorial.com/2014/02/drawing-graphics-on-android-using-view.html
    Paint paint = new Paint();
    static final private float pxinch = 500 / 67.f * 25.4f / 16;
    float width, height;

    public RulerView(Context context, AttributeSet foo) {
        super(context, foo);
        setBackgroundColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        paint.setAntiAlias(false);
        paint.setColor(Color.WHITE);
    }

    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;
    }

    public void onDraw(Canvas c) {
    	//0xffffffff background
//        c.drawColor(Color.BLUE); 
    	// add strips like a ruler
//    	int startX = 0;
//    	int startY = 2;
//    	int stopX = 0;
//    	int stopY = 5;
        for (int i = 0; ; ++i) {
            float x = (float) (i * (pxinch * 2.5)); // multiple pxinch * 2 to adjust to pixel
            if (x > 3000) {
                break;
            }
            int size = (i%2==0) ? 8 : 14;
            c.drawLine(x, 0, x, size, paint);
        }
        super.onDraw(c);
    }
}
;