
package com.github.mikephil.charting.utils;

import android.content.Context;

import com.github.mikephil.charting.R;

import java.util.ArrayList;

/**
 * Class that holds predefined color arrays (e.g.
 * ColorTemplate.VORDIPLOM_COLORS) and convenience methods for loading colors
 * from resources.
 * 
 * @author Philipp Jahoda
 */
public class ColorTemplate {

    /**
     * THE COLOR THEMES ARE PREDEFINED, FEEL FREE TO CREATE YOUR OWN WITH AS
     * MANY DIFFERENT COLORS AS YOU WANT
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
    public static final int[] PASTEL_COLORS = {
            R.color.pastel_1, R.color.pastel_2, R.color.pastel_3, R.color.pastel_4,
            R.color.pastel_5
    };
    public static final int[] VORDIPLOM_COLORS = {
            R.color.vordiplom_1, R.color.vordiplom_2, R.color.vordiplom_3, R.color.vordiplom_4,
            R.color.vordiplom_5
    };

    /**
     * turn an array of resource-colors into an arraylist of actual color values
     * 
     * @param c
     * @param colors e.g. ColorTemplate.MONO_COLORS
     * @return
     */
    public static ArrayList<Integer> createColors(Context c, int[] colors) {

        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i : colors) {
            result.add(c.getResources().getColor(i));
        }

        return result;
    }

    /**
     * tuns an array of colors (already converted with
     * getResources().getColor(...) int an arraylist of colors
     * 
     * @param colors
     * @return
     */
    public static ArrayList<Integer> createColors(int[] colors) {

        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i : colors) {
            result.add(i);
        }

        return result;
    }
}
