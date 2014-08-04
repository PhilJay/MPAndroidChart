package com.xxmassdeveloper.mpchartexample.listviewitems;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.xxmassdeveloper.mpchartexample.R;

public class BarChartItem extends ChartItem {
    
    private ColorTemplate mCt;
    private Typeface mTf;
    
    public BarChartItem(ChartData cd, Context c) {
        super(cd);
        
        mCt = new ColorTemplate();
        mCt.addDataSetColors(ColorTemplate.VORDIPLOM_COLORS, c);
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
        holder.chart.setYLabelCount(5);
        holder.chart.setColorTemplate(mCt);
        holder.chart.setBarSpace(20f);
        holder.chart.setYLabelTypeface(mTf);
        holder.chart.setXLabelTypeface(mTf);
        holder.chart.setValueTypeface(mTf);
        holder.chart.setDescription("");
        holder.chart.setDrawVerticalGrid(false);
        holder.chart.setDrawGridBackground(false);

        XLabels xl = holder.chart.getXLabels();
        xl.setCenterXLabelText(true);
        xl.setPosition(XLabelPosition.BOTTOM);

        // set data
        holder.chart.setData(mChartData);
        
        // do not forget to refresh the chart
        holder.chart.invalidate();

        return convertView;
    }
    
    private static class ViewHolder {
        BarChart chart;
    }
}
