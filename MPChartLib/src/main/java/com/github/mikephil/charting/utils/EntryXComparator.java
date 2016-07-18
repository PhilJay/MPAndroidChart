package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.data.Entry;

import java.util.Comparator;

/**
 * Comparator for comparing Entry-objects by their x-value.
 * Created by philipp on 17/06/15.
 */
public class EntryXComparator implements Comparator<Entry> {
    @Override
    public int compare(Entry entry1, Entry entry2) {
        float diff = entry1.getX() - entry2.getX();

        if (diff == 0f) return 0;
        else {
            if (diff > 0f) return 1;
            else return -1;
        }
    }
}
