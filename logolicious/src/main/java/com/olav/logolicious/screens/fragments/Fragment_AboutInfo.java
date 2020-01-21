package com.olav.logolicious.screens.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.util.GlobalClass;

public class Fragment_AboutInfo extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.about_info_credits, container, false);
		setHasOptionsMenu(false);
		final TextView version = (TextView) rootView.findViewById(R.id.version);
		final CheckBox showTipsNextime = (CheckBox) rootView.findViewById(R.id.showTipNextime);
		PackageInfo packageInfo = null;
		try {
			packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			version.setText("Version " + packageInfo.versionName);
		} catch (PackageManager.NameNotFoundException e2) {
			version.setText("Version is up to date");
		}
		
		if (GlobalClass.sqLiteHelper.isShowHint() == 1) {
			showTipsNextime.setChecked(true);
		} else {
			showTipsNextime.setChecked(false);
		}

		showTipsNextime.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
					if (isChecked) {
						GlobalClass.sqLiteHelper.updateHint(1,1);
					} else {
						GlobalClass.sqLiteHelper.updateHint(1,0);
					}	
			}
		});
		
		
		return rootView;
	}

}