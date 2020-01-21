package com.olav.logolicious.customize.adapters;

public class ArrayHolderLogos {

	private String name;
	private String itemPath;

	// Constructor or Setter
	/**
	 * 
	 * @param prefix
	 *            Category/Extension Prefix
	 * @param itemPath
	 *            Item individual path
	 * @return This method is used to stored item prefix and path.
	 */
	public ArrayHolderLogos(String name, String itemPath) {
		this.setName(name);
		this.setItemPath(itemPath);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemPath() {
		return itemPath;
	}

	public void setItemPath(String itemPath) {
		this.itemPath = itemPath;
	}

}