import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

public class BarLineChartTouchListenerTest {

    @Mock
    private BarLineChartBase<BarLineScatterCandleBubbleData<IBarLineScatterCandleBubbleDataSet<?>>> mockChart;

    @Mock
    private ViewPortHandler mockViewPortHandler;

    @Mock
    private VelocityTracker mockVelocityTracker;

    private BarLineChartTouchListener touchListener;
    private Matrix mockMatrix;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMatrix = new Matrix();

        when(mockChart.getViewPortHandler()).thenReturn(mockViewPortHandler);
        touchListener = new BarLineChartTouchListener(mockChart, mockMatrix, 3f);

        // Mock VelocityTracker and set it using reflection
        mockVelocityTracker = mock(VelocityTracker.class);
        setPrivateField(touchListener, "mVelocityTracker", mockVelocityTracker);
        setPrivateField(touchListener,"mMatrix", mockMatrix);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Mocks a MotionEvent with specified action and coordinates.
     */
    private MotionEvent mockMotionEvent(int action, float x, float y, int pointerCount) {
        MotionEvent event = mock(MotionEvent.class);
        when(event.getAction()).thenReturn(action);
        when(event.getX()).thenReturn(x);
        when(event.getY()).thenReturn(y);
        when(event.getPointerCount()).thenReturn(pointerCount);
        return event;
    }

    /**
     *Test 1: Fling Gesture Should Trigger Velocity Tracking**
     */
    @Test
    public void testOnTouch_Fling_TriggersVelocityTracking() {
        MotionEvent mockDownEvent = mockMotionEvent(MotionEvent.ACTION_DOWN, 100f, 100f, 1);
        MotionEvent mockMoveEvent = mockMotionEvent(MotionEvent.ACTION_MOVE, 200f, 200f, 1);
        MotionEvent mockUpEvent = mockMotionEvent(MotionEvent.ACTION_UP, 300f, 300f, 1);

        when(mockChart.isDragEnabled()).thenReturn(true);
        when(mockChart.isDragDecelerationEnabled()).thenReturn(true);
        when(mockVelocityTracker.getXVelocity(anyInt())).thenReturn(500f);
        when(mockVelocityTracker.getYVelocity(anyInt())).thenReturn(500f);

        touchListener.onTouch(null, mockDownEvent);
        touchListener.onTouch(null, mockMoveEvent);
        boolean result = touchListener.onTouch(null, mockUpEvent);

        assertTrue(result);

        // Verify velocity tracking was computed
        verify(mockVelocityTracker).computeCurrentVelocity(eq(1000), anyFloat());

        // Ensure deceleration is stopped to start a new one
        verify(mockVelocityTracker, atLeastOnce()).getXVelocity(anyInt());
        verify(mockVelocityTracker, atLeastOnce()).getYVelocity(anyInt());
    }

    /**
     *Test 2: Pointer Up During Zoom Should Set Post-Zoom Mode**
     */
    @Test
    public void testOnTouch_PointerUp_SetsPostZoomMode() {
        MotionEvent mockPointerDown = mockMotionEvent(MotionEvent.ACTION_POINTER_DOWN, 100f, 100f, 1);
        MotionEvent mockPointerUp = mockMotionEvent(MotionEvent.ACTION_POINTER_UP, 100f, 100f, 1);

        when(mockChart.isScaleXEnabled()).thenReturn(true);
        when(mockChart.isScaleYEnabled()).thenReturn(true);
        when(mockChart.isPinchZoomEnabled()).thenReturn(true);

        touchListener.onTouch(null, mockPointerDown);
        boolean result = touchListener.onTouch(null, mockPointerUp);

        assertTrue(result);

        // Ensure velocity tracker cleanup is called
        verify(mockChart, atLeastOnce()).getViewPortHandler();
        verify(mockVelocityTracker, atLeastOnce()).computeCurrentVelocity(eq(1000), anyFloat());
    }

    /**
     * Test 3: Drag Should Disable Scrolling**
     */
    @Test
    public void testOnTouch_Drag_UpdatesMatrix() {
        MotionEvent mockDownEvent = mockMotionEvent(MotionEvent.ACTION_DOWN, 50f, 50f,1);
        MotionEvent mockMoveEvent = mockMotionEvent(MotionEvent.ACTION_MOVE, 70f, 70f,1);

        when(mockChart.isDragEnabled()).thenReturn(true);
        when(mockChart.isDragXEnabled()).thenReturn(true);
        when(mockChart.isDragYEnabled()).thenReturn(true);
        when(mockChart.getViewPortHandler().refresh(mockMatrix, mockChart, true)).thenReturn(mockMatrix);

        touchListener.onTouch(null, mockDownEvent);
        when(mockMoveEvent.getX()).thenReturn(1000f);
        when(mockMoveEvent.getY()).thenReturn(1000f);
        touchListener.onTouch(null, mockMoveEvent);
        boolean result = touchListener.onTouch(null, mockMoveEvent);

        assertTrue(result);
        verify(mockChart).disableScroll();
    }

    /**
     * Test 4: Pinch Zoom Should Trigger Scaling**
     */
    @Test
    public void testOnTouch_PinchZoom_TriggersScale() {
        MotionEvent mockPointerDown = mockMotionEvent(MotionEvent.ACTION_POINTER_DOWN, 50f, 50f,2);
        MotionEvent mockZoomEvent = mockMotionEvent(MotionEvent.ACTION_MOVE, 180f, 180f,2);

        when(mockChart.isPinchZoomEnabled()).thenReturn(true);
        when(mockChart.isScaleXEnabled()).thenReturn(true);
        when(mockChart.isScaleYEnabled()).thenReturn(true);
        when(mockZoomEvent.getX(0)).thenReturn(1000f);
        when(mockZoomEvent.getX(1)).thenReturn(0f);

        touchListener.onTouch(null, mockPointerDown);
        boolean result = touchListener.onTouch(null, mockZoomEvent);

        assertTrue(result);
        verify(mockChart, atLeastOnce()).disableScroll();
    }
}