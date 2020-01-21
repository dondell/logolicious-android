package com.olav.logolicious.util.camera;

/**
 * Class to hold the display dimensions and orientation information 
 * 
 * @author SHRISH
 *
 */
public class ScreenDimensions {
	public int orientation;
	int displayWidth;
	int displayHeight;
	public double aspectratio;
	
	public int getOrientation() {
		return orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	public int getDisplayWidth() {
		return displayWidth;
	}
	public void setDisplayWidth(int displayWidth) {
		this.displayWidth = displayWidth;
	}
	public int getDisplayHeight() {
		return displayHeight;
	}
	public void setDisplayHeight(int displayHeight) {
		this.displayHeight = displayHeight;
	}		
}
