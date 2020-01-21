package com.olav.logolicious.customize.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.customize.datamodel.TemplateDetails;
import com.olav.logolicious.customize.widgets.LayersContainerView;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;

import java.util.ArrayList;

/**
 * Created by Dondell A. Batac on 4/23/2017.
 */

public class TemplateItemListCursorAdapter extends RecyclerView.Adapter<TemplateItemListCursorAdapter.ViewHolder> {

    private ArrayList<TemplateDetails> templateDetails;
    private String AR;
    private Activity ctx;
    private ImageView backgroundImage;
    private Handler mHandler;
    private LayersContainerView layeredLogos;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView templatePreview;
        public TextView template_name;

        public ViewHolder(View view) {
            super(view);
            template_name = (TextView) view.findViewById(R.id.template_name);
            templatePreview = (ImageView) view.findViewById(R.id.templatePreview);

        }
    }

    public TemplateItemListCursorAdapter(Activity ctx, ArrayList<TemplateDetails> templateDetails, String AR, ImageView backgroundImage, Handler mHandler, final LayersContainerView layeredLogos) {
        this.ctx = ctx;
        this.templateDetails = templateDetails;
        this.AR = AR;
        this.backgroundImage = backgroundImage;
        this.mHandler = mHandler;
        this.layeredLogos = layeredLogos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.template_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ActivityMainEditor.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        Bitmap b = this.templateDetails.get(position).bitmap;
        if (b != null) {
            holder.templatePreview.setImageBitmap(b);
        } else {
            holder.templatePreview.setImageResource(R.drawable.no_color);
        }

        final String template_name = this.templateDetails.get(position).template_name;
        Log.i("xxx","xxx template_name " + template_name);

        if(template_name.contains("1:1")) {
            holder.templatePreview.getLayoutParams().width = 70;
            holder.templatePreview.getLayoutParams().height = 70;
        } else if(template_name.contains("2:3")) {
            holder.templatePreview.getLayoutParams().width = 50;
            holder.templatePreview.getLayoutParams().height = 70;
        } else if(template_name.contains("3:2")) {
            holder.templatePreview.getLayoutParams().width = 70;
            holder.templatePreview.getLayoutParams().height = 50;
        } else if(template_name.contains("4:3")) {
            holder.templatePreview.getLayoutParams().width = 80;
            holder.templatePreview.getLayoutParams().height = 60;
        } else if(template_name.contains("3:4")) {
            holder.templatePreview.getLayoutParams().width = 60;
            holder.templatePreview.getLayoutParams().height = 80;
        } else if(template_name.contains("16:9")) {
            holder.templatePreview.getLayoutParams().width = 100;
            holder.templatePreview.getLayoutParams().height = 60;
        } else if(template_name.contains("9:16")) {
            Log.i("xxx", "xxx 9:16 " + this.templateDetails.get(position).template_name);
            holder.templatePreview.getLayoutParams().width = 60;
            holder.templatePreview.getLayoutParams().height = 100;
        } else {
            //holder.templatePreview.getLayoutParams().width = 70;
            //holder.templatePreview.getLayoutParams().height = 70;
        }
        //refresh template preview for new dimension
        holder.templatePreview.requestLayout();

        holder.templatePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTemplateClick(template_name, backgroundImage, mHandler, layeredLogos);
            }
        });

        holder.templatePreview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogoliciousApp.templateOption(ctx, layeredLogos, backgroundImage, template_name, mHandler);
                return false;
            }
        });

        holder.template_name.setText(template_name); //.replace("_" + this.AR,"")
    }

    @Override
    public int getItemCount() {
        return this.templateDetails.size();
    }

    private void onTemplateClick(String templateName, final ImageView backgroundImage, Handler mHandler, final LayersContainerView layeredLogos){
        if(null == backgroundImage.getDrawable() && !LogoliciousApp.isLive){
            Message msg = mHandler.obtainMessage(ActivityMainEditor.MESSAGE_APPLY_TEMPLATE_ERROR);
            mHandler.sendMessage(msg);
            ActivityMainEditor.removePopupFunnySelection();
            return;
        }

        // remove first the existing items
        layeredLogos.removeAllItems();
        layeredLogos.invalidate();

        final String template_name = templateName;

        // We check if the applied template is same as the Current AR
        // LogoliciousApp.showMessageOK(ActivityMainEditor.act, "Current is AR: " + GlobalClass.getAR().replace("_", "") + " templateAR: " + LogoliciousApp.getARName(template_name), null);
        if(GlobalClass.getAR().replace("_", "").equals(LogoliciousApp.getARName(template_name))){
            layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(template_name), backgroundImage, false);
        } else {
            LogoliciousApp.showYesNoAlert(ActivityMainEditor.act, "Template size unmatched", ctx.getString(R.string.TemplateApplyErrorMessage), "YES", "NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which){
                        case DialogInterface.BUTTON_POSITIVE:
                            layeredLogos.applyTemplate(GlobalClass.sqLiteHelper.getTemplateLayers(template_name), backgroundImage, true);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            });
        }

        ActivityMainEditor.removePopupFunnySelection();
    }
}