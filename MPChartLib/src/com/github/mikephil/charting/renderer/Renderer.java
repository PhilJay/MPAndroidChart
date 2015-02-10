
package com.github.mikephil.charting.renderer;


/**
 * Abstract baseclass of all Renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class Renderer {

    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }
}
