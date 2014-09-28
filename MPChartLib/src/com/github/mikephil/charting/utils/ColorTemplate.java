
package com.github.mikephil.charting.utils;

import android.content.res.Resources;
import android.graphics.Color;

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
     * THE COLOR THEMES ARE PREDEFINED (predefined color integer arrays), FEEL
     * FREE TO CREATE YOUR OWN WITH AS MANY DIFFERENT COLORS AS YOU WANT
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
            Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
    };

    /**
     * turn an array of resource-colors (contains resource-id integers) into an
     * array list of actual color integers
     * 
     * @param r
     * @param colors an integer array of resource id's of colors
     * @return
     */
    public static ArrayList<Integer> createColors(Resources r, int[] colors) {

        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i : colors) {
            result.add(r.getColor(i));
        }

        return result;
    }

    /**
     * Turns an array of colors (integer color values) into an ArrayList of
     * colors.
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
