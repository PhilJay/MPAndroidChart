
package com.github.mikephil.charting.buffer;

import java.util.ArrayList;

/**
 * Buffer class to boost performance while drawing.
 * 
 * @author Philipp Jahoda
 * @param <T>
 */
public abstract class AbstractBuffer<T> {

    protected int index = 0;
    public final float[] buffer;

    /**
     * Initialization with buffer-size.
     * 
     * @param size
     */
    public AbstractBuffer(int size) {
        index = 0;
        buffer = new float[size];
    }

    /**
     * Resets the buffer index to 0.
     */
    public void reset() {
        index = 0;
    }

    /**
     * Returns the size of the buffer array.
     * 
     * @return
     */
    public int size() {
        return buffer.length;
    }

    /**
     * Builds up the buffer with the provided data and resets the buffer-index
     * after feed-completion.
     * 
     * @param entries
     */
    public abstract void feed(ArrayList<T> entries);
}
