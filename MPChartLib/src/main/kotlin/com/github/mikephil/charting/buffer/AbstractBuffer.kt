package com.github.mikephil.charting.buffer

/**
 * Reusable float buffer that holds the coordinates to draw, so drawing does not allocate: replace instead of
 * recreate. [feed] fills [buffer] from data of type [T]; the subclass defines the layout of the floats.
 *
 * @param size number of floats in the buffer
 */
public abstract class AbstractBuffer<T>(size: Int) {

    /**
     * Write position of the next float in [buffer].
     */
    protected var index: Int = 0

    /**
     * The coordinates to draw, in the layout defined by the subclass (see [BarBuffer]). Renderers fill it in value
     * space and convert it to pixels in place.
     */
    public val buffer: FloatArray = FloatArray(size)

    /**
     * Animation phase along x (0 to 1). Default 1.
     */
    protected var phaseX: Float = 1f

    /**
     * Animation phase along y (0 to 1); scales the fed values. Default 1.
     */
    protected var phaseY: Float = 1f

    /**
     * Moves the write position back to the start so the buffer can be fed again.
     */
    public fun reset() {
        index = 0
    }

    /**
     * Number of floats in [buffer].
     */
    public val size: Int
        get() = buffer.size

    /**
     * Sets the x and y animation phases (0 to 1) used by the next [feed].
     */
    public fun setPhases(phaseX: Float, phaseY: Float) {
        this.phaseX = phaseX
        this.phaseY = phaseY
    }

    /**
     * Fills the buffer from [data], starting at the current write position.
     */
    public abstract fun feed(data: T)
}
