
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class LegendRenderer extends Renderer {

    /** paint for the legend labels */
    protected Paint mLegendLabelPaint;

    /** paint used for the legend forms */
    protected Paint mLegendFormPaint;

    public LegendRenderer(ViewPortHandler viewPortHandler) {
        super(viewPortHandler);

        mLegendLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9f));

        mLegendFormPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendFormPaint.setStyle(Paint.Style.FILL);
        mLegendFormPaint.setStrokeWidth(3f);
    }

    /**
     * Returns the Paint object used for drawing the Legend labels.
     * 
     * @return
     */
    public Paint getLabelPaint() {
        return mLegendLabelPaint;
    }

    /**
     * Returns the Paint object used for drawing the Legend forms.
     * 
     * @return
     */
    public Paint getFormPaint() {
        return mLegendFormPaint;
    }

    /**
     * Prepares the legend and calculates all needed forms and colors.
     * 
     * @param data
     */
    public Legend computeLegend(ChartData<?> data, Legend legend) {

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // loop for building up the colors and labels used in the legend
        for (int i = 0; i < data.getDataSetCount(); i++) {

            DataSet<? extends Entry> dataSet = data.getDataSetByIndex(i);

            ArrayList<Integer> clrs = dataSet.getColors();
            int entryCount = dataSet.getEntryCount();

            // if we have a barchart with stacked bars
            if (dataSet instanceof BarDataSet && ((BarDataSet) dataSet).getStackSize() > 1) {

                BarDataSet bds = (BarDataSet) dataSet;
                String[] sLabels = bds.getStackLabels();

                for (int j = 0; j < clrs.size() && j < bds.getStackSize(); j++) {

                    labels.add(sLabels[j % sLabels.length]);
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-2);
                labels.add(bds.getLabel());

            } else if (dataSet instanceof PieDataSet) {

                ArrayList<String> xVals = data.getXVals();
                PieDataSet pds = (PieDataSet) dataSet;

                for (int j = 0; j < clrs.size() && j < entryCount && j < xVals.size(); j++) {

                    labels.add(xVals.get(j));
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-2);
                labels.add(pds.getLabel());

            } else { // all others

                for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                    // if multiple colors are set for a DataSet, group them
                    if (j < clrs.size() - 1 && j < entryCount - 1) {

                        labels.add(null);
                    } else { // add label to the last entry

                        String label = data.getDataSetByIndex(i).getLabel();
                        labels.add(label);
                    }

                    colors.add(clrs.get(j));
                }
            }
        }

        Legend l = new Legend(colors, labels);

        if (legend != null) {
            // apply the old legend settings to a potential new legend
            l.apply(legend);
        }

        return l;
    }

    public void renderLegend(Canvas c, Legend legend) {

        if (legend == null || !legend.isEnabled())
            return;

        String[] labels = legend.getLegendLabels();
        Typeface tf = legend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(legend.getTextSize());
        mLegendLabelPaint.setColor(legend.getTextColor());

        float formSize = legend.getFormSize();

        // space between text and shape/form of entry
        float formTextSpaceAndForm = legend.getFormToTextSpace() + formSize;

        // space between the entries
        float stackSpace = legend.getStackSpace();

        float textSize = legend.getTextSize();

        // the amount of pixels the text needs to be set down to be on the same
        // height as the form
        float textDrop = (Utils.calcTextHeight(mLegendLabelPaint, "AQJ") + formSize) / 2f;

        float posX, posY;

        // contains the stacked legend size in pixels
        float stack = 0f;

        boolean wasStacked = false;

        switch (legend.getPosition()) {
            case BELOW_CHART_LEFT:

                posX = legend.getOffsetLeft();
                posY = mViewPortHandler.getChartHeight() - legend.getOffsetBottom() / 2f
                        - formSize / 2f;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX, posY, i, legend);

                    // grouped forms have null labels
                    if (labels[i] != null) {

                        // make a step to the left
                        if (legend.getColors()[i] != -2)
                            posX += formTextSpaceAndForm;

                        drawLabel(c, posX, posY + textDrop, legend.getLabel(i));
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + legend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
                    }
                }

                break;
            case BELOW_CHART_RIGHT:

                posX = mViewPortHandler.contentRight();
                posY = mViewPortHandler.getChartHeight() - legend.getOffsetBottom() / 2f
                        - formSize / 2f;

                for (int i = labels.length - 1; i >= 0; i--) {

                    if (labels[i] != null) {

                        posX -= Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + legend.getXEntrySpace();
                        drawLabel(c, posX, posY + textDrop, legend.getLabel(i));
                        if (legend.getColors()[i] != -2)
                            posX -= formTextSpaceAndForm;
                    } else {
                        posX -= stackSpace + formSize;
                    }

                    drawForm(c, posX, posY, i, legend);
                }

                break;
            case RIGHT_OF_CHART:

                posX = mViewPortHandler.getChartWidth()
                        - legend.getMaximumEntryLength(mLegendLabelPaint)
                        - formTextSpaceAndForm;
                posY = legend.getOffsetTop();

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, legend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (legend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            posY += textDrop;

                            drawLabel(c, x, posY, legend.getLabel(i));
                        } else {

                            posY += textSize * 1.2f + formSize;

                            drawLabel(c, posX, posY, legend.getLabel(i));
                        }

                        // make a step down
                        posY += legend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }
                break;
            case RIGHT_OF_CHART_CENTER:
                posX = mViewPortHandler.getChartWidth()
                        - legend.getMaximumEntryLength(mLegendLabelPaint)
                        - formTextSpaceAndForm;
                posY = mViewPortHandler.getChartHeight() / 2f
                        - legend.getFullHeight(mLegendLabelPaint) / 2f;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, legend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (legend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            posY += textDrop;

                            drawLabel(c, x, posY, legend.getLabel(i));
                        } else {

                            posY += textSize * 1.2f + formSize;

                            drawLabel(c, posX, posY, legend.getLabel(i));
                        }

                        // make a step down
                        posY += legend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;
            case BELOW_CHART_CENTER:

                float fullSize = legend.getFullWidth(mLegendLabelPaint);

                posX = mViewPortHandler.getChartWidth() / 2f - fullSize / 2f;
                posY = mViewPortHandler.getChartHeight() - legend.getOffsetBottom() / 2f
                        - formSize / 2f;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX, posY, i, legend);

                    // grouped forms have null labels
                    if (labels[i] != null) {

                        // make a step to the left
                        if (legend.getColors()[i] != -2)
                            posX += formTextSpaceAndForm;

                        drawLabel(c, posX, posY + textDrop, legend.getLabel(i));
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + legend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
                    }
                }

                break;
            case PIECHART_CENTER:

                posX = mViewPortHandler.getChartWidth()
                        / 2f
                        - (legend.getMaximumEntryLength(mLegendLabelPaint) + legend
                                .getXEntrySpace())
                        / 2f;
                posY = mViewPortHandler.getChartHeight() / 2f
                        - legend.getFullHeight(mLegendLabelPaint) / 2f;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, legend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (legend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            posY += textDrop;

                            drawLabel(c, x, posY, legend.getLabel(i));
                        } else {

                            posY += textSize * 1.2f + formSize;

                            drawLabel(c, posX, posY, legend.getLabel(i));

                        }

                        // make a step down
                        posY += legend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;
            case RIGHT_OF_CHART_INSIDE:

                posX = mViewPortHandler.getChartWidth()
                        - legend.getMaximumEntryLength(mLegendLabelPaint)
                        - formTextSpaceAndForm;
                posY = legend.getOffsetTop();

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, legend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (legend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            posY += textDrop;

                            drawLabel(c, x, posY, legend.getLabel(i));
                        } else {

                            posY += textSize * 1.2f + formSize;

                            drawLabel(c, posX, posY, legend.getLabel(i));

                        }

                        // make a step down
                        posY += legend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }
                break;
        }
    }

    /**
     * Draws the Legend-form at the given position with the color at the given
     * index.
     * 
     * @param c canvas to draw with
     * @param x
     * @param y
     * @param index the index of the color to use (in the colors array)
     */
    protected void drawForm(Canvas c, float x, float y, int index, Legend legend) {

        if (legend.getColors()[index] == -2)
            return;

        mLegendFormPaint.setColor(legend.getColors()[index]);

        float formsize = legend.getFormSize();
        float half = formsize / 2f;

        switch (legend.getForm()) {
            case CIRCLE:
                c.drawCircle(x + half, y + half, half, mLegendFormPaint);
                break;
            case SQUARE:
                c.drawRect(x, y, x + formsize, y + formsize, mLegendFormPaint);
                break;
            case LINE:
                c.drawLine(x, y + half, x + formsize, y + half, mLegendFormPaint);
                break;
        }
    }

    /**
     * Draws the provided label at the given position.
     * 
     * @param c canvas to draw with
     * @param x
     * @param y
     * @param label the label to draw
     */
    protected void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, mLegendLabelPaint);
    }
}
