package com.github.mikephil.charting.utils;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Fill
{
    public enum Type
    {
        EMPTY, COLOR, LINEAR_GRADIENT, DRAWABLE
    }

    public enum Direction
    {
        DOWN, UP, RIGHT, LEFT
    }

    /**
     * the type of fill
     */
    private Type mType = Type.EMPTY;

    /**
     * the color that is used for filling
     */
    @Nullable
    private Integer mColor = null;

    private Integer mFinalColor = null;

    /**
     * the drawable to be used for filling
     */
    @Nullable
    protected Drawable mDrawable;

    @Nullable
    private int[] mGradientColors;

    @Nullable
    private float[] mGradientPositions;

    /**
     * transparency used for filling
     */
    private int mAlpha = 255;

    public Fill()
    {
    }

    public Fill(int color)
    {
        this.mType = Type.COLOR;
        this.mColor = color;
        calculateFinalColor();
    }

    public Fill(int startColor, int endColor)
    {
        this.mType = Type.LINEAR_GRADIENT;
        this.mGradientColors = new int[]{startColor, endColor};
    }

    public Fill(@NonNull int[] gradientColors)
    {
        this.mType = Type.LINEAR_GRADIENT;
        this.mGradientColors = gradientColors;
    }

    public Fill(@NonNull int[] gradientColors, @NonNull float[] gradientPositions)
    {
        this.mType = Type.LINEAR_GRADIENT;
        this.mGradientColors = gradientColors;
        this.mGradientPositions = gradientPositions;
    }

    public Fill(@NonNull Drawable drawable)
    {
        this.mType = Type.DRAWABLE;
        this.mDrawable = drawable;
    }

    public Type getType()
    {
        return mType;
    }

    public void setType(Type type)
    {
        this.mType = type;
    }

    @Nullable
    public Integer getColor()
    {
        return mColor;
    }

    public void setColor(int color)
    {
        this.mColor = color;
        calculateFinalColor();
    }

    public int[] getGradientColors()
    {
        return mGradientColors;
    }

    public void setGradientColors(int[] colors)
    {
        this.mGradientColors = colors;
    }

    public float[] getGradientPositions()
    {
        return mGradientPositions;
    }

    public void setGradientPositions(float[] positions)
    {
        this.mGradientPositions = positions;
    }

    public void setGradientColors(int startColor, int endColor)
    {
        this.mGradientColors = new int[]{startColor, endColor};
    }

    public int getAlpha()
    {
        return mAlpha;
    }

    public void setAlpha(int alpha)
    {
        this.mAlpha = alpha;
        calculateFinalColor();
    }

    private void calculateFinalColor()
    {
        if (mColor == null)
        {
            mFinalColor = null;
        } else
        {
            int alpha = (int) Math.floor(((mColor >> 24) / 255.0) * (mAlpha / 255.0) * 255.0);
            mFinalColor = (alpha << 24) | (mColor & 0xffffff);
        }
    }

    public void fillRect(Canvas c, Paint paint,
                         float left, float top, float right, float bottom,
                         Direction gradientDirection)
    {
        switch (mType)
        {
            case EMPTY:
                return;

            case COLOR:
            {
                if (mFinalColor == null) return;

                if (isClipPathSupported())
                {
                    int save = c.save();

                    c.clipRect(left, top, right, bottom);
                    c.drawColor(mFinalColor);

                    c.restoreToCount(save);
                }
                else
                {
                    // save
                    Paint.Style previous = paint.getStyle();
                    int previousColor = paint.getColor();

                    // set
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(mFinalColor);

                    c.drawRect(left, top, right, bottom, paint);

                    // restore
                    paint.setColor(previousColor);
                    paint.setStyle(previous);
                }
            }
            break;

            case LINEAR_GRADIENT:
            {
                if (mGradientColors == null) return;

                LinearGradient gradient = new LinearGradient(
                        (int) (gradientDirection == Direction.RIGHT
                                ? right
                                : gradientDirection == Direction.LEFT
                                ? left
                                : left),
                        (int) (gradientDirection == Direction.UP
                                ? bottom
                                : gradientDirection == Direction.DOWN
                                ? top
                                : top),
                        (int) (gradientDirection == Direction.RIGHT
                                ? left
                                : gradientDirection == Direction.LEFT
                                ? right
                                : left),
                        (int) (gradientDirection == Direction.UP
                                ? top
                                : gradientDirection == Direction.DOWN
                                ? bottom
                                : top),
                        mGradientColors,
                        mGradientPositions,
                        android.graphics.Shader.TileMode.MIRROR);

                paint.setShader(gradient);

                c.drawRect(left, top, right, bottom, paint);
            }
            break;

            case DRAWABLE:
            {
                if (mDrawable == null) return;

                mDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                mDrawable.draw(c);
            }
            break;
        }
    }

    public void fillPath(Canvas c, Path path, Paint paint,
                         @Nullable RectF clipRect)
    {
        switch (mType)
        {
            case EMPTY:
                return;

            case COLOR:
            {
                if (mFinalColor == null) return;

                if (clipRect != null && isClipPathSupported())
                {
                    int save = c.save();

                    c.clipPath(path);
                    c.drawColor(mFinalColor);

                    c.restoreToCount(save);
                }
                else
                {
                    // save
                    Paint.Style previous = paint.getStyle();
                    int previousColor = paint.getColor();

                    // set
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(mFinalColor);

                    c.drawPath(path, paint);

                    // restore
                    paint.setColor(previousColor);
                    paint.setStyle(previous);
                }
            }
            break;

            case LINEAR_GRADIENT:
            {
                if (mGradientColors == null) return;

                LinearGradient gradient = new LinearGradient(
                        0,
                        0,
                        c.getWidth(),
                        c.getHeight(),
                        mGradientColors,
                        mGradientPositions,
                        android.graphics.Shader.TileMode.MIRROR);

                paint.setShader(gradient);

                c.drawPath(path, paint);
            }
            break;

            case DRAWABLE:
            {
                if (mDrawable == null) return;

                ensureClipPathSupported();

                int save = c.save();
                c.clipPath(path);

                mDrawable.setBounds(
                        clipRect == null ? 0 : (int) clipRect.left,
                        clipRect == null ? 0 : (int) clipRect.top,
                        clipRect == null ? c.getWidth() : (int) clipRect.right,
                        clipRect == null ? c.getHeight() : (int) clipRect.bottom);
                mDrawable.draw(c);

                c.restoreToCount(save);
            }
            break;
        }
    }

    private boolean isClipPathSupported()
    {
        return Utils.getSDKInt() >= 18;
    }

    private void ensureClipPathSupported()
    {
        if (Utils.getSDKInt() < 18)
        {
            throw new RuntimeException("Fill-drawables not (yet) supported below API level 18, " +
                    "this code was run on API level " + Utils.getSDKInt() + ".");
        }
    }
}
