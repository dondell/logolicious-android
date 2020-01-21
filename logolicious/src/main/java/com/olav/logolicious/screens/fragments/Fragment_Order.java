package com.olav.logolicious.screens.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.olav.logolicious.R;
import com.olav.logolicious.customize.adapters.AdapterGridLogos;
import com.olav.logolicious.customize.adapters.ArrayHolderLogos;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.LogoliciousApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Fragment_Order extends Fragment{
	
	private ArrayList<ArrayHolderLogos> arrayDesigns = new ArrayList<ArrayHolderLogos>();
	private AdapterGridLogos adp = null;
	
	private TextView email = null;
	private CheckBox cb = null;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.about_orderpage, container, false);
		setHasOptionsMenu(false);
		
		preferences = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
		editor = preferences.edit();
		
		arrayDesigns.clear();
		adp = new AdapterGridLogos(getContext(), arrayDesigns, R.layout.grid_designedlogoitem);
		
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			boolean hideTransPixel = bundle.getBoolean("hideTransPixel", true);
			LogoliciousApp.setViewVisibilityOrGone(rootView, R.id.transparentPixel, hideTransPixel == true ? false: true);
		}
		
		email = (TextView) rootView.findViewById(R.id.email);
		
		email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				//3rd solution
				List<Intent> targetShareIntents=new ArrayList<Intent>();
			    Intent shareIntent=new Intent();
			    shareIntent.setAction(Intent.ACTION_SEND);
			    shareIntent.setType("text/html");
			    List<ResolveInfo> resInfos=getActivity().getPackageManager().queryIntentActivities(shareIntent, 0);
			    if(!resInfos.isEmpty()){
			        System.out.println("Have package");
			        for(ResolveInfo resInfo : resInfos){
			            String packageName=resInfo.activityInfo.packageName;
			            Log.i("Package Name", packageName);
			            if(
			            		packageName.contains("com.google.android.gm")||  
			            		packageName.contains("com.appple.app.email")|| 
			            		packageName.contains("com.trtf.blue")|| 
			            		packageName.contains("com.microsoft.office.outlook")||
			            		packageName.contains("me.bluemail.mail")||
			            		packageName.contains("com.yahoo.mobile.client.android.mail")||
			            		packageName.contains("com.fsck.k9")||
			            		packageName.contains("com.my.mail")||
			            		packageName.contains("com.boxer.email")
			            		){
			            	Random r = new Random();
							int rOrderNumber = r.nextInt(999999 - 100000) + 1000000;
			                Intent eIntent=new Intent(Intent.ACTION_SEND);
			                eIntent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
			                eIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.OrderEmail)});
							eIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(Locale.US, "[Order %d] Create my logo", rOrderNumber));
							eIntent.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(getString(R.string.label_oderbodymsg)));
							eIntent.setType("text/html");
							eIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			                eIntent.setPackage(packageName);
			                targetShareIntents.add(eIntent);
			            }
			        }
			        if(!targetShareIntents.isEmpty()){
			            System.out.println("Have Intent");
			            Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose Email App");
			            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
			            startActivity(chooserIntent);
			        }else{
						LogoliciousApp.toast(getActivity(), "No email client App", Toast.LENGTH_LONG);
			        }
			    }
			}
		});
		
		email.setText(Html.fromHtml(getString(R.string.ClickHereToOrder)));
		cb = (CheckBox) rootView.findViewById(R.id.checkBoxNoLogoButton);
		if(null != cb) {
			cb.setChecked(preferences.getBoolean("HideNologoButton", false));
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton cb, boolean flag) {
					if(flag == true) {
						editor.putBoolean("HideNologoButton", true);
						editor.commit();
						LogoliciousApp.setViewVisibility(ActivityMainEditor.act, R.id.noLogo, false);
						LogoliciousApp.setViewVisibility(ActivityMainEditor.act, R.id.spacerNoLogo, false);
					} else {
						editor.putBoolean("HideNologoButton", false);
						editor.commit();
						LogoliciousApp.setViewVisibility(ActivityMainEditor.act, R.id.noLogo, true);
						LogoliciousApp.setViewVisibility(ActivityMainEditor.act, R.id.spacerNoLogo, true);
					}
				}
			});
		}
		return rootView;
	}

}