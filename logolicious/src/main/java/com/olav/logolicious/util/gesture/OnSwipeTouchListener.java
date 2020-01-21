package com.olav.logolicious.util.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class OnSwipeTouchListener implements OnTouchListener{
	
	private GestureDetector gestureDetector;
	private Context ctx;

	public OnSwipeTouchListener(Context ctx){
		this.ctx = ctx;
		gestureDetector = new GestureDetector(ctx, new GestureListener());
	}
	
	private final class GestureListener extends SimpleOnGestureListener{
		
		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;
		
		@SuppressWarnings("unused")
		public void OnSwipeTouchListener(Context ctx){
			gestureDetector = new GestureDetector(ctx, new GestureListener());
		}
		
		@Override 
		public boolean onDown(MotionEvent event){
			return false;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			boolean result = false;
			try{
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if(Math.abs(diffX) > Math.abs(diffY)){
					if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
						if(diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
					}
					result = true;
				}else if(Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
					if(diffY > 0) {
						onSwipeBottom();
					} else {
						onSwipeTop();
					}
				}
				result = true;
			} catch(Exception exception){
				exception.printStackTrace();
			}
			return result;
		}
		
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

	public void onSwipeRight() {
		// TODO Auto-generated method stub
		Toast.makeText(ctx, "swipe right", Toast.LENGTH_LONG).show();
	}

	public void onSwipeLeft() {
		// TODO Auto-generated method stub
		
	}
	
	public void onSwipeBottom() {
		// TODO Auto-generated method stub
		
	}
	
	public void onSwipeTop() {
		// TODO Auto-generated method stub
		
	}

}
