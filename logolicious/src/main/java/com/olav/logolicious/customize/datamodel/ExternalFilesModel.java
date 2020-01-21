package com.olav.logolicious.customize.datamodel;

public class ExternalFilesModel {

	private int icon;
	private String title;
	private String counter;
	private String path;
	private boolean isImage;

	private boolean isGroupHeader = false;

	public ExternalFilesModel(String title) {
//		this(-1, title);
		isGroupHeader = true;
	}

	public ExternalFilesModel(int icon, String title, String path, boolean isImage) {
		super();
		this.icon = icon;
		this.title = title;
		this.path = path;
		this.isImage = isImage;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}
	
	public boolean isGroupHeader() {
		return isGroupHeader;
	}

	public void setGroupHeader(boolean isGroupHeader) {
		this.isGroupHeader = isGroupHeader;
	}
	
}
