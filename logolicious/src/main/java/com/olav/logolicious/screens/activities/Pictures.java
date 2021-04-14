package com.olav.logolicious.screens.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.customize.adapters.AlbumDetails;
import com.olav.logolicious.util.GlobalClass;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ASUS on 4/19/2017.
 */

public class Pictures extends AppCompatActivity{

    private int count;
    private boolean[] thumbnailsselection;
    boolean holdLoading = false;
    private ImageAdapter imageAdapter;
    private ArrayList<AlbumDetails> mAlbumsList = new ArrayList<>();
    GridView imagegrid;
    String albumName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_gallery_albums);
        setTitle("Pictures");

        Bundle b = getIntent().getExtras();
        if(null != b) {
            mAlbumsList = (ArrayList<AlbumDetails>) b.getSerializable("AlbumPhotos");
            count = mAlbumsList.size();
            albumName = b.getString("AlbumName");
            setTitle("" + albumName);
            this.thumbnailsselection = new boolean[this.count];
        }

        imageAdapter = new ImageAdapter();
        imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imagegrid.setAdapter(imageAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.grid_galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                holder.galleryPicName = (TextView) convertView.findViewById(R.id.galleryPicName);
                holder.galleryPicTotal = (TextView) convertView.findViewById(R.id.galleryPicTotal);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });
            holder.imageview.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    holdLoading = true;
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + mAlbumsList.get(position).path), "image/*");
                    startActivity(intent);
                }
            });

            String imgUri = Uri.fromFile(new File(mAlbumsList.get(position).path)).toString();
            GlobalClass.imageLoader.displayImage(imgUri, holder.imageview);

            if (mAlbumsList.get(position).isAlbum)
                holder.checkbox.setVisibility(View.INVISIBLE);
            else {
                holder.checkbox.setVisibility(View.VISIBLE);
                holder.galleryPicName.setVisibility(View.INVISIBLE);
            }

            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        TextView galleryPicName;
        TextView galleryPicTotal;
        int id;
    }

}
