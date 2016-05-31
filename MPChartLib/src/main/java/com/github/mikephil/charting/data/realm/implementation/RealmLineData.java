package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.realm.base.RealmUtils;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public class RealmLineData extends LineData {

    public RealmLineData(RealmResults<? extends RealmObject> result, String xPositionField, String xLabelField, List<ILineDataSet> dataSets) {
        super(dataSets);
        //RealmUtils.toXVals(result, xPositionField, xLabelField)
    }
}
