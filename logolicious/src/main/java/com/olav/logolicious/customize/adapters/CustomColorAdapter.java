package com.olav.logolicious.customize.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.ClickColorListener;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.SQLiteHelper;

import java.util.ArrayList;

import static com.olav.logolicious.util.GlobalClass.sqLiteHelper;

public class CustomColorAdapter extends RecyclerView.Adapter<CustomColorAdapter.ViewHolder> {

    private static final String TAG = "CustomColorAdapter";
    private Activity context;
    private ArrayList<String> colors;
    private ClickColorListener clickColorListener;

    public CustomColorAdapter(Activity context, ArrayList<String> colors, ClickColorListener clickColorListener) {
        this.context = context;
        this.colors = colors;
        this.colors.add(0, "");
        this.clickColorListener = clickColorListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_color_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i(TAG, "colors.get(position) " + colors.get(position));
        if (!TextUtils.isEmpty(colors.get(position))) {
            holder.color_custom.setBackgroundColor(Color.parseColor(colors.get(position)));
            holder.icon_plus.setVisibility(View.INVISIBLE);
        } else {
            holder.color_custom.setBackgroundResource(R.drawable.button_grey_edge);
            holder.icon_plus.setVisibility(View.VISIBLE);
        }
        holder.color_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "xxx onClick");
                clickColorListener.onColorSelect(colors.get(position));
            }
        });

        holder.color_custom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                AlertDialog dialog;
                builder.setTitle("Delete Custom Color");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GlobalClass.sqLiteHelper.deleteColor(colors.get(position));
                        dialog.dismiss();
                        colors.clear();
                        Cursor colorCursor = GlobalClass.sqLiteHelper.getCustomColors();
                        while (colorCursor.moveToNext()) {
                            colors.add(colorCursor.getString(colorCursor.getColumnIndex(SQLiteHelper.COLOR_CODE)));
                        }
                        colors.add(0, "");
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
                Log.i(TAG, "xxx onLongClick");
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors != null ? colors.size() : 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout color_custom;
        ImageView icon_plus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            color_custom = itemView.findViewById(R.id.color_custom);
            icon_plus = itemView.findViewById(R.id.icon_plus);
        }
    }
}
