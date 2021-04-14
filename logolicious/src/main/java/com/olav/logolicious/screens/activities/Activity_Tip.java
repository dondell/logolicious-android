package com.olav.logolicious.screens.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.KeyEvent;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.fragments.TipFourth;
import com.olav.logolicious.screens.fragments.TipMain;
import com.olav.logolicious.screens.fragments.TipSecond;
import com.olav.logolicious.screens.fragments.TipThird;

public class Activity_Tip extends FragmentActivity{

	public ViewPager pager;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

            case 0: return TipMain.newInstance("FirstFragment, Instance 1");
            case 1: 
            	return TipSecond.newInstance("SecondFragment, Instance 2", 2, R.layout.tip_second, R.drawable.tip_step1_2);
            case 2: 
            	return TipThird.newInstance("ThirdFragment, Instance 3", 3, R.layout.tip_third, R.drawable.tip_step3_4);
            case 3: 
            	return TipFourth.newInstance("FourthFragment, Instance 4", 4, R.layout.tip_fourth, R.drawable.tip_4_guide);
            default: 
            	return TipMain.newInstance("FirstFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }       
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK)) 
		{
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
  
    
}