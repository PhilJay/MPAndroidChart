package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

/**
 * The same chart as the Kotlin examples, written in Java. Properties are getters and setters here, a fun
 * interface takes a lambda, and a function with default arguments needs every argument.
 */
public class JavaChartActivity extends DemoBase {

    private static final String[] MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = getLayoutInflater().inflate(R.layout.activity_java_chart, null);
        setContentView(root);
        setTitle("JavaChartActivity");

        chart = root.findViewById(R.id.chart1);

        float[] values = {42f, 48f, 45f, 60f, 58f, 72f, 70f, 84f, 79f, 92f, 88f, 97f};
        List<Entry<Object>> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            // The payload argument has no default, so Java passes it explicitly.
            entries.add(new Entry<>(i, values[i], null, null));
        }

        LineDataSet<Object> set = new LineDataSet<>(entries, "Revenue");
        set.setColor(Color.rgb(47, 180, 182));
        set.setLineWidth(2.5f);
        set.setCircleRadius(4f);
        set.setCircleColor(Color.rgb(47, 180, 182));
        set.setDrawValuesEnabled(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        chart.setData(new LineData(set));
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLinesEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLinesEnabled(false);
        chart.getXAxis().setGranularity(1f);

        // IAxisValueFormatter is a fun interface, so a lambda works from Java too.
        chart.getXAxis().setValueFormatter((value, axis) -> MONTHS[((int) value) % MONTHS.length]);

        // A listener with two methods is an anonymous class.
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry<?> entry, Highlight highlight) {
                String month = MONTHS[((int) entry.getX()) % MONTHS.length];
                Toast.makeText(JavaChartActivity.this, month + ": " + entry.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        // animateX has a default easing in Kotlin, which Java does not see, so both arguments are passed.
        chart.animateX(900, Easing.INSTANCE.getEaseOutCubic());
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "JavaChartActivity");
    }
}
