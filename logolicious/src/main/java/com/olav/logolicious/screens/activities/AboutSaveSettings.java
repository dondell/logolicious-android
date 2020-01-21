package com.olav.logolicious.screens.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;

public class AboutSaveSettings extends Activity {

	CheckBox type1 = null;
	CheckBox type2 = null;
	CheckBox type3 = null;
	EditText fileName = null;
	Button saveFileName = null;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_saving_setting);
        //this.setFinishOnTouchOutside(false);
		
		preferences = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
		editor = preferences.edit();
		
		WebView note = (WebView)findViewById(R.id.notes);
		note.setBackgroundColor(Color.TRANSPARENT);
		String text = "<html><body style='text-align:justify;color: #fff;margin: 0px;padding: 0px;'>"                 
				+ "Note: Leave empty for automatic file name<br>      (we never overwrite originals!)"
				+ "</body></html>";
		note.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
		
		type1 = (CheckBox)findViewById(R.id.type1);
		type1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				type1.setChecked(true);
				type2.setChecked(false);
				type3.setChecked(false);
				
				LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_HR_PNG;
				LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_HR_PNG;
				editor.putString("SavingType", LogoliciousApp.TYPE_HR_PNG);
				editor.commit();
			}
		});
		type2 = (CheckBox)findViewById(R.id.type2);
		type2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				type1.setChecked(false);
				type2.setChecked(true);
				type3.setChecked(false);

				LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_JPG_HQ;
				editor.putString("SavingType", LogoliciousApp.TYPE_JPG_HQ);
				editor.commit();
			}
		});
		type3 = (CheckBox)findViewById(R.id.type3);
		type3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				type1.setChecked(false);
				type2.setChecked(false);
				type3.setChecked(true);

				LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_JPG_L;
				LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_JPG_L;
				editor.putString("SavingType", LogoliciousApp.TYPE_JPG_L);
				editor.commit();
			}
		});
		
		String type = preferences.getString("SavingType", "JPG_HQ");
		LogoliciousApp.SAVING_TYPE = type;
		if(type.matches(LogoliciousApp.TYPE_HR_PNG)) {
			type1.setChecked(true);
			type2.setChecked(false);
			type3.setChecked(false);
		} else if(type.matches(LogoliciousApp.TYPE_JPG_HQ)) {
			type1.setChecked(false);
			type2.setChecked(true);
			type3.setChecked(false);
		} else if(type.matches(LogoliciousApp.TYPE_JPG_L)) {
			type1.setChecked(false);
			type2.setChecked(false);
			type3.setChecked(true);
		}
		
		CheckBox promptalways = (CheckBox)findViewById(R.id.promptalways);
		promptalways.setChecked(preferences.getBoolean("dontAskMeAgain", true));

		promptalways.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton cb, boolean flag) {
				if(true == flag) {
					editor.putBoolean("dontAskMeAgain", true);
					editor.commit();
				}
				else {
					editor.putBoolean("dontAskMeAgain", false);
					editor.commit();
				}
			}
		});

		fileName = (EditText)findViewById(R.id.fileName);
		SharedPreferences sharedPref = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
		String fName = sharedPref.getString("ImageFilename", null);
		if(!LogoliciousApp.strIsNullOrEmpty(fName)) {
			fileName.setText(fName);
		} else
			fileName.setText("");
		
		saveFileName = (Button)findViewById(R.id.save);
		saveFileName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPref = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("ImageFilename", fileName.getText().toString().trim());
				editor.commit();
				LogoliciousApp.toast(ActivityMainEditor.act, "Filename successfully set!", Toast.LENGTH_LONG);
			}
		});

		final TextView version = (TextView) findViewById(R.id.version);
		final CheckBox showTipsNextime = (CheckBox)findViewById(R.id.showTipNextime);
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
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

		TextView officialWebsiteLink = (TextView) findViewById(R.id.officialWebsiteLink);
		officialWebsiteLink.setText(Html.fromHtml(getString(R.string.website)));
        officialWebsiteLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.addyourlogoapp.com")));
            }
        });

	}

}