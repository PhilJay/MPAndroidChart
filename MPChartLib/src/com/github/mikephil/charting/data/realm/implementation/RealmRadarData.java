package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.realm.base.RealmUtils;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public class RealmRadarData extends RadarData{

    public RealmRadarData(RealmResults<? extends RealmObject> result, String xPositionField, String xLabelField, List<IRadarDataSet> dataSets) {
        super(RealmUtils.toXVals(result, xPositionField, xLabelField), dataSets);
    }
}
