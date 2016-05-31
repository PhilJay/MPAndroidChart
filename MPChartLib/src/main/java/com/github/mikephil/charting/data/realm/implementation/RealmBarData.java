package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.realm.base.RealmUtils;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public class RealmBarData extends BarData {

    public RealmBarData(RealmResults<? extends RealmObject> result, String xPositionField, String xLabelField, List<IBarDataSet> dataSets) {
        super(dataSets);
        //RealmUtils.toXVals(result, xPositionField, xLabelField)
    }
}
