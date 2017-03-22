package com.romens.extend.chart.format;

import android.text.TextUtils;
import android.view.TextureView;

/**
 * Created by zhoulisi on 15/4/7.
 */
public class DataFormat {
    protected final String mFormat;

    public DataFormat(String format) {
        this.mFormat = format;
    }

    public String format(String val) {
        if (!TextUtils.isEmpty(mFormat)) {
            return String.format(mFormat.replace("{0}", "%s"), TextUtils.isEmpty(val) ? "" : val);
        }
        return val;
    }
}
