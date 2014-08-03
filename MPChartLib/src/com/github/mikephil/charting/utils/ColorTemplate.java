
package com.github.mikephil.charting.utils;

import android.content.Context;

import com.github.mikephil.charting.R;

import java.util.ArrayList;

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

    /** an arraylist of color arrays (one color array per dataset) */
    private ArrayList<ArrayList<Integer>> mDataSetColors;

    /** the total amount of different colors in the template */
    private int mColorCount = 0;

    /**
     * constructor
     */
    public ColorTemplate() {
        mDataSetColors = new ArrayList<ArrayList<Integer>>();
    }

    /**
     * returns the total amount of different colors in the template
     * 
     * @return
     */
    public int getColorCount() {
        return mColorCount;
    }

    /**
     * Adds a new array of colors for one DataSet to the template. Make sure to
     * use getResources().getColor(R.color.yourcolor) for the colors. Use
     * ColorTemplate.createColors(...) to make a color arraylist.
     * 
     * @param colors
     */
    public void addDataSetColors(ArrayList<Integer> colors) {
        mDataSetColors.add(colors);
        mColorCount += colors.size();
    }

    /**
     * Adds a new array of colors for one DataSet to the template. You can use
     * R.color.yourcolor for the integer values. Conversion is done internally.
     * 
     * @param colors
     * @param c
     */
    public void addDataSetColors(int[] colors, Context c) {
        mDataSetColors.add(createColors(c, colors));
        mColorCount += colors.length;
    }

    /**
     * Adds colors to the ColorTemplate. Each of the colors will create a new
     * DataSet color array in the template with just one color. This is
     * especially useful when you want each of your DataSets only to be
     * represented by one color and not multiple.
     * 
     * @param colors
     * @param c
     */
    public void addColorsForDataSets(ArrayList<Integer> colors) {
        for (int i = 0; i < colors.size(); i++) {

            ArrayList<Integer> clrs = new ArrayList<Integer>();
            clrs.add(colors.get(i));
            addDataSetColors(clrs);
        }
    }

    /**
     * Adds colors to the ColorTemplate. Each of the colors will create a new
     * dataset color array in the template with just one color. This is
     * especially useful when you want each of your DataSets only to be
     * represented by one color and not multiple.
     * 
     * @param colors
     * @param c
     */
    public void addColorsForDataSets(int[] colors, Context c) {
        for (int i = 0; i < colors.length; i++) {
            addDataSetColors(new int[] {
                    colors[i]
            }, c);
        }
    }

    /**
     * Adds a single color for one DataSet. Each call of this method will add
     * one more color for one DataSet to the template.
     * 
     * @param color
     * @param c
     */
    public void addDataSetColor(int color, Context c) {
        addDataSetColors(new int[] {
                color
        }, c);
    }

    /**
     * Returns all color arrays the template represents.
     * 
     * @return
     */
    public ArrayList<ArrayList<Integer>> getColors() {
        return mDataSetColors;
    }

    /**
     * Returns all colors the template represents as an int array
     * 
     * @return
     */
    public int[] getColorsAsArray() {

        int[] colors = new int[mColorCount];
        int cnt = 0;

        for (int i = 0; i < mDataSetColors.size(); i++) {
            for (int j = 0; j < mDataSetColors.get(i).size(); j++) {
                colors[cnt] = mDataSetColors.get(i).get(j);
                cnt++;
            }
        }

        return colors;
    }

    /**
     * Returns all colors the template represents as an ArrayList<Integer>
     * 
     * @return
     */
    public ArrayList<Integer> getColorsAsArrayList() {

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int i = 0; i < mDataSetColors.size(); i++) {
            for (int j = 0; j < mDataSetColors.get(i).size(); j++) {
                colors.add(mDataSetColors.get(i).get(j));
            }
        }

        return colors;
    }

    /**
     * returns the dataset color array at the given index
     * 
     * @param dataSetIndex
     * @return
     */
    public ArrayList<Integer> getDataSetColors(int dataSetIndex) {
        return mDataSetColors.get(dataSetIndex);
    }

    /**
     * Returns the color value at the given index from the DataSet at the given
     * index. If index is outofbounds, it restart at lower indices.
     * 
     * @param dataSetIndex
     * @param colorIndex
     * @return
     */
    public int getDataSetColor(int dataSetIndex, int colorIndex) {
        return mDataSetColors.get(dataSetIndex).get(
                colorIndex % mDataSetColors.get(dataSetIndex).size());
    }

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
