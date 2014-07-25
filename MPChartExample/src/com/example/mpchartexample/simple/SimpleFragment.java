package com.example.mpchartexample.simple;

import android.support.v4.app.Fragment;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {

    /**
     * generates some random data, (2 DataSets, 2x20 values)
     * @return
     */
    protected ChartData generateData() {
        
        int count = 20;
        
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<Entry> entries2 = new ArrayList<Entry>();
        
        for(int i = 0; i < count; i++) {
            xVals.add("entry" + (i+1));
    
            entries1.add(new Entry((float) (Math.random() * 10000), i));
            entries2.add(new Entry((float) (Math.random() * 10000), i));
        }
        
        DataSet ds1 = new DataSet(entries1, "Company A");
        DataSet ds2 = new DataSet(entries1, "Company B");
        
        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        sets.add(ds1);
        sets.add(ds2);
        
        ChartData d = new ChartData(xVals, sets);
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
}
