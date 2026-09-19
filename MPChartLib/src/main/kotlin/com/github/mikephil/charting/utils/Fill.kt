package com.github.mikephil.charting.utils

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import kotlin.math.floor

/**
 * Describes how an area is filled: with nothing, a solid color, a linear gradient or a drawable.
 *
 * Bar data sets use a list of fills to paint their bars. Pick the constructor for the fill you want;
 * the no-argument constructor creates an empty fill that draws nothing. [type] decides which of the
 * other properties are used when drawing.
 */
open class Fill() {

    /**
     * What a [Fill] paints with: [EMPTY] draws nothing, [COLOR] a solid [color] with [alpha] applied,
     * [LINEAR_GRADIENT] a gradient built from [gradientColors] and [gradientPositions], [DRAWABLE] the
     * [drawable] stretched to the filled area.
     */
    enum class Type {
        EMPTY, COLOR, LINEAR_GRADIENT, DRAWABLE
    }

    /**
     * Direction in which a linear gradient runs across the filled rectangle: [DOWN] from the top edge,
     * [UP] from the bottom edge, [RIGHT] from the left edge and [LEFT] from the right edge.
     */
    enum class Direction {
        DOWN, UP, RIGHT, LEFT
    }

    /** Which kind of fill is drawn. Set by the constructors, [Type.EMPTY] by default. */
    var type = Type.EMPTY

    /** Solid color for [Type.COLOR]. Setting it recomputes the drawn color together with [alpha]. */
    var color: Int? = null
        set(value) {
            field = value
            calculateFinalColor()
        }

    private var finalColor: Int? = null

    /** Drawable for [Type.DRAWABLE]. Its bounds are set to the filled area on each draw. */
    var drawable: Drawable? = null

    /** Colors of the gradient for [Type.LINEAR_GRADIENT], at least two. */
    var gradientColors: List<Int>? = null

    /** Positions from 0 to 1 for each of [gradientColors]; null spreads the colors evenly. */
    var gradientPositions: List<Float>? = null

    /**
     * Opacity applied on top of [color], from 0 to 255. Default 255. Setting it recomputes the drawn color.
     * Only used by [Type.COLOR].
     */
    var alpha = 255
        set(value) {
            field = value
            calculateFinalColor()
        }

    /** Creates a solid color fill. */
    constructor(color: Int) : this() {
        type = Type.COLOR
        this.color = color
    }

    /** Creates a two-color linear gradient fill. */
    constructor(startColor: Int, endColor: Int) : this() {
        type = Type.LINEAR_GRADIENT
        gradientColors = listOf(startColor, endColor)
    }

    /** Creates a linear gradient fill with evenly spread colors. */
    constructor(gradientColors: List<Int>) : this() {
        type = Type.LINEAR_GRADIENT
        this.gradientColors = gradientColors
    }

    /**
     * Creates a linear gradient fill with a position from 0 to 1 for each color.
     */
    constructor(gradientColors: List<Int>, gradientPositions: List<Float>) : this() {
        type = Type.LINEAR_GRADIENT
        this.gradientColors = gradientColors
        this.gradientPositions = gradientPositions
    }

    /** Creates a fill that draws [drawable] stretched to the filled area. */
    constructor(drawable: Drawable) : this() {
        type = Type.DRAWABLE
        this.drawable = drawable
    }

    /** Replaces [gradientColors] with the two given colors. Does not change [type]. */
    fun setGradientColors(startColor: Int, endColor: Int) {
        gradientColors = listOf(startColor, endColor)
    }

    private fun calculateFinalColor() {
        val color = color
        finalColor = if (color == null) {
            null
        } else {
            val alpha = floor(((color ushr 24) / 255.0) * (alpha / 255.0) * 255.0).toInt()
            (alpha shl 24) or (color and 0xffffff)
        }
    }

    /**
     * Fills the rectangle given in pixels on [c].
     *
     * Solid colors clip the canvas to the rectangle; gradients set a shader on [paint] that stays
     * there afterwards. Does nothing for [Type.EMPTY] or when the data for the type is missing.
     *
     * @param gradientDirection direction of a linear gradient; ignored by the other types.
     */
    fun fillRect(c: Canvas, paint: Paint, left: Float, top: Float, right: Float, bottom: Float, gradientDirection: Direction) {
        when (type) {
            Type.EMPTY -> return
            Type.COLOR -> {
                val finalColor = finalColor ?: return
                val save = c.save()
                c.clipRect(left, top, right, bottom)
                c.drawColor(finalColor)
                c.restoreToCount(save)
            }
            Type.LINEAR_GRADIENT -> {
                val gradientColors = gradientColors ?: return
                val gradient = LinearGradient(
                    (if (gradientDirection == Direction.RIGHT) right else left).toInt().toFloat(),
                    (if (gradientDirection == Direction.UP) bottom else top).toInt().toFloat(),
                    (if (gradientDirection == Direction.RIGHT) left else if (gradientDirection == Direction.LEFT) right else left).toInt().toFloat(),
                    (if (gradientDirection == Direction.UP) top else if (gradientDirection == Direction.DOWN) bottom else top).toInt().toFloat(),
                    gradientColors.toIntArray(),
                    gradientPositions?.toFloatArray(),
                    Shader.TileMode.MIRROR
                )
                paint.shader = gradient
                c.drawRect(left, top, right, bottom, paint)
            }
            Type.DRAWABLE -> {
                val drawable = drawable ?: return
                drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                drawable.draw(c)
            }
        }
    }

    /**
     * Fills [path], given in pixels, on [c].
     *
     * Gradients run from the top left to the bottom right of the canvas and set a shader on [paint]
     * that stays there afterwards. Does nothing for [Type.EMPTY] or when the data for the type is missing.
     *
     * @param clipRect for a solid color, non-null clips to the path instead of drawing it with [paint];
     *   for a drawable it sets the drawable bounds, the whole canvas when null.
     */
    fun fillPath(c: Canvas, path: Path, paint: Paint, clipRect: RectF?) {
        when (type) {
            Type.EMPTY -> return
            Type.COLOR -> {
                val finalColor = finalColor ?: return
                if (clipRect != null) {
                    val save = c.save()
                    c.clipPath(path)
                    c.drawColor(finalColor)
                    c.restoreToCount(save)
                } else {
                    val previous = paint.style
                    val previousColor = paint.color
                    paint.style = Paint.Style.FILL
                    paint.color = finalColor
                    c.drawPath(path, paint)
                    paint.color = previousColor
                    paint.style = previous
                }
            }
            Type.LINEAR_GRADIENT -> {
                val gradientColors = gradientColors ?: return
                val gradient = LinearGradient(
                    0f, 0f, c.width.toFloat(), c.height.toFloat(),
                    gradientColors.toIntArray(), gradientPositions?.toFloatArray(), Shader.TileMode.MIRROR
                )
                paint.shader = gradient
                c.drawPath(path, paint)
            }
            Type.DRAWABLE -> {
                val drawable = drawable ?: return
                val save = c.save()
                c.clipPath(path)
                drawable.setBounds(
                    clipRect?.left?.toInt() ?: 0,
                    clipRect?.top?.toInt() ?: 0,
                    clipRect?.right?.toInt() ?: c.width,
                    clipRect?.bottom?.toInt() ?: c.height
                )
                drawable.draw(c)
                c.restoreToCount(save)
            }
        }
    }
}
