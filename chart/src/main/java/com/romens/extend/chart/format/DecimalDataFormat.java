package com.romens.extend.chart.format;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by zhoulisi on 15/4/7.
 */
public class DecimalDataFormat extends DataFormat {
    public DecimalDataFormat(String format) {
        super(format);
    }

    @Override
    public String format(String val) {
        if (!TextUtils.isEmpty(val)) {
            if (!TextUtils.isEmpty(mFormat)) {
                BigDecimal decimal = new BigDecimal(val);
                return format(decimal);
            }
        }
        return val;
    }

    public String format(BigDecimal val) {
        if (val == null) {
            val = BigDecimal.ZERO;
        }
        if (TextUtils.isEmpty(mFormat)) {
            return val.toString();
        }
        DecimalFormat df = new DecimalFormat(mFormat);
        return df.format(val);
    }
}
