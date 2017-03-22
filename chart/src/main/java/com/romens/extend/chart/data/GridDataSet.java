package com.romens.extend.chart.data;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.romens.extend.chart.format.DataFormat;
import com.romens.extend.chart.format.DecimalDataFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoulisi on 14/12/19.
 */
public class GridDataSet<T extends GridEntry> {

    /**
     * default highlight color
     */
    protected int mHighLightColor = Color.rgb(255, 187, 115);

    /**
     * Sets the color that is used for drawing the highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     *
     * @param color
     */
    public void setHighLightColor(int color) {
        mHighLightColor = color;
    }

    /**
     * Returns the color that is used for drawing the highlight indicators.
     *
     * @return
     */
    public int getHighLightColor() {
        return mHighLightColor;
    }

    /**
     * arraylist representing all colors that are used for this DataSet
     */
    protected List<Integer> mColors = null;

    /**
     * the entries that this dataset represents / holds together
     */
    protected ArrayList<T> mYVals = null;

    /**
     * maximum y-value in the y-value array
     */
    protected BigDecimal mYMax = BigDecimal.ZERO;

    /**
     * the minimum y-value in the y-value array
     */
    protected BigDecimal mYMin = BigDecimal.ZERO;

    /**
     * the total sum of all y-values
     */
    private BigDecimal yValueSum = BigDecimal.ZERO;

    private String mMaxLengthVal;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    private String mLabel = "DataSet";


    private final AmountType mAmountType;
    private final String mAmountExpress;
    private BigDecimal amountValue = BigDecimal.ZERO;
    private String amountValueText;

    private DataFormat mDataFormat;

    /**
     * Creates a new DataSet object with the given values it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     *
     * @param yVals
     * @param label
     */
    public GridDataSet(ArrayList<T> yVals, String label) {
        this(yVals, label, AmountType.NONE, null, null);
    }

    public GridDataSet(ArrayList<T> yVals, String label, AmountType amountType, String amountExpress) {
        this(yVals, label, amountType, amountExpress, null);
    }

    public GridDataSet(ArrayList<T> yVals, String label, AmountType amountType, String amountExpress, DataFormat format) {
        if (TextUtils.isEmpty(amountExpress)) {
            this.mAmountType = amountType;
            this.mAmountExpress = null;
        } else {
            this.mAmountType = AmountType.NONE;
            this.mAmountExpress = String.format("%s:=%s", label, amountExpress);
        }
        this.mDataFormat = format;
        this.mLabel = label;
        this.mYVals = yVals;

        if (mYVals == null)
            mYVals = new ArrayList<T>();

        // if (yVals.size() <= 0) {
        // return;
        // }

        mColors = new ArrayList<Integer>();

        // default colors
        // mColors.add(Color.rgb(192, 255, 140));
        // mColors.add(Color.rgb(255, 247, 140));
        mColors.add(Color.rgb(140, 234, 255));

        calcMinMax();
        calcYValueSumValue();
    }

    /**
     * Use this method to tell the data set that the underlying data has changed
     */
    public void notifyDataSetChanged() {
        calcMinMax();
        calcYValueSumValue();
    }

    /**
     * calc minimum and maximum y value
     */
    protected void calcMinMax() {
        if (mYVals.size() == 0) {
            return;
        }

        mYMin = mYVals.get(0).getFloatVal();
        mYMax = mYVals.get(0).getFloatVal();

        for (int i = 0; i < mYVals.size(); i++) {

            GridEntry e = mYVals.get(i);

            if (mYMin.compareTo(e.getFloatVal()) > 0) {
                mYMin = e.getFloatVal();
            }
            if (mYMax.compareTo(e.getFloatVal()) < 0) {
                mYMax = e.getFloatVal();
            }

//            if (e.getFloatVal() < mYMin)
//                mYMin = e.getFloatVal();
//
//            if (e.getFloatVal() > mYMax)
//                mYMax = e.getFloatVal();
        }
    }

    /**
     * calculates the sum of all y-values
     */
    private void calcYValueSumValue() {

        BigDecimal sum = BigDecimal.ZERO;
        final int size = mYVals.size();
        for (int i = 0; i < size; i++) {
            //mYValueSum += Math.abs(mYVals.get(i).getFloatVal());
            sum = sum.add(mYVals.get(i).getFloatVal());
            calcMaxLengthValue(mYVals.get(i).getVal());
        }
        changeValueSum(sum);
    }

    /**
     * Y 轴数据合计
     *
     * @param sum
     */
    private void changeValueSum(BigDecimal sum) {
        yValueSum = sum;
        calcMaxLengthValue(yValueSum.toString());
        //2015-03-23 Amount ERP合计方式
        BigDecimal amount;
        if (mAmountType == AmountType.COUNT) {
            amount = BigDecimal.valueOf(getEntryCount());
        } else if (mAmountType == AmountType.MAX) {
            amount = getYMax();
        } else if (mAmountType == AmountType.MIN) {
            amount = getYMin();
        } else if (mAmountType == AmountType.AVG) {
            int count = getEntryCount();
            amount = yValueSum;
            /**
             * 通过BigDecimal的divide方法进行除法时当不整除，出现无限循环小数时
             * 就会抛异常：java.lang.ArithmeticException: Non-terminating decimal expansion;
             * no exact representable decimal result.
             * 解决的办法就是给divide方法设置精确的小数点，如：divide(xxxxx,2)。
             */
            amount = count == 0 ? BigDecimal.ZERO : amount.divide(BigDecimal.valueOf(count), 4);
        } else if (mAmountType == AmountType.SUN) {
            amount = yValueSum;
        } else {
            amount = yValueSum;
        }
        onAmountValueChanged(amount);
    }

    /**
     * returns the number of y-values this DataSet represents
     *
     * @return
     */
    public int getEntryCount() {
        return mYVals.size();
    }

    /**
     * Returns the value of the Entry object at the given xIndex. Returns
     * Float.NaN if no value is at the given x-index. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     *
     * @param xIndex
     * @return
     */
    public String getYValForXIndex(int xIndex) {

        GridEntry e = getEntryForXIndex(xIndex);

        if (e != null)
            return e.getVal();
        else
            return "";
    }

    public String getMaxLengthVal() {
        return mMaxLengthVal;
    }

    /**
     * Returns the first Entry object found at the given xIndex with binary
     * search. Returns null if no Entry object at that index. INFORMATION: This
     * method does calculations at runtime. Do not over-use in performance
     * critical situations.
     *
     * @param x
     * @return
     */
    public T getEntryForXIndex(int x) {

        int low = 0;
        int high = mYVals.size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (x == mYVals.get(m).getXIndex()) {
                return mYVals.get(m);
            }

            if (x > mYVals.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;
        }

        return null;
    }

    /**
     * Returns all Entry objects at the given xIndex. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     *
     * @param x
     * @return
     */
    public ArrayList<T> getEntriesForXIndex(int x) {

        ArrayList<T> entries = new ArrayList<T>();

        int low = 0;
        int high = mYVals.size();

        while (low <= high) {
            int m = (high + low) / 2;

            if (x == mYVals.get(m).getXIndex()) {
                entries.add(mYVals.get(m));
            }

            if (x > mYVals.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;
        }

        return entries;
    }

    /**
     * returns the DataSets Entry array
     *
     * @return
     */
    public ArrayList<T> getYVals() {
        return mYVals;
    }

    /**
     * gets the sum of all y-values
     *
     * @return
     */
    public BigDecimal getYValueSum() {
        return yValueSum == null ? BigDecimal.ZERO : yValueSum;
    }

    /**
     * returns the minimum y-value this DataSet holds
     *
     * @return
     */
    public BigDecimal getYMin() {
        return mYMin;
    }

    /**
     * returns the maximum y-value this DataSet holds
     *
     * @return
     */
    public BigDecimal getYMax() {
        return mYMax;
    }

    /**
     * returns the type of the DataSet, specified via constructor
     *
     * @return
     */
    // public int getType() {
    // return mType;
    // }

    /**
     * The xIndex of an Entry object is provided. This method returns the actual
     * index in the Entry array of the DataSet. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     *
     * @param xIndex
     * @return
     */
    public int getIndexInEntries(int xIndex) {

        for (int i = 0; i < mYVals.size(); i++) {
            if (xIndex == mYVals.get(i).getXIndex())
                return i;
        }

        return -1;
    }

    /**
     * Provides an exact copy of the DataSet this method is used on.
     *
     * @return
     */
    public GridDataSet<T> copy() {
        GridDataSet dataSet = new GridDataSet(this.mYVals, this.mLabel, mAmountType, mAmountExpress, mDataFormat);
        dataSet.mMaxLengthVal = mMaxLengthVal;
        dataSet.yValueSum = yValueSum;
        dataSet.mYMax = mYMax;
        dataSet.mYMin = mYMin;
        return dataSet;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(toSimpleString());
        for (int i = 0; i < mYVals.size(); i++) {
            buffer.append(mYVals.get(i).toString() + " ");
        }
        return buffer.toString();
    }

    /**
     * Returns a simple string representation of the DataSet with the type and
     * the number of Entries.
     *
     * @return
     */
    public String toSimpleString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataSet, label: " + mLabel + ", entries: " + mYVals.size() + "\n");
        return buffer.toString();
    }

    /**
     * Returns the label string that describes the DataSet.
     *
     * @return
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Adds an Entry to the DataSet dynamically. This will also recalculate the
     * current minimum and maximum values of the DataSet and the value-sum.
     *
     * @param e
     */
    public void addEntry(GridEntry e) {

        if (e == null)
            return;

        BigDecimal val = e.getFloatVal();
        if (mYVals == null || mYVals.size() <= 0) {

            mYVals = new ArrayList<T>();
            mYMax = val;
            mYMin = val;
        } else {

            if (mYMax.compareTo(val) < 0) {
                mYMax = val;
            }
            if (mYMin.compareTo(val) > 0) {
                mYMin = val;
            }
//            if (mYMax < val)
//                mYMax = val;
//            if (mYMin > val)
//                mYMin = val;
        }
        // add the entry
        mYVals.add((T) e);

        BigDecimal newSum = yValueSum.add(val);
        changeValueSum(newSum);
    }

//    public void addEntries(ArrayList<GridEntry> entries) {
//        if (entries == null) {
//            return;
//        }
//        for (GridEntry entry : entries) {
//            addEntry(entry);
//        }
//    }

    /**
     * Removes an Entry from the DataSets entries array. This will also
     * recalculate the current minimum and maximum values of the DataSet and the
     * value-sum. Returns true if an Entry was removed, false if no Entry could
     * be removed.
     *
     * @param e
     */
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        // remove the entry
        boolean removed = mYVals.remove(e);

        if (removed) {
            BigDecimal val = e.getFloatVal();
            BigDecimal newSum = yValueSum.subtract(val);
            changeValueSum(newSum);
            calcMinMax();
        }

        return removed;
    }

    /**
     * Removes the Entry object that has the given xIndex from the DataSet.
     * Returns true if an Entry was removed, false if no Entry could be removed.
     *
     * @param xIndex
     */
    public boolean removeEntry(int xIndex) {

        T e = getEntryForXIndex(xIndex);
        return removeEntry(e);
    }

    /** BELOW THIS COLOR HANDLING */

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setColors(ArrayList<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     *
     * @param colors
     */
    public void setColors(int[] colors, Context c) {

        ArrayList<Integer> clrs = new ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mColors = clrs;
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     *
     * @param color
     */
    public void addColor(int color) {
        if (mColors == null)
            mColors = new ArrayList<Integer>();
        mColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     *
     * @param color
     */
    public void setColor(int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * returns all the colors that are set for this DataSet
     *
     * @return
     */
    public List<Integer> getColors() {
        return mColors;
    }

    /**
     * Returns the color at the given index of the DataSet's color array.
     * Performs a IndexOutOfBounds check by modulus.
     *
     * @param index
     * @return
     */
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    /**
     * Returns the first color (index 0) of the colors-array this DataSet
     * contains.
     *
     * @return
     */
    public int getColor() {
        return mColors.get(0);
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        mColors = new ArrayList<Integer>();
    }

    /**
     * Returns the position of the provided entry in the DataSets Entry array.
     * Returns -1 if doesnt exist.
     *
     * @param e
     * @return
     */
    public int getEntryPosition(GridEntry e) {

        for (int i = 0; i < mYVals.size(); i++) {
            if (e.equalTo(mYVals.get(i)))
                return i;
        }

        return -1;
    }

    // /**
    // * Convenience method to create multiple DataSets of different types with
    // * various double value arrays. Each double array represents the data of
    // one
    // * DataSet with a type created by this method, starting at 0 (and
    // * incremented).
    // *
    // * @param yValues
    // * @return
    // */
    // public static ArrayList<DataSet> makeDataSets(ArrayList<Double[]>
    // yValues) {
    //
    // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    //
    // for (int i = 0; i < yValues.size(); i++) {
    //
    // Double[] curValues = yValues.get(i);
    //
    // ArrayList<Entry> entries = new ArrayList<Entry>();
    //
    // for (int j = 0; j < curValues.length; j++) {
    // entries.add(new Entry(curValues[j].floatValue(), j));
    // }
    //
    // dataSets.add(new DataSet(entries, "DS " + i));
    // }
    //
    // return dataSets;
    // }


    protected void calcMaxLengthValue(String val) {
        if (TextUtils.isEmpty(val)) {
            return;
        }
        if (TextUtils.isEmpty(mMaxLengthVal)) {
            mMaxLengthVal = val;
            return;
        }
        int maxLength = calcTextLength(mMaxLengthVal);
        int length = calcTextLength(val);
        if (length > maxLength) {
            mMaxLengthVal = val;
        }
    }


    /**
     * 计算字符串长度
     *
     * @param text
     * @return
     */
    public static final int calcTextLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            int ascii = Character.codePointAt(text, i);
            if (ascii >= 0 && ascii <= 255) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    public AmountType getAmountType() {
        return mAmountType;
    }

    public String getAmountExpress() {
        return mAmountExpress;
    }

    public boolean hasFormula() {
        return !TextUtils.isEmpty(mAmountExpress);
    }

    public boolean enableAmount() {
        if (hasFormula()) {
            return true;
        }
        return mAmountType != AmountType.NONE;
    }

    public BigDecimal getAmountVal() {
        return amountValue;
    }

    public String getAmountValueText() {
        return amountValueText == null ? "" : amountValueText;
    }

//    public String getFormatAmountVal() {
//        if (mAmountType == AmountType.SUN ||
//                mAmountType == AmountType.AVG ||
//                mAmountType == AmountType.MAX ||
//                mAmountType == AmountType.MIN) {
//            return formatValue(amountValue);
//        } else if (mAmountType == AmountType.COUNT) {
//            return String.valueOf(amountValue.intValue());
//        }
//        return amountValue.toString();
//    }

    public String formatValue(BigDecimal val) {
        if (mDataFormat != null) {
            if (mDataFormat instanceof DecimalDataFormat) {
                return ((DecimalDataFormat) mDataFormat).format(val);
            }
            return mDataFormat.format(val.toString());
        }
        return val.toString();
    }

    public boolean changeAmountVal(BigDecimal amount) {
        final boolean isChange = (amountValue == null || amountValue.compareTo(amount) != 0);
        if (isChange) {
            onAmountValueChanged(amount);
        }
        return isChange;
    }

    private void onAmountValueChanged(BigDecimal amount) {
        amountValue = amount;
        if (mAmountType == AmountType.SUN ||
                mAmountType == AmountType.AVG ||
                mAmountType == AmountType.MAX ||
                mAmountType == AmountType.MIN) {
            amountValueText = formatValue(amountValue);
        } else if (mAmountType == AmountType.COUNT) {
            amountValueText = String.valueOf(amountValue.intValue());
        } else {
            amountValueText = amountValue.toString();
        }
        calcMaxLengthValue(amountValueText);
    }


    public enum AmountType {
        NONE, COUNT, AVG, MAX, MIN, SUN
    }

    /**
     * 筛选
     *
     * @param text 过滤字符串
     * @return xLabel 索引集合
     */
    public List<Integer> filter(String text) {
        List<Integer> positions = new ArrayList<>();
        final int size = mYVals.size();
        for (int i = 0; i < size; i++) {
            if (mYVals.get(i).getData().indexOf(text) >= 0) {
                positions.add(i);
            }
        }
        return positions;
    }
}
