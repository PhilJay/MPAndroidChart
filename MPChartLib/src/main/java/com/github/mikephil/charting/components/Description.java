package com.github.mikephil.charting.components;

import android.graphics.Paint;

import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/** This class details the text, position, and alignment
 * of a {@link com.github.mikephil.charting.charts.Chart}
 * description.
 *
 * Created by Philipp Jahoda on 17/09/16.
 */
public class Description extends ComponentBase {

    /**
     * the text used in the description.
     */
    private String text = "Description Label";

    /**
     * the custom position of the description text.
     */
    private MPPointF mPosition;

    /**
     * the alignment of the description text.
     */
    private Paint.Align mTextAlign = Paint.Align.RIGHT;

    public Description() {
        super();

        // default size
        mTextSize = Utils.convertDpToPixel(8f);
    }

    /**
     * Sets the text to be shown as the description.
     * Never set this to null as this will cause {@link NullPointerException} when drawing with Android Canvas.
     *
     * @param text - the text to be displayed within the description.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the description text.
     *
     * @return the text of the description.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets a custom position for the description text in pixels on the screen.
     *
     * @param x - xcoordinate, the horizontal position of the pixel on the screen.
     * @param y - ycoordinate, the vertical position of the pixel on the screen.
     */
    public void setPosition(float x, float y) {
        if (mPosition == null) {
            mPosition = MPPointF.getInstance(x, y);
        } else {
            mPosition.x = x;
            mPosition.y = y;
        }
    }

    /**
     * Returns the customized position of the description, or null if none set.
     *
     * @return the {@link MPPointF} position.
     */
    public MPPointF getPosition() {
        return mPosition;
    }

    /**
     * Sets the text alignment of the description text. Default RIGHT.
     *
     * @param align - the {@link Paint.Align} object that represents the text alignment.
     */
    public void setTextAlign(Paint.Align align) {
        this.mTextAlign = align;
    }

    /**
     * Returns the text alignment of the description.
     *
     * @return the {@link Paint.Align} object that represents the text alignment.
     */
    public Paint.Align getTextAlign() {
        return mTextAlign;
    }
}
