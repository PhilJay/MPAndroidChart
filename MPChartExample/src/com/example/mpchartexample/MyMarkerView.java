
package com.example.mpchartexample;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.Utils;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content
    @Override
    public void refreshContent(int xIndex, float value, int dataSetIndex) {
        tvContent.setText(" " + Utils.formatNumber(value, 0, true));
    }
}
