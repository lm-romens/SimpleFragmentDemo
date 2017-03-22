package com.romens.extend.chart.charts;

/**
 * Created by siery on 15/12/5.
 */
public class GridStyle {
    private int yLabelColor;
    private int fixColor;
    private int dividerRowColor;
    private int sumRowColor;
    private int scrollColor;

    private int headerSelectedColor;
    private int rowColSelectedColor;
    private int cellSelectedColor;

    private GridStyle() {

    }

    public int getYLabelColor() {
        return yLabelColor;
    }

    public int getFixColor() {
        return fixColor;
    }

    public int getDividerRowColor() {
        return dividerRowColor;
    }

    public int getSumRowColor() {
        return sumRowColor;
    }

    public int getScrollColor() {
        return scrollColor;
    }

    public int getHeaderSelectedColor() {
        return headerSelectedColor;
    }

    public int getRowColSelectedColor() {
        return rowColSelectedColor;
    }

    public int getCellSelectedColor() {
        return cellSelectedColor;
    }

    public static final class DefaultBuilder extends Builder {
        public DefaultBuilder() {
            super();
            yLabelColor = 0xff01579b;
            fixColor = 0xFFf0f0f0;
            dividerRowColor = 0xffe1f5fe;
            sumRowColor = 0xffffffff;

            headerSelectedColor = 0xFF1976D2;
            rowColSelectedColor = 0xFF90CAF9;
            cellSelectedColor = 0xFF90CAF9;
        }

    }


    public static class Builder {
        protected int yLabelColor;
        protected int fixColor;
        protected int dividerRowColor;
        protected int sumRowColor;
        protected int scrollColor;

        protected int headerSelectedColor;
        protected int rowColSelectedColor;
        protected int cellSelectedColor;

        public Builder() {

        }

        public Builder setYLabelColor(int color) {
            this.yLabelColor = color;
            return this;
        }

        public Builder setFixColor(int color) {
            this.fixColor = color;
            return this;
        }

        public Builder setDividerRowColor(int color) {
            this.dividerRowColor = color;
            return this;
        }

        public Builder setSumRowColor(int color) {
            this.sumRowColor = color;
            return this;
        }

        public Builder setScrollColor(int color) {
            this.scrollColor = color;
            return this;
        }

        public Builder setHeaderSelectedColor(int color) {
            this.headerSelectedColor = color;
            return this;
        }

        public Builder setRowColSelectedColor(int color) {
            this.rowColSelectedColor = color;
            return this;
        }

        public Builder setCellSelectedColor(int color) {
            this.cellSelectedColor = color;
            return this;
        }

        public GridStyle build() {
            GridStyle gridStyle = new GridStyle();
            gridStyle.yLabelColor = yLabelColor;
            gridStyle.fixColor = fixColor;
            gridStyle.dividerRowColor = dividerRowColor;
            gridStyle.sumRowColor = sumRowColor;
            gridStyle.scrollColor = scrollColor;

            gridStyle.headerSelectedColor = headerSelectedColor;
            gridStyle.rowColSelectedColor = rowColSelectedColor;
            gridStyle.cellSelectedColor = cellSelectedColor;
            return gridStyle;
        }

    }
}
