
package info.appdev.chartexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import info.appdev.chartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class BubbleChartActivity extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private BubbleChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bubblechart);

        setTitle("BubbleChartActivity");

        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);

        seekBarX = findViewById(R.id.seekBarX);
        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = findViewById(R.id.seekBarY);
        seekBarY.setOnSeekBarChangeListener(this);

        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);

        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(false);

        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(true);

        seekBarX.setProgress(10);
        seekBarY.setProgress(50);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTypeface(tfLight);

        YAxis yl = chart.getAxisLeft();
        yl.setTypeface(tfLight);
        yl.setSpaceTop(30f);
        yl.setSpaceBottom(30f);
        yl.setDrawZeroLine(false);

        chart.getAxisRight().setEnabled(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tfLight);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int count = seekBarX.getProgress();
        int range = seekBarY.getProgress();

        tvX.setText(String.valueOf(count));
        tvY.setText(String.valueOf(range));

        ArrayList<BubbleEntry> values1 = new ArrayList<>();
        ArrayList<BubbleEntry> values2 = new ArrayList<>();
        ArrayList<BubbleEntry> values3 = new ArrayList<>();
        Double[] sampleValues = DataTools.Companion.getValues(100);

        for (int i = 0; i < count; i++) {
            values1.add(new BubbleEntry(i, (float) (sampleValues[i+1] * range), (float) (sampleValues[i].floatValue() * range), getResources().getDrawable(R.drawable.star)));
            values2.add(new BubbleEntry(i, (float) (sampleValues[i+2] * range), (float) (sampleValues[i+1].floatValue() * range), getResources().getDrawable(R.drawable.star)));
            values3.add(new BubbleEntry(i, (float) (sampleValues[i] * range), (float) (sampleValues[i+2].floatValue() * range)));
        }

        // create a dataset and give it a type
        BubbleDataSet set1 = new BubbleDataSet(values1, "DS 1");
        set1.setDrawIcons(false);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130);
        set1.setDrawValues(true);

        BubbleDataSet set2 = new BubbleDataSet(values2, "DS 2");
        set2.setDrawIcons(false);
        set2.setIconsOffset(new MPPointF(0, 15));
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
        set2.setDrawValues(true);

        BubbleDataSet set3 = new BubbleDataSet(values3, "DS 3");
        set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130);
        set3.setDrawValues(true);

        ArrayList<IBubbleDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        dataSets.add(set2);
        dataSets.add(set3);

        // create a data object with the data sets
        BubbleData data = new BubbleData(dataSets);
        data.setDrawValues(false);
        data.setValueTypeface(tfLight);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightCircleWidth(1.5f);

        chart.setData(data);
        chart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bubble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/AppDevNext/AndroidChart/blob/master/app/src/main/java/com/xxmassdeveloper/mpchartexample/BubbleChartActivity.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleValues: {
                for (IDataSet set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                for (IDataSet set : chart.getData().getDataSets())
                    set.setDrawIcons(!set.isDrawIconsEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if(chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
            case R.id.animateX: {
                chart.animateX(2000);
                break;
            }
            case R.id.animateY: {
                chart.animateY(2000);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(2000, 2000);
                break;
            }
        }
        return true;
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "BubbleChartActivity");
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
