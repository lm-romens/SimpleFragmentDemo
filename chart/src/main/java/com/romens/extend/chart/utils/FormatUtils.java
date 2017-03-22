package com.romens.extend.chart.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by zhoulisi on 14/12/22.
 */
public class FormatUtils {

    public static String formatMoney(float value) {
        NumberFormat nf = new DecimalFormat("#,###.##");
        return nf.format(value);
    }

    public static float stringToFloat(String value) throws ParseException {
        if (TextUtils.isEmpty(value)) {
            return 0f;
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        return numberFormat.parse(value).floatValue();
    }
}
