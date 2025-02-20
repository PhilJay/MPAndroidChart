
package com.github.mikephil.charting.test;

import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.*;

public class DrawValuesHorizontalChartTest {

    private BarChartRenderer renderer;
    private Canvas mockCanvas;
    private BarChart mockChart;
    private BarData mockBarData;
    private BarEntry mockEntry;
    private ViewPortHandler mockViewPortHandler;
    private ChartAnimator mockChartAnimator;
    private IValueFormatter mockFormatter;

    @Before
    public void setUp() {
        // Mock dependencies
        mockCanvas = mock(Canvas.class);
        mockChart = mock(BarChart.class);
        mockBarData = mock(BarData.class);
        mockEntry = mock(BarEntry.class);
        mockViewPortHandler = mock(ViewPortHandler.class);
        mockChartAnimator = mock(ChartAnimator.class);
        mockFormatter = mock(IValueFormatter.class);

        // Set up renderer
        renderer = new HorizontalBarChartRenderer(mockChart, mockChartAnimator, mockViewPortHandler);

        when(mockChart.getData()).thenReturn(mockBarData);
        when(mockChart.getBarData()).thenReturn(mockBarData);
    }

    /*
     * This tests if anything is drawn on the canvas. 
     * It is an unstacked/single horizontal bar chart that should get text from a data set drawn on it. 
     */
    @Test
    public void testDrawValues_CallsDrawValueMethod() {
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(0, 0));
        entries.add(new BarEntry(1, 1));
        entries.add(new BarEntry(2, 2));
                
        IBarDataSet dataSet = new BarDataSet(entries, "Test Data");
        
        List<IBarDataSet> datasets = Arrays.asList(dataSet);

        when(mockBarData.getDataSets()).thenReturn(datasets);
        when(mockBarData.getDataSetByIndex(0)).thenReturn(dataSet);
       
        // Entry count must be smaller than maxVisibleCount * scaleY
        // for isDrawingValuesAllowed(mChart) to be true. 
        when(mockBarData.getEntryCount()).thenReturn(3);
        when(mockChart.getMaxVisibleCount()).thenReturn(4);
        when(mockViewPortHandler.getScaleY()).thenReturn(1f);

        when(mockChart.isDrawValueAboveBarEnabled()).thenReturn(true);

        when(mockBarData.getDataSetCount()).thenReturn(1);

        // Dataset must be visible and isDrawValues or isDrawIcons must be enabled
        // for shouldDrawValues(dataSet) to be true.
        dataSet.setVisible(true);
        dataSet.setDrawValues(true);
        dataSet.setDrawIcons(false);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        when(mockChart.isInverted(YAxis.AxisDependency.LEFT)).thenReturn(true);

        dataSet.setValueFormatter(mockFormatter);

        when(mockChartAnimator.getPhaseX()).thenReturn(1f);
        when(mockChartAnimator.getPhaseY()).thenReturn(1f);

        // We do not know what the buffer will be.
        when(mockViewPortHandler.isInBoundsTop(anyFloat())).thenReturn(true);
        when(mockViewPortHandler.isInBoundsX(anyFloat())).thenReturn(true);
        when(mockViewPortHandler.isInBoundsBottom(anyFloat())).thenReturn(true);

        when(mockFormatter.getFormattedValue(anyFloat(), any(BarEntry.class), anyInt(), any(ViewPortHandler.class))).thenReturn("Mock");

        // Call the method
        renderer.initBuffers();
        renderer.drawValues(mockCanvas);

        verify(mockCanvas, atLeastOnce()).drawText(eq("Mock"), anyFloat(), anyFloat(), any(Paint.class));
    }

    /*
    * This tests checks so that nothing is drawn on the canvas. 
    * isDrawingValuesAllowed is false and therefore nothing should be drawn.  
    */
    @Test
    public void testDrawValues_DrawsNothingIfNotAllowed() {
        // Entry count must be smaller than maxVisibleCount * scaleY
        // for isDrawingValuesAllowed(mChart) to be false. 
        when(mockBarData.getEntryCount()).thenReturn(5);
        when(mockChart.getMaxVisibleCount()).thenReturn(4);
        when(mockViewPortHandler.getScaleY()).thenReturn(1f);

       // Call the method
        renderer.initBuffers();
        renderer.drawValues(mockCanvas);

        verify(mockCanvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    /*
     * This tests checks so that nothing is drawn on the canvas. 
     * shouldDrawValues is false and there are no icons to be drawn so therefore nothing should be drawn.  
     */
    @Test
    public void testDrawValues_DrawsNothingIfItShouldnt() {
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(0, 0));
        entries.add(new BarEntry(1, 1));
        entries.add(new BarEntry(2, 2));
                
        IBarDataSet dataSet = new BarDataSet(entries, "Test Data");
        
        List<IBarDataSet> datasets = Arrays.asList(dataSet);

        when(mockBarData.getDataSets()).thenReturn(datasets);
        when(mockBarData.getDataSetByIndex(0)).thenReturn(dataSet);
       
        // Entry count must be smaller than maxVisibleCount * scaleY
        // for isDrawingValuesAllowed(mChart) to be true. 
        when(mockBarData.getEntryCount()).thenReturn(3);
        when(mockChart.getMaxVisibleCount()).thenReturn(4);
        when(mockViewPortHandler.getScaleY()).thenReturn(1f);

        when(mockChart.isDrawValueAboveBarEnabled()).thenReturn(true);

        when(mockBarData.getDataSetCount()).thenReturn(1);

        // Dataset must be visible and isDrawValues or isDrawIcons must be enabled
        // for shouldDrawValues(dataSet) to be true.
        dataSet.setVisible(true);
        dataSet.setDrawValues(false); // THIS IS FALSE
        dataSet.setDrawIcons(false); // THIS IS FALSE

        // Call the method
        renderer.initBuffers();
        renderer.drawValues(mockCanvas);

        verify(mockCanvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }


    /*
     * This tests checks so that nothing is drawn on the canvas. 
     * There are icons to be drawn however shouldDrawValues is false so nothing should be 
     * drawn (even though setup such as offset has been calculated since there are things to draw)
     */
    @Test
    public void testDrawValues_DrawsNothingIfNotEnabled() {
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(0, 0));
        entries.add(new BarEntry(1, 1));
        entries.add(new BarEntry(2, 2));
                
        IBarDataSet dataSet = new BarDataSet(entries, "Test Data");
        
        List<IBarDataSet> datasets = Arrays.asList(dataSet);

        when(mockBarData.getDataSets()).thenReturn(datasets);
        when(mockBarData.getDataSetByIndex(0)).thenReturn(dataSet);
       
        // Entry count must be smaller than maxVisibleCount * scaleY
        // for isDrawingValuesAllowed(mChart) to be true. 
        when(mockBarData.getEntryCount()).thenReturn(3);
        when(mockChart.getMaxVisibleCount()).thenReturn(4);
        when(mockViewPortHandler.getScaleY()).thenReturn(1f);

        when(mockChart.isDrawValueAboveBarEnabled()).thenReturn(true);

        when(mockBarData.getDataSetCount()).thenReturn(1);

        // Dataset must be visible and isDrawValues or isDrawIcons must be enabled
        // for shouldDrawValues(dataSet) to be true.
        dataSet.setVisible(true);
        dataSet.setDrawValues(false); // THIS IS FALSE
        dataSet.setDrawIcons(true); // THIS IS TRUE

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        when(mockChart.isInverted(YAxis.AxisDependency.LEFT)).thenReturn(true);

        dataSet.setValueFormatter(mockFormatter);

        when(mockChartAnimator.getPhaseX()).thenReturn(1f);
        when(mockChartAnimator.getPhaseY()).thenReturn(1f);

        // We do not know what the buffer will be.
        when(mockViewPortHandler.isInBoundsTop(anyFloat())).thenReturn(true);
        when(mockViewPortHandler.isInBoundsX(anyFloat())).thenReturn(true);
        when(mockViewPortHandler.isInBoundsBottom(anyFloat())).thenReturn(true);

        when(mockFormatter.getFormattedValue(anyFloat(), any(BarEntry.class), anyInt(), any(ViewPortHandler.class))).thenReturn("Mock");

        // Call the method
        renderer.initBuffers();
        renderer.drawValues(mockCanvas);

        verify(mockCanvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }
}