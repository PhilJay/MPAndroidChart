package com.github.mikephil.charting.renderer.ShapeRenders;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 10:12
 */
public class ScatterShape {


    public static final String SQUARE = "SQUARE";
    public static final String CIRCLE = "CIRCLE";
    public static final String TRIANGLE = "TRIANGLE";
    public static final String CROSS = "CROSS";
    public static final String X = "X";
    public static final String CHEVRON_UP = "CHEVRON_UP";
    public static final String CHEVRON_DOWN = "CHEVRON_DOWN";

    private static Map<String, String> scatterShapeNames;

    public static Map<String, String> getScatterShapeNames() {
        if (scatterShapeNames == null) {
            scatterShapeNames = new HashMap<>();
            scatterShapeNames.put(SQUARE, SQUARE);
            scatterShapeNames.put(CIRCLE, CIRCLE);
            scatterShapeNames.put(TRIANGLE, TRIANGLE);
            scatterShapeNames.put(CROSS, CROSS);
            scatterShapeNames.put(X, X);
            scatterShapeNames.put(CHEVRON_UP, CHEVRON_UP);
            scatterShapeNames.put(CHEVRON_DOWN, CHEVRON_DOWN);
        }
        return scatterShapeNames;
    }

    public static String[] getAllPossibleShapes() {
        String[] possibleShapes = new String[getScatterShapeNames().size()];
        for (int i = 0; i < getScatterShapeNames().size(); i++) {
            possibleShapes[i] = getScatterShapeNames().get(i);
        }
        return possibleShapes;
    }
}
