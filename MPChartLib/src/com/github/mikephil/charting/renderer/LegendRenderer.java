
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class LegendRenderer extends Renderer {

    /** paint for the legend labels */
    protected Paint mLegendLabelPaint;

    /** paint used for the legend forms */
    protected Paint mLegendFormPaint;

    /** the legend object this renderer renders */
    protected Legend mLegend;

    public LegendRenderer(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler);

        this.mLegend = legend;

        mLegendLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9f));
        mLegendLabelPaint.setTextAlign(Align.LEFT);

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
     * Prepares the legend and calculates all needed forms, labels and colors.
     * 
     * @param data
     */
    public void computeLegend(ChartData<?> data) {

        List<String> labels = new ArrayList<String>();
        List<Integer> colors = new ArrayList<Integer>();

        // loop for building up the colors and labels used in the legend
        for (int i = 0; i < data.getDataSetCount(); i++) {

            DataSet<? extends Entry> dataSet = data.getDataSetByIndex(i);

            List<Integer> clrs = dataSet.getColors();
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

                List<String> xVals = data.getXVals();
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

        mLegend.setColors(colors);
        mLegend.setLabels(labels);

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        // calculate all dimensions of the mLegend
        mLegend.calculateDimensions(mLegendLabelPaint);
    }

    public void renderLegend(Canvas c) {

        if (!mLegend.isEnabled())
            return;

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        String[] labels = mLegend.getLegendLabels();

        float formSize = mLegend.getFormSize();

        // space between text and shape/form of entry
        float formTextSpaceAndForm = mLegend.getFormToTextSpace() + formSize;

        // space between the entries
        float stackSpace = mLegend.getStackSpace();

        // the amount of pixels the text needs to be set down to be on the same
        // height as the form
        float textDrop = (Utils.calcTextHeight(mLegendLabelPaint, "AQJ") + formSize) / 2f;

        float posX, posY;

        // contains the stacked mLegend size in pixels
        float stack = 0f;

        boolean wasStacked = false;

        float yoffset = mLegend.getYOffset();
        float xoffset = mLegend.getXOffset();

        switch (mLegend.getPosition()) {
            case BELOW_CHART_LEFT:

                posX = mViewPortHandler.contentLeft() + xoffset;
                posY = mViewPortHandler.getChartHeight() - yoffset;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX, posY - mLegend.mTextHeightMax / 2f, i, mLegend);

                    // grouped forms have null labels
                    if (labels[i] != null) {

                        // make a step to the left
                        if (mLegend.getColors()[i] != -2)
                            posX += formTextSpaceAndForm;

                        drawLabel(c, posX, posY, mLegend.getLabel(i));
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
                    }
                }

                break;
            case BELOW_CHART_RIGHT:

                posX = mViewPortHandler.contentRight() - xoffset;
                ;
                posY = mViewPortHandler.getChartHeight() - yoffset;

                for (int i = labels.length - 1; i >= 0; i--) {

                    if (labels[i] != null) {

                        posX -= Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                        drawLabel(c, posX, posY, mLegend.getLabel(i));
                        if (mLegend.getColors()[i] != -2)
                            posX -= formTextSpaceAndForm;
                    } else {
                        posX -= stackSpace + formSize;
                    }

                    drawForm(c, posX, posY - mLegend.mTextHeightMax / 2f, i, mLegend);
                }

                break;
            case RIGHT_OF_CHART:

                posX = mViewPortHandler.getChartWidth() - mLegend.mTextWidthMax - xoffset;
                posY = mViewPortHandler.contentTop() + yoffset;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, mLegend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            drawLabel(c, x, posY + mLegend.mTextHeightMax / 2f, mLegend.getLabel(i));

                            posY += textDrop;
                        } else {
                            posY += mLegend.mTextHeightMax * 3f;
                            drawLabel(c, posX, posY - mLegend.mTextHeightMax, mLegend.getLabel(i));
                        }

                        // make a step down
                        posY += mLegend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }
                break;
            case RIGHT_OF_CHART_CENTER:
                posX = mViewPortHandler.getChartWidth() - mLegend.mTextWidthMax - xoffset;
                posY = mViewPortHandler.getChartHeight() / 2f - mLegend.mNeededHeight / 2f;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, mLegend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            drawLabel(c, x, posY + mLegend.mTextHeightMax / 2f, mLegend.getLabel(i));

                            posY += textDrop;
                        } else {
                            posY += mLegend.mTextHeightMax * 3f;
                            drawLabel(c, posX, posY - mLegend.mTextHeightMax, mLegend.getLabel(i));
                        }

                        // make a step down
                        posY += mLegend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;
            case BELOW_CHART_CENTER:

                posX = mViewPortHandler.getChartWidth() / 2f - mLegend.mNeededWidth / 2f;
                posY = mViewPortHandler.getChartHeight() - yoffset;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX, posY - mLegend.mTextHeightMax / 2f, i, mLegend);

                    // grouped forms have null labels
                    if (labels[i] != null) {

                        // make a step to the left
                        if (mLegend.getColors()[i] != -2)
                            posX += formTextSpaceAndForm;

                        drawLabel(c, posX, posY, mLegend.getLabel(i));
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
                    }
                }

                break;
            case PIECHART_CENTER:

                posX = mViewPortHandler.getChartWidth() / 2f - mLegend.mTextWidthMax / 2f;
                posY = mViewPortHandler.getChartHeight() / 2f - mLegend.mNeededHeight / 2f;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, mLegend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            drawLabel(c, x, posY + mLegend.mTextHeightMax / 2f, mLegend.getLabel(i));

                            posY += textDrop;
                        } else {
                            posY += mLegend.mTextHeightMax * 3f;
                            drawLabel(c, posX, posY - mLegend.mTextHeightMax, mLegend.getLabel(i));
                        }

                        // make a step down
                        posY += mLegend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;
            case RIGHT_OF_CHART_INSIDE:

                posX = mViewPortHandler.getChartWidth() - mLegend.mTextWidthMax - xoffset;
                posY = mViewPortHandler.contentTop() + yoffset;

                for (int i = 0; i < labels.length; i++) {

                    drawForm(c, posX + stack, posY, i, mLegend);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
                                x += formTextSpaceAndForm;

                            drawLabel(c, x, posY + mLegend.mTextHeightMax / 2f, mLegend.getLabel(i));

                            posY += textDrop;
                        } else {
                            posY += mLegend.mTextHeightMax * 3f;
                            drawLabel(c, posX, posY - mLegend.mTextHeightMax, mLegend.getLabel(i));
                        }

                        // make a step down
                        posY += mLegend.getYEntrySpace();
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
                c.drawCircle(x + half, y, half, mLegendFormPaint);
                break;
            case SQUARE:
                c.drawRect(x, y - half, x + formsize, y + half, mLegendFormPaint);
                break;
            case LINE:
                c.drawLine(x, y, x + formsize, y, mLegendFormPaint);
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
