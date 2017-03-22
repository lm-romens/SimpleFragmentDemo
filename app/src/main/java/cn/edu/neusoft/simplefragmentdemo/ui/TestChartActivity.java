package cn.edu.neusoft.simplefragmentdemo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import cn.edu.neusoft.simplefragmentdemo.util.DensityUtil;

/**
 * @author liang mei
 * @create 2017/3/21
 * @description
 */

public class TestChartActivity extends AppCompatActivity {
    private  BarChart barChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout container=new LinearLayout(this);
        setContentView(container,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView=new ScrollView(this);
        container.addView(scrollView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout content =new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(content,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //柱状图
        initBarChart(content);


    }

    private void initBarChart(LinearLayout content){
        barChart=new BarChart(this);
        barChart.setDrawBarShadow(true);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(this,380));
        content.addView(barChart,params);
        barChart.setPadding(DensityUtil.dip2px(this,16),DensityUtil.dip2px(this,16),DensityUtil.dip2px(this,16),DensityUtil.dip2px(this,16));

        barChart.setDescription("测试柱状图");
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(5,false);

        barChart.setData(initChartData());
        barChart.animateXY(1000,1000);
    }


    private BarData initChartData(){
        List<String> xValues = new ArrayList<>();
        xValues.add("1000");
        xValues.add("1200");
        xValues.add("1800");
        xValues.add("2000");
        List<BarDataSet> yDataSet = new ArrayList<>();

//        if (yColumnsList.length > 1) {
            List<BarEntry> entries = new ArrayList<>();
//            int yColsSize = data.y1Values.size();
//            for (int i = 0; i < yColsSize; i++) {
//                entries.add(new BarEntry(data.y1Values.get(i), i));
//            }

        entries.add(new BarEntry(666.2f,0));
        entries.add(new BarEntry(222.2f,1));
        entries.add(new BarEntry(555.2f,2));
        entries.add(new BarEntry(111.2f,3));
            BarDataSet set = new BarDataSet(entries, "测试");
            set.setColor(Color.BLUE);
            set.setValueTextColor(Color.RED);
            set.setDrawValues(true);
            set.setValueTextSize(10f);
            set.setValueTextColor(0xff212121);
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            yDataSet.add(set);
//        }
//        if (yColumnsList.length > 2) {
//            List<BarEntry> entries = new ArrayList<>();
//            int yColsSize = data.y2Values.size();
//            for (int i = 0; i < yColsSize; i++) {
//                entries.add(new BarEntry(data.y2Values.get(i), i));
//            }
//            BarDataSet set = new BarDataSet(entries, yColumnsList[1]);
//            set.setColor(ResourcesConfig.KPIValueColor1);
//            set.setValueTextColor(ResourcesConfig.KPIValueColor1);
//            set.setDrawValues(true);
//            set.setValueTextSize(10f);
//            set.setValueTextColor(0xff212121);
//            set.setAxisDependency(YAxis.AxisDependency.LEFT);
//            yDataSet.add(set);
//        }
        BarData barData = new BarData(xValues, yDataSet);
        return barData;
    }
}
