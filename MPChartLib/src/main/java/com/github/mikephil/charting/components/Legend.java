
package com.github.mikephil.charting.components;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.github.mikephil.charting.data.Entry;
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
     * This property is deprecated - Use `position`, `horizontalAlignment`, `verticalAlignment`, `orientation`, `drawInside`,
     * `direction`.
     */
    public enum LegendPosition {
        RIGHT_OF_CHART, RIGHT_OF_CHART_CENTER, RIGHT_OF_CHART_INSIDE,
        LEFT_OF_CHART, LEFT_OF_CHART_CENTER, LEFT_OF_CHART_INSIDE,
        BELOW_CHART_LEFT, BELOW_CHART_RIGHT, BELOW_CHART_CENTER,
        ABOVE_CHART_LEFT, ABOVE_CHART_RIGHT, ABOVE_CHART_CENTER,
        PIECHART_CENTER
    }

    public enum LegendForm {
        /**
         * Avoid drawing a form
         */
        NONE,

        /**
         * Do not draw the a form, but leave space for it
         */
        EMPTY,

        /**
         * Use default (default dataset's form to the legend's form)
         */
        DEFAULT,

        /**
         * Draw a square
         */
        SQUARE,

        /**
         * Draw a circle
         */
        CIRCLE,

        /**
         * Draw a horizontal line
         */
        LINE
    }

    public enum LegendHorizontalAlignment {
        LEFT, CENTER, RIGHT
    }

    public enum LegendVerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    public enum LegendOrientation {
        HORIZONTAL, VERTICAL
    }

    public enum LegendDirection {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    /**
     * The legend entries array
     */
    private LegendEntry[] mEntries = new LegendEntry[]{};

    /**
     * Entries that will be appended to the end of the auto calculated entries after calculating the legend.
     * (if the legend has already been calculated, you will need to call notifyDataSetChanged() to let the changes take effect)
     */
    private LegendEntry[] mExtraEntries;

    /**
     * Are the legend labels/colors a custom value or auto calculated? If false,
     * then it's auto, if true, then custom. default false (automatic legend)
     */
    private boolean mIsLegendCustom = false;

    private LegendHorizontalAlignment mHorizontalAlignment = LegendHorizontalAlignment.LEFT;
    private LegendVerticalAlignment mVerticalAlignment = LegendVerticalAlignment.BOTTOM;
    private LegendOrientation mOrientation = LegendOrientation.HORIZONTAL;
    private boolean mDrawInside = false;

    /**
     * the text direction for the legend
     */
    private LegendDirection mDirection = LegendDirection.LEFT_TO_RIGHT;

    /**
     * the shape/form the legend colors are drawn in
     */
    private LegendForm mShape = LegendForm.SQUARE;

    /**
     * the size of the legend forms/shapes
     */
    private float mFormSize = 8f;

    /**
     * the size of the legend forms/shapes
     */
    private float mFormLineWidth = 3f;

    /**
     * Line dash path effect used for shapes that consist of lines.
     */
    private DashPathEffect mFormLineDashEffect = null;

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

    /**
     * the space that should be left between stacked forms
     */
    private float mStackSpace = 3f;

    /**
     * the maximum relative size out of the whole chart view in percent
     */
    private float mMaxSizePercent = 0.95f;

    /**
     * default constructor
     */
    public Legend() {

        mFormSize = Utils.convertDpToPixel(8f);
        mFormLineWidth = Utils.convertDpToPixel(3f);
        mXEntrySpace = Utils.convertDpToPixel(6f);
        mYEntrySpace = Utils.convertDpToPixel(0f);
        mFormToTextSpace = Utils.convertDpToPixel(5f);
        mTextSize = Utils.convertDpToPixel(10f);
        mStackSpace = Utils.convertDpToPixel(3f);
        this.mXOffset = Utils.convertDpToPixel(5f);
        this.mYOffset = Utils.convertDpToPixel(3f); // 2
    }

    /**
     * Constructor. Provide entries for the legend.
     *
     * @param entries
     */
    public Legend(LegendEntry[] entries) {
        this();

        if (entries == null) {
            throw new IllegalArgumentException("entries array is NULL");
        }

        this.mEntries = entries;
    }

    @Deprecated
    public Legend(int[] colors, String[] labels) {
        this();

        if (colors == null || labels == null) {
            throw new IllegalArgumentException("colors array or labels array is NULL");
        }

        if (colors.length != labels.length) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < Math.min(colors.length, labels.length); i++) {
            final LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = labels[i];

            if (entry.formColor == ColorTemplate.COLOR_SKIP)
                entry.form = LegendForm.NONE;
            else if (entry.formColor == ColorTemplate.COLOR_NONE ||
                    entry.formColor == 0)
                entry.form = LegendForm.EMPTY;

            entries.add(entry);
        }

        mEntries = entries.toArray(new LegendEntry[entries.size()]);
    }

    @Deprecated
    public Legend(List<Integer> colors, List<String> labels) {
        this(Utils.convertIntegers(colors), Utils.convertStrings(labels));
    }

    /**
     * This method sets the automatically computed colors for the legend. Use setCustom(...) to set custom colors.
     *
     * @param entries
     */
    public void setEntries(List<LegendEntry> entries) {
        mEntries = entries.toArray(new LegendEntry[entries.size()]);
    }

    public LegendEntry[] getEntries() {
        return mEntries;
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
        float maxFormSize = 0f;

        for (LegendEntry entry : mEntries) {
            final float formSize = Float.isNaN(entry.formSize)
                    ? mFormSize : entry.formSize;
            if (formSize > maxFormSize)
                maxFormSize = formSize;

            String label = entry.label;
            if (label == null) continue;

            float length = (float) Utils.calcTextWidth(p, label);

            if (length > max)
                max = length;
        }

        return max + maxFormSize + mFormToTextSpace;
    }

    /**
     * returns the maximum height in pixels across all legend labels
     *
     * @param p the paint object used for rendering the text
     * @return
     */
    public float getMaximumEntryHeight(Paint p) {

        float max = 0f;

        for (LegendEntry entry : mEntries) {
            String label = entry.label;
            if (label == null) continue;

            float length = (float) Utils.calcTextHeight(p, label);

            if (length > max)
                max = length;
        }

        return max;
    }

    @Deprecated
    public int[] getColors() {

        int[] old = new int[mEntries.length];
        for (int i = 0; i < mEntries.length; i++) {
            old[i] = mEntries[i].form == LegendForm.NONE ? ColorTemplate.COLOR_SKIP :
                    (mEntries[i].form == LegendForm.EMPTY ? ColorTemplate.COLOR_NONE :
                            mEntries[i].formColor);
        }
        return old;
    }

    @Deprecated
    public String[] getLabels() {

        String[] old = new String[mEntries.length];
        for (int i = 0; i < mEntries.length; i++) {
            old[i] = mEntries[i].label;
        }
        return old;
    }

    @Deprecated
    public int[] getExtraColors() {

        int[] old = new int[mExtraEntries.length];
        for (int i = 0; i < mExtraEntries.length; i++) {
            old[i] = mExtraEntries[i].form == LegendForm.NONE ? ColorTemplate.COLOR_SKIP :
                    (mExtraEntries[i].form == LegendForm.EMPTY ? ColorTemplate.COLOR_NONE :
                            mExtraEntries[i].formColor);
        }
        return old;
    }

    @Deprecated
    public String[] getExtraLabels() {

        String[] old = new String[mExtraEntries.length];
        for (int i = 0; i < mExtraEntries.length; i++) {
            old[i] = mExtraEntries[i].label;
        }
        return old;
    }

    public LegendEntry[] getExtraEntries() {

        return mExtraEntries;
    }

    public void setExtra(List<LegendEntry> entries) {
        mExtraEntries = entries.toArray(new LegendEntry[entries.size()]);
    }

    public void setExtra(LegendEntry[] entries) {
        if (entries == null)
            entries = new LegendEntry[]{};
        mExtraEntries = entries;
    }

    @Deprecated
    public void setExtra(List<Integer> colors, List<String> labels) {
        setExtra(Utils.convertIntegers(colors), Utils.convertStrings(labels));
    }

    /**
     * Entries that will be appended to the end of the auto calculated
     *   entries after calculating the legend.
     * (if the legend has already been calculated, you will need to call notifyDataSetChanged()
     *   to let the changes take effect)
     */
    public void setExtra(int[] colors, String[] labels) {

        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < Math.min(colors.length, labels.length); i++) {
            final LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = labels[i];

            if (entry.formColor == ColorTemplate.COLOR_SKIP ||
                    entry.formColor == 0)
                entry.form = LegendForm.NONE;
            else if (entry.formColor == ColorTemplate.COLOR_NONE)
                entry.form = LegendForm.EMPTY;

            entries.add(entry);
        }

        mExtraEntries = entries.toArray(new LegendEntry[entries.size()]);
    }

    /**
     * Sets a custom legend's entries array.
     * * A null label will start a group.
     * This will disable the feature that automatically calculates the legend
     *   entries from the datasets.
     * Call resetCustom() to re-enable automatic calculation (and then
     *   notifyDataSetChanged() is needed to auto-calculate the legend again)
     */
    public void setCustom(LegendEntry[] entries) {

        mEntries = entries;
        mIsLegendCustom = true;
    }

    /**
     * Sets a custom legend's entries array.
     * * A null label will start a group.
     * This will disable the feature that automatically calculates the legend
     *   entries from the datasets.
     * Call resetCustom() to re-enable automatic calculation (and then
     *   notifyDataSetChanged() is needed to auto-calculate the legend again)
     */
    public void setCustom(List<LegendEntry> entries) {

        mEntries = entries.toArray(new LegendEntry[entries.size()]);
        mIsLegendCustom = true;
    }

    /**
     * Calling this will disable the custom legend entries (set by
     * setCustom(...)). Instead, the entries will again be calculated
     * automatically (after notifyDataSetChanged() is called).
     */
    public void resetCustom() {
        mIsLegendCustom = false;
    }

    /**
     * @return true if a custom legend entries has been set default
     * false (automatic legend)
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
        } else if (mOrientation == LegendOrientation.HORIZONTAL) {
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
        } else {
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
     * sets the size in dp of the legend forms, default 8f
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
     * sets the line width in dp for forms that consist of lines, default 3f
     *
     * @param size
     */
    public void setFormLineWidth(float size) {
        mFormLineWidth = Utils.convertDpToPixel(size);
    }

    /**
     * returns the line width in dp for drawing forms that consist of lines
     *
     * @return
     */
    public float getFormLineWidth() {
        return mFormLineWidth;
    }

    /**
     * Sets the line dash path effect used for shapes that consist of lines.
     *
     * @param dashPathEffect
     */
    public void setFormLineDashEffect(DashPathEffect dashPathEffect) {
        mFormLineDashEffect = dashPathEffect;
    }

    /**
     * @return The line dash path effect used for shapes that consist of lines.
     */
    public DashPathEffect getFormLineDashEffect() {
        return mFormLineDashEffect;
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
     * the total width of the legend (needed width space)
     */
    public float mNeededWidth = 0f;

    /**
     * the total height of the legend (needed height space)
     */
    public float mNeededHeight = 0f;

    public float mTextHeightMax = 0f;

    public float mTextWidthMax = 0f;

    /**
     * flag that indicates if word wrapping is enabled
     */
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

    private List<FSize> mCalculatedLabelSizes = new ArrayList<>(16);
    private List<Boolean> mCalculatedLabelBreakPoints = new ArrayList<>(16);
    private List<FSize> mCalculatedLineSizes = new ArrayList<>(16);

    public List<FSize> getCalculatedLabelSizes() {
        return mCalculatedLabelSizes;
    }

    public List<Boolean> getCalculatedLabelBreakPoints() {
        return mCalculatedLabelBreakPoints;
    }

    public List<FSize> getCalculatedLineSizes() {
        return mCalculatedLineSizes;
    }

    /**
     * Calculates the dimensions of the Legend. This includes the maximum width
     * and height of a single entry, as well as the total width and height of
     * the Legend.
     *
     * @param labelpaint
     */
    public void calculateDimensions(Paint labelpaint, ViewPortHandler viewPortHandler) {

        float defaultFormSize = mFormSize;
        float stackSpace = mStackSpace;
        float formToTextSpace = mFormToTextSpace;
        float xEntrySpace = mXEntrySpace;
        float yEntrySpace = mYEntrySpace;
        boolean wordWrapEnabled = mWordWrapEnabled;
        LegendEntry[] entries = mEntries;
        int entryCount = entries.length;

        mTextWidthMax = getMaximumEntryWidth(labelpaint);
        mTextHeightMax = getMaximumEntryHeight(labelpaint);

        switch (mOrientation) {
            case VERTICAL: {

                float maxWidth = 0f, maxHeight = 0f, width = 0f;
                float labelLineHeight = Utils.getLineHeight(labelpaint);
                boolean wasStacked = false;

                for (int i = 0; i < entryCount; i++) {

                    LegendEntry e = entries[i];
                    boolean drawingForm = e.form != LegendForm.NONE;
                    float formSize = Float.isNaN(e.formSize) ? defaultFormSize : e.formSize;
                    String label = e.label;

                    if (!wasStacked)
                        width = 0.f;

                    if (drawingForm) {
                        if (wasStacked)
                            width += stackSpace;
                        width += formSize;
                    }

                    // grouped forms have null labels
                    if (label != null) {

                        // make a step to the left
                        if (drawingForm && !wasStacked)
                            width += formToTextSpace;
                        else if (wasStacked) {
                            maxWidth = Math.max(maxWidth, width);
                            maxHeight += labelLineHeight + yEntrySpace;
                            width = 0.f;
                            wasStacked = false;
                        }

                        width += Utils.calcTextWidth(labelpaint, label);

                        if (i < entryCount - 1)
                            maxHeight += labelLineHeight + yEntrySpace;
                    } else {
                        wasStacked = true;
                        width += formSize;
                        if (i < entryCount - 1)
                            width += stackSpace;
                    }

                    maxWidth = Math.max(maxWidth, width);
                }

                mNeededWidth = maxWidth;
                mNeededHeight = maxHeight;

                break;
            }
            case HORIZONTAL: {

                float labelLineHeight = Utils.getLineHeight(labelpaint);
                float labelLineSpacing = Utils.getLineSpacing(labelpaint) + yEntrySpace;
                float contentWidth = viewPortHandler.contentWidth() * mMaxSizePercent;

                // Start calculating layout
                float maxLineWidth = 0.f;
                float currentLineWidth = 0.f;
                float requiredWidth = 0.f;
                int stackedStartIndex = -1;

                mCalculatedLabelBreakPoints.clear();
                mCalculatedLabelSizes.clear();
                mCalculatedLineSizes.clear();

                for (int i = 0; i < entryCount; i++) {

                    LegendEntry e = entries[i];
                    boolean drawingForm = e.form != LegendForm.NONE;
                    float formSize = Float.isNaN(e.formSize) ? defaultFormSize : e.formSize;
                    String label = e.label;

                    mCalculatedLabelBreakPoints.add(false);

                    if (stackedStartIndex == -1) {
                        // we are not stacking, so required width is for this label
                        // only
                        requiredWidth = 0.f;
                    } else {
                        // add the spacing appropriate for stacked labels/forms
                        requiredWidth += stackSpace;
                    }

                    // grouped forms have null labels
                    if (label != null) {

                        mCalculatedLabelSizes.add(Utils.calcTextSize(labelpaint, label));
                        requiredWidth += drawingForm ? mFormToTextSpace + formSize : 0.f;
                        requiredWidth += mCalculatedLabelSizes.get(i).width;
                    } else {

                        mCalculatedLabelSizes.add(FSize.getInstance(0.f, 0.f));
                        requiredWidth += drawingForm ? formSize : 0.f;

                        if (stackedStartIndex == -1) {
                            // mark this index as we might want to break here later
                            stackedStartIndex = i;
                        }
                    }

                    if (label != null || i == entryCount - 1) {

                        float requiredSpacing = currentLineWidth == 0.f ? 0.f : xEntrySpace;

                        if (!wordWrapEnabled // No word wrapping, it must fit.
                                // The line is empty, it must fit
                                || currentLineWidth == 0.f
                                // It simply fits
                                || (contentWidth - currentLineWidth >=
                                requiredSpacing + requiredWidth)) {
                            // Expand current line
                            currentLineWidth += requiredSpacing + requiredWidth;
                        } else { // It doesn't fit, we need to wrap a line

                            // Add current line size to array
                            mCalculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight));
                            maxLineWidth = Math.max(maxLineWidth, currentLineWidth);

                            // Start a new line
                            mCalculatedLabelBreakPoints.set(
                                    stackedStartIndex > -1 ? stackedStartIndex
                                            : i, true);
                            currentLineWidth = requiredWidth;
                        }

                        if (i == entryCount - 1) {
                            // Add last line size to array
                            mCalculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight));
                            maxLineWidth = Math.max(maxLineWidth, currentLineWidth);
                        }
                    }

                    stackedStartIndex = label != null ? -1 : stackedStartIndex;
                }

                mNeededWidth = maxLineWidth;
                mNeededHeight = labelLineHeight
                        * (float) (mCalculatedLineSizes.size())
                        + labelLineSpacing *
                        (float) (mCalculatedLineSizes.size() == 0
                                ? 0
                                : (mCalculatedLineSizes.size() - 1));

                break;
            }
        }

        mNeededHeight += mYOffset;
        mNeededWidth += mXOffset;
    }
}
