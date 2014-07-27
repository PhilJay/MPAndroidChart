package com.example.mpchartexample.simple;

import android.support.v4.app.Fragment;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.FileUtils;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {

    /**
     * generates some random data
     * @return
     */
    protected ChartData generateData(int dataSets, float range, int count) {
        
        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        
        for(int i = 0; i < dataSets; i++) {
           
            ArrayList<Entry> entries = new ArrayList<Entry>();
            
            for(int j = 0; j < count; j++) {        
                entries.add(new Entry((float) (Math.random() * range) + range / 4, j));
            }
            
            DataSet ds = new DataSet(entries, getLabel(i));
            sets.add(ds);
        }
        
        ChartData d = new ChartData(ChartData.generateXVals(0, count), sets);
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
        
        DataSet ds1 = new DataSet(entries1, "Quarterly Revenues 2014");
        
        ChartData d = new ChartData(xVals, ds1);
        return d;
    }
    
    protected ChartData getComplexity() {
        
//        DataSet ds1 = new DataSet(n, "O(n)");  
//        DataSet ds2 = new DataSet(nlogn, "O(nlogn)"); 
//        DataSet ds3 = new DataSet(nsquare, "O(n\u00B2)");
//        DataSet ds4 = new DataSet(nthree, "O(n\u00B3)");
        
        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        
        // load DataSets from textfiles in assets folder
        sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "sine.txt"));
        sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "cosine.txt"));
//        sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "n.txt"));
//        sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "nlogn.txt"));
//        sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "square.txt"));
//        sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "three.txt"));
        
        ChartData d = new ChartData(ChartData.generateXVals(0, 752),  sets);
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
