package com.romens.extend.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.github.mikephil.charting.utils.Utils;
import com.romens.extend.chart.data.GridData;
import com.romens.extend.chart.data.GridDataSet;
import com.romens.extend.chart.data.GridEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GridChart extends BaseChart implements
        GestureDetector.OnGestureListener {
    public static final int DEFAULT_FONT_SIZE = 14;
    private static final String TAG = "ReportGrid";

    protected GridData mOriginalData = null;

    protected boolean mDataNotSet = true;

    private boolean mOffsetsCalculated = false;

    protected float mOffsetLeft = 8;
    protected float mOffsetTop = 8;
    protected float mOffsetRight = 8;
    protected float mOffsetBottom = 8;

    protected float mCellOffsetLeft = 8;
    protected float mCellOffsetTop = 2;
    protected float mCellOffsetRight = 8;
    protected float mCellOffsetBottom = 2;

    protected float mCellHeight = 32;

    protected float mDivideLine = 1;

    protected TextPaint paintXLabel;
    protected TextPaint paintYLabel;
    protected Paint paintHighLightDraw;
    protected Paint painDraw;
    protected Paint painInfo;
    protected Paint painDrawLine;
    protected TextPaint paintValue;
    protected TextPaint paintFixValue;
    private Paint painScrollBar;
    private Paint painShadow;
    private Paint paintDrop;

    private float mScrollWidth = 8.0f;
    private int mFixedYValues = 0;

    private float mXLabelMaxWidth = 0;
    private boolean mXLabelDisplay = false;

    private ArrayList<Float> mYLabelWidths = new ArrayList<Float>();
    private float mFixYWidth;
    private float mMoveYWidth;


    private String mNoDataText = "无数据";
    private String mNoDataTextDescription;


    private float mDrawContentMoveRowStartY = 0;

    private int mDrawContentMoveRowStartIndex = 0;
    private int mDrawContentMoveRowStopIndex = 0;

    private float mDrawContentMoveColStartX = 0;

    private int mDrawContentMoveColStartIndex = 0;
    private int mDrawContentMoveColStopIndex = 0;

    private int mSelectedRowPosition = SELECT_NONE_FLAG;
    private int mSelectedColPosition = SELECT_NONE_FLAG;

    private float mDrawSelectorRectStartX = 0;
    private float mDrawSelectorRectStartY = 0;

    private Handler mHandler;

    private int mYLabelColor = 0xFF1A237E;
    private int mFixColor = 0xFFC5CAE9;
    private int mRowDivideColor = 0xFFE8EAF6;//0xFFEEEEEE;
    private int mSumColor = 0xFFFFFDE7;// 0xFFE8EAF6;//0xFFFFFDE7;
    private int mScrollColor = 0xFF000000;

    private int mHeadSelectedColor = 0xFF1976D2;
    private int mRowColSelectedColor = 0xFF90CAF9;
    private int mCellSelectedColor = 0xFF90CAF9;


    private ColumnLongPressAction mColLongPressAction = ColumnLongPressAction.None;


    protected enum ColumnLongPressAction {
        Select, None
    }


    private boolean isSetupTemplate = false;
    private boolean isTemplateChanged = false;
    private final ArrayList<Integer> mDisplayYLabels = new ArrayList<Integer>();
    private final List<Integer> filterXLabels = new ArrayList<>();
    private final Handler handler = new Handler();
    protected boolean searching;
    private Timer searchTimer;

    //报表字体
    private int fontSize = DEFAULT_FONT_SIZE;

    /**
     * default constructor for initialization in code
     */
    public GridChart(Context context) {
        super(context);
        init();
    }

    /**
     * constructor for initialization in xml
     */
    public GridChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * even more awesome constructor
     */
    public GridChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setGridStyle(GridStyle style) {
        mYLabelColor = style.getYLabelColor();
        mFixColor = style.getFixColor();
        mRowDivideColor = style.getDividerRowColor();
        mSumColor = style.getSumRowColor();
        mScrollColor = style.getScrollColor();

        mHeadSelectedColor = style.getHeaderSelectedColor();
        mRowColSelectedColor = style.getRowColSelectedColor();
        mCellSelectedColor = style.getCellSelectedColor();
        invalidate();
    }

    public void setOffset(float left, float right, float top, float bottom) {
        this.mOffsetLeft = left;
        this.mOffsetRight = right;
        this.mOffsetTop = top;
        this.mOffsetBottom = bottom;
    }

    protected void init() {
        this.mHandler = new Handler();
        this.mGestureDetector = new GestureDetector(getContext(), this);
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // initialize the utils
        Utils.init(getContext().getResources());

        // do screen density conversions
        mOffsetBottom = (int) Utils.convertDpToPixel(mOffsetBottom);
        mOffsetLeft = (int) Utils.convertDpToPixel(mOffsetLeft);
        mOffsetRight = (int) Utils.convertDpToPixel(mOffsetRight);
        mOffsetTop = (int) Utils.convertDpToPixel(mOffsetTop);

        mCellOffsetBottom = (int) Utils.convertDpToPixel(mCellOffsetBottom);
        mCellOffsetLeft = (int) Utils.convertDpToPixel(mCellOffsetLeft);
        mCellOffsetRight = (int) Utils.convertDpToPixel(mCellOffsetRight);
        mCellOffsetTop = (int) Utils.convertDpToPixel(mCellOffsetTop);

        paintValue = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paintValue.setColor(Color.BLACK);
        paintValue.setTextSize(Utils.convertDpToPixel(fontSize));

        paintFixValue = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paintFixValue.setColor(Color.BLACK);
        paintFixValue.setTextSize(Utils.convertDpToPixel(fontSize));
        paintFixValue.setTypeface(Typeface.create(Typeface.SANS_SERIF,
                Typeface.BOLD));

        paintHighLightDraw = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintHighLightDraw.setStyle(Paint.Style.FILL);

        paintXLabel = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paintXLabel.setColor(Color.BLACK);
        paintXLabel.setTextSize(Utils.convertDpToPixel(fontSize));
        paintXLabel.setTypeface(Typeface.create(Typeface.SANS_SERIF,
                Typeface.BOLD));


        paintYLabel = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paintYLabel.setColor(Color.WHITE);
        paintYLabel.setTextSize(Utils.convertDpToPixel(fontSize));
        paintYLabel.setTypeface(Typeface.create(Typeface.SANS_SERIF,
                Typeface.BOLD));

        painDraw = new Paint(Paint.ANTI_ALIAS_FLAG);
        painDraw.setStyle(Paint.Style.FILL);
        painDraw.setColor(Color.WHITE);


        painInfo = new Paint(Paint.ANTI_ALIAS_FLAG);
        painInfo.setColor(Color.rgb(247, 189, 51)); // orange
        painInfo.setTextAlign(Paint.Align.CENTER);
        painInfo.setTextSize(Utils.convertDpToPixel(fontSize));

        painDrawLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        painDrawLine.setColor(Color.BLACK);
        painDrawLine.setAlpha(32);
        painDrawLine.setStyle(Paint.Style.STROKE);
        painDrawLine.setStrokeWidth(mDivideLine);


        painScrollBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        painScrollBar.setColor(mScrollColor);
        painScrollBar.setStyle(Paint.Style.FILL);
        painScrollBar.setAlpha(64);

        painShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        painShadow.setStyle(Paint.Style.FILL);
        painShadow.setAlpha(215);

        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrop.setStyle(Paint.Style.FILL_AND_STROKE);
        paintDrop.setColor(0xFFe91e63);
        paintDrop.setStrokeWidth(mDivideLine * 3);

        calcCellHeight();
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        paintValue.setTextSize(Utils.convertDpToPixel(fontSize));
        paintFixValue.setTextSize(Utils.convertDpToPixel(fontSize));
        paintXLabel.setTextSize(Utils.convertDpToPixel(fontSize));
        paintYLabel.setTextSize(Utils.convertDpToPixel(fontSize));
        painInfo.setTextSize(Utils.convertDpToPixel(fontSize));
        calcCellHeight();
        invalidate();
    }

    private void calcCellHeight() {
        mCellHeight = 0;
        mathMaxCellHeight(paintXLabel);
        mathMaxCellHeight(paintYLabel);
        mathMaxCellHeight(paintFixValue);
        mathMaxCellHeight(paintValue);
    }

    private void mathMaxCellHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        float cellHeightTemp = mCellOffsetTop + mCellOffsetBottom + (float) Math.ceil(fm.descent - fm.ascent);
        mCellHeight = Math.max(mCellHeight, cellHeightTemp);
    }

    public void setData(GridData<GridDataSet<GridEntry>> data) {
        setData(data, 0);
    }

    public void appendData(GridData appendData) {
        if (appendData == null) {
            return;
        }
        ArrayList<GridDataSet<GridEntry>> appendDataSets = appendData.getDataSets();
        final int appendDataSetCount = appendDataSets == null ? 0 : appendDataSets.size();
        if (appendDataSetCount <= 0 || appendDataSetCount != mOriginalData.getDataSetCount()) {
            return;
        }

        final int appendXValuesCount = appendData.getXValCount();
        if (appendXValuesCount <= 0) {
            return;
        }
        final int originalDataXValCount = mOriginalData.getXValCount();
        int appendXValIndex = originalDataXValCount;
        for (int i = 0; i < appendXValuesCount; i++) {
            mOriginalData.addXVal(String.valueOf(appendXValIndex + 1));
            appendXValIndex++;
        }
        GridDataSet appendDataSet;
        GridEntry appendEntry;
        for (int i = 0; i < appendDataSetCount; i++) {
            appendDataSet = appendDataSets.get(i);
            if (!TextUtils.equals(appendDataSet.getLabel(), mOriginalData.getDataSetByIndex(i).getLabel())) {
                return;
            }
            if (appendXValuesCount != appendDataSet.getEntryCount()) {
                return;
            }
            appendXValIndex = originalDataXValCount;
            for (int j = 0; j < appendXValuesCount; j++) {
                appendEntry = appendDataSet.getEntryForXIndex(j).copy();
                appendEntry.setXIndex(appendXValIndex);
                mOriginalData.addEntry(appendEntry, i);
                appendXValIndex++;
            }
        }
        prepareForAppend();
        onSetupDataAfter();
    }

    public void setData(GridData<GridDataSet<GridEntry>> data, int fixedYValues) {
        this.isSetupTemplate = false;
        this.isTemplateChanged = false;
        this.mFixedYValues = fixedYValues;
        initCharState();
        // if (data == null || !data.isValid()) {
        // Log.e(LOG_TAG,
        // "Cannot set data for chart. Provided chart values are null or contain less than 1 entry.");
        // mDataNotSet = true;
        // return;
        // }

        if (data == null || data.getDataSetCount() <= 0 || data.getXValCount() <= 0) {
            mDataNotSet = true;
            return;
        }

        // Log.i(LOG_TAG, "xvalcount: " + data.getXValCount());
        // Log.i(LOG_TAG, "entrycount: " + data.getYValCount());

        // LET THE CHART KNOW THERE IS DATA
        mDataNotSet = false;
        mOffsetsCalculated = false;
        mOriginalData = data;
        prepare();
        // calculate how many digits are needed
        calcFormats();
        onSetupDataAfter();
    }

    protected void onSetupDataAfter() {
        notifyDataSetChanged();
    }

    public GridData getData() {
        return mOriginalData;
    }

    public boolean isTemplateChanged() {
        return this.isTemplateChanged;
    }


    public void clear() {
        mOriginalData = null;
        mDataNotSet = true;
        initCharState();
        invalidate();
    }

    private void initCharState() {
        mSelectedRowPosition = SELECT_NONE_FLAG;
        mSelectedColPosition = SELECT_NONE_FLAG;
    }

    public void prepare() {

        if (mDataNotSet)
            return;
        prepareXLabels();

        prepareYLabels();

        calculateCellMaxWidth();

        prepareMoveContentRect();
    }

    protected void prepareForAppend() {
        prepareXLabels();
        if (!isSetupTemplate) {
            calculateCellMaxWidth();
        }
        prepareMoveContentRect();
    }

    protected void prepareForTemplate() {
        int count = getDisplayYLabelCount();
        mFixYWidth = 0;
        mMoveYWidth = 0;
        for (int i = 0; i < count; i++) {
            if (i < mFixedYValues) {
                mFixYWidth += calcCellWidth(getGridYLabelWidth(i));
            } else {
                mMoveYWidth += calcCellWidth(getGridYLabelWidth(i));
            }
        }
        prepareMoveContentRect();
    }

    public int convertGridIndexToDataIndex(int gridIndex) {
        int size = mDisplayYLabels == null ? 0 : mDisplayYLabels.size();
        if (gridIndex < 0 || gridIndex >= size) {
            return -1;
        }
        int dataIndex = mDisplayYLabels.get(gridIndex);
        return dataIndex;
    }

    protected float getGridYLabelWidth(int gridIndex) {
        int dataIndex = convertGridIndexToDataIndex(gridIndex);
        if (dataIndex >= 0) {
            return mYLabelWidths.get(dataIndex);
        }
        return 0;
    }

    protected int getDisplayYLabelCount() {
        return mDisplayYLabels.size();
    }

    protected void prepareXLabels() {
        int xValCount = mOriginalData.getXValCount();
        if (xValCount > 0) {
            mXLabelDisplay = true;
            String labelStr = mOriginalData.getXVal(xValCount - 1);
            mXLabelMaxWidth = calcTextWidth(paintXLabel, labelStr);
        } else {
            mXLabelDisplay = false;
            mXLabelMaxWidth = 0;
        }
    }

    protected void prepareYLabels() {
        int count = mOriginalData.getDataSetCount();
        mYLabelWidths.clear();
        mDisplayYLabels.clear();
        for (int i = 0; i < count; i++) {
            mYLabelWidths.add(i, calcTextWidth(paintYLabel, getGridDataSetForDataIndex(i).getLabel()));
            mDisplayYLabels.add(i);
        }
    }

    protected void calculateCellMaxWidth() {
        int count = getDisplayYLabelCount();
        mFixYWidth = 0;
        mMoveYWidth = 0;
        float cellWidth;
        for (int i = 0; i < count; i++) {
            cellWidth = calcTextWidth(paintYLabel, getGridDataSetForColIndex(i).getMaxLengthVal());
            mYLabelWidths.set(convertGridIndexToDataIndex(i), calcMaxWidth(cellWidth, getGridYLabelWidth(i)));
            if (i < mFixedYValues) {
                mFixYWidth += calcCellWidth(getGridYLabelWidth(i));
            } else {
                mMoveYWidth += calcCellWidth(getGridYLabelWidth(i));
            }
        }
    }


    private void prepareScrollBorder() {
        xMax = mMoveContentRect.left + mMoveYWidth - mMoveContentRect.right;
        if (xMax < 0.0F) {
            xMax = 0.0F;
        }
        int xValCount = getChartRowCount();
        float height = mChartRect.bottom - mChartRect.top;
        yMax = mCellHeight * (xValCount + 2) - height;
        if (yMax < 0.0F) {
            yMax = 0.0F;
        }
    }

    public void calcFormats() {

    }


    public static float calcTextWidth(Paint paint, String text) {
        return TextUtils.isEmpty(text) ? 0 : paint.measureText(text, 0, text.length());
    }

    public static float calcMaxWidth(float a, float b) {
        return Math.max(a, b);
    }

    public void setNoDataText(String text) {
        mNoDataText = text;
        invalidate();
    }

    public void setNoDataTextDescription(String text) {
        mNoDataTextDescription = text;
    }

    protected RectF mChartRect = new RectF();
    protected RectF mMoveContentRect = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (mDataNotSet) { // check if there is data

            // if no data, inform the user
            canvas.drawText(mNoDataText, getWidth() / 2, getHeight() / 2, painInfo);

            if (!TextUtils.isEmpty(mNoDataTextDescription)) {
                float textOffset = -painInfo.ascent() + painInfo.descent();
                canvas.drawText(mNoDataTextDescription, getWidth() / 2, (getHeight() / 2)
                        + textOffset, painInfo);
            }
            return;
        }
        prepareMoveContentRect();


        calcDrawContentXYPosition();


        //background
        drawGridBackground(canvas);
        drawGridSelector(canvas);
        //border
        drawChartBorder(canvas);
        drawHorizontalGrid(canvas);
        drawVerticalGrid(canvas);

        //label
        drawXLabel(canvas);
        drawYLabel(canvas);
        drawValues(canvas);
        drawSums(canvas);

        drawScrollBar(canvas);
        drawShadow(canvas);
        drawGridDropColumnLabel(canvas);
    }

    protected void prepareMoveContentRect() {
        float fixGridWidth = calcCellWidth(mXLabelMaxWidth) + mFixYWidth;
        mMoveContentRect.set(mChartRect.left + fixGridWidth, mChartRect.top + mCellHeight, mChartRect.right, mChartRect.bottom - mCellHeight);
        prepareScrollBorder();
    }

    private void drawGridDropColumnLabel(Canvas canvas) {
        if (mColLongPressAction == ColumnLongPressAction.Select) {
            final float dropLeftX = mDrawSelectorRectStartX + mDropColBorderMovedLeftSpace;
            final float dropRightX = mDrawSelectorRectStartX + calcCellWidth(getGridYLabelWidth(mSelectedColPosition)) + mDropColBorderMovedRightSpace;
            final float startY = mChartRect.top;
            final float radius = mCellHeight / 2;
            final float stopY = mChartRect.top + mCellHeight + radius;

            int saveCount = canvas.save();
            if (mSelectedColPosition < mFixedYValues) {
                canvas.clipRect(mChartRect.left + calcCellWidth(mXLabelMaxWidth), mChartRect.top, mMoveContentRect.left, mChartRect.bottom);
            } else {
                canvas.clipRect(mMoveContentRect.left, mChartRect.top, mMoveContentRect.right, mChartRect.bottom);
            }


            RectF colChangeLeftRect = null;
            if (mDropColBorderMovedLeftSpace > 0) {
                colChangeLeftRect = new RectF(mDrawSelectorRectStartX, mChartRect.top, dropLeftX, mChartRect.bottom);
            } else if (mDropColBorderMovedLeftSpace < 0) {
                colChangeLeftRect = new RectF(dropLeftX, mChartRect.top, mDrawSelectorRectStartX, mChartRect.bottom);
            }
            if (colChangeLeftRect != null) {
                paintHighLightDraw.setColor(0xFFF8BBD0);
                canvas.drawRect(colChangeLeftRect, paintHighLightDraw);
            }

            RectF colChangeRightRect = null;
            float dropColumnSelectorEndX = mDrawSelectorRectStartX + calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
            if (mDropColBorderMovedRightSpace < 0) {
                colChangeRightRect = new RectF(dropRightX, mChartRect.top, dropColumnSelectorEndX, mChartRect.bottom);
            } else if (mDropColBorderMovedRightSpace > 0) {
                colChangeRightRect = new RectF(dropColumnSelectorEndX, mChartRect.top, dropRightX, mChartRect.bottom);
            }
            if (colChangeRightRect != null) {
                paintHighLightDraw.setColor(0xFFF8BBD0);
                canvas.drawRect(colChangeRightRect, paintHighLightDraw);
            }
            if (dropLeftX >= mMoveContentRect.left && dropLeftX <= mMoveContentRect.right) {
                canvas.drawLine(dropLeftX, startY, dropLeftX, stopY, paintDrop);
                canvas.drawCircle(dropLeftX, stopY, radius, paintDrop);
            }
            if (dropRightX >= mMoveContentRect.left && dropRightX <= mMoveContentRect.right) {
                canvas.drawLine(dropRightX, startY, dropRightX, stopY, paintDrop);
                canvas.drawCircle(dropRightX, stopY, radius, paintDrop);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    public static final int SELECT_ROW_COL_HEAD_FLAG = -1;
    public static final int SELECT_SUM_FLAG = -2;
    public static final int SELECT_NONE_FLAG = -3;

    private void drawGridSelector(Canvas canvas) {
        if (mSelectedColPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedRowPosition == SELECT_ROW_COL_HEAD_FLAG) {
            return;
        }
        if (mSelectedColPosition == SELECT_SUM_FLAG && mSelectedRowPosition == SELECT_SUM_FLAG) {
            return;
        }
        if (mSelectedColPosition == SELECT_NONE_FLAG || mSelectedRowPosition == SELECT_NONE_FLAG) {
            return;
        }
        if (mSelectedColPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedRowPosition >= 0) {
            drawGridRowSelector(canvas);
        } else if (mSelectedRowPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedColPosition >= 0) {
            drawGridColSelector(canvas);
        } else {
            drawGridCellSelector(canvas);
        }
    }

    private void drawGridRowSelector(Canvas canvas) {
        //选择的行必须在显示区域内
        if (mSelectedRowPosition >= mDrawContentMoveRowStartIndex && mSelectedRowPosition <= mDrawContentMoveRowStopIndex) {
            int saveCount = canvas.save();
            canvas.clipRect(mChartRect.left, mMoveContentRect.top, mChartRect.right, mMoveContentRect.bottom);
            float top = mDrawSelectorRectStartY;
            float rowHeadWidth = calcCellWidth(mXLabelMaxWidth);
            //Head
            paintHighLightDraw.setColor(mHeadSelectedColor);
            RectF rowRect = new RectF(mChartRect.left, top, mChartRect.left + rowHeadWidth, top + mCellHeight);
            canvas.drawRect(rowRect, paintHighLightDraw);
            //Content
            paintHighLightDraw.setColor(mRowColSelectedColor);
            rowRect = new RectF(mChartRect.left + rowHeadWidth, top, mChartRect.right, top + mCellHeight);
            canvas.drawRect(rowRect, paintHighLightDraw);
            canvas.restoreToCount(saveCount);
        }
    }


    private void drawGridColSelector(Canvas canvas) {
        if (mSelectedColPosition >= 0 && mSelectedColPosition < mFixedYValues) {
            drawGridFixColSelector(canvas);
        } else if (mSelectedColPosition >= mDrawContentMoveColStartIndex && mSelectedColPosition <= mDrawContentMoveColStopIndex) {
            drawGridMoveColSelector(canvas);
        }
    }

    private void drawGridFixColSelector(Canvas canvas) {
        float left = mDrawSelectorRectStartX;
        float right = left + calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
        RectF colRect = new RectF(left, mChartRect.top, right, mChartRect.bottom);
        int saveCount = canvas.save();
        canvas.clipRect(mChartRect.left, colRect.top, mMoveContentRect.left, colRect.bottom);
        //Head
        paintHighLightDraw.setColor(mHeadSelectedColor);
        canvas.drawRect(colRect.left, colRect.top, colRect.right, colRect.top + mCellHeight, paintHighLightDraw);
        //Content
        paintHighLightDraw.setColor(mRowColSelectedColor);
        canvas.drawRect(colRect.left, colRect.top + mCellHeight, colRect.right, colRect.bottom, paintHighLightDraw);
        canvas.restoreToCount(saveCount);
    }

    private void drawGridMoveColSelector(Canvas canvas) {
        float left = mDrawSelectorRectStartX;
        float right = left + calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
        RectF colRect = new RectF(left, mChartRect.top, right, mChartRect.bottom);
        int saveCount = canvas.save();
        canvas.clipRect(mMoveContentRect.left, colRect.top, mMoveContentRect.right, mChartRect.bottom);
        //Head
        paintHighLightDraw.setColor(mHeadSelectedColor);
        canvas.drawRect(colRect.left, colRect.top, colRect.right, colRect.top + mCellHeight, paintHighLightDraw);
        //Content
        paintHighLightDraw.setColor(mRowColSelectedColor);
        canvas.drawRect(colRect.left, colRect.top + mCellHeight, colRect.right, colRect.bottom, paintHighLightDraw);
        canvas.restoreToCount(saveCount);
    }


    private void drawGridCellSelector(Canvas canvas) {
        if (mSelectedRowPosition < mDrawContentMoveRowStartIndex) {
            return;
        }
        if (mSelectedRowPosition > mDrawContentMoveRowStopIndex) {
            return;
        }
        if (mSelectedColPosition < 0) {
            return;
        }
        if (mSelectedColPosition >= mFixedYValues && mSelectedColPosition < mDrawContentMoveColStartIndex) {
            return;
        }
        if (mSelectedColPosition > mDrawContentMoveColStopIndex) {
            return;
        }

        int saveCount = canvas.save();
        if (mSelectedColPosition >= 0 && mSelectedColPosition < mFixedYValues) {
            canvas.clipRect(mChartRect.left, mMoveContentRect.top, mMoveContentRect.left, mMoveContentRect.bottom);
        } else {
            canvas.clipRect(mMoveContentRect.left, mMoveContentRect.top, mMoveContentRect.right, mMoveContentRect.bottom);
        }
        float top = mDrawSelectorRectStartY;
        float left = mDrawSelectorRectStartX;
        float right = left + calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
        float bottom = top + mCellHeight;
        RectF cellRect = new RectF(left, top, right, bottom);
        paintHighLightDraw.setColor(mCellSelectedColor);
        canvas.drawRect(cellRect, paintHighLightDraw);
        canvas.restoreToCount(saveCount);
    }

    private void drawXLabel(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.clipRect(mChartRect.left, mMoveContentRect.top, mChartRect.right, mMoveContentRect.bottom);
        float startX = mChartRect.left + mCellOffsetLeft;
        float startY = mDrawContentMoveRowStartY + mCellOffsetTop;
        float width = mXLabelMaxWidth;
        for (int i = mDrawContentMoveRowStartIndex; i <= mDrawContentMoveRowStopIndex; i++) {
            if (mSelectedColPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedRowPosition == i) {
                paintXLabel.setColor(Color.WHITE);
            } else {
                paintXLabel.setColor(Color.BLACK);
            }
            drawText(getXLabel(i), canvas, paintXLabel, startX, startY, width, Layout.Alignment.ALIGN_CENTER);
            startY += mCellHeight;
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawYLabel(Canvas canvas) {
        int saveCount;
        float xLabelWidth = calcCellWidth(mXLabelMaxWidth);
        float startX = mChartRect.left + xLabelWidth + mCellOffsetLeft;
        float startY = mChartRect.top + mCellOffsetTop;
        if (mFixedYValues > 0) {
            saveCount = canvas.save();
            canvas.clipRect(mChartRect);
            //draw fix cols
            for (int i = 0; i < mFixedYValues; i++) {
                float width = getGridYLabelWidth(i);
                drawText(getGridDataSetForColIndex(i).getLabel(), canvas, paintYLabel, startX, startY, width, Layout.Alignment.ALIGN_CENTER);
                startX += calcCellWidth(width);
            }
            canvas.restoreToCount(saveCount);
        }
        //draw  move
        saveCount = canvas.save();
        canvas.clipRect(mMoveContentRect.left, startY, mMoveContentRect.right, startY + mCellHeight);
        startX = mDrawContentMoveColStartX + mCellOffsetLeft;
        for (int i = mDrawContentMoveColStartIndex; i <= mDrawContentMoveColStopIndex; i++) {
            float width = getGridYLabelWidth(i);
            drawText(getGridDataSetForColIndex(i).getLabel(), canvas, paintYLabel, startX, startY, width, Layout.Alignment.ALIGN_CENTER);
            startX += calcCellWidth(width);
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawValues(Canvas canvas) {
        int saveCount = canvas.save();
        float xLabelWidth = calcCellWidth(mXLabelMaxWidth);
        float left = mChartRect.left + xLabelWidth;
        canvas.clipRect(left, mMoveContentRect.top, mMoveContentRect.left, mMoveContentRect.bottom);
        //draw fix values
        float startY;
        float startX = left + mCellOffsetLeft;
        GridDataSet dataSet;
        GridEntry entry;
        for (int i = 0; i < mFixedYValues; i++) {
            startY = mDrawContentMoveRowStartY + mCellOffsetTop;
            dataSet = getGridDataSetForColIndex(i);
            float width = getGridYLabelWidth(i);
            for (int j = mDrawContentMoveRowStartIndex; j <= mDrawContentMoveRowStopIndex; j++) {
                int xLabelIndex = getXLabelIndex(j);
                entry = dataSet.getEntryForXIndex(xLabelIndex);
                drawText(entry.getVal(), canvas, paintFixValue, startX, startY, width, getEntryAli(entry));
                startY += mCellHeight;
            }
            startX += calcCellWidth(width);
        }
        canvas.restoreToCount(saveCount);

        //draw  move
        saveCount = canvas.save();
        canvas.clipRect(mMoveContentRect.left, mMoveContentRect.top, mMoveContentRect.right, mMoveContentRect.bottom);
        startX = mDrawContentMoveColStartX + mCellOffsetLeft;
        for (int i = mDrawContentMoveColStartIndex; i <= mDrawContentMoveColStopIndex; i++) {
            startY = mDrawContentMoveRowStartY + mCellOffsetTop;
            dataSet = getGridDataSetForColIndex(i);
            float width = getGridYLabelWidth(i);
            for (int j = mDrawContentMoveRowStartIndex; j <= mDrawContentMoveRowStopIndex; j++) {
                if (j >= 200) {
                    Log.v(TAG, "i" + j);
                }
                int xLabelIndex = getXLabelIndex(j);
                entry = dataSet.getEntryForXIndex(xLabelIndex);
                drawText(entry.getVal(), canvas, paintValue, startX, startY, width, getEntryAli(entry));
                startY += mCellHeight;
            }
            startX += calcCellWidth(width);
        }
        canvas.restoreToCount(saveCount);
    }

    protected Layout.Alignment getEntryAli(GridEntry entry) {
        GridEntry.DataType type = entry.getDataType();
        if (type == GridEntry.DataType.Number) {
            return Layout.Alignment.ALIGN_OPPOSITE;
        }
        return Layout.Alignment.ALIGN_NORMAL;
    }

    private void drawSums(Canvas canvas) {
        int saveCount = canvas.save();
        float xLabelWidth = calcCellWidth(mXLabelMaxWidth);
        float left = mChartRect.left + xLabelWidth;
        float top = mChartRect.bottom - mCellHeight;
        canvas.clipRect(left, top, mMoveContentRect.left, mChartRect.bottom);
        //draw fix values
        float startY;
        float startX = left + mCellOffsetLeft;
        GridDataSet dataSet;
        GridEntry entry;
        if (getDataRowCount() > 0) {
            for (int i = 0; i < mFixedYValues; i++) {
                startY = top + mCellOffsetTop;
                dataSet = getGridDataSetForColIndex(i);
                float width = getGridYLabelWidth(i);
//                if (dataSet.getEntryForXIndex(0).enableSum()) {
//                    drawText(FormatUtils.formatMoney(dataSet.getYValueSum()), canvas, paintFixValue, startX, startY, width, Layout.Alignment.ALIGN_OPPOSITE);
//                }
                if (dataSet.enableAmount()) {
                    drawText(dataSet.getAmountValueText(), canvas, paintFixValue, startX, startY, width, Layout.Alignment.ALIGN_OPPOSITE);
                }
                startX += calcCellWidth(width);
            }
        }
        canvas.restoreToCount(saveCount);

        //draw  move
        saveCount = canvas.save();
        left = mMoveContentRect.left;
        top = mChartRect.bottom - mCellHeight;
        canvas.clipRect(left, top, mMoveContentRect.right, mChartRect.bottom);
        startX = mDrawContentMoveColStartX + mCellOffsetLeft;
        for (int i = mDrawContentMoveColStartIndex; i <= mDrawContentMoveColStopIndex; i++) {
            startY = top + mCellOffsetTop;
            dataSet = getGridDataSetForColIndex(i);
            float width = getGridYLabelWidth(i);
            if (dataSet.enableAmount()) {
                drawText(dataSet.getAmountValueText(), canvas, paintValue, startX, startY, width, Layout.Alignment.ALIGN_OPPOSITE);
            }
            startX += calcCellWidth(width);
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawGridBackground(Canvas canvas) {
        //fix
        int saveCount = canvas.save();
        canvas.clipRect(mChartRect);
        //Draw Head
        paintHighLightDraw.setColor(mYLabelColor);
        canvas.drawRect(mChartRect.left, mChartRect.top, mChartRect.right, mChartRect.top + mCellHeight, paintHighLightDraw);
        //Draw Fix
        paintHighLightDraw.setColor(mFixColor);
        canvas.drawRect(mChartRect.left, mChartRect.top + mCellHeight, mMoveContentRect.left, mChartRect.bottom, paintHighLightDraw);
        canvas.restoreToCount(saveCount);
        //Move
        saveCount = canvas.save();
        RectF contentRect = new RectF(mMoveContentRect.left, mMoveContentRect.top, mMoveContentRect.right, mMoveContentRect.bottom);
        canvas.clipRect(contentRect);
        paintHighLightDraw.setColor(Color.WHITE);
        canvas.drawRect(contentRect, paintHighLightDraw);
        paintHighLightDraw.setColor(mRowDivideColor);
        //Separate
        float drawTop = mDrawContentMoveRowStartY;
        for (int i = mDrawContentMoveRowStartIndex; i <= mDrawContentMoveRowStopIndex; i++) {
            if ((i + 1) % 2 == 0) {
                canvas.drawRect(mMoveContentRect.left, drawTop, mMoveContentRect.right, drawTop + mCellHeight, paintHighLightDraw);
            }
            drawTop += mCellHeight;
        }
        canvas.restoreToCount(saveCount);
        //sum
        saveCount = canvas.save();
        RectF sumRect = new RectF(mMoveContentRect.left, mChartRect.bottom - mCellHeight, mMoveContentRect.right, mChartRect.bottom);
        canvas.clipRect(sumRect);
        paintHighLightDraw.setColor(mSumColor);
        canvas.drawRect(sumRect, paintHighLightDraw);
        canvas.restoreToCount(saveCount);
    }


    /**
     * 画滚动条
     *
     * @param canvas 画布
     */
    private void drawScrollBar(Canvas canvas) {
        // 画滚动条
        int saveCount = canvas.save();
        float f1 = mCellHeight * (1 + getChartRowCount());
        float left = mMoveContentRect.left;
        float right = mMoveContentRect.right;
        float top = mMoveContentRect.top;
        float bottom = mMoveContentRect.bottom;

        canvas.clipRect(left, top, right, bottom);
        float width = right - left;
        if (mMoveYWidth > width) {
            float f2 = mMoveYWidth;
            float f3 = width;
            float f4 = f3 * f3 / f2;
            float f5 = left + xPos / (f2 - f4) * (f3 - f3 * f4 / f2);
            canvas.drawRect(f5, bottom - mScrollWidth,
                    f5 + f4, bottom, painScrollBar);
        }
        float height = bottom - top;
        if (f1 > height) {
            float f6 = f1 - top;
            float f7 = height;
            float f8 = f7 * f7 / f6;
            float f9 = top + yPos / (f6 - f8) * (f7 - f7 * f8 / f6);
            canvas.drawRect(right - mScrollWidth, f9,
                    right, f9 + f8, painScrollBar);
        }
        canvas.restoreToCount(saveCount);
    }

    /**
     * 画标题栏阴影
     *
     * @param canvas 画布
     */
    private void drawShadow(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.clipRect(mChartRect);
        if (mDrawContentMoveRowStartY < mMoveContentRect.top) {
            float height = mCellHeight / 2;
            painShadow.setShader(new LinearGradient(mChartRect.left, mMoveContentRect.top, mChartRect.left, mMoveContentRect.top + height,
                    new int[]{Color.BLACK, Color.TRANSPARENT}, null, Shader.TileMode.REPEAT));
            canvas.drawRect(mChartRect.left, mMoveContentRect.top, mChartRect.right, mMoveContentRect.top + height - mDivideLine,
                    painShadow);
        }
        if (mDrawContentMoveColStartX < mMoveContentRect.left) {
            float width = mCellHeight / 2;
            painShadow.setShader(new LinearGradient(mMoveContentRect.left, mChartRect.bottom - mMoveContentRect.top,
                    mMoveContentRect.left + width, mChartRect.bottom - mMoveContentRect.top, new int[]{Color.BLACK, Color.TRANSPARENT},
                    null, Shader.TileMode.REPEAT));
            canvas.drawRect(mMoveContentRect.left, mMoveContentRect.top, mMoveContentRect.left + width, mChartRect.bottom,
                    painShadow);
        }
        canvas.restoreToCount(saveCount);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        prepareChartRect();
    }

    private void calcDrawContentXYPosition() {
        //calc row  position
        float moveY = -yPos + mOffsetTop + mCellHeight;
        Log.v(TAG, "moveY:" + moveY);
        int yCount = getChartRowCount();
        int cursorYPosition;
        for (cursorYPosition = 0; cursorYPosition < yCount; cursorYPosition++) {
            moveY += mCellHeight;
            if (moveY > mMoveContentRect.top) {
                mDrawContentMoveRowStartY = moveY - mCellHeight;
                mDrawContentMoveRowStartIndex = cursorYPosition;
                break;
            } else if (moveY == mMoveContentRect.top) {
                //2016-02-16 小概率BUG修复
                cursorYPosition++;
                moveY += mCellHeight;
                mDrawContentMoveRowStartY = mMoveContentRect.top;
                mDrawContentMoveRowStartIndex = cursorYPosition;
                break;
            }
        }
        final float rowEndY = mMoveContentRect.bottom;
        mDrawContentMoveRowStopIndex = mDrawContentMoveRowStartIndex;
        for (int j = (cursorYPosition + 1); j < yCount; j++) {
            moveY += mCellHeight;
            if (moveY > rowEndY) {
                mDrawContentMoveRowStopIndex = j;
                break;
            } else if (moveY == rowEndY) {
                mDrawContentMoveRowStopIndex = j;
                break;
            } else {
                mDrawContentMoveRowStopIndex = j;
            }
        }

        //calc col position
        final float currXPos = xPos;
        int xCount = getDisplayYLabelCount();
        float moveX = -currXPos + mMoveContentRect.left;

        int cursorXPosition;
        for (cursorXPosition = mFixedYValues; cursorXPosition < xCount; cursorXPosition++) {
            moveX += calcCellWidth(getGridYLabelWidth(cursorXPosition));
            if (moveX > mMoveContentRect.left) {
                mDrawContentMoveColStartX = moveX - calcCellWidth(getGridYLabelWidth(cursorXPosition));
                mDrawContentMoveColStartIndex = cursorXPosition;
                break;
            } else if (moveX == mMoveContentRect.left) {
                mDrawContentMoveColStartX = mMoveContentRect.left;
                mDrawContentMoveColStartIndex = cursorXPosition;
                break;
            }
        }
        mDrawContentMoveColStopIndex = mDrawContentMoveColStartIndex;
        for (int j = (cursorXPosition + 1); j < xCount; j++) {
            moveX += calcCellWidth(getGridYLabelWidth(j));
            if (moveX > mMoveContentRect.right) {
                mDrawContentMoveColStopIndex = j;
                break;
            } else if (moveX == mMoveContentRect.right) {
                mDrawContentMoveColStopIndex = j;
                break;
            } else {
                mDrawContentMoveColStopIndex = j;
            }
        }
        //Selector
        calcSelectorRectBorder();
    }

    protected float calcCellWidth(float cellValWidth) {
        return cellValWidth + mCellOffsetLeft + mCellOffsetRight;
    }

    protected void prepareChartRect() {
        mChartRect.set(mOffsetLeft,
                mOffsetTop,
                getWidth() - mOffsetRight,
                getHeight() - mOffsetBottom);
    }

    private void drawChartBorder(Canvas canvas) {
        canvas.save();
        canvas.clipRect(mChartRect);
        canvas.drawRect(mChartRect, painDrawLine);
        canvas.restore();
    }

    private void drawHorizontalGrid(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.clipRect(mChartRect);
        //draw top
        float startX = mChartRect.left;
        float stopX = mChartRect.right;
        float y = mChartRect.top + mCellHeight;
        canvas.drawLine(startX, y, stopX, y, painDrawLine);
        //draw bottom
        y = mChartRect.bottom - mCellHeight;
        canvas.drawLine(startX, y, stopX, y, painDrawLine);
        canvas.restoreToCount(saveCount);
        //draw content
        saveCount = canvas.save();
        canvas.clipRect(mChartRect.left, mMoveContentRect.top, mChartRect.right, mMoveContentRect.bottom);
        y = mDrawContentMoveRowStartY;
        for (int i = mDrawContentMoveRowStartIndex; i <= mDrawContentMoveRowStopIndex; i++) {
            y += mCellHeight;
            canvas.drawLine(startX, y, stopX, y, painDrawLine);
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawVerticalGrid(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.clipRect(mChartRect);
        float startY = mChartRect.top;
        float stopY = mChartRect.bottom;
        //draw XLabel
        float x = mChartRect.left + calcCellWidth(mXLabelMaxWidth);
        canvas.drawLine(x, startY, x, stopY, painDrawLine);
        //draw fix
        for (int i = 0; i < mFixedYValues; i++) {
            x += calcCellWidth(getGridYLabelWidth(i));
            canvas.drawLine(x, startY, x, stopY, painDrawLine);
        }
        canvas.restoreToCount(saveCount);
        //draw  move
        saveCount = canvas.save();
        canvas.clipRect(mMoveContentRect.left, mChartRect.top, mMoveContentRect.right, mChartRect.bottom);
        x = mDrawContentMoveColStartX;
        for (int i = mDrawContentMoveColStartIndex; i <= mDrawContentMoveColStopIndex; i++) {
            x += calcCellWidth(getGridYLabelWidth(i));
            canvas.drawLine(x, startY, x, stopY, painDrawLine);
        }
        canvas.restoreToCount(saveCount);
    }

    protected static final void drawText(CharSequence text, Canvas canvas, TextPaint paint, float startX, float startY, float width, Layout.Alignment alignment) {
        int saveCount = canvas.save();
        canvas.translate(startX, startY);
        StaticLayout layout = createStaticLayout(text, paint, width, alignment);
        layout.draw(canvas);
        canvas.restoreToCount(saveCount);
    }


    protected static final StaticLayout createStaticLayout(CharSequence text, TextPaint paint, float width, Layout.Alignment alignment) {
        StaticLayout layout;
        text = TextUtils.ellipsize(text, paint, width, TruncateAt.END);
        TruncateAt localTruncateAt = TruncateAt.END;
        layout = new StaticLayout(text, 0, text.length(), paint, (int) width, alignment, 1.0f, 0.0f,
                true, localTruncateAt, (int) width);
        return layout;
    }

    private GestureDetector mGestureDetector;
    private Scroller mScroller;

    private float xPos = 0.0F;
    private float yPos = 0.0F;
    private float xMax = 0.0F;
    private float yMax = 0.0F;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mIsDropColBorderMoving) {
                tryHandleDropColumnChanged();
                return true;
            }
        }
        boolean result = setMotionEvent(event);
        return result;
    }

    private void tryHandleDropColumnChanged() {
        mIsDropColBorderMoving = false;
        final float columnChange = mDropColBorderMovedRightSpace - mDropColBorderMovedLeftSpace;
        float oldColumnWidth = getGridYLabelWidth(mSelectedColPosition);
        float newColumnWidth = oldColumnWidth + columnChange;
        mYLabelWidths.set(convertGridIndexToDataIndex(mSelectedColPosition), newColumnWidth);
        if (mSelectedColPosition < mFixedYValues) {
            mFixYWidth += columnChange;
        } else {
            mMoveYWidth += columnChange;
        }
        mColLongPressAction = ColumnLongPressAction.None;
        mDropColBorderAction = DropColumnBorderAction.None;
        mDropColBorderMovedLeftSpace = 0;
        mDropColBorderMovedRightSpace = 0;
        isTemplateChanged = true;
        invalidate();
    }

    protected boolean setMotionEvent(MotionEvent paramMotionEvent) {
        return mGestureDetector.onTouchEvent(paramMotionEvent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mScroller != null) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // LogUtil.v(TAG, "#onFling# run this");
        this.mScroller = new Scroller(getContext());
        // LogUtil.v("ReportGrid", "onFling:xPos=>" + xPos + "//xMax=>" + xMax
        // + "//yPos=>" + yPos + "//yMax=>" + yMax);
        this.mScroller.fling((int) xPos, (int) yPos, -1 * (int) velocityX, -1
                * (int) velocityY, 0, (int) xMax, 0, (int) yMax);
        this.mHandler.post(this.mFlingRunnable);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // LogUtil.v(TAG, "onLongPress run this");
        mIsMoveStarted = false;
        boolean ret = calcSelectPosition(e);
        if (ret && mSelectedRowPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedColPosition >= 0) {
            mColLongPressAction = ColumnLongPressAction.Select;
            mDropColBorderMovedLeftSpace = 0;
            mDropColBorderMovedRightSpace = 0;
            invalidate();
            if (this.onLongClickListener != null) {
                this.onLongClickListener.onLongClick(getRootView());
            }
        }
    }

    private void clearLongSelectedState() {
        mColLongPressAction = ColumnLongPressAction.None;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        this.mIsMoveStarted = true;
        if ((!isEmpty()) && mIsMoveStarted) {
            if (mIsDropColBorderMoving) {
                if (mDropColBorderAction == DropColumnBorderAction.None) {
                    return true;
                }
                float dropX = getDrawingDropColumnX(distanceX);
                if (mSelectedColPosition >= mFixedYValues) {
                    if (dropX >= mMoveContentRect.left) {
                        if (dropX > mMoveContentRect.right) {
                            dropX = mMoveContentRect.right;
                        }
                    } else {
                        dropX = mMoveContentRect.left;
                    }
                } else {
                    if (dropX >= mChartRect.left) {
                        if (dropX > mMoveContentRect.left) {
                            dropX = mMoveContentRect.left;
                        }
                    } else {
                        dropX = mChartRect.left;
                    }
                }
                setDrawingDropColumnX(dropX);
                invalidate();
                return true;
            }
            this.xPos = distanceX + this.xPos;
            if (this.xPos >= 0.0F) {
                if (this.xPos <= this.xMax) {

                } else {
                    this.xPos = this.xMax;
                }
            } else {
                this.xPos = 0.0F;
            }
            int displayCount = getDisplayYLabelCount();
            if (displayCount > 0) {
                this.yPos = (distanceY + this.yPos);
                if (this.yPos >= 0.0F) {
                    if (this.yPos <= this.yMax) {

                    } else {
                        this.yPos = this.yMax;
                    }
                } else {
                    this.yPos = 0.0F;
                }
            }
            invalidate();
        }
        return true;
    }

    /**
     * 内部检测当前变化的列大小标识计算
     *
     * @return
     */
    private float getDrawingDropColumnX(float distanceX) {
        float x = 0;
        float columnWidth = calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
        float miniWidth = Math.min(mCellHeight, columnWidth / 5);
        final float dropLeftX = mDrawSelectorRectStartX + mDropColBorderMovedLeftSpace;
        if (mDropColBorderAction == DropColumnBorderAction.Left) {
            //触发的是变化的列左边缘
            x = mDrawSelectorRectStartX + mDropColBorderMovedLeftSpace - distanceX;
            float rightBorder = mDrawSelectorRectStartX + calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
            //检测触发的当前坐标是否越界右侧标识坐标
            if (mDropColBorderMovedRightSpace < 0) {
                rightBorder = rightBorder + mDropColBorderMovedRightSpace - miniWidth;
            } else {
                rightBorder = rightBorder - miniWidth;
            }
            if (x > rightBorder) {
                x = rightBorder;
            }
        } else if (mDropColBorderAction == DropColumnBorderAction.Right) {
            //触发的是变化的列右边缘
            x = mDrawSelectorRectStartX + calcCellWidth(getGridYLabelWidth(mSelectedColPosition)) + mDropColBorderMovedRightSpace - distanceX;
            float leftBorder = mDrawSelectorRectStartX;
            //检测触发的当前坐标是否越界左侧标识坐标
            if (mDropColBorderMovedLeftSpace > 0) {
                leftBorder = leftBorder + mDropColBorderMovedLeftSpace + miniWidth;
            } else {
                leftBorder = leftBorder + miniWidth;
            }
            if (x < leftBorder) {
                x = leftBorder;
            }
        }
        return x;
    }

    private void setDrawingDropColumnX(float x) {
        if (mDropColBorderAction == DropColumnBorderAction.Left) {
            mDropColBorderMovedLeftSpace = x - mDrawSelectorRectStartX;
        } else if (mDropColBorderAction == DropColumnBorderAction.Right) {
            mDropColBorderMovedRightSpace = x - mDrawSelectorRectStartX - calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {
        this.mIsMoveStarted = false;
        //his.storedMotionEvent = e;
        //this.mHandler.postDelayed(this.mHighlightRow, 200L);
        tryCalcDropColumnBorderMoveAction(e);
    }

    private boolean mIsDropColBorderMoving = false;
    private DropColumnBorderAction mDropColBorderAction = DropColumnBorderAction.None;
    private float mDropColBorderMovedLeftSpace = 0;
    private float mDropColBorderMovedRightSpace = 0;

    private enum DropColumnBorderAction {
        Left, Right, None
    }

    private void tryCalcDropColumnBorderMoveAction(MotionEvent e) {
        if (mColLongPressAction == ColumnLongPressAction.Select) {
            mIsDropColBorderMoving = false;
            mDropColBorderAction = DropColumnBorderAction.None;
            float x = e.getX();
            float y = e.getY();
            float radios = mCellHeight / 2;
            if (y > mMoveContentRect.top && y < (mMoveContentRect.top + mCellHeight)) {
                final float dropLeftX = mDrawSelectorRectStartX + mDropColBorderMovedLeftSpace;
                float currColWidth = calcCellWidth(getGridYLabelWidth(mSelectedColPosition));
                final float dropRightX = mDrawSelectorRectStartX + currColWidth + mDropColBorderMovedRightSpace;
                if (x > (dropLeftX - radios) && x < (dropLeftX + radios)) {
                    mIsDropColBorderMoving = true;
                    mDropColBorderAction = DropColumnBorderAction.Left;
                } else if (x > (dropRightX - radios) && x < (dropRightX + radios)) {
                    mIsDropColBorderMoving = true;
                    mDropColBorderAction = DropColumnBorderAction.Right;
                }
            }
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mIsMoveStarted = false;
        if (mDataNotSet) {
            return true;
        }
        boolean ret = calcSelectPosition(e);
        if (ret) {
            clearLongSelectedState();
            invalidate();
            onSelectedChanged();
        }
        return false;
    }

    public void selectCell(int rowPosition, int colPosition) {
        int rowCount = getChartRowCount();
        int colCount = mOriginalData == null ? 0 : mOriginalData.getYValCount();
        if (rowPosition >= 0 && rowPosition < rowCount && colPosition >= 0 && colPosition < colCount) {
            mSelectedRowPosition = rowPosition;
            mSelectedColPosition = colPosition;
            clearLongSelectedState();
            invalidate();
            onSelectedChanged();
        }
    }

    public void selectRow(int rowPosition) {
        int rowCount = getChartRowCount();
        if (rowPosition >= 0 && rowPosition < rowCount) {
            mSelectedRowPosition = rowPosition;
            mSelectedColPosition = SELECT_ROW_COL_HEAD_FLAG;
            clearLongSelectedState();
            invalidate();
            onSelectedChanged();
        }
    }

    public void scrollToRow(int rowPosition) {
        scrollToPosition(rowPosition, 0);
    }

    public void scrollToPosition(int rowPosition, int colPosition) {
        int yLabelIndex = mDisplayYLabels.get(colPosition);
        xPos = 0.0f;
        if (yLabelIndex > 0) {
            for (int i = 0; i < yLabelIndex; i++) {
                xPos += mYLabelWidths.get(mDisplayYLabels.get(i));
            }
        }
        if (this.xPos >= 0.0F) {
            if (this.xPos <= this.xMax) {

            } else {
                this.xPos = this.xMax;
            }
        } else {
            this.xPos = 0.0F;
        }

        this.yPos = mCellHeight * rowPosition;
        if (this.yPos >= 0.0F) {
            if (this.yPos <= this.yMax) {

            } else {
                this.yPos = this.yMax;
            }
        } else {
            this.yPos = 0.0F;
        }
        invalidate();
    }

    public int getSelectedRowPosition() {
        return this.mSelectedRowPosition;
    }

    public GridEntry getGridEntryForYLabel(String yLabel, int rowIndex) {
        GridDataSet dataSet = mOriginalData.getDataSetByLabel(yLabel, false);
        int xLabelIndex = getXLabelIndex(rowIndex);
        return dataSet.getEntryForXIndex(xLabelIndex);
    }

    private Runnable mFlingRunnable = new Runnable() {
        public void run() {
            if (GridChart.this.mScroller.computeScrollOffset()) {
                xPos = GridChart.this.mScroller.getCurrX();
                yPos = GridChart.this.mScroller.getCurrY();
                invalidate();
                mHandler.post(GridChart.this.mFlingRunnable);
            }
        }
    };

    private int calcSelectedColPosition(float x) {
        float fixBorder = mChartRect.left + calcCellWidth(mXLabelMaxWidth) + mFixYWidth;
        float xTemp;
        float xSpace;
        int position = SELECT_NONE_FLAG;
        if (x < fixBorder) {
            xTemp = 0;
            xSpace = x - (mChartRect.left + calcCellWidth(mXLabelMaxWidth));
            for (int i = 0; i < mFixedYValues; i++) {
                xTemp += calcCellWidth(getGridYLabelWidth(i));
                if (xTemp >= xSpace) {
                    position = i;
                    break;
                }
            }
        } else {
            xTemp = 0;
            xSpace = x - mDrawContentMoveColStartX;
            for (int i = mDrawContentMoveColStartIndex; i <= mDrawContentMoveColStopIndex; i++) {
                xTemp += calcCellWidth(getGridYLabelWidth(i));
                if (xTemp >= xSpace) {
                    position = i;
                    break;
                }
            }
        }
        if (position < getDisplayYLabelCount()) {
            return position;
        }
        return SELECT_NONE_FLAG;
    }

    private int calcSelectedContentRowPosition(float y) {
        float ySpace = y - mDrawContentMoveRowStartY;
        int position = (int) (ySpace / this.mCellHeight);
        if ((position * mCellHeight - ySpace) > 0) {
            position++;
        }
        position = position + mDrawContentMoveRowStartIndex;
        if (position < getXLabelsSize()) {
            return position;
        }
        return SELECT_NONE_FLAG;
    }

    private boolean mIsMoveStarted = false;
//    private MotionEvent storedMotionEvent = null;
//    private final Runnable mHighlightRow = new Runnable() {
//        public void run() {
//            if (!mIsMoveStarted) {
//                MotionEvent localMotionEvent = GridChart.this.storedMotionEvent;
//                GridChart.this.storedMotionEvent = null;
//                boolean ret = calcSelectPosition(localMotionEvent);
//                if (ret) {
//                    invalidate();
//                }
//            }
//        }
//    };

    /**
     * 计算触摸位置
     *
     * @param e
     * @return
     */
    private boolean calcSelectPosition(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        final float chartBorderStartX = mChartRect.left;
        final float chartBorderEndX = mChartRect.right;
        final float chartBorderStartY = mChartRect.top;
        final float chartBorderEndY = mChartRect.bottom;
        final float rowBorderForHead = chartBorderStartY + mCellHeight;
        final float colBorderStartX = mChartRect.left + calcCellWidth(mXLabelMaxWidth);

        final float chartMoveBorderStartX = mMoveContentRect.left;
        final float chartMoveBorderEndY = mMoveContentRect.bottom;


        if (y > chartBorderStartY && y < rowBorderForHead) {

            if (x > colBorderStartX) {
                mSelectedRowPosition = SELECT_ROW_COL_HEAD_FLAG;
                mSelectedColPosition = calcSelectedColPosition(x);
            } else {
                mSelectedRowPosition = SELECT_ROW_COL_HEAD_FLAG;
                mSelectedColPosition = SELECT_ROW_COL_HEAD_FLAG;
                return false;
            }
        } else if (y > rowBorderForHead && y < chartMoveBorderEndY) {
            if (x > chartBorderStartX && x < colBorderStartX) {
                mSelectedColPosition = SELECT_ROW_COL_HEAD_FLAG;
                mSelectedRowPosition = calcSelectedContentRowPosition(y);
            } else if (x > colBorderStartX && x < chartMoveBorderStartX) {
                mSelectedColPosition = calcSelectedColPosition(x);
                mSelectedRowPosition = calcSelectedContentRowPosition(y);
            } else if (x > chartMoveBorderStartX && x < chartBorderEndX) {
                mSelectedColPosition = calcSelectedColPosition(x);
                mSelectedRowPosition = calcSelectedContentRowPosition(y);
            } else {
                mSelectedRowPosition = SELECT_NONE_FLAG;
                mSelectedColPosition = SELECT_NONE_FLAG;
                return false;
            }
        } else if (y > chartMoveBorderEndY && y < chartBorderEndY) {
            mSelectedRowPosition = SELECT_SUM_FLAG;
            mSelectedColPosition = calcSelectedColPosition(x);
        } else {
            mSelectedRowPosition = SELECT_NONE_FLAG;
            mSelectedColPosition = SELECT_NONE_FLAG;
            return false;
        }
        return true;
    }

    private void calcSelectorRectBorder() {
        mDrawSelectorRectStartX = calcSelectorRectStartX(mSelectedColPosition);
        mDrawSelectorRectStartY = calcSelectorRectStartY(mSelectedRowPosition);
    }

    private float calcSelectorRectStartY(int selectRowPosition) {
        float top;
        if (selectRowPosition == SELECT_ROW_COL_HEAD_FLAG) {
            top = mChartRect.top;
        } else if (selectRowPosition == SELECT_SUM_FLAG) {
            top = mChartRect.bottom - mCellHeight;
        } else {
            top = mDrawContentMoveRowStartY;
            for (int i = mDrawContentMoveRowStartIndex; i < mSelectedRowPosition; i++) {
                top += mCellHeight;
            }
        }
        return top;
    }

    private float calcSelectorRectStartX(int selectColPosition) {
        if (selectColPosition == SELECT_ROW_COL_HEAD_FLAG || selectColPosition == SELECT_NONE_FLAG || selectColPosition == SELECT_SUM_FLAG) {
            return mChartRect.left;
        } else {
            if (selectColPosition >= 0 && selectColPosition < mFixedYValues) {
                float x = mChartRect.left + calcCellWidth(mXLabelMaxWidth);
                for (int i = 0; i < selectColPosition; i++) {
                    x += calcCellWidth(getGridYLabelWidth(i));
                }
                return x;
            } else if (selectColPosition >= mFixedYValues) {
                if (selectColPosition == mDrawContentMoveColStartIndex) {
                    return mDrawContentMoveColStartX;
                } else if (selectColPosition > mDrawContentMoveColStartIndex) {
                    float x = mDrawContentMoveColStartX;
                    for (int i = mDrawContentMoveColStartIndex; i < selectColPosition; i++) {
                        x += calcCellWidth(getGridYLabelWidth(i));
                    }
                    return x;
                } else {
                    float x = mDrawSelectorRectStartX;
                    for (int i = mDrawContentMoveColStartIndex - 1; i >= selectColPosition; i--) {
                        x -= calcCellWidth(getGridYLabelWidth(i));
                    }
                    return x;
                }
            } else {
                return mChartRect.left;
            }
        }
    }


    private OnLongClickListener onLongClickListener = null;

    public void setOnLongClickListener(
            OnLongClickListener paramOnLongClickListener) {
        this.onLongClickListener = paramOnLongClickListener;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable paramParcelable) {
        Bundle localBundle = (Bundle) paramParcelable;
        super.onRestoreInstanceState(localBundle.getParcelable("super_state"));
        this.xPos = localBundle.getFloat("x_pos");
        this.yPos = localBundle.getFloat("y_pos");
        this.mSelectedRowPosition = localBundle.getInt("selected_row");
        this.mSelectedColPosition = localBundle.getInt("selected_column");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable localParcelable = super.onSaveInstanceState();
        Bundle localBundle = new Bundle();
        localBundle.putFloat("x_pos", this.xPos);
        localBundle.putFloat("y_pos", this.yPos);
        localBundle.putInt("selected_row", this.mSelectedRowPosition);
        localBundle.putInt("selected_column", this.mSelectedColPosition);
        localBundle.putParcelable("super_state", localParcelable);
        return localBundle;
    }

    public interface OnGridChartRowSelectListener {
        public void onSelected(int rowIndex);
    }

    public interface OnGridChartColumnSelectListener {
        public void onSelected(int colIndex);
    }

    public interface OnGridChartCellSelectListener {
        public void onSelected(int rowIndex, int colIndex);
    }

    private OnGridChartRowSelectListener mOnGridChartRowSelectListener;
    private OnGridChartColumnSelectListener mOnGridChartColumnSelectListener;
    private OnGridChartCellSelectListener mOnGridChartCellSelectListener;

    public void setOnGridChartRowSelectListener(OnGridChartRowSelectListener listener) {
        mOnGridChartRowSelectListener = listener;
    }

    public void setOnGridChartColumnSelectListener(OnGridChartColumnSelectListener listener) {
        mOnGridChartColumnSelectListener = listener;
    }

    public void setOnGridChartCellSelectListener(OnGridChartCellSelectListener listener) {
        mOnGridChartCellSelectListener = listener;
    }

    private void onSelectedChanged() {
        if (mSelectedRowPosition != SELECT_NONE_FLAG && mSelectedColPosition != SELECT_NONE_FLAG) {
            if (mSelectedRowPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedColPosition != SELECT_ROW_COL_HEAD_FLAG) {
                if (mOnGridChartColumnSelectListener != null) {
                    mOnGridChartColumnSelectListener.onSelected(mSelectedColPosition);
                }
            } else if (mSelectedColPosition == SELECT_ROW_COL_HEAD_FLAG && mSelectedRowPosition != SELECT_ROW_COL_HEAD_FLAG) {
                if (mOnGridChartRowSelectListener != null) {
                    mOnGridChartRowSelectListener.onSelected(mSelectedRowPosition);
                }
            } else if (mSelectedRowPosition != SELECT_SUM_FLAG) {
                if (mOnGridChartCellSelectListener != null) {
                    mOnGridChartCellSelectListener.onSelected(mSelectedRowPosition, mSelectedColPosition);
                }
            }
        }
    }

    public boolean isEmpty() {

        if (mOriginalData == null)
            return true;
        else {

            if (mOriginalData.getYValCount() <= 0)
                return true;
            else
                return false;
        }
    }

    public String setupTemplate(GridChartTemplate template) {
        if ((!isEmpty())) {
            if (template == null) {
                prepare();
                isSetupTemplate = false;
                isTemplateChanged = false;
            } else {
                template.sort();
                //format data
                ArrayList<GridChartTemplate.Child> childes = template.getChildes();
                ArrayList<GridDataSet> dataSets = mOriginalData.getDataSets();
                //check template safe
                int contentSize = dataSets.size();
                if (childes.size() != contentSize) {
                    return "报表数据结构发生变化,原有的个性化样式模板失效.";
                }
                mDisplayYLabels.clear();
                int newFixValues = 0;
                for (GridChartTemplate.Child child : childes) {
                    if (child.enableDisplay) {
                        if (child.isFix) {
                            newFixValues++;
                        }
                        mYLabelWidths.set(child.dataIndex, child.width);
                        mDisplayYLabels.add(child.dataIndex);
                    }
                }
                mFixedYValues = newFixValues;
                //setup
                isSetupTemplate = true;
                isTemplateChanged = false;
            }
            initCharState();
            prepareForTemplate();
            invalidate();
            return null;
        }
        return "报表数据为空,个性化样式模板不能加载";
    }

    public GridChartTemplate makeGridChartTemplate() {
        GridChartTemplate template = new GridChartTemplate();
        int size = mDisplayYLabels.size();
        for (int i = 0; i < size; i++) {
            int dataIndex = mDisplayYLabels.get(i);
            template.addChild(new GridChartTemplate.Child(i, dataIndex, getGridDataSetForDataIndex(dataIndex).getLabel(), true, i < mFixedYValues, getGridYLabelWidth(i)));
        }
        int position = size;
        size = mYLabelWidths.size();
        for (int i = 0; i < size; i++) {
            if (mDisplayYLabels.indexOf(i) < 0) {
                template.addChild(new GridChartTemplate.Child(position, i, getGridDataSetForDataIndex(i).getLabel(), false, false, mYLabelWidths.get(i)));
                position++;
            }
        }
        template.sort();
        return template;
    }

    protected GridDataSet getGridDataSetForColIndex(int index) {
        int dataIndex = mDisplayYLabels.get(index);
        return mOriginalData.getDataSetByIndex(dataIndex);
    }

    protected GridDataSet getGridDataSetForDataIndex(int index) {
        return mOriginalData.getDataSetByIndex(index);
    }

    public int getDataRowCount() {
        return mOriginalData == null ? 0 : mOriginalData.getXValCount();
    }

    protected int getDataColCount() {
        return mOriginalData == null ? 0 : mOriginalData.getYValCount();
    }

    public ArrayList<Integer> getDisplayYLabels() {
        return mDisplayYLabels;
    }

    public List<String> getDisplayYLabelNames() {
        List<String> labelNames = new ArrayList<>();
        final int labelSize = mDisplayYLabels.size();
        for (int i = 0; i < labelSize; i++) {
            labelNames.add(getGridDataSetForColIndex(i).getLabel());
        }
        return labelNames;
    }

    public void searchDelayed(final List<String> yLabels, final String query) {
        if (query == null || query.length() == 0) {
            filterXLabels.clear();
            notifyDataSetChanged();
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            } catch (Exception e) {

            }
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        searchTimer.cancel();
                        searchTimer = null;
                    } catch (Exception e) {

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            searchWithQuery(yLabels, query);
                        }
                    });
                }
            }, 200, 500);
        }
    }


    private void searchWithQuery(List<String> yLabels, String query) {
        if (searching) {
            searching = false;
        }
        try {
            searching = true;
            filterXLabels.clear();
            if (yLabels != null && yLabels.size() > 0) {
                for (String yLabel : yLabels) {
                    List<Integer> result = mOriginalData.filterByYLabels(yLabel, query);
                    formatFilterXLabelResult(result);
                }
            } else {
                int yLabelSize = mOriginalData.getDataSetCount();
                for (int i = 0; i < yLabelSize; i++) {
                    List<Integer> result = mOriginalData.filterByYLabels(i, query);
                    formatFilterXLabelResult(result);
                }
            }
            Collections.sort(filterXLabels, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    return lhs.compareTo(rhs);
                }
            });
        } catch (Exception e) {
            searching = false;
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mSelectedRowPosition = SELECT_ROW_COL_HEAD_FLAG;
        mSelectedColPosition = SELECT_ROW_COL_HEAD_FLAG;
        xPos = 0.0f;
        yPos = 0.0f;
        clearLongSelectedState();
        invalidate();
    }

    private void formatFilterXLabelResult(List<Integer> xLabels) {
        if (xLabels != null && xLabels.size() > 0) {
            for (Integer label : xLabels) {
                if (!filterXLabels.contains(label)) {
                    filterXLabels.add(label);
                }
            }
        }
    }

    private int getXLabelsSize() {
        if (filterXLabels.isEmpty()) {
            return mOriginalData.getXValCount();
        } else {
            return filterXLabels.size();
        }
    }

    private String getXLabel(int position) {
        int xIndex = position;
        if (!filterXLabels.isEmpty()) {
            xIndex = filterXLabels.get(position);
        }
        return mOriginalData.getXVal(xIndex);
    }

    public int getXLabelIndex(int position) {
        if (!filterXLabels.isEmpty()) {
            return filterXLabels.get(position);
        }
        return position;
    }

    private int getChartRowCount() {
        int rowCount;
        if (filterXLabels.isEmpty()) {
            rowCount = mOriginalData == null ? 0 : mOriginalData.getXValCount();
        } else {
            rowCount = filterXLabels.size();
        }
        return rowCount;
    }

    public void clearFilter() {
        filterXLabels.clear();
        notifyDataSetChanged();
    }
}
