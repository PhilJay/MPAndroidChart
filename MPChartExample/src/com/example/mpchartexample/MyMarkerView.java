package com.example.mpchartexample;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.utils.MarkerView;

public class MyMarkerView extends MarkerView {
        
    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(int xIndex, float value, int dataSetIndex) {
        tvContent.setText("Idx: " + xIndex);
    }  
}
