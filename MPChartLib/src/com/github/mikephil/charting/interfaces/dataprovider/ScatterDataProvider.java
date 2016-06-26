package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.renderer.scatter.ShapeRenderer;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();

    /**
     * Adds a new ShapeRenderer and the shapeIdentifier it is responsible for drawing.
     * This shapeIdentifier should correspond to a DataSet with the same identifier.
     *
     * @param shapeRenderer
     * @param shapeIdentifier
     */
    void addShapeRenderer(ShapeRenderer shapeRenderer, String shapeIdentifier);

    /**
     * Returns the corresponding ShapeRenderer for the given identifier.
     *
     * @param shapeIdentifier
     * @return
     */
    ShapeRenderer getShapeRenderer(String shapeIdentifier);
}
