package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.realm.base.RealmUtils;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public class RealmPieData extends PieData {

    public RealmPieData(RealmResults<? extends RealmObject> result,String xPositionField, String xLabelField, IPieDataSet dataSet) {
        super(dataSet);
        //RealmUtils.toXVals(result, xPositionField, xLabelField)
    }
}
