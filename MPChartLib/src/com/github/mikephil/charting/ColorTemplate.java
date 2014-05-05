
package com.github.mikephil.charting;

import android.content.Context;

import java.util.ArrayList;

public class ColorTemplate {

    /**
     * THE COLOR THEMES BELOW ARE PREDEFINED, FEEL FREE TO CREATE YOUR OWN WITH
     * AS MANY DIFFERENT COLORS AS YOU WANT
     */

    public static final int[] FRESH_COLORS = {
            R.color.fresh_1, R.color.fresh_2, R.color.fresh_3, R.color.fresh_4, R.color.fresh_5
    };
    public static final int[] MONO_COLORS = {
            R.color.mono_1, R.color.mono_2, R.color.mono_3, R.color.mono_4, R.color.mono_5
    };
    public static final int[] LIBERTY_COLORS = {
            R.color.liberty_1, R.color.liberty_2, R.color.liberty_3, R.color.liberty_4,
            R.color.liberty_5
    };
    public static final int[] COLORFUL_COLORS = {
            R.color.colorful_1, R.color.colorful_2, R.color.colorful_3, R.color.colorful_4,
            R.color.colorful_5
    };
    public static final int[] GREEN_COLORS = {
            R.color.greens_1, R.color.greens_2, R.color.greens_3, R.color.greens_4,
            R.color.greens_5
    };
    public static final int[] JOYFUL_COLORS = {
            R.color.joyful_1, R.color.joyful_2, R.color.joyful_3, R.color.joyful_4,
            R.color.joyful_5
    };

    /** the array of colors the template represents */
    private ArrayList<Integer> mColors;

    /**
     * constructor
     * @param colors
     */
    public ColorTemplate(ArrayList<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * constructor
     * @param colors
     * @param c
     */
    public ColorTemplate(int[] colors, Context c) {
        this.mColors = getColors(c, colors);
    }

    /**
     * returns an array of colors this template represents
     * 
     * @return
     */
    public ArrayList<Integer> getColors() {
        return mColors;
    }

    /**
     * turn an array of resource-colors into an arraylist of actual color values
     * 
     * @param c
     * @param colors e.g. ColorTemplate.MONO_COLORS
     * @return
     */
    public static ArrayList<Integer> getColors(Context c, int[] colors) {

        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i : colors) {
            result.add(c.getResources().getColor(i));
        }

        return result;
    }
}
