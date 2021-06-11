package com.olav.logolicious.screens.fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.Activity_Tip;
import com.olav.logolicious.util.GlobalClass;

public class TipMain extends Fragment {

	SharedPreferences preferences;
	boolean isShowHint = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tip_first, container, false);

		ImageView imageView1 = (ImageView) v.findViewById(R.id.imageView1);
		Glide.with(GlobalClass.getAppContext()).load(R.drawable.tip_start).into(imageView1);

		ImageButton btnOk = (ImageButton) v.findViewById(R.id.buttonOk);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity_Tip) getActivity()).pager.setCurrentItem(1);
			}
		});

		ImageButton buttonSkip = (ImageButton) v.findViewById(R.id.buttonSkip);
		buttonSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity_Tip) getActivity()).finish();
			}
		});

		CheckBox checkBoxHint = (CheckBox) v.findViewById(R.id.showTipNextime);
		checkBoxHint.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					GlobalClass.sqLiteHelper.updateHint(1, 0);
				} else {
					GlobalClass.sqLiteHelper.updateHint(1, 1);
				}
			}
		});

		return v;
	}

	public static TipMain newInstance(String text) {

		TipMain f = new TipMain();
		Bundle b = new Bundle();
		b.putString("msg", text);

		f.setArguments(b);

		return f;
	}

	public void skip(View v) {
		this.getActivity().finish();
	}

}
