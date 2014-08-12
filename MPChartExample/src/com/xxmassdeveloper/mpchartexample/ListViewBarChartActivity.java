
package com.xxmassdeveloper.mpchartexample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 * 
 * @author Philipp Jahoda
 */
public class ListViewBarChartActivity extends DemoBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);
        
        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ChartData> list = new ArrayList<ChartData>();

        // 20 items
        for (int i = 0; i < 20; i++) {
            list.add(generateData(i + 1));
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    private class ChartDataAdapter extends ArrayAdapter<ChartData> {

        private Typeface mTf;

        public ChartDataAdapter(Context context, List<ChartData> objects) {
            super(context, 0, objects);

            mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ChartData c = getItem(position);

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(
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
            holder.chart.setValueTextColor(Color.WHITE);

            XLabels xl = holder.chart.getXLabels();
            xl.setCenterXLabelText(true);
            xl.setPosition(XLabelPosition.BOTTOM);
            xl.setTypeface(mTf);
            
            YLabels yl = holder.chart.getYLabels();
            yl.setTypeface(mTf);
            yl.setLabelCount(5);

            // set data
            holder.chart.setData(c);
            
            // do not forget to refresh the chart
            holder.chart.invalidate();

            return convertView;
        }

        private class ViewHolder {

            BarChart chart;
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     * 
     * @return
     */
    private BarData generateData(int cnt) {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new Entry((int) (Math.random() * 70) + 30, i));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);    
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS, getApplicationContext());
        d.setBarShadowColor(Color.rgb(203, 203, 203));
        
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(d);
        
        BarData cd = new BarData(getMonths(), sets);
        return cd;
    }

    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
    }
}
