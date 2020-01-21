package com.olav.logolicious.screens.fragments;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.util.LogoliciousApp;

public class TipFontFeature extends DialogFragment {

    static int layResId;
    static int fragNum;
    View v = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(layResId, container, false);
        Button gotIt = v.findViewById(R.id.gotIt);
        TextView textView = v.findViewById(R.id.textView);
        gotIt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TipFontFeature.this.dismiss();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(getString(R.string.font_feature_tip), Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(getString(R.string.font_feature_tip)));
        }
        return v;
    }

    public static TipFontFeature newInstance(String text, int fragNum, int layResId) {

        TipFontFeature.layResId = layResId;
        TipFontFeature.fragNum = fragNum;
        TipFontFeature f = new TipFontFeature();

        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*LogoliciousApp.getScreenDimensions(getActivity(), Configuration.ORIENTATION_PORTRAIT, 0);
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int)(params.width * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);*/
        /*Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = 400;
        params.height = 400;
        window.setAttributes(params);*/
    }

}