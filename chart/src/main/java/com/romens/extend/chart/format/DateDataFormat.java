package com.romens.extend.chart.format;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhoulisi on 15/4/7.
 */
public class DateDataFormat extends DataFormat {
    public DateDataFormat(String format) {
        super(format);
    }

    @Override
    public String format(String val) {
        if (!TextUtils.isEmpty(val)) {
            if (!TextUtils.isEmpty(mFormat)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = sdf.parse(val);
                    sdf.applyPattern(mFormat);
                    return sdf.format(date);
                } catch (ParseException e) {
                    return val;
                }
            }
        }
        return val;
    }
}
