package com.xxmassdeveloper.mpchartexample.agiplan;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.xxmassdeveloper.mpchartexample.R;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentMarkerView extends MarkerView {

    private TextView investmentChartDescription;
    private TextView textViewPercentage;
    private View verticalLine;

    public InvestmentMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        investmentChartDescription = (TextView) findViewById(R.id.investment_chart_description);
        verticalLine = findViewById(R.id.vertical_line_graph);
        textViewPercentage = (TextView) findViewById(R.id.percentage_value_graph);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int position = Math.round(highlight.getX());

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.pie_graph_color_primary));
        colors.add(getResources().getColor(R.color.pie_graph_color_blue));
        colors.add(getResources().getColor(R.color.pie_graph_color_green));

        if (position == 0) {
            textViewPercentage.setBackground(getResources().getDrawable(R.drawable.graph_circle_highlight_primary));
            verticalLine.setBackgroundColor(getResources().getColor(R.color.pie_graph_color_primary));
        } else if (position == 1) {
            textViewPercentage.setBackground(getResources().getDrawable(R.drawable.graph_circle_highlight_blue));
            verticalLine.setBackgroundColor(getResources().getColor(R.color.pie_graph_color_blue));
        } else {
            textViewPercentage.setBackground(getResources().getDrawable(R.drawable.graph_circle_highlight_green));
            verticalLine.setBackgroundColor(getResources().getColor(R.color.pie_graph_color_green));
        }

        InvestmentPieEntry investmentPieEntry = (InvestmentPieEntry)e;
        String investmentValueFormated = NumberFormat.getInstance().format(investmentPieEntry.getValue());
        String investmentPercentage = new PercentFormatter().getFormattedValue(investmentPieEntry.getPercentage(), null);

        textViewPercentage.setText("" + investmentPercentage);
        investmentChartDescription.setText("" + investmentPieEntry.getDescription()
                + "\n" + investmentValueFormated);
    }
}