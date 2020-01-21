package com.olav.logolicious.customize.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.customize.datamodel.TemplateDetails;
import com.olav.logolicious.customize.widgets.LayersContainerView;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;

import java.util.ArrayList;

/**
 * Created by Dondell A. Batac on 4/21/2017.
 */

public class TemplateListAdapter extends BaseAdapter {

    private Activity context;
    private LayoutInflater inflater;
    private String[] ARs;
    private ImageView backgroundImage;
    private Handler mHandler;
    private LayersContainerView layeredLogos;

    private TemplateItemListCursorAdapter templateItemListCursorAdapter;

    public TemplateListAdapter(Activity context, String[] ARs, ImageView backgroundImage, Handler mHandler, final LayersContainerView layeredLogos) {
        this.context = context;
        this.ARs = ARs;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.backgroundImage = backgroundImage;
        this.mHandler = mHandler;
        this.layeredLogos = layeredLogos;
    }

    @Override
    public int getCount() {
        return this.ARs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.template_list_item, parent, false);
            holder.tvAR = (TextView)convertView.findViewById(R.id.tvAR);
            //holder.lvTemplateList = (ListView) convertView.findViewById(R.id.lvTemplateList);
            //holder.linearListContainer = (LinearLayout) convertView.findViewById(R.id.linearListContainer);
            holder.my_recycler_view = (RecyclerView) convertView.findViewById(R.id.my_recycler_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvAR.setText(this.ARs[position]);

        //Cursor cursor = null;
        String param1, param2;
        ArrayList<TemplateDetails> templateDetails;

        param1 = this.ARs[position];
        if(this.ARs[position].contains("16:9")){
            //manual reverse for 9:16 because reverse string only capable of x:x not xx:x or x:xx
            param2 = "9:16";
        } else {
            param2 = LogoliciousApp.reverseString(this.ARs[position]);
        }

        if (this.ARs[position].equalsIgnoreCase("OTHER SIZES")) {
            templateDetails = GlobalClass.sqLiteHelper.getOtherTemplatesARSizes();
        } else
            templateDetails = GlobalClass.sqLiteHelper.getAllTemplatesByAR(param1, param2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.my_recycler_view.setLayoutManager(layoutManager);

        templateItemListCursorAdapter = new TemplateItemListCursorAdapter(context, templateDetails, this.ARs[position], backgroundImage, mHandler, layeredLogos);
        holder.my_recycler_view.setAdapter(templateItemListCursorAdapter);

        return convertView;
    }

    public static class ViewHolder {
        public TextView tvAR;
        public LinearLayout linearListContainer;
        public RecyclerView my_recycler_view;
    }
}