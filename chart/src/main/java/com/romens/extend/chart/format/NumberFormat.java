package com.romens.extend.chart.format;

import com.romens.extend.chart.utils.FormatUtils;

import java.text.ParseException;

/**
 * Created by zhoulisi on 14/12/26.
 */
public class NumberFormat extends DefaultFormat {
    public String format(String value) throws ParseException {
        float valueTemp = FormatUtils.stringToFloat(value);
        return FormatUtils.formatMoney(valueTemp);
    }

    public String error(String value) {
        return String.valueOf(Float.NaN);
    }
}
