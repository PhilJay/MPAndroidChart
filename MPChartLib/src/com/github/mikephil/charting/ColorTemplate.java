package com.github.mikephil.charting;

import android.content.Context;

import java.util.ArrayList;

public class ColorTemplate {
    
    public static final int[] FRESH_COLORS = { R.color.fresh_1, R.color.fresh_2, R.color.fresh_3, R.color.fresh_4, R.color.fresh_5 };
    public static final int[] MONO_COLORS = { R.color.mono_1, R.color.mono_2, R.color.mono_3, R.color.mono_4, R.color.mono_5 };    
    public static final int[] LIBERTY_COLORS = { R.color.liberty_1, R.color.liberty_2, R.color.liberty_3, R.color.liberty_4, R.color.liberty_5 };
    
    private ArrayList<Integer> mColors;

    public ColorTemplate(ArrayList<Integer> mColors) {
        this.mColors = mColors;
    }
    
    public ArrayList<Integer> getColors() {
        return mColors;
    }
    
    public static ArrayList<Integer> getColors(Context c, int[] colors) {
        
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        for(int i : colors) {
            result.add(c.getResources().getColor(i));
        }
        
        return result;
    }

}
