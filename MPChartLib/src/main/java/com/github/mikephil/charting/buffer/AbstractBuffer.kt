package com.github.mikephil.charting.buffer

/**
 * Buffer class to boost performance while drawing. Concept: Replace instead of recreate.
 *
 * @param <T> The data the buffer accepts to be fed with.
</T> */
abstract class AbstractBuffer<T>(size: Int) {
    /** index in the buffer  */
    @JvmField
    protected var index: Int = 0

    /** float-buffer that holds the data points to draw, order: x,y,x,y,...  */
    @JvmField
    val buffer: FloatArray

    /** animation phase x-axis  */
    @JvmField
    protected var phaseX: Float = 1f

    /** animation phase y-axis  */
    @JvmField
    protected var phaseY: Float = 1f

    /** indicates from which x-index the visible data begins  */
    protected var from: Int = 0

    /** indicates to which x-index the visible data ranges  */
    protected var to: Int = 0

    init {
        index = 0
        buffer = FloatArray(size)
    }

    /** limits the drawing on the x-axis  */
    fun limitFrom(fromGiven: Int) {
        from = if (fromGiven < 0)
            0
        else
            fromGiven
    }

    /** limits the drawing on the x-axis  */
    fun limitTo(toGiven: Int) {
        to = if (toGiven < 0)
            0
        else
            toGiven
    }

    /**
     * Resets the buffer index to 0 and makes the buffer reusable.
     */
    fun reset() {
        index = 0
    }

    /**
     * Returns the size (length) of the buffer array.
     */
    fun size() = buffer.size

    /**
     * Set the phases used for animations.
     *
     * @param phaseX
     * @param phaseY
     */
    fun setPhases(phaseX: Float, phaseY: Float) {
        this.phaseX = phaseX
        this.phaseY = phaseY
    }

    /**
     * Builds up the buffer with the provided data and resets the buffer-index
     * after feed-completion. This needs to run FAST.
     *
     * @param data
     */
    abstract fun feed(data: T)
}
