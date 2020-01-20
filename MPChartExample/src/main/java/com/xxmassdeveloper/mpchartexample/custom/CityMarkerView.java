package com.xxmassdeveloper.mpchartexample.custom;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.data.City;

public class CityMarkerView extends MarkerView {
    private final TextView tv;
    private LargeValueFormatter formatter = new LargeValueFormatter();

    public CityMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tv = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof BubbleEntry) {

            BubbleEntry bubble = (BubbleEntry) e;
            City city = (City) bubble.getData();
            tv.setText(city.name + " " + formatter.getFormattedValue(city.population));
        } else {
            tv.setText("no city selected");
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }

}
