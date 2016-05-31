package com.github.mikephil.charting.data.realm.base;

/**
 * Created by Philipp Jahoda on 19/12/15.
 */
public final class RealmUtils {

//    /**
//     * Transforms the given Realm-ResultSet into a String array by using the provided xValuesField.
//     *
//     * @param result
//     * @param xPositionField
//     * @param xLabelField
//     * @return
//     */
//    public static List<XAxisValue> toXVals(RealmResults<? extends RealmObject> result, String xPositionField, String xLabelField) {
//
//        List<XAxisValue> xVals = new ArrayList<XAxisValue>();
//
//        for (RealmObject object : result) {
//
//            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
//
//            XAxisValue val = new XAxisValue(dynamicObject.getDouble(xPositionField), dynamicObject.getString(xLabelField));
//            xVals.add(val);
//        }
//
//        return xVals;
//    }
}
