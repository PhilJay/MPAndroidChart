
package com.github.mikephil.charting.data;

import android.graphics.drawable.shapes.Shape;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.scatter.ShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.SquareShapeRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ShapeRendererHandler;

import java.util.ArrayList;
import java.util.List;

public class ScatterDataSet extends LineScatterCandleRadarDataSet<Entry> implements IScatterDataSet {

    /**
     * the size the scattershape will have, in density pixels
     */
    private float mShapeSize = 15f;

    /**
     * Renderer responsible for rendering this DataSet, default: square
     */
    protected ShapeRenderer mShapeRenderer = new SquareShapeRenderer();

    /**
     * The radius of the hole in the shape (applies to Square, Circle and Triangle)
     * - default: 0.0
     */
    private float mScatterShapeHoleRadius = 0f;

    /**
     * Color for the hole in the shape.
     * Setting to `ColorTemplate.COLOR_NONE` will behave as transparent.
     * - default: ColorTemplate.COLOR_NONE
     */
    private int mScatterShapeHoleColor = ColorTemplate.COLOR_NONE;

    /**
     * Custom path object the user can provide that is drawn where the values
     * are at. This is used when ScatterShape.CUSTOM is set for a DataSet.
     */
    //private Path mCustomScatterPath = null;
    public ScatterDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<Entry> copy() {

        List<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mValues.size(); i++) {
            yVals.add(mValues.get(i).copy());
        }

        ScatterDataSet copied = new ScatterDataSet(yVals, getLabel());
        copied.mDrawValues = mDrawValues;
        copied.mValueColors = mValueColors;
        copied.mColors = mColors;
        copied.mShapeSize = mShapeSize;
        copied.mShapeRenderer = mShapeRenderer;
        copied.mScatterShapeHoleRadius = mScatterShapeHoleRadius;
        copied.mScatterShapeHoleColor = mScatterShapeHoleColor;
        copied.mHighlightLineWidth = mHighlightLineWidth;
        copied.mHighLightColor = mHighLightColor;
        copied.mHighlightDashPathEffect = mHighlightDashPathEffect;

        return copied;
    }

    /**
     * Sets the size in density pixels the drawn scattershape will have. This
     * only applies for non custom shapes.
     *
     * @param size
     */
    public void setScatterShapeSize(float size) {
        mShapeSize = size;
    }

    @Override
    public float getScatterShapeSize() {
        return mShapeSize;
    }

    /**
     * Sets the ScatterShape this DataSet should be drawn with. This will search for an available ShapeRenderer and set this
     * renderer for the DataSet.
     *
     * @param shape
     */
    public void setScatterShape(ScatterChart.ScatterShape shape) {

        ShapeRendererHandler handler = new ShapeRendererHandler();
        mShapeRenderer = handler.getShapeRenderer(shape);
    }

    /**
     * Sets a new ShapeRenderer responsible for drawing this DataSet.
     * This can also be used to set a custom ShapeRenderer aside from the default ones.
     *
     * @param shapeRenderer
     */
    public void setShapeRenderer(ShapeRenderer shapeRenderer) {
        mShapeRenderer = shapeRenderer;
    }

    @Override
    public ShapeRenderer getShapeRenderer() {
        return mShapeRenderer;
    }

    /**
     * Sets the radius of the hole in the shape (applies to Square, Circle and Triangle)
     * Set this to <= 0 to remove holes.
     *
     * @param holeRadius
     */
    public void setScatterShapeHoleRadius(float holeRadius) {
        mScatterShapeHoleRadius = holeRadius;
    }

    @Override
    public float getScatterShapeHoleRadius() {
        return mScatterShapeHoleRadius;
    }

    /**
     * Sets the color for the hole in the shape.
     *
     * @param holeColor
     */
    public void setScatterShapeHoleColor(int holeColor) {
        mScatterShapeHoleColor = holeColor;
    }

    @Override
    public int getScatterShapeHoleColor() {
        return mScatterShapeHoleColor;
    }
}
