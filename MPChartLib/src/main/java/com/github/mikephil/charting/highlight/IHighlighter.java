package com.github.mikephil.charting.highlight;

/**
 * Created by philipp on 10/06/16.
 */
public interface IHighlighter
{

    /**
     * Returns a Highlight object corresponding to the given x- and y- touch positions in pixels.
     *
     * @param x
     * @param y
     * @return
     */
    Highlight getHighlight(float x, float y);

    /**
     * Returns a Highlight object corresponding to the given index from the list of highlights.
     *
     * @param index
     * @return
     */
    Highlight getHighlightByIndex(int index);
}
