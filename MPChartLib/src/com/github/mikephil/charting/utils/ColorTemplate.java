
package com.github.mikephil.charting.utils;

import android.content.res.Resources;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * Class that holds predefined color integer arrays (e.g.
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
    public static final int[] LIBERTY_COLORS = {
            Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
            Color.rgb(118, 174, 175), Color.rgb(42, 109, 130)
    };
    public static final int[] JOYFUL_COLORS = {
            Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
            Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)
    };
    public static final int[] PASTEL_COLORS = {
            Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)
    };
    public static final int[] COLORFUL_COLORS = {
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
    };
    public static final int[] VORDIPLOM_COLORS = {
            Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
    };

    // public static final int[] FRESH_COLORS = {
    // R.color.fresh_1, R.color.fresh_2, R.color.fresh_3, R.color.fresh_4,
    // R.color.fresh_5
    // };
    // public static final int[] MONO_COLORS = {
    // R.color.mono_1, R.color.mono_2, R.color.mono_3, R.color.mono_4,
    // R.color.mono_5
    // };
    // public static final int[] GREEN_COLORS = {
    // R.color.greens_1, R.color.greens_2, R.color.greens_3, R.color.greens_4,
    // R.color.greens_5
    // };

    /**
     * Returns the Android ICS holo blue light color.
     * 
     * @return
     */
    public static int getHoloBlue() {
        return Color.rgb(51, 181, 229);
    }

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
