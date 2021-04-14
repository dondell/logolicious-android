package com.olav.logolicious.screens.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTabHost;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.AboutSaveSettings;

import java.util.List;

public class AboutUsFragment extends DialogFragment{

	private FragmentTabHost mTabHost;
	private ViewPager viewPager;
	private AboutUsPagerAdapter adapter;
	private static int tab_number = 0;
	private static int tab_total = 1;
	public static boolean isClick = false;
	private static boolean onlyShowCurrentTab;
	
	public static AboutUsFragment newInstance(int num, int tab, boolean showCurrentTab) {
		AboutUsFragment f = new AboutUsFragment();
		// supply num input as an argument
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		tab_number = tab;
		onlyShowCurrentTab = showCurrentTab;
		if(onlyShowCurrentTab) {
			tab_total = 1;
			tab_number = 0;
		} else 
			tab_total = 3;
		return f;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.about_dialog_fragment, container, false);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		mTabHost = (FragmentTabHost) view.findViewById(R.id.tabs);
		
		mTabHost.setup(getActivity(), getChildFragmentManager());
		
		if(!onlyShowCurrentTab) {
			//mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("ABOUT"), Fragment_AboutInfo.class, null);
			mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("SETTINGS"), AboutSaveSettings.class, null);
			//mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("NO LOGO?"), Fragment_Order.class, null);
		} else {
			// set transparent background
			final Drawable d = new ColorDrawable(Color.TRANSPARENT);
	        d.setAlpha(130);

	        getDialog().getWindow().setBackgroundDrawable(d);
			mTabHost.addTab(mTabHost.newTabSpec("").setIndicator(""), Fragment_Order.class, null);
			mTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 0;
		}
		
		
		adapter = new AboutUsPagerAdapter(getChildFragmentManager(), getArguments());
		adapter.setTitles(new String[] {"ABOUT","SETTINGS","ORDER"});

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int i) {
				// TODO Auto-generated method stub
				mTabHost.setCurrentTab(i);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});

		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String s) {
				// TODO Auto-generat ed method stub
				int i = mTabHost.getCurrentTab();
				viewPager.setCurrentItem(i);
			}
		});

		viewPager.setCurrentItem(tab_number);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}
	
	public class AboutUsPagerAdapter extends FragmentPagerAdapter {
		
		Bundle bundle;
		String [] titles;

		public AboutUsPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}
		
		public AboutUsPagerAdapter(FragmentManager fm, Bundle bundle) {
			super(fm);
			this.bundle = bundle;
		}
		
		@Override
		public Fragment getItem(int num) {
			// TODO Auto-generated method stub
			Fragment fragment = null;
			boolean hideTransPixel = true;
			if(onlyShowCurrentTab){
				fragment = new Fragment_Order();
				hideTransPixel = false;
			} else {
				hideTransPixel = true;
				if(num == 0){
					fragment = new Fragment_AboutInfo();	
				} else if(num == 1){
					//fragment = new AboutSaveSettings();
				} else if(num == 2){
					fragment = new Fragment_Order();
				}
			}
			
			Bundle args = new Bundle();
			args.putSerializable("aboutus", bundle.getSerializable(num == 0 ? "pros" : "cons"));
			args.putBoolean("isClick", false);
			args.putBoolean("hideTransPixel", hideTransPixel);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tab_total;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
		
		public void setTitles(String[] titles) {
			this.titles = titles;
		}
		
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
	
	
}
