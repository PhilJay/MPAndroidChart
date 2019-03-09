package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.highlight.BubbleHighlighter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BubbleChartHighlightActivity extends BubbleChartActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("BubbleChartHighlightActivity");
        seekBarX.setVisibility(View.GONE);
        seekBarY.setVisibility(View.GONE);
        tvX.setVisibility(View.GONE);
        tvY.setVisibility(View.GONE);

        chart.getLegend().setEnabled(false);

        ArrayList<BubbleEntry> values1 = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            float y = (float) (Math.random() * 40);
            float size = (float) (Math.random() * 20) + 20;
            switch (i % 3) {
                case 0:
                    values1.add(new BubbleEntry(i, y, size));
                    values1.add(new BubbleEntry(i + 0.2f, y + 5f, size));
                    values1.add(new BubbleEntry(i + 0.4f, y + 10f, size));
                    values1.add(new BubbleEntry(i + 0.6f, y + 15f, size));
                    break;
                case 1:
                    values1.add(new BubbleEntry(i - 0.6f, y, size));
                    values1.add(new BubbleEntry(i - 0.4f, y + 5f, size));
                    values1.add(new BubbleEntry(i - 0.2f, y + 10f, size));
                    values1.add(new BubbleEntry(i, y + 15f, size));
                    break;
                default:
                    values1.add(new BubbleEntry(i, y, size));
                    values1.add(new BubbleEntry(i, y + 5, size));
                    values1.add(new BubbleEntry(i, y + 10f, size));
                    values1.add(new BubbleEntry(i, y + 15f, size));
            }
        }

        // sort by x
        Collections.sort(values1, new Comparator<BubbleEntry>() {
            @Override
            public int compare(BubbleEntry o1, BubbleEntry o2) {
                return Float.compare(o1.getX(), o2.getX());
            }
        });

        // create a data set and give it a type
        BubbleDataSet set1 = new BubbleDataSet(values1, "DS 1");
        set1.setDrawIcons(false);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 0x80);
        set1.setDrawValues(true);

        // create a data object with the data set
        BubbleData data = new BubbleData(set1);
        data.setDrawValues(false);
        data.setValueTypeface(tfLight);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightCircleWidth(5f);

        chart.setData(data);
        chart.setHighlighter(new BubbleHighlighter(chart));
        chart.invalidate();
    }

}
