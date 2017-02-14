package cn.edu.neusoft.simplefragmentdemo.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Liang Mei on 2017/2/10.
 */

public class TestCell extends LinearLayout {
    public TestCell(Context context) {
        super(context);
        initView(context);
    }

    public TestCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TestCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    TextView textView;

    private void initView(Context context){
        setOrientation(VERTICAL);

        textView=new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        textView.setGravity(Gravity.CENTER);
        addView(textView,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void setValue(String text){
        textView.setText(text);
    }

}
