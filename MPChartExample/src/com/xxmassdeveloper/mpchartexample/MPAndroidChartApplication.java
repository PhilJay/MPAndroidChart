package com.xxmassdeveloper.mpchartexample;

import android.app.Application;
import io.realm.Realm;

public class MPAndroidChartApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		Realm.init(this);
	}
}
