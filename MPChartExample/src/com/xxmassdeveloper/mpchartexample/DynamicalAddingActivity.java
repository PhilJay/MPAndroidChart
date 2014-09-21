
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class DynamicalAddingActivity extends DemoBase implements OnChartValueSelectedListener {

    private LineChart mChart;
    private LineData mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart_noseekbar);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawYValues(false);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");

        // create 30 x-vals
        String[] xVals = new String[30];

        for (int i = 0; i < 30; i++)
            xVals[i] = "" + i;

        // create 10 y-vals
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < 10; i++)
            yVals.add(new Entry((float) (Math.random() * 50) + 50f, i));

        LineDataSet set = new LineDataSet(yVals, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleSize(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));

        mData = new LineData(xVals, set);

        mChart.setData(mData);
        mChart.invalidate();
    }
    
    int[] mColors = ColorTemplate.VORDIPLOM_COLORS;   

    private void addEntry() {

        LineDataSet set = mData.getDataSetByIndex(0);
        // set.addEntry(...);

        mData.addEntry(new Entry((float) (Math.random() * 50) + 50f, set.getEntryCount()), 0);

        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        // redraw the chart
        mChart.invalidate();
    }

    private void removeLastEntry() {

        LineDataSet set = mData.getDataSetByIndex(0);

        Entry e = set.getEntryForXIndex(set.getEntryCount() - 1);

        mData.removeEntry(e, 0);
        // or remove by index
        // mData.removeEntry(xIndex, dataSetIndex);

        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void addDataSet() {
        
        int count = (mData.getDataSetCount() + 1);

        // create 10 y-vals
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mData.getXValCount(); i++)
            yVals.add(new Entry((float) (Math.random() * 50f) + 50f * count, i));
        

        LineDataSet set = new LineDataSet(yVals, "DataSet " + count);
        set.setLineWidth(2.5f);
        set.setCircleSize(4.5f);
        
        int color = getResources().getColor(mColors[count % mColors.length]);
        
        set.setColor(color);
        set.setCircleColor(color);
        set.setHighLightColor(color);

        mData.addDataSet(set);
        
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void removeDataSet() {

        mData.removeDataSet(mData.getDataSetByIndex(mData.getDataSetCount() - 1));
        
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dynamical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionAddEntry:
                addEntry();
                Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionRemoveEntry:
                removeLastEntry();
                Toast.makeText(this, "Entry removed!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionAddDataSet:
                addDataSet();
                Toast.makeText(this, "DataSet added!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionRemoveDataSet:
                removeDataSet();
                Toast.makeText(this, "DataSet removed!", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
