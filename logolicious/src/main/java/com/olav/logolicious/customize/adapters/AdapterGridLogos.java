package com.olav.logolicious.customize.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.olav.logolicious.R;
import com.olav.logolicious.util.GlobalClass;

import java.io.File;
import java.util.ArrayList;

public class AdapterGridLogos extends BaseAdapter {
	private Context context;
	private final ArrayList<ArrayHolderLogos> images;
	ImageLoader imageLoader;
	private int resLayId;

	public AdapterGridLogos(Context context, ArrayList<ArrayHolderLogos> images, int resLayId) {
		this.context = context;
		this.images = images;
		this.imageLoader = GlobalClass.imageLoader;
		this.resLayId = resLayId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);
			gridView = inflater.inflate(resLayId, (ViewGroup) null);

			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			String imgUri = Uri.fromFile(new File(images.get(position).getItemPath())).toString();
			imageLoader.displayImage(imgUri, imageView);
		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return images.get(position).getItemPath();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}