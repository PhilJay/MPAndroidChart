package com.github.mikephil.charting.test;

import android.graphics.Canvas;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.MPPointF;



import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BarChartRendererTest {

    /**
     * Requirements:
     * 1. The method doesn't draw if not allowed to.
     * 2. The method handles gettin no data
     * 3. The outer for loop iterates the correct number of times.
     * 4. The method handles valid data.
     * */

    private TestBarChartRendererNotAllowed mBarChartRendererNotAllowed;
    private TestBarChartRendererAllowed mBarChartRendererAllowed;
    private BarDataProvider mChartMock;
    private BarData mBarDataMock;
    private IBarDataSet mDataSetMock;
    private ChartAnimator mAnimatorMock;
    private ViewPortHandler mViewPortHandlerMock;
    private Canvas mCanvasMock;

    /*Variables that are specifically used for the valid data (Test 4)*/
    BarDataProvider mChartValidData;
    BarData mBarDataValid;
    IBarDataSet dataSetMock;
    TestBarChartRendererValidData mRenderer;
    MPPointF iconOffsetMock;

    @Before
    public void setUp() {
        /*
        * This setUp method handles the set up for tests 1-3, since they don't require any valid data input.
        * It generates mock objects using Mockito and specifies specific behavior that is necessary
        * for the drawValues() method.
        * */

        mChartMock = mock(BarDataProvider.class);
        mAnimatorMock = mock(ChartAnimator.class);
        mViewPortHandlerMock = mock(ViewPortHandler.class);
        mCanvasMock = mock(Canvas.class);
        mBarDataMock = mock(BarData.class);
        mDataSetMock = mock(IBarDataSet.class);


        List<IBarDataSet> dataSets = new ArrayList<>();

        // Add two data entries (that are not a valid format):
        for(int i = 0; i < 2; i++) dataSets.add(mDataSetMock);

        when(mBarDataMock.getDataSets()).thenReturn(dataSets);
        when(mChartMock.getBarData()).thenReturn(mBarDataMock);

        // Use Mock classes
        mBarChartRendererNotAllowed = new TestBarChartRendererNotAllowed(mChartMock, mAnimatorMock, mViewPortHandlerMock);
        mBarChartRendererNotAllowed = spy(mBarChartRendererNotAllowed);

        mBarChartRendererAllowed = new TestBarChartRendererAllowed(mChartMock, mAnimatorMock, mViewPortHandlerMock);
        mBarChartRendererAllowed = spy(mBarChartRendererAllowed);
    }



    @Before
    public void setUpValidData(){
        /*
         * This setUp method handles the set up for test 4, since it requires valid data input.
         * It generates mock objects using Mockito and specifies specific behavior that is necessary
         * for the drawValues() method. The comments below stating line X means that it defines
         * the required behavior found in the BarChartRenderer file where the drawValues() method
         * is located.
         * */
        mChartValidData = mock(BarDataProvider.class);
        mBarDataValid = mock(BarData.class);
        // line 209
        dataSetMock = mock(IBarDataSet.class);
        when(mBarDataValid.getDataSets()).thenReturn(new ArrayList<>(Arrays.asList(dataSetMock,dataSetMock,dataSetMock,dataSetMock)));
        when(mChartValidData.getBarData()).thenReturn(mBarDataValid);

        // line 214
        when(mChartValidData.isDrawValueAboveBarEnabled()).thenReturn(true);

        // line 216
        when(mBarDataValid.getDataSetCount()).thenReturn(4); // has to match number of datapoints.


        // line 220 and 224 handled in mock class definition.

        // line 226
        when(dataSetMock.getAxisDependency()).thenReturn(AxisDependency.LEFT);
        when(mChartValidData.isInverted(AxisDependency.LEFT)).thenReturn(false);

        // line 230

        // line 242
        when(mAnimatorMock.getPhaseY()).thenReturn(1f);

        // line 244
        iconOffsetMock = mock(MPPointF.class);
        when(dataSetMock.getIconsOffset()).thenReturn(iconOffsetMock);
        // line 251 I want to exit, just want to test if it gets through the outer for loop.
        when(mAnimatorMock.getPhaseX()).thenReturn(0f); // This should make the inner loop not execute.


        mRenderer = new TestBarChartRendererValidData(mChartValidData, mAnimatorMock, mViewPortHandlerMock); //Potentially update the ones that are old.
        //line 62
        when(mBarDataValid.getDataSetByIndex(anyInt())).thenReturn(dataSetMock);
        when(dataSetMock.getEntryCount()).thenReturn(1);
        when(dataSetMock.isStacked()).thenReturn(false);
        mRenderer.initBuffers();
        mRenderer = spy(mRenderer);
    }

    @Test
    public void testDrawValuesNotAllowed() {
        /* Tests whether the correct branches of the code are run when drawing is not allowed,
        * it does this by checking if the correct functions are called (the correct number of times
        * without calling and functions in branches that are not covered by the test.*/

        mBarChartRendererNotAllowed.drawValues(mCanvasMock);

        verify(mBarChartRendererNotAllowed, times(1)).drawValues(mCanvasMock);
        verify(mBarChartRendererNotAllowed, times(1)).isDrawingValuesAllowed(mChartMock);
        verify(mChartMock, times(0)).getBarData();
    }

    @Test
    public void testDrawValuesNoData() {
        /* Tests whether the correct branches of the code are run when no data is supplied,
         * it does this by checking if the correct functions are called (the correct number of times
         * without calling and functions in branches that are not covered by the test.*/
        when(mBarDataMock.getDataSetCount()).thenReturn(0);
        mBarChartRendererAllowed.drawValues(mCanvasMock);

        verify(mBarChartRendererAllowed, times(1)).drawValues(mCanvasMock);
        verify(mBarDataMock, times(1)).getDataSetCount();
        verify(mBarChartRendererAllowed, times(0)).shouldDrawValues(mDataSetMock);
    }

    @Test
    public void testDrawValuesForLoopIterations() {
        /* Tests whether the for loop iterates the correct number of times for two data points,
         * it does this by checking if the correct functions are called (the correct number of times
         * without calling and functions in branches that are not covered by the test.*/
        when(mBarDataMock.getDataSetCount()).thenReturn(2);
        mBarChartRendererAllowed.drawValues(mCanvasMock);

        verify(mBarChartRendererAllowed, times(1)).drawValues(mCanvasMock);
        // The following is called once per loop, meaning it should be called two times if there are two datapoints.
        verify(mBarChartRendererAllowed, times(2)).shouldDrawValues(mDataSetMock);

    }

    @Test
    public void testDrawValuesValidData() {
        /* Tests whether the correct branches of the code are run when valid data is supplied,
         * it does this by checking if the correct functions are called (the correct number of times
         * without calling and functions in branches that are not covered by the test.*/
        mRenderer.drawValues(mCanvasMock);
        verify(mRenderer, times(1)).drawValues(mCanvasMock);
        verify(mRenderer, times(4)).shouldDrawValues(dataSetMock);
        verify(mAnimatorMock, times(4)).getPhaseY();
        verify(mAnimatorMock, times(4)).getPhaseX();

        /* the scope of this test is to not cover the inner for loop, therefore the mocks are
        * initialized is such a way that the inner for loop shouldn't execute, therefore the
        * method below should be called a total of 0 times.*/
        verify(mViewPortHandlerMock, times(0)).isInBoundsRight(anyFloat());
    }
}

/*
Below are mock classes meant to override certain methods that are protected and can therefore not be
mocked using Mockito.
*/

// A mock class where drawing values is not allowed.
class TestBarChartRendererNotAllowed extends BarChartRenderer {
    public TestBarChartRendererNotAllowed(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return false; // Mock behavior
    }
}

// A mock class where drawing values is allowed.
class TestBarChartRendererAllowed extends BarChartRenderer {
    public TestBarChartRendererAllowed(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return true; // Mock behavior
    }
    @Override
    public boolean shouldDrawValues(IDataSet set){
        return false; // Mock behavior, if this is false we don't have to construct an actual data object.
    }
}

/* A mock class used for when valid data is supplied, with certain methods overriden in order
   to mock some protected method behaviors.*/
class TestBarChartRendererValidData extends BarChartRenderer {
    public TestBarChartRendererValidData(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return true; // Mock behavior
    }
    @Override
    public boolean shouldDrawValues(IDataSet set){
        return true; // Mock behavior
    }
    @Override
    public void applyValueTextStyle(IDataSet set){
        return;
    }
}

