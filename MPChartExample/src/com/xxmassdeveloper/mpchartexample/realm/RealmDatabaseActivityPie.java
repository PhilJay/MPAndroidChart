package com.xxmassdeveloper.mpchartexample.realm;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.realm.implementation.RealmPieData;
import com.github.mikephil.charting.data.realm.implementation.RealmPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;

import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmDatabaseActivityPie extends RealmBaseActivity {

    private PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_piechart_noseekbar);

        mChart = (PieChart) findViewById(R.id.chart1);
        setup(mChart);

        mChart.setCenterText(generateCenterSpannableText());
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        // write some demo-data into the realm.io database
        writeToDBPie();

        // add data to the chart
        setData();
    }

    private void setData() {

        RealmResults<RealmDemoData> result = mRealm.allObjects(RealmDemoData.class);

        //RealmBarDataSet<RealmDemoData> set = new RealmBarDataSet<RealmDemoData>(result, "stackValues", "xIndex"); // normal entries
        RealmPieDataSet<RealmDemoData> set = new RealmPieDataSet<RealmDemoData>(result, "yValue", "xIndex"); // stacked entries
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setLabel("Example market share");
        set.setSliceSpace(2);

        // create a data object with the dataset list
        RealmPieData data = new RealmPieData(result, "xAxisPosition", "xAxisLabel", set);
        styleData(data);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12f);

        // set data
        mChart.setData(data);
        mChart.animateY(1400);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Realm.io\nmobile database");
        s.setSpan(new ForegroundColorSpan(Color.rgb(240, 115, 126)), 0, 8, 0);
        s.setSpan(new RelativeSizeSpan(2.2f), 0, 8, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 9, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 9, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.85f), 9, s.length(), 0);
        return s;
    }
}
