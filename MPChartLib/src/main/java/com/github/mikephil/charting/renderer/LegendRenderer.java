
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegendRenderer extends Renderer {

    /**
     * paint for the legend labels
     */
    protected Paint mLegendLabelPaint;

    /**
     * paint used for the legend forms
     */
    protected Paint mLegendFormPaint;

    /**
     * the legend object this renderer renders
     */
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

    protected ArrayList<String> labelsForComputeLegend = new ArrayList<>(16);
    protected ArrayList<Integer> colorsForComputeLegend = new ArrayList<>(16);
    /**
     * Prepares the legend and calculates all needed forms, labels and colors.
     *
     * @param data
     */
    public void computeLegend(ChartData<?> data) {

        if (!mLegend.isLegendCustom()) {

            ArrayList<String> labels = labelsForComputeLegend;
            ArrayList<Integer> colors = colorsForComputeLegend;

            labels.clear();
            colors.clear();

            // loop for building up the colors and labels used in the legend
            for (int i = 0; i < data.getDataSetCount(); i++) {

                IDataSet dataSet = data.getDataSetByIndex(i);

                List<Integer> clrs = dataSet.getColors();
                int entryCount = dataSet.getEntryCount();

                // if we have a barchart with stacked bars
                if (dataSet instanceof IBarDataSet && ((IBarDataSet) dataSet).isStacked()) {

                    IBarDataSet bds = (IBarDataSet) dataSet;
                    String[] sLabels = bds.getStackLabels();

                    for (int j = 0; j < clrs.size() && j < bds.getStackSize(); j++) {

                        labels.add(sLabels[j % sLabels.length]);
                        colors.add(clrs.get(j));
                    }

                    if (bds.getLabel() != null) {
                        // add the legend description label
                        colors.add(ColorTemplate.COLOR_SKIP);
                        labels.add(bds.getLabel());
                    }

                } else if (dataSet instanceof IPieDataSet) {

                    IPieDataSet pds = (IPieDataSet) dataSet;

                    for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                        labels.add(pds.getEntryForIndex(j).getLabel());
                        colors.add(clrs.get(j));
                    }

                    if (pds.getLabel() != null) {
                        // add the legend description label
                        colors.add(ColorTemplate.COLOR_SKIP);
                        labels.add(pds.getLabel());
                    }

                } else if (dataSet instanceof ICandleDataSet && ((ICandleDataSet) dataSet).getDecreasingColor() !=
                        ColorTemplate.COLOR_NONE) {

                    int decreasingColor = ((ICandleDataSet) dataSet).getDecreasingColor();
                    colors.add(decreasingColor);

                    int increasingColor = ((ICandleDataSet) dataSet).getIncreasingColor();
                    colors.add(increasingColor);

                    labels.add(null);
                    labels.add(dataSet.getLabel());

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

            if (mLegend.getExtraColors() != null && mLegend.getExtraLabels() != null) {
                for (int color : mLegend.getExtraColors())
                    colors.add(color);
                Collections.addAll(labels, mLegend.getExtraLabels());
            }

            mLegend.setComputedColors(colors);
            mLegend.setComputedLabels(labels);
        }

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        // calculate all dimensions of the mLegend
        mLegend.calculateDimensions(mLegendLabelPaint, mViewPortHandler);
    }

    protected Paint.FontMetrics fontMetricsForRenderLegent = new Paint.FontMetrics();
    public void renderLegend(Canvas c) {

        if (!mLegend.isEnabled())
            return;

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        float labelLineHeight = Utils.getLineHeight(mLegendLabelPaint, fontMetricsForRenderLegent);
        float labelLineSpacing = Utils.getLineSpacing(mLegendLabelPaint, fontMetricsForRenderLegent) + mLegend.getYEntrySpace();
        float formYOffset = labelLineHeight - Utils.calcTextHeight(mLegendLabelPaint, "ABC") / 2.f;

        String[] labels = mLegend.getLabels();
        int[] colors = mLegend.getColors();

        float formToTextSpace = mLegend.getFormToTextSpace();
        float xEntrySpace = mLegend.getXEntrySpace();
        Legend.LegendOrientation orientation = mLegend.getOrientation();
        Legend.LegendHorizontalAlignment horizontalAlignment = mLegend.getHorizontalAlignment();
        Legend.LegendVerticalAlignment verticalAlignment = mLegend.getVerticalAlignment();
        Legend.LegendDirection direction = mLegend.getDirection();
        float formSize = mLegend.getFormSize();

        // space between the entries
        float stackSpace = mLegend.getStackSpace();

        float yoffset = mLegend.getYOffset();
        float xoffset = mLegend.getXOffset();
        float originPosX = 0.f;

        switch (horizontalAlignment) {
            case LEFT:

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = xoffset;
                else
                    originPosX = mViewPortHandler.contentLeft() + xoffset;

                if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                    originPosX += mLegend.mNeededWidth;

                break;

            case RIGHT:

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = mViewPortHandler.getChartWidth() - xoffset;
                else
                    originPosX = mViewPortHandler.contentRight() - xoffset;

                if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                    originPosX -= mLegend.mNeededWidth;

                break;

            case CENTER:

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = mViewPortHandler.getChartWidth() / 2.f;
                else
                    originPosX = mViewPortHandler.contentLeft()
                            + mViewPortHandler.contentWidth() / 2.f;

                originPosX += (direction == Legend.LegendDirection.LEFT_TO_RIGHT
                        ? +xoffset
                        : -xoffset);

                // Horizontally layed out legends do the center offset on a line basis,
                // So here we offset the vertical ones only.
                if (orientation == Legend.LegendOrientation.VERTICAL) {
                    originPosX += (direction == Legend.LegendDirection.LEFT_TO_RIGHT
                            ? -mLegend.mNeededWidth / 2.0 + xoffset
                            : mLegend.mNeededWidth / 2.0 - xoffset);
                }

                break;
        }

        switch (orientation) {
            case HORIZONTAL: {

                FSize[] calculatedLineSizes = mLegend.getCalculatedLineSizes();
                FSize[] calculatedLabelSizes = mLegend.getCalculatedLabelSizes();
                Boolean[] calculatedLabelBreakPoints = mLegend.getCalculatedLabelBreakPoints();

                float posX = originPosX;
                float posY = 0.f;

                switch (verticalAlignment) {
                    case TOP:
                        posY = yoffset;
                        break;

                    case BOTTOM:
                        posY = mViewPortHandler.getChartHeight() - yoffset - mLegend.mNeededHeight;
                        break;

                    case CENTER:
                        posY = (mViewPortHandler.getChartHeight() - mLegend.mNeededHeight) / 2.f + yoffset;
                        break;
                }

                int lineIndex = 0;

                for (int i = 0, count = labels.length; i < count; i++) {
                    if (i < calculatedLabelBreakPoints.length && calculatedLabelBreakPoints[i]) {
                        posX = originPosX;
                        posY += labelLineHeight + labelLineSpacing;
                    }

                    if (posX == originPosX &&
                            horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER &&
                            lineIndex < calculatedLineSizes.length) {
                        posX += (direction == Legend.LegendDirection.RIGHT_TO_LEFT
                                ? calculatedLineSizes[lineIndex].width
                                : -calculatedLineSizes[lineIndex].width) / 2.f;
                        lineIndex++;
                    }

                    boolean drawingForm = colors[i] != ColorTemplate.COLOR_SKIP;
                    boolean isStacked = labels[i] == null; // grouped forms have null labels

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= formSize;

                        drawForm(c, posX, posY + formYOffset, i, mLegend);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (!isStacked) {
                        if (drawingForm)
                            posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -formToTextSpace :
                                    formToTextSpace;

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= calculatedLabelSizes[i].width;

                        drawLabel(c, posX, posY + labelLineHeight, labels[i]);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += calculatedLabelSizes[i].width;

                        posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -xEntrySpace : xEntrySpace;
                    } else
                        posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -stackSpace : stackSpace;
                }

                break;
            }

            case VERTICAL: {
                // contains the stacked legend size in pixels
                float stack = 0f;
                boolean wasStacked = false;
                float posY = 0.f;

                switch (verticalAlignment) {
                    case TOP:
                        posY = (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER
                                ? 0.f
                                : mViewPortHandler.contentTop());
                        posY += yoffset;
                        break;

                    case BOTTOM:
                        posY = (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER
                                ? mViewPortHandler.getChartHeight()
                                : mViewPortHandler.contentBottom());
                        posY -= mLegend.mNeededHeight + yoffset;
                        break;

                    case CENTER:
                        posY = mViewPortHandler.getChartHeight() / 2.f
                                - mLegend.mNeededHeight / 2.f
                                + mLegend.getYOffset();
                        break;
                }

                for (int i = 0; i < labels.length; i++) {

                    Boolean drawingForm = colors[i] != ColorTemplate.COLOR_SKIP;
                    float posX = originPosX;

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += stack;
                        else
                            posX -= formSize - stack;

                        drawForm(c, posX, posY + formYOffset, i, mLegend);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (labels[i] != null) {

                        if (drawingForm && !wasStacked)
                            posX += direction == Legend.LegendDirection.LEFT_TO_RIGHT ? formToTextSpace
                                    : -formToTextSpace;
                        else if (wasStacked)
                            posX = originPosX;

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= Utils.calcTextWidth(mLegendLabelPaint, labels[i]);

                        if (!wasStacked) {
                            drawLabel(c, posX, posY + labelLineHeight, labels[i]);
                        } else {
                            posY += labelLineHeight + labelLineSpacing;
                            drawLabel(c, posX, posY + labelLineHeight, labels[i]);
                        }

                        // make a step down
                        posY += labelLineHeight + labelLineSpacing;
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;

            }
        }
    }

    /**
     * Draws the Legend-form at the given position with the color at the given
     * index.
     *
     * @param c     canvas to draw with
     * @param x     position
     * @param y     position
     * @param index the index of the color to use (in the colors array)
     */
    protected void drawForm(Canvas c, float x, float y, int index, Legend legend) {

        if (legend.getColors()[index] == ColorTemplate.COLOR_SKIP)
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
     * @param c     canvas to draw with
     * @param x
     * @param y
     * @param label the label to draw
     */
    protected void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, mLegendLabelPaint);
    }
}
