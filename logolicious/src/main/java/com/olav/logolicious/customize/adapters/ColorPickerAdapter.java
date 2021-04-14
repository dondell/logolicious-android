package com.olav.logolicious.customize.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.olav.logolicious.R;
import com.olav.logolicious.util.ClickColorListener;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    private static List<String> colorList = new ArrayList<>();
    private ClickColorListener clickColorListener;

    public ColorPickerAdapter(Context context, ClickColorListener clickColorListener) {
        this.clickColorListener = clickColorListener;

        // for convenience and better reading, we place the colors in a two dimension array
        String[][] colors = {
                {"9f1f66", "1baae4", "3ab24b", "feaf45", "be1e2e"},
                {"ed297d", "1474c1", "08663b", "f9eb30", "f23b37"},
                {"ffffff", "28398f", "2bb770", "cee31c", "f0592a"},
                {"000000", "642c8f", "05aa9d", "8cc644", "f69220"}
        };

        colorList = new ArrayList<>();

        // add the color array to the list
        for (String[] color : colors) {
            for (String s : color) {
                colorList.add("#" + s);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_color_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // check if last color item is reach, if true then replace it with no color icon
        if (Color.parseColor(colorList.get(position)) == Color.parseColor("#ffffff")) {
            holder.preColors.setBackgroundResource(R.drawable.no_color);
        } else {
            holder.preColors.setBackgroundColor(Color.parseColor(colorList.get(position)));
        }

        holder.preColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickColorListener.onColorSelect(colorList.get(position));
            }
        });
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout preColors;
        ImageView grid_item_color;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            preColors = itemView.findViewById(R.id.preColors);
            grid_item_color = itemView.findViewById(R.id.grid_item_color);
        }
    }
}
