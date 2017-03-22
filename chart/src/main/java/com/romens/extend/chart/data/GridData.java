package com.romens.extend.chart.data;

import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.components.LimitLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoulisi on 14/12/19.
 */
public class GridData<T extends GridDataSet<? extends GridEntry>> {

    /**
     * array of limit-lines that are set for this data object
     */
    private ArrayList<LimitLine> mLimitLines;

    /**
     * Adds a new LimitLine to the data.
     *
     * @param limitLine
     */
    public void addLimitLine(LimitLine limitLine) {
        if (mLimitLines == null)
            mLimitLines = new ArrayList<LimitLine>();
        mLimitLines.add(limitLine);
        updateMinMax();
    }

    /**
     * Adds a new array of LimitLines.
     *
     * @param lines
     */
    public void addLimitLines(ArrayList<LimitLine> lines) {
        mLimitLines = lines;
        updateMinMax();
    }

    /**
     * Resets the limit lines array to null. Causes no more limit lines to be
     * set for this data object.
     */
    public void resetLimitLines() {
        mLimitLines = null;
        calcMinMax(mDataSets);
    }

    /**
     * Returns the LimitLine array of this data object.
     *
     * @return
     */
    public ArrayList<LimitLine> getLimitLines() {
        return mLimitLines;
    }

    /**
     * Returns the LimitLine from the limitlines array at the specified index.
     *
     * @param index
     * @return
     */
    public LimitLine getLimitLine(int index) {
        if (mLimitLines == null || mLimitLines.size() <= index)
            return null;
        else
            return mLimitLines.get(index);
    }

    /**
     * Updates the min and max y-value according to the set limits.
     */
    private void updateMinMax() {

        if (mLimitLines == null)
            return;

        BigDecimal limit;
        for (int i = 0; i < mLimitLines.size(); i++) {

            LimitLine l = mLimitLines.get(i);
            limit = new BigDecimal(Float.toString(l.getLimit()));
            if (mYMax.compareTo(limit) < 0) {
                mYMax = limit;
            }

            if (mYMin.compareTo(limit) > 0) {
                mYMin = limit;
            }
//            if (l.getLimit() > mYMax)
//                mYMax = l.getLimit();
//
//            if (l.getLimit() < mYMin)
//                mYMin = l.getLimit();
        }
    }

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
    private BigDecimal mYValueSum = BigDecimal.ZERO;

    /**
     * total number of y-values across all DataSet objects
     */
    private int mYValCount = 0;

    /**
     * contains the average length (in characters) an entry in the x-vals array
     * has
     */
    private BigDecimal mXValAverageLength = BigDecimal.ZERO;

    /**
     * holds all x-values the chart represents
     */
    protected ArrayList<String> mXVals;

    /**
     * array that holds all DataSets the ChartData object represents
     */
    protected ArrayList<T> mDataSets;

    /**
     * Constructor for only x-values. This constructor can be used for setting
     * up an empty chart without data.
     *
     * @param xVals
     */
    public GridData(ArrayList<String> xVals) {
        this.mXVals = xVals;

        init();
    }

    /**
     * Constructor for only x-values. This constructor can be used for setting
     * up an empty chart without data.
     *
     * @param xVals
     */
    public GridData(String[] xVals) {
        this.mXVals = arrayToArrayList(xVals);

        init();
    }

    /**
     * constructor for chart data
     *
     * @param xVals The values describing the x-axis. Must be at least as long
     *              as the highest xIndex in the Entry objects across all
     *              DataSets.
     * @param sets  the dataset array
     */
    public GridData(ArrayList<String> xVals, ArrayList<T> sets) {
        this.mXVals = xVals;
        this.mDataSets = sets;

        init();
    }

    /**
     * constructor that takes string array instead of arraylist string
     *
     * @param xVals The values describing the x-axis. Must be at least as long
     *              as the highest xIndex in the Entry objects across all
     *              DataSets.
     * @param sets  the dataset array
     */
    public GridData(String[] xVals, ArrayList<T> sets) {
        this.mXVals = arrayToArrayList(xVals);
        this.mDataSets = sets;

        init();
    }

    /**
     * Turns an array of strings into an arraylist of strings.
     *
     * @param array
     * @return
     */
    private ArrayList<String> arrayToArrayList(String[] array) {

        ArrayList<String> arraylist = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            arraylist.add(array[i]);
        }

        return arraylist;
    }

    /**
     * performs all kinds of initialization calculations, such as min-max and
     * value count and sum
     */
    private void init() {

        isLegal(mDataSets);

        calcMinMax(mDataSets);
        calcYValueSum(mDataSets);
        calcYValueCount(mDataSets);

        //calcXValAverageLength();
    }

    /**
     * calculates the average length (in characters) across all x-value strings
     */
//    private void calcXValAverageLength() {
//
//        if (mXVals.size() == 0) {
//            mXValAverageLength = 1;
//            return;
//        }
//
//        float sum = 0f;
//
//        for (int i = 0; i < mXVals.size(); i++) {
//            sum += mXVals.get(i).length();
//        }
//
//        mXValAverageLength = sum / (float) mXVals.size();
//    }

    /**
     * Checks if the combination of x-values array and DataSet array is legal or
     * not.
     *
     * @param dataSets
     */
    private void isLegal(ArrayList<T> dataSets) {

        if (dataSets == null)
            return;

        for (int i = 0; i < dataSets.size(); i++) {
            if (dataSets.get(i)
                    .getYVals()
                    .size() > mXVals.size()) {
                throw new IllegalArgumentException(
                        "One or more of the DataSet Entry arrays are longer than the x-values array of this ChartData object.");
            }
        }
    }

    /**
     * Call this method to let the CartData know that the underlying data has
     * changed.
     */
    public void notifyDataChanged() {
        init();
    }

    /**
     * calc minimum and maximum y value over all datasets
     */
    protected void calcMinMax(ArrayList<T> dataSets) {

        if (dataSets == null || dataSets.size() < 1) {

            mYMax = BigDecimal.ZERO;
            mYMin = BigDecimal.ZERO;
        } else {

            mYMin = dataSets.get(0).getYMin();
            mYMax = dataSets.get(0).getYMax();

            for (int i = 0; i < dataSets.size(); i++) {
                if (mYMin.compareTo(dataSets.get(i).getYMin()) > 0)
                    mYMin = dataSets.get(i).getYMin();

                if (mYMax.compareTo(dataSets.get(i).getYMax()) < 0)
                    mYMax = dataSets.get(i).getYMax();
            }
        }
    }

    /**
     * calculates the sum of all y-values in all datasets
     */
    protected void calcYValueSum(ArrayList<T> dataSets) {

        mYValueSum = BigDecimal.ZERO;

        if (dataSets == null)
            return;

        for (T t :dataSets) {
            mYValueSum = mYValueSum.add(t.getYValueSum());
        }
//        for (int i = 0; i < dataSets.size(); i++) {
//            mYValueSum = mYValueSum.add(dataSets.get(i).getYValueSum());
//        }
    }

    /**
     * Calculates the total number of y-values across all DataSets the ChartData
     * represents.
     *
     * @return
     */
    protected void calcYValueCount(ArrayList<T> dataSets) {

        mYValCount = 0;

        if (dataSets == null)
            return;

        int count = 0;

        for (int i = 0; i < dataSets.size(); i++) {
            count += dataSets.get(i).getEntryCount();
        }

        mYValCount = count;
    }

    /** ONLY GETTERS AND SETTERS BELOW THIS */

    /**
     * returns the number of LineDataSets this object contains
     *
     * @return
     */
    public int getDataSetCount() {
        if (mDataSets == null)
            return 0;
        return mDataSets.size();
    }

    /**
     * Returns the smallest y-value the data object contains.
     *
     * @return
     */
    public BigDecimal getYMin() {
        return mYMin;
    }

    /**
     * Returns the greatest y-value the data object contains.
     *
     * @return
     */
    public BigDecimal getYMax() {
        return mYMax;
    }

    /**
     * returns the average length (in characters) across all values in the
     * x-vals array
     *
     * @return
     */
    public BigDecimal getXValAverageLength() {
        return mXValAverageLength;
    }

    /**
     * Returns the total y-value sum across all DataSet objects the this object
     * represents.
     *
     * @return
     */
    public BigDecimal getYValueSum() {
        return mYValueSum;
    }

    /**
     * Returns the total number of y-values across all DataSet objects the this
     * object represents.
     *
     * @return
     */
    public int getYValCount() {
        return mYValCount;
    }

    // /**
    // * Checks if the ChartData object contains valid data
    // *
    // * @return
    // */
    // public boolean isValid() {
    // if (mXVals == null || mXVals.size() < 1)
    // return false;
    //
    // if (mDataSets == null || mDataSets.size() < 1)
    // return false;
    //
    // return true;
    // }

    /**
     * returns the x-values the chart represents
     *
     * @return
     */
    public ArrayList<String> getXVals() {
        return mXVals;
    }

    public String getXVal(int index) {
        return mXVals.get(index);
    }

    /**
     * Returns an the array of DataSets this object holds.
     *
     * @return
     */
    public ArrayList<T> getDataSets() {
        return mDataSets;
    }

    // /**
    // * returns the Entries array from the DataSet at the given index. If a
    // * filter is set, the filtered Entries are returned
    // *
    // * @param index
    // * @return
    // */
    // public ArrayList<Entry> getYVals(int index) {
    // return mDataSets.get(index).getYVals();
    // }

    /**
     * Retrieve the index of a DataSet with a specific label from the ChartData.
     * Search can be case sensitive or not. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     *
     * @param dataSets   the DataSet array to search
     * @param label
     * @param ignorecase if true, the search is not case-sensitive
     * @return
     */
    protected int getDataSetIndexByLabel(ArrayList<T> dataSets, String label,
                                         boolean ignorecase) {

        if (ignorecase) {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equalsIgnoreCase(dataSets.get(i).getLabel()))
                    return i;
        } else {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equals(dataSets.get(i).getLabel()))
                    return i;
        }

        return -1;
    }

    public int getDataSetIndexByLabel(String label) {
        return getDataSetIndexByLabel(mDataSets, label, true);
    }

    /**
     * returns the total number of x-values this ChartData object represents
     * (the size of the x-values array)
     *
     * @return
     */
    public int getXValCount() {
        return mXVals.size();
    }

    /**
     * Returns the labels of all DataSets as a string array.
     *
     * @return
     */
    protected String[] getDataSetLabels() {

        String[] types = new String[mDataSets.size()];

        for (int i = 0; i < mDataSets.size(); i++) {
            types[i] = mDataSets.get(i).getLabel();
        }

        return types;
    }

    /**
     * Returns the DataSet object with the given label. Search can be case
     * sensitive or not. IMPORTANT: This method does calculations at runtime.
     * Use with care in performance critical situations.
     *
     * @param label
     * @param ignoreCase
     * @return
     */
    public T getDataSetByLabel(String label, boolean ignoreCase) {

        int index = getDataSetIndexByLabel(mDataSets, label, ignoreCase);
        //原有 index<=0 无法去除第一列
        if (index < 0 || index >= mDataSets.size())
            return null;
        else
            return mDataSets.get(index);
    }

    public boolean containDataSetByLabel(String label, boolean ignoreCase) {
        int index = getDataSetIndexByLabel(mDataSets, label, ignoreCase);
        return (index > 0 && index < mDataSets.size());
    }

    /**
     * Returns the DataSet object at the given index.
     *
     * @param index
     * @return
     */
    public T getDataSetByIndex(int index) {

        if (mDataSets == null || index < 0 || index >= mDataSets.size())
            return null;

        return mDataSets.get(index);
    }


    /**
     * Adds a DataSet dynamically.
     *
     * @param d
     */
    public void addDataSet(T d) {
        if (mDataSets == null)
            mDataSets = new ArrayList<T>();
        mDataSets.add(d);

        mYValCount += d.getEntryCount();
        mYValueSum = mYValueSum.add(d.getYValueSum());

        if (mYMax.compareTo(d.getYMax()) < 0) {
            mYMax = d.getYMax();
        }
        if (mYMin.compareTo(d.getYMin()) > 0) {
            mYMin = d.getYMin();
        }
//        if (mYMax < d.getYMax())
//            mYMax = d.getYMax();
//        if (mYMin > d.getYMin())
//            mYMin = d.getYMin();
    }

    /**
     * Removes the given DataSet from this data object. Also recalculates all
     * minimum and maximum values. Returns true if a DataSet was removed, false
     * if no DataSet could be removed.
     *
     * @param d
     */
    public boolean removeDataSet(T d) {

        if (mDataSets == null || d == null)
            return false;

        boolean removed = mDataSets.remove(d);

        // if a DataSet was removed
        if (removed) {

            mYValCount -= d.getEntryCount();
            mYValueSum = mYValueSum.subtract(d.getYValueSum());

            calcMinMax(mDataSets);
        }

        return removed;
    }

    /**
     * Removes the DataSet at the given index in the DataSet array from the data
     * object. Also recalculates all minimum and maximum values. Returns true if
     * a DataSet was removed, false if no DataSet could be removed.
     *
     * @param index
     */
    public boolean removeDataSet(int index) {

        if (mDataSets == null || index >= mDataSets.size() || index < 0)
            return false;

        T set = mDataSets.get(index);
        return removeDataSet(set);
    }

    /**
     * Adds an Entry to the DataSet at the specified index. Entries are added to
     * the end of the list.
     *
     * @param e
     * @param dataSetIndex
     */
    public void addEntry(GridEntry e, int dataSetIndex) {

        BigDecimal val = e.getFloatVal();
        mYValCount += 1;
        mYValueSum = mYValueSum.add(val);

        if (mYMax.compareTo(val) < 0) {
            mYMax = val;
        }
        if (mYMin.compareTo(val) > 0) {
            mYMin = val;
        }
//        if (mYMax < val)
//            mYMax = val;
//        if (mYMin > val)
//            mYMin = val;

        if (mDataSets == null)
            mDataSets = new ArrayList<T>();

        if (mDataSets.size() > dataSetIndex) {
            T set = mDataSets.get(dataSetIndex);

            if (set != null) {
                // add the entry to the dataset
                set.addEntry(e);
            }
        } else {
            Log.e("addEntry", "Cannot add Entry because dataSetIndex too high.");
        }
    }

    public void addXVal(String xVal) {
        mXVals.add(xVal);
    }

    /**
     * Removes the given Entry object from the DataSet at the specified index.
     *
     * @param e
     * @param dataSetIndex
     */
    public boolean removeEntry(GridEntry e, int dataSetIndex) {

        // entry null, outofbounds
        if (e == null || dataSetIndex >= mDataSets.size())
            return false;

        // remove the entry from the dataset
        boolean removed = mDataSets.get(dataSetIndex).removeEntry(e.getXIndex());

        if (removed) {

            BigDecimal val = e.getFloatVal();

            mYValCount -= 1;
            mYValueSum = mYValueSum.subtract(val);

            calcMinMax(mDataSets);
        }

        return removed;
    }

    /**
     * Removes the Entry object at the given xIndex from the DataSet at the
     * specified index. Returns true if an Entry was removed, false if no Entry
     * was found that meets the specified requirements.
     *
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    public boolean removeEntry(int xIndex, int dataSetIndex) {

        if (dataSetIndex >= mDataSets.size())
            return false;

        T dataSet = mDataSets.get(dataSetIndex);
        GridEntry e = dataSet.getEntryForXIndex(xIndex);

        return removeEntry(e, dataSetIndex);
    }

    /**
     * Returns the DataSet that contains the provided Entry, or null, if no
     * DataSet contains this Entry.
     *
     * @param e
     * @return
     */
    public T getDataSetForEntry(GridEntry e) {

        if (e == null)
            return null;

        for (int i = 0; i < mDataSets.size(); i++) {

            T set = mDataSets.get(i);

            for (int j = 0; j < set.getEntryCount(); j++) {
                if (e.equalTo(set.getEntryForXIndex(e.getXIndex())))
                    return set;
            }
        }

        return null;
    }

    /**
     * Returns all colors used across all DataSet objects this object
     * represents.
     *
     * @return
     */
    public int[] getColors() {

        if (mDataSets == null)
            return null;

        int clrcnt = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            clrcnt += mDataSets.get(i).getColors().size();
        }

        int[] colors = new int[clrcnt];
        int cnt = 0;

        for (int i = 0; i < mDataSets.size(); i++) {

            List<Integer> clrs = mDataSets.get(i).getColors();

            for (Integer clr : clrs) {
                colors[cnt] = clr;
                cnt++;
            }
        }

        return colors;
    }

    /**
     * Generates an x-values array filled with numbers in range specified by the
     * parameters. Can be used for convenience.
     *
     * @return
     */
    public static ArrayList<String> generateXVals(int from, int to) {

        ArrayList<String> xvals = new ArrayList<String>();

        for (int i = from; i < to; i++) {
            xvals.add("" + i);
        }

        return xvals;
    }

    public List<Integer> filterByYLabels(String label, String text) {
        for (int i = 0; i < mDataSets.size(); i++) {
            if (label.equals(mDataSets.get(i).getLabel())) {
                return mDataSets.get(i).filter(text);
            }
        }
        return null;
    }
    public List<Integer> filterByYLabels(int yLabelIndex,String text) {
        return mDataSets.get(yLabelIndex).filter(text);
    }
}
