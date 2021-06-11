package com.olav.logolicious.screens.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.olav.logolicious.R;
import com.olav.logolicious.customize.adapters.AlbumDetails;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;

import java.io.File;
import java.util.ArrayList;

public class GalleryViewerActivity extends AppCompatActivity implements OnClickListener {

    final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
    final String orderBy = MediaStore.Images.Media._ID + " DESC";
    GridView imagegrid;
    Button selectBtn;
    ListView galList;
    Cursor imagecursor;
    int image_column_index;
    boolean holdLoading = false;
    int holdOnIndex = 0;
    //ProgressBar progressGallery;
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    //private String[] arrPath;
    private ImageAdapter imageAdapter;
    private ArrayList<AlbumDetails> mAlbumsList = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_gallery_albums);
        setTitle("Albums");

        //progressGallery = (ProgressBar) findViewById(R.id.progressGallery);
        selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(this);

        initGalleryAlbums();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        holdLoading = true;
    }

    private void initGalleryAlbums() {
        //Query Gallery folders
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Thumbnails.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        ids.clear();
        mAlbumsList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AlbumDetails album = new AlbumDetails();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                album.id = cursor.getString(columnIndex);

                if (!ids.contains(album.id)) {
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    album.name = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    album.index = columnIndex;
                    album.coverID = cursor.getLong(columnIndex);
                    album.isAlbum = true;
                    if (album.coverID != -1) {
                        //STEP 2 Now
                        //Log.i(TAG, "imageId-"+album.coverID);
                        String[] columnsReturn = {MediaStore.Images.Media.DATA};
                        String whereimageId = MediaStore.Images.Media._ID + " LIKE ?";
                        String valuesIs[] = {"%" + album.coverID};
                        Cursor mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columnsReturn, whereimageId, valuesIs, null);
                        int rawDataPath = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        int galleryCount = 0;
                        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                            //magePath=mCursor.getString(rawDataPath);
                            Log.i("GalleryViewerActivity", "xxx album.data " + mCursor.getString(rawDataPath));
                            album.path = mCursor.getString(rawDataPath);
                            galleryCount = galleryCount + 1;
                        }
                        album.pictureCount = galleryCount;
                    }
                    album.data = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);

                    //album.path = cursor.getString(album.data);

                    mAlbumsList.add(album);
                    ids.add(album.id);
                    count++;
                } else {
                    mAlbumsList.get(ids.indexOf(album.id));
                }
            }
            cursor.close();

            imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
            galList = (ListView) findViewById(R.id.galList);
            imageAdapter = new ImageAdapter();
            imagegrid.setAdapter(imageAdapter);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectBtn:
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        //selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {
                    finish();
                }
                break;
        }
    }

    class LoadGalleryTask extends AsyncTask<ImageView, Integer, String> {

        ImageView imageView = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return "";//download_Image((String)this.imageView.getTag());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(null != imageAdapter)
                imageAdapter.notifyDataSetChanged();
        }

        protected void onPostExecute(String result) {
            if(null != imageAdapter)
                imageAdapter.notifyDataSetChanged();
            //galList.setAdapter(imageAdapter);
            //progressGallery.setVisibility(View.GONE);
        }

//        private Bitmap download_Image(String url) {
//        }

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
            holder.checkbox.setOnClickListener(new OnClickListener() {

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
            holder.imageview.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    holdLoading = true;
                    if (mAlbumsList.get(position).isAlbum) {
                        //get the pictures inside the folder
                        //get the root folder of the gallery
                        String albumRootPath = "" + mAlbumsList.get(position).path.substring(0, mAlbumsList.get(position).path.lastIndexOf("/"));
                        ArrayList<AlbumDetails> arrayAlbums = new ArrayList<>();
                        File path = new File(albumRootPath);
                        String[] filenames = null;
                        if (path.exists()) {
                            filenames = path.list();
                        }
                        for (int i = 0; i < filenames.length; i++) {
                            //Bitmap mBitmap = BitmapFactory.decodeFile(path.getPath()+"/"+ filenames[i]);
                            ///Now set this bitmap on imageview
                            AlbumDetails albumDetails = new AlbumDetails();
                            albumDetails.isAlbum = false;
                            albumDetails.path = path.getPath() + "/" + filenames[i];
                            arrayAlbums.add(albumDetails);
                        }
                        count = filenames.length;
                        LogoliciousApp.startActivity(GalleryViewerActivity.this, Pictures.class, "AlbumName", albumRootPath, "AlbumPhotos", arrayAlbums);
                        //notifyDataSetChanged();
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + mAlbumsList.get(position).path), "image/*");
                        startActivity(intent);
                    }
                }
            });
            if (mAlbumsList.get(position).isAlbum) {
                Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), mAlbumsList.get(position).coverID, MediaStore.Images.Thumbnails.MINI_KIND, null);
                holder.imageview.setImageBitmap(bm);

                if (null != thumbnailsselection)
                    holder.checkbox.setChecked(thumbnailsselection[position]);
                holder.galleryPicName.setText(mAlbumsList.get(position).name);
                holder.galleryPicTotal.setText("" + mAlbumsList.get(position).pictureCount);
            } else {
                //holder.imageview.setTag(mAlbumsList.get(position).path);
                //new LoadGalleryTask().execute(mAlbumsList.get(position).path);
//                Bitmap bm = ImageHelper.decodeSampledBitmapFromPath(mAlbumsList.get(position).path, 40, 40);
                String imgUri = Uri.fromFile(new File(mAlbumsList.get(position).path)).toString();
                Glide.with(GlobalClass.getAppContext()).load(imgUri).into(holder.imageview);
//                holder.imageview.setImageBitmap(bm);
            }

            if (mAlbumsList.get(position).isAlbum) {
                holder.checkbox.setVisibility(View.INVISIBLE);
                selectBtn.setVisibility(View.INVISIBLE);
            }
            else {
                holder.checkbox.setVisibility(View.VISIBLE);
                selectBtn.setVisibility(View.VISIBLE);
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