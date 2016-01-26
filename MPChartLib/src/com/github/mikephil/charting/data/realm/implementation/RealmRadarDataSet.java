package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.realm.base.RealmLineRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmRadarDataSet<T extends RealmObject> extends RealmLineRadarDataSet<T> implements IRadarDataSet {

    /**
     * Constructor for creating a RadarDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     */
    public RealmRadarDataSet(RealmResults<T> result, String yValuesField) {
        super(result, yValuesField);

        build(this.results);
        calcMinMax(0, results.size());
    }

    /**
     * Constructor for creating a RadarDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     * @param xIndexField  the name of the field in your data object that represents the x-index
     */
    public RealmRadarDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super(result, yValuesField, xIndexField);

        build(this.results);
        calcMinMax(0, results.size());
    }

    @Override
    public void build(RealmResults<T> results) {
        super.build(results);
    }
}
