package com.github.mikephil.charting.data.filter;

import android.util.Log;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;

import java.util.ArrayList;

public class ZoomHandler {
    
    public static final int MAX_ZOOM_LEVEL          = 7;
    public static final int MIN_ZOOM_LEVEL          = 1;
    
    private int mZoomLevelX = MIN_ZOOM_LEVEL;
    private int mZoomLevelY = MIN_ZOOM_LEVEL;
    
    private Approximator mCustomApprox;
    
    public ZoomHandler() {
        
    }
    
    /**
     * returns the filtered ChartData object filtered according to the current zoom level
     * @param original
     * @param zoomlevel
     * @return
     */
    public ChartData getFiltered(ChartData original, float scaleX, float scaleY) {
        
        mZoomLevelX = getZoomLevel(scaleX);
        mZoomLevelY = getZoomLevel(scaleY);
        
        float deltaY = original.getYMax() - original.getYMin();
        float scaleSum = scaleX + scaleY;
        
        float weightX = scaleX / scaleSum;
        float weightY = scaleY / scaleSum;
        
        float yTolerance = 0;
        float xTolerance = 0;
        float tolerance = 0;
//        float valuesOnScreen = (float) original.getYValCount() * Math.max(scaleX, scaleY);
        
        if(mZoomLevelY < MAX_ZOOM_LEVEL) yTolerance = deltaY - deltaY * weightY;
        
        if(mZoomLevelX < MAX_ZOOM_LEVEL) xTolerance = original.getXValCount() - original.getXValCount() * weightX;
        
        tolerance = (yTolerance * weightY + xTolerance * weightX) / 2f;
        
        
        Log.i("ZoomHandler", "Tolerance: " + tolerance + ", Zoomlevel: " + mZoomLevelX);
        
        // if there is no tolerance, or less than 50 vals
        if(tolerance == 0) {
            return original;
        }
        
        Approximator approx;
        
        if(usesCustomApprox()) approx = mCustomApprox;
        else {
            
            approx = new Approximator();
            approx.setTypeAndTolerance(ApproximatorType.DOUGLAS_PEUCKER, tolerance);
        }
               
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        
        for(int j = 0; j < original.getDataSetCount(); j++) {
            
            DataSet old = original.getDataSetByIndex(j);
            
            ArrayList<Entry> approximated = approx.filter(old.getYVals());
            
            DataSet set = new DataSet(approximated, old.getType());
            dataSets.add(set);
        }
        
        ChartData d = new ChartData(original.getXVals(), dataSets);
        return d;
    }

    /**
     * returns the zoom level for the given scale factor (scale X or scale Y)
     * @param scaleFactor
     * @return
     */
    public int getZoomLevel(float scaleFactor) {
        int lvl = (int) scaleFactor;
        
        if(lvl > MAX_ZOOM_LEVEL) return MAX_ZOOM_LEVEL;
        if(lvl < MIN_ZOOM_LEVEL) return MIN_ZOOM_LEVEL;
        
        return lvl;
    }
    
    /**
     * sets a custom approximator for the zoomhandler, set this to null to use the default filtering
     * @param a
     */
    public void setCustomApproximator(Approximator a) {
        this.mCustomApprox = a;
    }
    
    /** 
     * returns true if a custom approximator is used, false if not
     * @return
     */
    public boolean usesCustomApprox() {
        return mCustomApprox == null ? false : true;
    }
    
    /**
     * returns the current zoom level on the x axis
     * @return
     */
    public int getCurrentZoomLevelX() {
        return mZoomLevelX;
    }
    
    /**
     * returns the current zoom level on the y axis
     * @return
     */
    public int getCurrentZoomLevelY() {
        return mZoomLevelY;
    }
}
