package com.olav.logolicious.customize.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.olav.logolicious.R;
import com.olav.logolicious.util.SQLiteHelper;
import com.olav.logolicious.util.image.DbBitmapUtility;

public class ImageCursorAdapter extends SimpleCursorAdapter {

    private Cursor c;
    private Context context;
    
    @SuppressWarnings("deprecation")
	public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
	    super(context, layout, c, from, to);
	    this.c = c;
	    this.context = context;
    }

	public View getView(int pos, View inView, ViewGroup parent) {
       View v = inView;
       if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.template_item, null);
       }
       this.c.moveToPosition(pos);      
       String template_name = this.c.getString(this.c.getColumnIndex(SQLiteHelper.TEMPLATE_COLUMN_TEMPLATE_NAME));
       byte[] image = this.c.getBlob(this.c.getColumnIndex(SQLiteHelper.TEMPLATE_PREVIEW));
       ImageView iv = (ImageView) v.findViewById(R.id.templatePreview);
       Bitmap b = DbBitmapUtility.getImage(image);
       if (image != null) {
    	   iv.setImageBitmap(b);
       } else {
    	   iv.setImageResource(R.drawable.no_color);
       }
       
//       // landscape template
//       if(b.getWidth() > b.getHeight()) {
//    	   iv.getLayoutParams().height = 50;
//    	   iv.getLayoutParams().width = 70;
//       } else {
//       // portrait template
//    	   iv.getLayoutParams().height = 70;
//    	   iv.getLayoutParams().width = 50;
//       }
//       iv.requestLayout();
       
       TextView fname = (TextView) v.findViewById(R.id.template_name);
       fname.setText(template_name);
       return(v);
	}
}