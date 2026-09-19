package com.github.mikephil.charting.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin

/**
 * Static helpers shared by all charts: dp to pixel conversion, text measuring and drawing, number
 * formatting and small geometry functions.
 *
 * [init] must run once with a [Context] before the dp conversions work. Every chart calls it in its
 * constructor, so you only need to call it yourself when you use these helpers without a chart.
 */
object Utils {

    private const val TAG = "MPChartLib-Utils"

    private var metrics: DisplayMetrics? = null

    /** Minimum touch velocity in pixels per second that counts as a fling. Read from [ViewConfiguration] in [init]. */
    var minimumFlingVelocity = 50
        private set

    /** Maximum touch velocity in pixels per second used for fling calculations. Read from [ViewConfiguration] in [init]. */
    var maximumFlingVelocity = 8000
        private set

    /** Factor that converts degrees to radians as a [Double]. */
    const val DEG2RAD = 3.141592653589793 / 180.0

    /** Factor that converts degrees to radians as a [Float]. */
    const val FDEG2RAD = 3.1415927f / 180f

    /** Smallest positive [Double] value, useful for comparisons against zero. */
    const val DOUBLE_EPSILON = Double.MIN_VALUE

    /** Smallest positive [Float] value, so a comparison against it is effectively a comparison for equality. */
    const val FLOAT_EPSILON = Float.MIN_VALUE

    /**
     * Reads the display density and fling velocities from the given context.
     *
     * Charts call this in their constructor. Call it yourself before using [convertDpToPixel] or
     * [convertPixelsToDp] without a chart.
     */
    fun init(context: Context) {
        val viewConfiguration = ViewConfiguration.get(context)
        minimumFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity
        maximumFlingVelocity = viewConfiguration.scaledMaximumFlingVelocity
        metrics = context.resources.displayMetrics
    }

    /**
     * Converts a dp value to pixels using the display density read in [init].
     *
     * If [init] has not been called yet, this logs an error and returns [dp] unchanged.
     */
    fun convertDpToPixel(dp: Float): Float {
        val metrics = metrics
        if (metrics == null) {
            Log.e(TAG, "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertDpToPixel(...). Otherwise conversion does not take place.")
            return dp
        }
        return dp * metrics.density
    }

    /**
     * Converts a pixel value to dp using the display density read in [init].
     *
     * If [init] has not been called yet, this logs an error and returns [px] unchanged.
     */
    fun convertPixelsToDp(px: Float): Float {
        val metrics = metrics
        if (metrics == null) {
            Log.e(TAG, "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertPixelsToDp(...). Otherwise conversion does not take place.")
            return px
        }
        return px / metrics.density
    }

    /**
     * Measures the width in pixels that [demoText] takes with [paint].
     *
     * Avoid calling this repeatedly inside drawing code.
     */
    fun calcTextWidth(paint: Paint, demoText: String): Int = paint.measureText(demoText).toInt()

    private val calcTextHeightRect = Rect()

    /**
     * Measures the height in pixels of the glyph bounds of [demoText] with [paint].
     *
     * Avoid calling this repeatedly inside drawing code.
     */
    fun calcTextHeight(paint: Paint, demoText: String): Int {
        val r = calcTextHeightRect
        r.set(0, 0, 0, 0)
        paint.getTextBounds(demoText, 0, demoText.length, r)
        return r.height()
    }

    private val fontMetrics = Paint.FontMetrics()

    /**
     * Returns the line height in pixels of [paint], measured from ascent to descent.
     *
     * @param fontMetrics buffer that receives the font metrics; a shared buffer is used when omitted.
     */
    fun getLineHeight(paint: Paint, fontMetrics: Paint.FontMetrics = this.fontMetrics): Float {
        paint.getFontMetrics(fontMetrics)
        return fontMetrics.descent - fontMetrics.ascent
    }

    /**
     * Returns the extra spacing in pixels between two lines of [paint], the part of the font
     * above the ascent plus the part below the descent.
     *
     * @param fontMetrics buffer that receives the font metrics; a shared buffer is used when omitted.
     */
    fun getLineSpacing(paint: Paint, fontMetrics: Paint.FontMetrics = this.fontMetrics): Float {
        paint.getFontMetrics(fontMetrics)
        return fontMetrics.ascent - fontMetrics.top + fontMetrics.bottom
    }

    /**
     * Measures the glyph bounds of [demoText] with [paint].
     *
     * @return a pooled [FSize] in pixels; recycle it with [FSize.recycleInstance] when done.
     */
    fun calcTextSize(paint: Paint, demoText: String): FSize {
        val result = FSize.getInstance(0f, 0f)
        calcTextSize(paint, demoText, result)
        return result
    }

    private val calcTextSizeRect = Rect()

    /**
     * Measures the glyph bounds of [demoText] with [paint] and writes the width and height in pixels
     * into [outputFSize].
     */
    fun calcTextSize(paint: Paint, demoText: String, outputFSize: FSize) {
        val r = calcTextSizeRect
        r.set(0, 0, 0, 0)
        paint.getTextBounds(demoText, 0, demoText.length, r)
        outputFSize.width = r.width().toFloat()
        outputFSize.height = r.height().toFloat()
    }

    private val POW_10 = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)

    /** Formatter with one decimal place, used by chart components that have no formatter of their own. */
    val defaultValueFormatter: IValueFormatter = DefaultValueFormatter(1)

    /**
     * Formats [number] with a fixed number of decimals, using a comma as the decimal separator.
     *
     * Zero returns "0". Values between -1 and 1 get a leading "0". The result is at most 35 characters.
     *
     * @param digitCount number of decimals to show; values above 9 are limited to 9.
     * @param separateThousands true to insert [separateChar] between groups of three integer digits.
     * @param separateChar the thousands separator, a dot by default.
     */
    fun formatNumber(number: Float, digitCount: Int, separateThousands: Boolean, separateChar: Char = '.'): String {
        val out = CharArray(35)
        var value = number
        var digits = digitCount
        var neg = false

        if (value == 0f) return "0"

        val zero = value < 1 && value > -1

        if (value < 0) {
            neg = true
            value = -value
        }

        if (digits >= POW_10.size) {
            digits = POW_10.size - 1
        }

        value *= POW_10[digits]
        var lval = value.roundToLong()
        var ind = out.size - 1
        var charCount = 0
        var decimalPointAdded = false

        while (lval != 0L || charCount < digits + 1) {
            val digit = (lval % 10).toInt()
            lval /= 10
            out[ind--] = ('0' + digit)
            charCount++

            if (charCount == digits) {
                out[ind--] = ','
                charCount++
                decimalPointAdded = true
            } else if (separateThousands && lval != 0L && charCount > digits) {
                if (decimalPointAdded) {
                    if ((charCount - digits) % 4 == 0) {
                        out[ind--] = separateChar
                        charCount++
                    }
                } else {
                    if ((charCount - digits) % 4 == 3) {
                        out[ind--] = separateChar
                        charCount++
                    }
                }
            }
        }

        if (zero) {
            out[ind--] = '0'
            charCount += 1
        }

        if (neg) {
            out[ind--] = '-'
            charCount += 1
        }

        val start = out.size - charCount
        return String(out, start, out.size - start)
    }

    /**
     * Rounds [number] to one significant digit, for example 123 to 100, 0.0234 to 0.02 and 150 to 200.
     *
     * Infinite, NaN and zero input return 0. Axis renderers use this to pick a readable label interval.
     */
    fun roundToNextSignificant(number: Double): Float {
        if (number.isInfinite() || number.isNaN() || number == 0.0) return 0f

        val d = ceil(log10(if (number < 0) -number else number).toFloat())
        val pw = 1 - d.toInt()
        val magnitude = 10.0.pow(pw).toFloat()
        val shifted = (number * magnitude).roundToLong()
        return shifted / magnitude
    }

    /**
     * Returns how many decimals are needed to show [number] with two digits beyond its magnitude,
     * for example 0 for 100, 2 for 5 and 4 for 0.02.
     *
     * Charts use this to build their default value formatter.
     */
    fun getDecimals(number: Float): Int {
        if (number == 0f) return 0
        val i = roundToNextSignificant(number.toDouble())
        if (i.isInfinite()) return 0
        return ceil(-log10(i)).toInt() + 2
    }

    /**
     * Returns the smallest [Double] that is greater than [d]. Positive infinity is returned unchanged.
     */
    fun nextUp(d: Double): Double {
        if (d == Double.POSITIVE_INFINITY) return d
        val value = d + 0.0
        return java.lang.Double.longBitsToDouble(
            java.lang.Double.doubleToRawLongBits(value) + (if (value >= 0.0) 1L else -1L)
        )
    }

    /**
     * Calculates the point at [dist] pixels from [center] at [angle] degrees, measured clockwise from
     * 3 o'clock.
     *
     * @return a pooled [MPPointF]; recycle it with [MPPointF.recycleInstance] when done.
     */
    fun getPosition(center: MPPointF, dist: Float, angle: Float): MPPointF {
        val p = MPPointF.getInstance(0f, 0f)
        getPosition(center, dist, angle, p)
        return p
    }

    /**
     * Calculates the point at [dist] pixels from [center] at [angle] degrees, measured clockwise from
     * 3 o'clock, and writes it into [outputPoint].
     */
    fun getPosition(center: MPPointF, dist: Float, angle: Float, outputPoint: MPPointF) {
        outputPoint.x = (center.x + dist * cos(Math.toRadians(angle.toDouble()))).toFloat()
        outputPoint.y = (center.y + dist * sin(Math.toRadians(angle.toDouble()))).toFloat()
    }

    /**
     * Clears [tracker] when the pointer that was lifted in [ev] moved in the opposite direction of
     * another pointer, so a pinch does not end in a fling.
     */
    fun velocityTrackerPointerUpCleanUpIfNecessary(ev: MotionEvent, tracker: VelocityTracker) {
        tracker.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
        val upIndex = ev.actionIndex
        val id1 = ev.getPointerId(upIndex)
        val x1 = tracker.getXVelocity(id1)
        val y1 = tracker.getYVelocity(id1)
        for (i in 0 until ev.pointerCount) {
            if (i == upIndex) continue
            val id2 = ev.getPointerId(i)
            val x = x1 * tracker.getXVelocity(id2)
            val y = y1 * tracker.getYVelocity(id2)
            if (x + y < 0) {
                tracker.clear()
                break
            }
        }
    }

    /** Requests a redraw of [view] on the next animation frame. */
    fun postInvalidateOnAnimation(view: View) {
        view.postInvalidateOnAnimation()
    }

    /** Maps [angle] in degrees into the range 0 (inclusive) to 360 (exclusive). */
    fun getNormalizedAngle(angle: Float): Float {
        var normalized = angle
        while (normalized < 0f) normalized += 360f
        return normalized % 360f
    }

    private val drawableBoundsCache = Rect()

    /**
     * Draws [drawable] centered on the pixel position ([x], [y]) at the given size.
     *
     * The drawable keeps its previous bounds origin; the canvas is translated and restored afterwards.
     *
     * @param width width in pixels.
     * @param height height in pixels.
     */
    fun drawImage(canvas: Canvas, drawable: Drawable, x: Int, y: Int, width: Int, height: Int) {
        val drawOffset = MPPointF.getInstance()
        drawOffset.x = (x - width / 2).toFloat()
        drawOffset.y = (y - height / 2).toFloat()

        drawable.copyBounds(drawableBoundsCache)
        drawable.setBounds(
            drawableBoundsCache.left,
            drawableBoundsCache.top,
            drawableBoundsCache.left + width,
            drawableBoundsCache.top + height
        )

        val saveId = canvas.save()
        canvas.translate(drawOffset.x, drawOffset.y)
        drawable.draw(canvas)
        canvas.restoreToCount(saveId)
    }

    private val drawTextRectBuffer = Rect()
    private val fontMetricsBuffer = Paint.FontMetrics()

    /**
     * Draws a single line of text at the pixel position ([x], [y]), optionally rotated.
     *
     * The text alignment of [paint] is temporarily set to left and restored afterwards.
     *
     * @param anchor which point of the text box sits on ([x], [y]), from (0, 0) top left to (1, 1) bottom right.
     * @param angleDegrees rotation around the anchor in degrees, clockwise.
     */
    fun drawXAxisValue(c: Canvas, text: String, x: Float, y: Float, paint: Paint, anchor: MPPointF, angleDegrees: Float) {
        var drawOffsetX = 0f
        var drawOffsetY = 0f

        val lineHeight = paint.getFontMetrics(fontMetricsBuffer)
        paint.getTextBounds(text, 0, text.length, drawTextRectBuffer)

        drawOffsetX -= drawTextRectBuffer.left
        drawOffsetY += -fontMetricsBuffer.ascent

        val originalTextAlign = paint.textAlign
        paint.textAlign = Paint.Align.LEFT

        if (angleDegrees != 0f) {
            drawOffsetX -= drawTextRectBuffer.width() * 0.5f
            drawOffsetY -= lineHeight * 0.5f

            var translateX = x
            var translateY = y

            if (anchor.x != 0.5f || anchor.y != 0.5f) {
                val rotatedSize = getSizeOfRotatedRectangleByDegrees(drawTextRectBuffer.width().toFloat(), lineHeight, angleDegrees)
                translateX -= rotatedSize.width * (anchor.x - 0.5f)
                translateY -= rotatedSize.height * (anchor.y - 0.5f)
                FSize.recycleInstance(rotatedSize)
            }

            c.save()
            c.translate(translateX, translateY)
            c.rotate(angleDegrees)
            c.drawText(text, drawOffsetX, drawOffsetY, paint)
            c.restore()
        } else {
            if (anchor.x != 0f || anchor.y != 0f) {
                drawOffsetX -= drawTextRectBuffer.width() * anchor.x
                drawOffsetY -= lineHeight * anchor.y
            }
            drawOffsetX += x
            drawOffsetY += y
            c.drawText(text, drawOffsetX, drawOffsetY, paint)
        }

        paint.textAlign = originalTextAlign
    }

    /**
     * Draws an already laid out multi-line text at the pixel position ([x], [y]), optionally rotated.
     *
     * The text alignment of [paint] is temporarily set to left and restored afterwards.
     *
     * @param anchor which point of the text box sits on ([x], [y]), from (0, 0) top left to (1, 1) bottom right.
     * @param angleDegrees rotation around the anchor in degrees, clockwise.
     */
    fun drawMultilineText(c: Canvas, textLayout: StaticLayout, x: Float, y: Float, paint: TextPaint, anchor: MPPointF, angleDegrees: Float) {
        var drawOffsetX = 0f
        var drawOffsetY = 0f

        val lineHeight = paint.getFontMetrics(fontMetricsBuffer)
        val drawWidth = textLayout.width.toFloat()
        val drawHeight = textLayout.lineCount * lineHeight

        drawOffsetX -= drawTextRectBuffer.left
        drawOffsetY += drawHeight

        val originalTextAlign = paint.textAlign
        paint.textAlign = Paint.Align.LEFT

        if (angleDegrees != 0f) {
            drawOffsetX -= drawWidth * 0.5f
            drawOffsetY -= drawHeight * 0.5f

            var translateX = x
            var translateY = y

            if (anchor.x != 0.5f || anchor.y != 0.5f) {
                val rotatedSize = getSizeOfRotatedRectangleByDegrees(drawWidth, drawHeight, angleDegrees)
                translateX -= rotatedSize.width * (anchor.x - 0.5f)
                translateY -= rotatedSize.height * (anchor.y - 0.5f)
                FSize.recycleInstance(rotatedSize)
            }

            c.save()
            c.translate(translateX, translateY)
            c.rotate(angleDegrees)
            c.translate(drawOffsetX, drawOffsetY)
            textLayout.draw(c)
            c.restore()
        } else {
            if (anchor.x != 0f || anchor.y != 0f) {
                drawOffsetX -= drawWidth * anchor.x
                drawOffsetY -= drawHeight * anchor.y
            }
            drawOffsetX += x
            drawOffsetY += y
            c.save()
            c.translate(drawOffsetX, drawOffsetY)
            textLayout.draw(c)
            c.restore()
        }

        paint.textAlign = originalTextAlign
    }

    /**
     * Lays out [text] so it wraps within the width of [constrainedToSize] and draws it at the pixel
     * position ([x], [y]), optionally rotated.
     *
     * @param constrainedToSize only the width is used; it is rounded up and at least 1 pixel.
     * @param anchor which point of the text box sits on ([x], [y]), from (0, 0) top left to (1, 1) bottom right.
     * @param angleDegrees rotation around the anchor in degrees, clockwise.
     */
    fun drawMultilineText(c: Canvas, text: String, x: Float, y: Float, paint: TextPaint, constrainedToSize: FSize, anchor: MPPointF, angleDegrees: Float) {
        val width = max(ceil(constrainedToSize.width), 1f).toInt()
        val textLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
        drawMultilineText(c, textLayout, x, y, paint, anchor, angleDegrees)
    }

    /**
     * Returns the bounding box size of [rectangleSize] after rotating it by [degrees].
     *
     * @return a pooled [FSize]; recycle it with [FSize.recycleInstance] when done.
     */
    fun getSizeOfRotatedRectangleByDegrees(rectangleSize: FSize, degrees: Float): FSize {
        return getSizeOfRotatedRectangleByRadians(rectangleSize.width, rectangleSize.height, degrees * FDEG2RAD)
    }

    /**
     * Returns the bounding box size of [rectangleSize] after rotating it by [radians].
     *
     * @return a pooled [FSize]; recycle it with [FSize.recycleInstance] when done.
     */
    fun getSizeOfRotatedRectangleByRadians(rectangleSize: FSize, radians: Float): FSize {
        return getSizeOfRotatedRectangleByRadians(rectangleSize.width, rectangleSize.height, radians)
    }

    /**
     * Returns the bounding box size of a [rectangleWidth] by [rectangleHeight] rectangle rotated by [degrees].
     *
     * @return a pooled [FSize]; recycle it with [FSize.recycleInstance] when done.
     */
    fun getSizeOfRotatedRectangleByDegrees(rectangleWidth: Float, rectangleHeight: Float, degrees: Float): FSize {
        return getSizeOfRotatedRectangleByRadians(rectangleWidth, rectangleHeight, degrees * FDEG2RAD)
    }

    /**
     * Returns the bounding box size of a [rectangleWidth] by [rectangleHeight] rectangle rotated by [radians].
     *
     * @return a pooled [FSize]; recycle it with [FSize.recycleInstance] when done.
     */
    fun getSizeOfRotatedRectangleByRadians(rectangleWidth: Float, rectangleHeight: Float, radians: Float): FSize {
        return FSize.getInstance(
            abs(rectangleWidth * cos(radians)) + abs(rectangleHeight * sin(radians)),
            abs(rectangleWidth * sin(radians)) + abs(rectangleHeight * cos(radians))
        )
    }
}
