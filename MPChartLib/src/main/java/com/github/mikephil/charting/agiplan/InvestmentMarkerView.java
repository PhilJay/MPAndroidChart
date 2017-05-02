package com.github.mikephil.charting.agiplan;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.NumberFormat;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentMarkerView extends MarkerView {

    private TextView investmentChartDescription;
    private TextView textViewPercentage;
    private View verticalLine;

    public InvestmentMarkerView (Context context, int layoutResource, int description, int line, int percentage) {
        super(context, layoutResource);
        investmentChartDescription = (TextView) findViewById(description);
        verticalLine = findViewById(line);
        textViewPercentage = (TextView) findViewById(percentage);
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int position = Math.round(highlight.getX());

        InvestmentPieEntry investmentPieEntry = (InvestmentPieEntry)e;
        String investmentPercentage = new PercentFormatter().getFormattedValue(investmentPieEntry.getValue(), null);

        textViewPercentage.setText(investmentPercentage);
        investmentChartDescription.setText(investmentPieEntry.getDetail());


        textViewPercentage.setBackground(investmentPieEntry.getCircle());
        verticalLine.setBackgroundColor(investmentPieEntry.getColor());
    }
}