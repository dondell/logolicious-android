package com.olav.logolicious.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.olav.logolicious.customize.datamodel.TemplateDetails;
import com.olav.logolicious.util.image.DbBitmapUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "logolicious.db";
    private static final int DATABASE_VERSION = 27;
    public static final String APP_TABLE = "logolicious";
    public static final String TEMPLATE_TABLE_NAME1 = "template";
    public static final String TEMPLATE_TABLE_NAME2 = "template_preview";
    public static final String HINT_TABLE = "hint";
    public static final String FONT_TABLE = "user_fonts";

    public static final String FONT_COLUMN_ID = "_id";
    public static final String FONT_PATH = "path";

    public static final String APP_TABLE_COLUMN_LOGOLICIOUS_USAGE = "app_use";
    public static final String APP_TABLE_COLUMN_LOGOLICIOUS_SAVECOUNT = "save_count";
    public static final String APP_TABLE_COLUMN_ISRATED = "israted";
    public static final String APP_TABLE_COLUMN_FONT_SELECTED = "font_selected";
    public static final String HINT_TABLE_COLUMN_IS_HINT_SHOW = "IS_HINT_SHOW";
    public static final String TEMPLATE_COLUMN_ID = "_id";
    public static final String TEMPLATE_COLUMN_TEMPLATE_NAME = "template_name";
    public static final String TEMPLATE_COLUMN_LAYER = "layer_seq";
    public static final String TEMPLATE_COLUMN_BITMAP_PATH = "bitmap_path";
    public static final String TEMPLATE_COLUMN_TEXT = "text_value";
    public static final String TEMPLATE_COLUMN_TEXT_COLOR = "text_color";
    public static final String TEMPLATE_COLUMN_TRANSPARENCY = "item_transparency";
    public static final String TEMPLATE_MTX_MPERSP_0 = "MPERSP_0";
    public static final String TEMPLATE_MTX_MPERSP_1 = "MPERSP_1";
    public static final String TEMPLATE_MTX_MPERSP_2 = "MPERSP_2";
    public static final String TEMPLATE_MTX_MSCALE_X = "MSCALE_X";
    public static final String TEMPLATE_MTX_MSCALE_Y = "MSCALE_Y";
    public static final String TEMPLATE_MTX_MSKEW_X = "MSKEW_X";
    public static final String TEMPLATE_MTX_MSKEW_Y = "MSKEW_Y";
    public static final String TEMPLATE_MTX_MTRANS_X = "MTRANS_X";
    public static final String TEMPLATE_MTX_MTRANS_Y = "MTRANS_Y";
    public static final String TEMPLATE_MTX_ROTATION = "MROT";
    public static final String TEMPLATE_FONT_STYLE = "font_style";
    public static final String TEMPLATE_PREVIEW = "TEMPLATE_PREVIEW";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + APP_TABLE + "(" +
                TEMPLATE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                APP_TABLE_COLUMN_LOGOLICIOUS_USAGE + " TEXT, " +
                APP_TABLE_COLUMN_LOGOLICIOUS_SAVECOUNT + " INTEGER, " +
                APP_TABLE_COLUMN_ISRATED + " INTEGER DEFAULT 0, " +
                APP_TABLE_COLUMN_FONT_SELECTED + " TEXT) "
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS " + HINT_TABLE + "(" +
                TEMPLATE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                HINT_TABLE_COLUMN_IS_HINT_SHOW + " INTEGER)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TEMPLATE_TABLE_NAME1 + "(" +
                TEMPLATE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                TEMPLATE_COLUMN_TEMPLATE_NAME + " TEXT, " +
                TEMPLATE_COLUMN_LAYER + " TEXT, " +
                TEMPLATE_COLUMN_BITMAP_PATH + " TEXT ," +
                TEMPLATE_COLUMN_TEXT + " TEXT, " +
                TEMPLATE_COLUMN_TEXT_COLOR + " TEXT, " +
                TEMPLATE_COLUMN_TRANSPARENCY + " INTEGER, " +
                TEMPLATE_MTX_MPERSP_0 + " FLOAT, " +
                TEMPLATE_MTX_MPERSP_1 + " FLOAT, " +
                TEMPLATE_MTX_MPERSP_2 + " FLOAT, " +
                TEMPLATE_MTX_MSCALE_X + " FLOAT, " +
                TEMPLATE_MTX_MSCALE_Y + " FLOAT, " +
                TEMPLATE_MTX_MSKEW_X + " FLOAT, " +
                TEMPLATE_MTX_MSKEW_Y + " FLOAT, " +
                TEMPLATE_MTX_MTRANS_X + " FLOAT, " +
                TEMPLATE_MTX_MTRANS_Y + " FLOAT, " +
                TEMPLATE_MTX_ROTATION + " FLOAT, " +
                TEMPLATE_FONT_STYLE + " TEXT) "
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TEMPLATE_TABLE_NAME2 + "(" +
                TEMPLATE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                TEMPLATE_COLUMN_TEMPLATE_NAME + " TEXT, " +
                TEMPLATE_PREVIEW +" BLOB)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS " + FONT_TABLE + " (" +
                FONT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FONT_PATH + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
		/*db.execSQL("DROP TABLE IF EXISTS " + APP_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + HINT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TEMPLATE_TABLE_NAME1);
		db.execSQL("DROP TABLE IF EXISTS " + TEMPLATE_TABLE_NAME2);*/
        onCreate(db);
    }

    public boolean insertAppTableDefaultValues() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APP_TABLE_COLUMN_LOGOLICIOUS_USAGE, 1);
        contentValues.put(APP_TABLE_COLUMN_LOGOLICIOUS_SAVECOUNT, 0);
        contentValues.put(APP_TABLE_COLUMN_FONT_SELECTED, "new_fonts/Idolwild.ttf");
        db.insert(APP_TABLE, null, contentValues);
        return true;
    }

    public boolean insertHint() {
        SQLiteDatabase db = getWritableDatabase();
        // delete first existing data
        db.delete(HINT_TABLE, null, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(HINT_TABLE_COLUMN_IS_HINT_SHOW, 1);
        db.insert(HINT_TABLE, null, contentValues);
        return true;
    }

    public boolean checkIfFontExist(String fontName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + FONT_TABLE +
                        " WHERE " + FONT_PATH + " = '" + fontName + "'",
                null);
        if (res.getCount() != 0)
            return true;
        res.close();
        return false;
    }

    public long insertFont(String path) {
        if (checkIfFontExist(path))
            return -1;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FONT_PATH, path);
        return db.insert(FONT_TABLE, null, contentValues);
    }

    public Integer deleteFont(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FONT_TABLE, FONT_PATH + " = ? ", new String[]{path});
    }

    public void deleteAllUserFonts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ FONT_TABLE);
    }

    public Cursor getFonts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + FONT_TABLE, null);
    }

    public int isShowHint() {
        int isChecked = -1;
        SQLiteDatabase db = getReadableDatabase();
        //ctx.getReadableDatabase();
        String sql = "SELECT " + TEMPLATE_COLUMN_ID + "," + HINT_TABLE_COLUMN_IS_HINT_SHOW + " FROM " + HINT_TABLE;
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c.moveToNext()) {
                isChecked = c.getInt(1);
                Log.i("isChecked", "" + isChecked);
            } else {
                isChecked = -1;
                insertHint();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isChecked = -1;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }

        return isChecked;
    }

    public Integer deleteHint(String template_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(HINT_TABLE, null, null);
    }

    public boolean updateHint(Integer id, Integer isshow) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HINT_TABLE_COLUMN_IS_HINT_SHOW, isshow);
        db.update(HINT_TABLE, contentValues, TEMPLATE_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getFontSelected() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + APP_TABLE_COLUMN_FONT_SELECTED + ", " + TEMPLATE_COLUMN_ID + " FROM " + APP_TABLE, null);
    }

    public boolean updateFontSelected(Integer id, String fontPath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APP_TABLE_COLUMN_FONT_SELECTED, fontPath);
        db.update(APP_TABLE, contentValues, TEMPLATE_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean insertSaveCount() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APP_TABLE_COLUMN_LOGOLICIOUS_USAGE, 1);
        contentValues.put(APP_TABLE_COLUMN_LOGOLICIOUS_SAVECOUNT, 1);
        db.insert(APP_TABLE, null, contentValues);
        return true;
    }

    public Cursor checkAppTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + APP_TABLE, null);
    }

    public Cursor getSaveCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + APP_TABLE_COLUMN_LOGOLICIOUS_SAVECOUNT + ", " + TEMPLATE_COLUMN_ID + ", " + APP_TABLE_COLUMN_ISRATED + " FROM " + APP_TABLE, null);
    }

    public boolean updateSaveCount(Integer id, Integer saveCount, Integer isRated) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APP_TABLE_COLUMN_LOGOLICIOUS_SAVECOUNT, saveCount);
        contentValues.put(APP_TABLE_COLUMN_ISRATED, isRated);
        db.update(APP_TABLE, contentValues, TEMPLATE_COLUMN_ID + "= ?", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean insertTemplatePreview(String t_name, byte[] t_preview) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEMPLATE_COLUMN_TEMPLATE_NAME, t_name);
        contentValues.put(TEMPLATE_PREVIEW, t_preview);
        db.insert(TEMPLATE_TABLE_NAME2, null, contentValues);
        return true;
    }

    public boolean insertTemplateDetails(String t_name,
                                         String t_lyr,
                                         String t_bitmap_path,
                                         String t_txt,
                                         String t_txt_color,
                                         int t_transparency,
                                         float t_MPERSP_0,
                                         float t_MPERSP_1,
                                         float t_MPERSP_2,
                                         float t_MSCALE_X,
                                         float t_MSCALE_Y,
                                         float t_MSKEW_X,
                                         float t_MSKEW_Y,
                                         float t_MTRANS_X,
                                         float t_MTRANS_Y,
                                         float t_MROT,
                                         String font_style) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEMPLATE_COLUMN_TEMPLATE_NAME, t_name);
        contentValues.put(TEMPLATE_COLUMN_LAYER, t_lyr);
        contentValues.put(TEMPLATE_COLUMN_BITMAP_PATH, t_bitmap_path);
        contentValues.put(TEMPLATE_COLUMN_TEXT, t_txt);
        contentValues.put(TEMPLATE_COLUMN_TEXT_COLOR, t_txt_color);
        contentValues.put(TEMPLATE_COLUMN_TRANSPARENCY, t_transparency);
        contentValues.put(TEMPLATE_MTX_MPERSP_0, t_MPERSP_0);
        contentValues.put(TEMPLATE_MTX_MPERSP_1, t_MPERSP_1);
        contentValues.put(TEMPLATE_MTX_MPERSP_2, t_MPERSP_2);
        contentValues.put(TEMPLATE_MTX_MSCALE_X, t_MSCALE_X);
        contentValues.put(TEMPLATE_MTX_MSCALE_Y, t_MSCALE_Y);
        contentValues.put(TEMPLATE_MTX_MSKEW_X, t_MSKEW_X);
        contentValues.put(TEMPLATE_MTX_MSKEW_Y, t_MSKEW_Y);
        contentValues.put(TEMPLATE_MTX_MTRANS_X, t_MTRANS_X);
        contentValues.put(TEMPLATE_MTX_MTRANS_Y, t_MTRANS_Y);
        contentValues.put(TEMPLATE_MTX_ROTATION, t_MROT);
        contentValues.put(TEMPLATE_FONT_STYLE, font_style);
        if (-1 == db.insert(TEMPLATE_TABLE_NAME1, null, contentValues))
            Log.i("xxx", "xxx Error inserting template");
        return true;
    }

    public boolean updateTemplateDetails(Integer id,
                                         String t_name,
                                         String t_lyr,
                                         String t_bitmap_path,
                                         String t_txt,
                                         String t_txt_color,
                                         int t_transparency,
                                         float t_MPERSP_0,
                                         float t_MPERSP_1,
                                         float t_MPERSP_2,
                                         float t_MSCALE_X,
                                         float t_MSCALE_Y,
                                         float t_MSKEW_X,
                                         float t_MSKEW_Y,
                                         float t_MTRANS_X,
                                         float t_MTRANS_Y,
                                         float t_MROT) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEMPLATE_COLUMN_TEMPLATE_NAME, t_name);
        contentValues.put(TEMPLATE_COLUMN_LAYER, t_lyr);
        contentValues.put(TEMPLATE_COLUMN_BITMAP_PATH, t_bitmap_path);
        contentValues.put(TEMPLATE_COLUMN_TEXT, t_txt);
        contentValues.put(TEMPLATE_COLUMN_TEXT_COLOR, t_txt_color);
        contentValues.put(TEMPLATE_COLUMN_TRANSPARENCY, t_transparency);
        contentValues.put(TEMPLATE_MTX_MPERSP_0, t_MPERSP_0);
        contentValues.put(TEMPLATE_MTX_MPERSP_1, t_MPERSP_1);
        contentValues.put(TEMPLATE_MTX_MPERSP_2, t_MPERSP_2);
        contentValues.put(TEMPLATE_MTX_MSCALE_X, t_MSCALE_X);
        contentValues.put(TEMPLATE_MTX_MSCALE_Y, t_MSCALE_Y);
        contentValues.put(TEMPLATE_MTX_MSKEW_X, t_MSKEW_X);
        contentValues.put(TEMPLATE_MTX_MSKEW_Y, t_MSKEW_Y);
        contentValues.put(TEMPLATE_MTX_MTRANS_X, t_MTRANS_X);
        contentValues.put(TEMPLATE_MTX_MTRANS_Y, t_MTRANS_Y);
        contentValues.put(TEMPLATE_MTX_ROTATION, t_MROT);
        db.update(TEMPLATE_TABLE_NAME1, contentValues, TEMPLATE_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateTemplateName(String t_OldName, String t_NewName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEMPLATE_COLUMN_TEMPLATE_NAME, t_NewName);
        db.update(TEMPLATE_TABLE_NAME1, contentValues, TEMPLATE_COLUMN_TEMPLATE_NAME + " = ? ", new String[]{t_OldName});
        return true;
    }

    public boolean checkIfTemplateExist(String template_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT " +
                "t1." + TEMPLATE_COLUMN_ID + ", " +
                "t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + ", " +
                "t2." + TEMPLATE_PREVIEW +
                " FROM " + TEMPLATE_TABLE_NAME1 + " AS t1 " +
                " LEFT JOIN " + TEMPLATE_TABLE_NAME2 + " AS t2 ON t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " = t1." + TEMPLATE_COLUMN_TEMPLATE_NAME +
                " WHERE t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + " = '" + template_name + "'" +
                " GROUP BY t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + " ORDER BY t1." + TEMPLATE_COLUMN_ID + " ASC", null);
        if (res.getCount() != 0)
            return true;
        return false;
    }

    public Cursor getAllTemplates() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT t1." + TEMPLATE_COLUMN_ID + ", t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + ", t2." + TEMPLATE_PREVIEW + " FROM " + TEMPLATE_TABLE_NAME1 + " AS t1 " +
                " LEFT JOIN " + TEMPLATE_TABLE_NAME2 + " AS t2 ON t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " = t1." + TEMPLATE_COLUMN_TEMPLATE_NAME +
                " GROUP BY t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + " ORDER BY t1." + TEMPLATE_COLUMN_ID + " ASC", null);
        return res;
    }

    public ArrayList<TemplateDetails> getAllTemplatesByAR(String AR, String reverseAR) {
        ArrayList<TemplateDetails> templateDetailses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DISTINCT t1." + TEMPLATE_COLUMN_ID + ", t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + ", t2." + TEMPLATE_PREVIEW + " FROM " + TEMPLATE_TABLE_NAME1 + " AS t1 " +
                    " LEFT JOIN " + TEMPLATE_TABLE_NAME2 + " AS t2 ON t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " = t1." + TEMPLATE_COLUMN_TEMPLATE_NAME +
                    " WHERE " +
                    "(" +
                    "t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " LIKE '%" + AR + "'" +
                    " OR " +
                    "t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " LIKE '%" + reverseAR + "')" +
                    " GROUP BY t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + " ORDER BY t1." + TEMPLATE_COLUMN_ID + " ASC", null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String bookName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TEMPLATE_COLUMN_TEMPLATE_NAME));
                        byte[] image = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.TEMPLATE_PREVIEW));
                        Bitmap b = DbBitmapUtility.getImage(image);
                        templateDetailses.add(new TemplateDetails(bookName, b));
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception ex) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return templateDetailses;
    }

    public ArrayList<TemplateDetails> getOtherTemplatesARSizes() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TemplateDetails> templateDetailses = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DISTINCT t1." + TEMPLATE_COLUMN_ID + ", t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + ", t2." + TEMPLATE_PREVIEW + " FROM " + TEMPLATE_TABLE_NAME1 + " AS t1 " +
                    " LEFT JOIN " + TEMPLATE_TABLE_NAME2 + " AS t2 ON t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " = t1." + TEMPLATE_COLUMN_TEMPLATE_NAME +
                    " WHERE " +
                    "(" +
                    "t2." + TEMPLATE_COLUMN_TEMPLATE_NAME + " LIKE '%.%'" +
                    ")" +
                    " GROUP BY t1." + TEMPLATE_COLUMN_TEMPLATE_NAME + " ORDER BY t1." + TEMPLATE_COLUMN_ID + " ASC", null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String bookName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TEMPLATE_COLUMN_TEMPLATE_NAME));
                        byte[] image = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.TEMPLATE_PREVIEW));
                        Bitmap b = DbBitmapUtility.getImage(image);
                        templateDetailses.add(new TemplateDetails(bookName, b));
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception ex) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return templateDetailses;
    }

    public Cursor getTemplateLayers(String template_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TEMPLATE_TABLE_NAME1 + " WHERE " +
                TEMPLATE_COLUMN_TEMPLATE_NAME + "=?", new String[]{template_name});
    }

    public Integer deleteTemplate(String template_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TEMPLATE_TABLE_NAME1, TEMPLATE_COLUMN_TEMPLATE_NAME + " = ? ",
                new String[]{template_name});
    }

    public Integer deleteTemplatePreview(String template_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TEMPLATE_TABLE_NAME2, TEMPLATE_COLUMN_TEMPLATE_NAME + " = ? ",
                new String[]{template_name});
    }

    public static void exportDatabase(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//user//0//" + context.getPackageName() + "//databases//" + DATABASE_NAME + "";
                String backupDBPath = "logolicious.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                Log.i("xxx", "xxx Database exported " + backupDB.getAbsolutePath());

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
