package com.olav.logolicious.customize.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.olav.logolicious.R;
import com.olav.logolicious.customize.datamodel.ExternalFilesModel;
import com.olav.logolicious.screens.activities.ExternalFilesActivity;
import com.olav.logolicious.util.FileUtil;

import java.io.File;
import java.util.ArrayList;


public class ExternalFilesAdapter extends ArrayAdapter<ExternalFilesModel> {

	private final Context context;
	private final ArrayList<ExternalFilesModel> modelsArrayList;
	ImageLoader imageLoader;
	private int mode;

	public ExternalFilesAdapter(Context context, ArrayList<ExternalFilesModel> modelsArrayList, ImageLoader imageLoader, int mode) {
		super(context, R.layout.files_image_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
		this.imageLoader = imageLoader;
		this.mode = mode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1. Create inflater
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 2. Get rowView from inflater
		View rowView = null;
		if (!modelsArrayList.get(position).isGroupHeader()) {
//			if(mode == ExternalFilesActivity.MODE_IMAGE_BATCHING){
				rowView = inflater.inflate(R.layout.files_image_item, parent, false);
				// 3. Get icon,title & counter views from the rowView
				ImageView imgView = (ImageView) rowView.findViewById(R.id.imagePreview);
				TextView titleView = (TextView) rowView.findViewById(R.id.rowFiles);
				CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBoxFileBrowser);
				if(FileUtil.isFile(modelsArrayList.get(position).getPath()))
					cb.setVisibility(View.VISIBLE);
				else
					cb.setVisibility(View.INVISIBLE);
				
				// decide either to check or not
				if(ExternalFilesActivity.isSelectedPictureExist(ExternalFilesActivity.array_selected, modelsArrayList.get(position).getPath())){
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}
				
				if(modelsArrayList.get(position).isImage() == true) {
					String imgUri = Uri.fromFile(new File(modelsArrayList.get(position).getPath())).toString();
					imageLoader.displayImage(imgUri, imgView);
				} else {
					imgView.setImageResource(modelsArrayList.get(position).getIcon());
				}
				// 4. Set the text for textView
				titleView.setSingleLine(true);
				titleView.setEllipsize(TruncateAt.START);
				titleView.setText(modelsArrayList.get(position).getTitle());
//			} else if (mode == ExternalFilesActivity.MODE_LOGO_UPLOADING){
//				rowView = inflater.inflate(R.layout.files_logo_item, parent, false);
//			}
			
		} else {
			rowView = inflater.inflate(R.layout.group_header_item, parent, false);
			TextView titleView = (TextView) rowView.findViewById(R.id.header);
			titleView.setText(modelsArrayList.get(position).getTitle());
		}

		// 5. return rowView
		return rowView;
	}
	
}