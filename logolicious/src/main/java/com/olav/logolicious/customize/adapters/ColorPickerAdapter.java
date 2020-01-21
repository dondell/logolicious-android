package com.olav.logolicious.customize.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olav.logolicious.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends BaseAdapter {

	private Context context;
	// list which holds the colors to be displayed
	public static List<String> colorList = new ArrayList<String>();
	// width of grid column
	int colorGridColumnWidth;

	public ColorPickerAdapter(Context context) {
		this.context = context;

		// defines the width of each color square
		colorGridColumnWidth = context.getResources().getInteger(R.integer.colorGridColumnWidth);

		// for convenience and better reading, we place the colors in a two dimension array
		String colors[][] = {
				{ "9f1f66","1baae4","3ab24b","feaf45","be1e2e" }, 
				{ "ed297d","1474c1","08663b","f9eb30","f23b37" },
				{ "ffffff","28398f","2bb770","cee31c","f0592a" }, 
				{ "000000","642c8f","05aa9d","8cc644","f69220" }
				};

		colorList = new ArrayList<String>();

		// add the color array to the list
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < colors[i].length; j++) {
				colorList.add("#" + colors[i][j]);
			}
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
//		ImageView imageView;
		View gridView;

		// can we reuse a view?
		if (convertView == null) {
			
			gridView = new View(context);
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.grid_color_item, (ViewGroup)null);
			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_color);
//			imageView = new ImageView(context);
			// set the width of each color square
			imageView.setLayoutParams(new LinearLayout.LayoutParams(colorGridColumnWidth, colorGridColumnWidth));
			// check if last color item is reach
			// if true then replace it with no color icon
			if(Color.parseColor(colorList.get(position)) == Color.parseColor("#ffffff")){
				imageView.setBackgroundResource(R.drawable.no_color);
			} else {
				imageView.setBackgroundColor(Color.parseColor(colorList.get(position)));	
			}
			imageView.setId(position);
			
			// set value into textview
			TextView textView = (TextView) gridView.findViewById(R.id.grid_item_color_label);
			textView.setText(colorList.get(position));
		} else {
//			imageView = (ImageView) convertView;
			gridView = (View) convertView;
		}

		return gridView;
	}

	public int getCount() {
		return colorList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
}
