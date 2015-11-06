package com.xxmassdeveloper.mpchartexample.notimportant;

import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Philipp Jahoda on 05/11/15.
 */
public abstract class RealmBaseActivity extends DemoBase {

    protected Realm mRealm;

    @Override
    protected void onResume() {
        super.onResume();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("myrealm.realm")
                .build();

        Realm.deleteRealm(config);

        Realm.setDefaultConfiguration(config);

        mRealm = Realm.getInstance(config);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRealm.close();
    }

    protected void writeToDB(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for(int i = 0; i < objectCount; i++) {

            RealmDemoData d = new RealmDemoData(30f + (float) (Math.random() * 100.0), i, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }
}
