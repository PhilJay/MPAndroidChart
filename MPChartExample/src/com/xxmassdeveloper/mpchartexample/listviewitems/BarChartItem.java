package com.xxmassdeveloper.mpchartexample.listviewitems;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.xxmassdeveloper.mpchartexample.R;

public class BarChartItem extends ChartItem {
    
    private Typeface mTf;
    
    public BarChartItem(ChartData cd, Context c) {
        super(cd);

        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_BARCHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_barchart, null);
            holder.chart = (BarChart) convertView.findViewById(R.id.chart);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.setValueTypeface(mTf);
        holder.chart.setDescription("");
        holder.chart.setDrawVerticalGrid(false);
        holder.chart.setDrawGridBackground(false);
        holder.chart.setDrawBarShadow(false);

        XLabels xl = holder.chart.getXLabels();
        xl.setCenterXLabelText(true);
        xl.setPosition(XLabelPosition.BOTTOM);
        xl.setTypeface(mTf);
        
        YLabels yl = holder.chart.getYLabels();
        yl.setTypeface(mTf);
        yl.setLabelCount(5);

        // set data
        holder.chart.setData((BarData) mChartData);
        
        // do not forget to refresh the chart
//        holder.chart.invalidate();
        holder.chart.animateY(700);

        return convertView;
    }
    
    private static class ViewHolder {
        BarChart chart;
    }
}
