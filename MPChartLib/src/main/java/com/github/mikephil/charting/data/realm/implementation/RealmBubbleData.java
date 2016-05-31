package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.realm.base.RealmUtils;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public class RealmBubbleData extends BubbleData {

    public RealmBubbleData(RealmResults<? extends RealmObject> result, String xPositionField, String xLabelField, List<IBubbleDataSet> dataSets) {
        super(dataSets);
        ////RealmUtils.toXVals(result, xPositionField, xLabelField)
    }
}
