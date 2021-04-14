package com.olav.logolicious.screens.fragments;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.Activity_Tip;
import com.olav.logolicious.util.GlobalClass;

public class TipFourth extends Fragment{
	
	static int layResId;
	static int fragNum;
	static ImageView imageView1 = null;
	static View v = null;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(layResId, container, false);
        ImageButton btnOk = (ImageButton) v.findViewById(R.id.tipOk);
        btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(fragNum == 4)
					getActivity().finish(); // last Tips fragment
				else
					((Activity_Tip)getActivity()).pager.setCurrentItem(fragNum);
			}
		});

        imageView1 = (ImageView) v.findViewById(R.id.imageView1);
        GlobalClass.imageLoader.displayImage(Uri.parse("drawable://" + R.drawable.tip_4_guide).toString(), imageView1);
        return v;
    }
	
    public static TipFourth newInstance(String text, int fragNum, int layResId, int drawableId) {

    	TipFourth.layResId = layResId;
    	TipFourth.fragNum = fragNum;
    	TipFourth f = new TipFourth();
    	
        Bundle b = new Bundle();
        b.putString("msg", text);
        b.putInt("drawable", drawableId);

        f.setArguments(b);
        return f;
    }
    
}