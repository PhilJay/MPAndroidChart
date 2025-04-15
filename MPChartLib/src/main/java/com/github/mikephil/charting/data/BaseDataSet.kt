package com.github.mikephil.charting.data

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils

/**
 * This is the base dataset of all DataSets. It's purpose is to implement critical methods
 * provided by the IDataSet interface.
 */
abstract class BaseDataSet<T : Entry?>() : IDataSet<T> {
    /**
     * List representing all colors that are used for this DataSet
     */
    protected var mColors: MutableList<Int>

    /**
     * List representing all colors that are used for drawing the actual values for this DataSet
     */
    protected var mValueColors: MutableList<Int>

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    private var mLabel = "DataSet"

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    protected var mAxisDependency: AxisDependency = AxisDependency.LEFT

    /**
     * if true, value highlightning is enabled
     */
    protected var mHighlightEnabled: Boolean = true

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    @Transient
    protected var mValueFormatter: IValueFormatter? = null

    /**
     * the typeface used for the value text
     */
    protected var mValueTypeface: Typeface? = null

    private var mForm = LegendForm.DEFAULT
    private var mFormSize = Float.NaN
    private var mFormLineWidth = Float.NaN
    private var mFormLineDashEffect: DashPathEffect? = null

    /**
     * if true, y-values are drawn on the chart
     */
    protected var mDrawValues: Boolean = true

    /**
     * if true, y-icons are drawn on the chart
     */
    protected var mDrawIcons: Boolean = true

    /**
     * the offset for drawing icons (in dp)
     */
    protected var mIconsOffset: MPPointF = MPPointF()

    /**
     * the size of the value-text labels
     */
    protected var mValueTextSize: Float = 17f

    /**
     * flag that indicates if the DataSet is visible or not
     */
    protected var mVisible: Boolean = true

    /**
     * Default constructor.
     */
    init {
        mColors = ArrayList()
        mValueColors = ArrayList()

        // default color
        mColors.add(Color.rgb(140, 234, 255))
        mValueColors.add(Color.BLACK)
    }

    /**
     * Constructor with label.
     *
     * @param label
     */
    constructor(label: String) : this() {
        this.mLabel = label
    }

    /**
     * Use this method to tell the data set that the underlying data has changed.
     */
    fun notifyDataSetChanged() {
        calcMinMax()
    }

    override fun getColors(): List<Int> {
        return mColors
    }

    val valueColors: List<Int>
        get() = mValueColors

    override fun getColor(): Int {
        return mColors[0]
    }

    override fun getColor(index: Int): Int {
        return mColors[index % mColors.size]
    }

    /**
     * ###### ###### COLOR SETTING RELATED METHODS ##### ######
     */
    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * ContextCompat.getColor(context,..) before adding them to the DataSet.
     *
     * @param colors
     */
    fun setColors(colors: MutableList<Int>) {
        this.mColors = colors
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * ContextCompat.getColor(context,...)) before adding them to the DataSet.
     *
     * @param colors
     */
    fun setColors(vararg colors: Int) {
        this.mColors = ColorTemplate.createColors(colors)
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * ContextCompat.getColor(context,...)
     *
     * @param colors
     */
    fun setColors(colors: IntArray, context: Context) {
        mColors.clear()

        for (color in colors) {
            mColors.add(ContextCompat.getColor(context, color))
        }
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     *
     * @param color
     */
    fun addColor(color: Int) {
        mColors.add(color)
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     *
     * @param color
     */
    fun setColor(color: Int) {
        resetColors()
        mColors.add(color)
    }

    /**
     * Sets a color with a specific alpha value.
     *
     * @param color
     * @param alpha from 0-255
     */
    fun setColor(color: Int, alpha: Int) {
        setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)))
    }

    /**
     * Sets colors with a specific alpha value.
     *
     * @param colors
     * @param alpha
     */
    fun setColors(colors: IntArray, alpha: Int) {
        resetColors()
        for (color in colors) {
            addColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)))
        }
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    fun resetColors() {
        mColors.clear()
    }

    /**
     * ###### ###### OTHER STYLING RELATED METHODS ##### ######
     */
    override fun setLabel(label: String) {
        mLabel = label
    }

    override fun getLabel(): String {
        return mLabel
    }

    override fun setHighlightEnabled(enabled: Boolean) {
        mHighlightEnabled = enabled
    }

    override fun isHighlightEnabled(): Boolean {
        return mHighlightEnabled
    }

    override fun setValueFormatter(f: IValueFormatter?) {
        if (f == null) return
        else mValueFormatter = f
    }

    override fun getValueFormatter(): IValueFormatter {
        if (needsFormatter()) return Utils.getDefaultValueFormatter()
        return mValueFormatter!!
    }

    override fun needsFormatter(): Boolean {
        return mValueFormatter == null
    }

    override fun setValueTextColor(color: Int) {
        mValueColors.clear()
        mValueColors.add(color)
    }

    override fun setValueTextColors(colors: MutableList<Int>) {
        mValueColors = colors
    }

    override fun setValueTypeface(tf: Typeface?) {
        mValueTypeface = tf
    }

    override fun setValueTextSize(size: Float) {
        mValueTextSize = Utils.convertDpToPixel(size)
    }

    override fun getValueTextColor(): Int {
        return mValueColors[0]
    }

    override fun getValueTextColor(index: Int): Int {
        return mValueColors[index % mValueColors.size]
    }

    override fun getValueTypeface(): Typeface? {
        return mValueTypeface
    }

    override fun getValueTextSize(): Float {
        return mValueTextSize
    }

    fun setForm(form: LegendForm) {
        mForm = form
    }

    override fun getForm(): LegendForm {
        return mForm
    }

    fun setFormSize(formSize: Float) {
        mFormSize = formSize
    }

    override fun getFormSize(): Float {
        return mFormSize
    }

    fun setFormLineWidth(formLineWidth: Float) {
        mFormLineWidth = formLineWidth
    }

    override fun getFormLineWidth(): Float {
        return mFormLineWidth
    }

    fun setFormLineDashEffect(dashPathEffect: DashPathEffect?) {
        mFormLineDashEffect = dashPathEffect
    }

    override fun getFormLineDashEffect(): DashPathEffect? {
        return mFormLineDashEffect
    }

    override fun setDrawValues(enabled: Boolean) {
        this.mDrawValues = enabled
    }

    override fun isDrawValuesEnabled(): Boolean {
        return mDrawValues
    }

    override fun setDrawIcons(enabled: Boolean) {
        mDrawIcons = enabled
    }

    override fun isDrawIconsEnabled(): Boolean {
        return mDrawIcons
    }

    override fun setIconsOffset(offsetDp: MPPointF) {
        mIconsOffset.x = offsetDp.x
        mIconsOffset.y = offsetDp.y
    }

    override fun getIconsOffset(): MPPointF {
        return mIconsOffset
    }

    override fun setVisible(visible: Boolean) {
        mVisible = visible
    }

    override fun isVisible(): Boolean {
        return mVisible
    }

    override fun getAxisDependency(): AxisDependency {
        return mAxisDependency
    }

    override fun setAxisDependency(dependency: AxisDependency) {
        mAxisDependency = dependency
    }


    /**
     * ###### ###### DATA RELATED METHODS ###### ######
     */
    override fun getIndexInEntries(xIndex: Int): Int {
        for (i in 0..<entryCount) {
            if (xIndex.toFloat() == getEntryForIndex(i)!!.x) return i
        }

        return -1
    }

    override fun removeFirst(): Boolean {
        if (entryCount > 0) {
            val entry = getEntryForIndex(0)
            return removeEntry(entry)
        } else return false
    }

    override fun removeLast(): Boolean {
        if (entryCount > 0) {
            val e = getEntryForIndex(entryCount - 1)
            return removeEntry(e)
        } else return false
    }

    override fun removeEntryByXValue(xValue: Float): Boolean {
        val e = getEntryForXValue(xValue, Float.NaN)
        return removeEntry(e)
    }

    override fun removeEntry(index: Int): Boolean {
        val e = getEntryForIndex(index)
        return removeEntry(e)
    }

    override fun contains(e: T): Boolean {
        for (i in 0..<entryCount) {
            if (getEntryForIndex(i) == e) return true
        }

        return false
    }

    protected fun copy(baseDataSet: BaseDataSet<*>) {
        baseDataSet.mAxisDependency = mAxisDependency
        baseDataSet.mColors = mColors
        baseDataSet.mDrawIcons = mDrawIcons
        baseDataSet.mDrawValues = mDrawValues
        baseDataSet.mForm = mForm
        baseDataSet.mFormLineDashEffect = mFormLineDashEffect
        baseDataSet.mFormLineWidth = mFormLineWidth
        baseDataSet.mFormSize = mFormSize
        baseDataSet.mHighlightEnabled = mHighlightEnabled
        baseDataSet.mIconsOffset = mIconsOffset
        baseDataSet.mValueColors = mValueColors
        baseDataSet.mValueFormatter = mValueFormatter
        baseDataSet.mValueTextSize = mValueTextSize
        baseDataSet.mVisible = mVisible
    }
}
