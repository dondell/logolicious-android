package com.olav.logolicious.customize.adapters;

public class AdapterFontDetails {
	
	private String fontName;
	private String fontType;
	private String fontSDCardPath;
	private boolean isExternal = false;

	public AdapterFontDetails(String fontName, String fontType) {
		this.setFontName(fontName);
		this.setFontType(fontType);
	}

	public AdapterFontDetails(String fontName, String fontType, String fontSDCardPath, boolean isExternal) {
		this.fontName = fontName;
		this.fontType = fontType;
		this.fontSDCardPath = fontSDCardPath;
		this.isExternal = isExternal;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFontType() {
		return fontType;
	}

	public void setFontType(String fontType) {
		this.fontType = fontType;
	}

	public String getFontSDCardPath() {
		return fontSDCardPath;
	}

	public void setFontSDCardPath(String fontSDCardPath) {
		this.fontSDCardPath = fontSDCardPath;
	}

	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean external) {
		isExternal = external;
	}
}
