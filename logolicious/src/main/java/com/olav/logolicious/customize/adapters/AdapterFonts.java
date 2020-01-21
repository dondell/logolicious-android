package com.olav.logolicious.customize.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.olav.logolicious.R;

import java.util.ArrayList;

public class AdapterFonts extends BaseAdapter {
	private Context context;
	private final ArrayList<AdapterFontDetails> fonts;
	int layoutResourceId;

	public AdapterFonts(Context context, int layoutResourceId, ArrayList<AdapterFontDetails> fonts) {
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.fonts = fonts;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view;

		if (convertView == null) {

			view = new View(context);
			view = inflater.inflate(this.layoutResourceId, (ViewGroup)null);

		} else {
			view = (View) convertView;
		}
		
		// set image based on selected text
		TextView fontTextView = view.findViewById(R.id.fontStyle);
		Typeface face;
		if(fonts.get(position).isExternal()) {
			face = Typeface.createFromFile(fonts.get(position).getFontSDCardPath().replace("/mimetype//", ""));
			fontTextView.setTypeface(face);
			fontTextView.setText(fonts.get(position).getFontName());
		} else {
			face = Typeface.createFromAsset(context.getAssets(), fonts.get(position).getFontType());
			fontTextView.setTypeface(face);
			fontTextView.setText(fonts.get(position).getFontName());
		}

		return view;
	}

	@Override
	public int getCount() {
		return fonts.size();
	}

	@Override
	public Object getItem(int position) {
		return fonts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}