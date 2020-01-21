package com.olav.logolicious.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StringUtil {

	private static ArrayList<String> array_splittedStrings = new ArrayList<String>();

	public static ArrayList<String> splitStr(String str, String delimeter) {
		StringTokenizer st = new StringTokenizer(str, delimeter);
		ArrayList<String> array = new ArrayList<String>();
		int i = 0;
		while (st.hasMoreTokens()) {
			String data = "" + st.nextElement();
			array.add(i, data);
			i++;
		}

		array_splittedStrings = array;
		return array;
	}

	/**
	 * @return the array_splittedStrings
	 */
	public static ArrayList<String> getArray_splittedStrings() {
		return array_splittedStrings;
	}

	/**
	 * @param aArray_splittedStrings
	 *            the array_splittedStrings to set
	 */
	public static void setArray_splittedStrings(
			ArrayList<String> aArray_splittedStrings) {
		array_splittedStrings = aArray_splittedStrings;
	}

	public static String unescape(String description) {
		return description.replaceAll("\\\\n", "\\n");
	}

}
