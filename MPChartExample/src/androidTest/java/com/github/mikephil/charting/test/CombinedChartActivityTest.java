package com.github.mikephil.charting.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import android.view.View;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CombinedData;
import com.xxmassdeveloper.mpchartexample.CombinedChartActivity;
import com.xxmassdeveloper.mpchartexample.R;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class CombinedChartActivityTest {
    @Rule
    public ActivityTestRule<CombinedChartActivity> rule = new ActivityTestRule<>(CombinedChartActivity.class);

    @Test
    public void testCase1() {
        CombinedChartActivity activity = rule.getActivity();
        View view = activity.findViewById(R.id.chart1);

        assertNotNull(view);
        assertTrue(view instanceof CombinedChart);

        CombinedChart chart = (CombinedChart) view;
        assertFalse(chart.isDrawBarShadowEnabled());
        assertTrue(chart.isDrawValueAboveBarEnabled());
        assertFalse(chart.isHighlightFullBarEnabled());

        chart.setDrawBarShadow(false);
        assertFalse(chart.isDrawBarShadowEnabled());

        chart.setDrawValueAboveBar(true);
        assertTrue(chart.isDrawValueAboveBarEnabled());

        chart.setHighlightFullBarEnabled(false);
        assertFalse(chart.isHighlightFullBarEnabled());

        chart.setDrawBarShadow(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setDrawBarShadow(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setDrawBarShadow(false);
        assertFalse(chart.isDrawBarShadowEnabled());

        chart.setDrawValueAboveBar(false);
        assertFalse(chart.isDrawValueAboveBarEnabled());

        chart.setDrawValueAboveBar(false);
        assertFalse(chart.isDrawValueAboveBarEnabled());

        chart.setDrawValueAboveBar(true);
        assertTrue(chart.isDrawValueAboveBarEnabled());

        chart.setHighlightFullBarEnabled(true);
        assertTrue(chart.isHighlightFullBarEnabled());

        chart.setHighlightFullBarEnabled(true);
        assertTrue(chart.isHighlightFullBarEnabled());

        chart.setHighlightFullBarEnabled(false);
        assertFalse(chart.isHighlightFullBarEnabled());

        //set data
        CombinedData testData = new CombinedData();
        testData.setData(chart.getBarData());
        chart.setData(testData);

        assertNotNull(chart.getData());
        assertTrue(chart.getData() == testData);
    }

    @Test
    public void testCase2() {
        CombinedChartActivity activity = rule.getActivity();
        View view = activity.findViewById(R.id.chart1);

        assertNotNull(view);
        assertTrue(view instanceof CombinedChart);

        CombinedChart chart = (CombinedChart) view;

        chart.setHighlightFullBarEnabled(true);
        assertTrue(chart.isHighlightFullBarEnabled());

        chart.setHighlightFullBarEnabled(false);
        assertFalse(chart.isHighlightFullBarEnabled());

        chart.setDrawValueAboveBar(false);
        assertFalse(chart.isDrawValueAboveBarEnabled());

        chart.setHighlightFullBarEnabled(true);
        assertTrue(chart.isHighlightFullBarEnabled());

        chart.setDrawValueAboveBar(true);
        assertTrue(chart.isDrawValueAboveBarEnabled());

        chart.setDrawValueAboveBar(false);
        assertFalse(chart.isDrawValueAboveBarEnabled());

        chart.setHighlightFullBarEnabled(false);
        assertFalse(chart.isHighlightFullBarEnabled());

        chart.setDrawValueAboveBar(true);
        assertTrue(chart.isDrawValueAboveBarEnabled());

        chart.setDrawBarShadow(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setHighlightFullBarEnabled(true);
        assertTrue(chart.isHighlightFullBarEnabled());

        chart.setDrawBarShadow(false);
        assertFalse(chart.isDrawBarShadowEnabled());

        chart.setDrawBarShadow(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setDrawValueAboveBar(false);
        assertFalse(chart.isDrawValueAboveBarEnabled());

        chart.setDrawBarShadow(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setDrawBarShadow(false);
        assertFalse(chart.isDrawBarShadowEnabled());

        chart.setHighlightFullBarEnabled(false);
        assertFalse(chart.isHighlightFullBarEnabled());

        chart.setDrawBarShadow(false);
        assertFalse(chart.isDrawBarShadowEnabled());

        chart.setDrawBarShadow(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setDrawValueAboveBar(true);
        assertTrue(chart.isDrawBarShadowEnabled());

        chart.setDrawValueAboveBar(false);
        assertFalse(chart.isDrawValueAboveBarEnabled());

        chart.setHighlightFullBarEnabled(true);
        assertTrue(chart.isHighlightFullBarEnabled());

        chart.setDrawValueAboveBar(true);
        assertTrue(chart.isDrawValueAboveBarEnabled());

        chart.setHighlightFullBarEnabled(false);
        assertFalse(chart.isHighlightFullBarEnabled());

        chart.setDrawBarShadow(false);
        assertFalse(chart.isDrawBarShadowEnabled());
    }

    @Test
    public void testCase3() {
        CombinedChartActivity activity = rule.getActivity();
        View view = activity.findViewById(R.id.chart1);

        assertNotNull(view);
        assertTrue(view instanceof CombinedChart);

        CombinedChart chart = (CombinedChart) view;

        assertNotNull(chart.getBarData());
        assertNotNull(chart.getBubbleData());
        assertNotNull(chart.getCandleData());
        assertNotNull(chart.getCombinedData());
        assertNotNull(chart.getLineData());
        assertNotNull(chart.getScatterData());
    }
}
