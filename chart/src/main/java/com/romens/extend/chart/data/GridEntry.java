package com.romens.extend.chart.data;

import android.text.TextUtils;

import com.romens.extend.chart.format.DataFormat;
import com.romens.extend.chart.format.DateDataFormat;
import com.romens.extend.chart.format.DecimalDataFormat;
import com.romens.extend.chart.format.DefaultFormat;
import com.romens.extend.chart.format.NumberFormat;
import com.romens.extend.chart.utils.FormatUtils;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Created by zhoulisi on 14/12/22.
 */
public class GridEntry {
    /**
     * the actual value
     */
    private String mValue;
    /**
     * the index on the x-axis
     */
    private int mXIndex = 0;

    /**
     * optional spot for additional data this Entry represents
     */
    private String mData = null;

    private boolean mIsParseError = false;
    private DataType mDataType = DataType.Text;
    private DataFormat mDataFormat;


    public enum DataType {
        Text, Number, Date
    }


    public GridEntry(String data, int xIndex, DataType dataType, DataFormat dataFormat) {
        this.mDataFormat = dataFormat;
        if (dataType == DataType.Number) {
            this.mData = TextUtils.isEmpty(data) ? "0" : data;
        } else {
            this.mData = TextUtils.isEmpty(data) ? "" : data;
        }
        this.mIsParseError = false;
        this.mXIndex = xIndex;
        this.mDataType = dataType;
        formatData(mData);
    }

    protected void formatData(String data) {
        if (mDataFormat != null) {
            try {
                this.mValue = mDataFormat.format(data);
            } catch (Exception e) {
                this.mIsParseError = true;
                this.mValue = data;
            }
        } else {
            this.mValue = data;
        }
    }

    GridEntry(String data, int xIndex, String value) {
        this.mData = data;
        this.mXIndex = xIndex;
        this.mValue = value;
    }

    public DataType getDataType() {
        return this.mDataType;
    }

    public boolean isParseError() {
        return this.mIsParseError;
    }

    /**
     * returns the x-index the value of this object is mapped to
     *
     * @return
     */
    public int getXIndex() {
        return mXIndex;
    }

    /**
     * sets the x-index for the entry
     *
     * @param x
     */
    public void setXIndex(int x) {
        this.mXIndex = x;
    }

    /**
     * Returns the total value the entry represents.
     *
     * @return
     */
    public String getVal() {
        return mValue;
    }

    public BigDecimal getFloatVal() {
        if (mDataType == DataType.Number) {
            return new BigDecimal(mData);
        }
        return BigDecimal.ZERO;
    }

    public boolean enableSum() {
        return (mDataType == DataType.Number);
    }

    /**
     * Sets the value for the entry.
     *
     * @param val
     */
    public void setVal(String val) {
        this.mValue = val;
    }

    /**
     * Returns the data, additional information that this Entry represents, or
     * null, if no data has been specified.
     *
     * @return
     */
    public String getData() {
        return mData;
    }

    /**
     * Sets additional data this Entry should represents.
     *
     * @param data
     */
    public void setData(String data) {
        this.mData = data;
        formatData(data);
    }

    // /**
    // * If this Enry represents mulitple values (e.g. Stacked BarChart), it
    // will
    // * return the sum of them, otherwise just the one value it represents.
    // *
    // * @return
    // */
    // public float getSum() {
    // if (mVals == null)
    // return mVal;
    // else {
    //
    // float sum = 0f;
    //
    // for (int i = 0; i < mVals.length; i++)
    // sum += mVals[i];
    //
    // return sum;
    // }
    // }

    /**
     * returns an exact copy of the entry
     *
     * @return
     */
    public GridEntry copy() {
        GridEntry e = new GridEntry(mData, mXIndex, mValue);
        e.mDataType = mDataType;
        e.mDataFormat = mDataFormat;
        e.mIsParseError = mIsParseError;
        return e;
    }

    /**
     * Compares value, xIndex and data of the entries. Returns true if entries
     * are equal, false if not.
     *
     * @param e
     * @return
     */
    public boolean equalTo(GridEntry e) {

        if (e == null)
            return false;

        if (!TextUtils.equals(e.mData, this.mData))
            return false;
        if (e.mXIndex != this.mXIndex)
            return false;

        if (getFloatVal().compareTo(e.getFloatVal()) != 0) {
            return false;
        }
//        if (Math.abs(e.getFloatVal() - getFloatVal()) > 0.00001f)
//            return false;
        return true;
    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    public String toString() {
        return "Entry, xIndex: " + mXIndex + " val (sum): " + getVal();
    }

}
