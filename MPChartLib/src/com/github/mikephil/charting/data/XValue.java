package com.github.mikephil.charting.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a x value label, which must contain a title and optionally an image.
 *
 * @author Alex Macrae
 */
public class XValue {
    private final String value;
    private final Bitmap image;

    public XValue(String value) {
        this(value, null);
    }

    public XValue(String value, Bitmap image) {
        this.value = value;
        this.image = image;
    }

    public String getValue() {
        return value;
    }

    public Bitmap getImage() {
        return image;
    }

    public static List<XValue> convertStringList(List<String> xValueStrings) {
        ArrayList<XValue> xValues = new ArrayList<>();
        for (String xValueString : xValueStrings) {
            xValues.add(new XValue(xValueString));
        }

        return xValues;
    }
}
