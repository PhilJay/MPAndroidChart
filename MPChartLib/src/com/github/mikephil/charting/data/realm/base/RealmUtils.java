package com.github.mikephil.charting.data.realm.base;

import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealmObject;
import io.realm.RealmObject;
import io.realm.RealmResults;
/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public final class RealmUtils {

    /**
     * Transforms the given Realm-ResultSet into a String array by using the provided xValuesField.
     *
     * @param result
     * @param xValuesField
     * @return
     */
    public static List<String> toXVals(RealmResults<? extends RealmObject> result, String xValuesField) {

        List<String> xVals = new ArrayList<String>();

        for (RealmObject object : result) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            xVals.add(dynamicObject.getString(xValuesField));
        }

        return xVals;
    }
}
