package com.example.mpchartexample.simple;

import android.support.v4.app.Fragment;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {

    /**
     * generates some random data
     * @return
     */
    protected ChartData generateData(int dataSets, float range) {
        
        int count = 12;
        
        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        
        for(int i = 0; i < dataSets; i++) {
           
            ArrayList<Entry> entries = new ArrayList<Entry>();
            
            for(int j = 0; j < count; j++) {        
                entries.add(new Entry((float) (Math.random() * range), j));
            }
            
            DataSet ds = new DataSet(entries, getLabel(i));
            sets.add(ds);
        }
        
        ChartData d = new ChartData(getXVals(), sets);
        return d;
    }
    
    /**
     * generates less data (1 DataSet, 4 values)
     * @return
     */
    protected ChartData generateLessData() {
        
        int count = 4;
        
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        
        xVals.add("Quarter 1");
        xVals.add("Quarter 2");
        xVals.add("Quarter 3");
        xVals.add("Quarter 4");
        
        for(int i = 0; i < count; i++) {
            xVals.add("entry" + (i+1));
    
            entries1.add(new Entry((float) (Math.random() * 100), i));
        }
        
        DataSet ds1 = new DataSet(entries1, "Quarterly revenues");
        
        ChartData d = new ChartData(xVals, ds1);
        return d;
    }
    
    private String[] mLabels = new String[] { "Company A", "Company B", "Company C", "Company D", "Company E", "Company F" };
    private String[] mXVals = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };
    
    private String getLabel(int i) {
        return mLabels[i];
    }
    
    private String[] getXVals() {
        return mXVals;
    }
}
