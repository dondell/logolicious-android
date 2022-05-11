package com.olav.logolicious.customize.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import androidx.core.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.olav.logolicious.R;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.PaintUtil;
import com.olav.logolicious.util.SavingTemplateListener;
import com.olav.logolicious.util.image.DbBitmapUtility;
import com.olav.logolicious.util.image.ImageHelper;

import org.acra.ACRA;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LayersContainerView extends ImageView implements OnTouchListener {
    Context ctx;
    private final String TAG = getClass().getSimpleName();
    private Canvas mCanvas;
    private Paint mBitmapPaint;
    private Matrix matrix = new Matrix();

    public static List<Layer> layers = new LinkedList<>();
    public Layer targetSelected = null;
    private History ITEM_INITIAL_DATA;
    int parentWidth;
    int parentHeight;
    public boolean isLandscape = false;

    private static ArrayList<String> deletedFonts = new ArrayList<>();
    private static ArrayList<String> deletedLogo = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private int uniqueIDforEachLayer = 0;

    private GestureDetectorCompat mGestureDetector;
    private LongPressGestureListener longPressGestureListener;
    private final int HISTORY_LIMIT = 10;

    public LayersContainerView(Context context) {
        super(context);
        this.ctx = context;
        if (!isInEditMode()) {
            init();
        }
    }

    public LayersContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        if (!isInEditMode()) {
            init();
        }
    }

    public LayersContainerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        layers.clear();
        if (null != ActivityMainEditor.act)
            sharedPreferences = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE);
        setScaleType(ScaleType.FIT_CENTER);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        mBitmapPaint = new Paint();
        mCanvas = new Canvas();
        this.setDrawingCacheEnabled(true);

        longPressGestureListener = new LongPressGestureListener();
        mGestureDetector = new GestureDetectorCompat(ctx, longPressGestureListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //http://stackoverflow.com/questions/2159320/how-to-size-an-android-view-based-on-its-parents-dimensions
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth / 2, parentHeight / 2);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMeasured(int width, int height) {
        setMeasuredDimension(width, height);
    }

    public void addColorFilter(ColorFilter cf) {
        mBitmapPaint.setColorFilter(cf);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Snap on Grid
        if (null != sharedPreferences) {
            if (sharedPreferences.getBoolean("SnapOnGrid", false)) {
                int gridThichness = (int) (canvas.getWidth() * 0.005);
                canvas.drawLine(0, 0, canvas.getWidth(), canvas.getHeight(), PaintUtil.newSnapOnGridPaint(gridThichness));
                canvas.drawLine(canvas.getWidth(), 0, 0, canvas.getHeight(), PaintUtil.newSnapOnGridPaint(gridThichness));
            }
        }

        //scale canvas from template portrait to landscape
        for (Layer l : layers) {
            l.draw(canvas, mBitmapPaint, false, false);
        }
    }

    public void setMyMatrix(Matrix m) {
        this.matrix = m;
    }

    public void removeAllItems() {
        layers.clear();
    }

    int lyrCurrentIdx = -1;

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //This will fix error on null data when scaling item while moving without selecting item
            //So we need to add initial data.
            ITEM_INITIAL_DATA = new History();
            ITEM_INITIAL_DATA.itemPath = targetSelected.bitmapPath;
            ITEM_INITIAL_DATA.matrixHis.reset();
            ITEM_INITIAL_DATA.matrixHis.set(targetSelected.matrix);
            ITEM_INITIAL_DATA.layerHis = targetSelected;
            ITEM_INITIAL_DATA.identifier = targetSelected.uniqueID;
            ITEM_INITIAL_DATA.rotation = targetSelected.rotation;
            ITEM_INITIAL_DATA.skipMe = true;
            ITEM_INITIAL_DATA.color = targetSelected.color;
            /**
             *  loop through layers from top to bottom to get the selected item.
             */
            int lyrSize = layers.size() - 1;
            for (int i = lyrSize; i >= 0; i--) {
                Layer l = layers.get(i);
                if (l.contains(event)) {
                    lyrCurrentIdx = i;
                    targetSelected = l;
                    targetSelected.touch = true;
                    layers.remove(l);
                    layers.add(l);

                    // update seekbar progress base on the item
                    ActivityMainEditor.seekbarTrans.setProgress(targetSelected.seekBarTransparent);
                    l.firstTouchX = event.getX();
                    l.firstTouchY = event.getY();

                    ITEM_INITIAL_DATA = new History();
                    ITEM_INITIAL_DATA.itemPath = targetSelected.bitmapPath;
                    ITEM_INITIAL_DATA.matrixHis.reset();
                    ITEM_INITIAL_DATA.matrixHis.set(targetSelected.matrix);
                    ITEM_INITIAL_DATA.layerHis = targetSelected;
                    ITEM_INITIAL_DATA.identifier = targetSelected.uniqueID;
                    ITEM_INITIAL_DATA.rotation = targetSelected.rotation;
                    ITEM_INITIAL_DATA.skipMe = true;
                    ITEM_INITIAL_DATA.color = targetSelected.color;

                    invalidate();
                    Log.i("xxx", "xxx ACTION_DOWN");
                    break;
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (targetSelected != null) {
                if (!targetSelected.isLock) {
                    targetSelected.firstTouchX = event.getX();
                    targetSelected.firstTouchY = event.getY();
                    targetSelected.touch = true;
                    int count = event.getPointerCount();
                    if (count > 1)
                        targetSelected.isTwoFinger = true;
                    else
                        targetSelected.isTwoFinger = false;

                    //set boolean has changes for history
                    hasMatrixChanges = true;
                    //Log.i("xxx","xxx targetSelected.isTwoFinger " + targetSelected.isTwoFinger);
                    invalidate();
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (targetSelected != null)
                targetSelected.touch = false;

            //track current record
            int lyrSize = layers.size() - 1;
            for (int i = lyrSize; i >= 0; i--) {
                Layer l = layers.get(i);
                if (l.contains(event) || l.uniqueID == targetSelected.uniqueID) {
                    targetSelected = l;
                    targetSelected.touch = false;
                    layers.remove(l);
                    layers.add(l);

                    invalidate();

//                    Log.i("xxx","xxx hasMatrixChanges " + hasMatrixChanges);
                    if (hasMatrixChanges) {
                        /*
                        Check first if the user move item and redo is not empty, then delete all redo array. The last move will
                        automatically the last history for redo array
                         */
                        deleteRedos();

                        Log.i("xxx", "xxx ACTION_UP undo.size()-> " + undos.size() + " redos.size()->" + redos.size());
                        putToHistory();
                        hasMatrixChanges = false;
                    }

                    updateHistoryButtons(ActivityMainEditor.act);
                    break;
                }
            }

            invalidate();
        }

        if (targetSelected == null) {
            return true;
        }

        // targetSelected not null, call the Layer touchEvents.
        return targetSelected.onTouchEvent(event);
    }

    private boolean isLayerInHistory(Layer l) {
        boolean bRet = false;
        if (null != undos) {
            if (undos.size() > 0) {
                //we should delete from the end to fully delete items
                //note: size will change when one item is deleted
                for (int i = undos.size() - 1; i >= 0; i--) {
                    if (undos.get(i).layerHis.uniqueID == l.uniqueID) {
                        bRet = true;
                    }
                }
            }
        }

        if (null != redos) {
            if (redos.size() > 0) {
                for (int i = redos.size() - 1; i >= 0; i--) {
                    if (redos.get(i).layerHis.uniqueID == l.uniqueID) {
                        bRet = true;
                    }
                }
            }
        }

        Log.i("xxx", "xxx isLayerInHistory " + bRet);
        return bRet;
    }


    /**
     * @param b
     * @param v
     * @param bitmapPath
     * @return Add bitmap to layer
     */
    public void addItem(Bitmap b, View v, String bitmapPath) {
        if (b != null) {
            Layer newLayer = new Layer(ctx, this, b, bitmapPath, v);

            //add a uniqueID identifier
            newLayer.uniqueID = uniqueIDforEachLayer;
            //iterate uniqueID
            uniqueIDforEachLayer = uniqueIDforEachLayer + 1;
            deleteRedos();
//            skipLastUndos();

//            putFirstItemHistory(newLayer);

            layers.add(newLayer);
            // focus on the added item by default
            targetSelected = newLayer;
            invalidate();
        }
    }

    public void addItem(String text, View v, int color, String font_style, boolean shadow) {
        if (text != null) {
            Layer newLayer = new Layer(ctx, this, text, v, color, font_style, shadow);

            //add a uniqueID identifier
            newLayer.uniqueID = uniqueIDforEachLayer;
            //iterate uniqueID
            uniqueIDforEachLayer = uniqueIDforEachLayer + 1;
            deleteRedos();
//            skipLastUndos();
//            putFirstItemHistory(newLayer);

            layers.add(newLayer);
            // focus on the added item by default
            targetSelected = newLayer;
            invalidate();
        }
    }

    /**
     * Delete last item of undo when new item is created
     */
    private void skipLastUndos() {
        if (undos.size() > 1) {
            undos.get(undos.size() - 1).skipMe = true;
        }
    }

    /***
     *  This will delete redo records if the current created item is created when redo size is not zero
     */
    private void deleteRedos() {
        if (redos.size() > 0) {
            if (undos.size() == 0) {
                undos.add(redos.get(redos.size() - 1));
            }
            redos.clear();
            Log.i("xxx", "xxx redos.clear() == undo.size()-> " + undos.size() + " redos.size()->" + redos.size());
        }

        // Delete history when reaches more than the limit
        if (null != undos) {
            if (undos.size() > HISTORY_LIMIT) {
                undos.remove(0);
            }
        }
    }

    private void putFirstItemHistory(Layer l) {
        History newHisRecord = new History();
        newHisRecord.itemPath = l.bitmapPath;
        newHisRecord.matrixHis.reset();
        newHisRecord.matrixHis.set(l.matrix);
        newHisRecord.layerHis = l;
        newHisRecord.identifier = l.uniqueID;
        newHisRecord.rotation = l.rotation;
        newHisRecord.skipMe = true;
        if (undos.size() == 0) {
            newHisRecord.firstIndex = true;
            newHisRecord.lastIndex = false;
        }
        undos.add(newHisRecord);
        Log.i("xxx", "xxx putHistory undo.size()-> " + undos.size() + " redos.size()->" + redos.size());
    }

    public void putToHistory() {
        if (!isLayerInHistory(targetSelected)) {
            putInitialItemPositionHistory(ITEM_INITIAL_DATA);
        }

        putHistory(targetSelected);
    }

    private void putInitialItemPositionHistory(History h) {
        undos.add(h);
    }

    private void putHistory(Layer l) {
        History newHisRecord = new History();
        newHisRecord.itemPath = l.bitmapPath;
        newHisRecord.matrixHis.reset();
        newHisRecord.matrixHis.set(l.matrix);
        newHisRecord.layerHis = l;
        newHisRecord.identifier = l.uniqueID;
        newHisRecord.rotation = l.rotation;
        newHisRecord.color = l.color;
        if (undos.size() == 0) {
            newHisRecord.firstIndex = true;
            newHisRecord.lastIndex = false;
        } else {
            //un-skip previous
            if (null != undos) {
                Log.i("xxx", "xxx dmdm " + undos.size());
                undos.get(undos.size() - 1).skipMe = false;
                undos.add(newHisRecord);
                //Skip last record for Undo array
                undos.get(undos.size() - 1).skipMe = true;
                Log.i("xxx", "xxx putHistory id=" + undos.get(undos.size() - 1).identifier + " undo.size()-> " + undos.size() + " redos.size()->" + redos.size() + " skip " + undos.get(undos.size() - 1).skipMe);
            }
        }
    }

    public Bitmap computeLogoOptimizeDimension(Bitmap b, String logoPath, int screenW, int screenH) {
        Log.i("LogoOptimizeDimension", "screen w" + screenW + " h " + screenH);
        Log.i("LogoOptimizeDimension", "orig w " + b.getWidth() + " h " + b.getHeight());
        Bitmap optimizeBitmap = null;

        if (null == b)
            return null;

        double ratioX = (double) b.getWidth() / screenW;
        double ratioY = (double) b.getHeight() / screenH;
        // compute which is the largest scaled
        double maxRatio = ratioX > ratioY ? ratioX : ratioY;
        // apply the max Ratio
        double computedX = (b.getWidth() / maxRatio) * 0.6;
        double computedY = (b.getHeight() / maxRatio) * 0.6;

        if (b.getWidth() < computedX && b.getHeight() < computedY) {
            optimizeBitmap = ImageHelper.scaleWithRespectToAspectRatio(b, (float) computedX, (float) computedY);
            Log.i("using larger ratio", "xxx a resize logo w " + computedX + " h " + computedY);
        } else {
            optimizeBitmap = ImageHelper.decodeSampledBitmapFromPathb(logoPath, (int) computedX, (int) computedY);
            Log.i("using decode sample", "xxx b resize logo w " + optimizeBitmap.getWidth() + " h " + optimizeBitmap.getHeight());
        }
        return optimizeBitmap;
    }

    public void removePerItem() {
        /**
         *  loop through layers from top to bottom to get the selected item.
         */
        for (int i = layers.size() - 1; i >= 0; i--) {
            Layer l = layers.get(i);
            layers.remove(l);
            layers.add(i, l);
            targetSelected = layers.get(layers.size() - 1);
        }
        if (targetSelected != null) {
            layers.remove(targetSelected);
            Log.i("xxx", "xxx Path to delete " + targetSelected.bitmapPath);
            deleteHistoryOnDoubleTapDelete(undos, redos, targetSelected.uniqueID);
        }
        updateHistoryButtons(ActivityMainEditor.act);
    }

    public void adjustTransparency(int seekBarTrans) {
        try {
            if (targetSelected != null) { //layers.size() > 0
                targetSelected.seekBarTransparent = seekBarTrans;
            }
            invalidate();
        } catch (Exception ex) {
        }
    }

    public Bitmap saveBitmapLayers(Bitmap b, int origImageW, int origImageH, Context context, boolean asPicture) {
        Bitmap bm = null;
        bm = Bitmap.createBitmap(origImageW, origImageH, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bm);
        if (asPicture) {
            mCanvas.drawBitmap(b, 0, 0, null);
        }

        //Snap on Grid
        if (null != sharedPreferences) {
            if (sharedPreferences.getBoolean("SnapOnGrid", false)) {
                int gridThichness = (int) (mCanvas.getWidth() * 0.005);
                mCanvas.drawLine(0, 0, mCanvas.getWidth(), mCanvas.getHeight(), PaintUtil.newSnapOnGridPaint(gridThichness));
                mCanvas.drawLine(mCanvas.getWidth(), 0, 0, mCanvas.getHeight(), PaintUtil.newSnapOnGridPaint(gridThichness));
            }
        }

        // loop through all the layers and push the canvas to the main bitmap
        mBitmapPaint.setColor(0x77ff0000);
        for (Layer l : layers) {
            l.draw(mCanvas, mBitmapPaint, true, false);
            FileUtil.fileWrite(GlobalClass.log_path, "saving layer. Trans = " + l.seekBarTransparent +
                    "\n\t\t Bitmap " + l.bitmapPath, true);
        }

        return bm;
    }

    public Bitmap saveBitmapLayersForTemplate(Context context, boolean asPicture) {

        int smallWidth = (int) ((ActivityMainEditor.bW - 40) * .4);
        int smallHeight = (int) ((ActivityMainEditor.bH - 40) * .4);
        // create template background
        Bitmap template = null;
        template = Bitmap.createBitmap(smallWidth, smallHeight, Bitmap.Config.ARGB_8888);
        if (GlobalClass.diskCache != null)
            GlobalClass.diskCache.put("TemplateBaseImage", template);

        mCanvas = new Canvas(template);
        if (asPicture) {
            mCanvas.drawColor(getResources().getColor(android.R.color.primary_text_light));
        }
        // loop through all the layers and push the canvas to the main bitmap
        for (Layer l : layers) {
            l.draw(mCanvas, mBitmapPaint, true, true);
        }

        return template;
    }

    public void saveAsTemplate(Context context, String t_name, SavingTemplateListener listener) {
        int index = 0;
        for (Layer l : layers) {
            float[] values = new float[9];
            l.matrix.getValues(values);
            if (l.isLock) {
                Log.i("xxx", "xxx saving template l.isLock = " + l.isLock);
                l.text = l.text + "41411"; // index 4 = isLock
            } else if (l.shadow) {
                l.text = l.text + "51511"; //index 8 = shadow
            }

            GlobalClass.sqLiteHelper.insertTemplateDetails(
                    t_name,
                    "" + index,
                    l.bitmapPath,
                    l.text,
                    "" + l.color,
                    l.seekBarTransparent,
                    values[Matrix.MPERSP_0],
                    values[Matrix.MPERSP_1],
                    values[Matrix.MPERSP_2],
                    values[Matrix.MSCALE_X],
                    values[Matrix.MSCALE_Y],
                    values[Matrix.MSKEW_X],
                    values[Matrix.MSKEW_Y],
                    values[Matrix.MTRANS_X],
                    values[Matrix.MTRANS_Y],
                    l.rotation,
                    l.font_style);
            index = index + 1;
        }
        // save preview bitmap to table "template_preview"
        Bitmap b = saveBitmapLayersForTemplate(context, false);
        GlobalClass.sqLiteHelper.insertTemplatePreview(
                t_name, DbBitmapUtility.getBytes(
                        ImageHelper.scaleWithRespectToAspectRatio(
                                b, b.getWidth(), b.getHeight())));

        if (b != null) {
            b.recycle();
        }

        listener.onSuccessSavingTemplate();
    }

    public boolean applyTemplate(Cursor c, View v, boolean isTemplateMismatch) {
        FileUtil.fileWrite(GlobalClass.log_path, "->>Applying Template", true);
        deletedFonts.clear();
        deletedLogo.clear();

        boolean bRet = true;
        TemplateData data = new TemplateData(ActivityMainEditor.act.getApplicationContext(), c, v);
        TemplateData td = data;
        int layersSize = td.c.getCount();
        Bitmap b = null;
        Layer newLayer = null;
        ActivityMainEditor.resetAcraLogoApplyTempate();
        for (int i = 0; i < layersSize; i++) {
            c.moveToNext();

            FileUtil.fileWrite(GlobalClass.log_path,
                    "->>Layer sequence = " + c.getString(2)
                            + " \n\t\t Bitmap = " + c.getString(3)
                            + ",  \n\t\t Trans = " + c.getString(6)
                    , true);
            if (null != c.getString(3)) {
                // check if the logo is deleted
                File fL = new File(c.getString(3));
                if (fL.isFile() && fL.exists()) {
                    if (b != null) {
                        b.recycle();
                        b = null;
                    }

                    //this will use too much memory
                    b = computeLogoOptimizeDimension(
                            ImageHelper.decodeLogo(
                                    c.getString(3).toString(),
                                    4,
                                    (int) (ActivityMainEditor.DEVICE_WIDTH * .4),
                                    (int) (ActivityMainEditor.DEVICE_HEIGHT * .4)),
                            c.getString(3),
                            ActivityMainEditor.DEVICE_WIDTH,
                            ActivityMainEditor.DEVICE_HEIGHT
                    );

                    if (null == b)
                        continue;

                    GlobalClass.LOGO_APPLY_TEMPLATE_COUNT = GlobalClass.LOGO_APPLY_TEMPLATE_COUNT + 1;
                    ACRA.getErrorReporter().putCustomData(GlobalClass.LOGO_APPLY_TEMPLATE + GlobalClass.LOGO_APPLY_TEMPLATE_COUNT, LogoliciousApp.fileSizeInMb(c.getString(3).toString()));

                    // add image to layer
                    newLayer = new Layer(
                            td.ctx,
                            LayersContainerView.this,
                            b.copy(Config.ARGB_8888, true),
                            c.getString(3),
                            td.v);
                }
            } else {
                // apply to text layer
                if (!fontIsExemption(c.getString(17))) {
                    boolean hasShadow = false;
                    if (!LogoliciousApp.strIsNullOrEmpty(c.getString(4))) {
                        if (c.getString(4).contains("51511")) {
                            hasShadow = true;
                        }
                    }
                    newLayer = new Layer(td.ctx,
                            LayersContainerView.this,
                            c.getString(4),
                            td.v,
                            Integer.parseInt(c.getString(5)),
                            c.getString(17),
                            hasShadow);
                }
            }

            if (null == newLayer)
                continue;

            //add a uniqueID identifier
            newLayer.uniqueID = uniqueIDforEachLayer;
            //iterate uniqueID
            uniqueIDforEachLayer = uniqueIDforEachLayer + 1;

            layers.add(newLayer);

            layers.get(layers.size() - 1).seekBarTransparent = c.getInt(6);

            float[] values = new float[9];
            values[Matrix.MPERSP_0] = c.getFloat(7);
            values[Matrix.MPERSP_1] = c.getFloat(8);
            values[Matrix.MPERSP_2] = c.getFloat(9);
            values[Matrix.MSCALE_X] = c.getFloat(10);
            values[Matrix.MSCALE_Y] = c.getFloat(11);
            values[Matrix.MSKEW_X] = c.getFloat(12);
            values[Matrix.MSKEW_Y] = c.getFloat(13);
            values[Matrix.MTRANS_X] = c.getFloat(14);
            values[Matrix.MTRANS_Y] = c.getFloat(15);
            layers.get(layers.size() - 1).rotation = c.getFloat(16);
            // Apply Matrix for each layer
            layers.get(layers.size() - 1).matrix.setValues(values); // apply to matrix using values

            targetSelected = layers.get(layers.size() - 1);
            //put the item on the center if template AR is not match
            if (isTemplateMismatch) {
                layers.get(layers.size() - 1).centerMismatchTemplate();
            }

            //check locking flag
            layers.get(layers.size() - 1).text = c.getString(4);
            String strLock = layers.get(layers.size() - 1).text;
            if (null != strLock) {
                int indexOfLock = strLock.lastIndexOf("4141");
                Log.i("xxx", "xxx indexOfLock " + indexOfLock);
                if (indexOfLock > -1) {
                    String xx = strLock.substring(indexOfLock, indexOfLock + 5);
                    String strLockText = xx.substring(4, 5);
                    layers.get(layers.size() - 1).isLock = Integer.valueOf(strLockText) == 1 ? true : false;
                    layers.get(layers.size() - 1).text = layers.get(layers.size() - 1).text.replace("41411", "");
                    invalidate();
                }
            }

            //check shadow flag
            layers.get(layers.size() - 1).text = c.getString(4);
            String strShadow = layers.get(layers.size() - 1).text;
            if (null != strShadow) {
                int indexOfShadow = strShadow.lastIndexOf("5151");
                Log.i("xxx", "xxx indexOfShadow " + indexOfShadow);
                if (indexOfShadow > -1) {
                    String xx = strShadow.substring(indexOfShadow, indexOfShadow + 5);
                    String strShadowText = xx.substring(4, 5);
                    layers.get(layers.size() - 1).shadow = Integer.valueOf(strShadowText) == 1 ? true : false;
                    layers.get(layers.size() - 1).text = layers.get(layers.size() - 1).text.replace("51511", "");
                    Log.i("xxx", "xxx abc " + layers.get(layers.size() - 1).text);
                    invalidate();
                }
            }

            putHistory(targetSelected);
        }

        FileUtil.fileWrite(GlobalClass.log_path, "->> refresh layer view", true);
        return bRet;
    }

    public void refreshMe() {
        invalidate();
    }

    class TemplateData {
        private Context ctx;
        private Cursor c;
        private View v;

        public TemplateData(Context ctx, Cursor c, View v) {
            this.ctx = ctx;
            this.c = c;
            this.v = v;
        }
    }

    /**
     * @param fontStyle
     * @return This function will check if the font is not allowed to be use.
     */
    private boolean fontIsExemption(String fontStyle) {
        boolean bRet = false;
        String[] fontList_Exemption = {"new_fonts/Sign-handwriting.ttf", "new_fonts/Hamurz Free Version.ttf"};
        for (String font : fontList_Exemption) {
            if (fontStyle.matches(font)) {
                bRet = true;
            }
        }
        return bRet;
    }

    public static ArrayList<String> geteMissingFonts() {
        return deletedFonts;
    }

    private int YAxisAdjustment = 0;

    public boolean isLayerEmpty() {
        return layers.size() > 0 ? false : true;
    }

    //Undo and Redo Feature
    public static ArrayList<History> undos = new ArrayList<>();
    public static ArrayList<History> redos = new ArrayList<>();
    private static boolean hasMatrixChanges = false;
    private static int currentIdentifier = -1;

    public static class History {
        public Matrix matrixHis = new Matrix();
        public int identifier = 0;
        public String itemPath = "";
        public Layer layerHis;
        public boolean firstIndex = false;
        public boolean lastIndex = false;
        public boolean skipMe = false;
        public float rotation = 0;
        public int color;

        public History() {
            this.itemPath = "";
            this.matrixHis = new Matrix();
        }

        public History(Matrix m, int id, String path, Layer l) {
            this.matrixHis = m;
            this.identifier = id;
            this.itemPath = path;
            this.layerHis = l;
            this.color = l.color;
        }
    }

    private boolean isLastHistoryIndexOfAnItem(History currentData, ArrayList<History> histData) {
        if (null == currentData)
            return false;

        boolean bRet = false;
        int count = 0;
        for (int i = 0; i < histData.size(); i++) {
            if (currentData.identifier == histData.get(i).identifier) {
                count = count + 1;
            }
        }

        if (count == 1)
            bRet = true;

        return bRet;
    }

    /**
     * @param hData
     * @param curData
     * @return Get the next history for the current item(layer).
     */
    private History getNextHistoryForThisItem(ArrayList<History> hData, History curData) {
        History dataReturn = null;
        // for (int i = hData.size() - 1; i >= 0 ; i--) {
        Log.i("xxx", "xxx g getNextHistoryForThisItem index " + curData.identifier + " hData.size() - 1 = " + (hData.size()));
        for (int i = 0; i < hData.size(); i++) {
            Log.i("", "xxx hData size = " + hData.size() + " hData.get(i).identifier = " + hData.get(i).identifier + " curData.identifier = " + curData.identifier);
            if (hData.get(i).identifier == curData.identifier) {
                dataReturn = hData.get(i);
                Log.i("xxx", "xxx getNextHistoryForThisItem index " + dataReturn.identifier);
            }
        }
        return dataReturn;
    }


    public void undo(View v, Activity context) {
        //apply the old transformation of the layer in the layer
        boolean isdoubleClick = false;
        History undoData = null;
        History origData = null;
        if (null != undos) {
            if (undos.size() >= 1) {
                undoData = undos.get(undos.size() - 1);
                origData = undoData;

                //check skipper
                if (true == undoData.skipMe) {
                    Log.i("xxx", "xxx c undo.size()-> " + undos.size() + "\n redos.size()->" + redos.size() + " \nPath:" + undoData.itemPath + "\n Skip:" + undoData.skipMe + "\n # " + undoData.identifier);
                    undos.get(undos.size() - 1).skipMe = false;
                    redos.add(undos.get(undos.size() - 1));
                    undos.remove(undos.get(undos.size() - 1));
                    //since this is a skip, we need to use the next history to be use for transformation
                    if (undos.size() > 0)
                        undoData = getNextHistoryForThisItem(undos, origData);
                } else {
                    Log.i("xxx", "xxx d undo.size()-> " + undos.size() + "\n redos.size()->" + redos.size() + " \n Path:" + undoData.itemPath + "\n Skip:" + undoData.skipMe + "\n # " + undoData.identifier);
                    undos.get(undos.size() - 1).skipMe = false;
                    redos.add(undos.get(undos.size() - 1));
                    undos.remove(undos.get(undos.size() - 1));
                    if (undos.size() > 0)
                        undoData = getNextHistoryForThisItem(undos, origData);
                }

                if (isLastHistoryIndexOfAnItem(undoData, undos)) {
                    Log.i("xxx", "xxx isLastHistoryIndexOfAnItem ");
                    if (undos.size() > 0) {
                        redos.add(undoData);
                        undos.remove(undoData);
                    }
                    if (redos.size() > 0)
                        redos.get(redos.size() - 1).skipMe = true;
                }

            }
        }

        if (null == undoData)
            return;

        //search current layer and apply the previous matrix
        int lyrSize = layers.size() - 1;
        for (int i = lyrSize; i >= 0; i--) {
            Layer l = layers.get(i);
            if (l.uniqueID == undoData.layerHis.uniqueID) {
                layers.get(i).matrix.reset();
                layers.get(i).matrix.preConcat(undoData.matrixHis);
                layers.get(i).rotation = undoData.rotation;
//                layers.get(i).changeTextColor(undoData.color);
                targetSelected = layers.get(i);
                //refresh view
                invalidate();
            }
        }

        Log.i("xxx", "xxx z undo.size()-> " + undos.size() + " redos.size()->" + redos.size());
        updateHistoryButtons(context);
    }

    public void redo(View v, ActivityMainEditor context) {
        //apply the old transformation of the layer in the layer
        boolean isdoubleClick = false;
        History redoData = null;
        History origData = null;
        if (null != redos) {
            if (redos.size() >= 1) {
                redoData = redos.get(redos.size() - 1);
                origData = redoData;

                //check skipper
                if (true == redoData.skipMe) {
//                    Log.i("xxx","xxx c undo.size()-> " + undos.size() + "\n redos.size()->" + redos.size() + " \nPath:" + redoData.itemPath + "\n Skip:" + redoData.skipMe + "\n # " + redoData.identifier);
                    redos.get(redos.size() - 1).skipMe = false;
                    undos.add(redos.get(redos.size() - 1));
                    redos.remove(redos.get(redos.size() - 1));
//                    //since this is a skip, we need to use the next history to be use for transformation
                    if (redos.size() > 0)
                        redoData = getNextHistoryForThisItem(redos, origData);
                } else {
//                    Log.i("xxx","xxx d undo.size()-> " + undos.size() + "\n redos.size()->" + redos.size() + " \n Path:" + redoData.itemPath + "\n Skip:" + redoData.skipMe + "\n # " + redoData.identifier);
                    undos.add(redos.get(redos.size() - 1));
                    redos.remove(redos.get(redos.size() - 1));
                    if (redos.size() > 0)
                        redoData = getNextHistoryForThisItem(redos, origData);
                }

                if (isLastHistoryIndexOfAnItem(redoData, redos)) {
                    Log.i("xxx", "xxx isLastHistoryIndexOfAnItem ");
                    if (redos.size() > 0) {
                        undos.add(redoData);
                        redos.remove(redoData);
                    }
                    if (undos.size() > 0)
                        undos.get(undos.size() - 1).skipMe = true;
                }
            }
        }

        if (null == redoData)
            return;

        //search current layer and apply the previous matrix
        int lyrSize = layers.size() - 1;
        for (int i = lyrSize; i >= 0; i--) {
            Layer l = layers.get(i);
            if (l.uniqueID == redoData.layerHis.uniqueID) {
                layers.get(i).matrix.reset();
                layers.get(i).matrix.preConcat(redoData.matrixHis);
                layers.get(i).rotation = redoData.rotation;
//                layers.get(i).changeTextColor(redoData.color);
                targetSelected = layers.get(i);
                //refresh view
                invalidate();
            }
        }

        updateHistoryButtons(context);
//        Log.i("xxx","xxx undo.size()-> " + undos.size() + " redos.size()->" + redos.size());
    }


    private void deleteHistoryOnDoubleTapDelete(ArrayList<History> undo, ArrayList<History> redo, int uniqueIDforEachLayer) {
        //delete the history if match
        if (null != undo) {
            if (undo.size() > 0) {
                //we should delete from the end to fully delete items
                //note: size will change when one item is deleted
                for (int i = undo.size() - 1; i >= 0; i--) {
                    if (undo.get(i).layerHis.uniqueID == uniqueIDforEachLayer) {
                        undo.remove(i);
                    }
                }
            }
        }

        if (null != redo) {
            if (redo.size() > 0) {
                for (int i = redo.size() - 1; i >= 0; i--) {
                    if (redo.get(i).layerHis.uniqueID == uniqueIDforEachLayer) {
                        redo.remove(i);
                    }
                }
            }
        }

    }

    public void updateHistoryButtons(Activity context) {

        ImageView undo = (ImageView) (context).findViewById(R.id.undo);
        ImageView redo = (ImageView) (context).findViewById(R.id.redo);

        if (undos.size() >= 1)
            LogoliciousApp.setImageViewTint(context, R.id.undo, context.getResources().getColor(R.color.transparent_100));
        else
            LogoliciousApp.setImageViewTint(context, R.id.undo, context.getResources().getColor(R.color.DimGray));

        if (redos.size() >= 1)
            LogoliciousApp.setImageViewTint(context, R.id.redo, context.getResources().getColor(R.color.transparent_100));
        else
            LogoliciousApp.setImageViewTint(context, R.id.redo, context.getResources().getColor(R.color.DimGray));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        // Handle any other event here, if not long press.
        return true;
    }

    public class LongPressGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            if (null != targetSelected) {
                //enable option only on textmode
                if (targetSelected.isTextMode) {
                    longPressOptions(e);
                }
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private ActivityMainEditor.TextLongClickForEdit longClickForTextEdit;

    public void setLongClickInterface(ActivityMainEditor.TextLongClickForEdit longClickForTextEdit) {
        this.longClickForTextEdit = longClickForTextEdit;
    }

    private void longPressOptions(final MotionEvent e) {
        if(null != longClickForTextEdit)
            longClickForTextEdit.onLongClick();
    }

    private void lockUnlockItem(MotionEvent e) {
        int lyrSize = layers.size() - 1;
        for (int i = lyrSize; i >= 0; i--) {
            Layer l = layers.get(i);
            lyrCurrentIdx = i;
            if (l.contains(e)) {
                if (targetSelected.isLock) {
                    targetSelected.isLock = false;
                    targetSelected.touch = false;
                    deleteHistoryOnDoubleTapDelete(undos, redos, targetSelected.uniqueID);
                    updateHistoryButtons(ActivityMainEditor.act);
                    invalidate();
                } else {
                    targetSelected.isLock = true;
                    targetSelected.touch = true;
                    deleteHistoryOnDoubleTapDelete(undos, redos, targetSelected.uniqueID);
                    updateHistoryButtons(ActivityMainEditor.act);
                    invalidate();
                }
            }
        }
    }

    public void rotateLogo0() {
        if (targetSelected != null) {
            targetSelected.rotate0();
            invalidate();

            targetSelected.rotation = 0;
            deleteRedos();
            putHistory(targetSelected);
            updateHistoryButtons(ActivityMainEditor.act);
        }
    }

    public void rotateLogo90() {
        if (targetSelected != null) {
            targetSelected.rotate90();
            invalidate();
            deleteRedos();
            putHistory(targetSelected);
            updateHistoryButtons(ActivityMainEditor.act);
        }
    }

}