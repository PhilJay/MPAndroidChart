package com.github.mikephil.charting.model;

import com.github.mikephil.charting.utils.Fill;

/**
 * Deprecated. Use `Fill`
 */
@Deprecated
public class GradientColor extends Fill
{
    /**
     * Deprecated. Use `Fill.getGradientColors()`
     */
    @Deprecated
    public int getStartColor()
    {
        return getGradientColors()[0];
    }

    /**
     * Deprecated. Use `Fill.setGradientColors(...)`
     */
    @Deprecated
    public void setStartColor(int startColor)
    {
        if (getGradientColors() == null || getGradientColors().length != 2)
        {
            setGradientColors(new int[]{
                    startColor,
                    getGradientColors() != null && getGradientColors().length > 1
                            ? getGradientColors()[1]
                            : 0
            });
        } else
        {
            getGradientColors()[0] = startColor;
        }
    }

    /**
     * Deprecated. Use `Fill.getGradientColors()`
     */
    @Deprecated
    public int getEndColor()
    {
        return getGradientColors()[1];
    }

    /**
     * Deprecated. Use `Fill.setGradientColors(...)`
     */
    @Deprecated
    public void setEndColor(int endColor)
    {
        if (getGradientColors() == null || getGradientColors().length != 2)
        {
            setGradientColors(new int[]{
                    getGradientColors() != null && getGradientColors().length > 0
                            ? getGradientColors()[0]
                            : 0,
                    endColor
            });
        } else
        {
            getGradientColors()[1] = endColor;
        }
    }

}
