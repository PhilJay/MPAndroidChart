package com.xxmassdeveloper.mpchartexample.realm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.notimportant.ContentItem;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.notimportant.MyAdapter;

import java.util.ArrayList;

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
public class RealmMainActivity extends DemoBase implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ArrayList<ContentItem> objects = new ArrayList<ContentItem>();

        objects.add(new ContentItem("Line Chart", "Creating a LineChart with Realm.io database"));
        objects.add(new ContentItem("Bar Chart",
                "Creating a BarChart with Realm.io database"));
        objects.add(new ContentItem("Horizontal Bar Chart",
                "Creating a HorizontalBarChart with Realm.io database"));
        objects.add(new ContentItem("Scatter Chart",
                "Creating a ScatterChart with Realm.io database"));
        objects.add(new ContentItem("Candle Stick Chart", "Creating a CandleStickChart with Realm.io database"));
        objects.add(new ContentItem("Bubble Chart", "Creating a BubbleChart with Realm.io database"));
        objects.add(new ContentItem("Pie Chart", "Creating a PieChart with Realm.io database"));
        objects.add(new ContentItem("Radar Chart", "Creating a RadarChart with Realm.io database"));

        MyAdapter adapter = new MyAdapter(this, objects);

        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long arg3) {

        Intent i;

        switch (pos) {
            case 0:
                i = new Intent(this, RealmDatabaseActivityLine.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(this, RealmDatabaseActivityBar.class);
                startActivity(i);
                break;
            case 3:
                i = new Intent(this, RealmDatabaseActivityScatter.class);
                startActivity(i);
                break;
            case 4:
                i = new Intent(this, RealmDatabaseActivityCandle.class);
                startActivity(i);
                break;
            case 6:
                i = new Intent(this, RealmDatabaseActivityPie.class);
                startActivity(i);
                break;
        }
    }
}
