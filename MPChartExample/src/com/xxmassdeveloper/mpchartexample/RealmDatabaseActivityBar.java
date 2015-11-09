package com.xxmassdeveloper.mpchartexample;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.realm.implementation.RealmBarDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;
import com.xxmassdeveloper.mpchartexample.notimportant.RealmBaseActivity;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmDatabaseActivityBar extends RealmBaseActivity {

    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart_noseekbar);

        mChart = (BarChart) findViewById(R.id.chart1);
        setup(mChart);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        // write some demo-data into the realm.io database
        writeToDBStack(4);

        // add data to the chart
        setData();
    }

    private void setData() {

        RealmResults<RealmDemoData> result = mRealm.allObjects(RealmDemoData.class);

        RealmBarDataSet<RealmDemoData> set = new RealmBarDataSet<RealmDemoData>(result, "stackValues", "xIndex");
        System.out.println("Act: " + set.getStackSize());
        set.setValueTextSize(9f);
        set.setColors(getColors(set.getStackSize()));
        set.setLabel("Realm BarDataSet");
        set.setStackLabels(new String[]{"Births", "Divorces", "Marriages"});
        System.out.println("Act: " + set.getStackSize());

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        BarData data = new BarData(result, "xValue", dataSets);

        // set data
        mChart.setData(data);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    private int[] getColors(int stacksize) {

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
        }

        return colors;
    }
}
