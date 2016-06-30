
package com.github.mikephil.charting.components;

import android.graphics.Paint;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the legend of the chart. The legend will contain one entry
 * per color and DataSet. Multiple colors in one DataSet are grouped together.
 * The legend object is NOT available before setting data to the chart.
 * 
 * @author Philipp Jahoda
 */
public class Legend extends ComponentBase {

    /**
     * This property is deprecated - Use `position`, `horizontalAlignment`, `verticalAlignment`, `orientation`, `drawInside`, `direction`.
     */
    public enum LegendPosition {
        RIGHT_OF_CHART, RIGHT_OF_CHART_CENTER, RIGHT_OF_CHART_INSIDE,
        LEFT_OF_CHART, LEFT_OF_CHART_CENTER, LEFT_OF_CHART_INSIDE,
        BELOW_CHART_LEFT, BELOW_CHART_RIGHT, BELOW_CHART_CENTER,
        ABOVE_CHART_LEFT, ABOVE_CHART_RIGHT, ABOVE_CHART_CENTER,
        PIECHART_CENTER
    }

    public enum LegendForm {
        SQUARE, CIRCLE, LINE
    }

    public enum LegendHorizontalAlignment
    {
        LEFT, CENTER, RIGHT
    }

    public enum LegendVerticalAlignment
    {
        TOP, CENTER, BOTTOM
    }

    public enum LegendOrientation
    {
        HORIZONTAL, VERTICAL
    }

    public enum LegendDirection {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    /**
     * the legend colors array, each color is for the form drawn at the same
     * index
     */
    private int[] mColors;

    /** the legend text array. a null label will start a group. */
    private String[] mLabels;

    /**
     * colors that will be appended to the end of the colors array after
     * calculating the legend.
     */
    private int[] mExtraColors;

    /**
     * labels that will be appended to the end of the labels array after
     * calculating the legend. a null label will start a group.
     */
    private String[] mExtraLabels;

    /**
     * Are the legend labels/colors a custom value or auto calculated? If false,
     * then it's auto, if true, then custom. default false (automatic legend)
     */
    private boolean mIsLegendCustom = false;

    private LegendHorizontalAlignment mHorizontalAlignment = LegendHorizontalAlignment.LEFT;
    private LegendVerticalAlignment mVerticalAlignment = LegendVerticalAlignment.BOTTOM;
    private LegendOrientation mOrientation = LegendOrientation.HORIZONTAL;
    private boolean mDrawInside = false;

    /** the text direction for the legend */
    private LegendDirection mDirection = LegendDirection.LEFT_TO_RIGHT;

    /** the shape/form the legend colors are drawn in */
    private LegendForm mShape = LegendForm.SQUARE;

    /** the size of the legend forms/shapes */
    private float mFormSize = 8f;

    /**
     * the space between the legend entries on a horizontal axis, default 6f
     */
    private float mXEntrySpace = 6f;

    /**
     * the space between the legend entries on a vertical axis, default 5f
     */
    private float mYEntrySpace = 0f;

    /**
     * the space between the legend entries on a vertical axis, default 2f
     * private float mYEntrySpace = 2f; /** the space between the form and the
     * actual label/text
     */
    private float mFormToTextSpace = 5f;

    /** the space that should be left between stacked forms */
    private float mStackSpace = 3f;

    /** the maximum relative size out of the whole chart view in percent */
    private float mMaxSizePercent = 0.95f;

    /** default constructor */
    public Legend() {

        mFormSize = Utils.convertDpToPixel(8f);
        mXEntrySpace = Utils.convertDpToPixel(6f);
        mYEntrySpace = Utils.convertDpToPixel(0f);
        mFormToTextSpace = Utils.convertDpToPixel(5f);
        mTextSize = Utils.convertDpToPixel(10f);
        mStackSpace = Utils.convertDpToPixel(3f);
        this.mXOffset = Utils.convertDpToPixel(5f);
        this.mYOffset = Utils.convertDpToPixel(3f); // 2
    }

    /**
     * Constructor. Provide colors and labels for the legend.
     * 
     * @param colors
     * @param labels
     */
    public Legend(int[] colors, String[] labels) {
        this();

        if (colors == null || labels == null) {
            throw new IllegalArgumentException("colors array or labels array is NULL");
        }

        if (colors.length != labels.length) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        this.mColors = colors;
        this.mLabels = labels;
    }

    /**
     * Constructor. Provide colors and labels for the legend.
     * 
     * @param colors
     * @param labels
     */
    public Legend(List<Integer> colors, List<String> labels) {
        this();

        if (colors == null || labels == null) {
            throw new IllegalArgumentException("colors array or labels array is NULL");
        }

        if (colors.size() != labels.size()) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        this.mColors = Utils.convertIntegers(colors);
        this.mLabels = Utils.convertStrings(labels);
    }

    /**
     * This method sets the automatically computed colors for the legend. Use setCustom(...) to set custom colors.
     * @param colors
     */
    public void setComputedColors(List<Integer> colors) {
        mColors = Utils.convertIntegers(colors);
    }

    /**
     * This method sets the automatically computed labels for the legend. Use setCustom(...) to set custom labels.
     * @param labels
     */
    public void setComputedLabels(List<String> labels) {
        mLabels = Utils.convertStrings(labels);
    }

    /**
     * returns the maximum length in pixels across all legend labels + formsize
     * + formtotextspace
     * 
     * @param p the paint object used for rendering the text
     * @return
     */
    public float getMaximumEntryWidth(Paint p) {

        float max = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            if (mLabels[i] != null) {

                float length = (float) Utils.calcTextWidth(p, mLabels[i]);

                if (length > max)
                    max = length;
            }
        }

        return max + mFormSize + mFormToTextSpace;
    }

    /**
     * returns the maximum height in pixels across all legend labels
     * 
     * @param p the paint object used for rendering the text
     * @return
     */
    public float getMaximumEntryHeight(Paint p) {

        float max = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            if (mLabels[i] != null) {

                float length = (float) Utils.calcTextHeight(p, mLabels[i]);

                if (length > max)
                    max = length;
            }
        }

        return max;
    }

    /**
     * returns all the colors the legend uses
     * 
     * @return
     */
    public int[] getColors() {
        return mColors;
    }

    /**
     * returns all the labels the legend uses
     * 
     * @return
     */
    public String[] getLabels() {
        return mLabels;
    }

    /**
     * Returns the legend-label at the given index.
     * 
     * @param index
     * @return
     */
    public String getLabel(int index) {
        return mLabels[index];
    }

    /**
     * colors that will be appended to the end of the colors array after
     * calculating the legend.
     */
    public int[] getExtraColors() {
        return mExtraColors;
    }

    /**
     * labels that will be appended to the end of the labels array after
     * calculating the legend. a null label will start a group.
     */
    public String[] getExtraLabels() {
        return mExtraLabels;
    }

    /**
     * Colors and labels that will be appended to the end of the auto calculated
     * colors and labels arrays after calculating the legend. (if the legend has
     * already been calculated, you will need to call notifyDataSetChanged() to
     * let the changes take effect)
     */
    public void setExtra(List<Integer> colors, List<String> labels) {
        this.mExtraColors = Utils.convertIntegers(colors);
        this.mExtraLabels = Utils.convertStrings(labels);
    }

    /**
     * Colors and labels that will be appended to the end of the auto calculated
     * colors and labels arrays after calculating the legend. (if the legend has
     * already been calculated, you will need to call notifyDataSetChanged() to
     * let the changes take effect)
     */
    public void setExtra(int[] colors, String[] labels) {
        this.mExtraColors = colors;
        this.mExtraLabels = labels;
    }

    /**
     * Sets a custom legend's labels and colors arrays. The colors count should
     * match the labels count. * Each color is for the form drawn at the same
     * index. * A null label will start a group. * A ColorTemplate.COLOR_SKIP
     * color will avoid drawing a form This will disable the feature that
     * automatically calculates the legend labels and colors from the datasets.
     * Call resetCustom() to re-enable automatic calculation (and then
     * notifyDataSetChanged() is needed to auto-calculate the legend again)
     */
    public void setCustom(int[] colors, String[] labels) {

        if (colors.length != labels.length) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        mLabels = labels;
        mColors = colors;
        mIsLegendCustom = true;
    }

    /**
     * Sets a custom legend's labels and colors arrays. The colors count should
     * match the labels count. * Each color is for the form drawn at the same
     * index. * A null label will start a group. * A ColorTemplate.COLOR_SKIP
     * color will avoid drawing a form This will disable the feature that
     * automatically calculates the legend labels and colors from the datasets.
     * Call resetCustom() to re-enable automatic calculation (and then
     * notifyDataSetChanged() is needed to auto-calculate the legend again)
     */
    public void setCustom(List<Integer> colors, List<String> labels) {

        if (colors.size() != labels.size()) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        mColors = Utils.convertIntegers(colors);
        mLabels = Utils.convertStrings(labels);
        mIsLegendCustom = true;
    }

    /**
     * Calling this will disable the custom legend labels (set by
     * setCustom(...)). Instead, the labels will again be calculated
     * automatically (after notifyDataSetChanged() is called).
     */
    public void resetCustom() {
        mIsLegendCustom = false;
    }

    /**
     * @return true if a custom legend labels and colors has been set default
     *         false (automatic legend)
     */
    public boolean isLegendCustom() {
        return mIsLegendCustom;
    }

    /**
     * returns the position of the legend relative to the chart
     * 
     * @return
     */
    public LegendPosition getPosition() {

        if (mOrientation == LegendOrientation.VERTICAL
                && mHorizontalAlignment == LegendHorizontalAlignment.CENTER
                && mVerticalAlignment == LegendVerticalAlignment.CENTER) {
            return LegendPosition.PIECHART_CENTER;
        }
        else if (mOrientation == LegendOrientation.HORIZONTAL) {
            if (mVerticalAlignment == LegendVerticalAlignment.TOP)
                return mHorizontalAlignment == LegendHorizontalAlignment.LEFT
                        ? LegendPosition.ABOVE_CHART_LEFT
                        : (mHorizontalAlignment == LegendHorizontalAlignment.RIGHT
                        ? LegendPosition.ABOVE_CHART_RIGHT
                        : LegendPosition.ABOVE_CHART_CENTER);
            else
                return mHorizontalAlignment == LegendHorizontalAlignment.LEFT
                        ? LegendPosition.BELOW_CHART_LEFT
                        : (mHorizontalAlignment == LegendHorizontalAlignment.RIGHT
                        ? LegendPosition.BELOW_CHART_RIGHT
                        : LegendPosition.BELOW_CHART_CENTER);
        }
        else {
            if (mHorizontalAlignment == LegendHorizontalAlignment.LEFT)
                return mVerticalAlignment == LegendVerticalAlignment.TOP && mDrawInside
                        ? LegendPosition.LEFT_OF_CHART_INSIDE
                        : (mVerticalAlignment == LegendVerticalAlignment.CENTER
                        ? LegendPosition.LEFT_OF_CHART_CENTER
                        : LegendPosition.LEFT_OF_CHART);
            else
                return mVerticalAlignment == LegendVerticalAlignment.TOP && mDrawInside
                        ? LegendPosition.RIGHT_OF_CHART_INSIDE
                        : (mVerticalAlignment == LegendVerticalAlignment.CENTER
                        ? LegendPosition.RIGHT_OF_CHART_CENTER
                        : LegendPosition.RIGHT_OF_CHART);
        }
    }

    /**
     * sets the position of the legend relative to the whole chart
     * 
     * @param newValue
     */
    public void setPosition(LegendPosition newValue) {

        switch (newValue) {
            case LEFT_OF_CHART:
            case LEFT_OF_CHART_INSIDE:
            case LEFT_OF_CHART_CENTER:
                mHorizontalAlignment = LegendHorizontalAlignment.LEFT;
                mVerticalAlignment = newValue == LegendPosition.LEFT_OF_CHART_CENTER
                        ? LegendVerticalAlignment.CENTER
                        : LegendVerticalAlignment.TOP;
                mOrientation = LegendOrientation.VERTICAL;
                break;

            case RIGHT_OF_CHART:
            case RIGHT_OF_CHART_INSIDE:
            case RIGHT_OF_CHART_CENTER:
                mHorizontalAlignment = LegendHorizontalAlignment.RIGHT;
                mVerticalAlignment = newValue == LegendPosition.RIGHT_OF_CHART_CENTER
                        ? LegendVerticalAlignment.CENTER
                        : LegendVerticalAlignment.TOP;
                mOrientation = LegendOrientation.VERTICAL;
                break;

            case ABOVE_CHART_LEFT:
            case ABOVE_CHART_CENTER:
            case ABOVE_CHART_RIGHT:
                mHorizontalAlignment = newValue == LegendPosition.ABOVE_CHART_LEFT
                        ? LegendHorizontalAlignment.LEFT
                        : (newValue == LegendPosition.ABOVE_CHART_RIGHT
                        ? LegendHorizontalAlignment.RIGHT
                        : LegendHorizontalAlignment.CENTER);
                mVerticalAlignment = LegendVerticalAlignment.TOP;
                mOrientation = LegendOrientation.HORIZONTAL;
                break;

            case BELOW_CHART_LEFT:
            case BELOW_CHART_CENTER:
            case BELOW_CHART_RIGHT:
                mHorizontalAlignment = newValue == LegendPosition.BELOW_CHART_LEFT
                        ? LegendHorizontalAlignment.LEFT
                        : (newValue == LegendPosition.BELOW_CHART_RIGHT
                        ? LegendHorizontalAlignment.RIGHT
                        : LegendHorizontalAlignment.CENTER);
                mVerticalAlignment = LegendVerticalAlignment.BOTTOM;
                mOrientation = LegendOrientation.HORIZONTAL;
                break;

            case PIECHART_CENTER:
                mHorizontalAlignment = LegendHorizontalAlignment.CENTER;
                mVerticalAlignment = LegendVerticalAlignment.CENTER;
                mOrientation = LegendOrientation.VERTICAL;
                break;
        }

        mDrawInside = newValue == LegendPosition.LEFT_OF_CHART_INSIDE
                || newValue == LegendPosition.RIGHT_OF_CHART_INSIDE;
    }

    /**
     * returns the horizontal alignment of the legend
     *
     * @return
     */
    public LegendHorizontalAlignment getHorizontalAlignment() {
        return mHorizontalAlignment;
    }

    /**
     * sets the horizontal alignment of the legend
     *
     * @param value
     */
    public void setHorizontalAlignment(LegendHorizontalAlignment value) {
        mHorizontalAlignment = value;
    }

    /**
     * returns the vertical alignment of the legend
     *
     * @return
     */
    public LegendVerticalAlignment getVerticalAlignment() {
        return mVerticalAlignment;
    }

    /**
     * sets the vertical alignment of the legend
     *
     * @param value
     */
    public void setVerticalAlignment(LegendVerticalAlignment value) {
        mVerticalAlignment = value;
    }

    /**
     * returns the orientation of the legend
     *
     * @return
     */
    public LegendOrientation getOrientation() {
        return mOrientation;
    }

    /**
     * sets the orientation of the legend
     *
     * @param value
     */
    public void setOrientation(LegendOrientation value) {
        mOrientation = value;
    }

    /**
     * returns whether the legend will draw inside the chart or outside
     *
     * @return
     */
    public boolean isDrawInsideEnabled() {
        return mDrawInside;
    }

    /**
     * sets whether the legend will draw inside the chart or outside
     *
     * @param value
     */
    public void setDrawInside(boolean value) {
        mDrawInside = value;
    }

    /**
     * returns the text direction of the legend
     *
     * @return
     */
    public LegendDirection getDirection() {
        return mDirection;
    }

    /**
     * sets the text direction of the legend
     *
     * @param pos
     */
    public void setDirection(LegendDirection pos) {
        mDirection = pos;
    }

    /**
     * returns the current form/shape that is set for the legend
     * 
     * @return
     */
    public LegendForm getForm() {
        return mShape;
    }

    /**
     * sets the form/shape of the legend forms
     * 
     * @param shape
     */
    public void setForm(LegendForm shape) {
        mShape = shape;
    }

    /**
     * sets the size in pixels of the legend forms, this is internally converted
     * in dp, default 8f
     * 
     * @param size
     */
    public void setFormSize(float size) {
        mFormSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the size in dp of the legend forms
     * 
     * @return
     */
    public float getFormSize() {
        return mFormSize;
    }

    /**
     * returns the space between the legend entries on a horizontal axis in
     * pixels
     * 
     * @return
     */
    public float getXEntrySpace() {
        return mXEntrySpace;
    }

    /**
     * sets the space between the legend entries on a horizontal axis in pixels,
     * converts to dp internally
     * 
     * @param space
     */
    public void setXEntrySpace(float space) {
        mXEntrySpace = Utils.convertDpToPixel(space);
    }

    /**
     * returns the space between the legend entries on a vertical axis in pixels
     * 
     * @return
     */
    public float getYEntrySpace() {
        return mYEntrySpace;
    }

    /**
     * sets the space between the legend entries on a vertical axis in pixels,
     * converts to dp internally
     * 
     * @param space
     */
    public void setYEntrySpace(float space) {
        mYEntrySpace = Utils.convertDpToPixel(space);
    }

    /**
     * returns the space between the form and the actual label/text
     * 
     * @return
     */
    public float getFormToTextSpace() {
        return mFormToTextSpace;
    }

    /**
     * sets the space between the form and the actual label/text, converts to dp
     * internally
     * 
     * @param space
     */
    public void setFormToTextSpace(float space) {
        this.mFormToTextSpace = Utils.convertDpToPixel(space);
    }

    /**
     * returns the space that is left out between stacked forms (with no label)
     * 
     * @return
     */
    public float getStackSpace() {
        return mStackSpace;
    }

    /**
     * sets the space that is left out between stacked forms (with no label)
     * 
     * @param space
     */
    public void setStackSpace(float space) {
        mStackSpace = space;
    }

    /**
     * calculates the full width the fully drawn legend will use in pixels
     * 
     * @return
     */
    public float getFullWidth(Paint labelpaint) {

        float width = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            // grouped forms have null labels
            if (mLabels[i] != null) {

                // make a step to the left
                if (mColors[i] != ColorTemplate.COLOR_SKIP)
                    width += mFormSize + mFormToTextSpace;

                width += Utils.calcTextWidth(labelpaint, mLabels[i]);

                if (i < mLabels.length - 1)
                    width += mXEntrySpace;
            } else {
                width += mFormSize;
                if (i < mLabels.length - 1)
                    width += mStackSpace;
            }
        }

        return width;
    }

    /**
     * Calculates the full height of the drawn legend.
     * 
     * @param labelpaint
     * @return
     */
    public float getFullHeight(Paint labelpaint) {

        float height = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            // grouped forms have null labels
            if (mLabels[i] != null) {

                height += Utils.calcTextHeight(labelpaint, mLabels[i]);

                if (i < mLabels.length - 1)
                    height += mYEntrySpace;
            }
        }

        return height;
    }

    /** the total width of the legend (needed width space) */
    public float mNeededWidth = 0f;

    /** the total height of the legend (needed height space) */
    public float mNeededHeight = 0f;

    public float mTextHeightMax = 0f;

    public float mTextWidthMax = 0f;

    /** flag that indicates if word wrapping is enabled */
    private boolean mWordWrapEnabled = false;

    /**
     * Should the legend word wrap? / this is currently supported only for:
     * BelowChartLeft, BelowChartRight, BelowChartCenter. / note that word
     * wrapping a legend takes a toll on performance. / you may want to set
     * maxSizePercent when word wrapping, to set the point where the text wraps.
     * / default: false
     * 
     * @param enabled
     */
    public void setWordWrapEnabled(boolean enabled) {
        mWordWrapEnabled = enabled;
    }

    /**
     * If this is set, then word wrapping the legend is enabled. This means the
     * legend will not be cut off if too long.
     * 
     * @return
     */
    public boolean isWordWrapEnabled() {
        return mWordWrapEnabled;
    }

    /**
     * The maximum relative size out of the whole chart view. / If the legend is
     * to the right/left of the chart, then this affects the width of the
     * legend. / If the legend is to the top/bottom of the chart, then this
     * affects the height of the legend. / If the legend is the center of the
     * piechart, then this defines the size of the rectangular bounds out of the
     * size of the "hole". / default: 0.95f (95%)
     * 
     * @return
     */
    public float getMaxSizePercent() {
        return mMaxSizePercent;
    }

    /**
     * The maximum relative size out of the whole chart view. / If
     * the legend is to the right/left of the chart, then this affects the width
     * of the legend. / If the legend is to the top/bottom of the chart, then
     * this affects the height of the legend. / default: 0.95f (95%)
     * 
     * @param maxSize
     */
    public void setMaxSizePercent(float maxSize) {
        mMaxSizePercent = maxSize;
    }

    private FSize[] mCalculatedLabelSizes = new FSize[] {};
    private Boolean[] mCalculatedLabelBreakPoints = new Boolean[] {};
    private FSize[] mCalculatedLineSizes = new FSize[] {};

    public FSize[] getCalculatedLabelSizes() {
        return mCalculatedLabelSizes;
    }

    public Boolean[] getCalculatedLabelBreakPoints() {
        return mCalculatedLabelBreakPoints;
    }

    public FSize[] getCalculatedLineSizes() {
        return mCalculatedLineSizes;
    }

    protected Paint.FontMetrics fontMetricsForCalculateDimensions = new Paint.FontMetrics();
    protected ArrayList<FSize> calculatedLineSizesForCalculateDimensions = new ArrayList<>();
    /**
     * Calculates the dimensions of the Legend. This includes the maximum width
     * and height of a single entry, as well as the total width and height of
     * the Legend.
     * 
     * @param labelpaint
     */
    public void calculateDimensions(Paint labelpaint, ViewPortHandler viewPortHandler) {

        mTextWidthMax = getMaximumEntryWidth(labelpaint);
        mTextHeightMax = getMaximumEntryHeight(labelpaint);

        switch (mOrientation) {
            case VERTICAL: {

                float maxWidth = 0f, maxHeight = 0f, width = 0f;
                float labelLineHeight = Utils.getLineHeight(labelpaint, fontMetricsForCalculateDimensions);
                final int count = mLabels.length;
                boolean wasStacked = false;

                for (int i = 0; i < count; i++) {

                    boolean drawingForm = mColors[i] != ColorTemplate.COLOR_SKIP;

                    if (!wasStacked)
                        width = 0.f;

                    if (drawingForm) {
                        if (wasStacked)
                            width += mStackSpace;
                        width += mFormSize;
                    }

                    // grouped forms have null labels
                    if (mLabels[i] != null) {

                        // make a step to the left
                        if (drawingForm && !wasStacked)
                            width += mFormToTextSpace;
                        else if (wasStacked) {
                            maxWidth = Math.max(maxWidth, width);
                            maxHeight += labelLineHeight + mYEntrySpace;
                            width = 0.f;
                            wasStacked = false;
                        }

                        width += Utils.calcTextWidth(labelpaint, mLabels[i]);

                        if (i < count - 1)
                            maxHeight += labelLineHeight + mYEntrySpace;
                    }
                    else {
                        wasStacked = true;
                        width += mFormSize;
                        if (i < count - 1)
                            width += mStackSpace;
                    }

                    maxWidth = Math.max(maxWidth, width);
                }

                mNeededWidth = maxWidth;
                mNeededHeight = maxHeight;

                break;
            }
            case HORIZONTAL: {

                int labelCount = mLabels.length;
                float labelLineHeight = Utils.getLineHeight(labelpaint, fontMetricsForCalculateDimensions);
                float labelLineSpacing = Utils.getLineSpacing(labelpaint, fontMetricsForCalculateDimensions) + mYEntrySpace;
                float contentWidth = viewPortHandler.contentWidth() * mMaxSizePercent;

                // Prepare arrays for calculated layout

                //ArrayList<Boolean> calculatedLabelBreakPoints = new ArrayList<Boolean>(labelCount);

                if(mCalculatedLabelSizes.length != labelCount){
                    FSize[] temp = new FSize[labelCount];
                    int count = mCalculatedLabelSizes.length;
                    for(int i = 0 ; i < count && i < labelCount ; i++){
                        temp[i] = mCalculatedLabelSizes[i];
                    }
                    while(count > labelCount){
                        count--;
                        FSize.recycleInstance(mCalculatedLabelSizes[count]);
                    }
                    mCalculatedLabelSizes = temp;
                }
                int calculatedLabelSizesIndex = 0;

                if(mCalculatedLabelBreakPoints.length != labelCount){
                    mCalculatedLabelBreakPoints = new Boolean[labelCount];
                }
                int calculatedLabelBreakIndex = 0;

                ArrayList<FSize> calculatedLineSizes = calculatedLineSizesForCalculateDimensions;
                FSize.recycleInstances(calculatedLineSizes);
                calculatedLineSizes.clear();

                // Start calculating layout
                float maxLineWidth = 0.f;
                float currentLineWidth = 0.f;
                float requiredWidth = 0.f;
                int stackedStartIndex = -1;

                for (int i = 0; i < labelCount; i++) {

                    boolean drawingForm = mColors[i] != ColorTemplate.COLOR_SKIP;

                    mCalculatedLabelBreakPoints[calculatedLabelBreakIndex] = false;
                    calculatedLabelBreakIndex++;

                    if (stackedStartIndex == -1) {
                        // we are not stacking, so required width is for this label
                        // only
                        requiredWidth = 0.f;
                    }
                    else {
                        // add the spacing appropriate for stacked labels/forms
                        requiredWidth += mStackSpace;
                    }

                    // grouped forms have null labels
                    if (mLabels[i] != null) {
                        if(mCalculatedLabelSizes[calculatedLabelSizesIndex] == null){
                            mCalculatedLabelSizes[calculatedLabelSizesIndex] = Utils.calcTextSize(labelpaint, mLabels[i]);
                        }else{
                            Utils.calcTextSize(labelpaint, mLabels[i], mCalculatedLabelSizes[calculatedLabelSizesIndex]);
                        }
                        FSize labelSize = mCalculatedLabelSizes[calculatedLabelSizesIndex];
                        calculatedLabelSizesIndex++;
                        requiredWidth += drawingForm ? mFormToTextSpace + mFormSize : 0.f;
                        requiredWidth += labelSize.width;
                    }
                    else {

                        if(mCalculatedLabelSizes[calculatedLabelSizesIndex] == null){
                            mCalculatedLabelSizes[calculatedLabelSizesIndex] = FSize.getInstance(0.f, 0.f);
                        }else{
                            mCalculatedLabelSizes[calculatedLabelSizesIndex].width = 0.f;
                            mCalculatedLabelSizes[calculatedLabelSizesIndex].height = 0.f;
                        }
                        calculatedLabelSizesIndex++;
                        requiredWidth += drawingForm ? mFormSize : 0.f;

                        if (stackedStartIndex == -1) {
                            // mark this index as we might want to break here later
                            stackedStartIndex = i;
                        }
                    }

                    if (mLabels[i] != null || i == labelCount - 1) {

                        float requiredSpacing = currentLineWidth == 0.f ? 0.f : mXEntrySpace;

                        if (!mWordWrapEnabled // No word wrapping, it must fit.
                                // The line is empty, it must fit
                                || currentLineWidth == 0.f
                                // It simply fits
                                || (contentWidth - currentLineWidth >=
                                requiredSpacing + requiredWidth)) {
                            // Expand current line
                            currentLineWidth += requiredSpacing + requiredWidth;
                        }
                        else { // It doesn't fit, we need to wrap a line

                            // Add current line size to array
                            calculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight));
                            maxLineWidth = Math.max(maxLineWidth, currentLineWidth);

                            // Start a new line
                            mCalculatedLabelBreakPoints[
                                    stackedStartIndex > -1 ? stackedStartIndex
                                            : i] =  true;
                            currentLineWidth = requiredWidth;
                        }

                        if (i == labelCount - 1) {
                            // Add last line size to array
                            calculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight));
                            maxLineWidth = Math.max(maxLineWidth, currentLineWidth);
                        }
                    }

                    stackedStartIndex = mLabels[i] != null ? -1 : stackedStartIndex;
                }

                mCalculatedLineSizes = calculatedLineSizes
                        .toArray(new FSize[calculatedLineSizes.size()]);

                mNeededWidth = maxLineWidth;
                mNeededHeight = labelLineHeight
                        * (float) (mCalculatedLineSizes.length)
                        + labelLineSpacing *
                        (float) (mCalculatedLineSizes.length == 0
                                ? 0
                                : (mCalculatedLineSizes.length - 1));

                break;
            }
        }
    }
}
