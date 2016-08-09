package com.xxmassdeveloper.mpchartexample.realm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.notimportant.ContentItem;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.notimportant.MyAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

        setTitle("Realm.io Examples");

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
        objects.add(new ContentItem("Realm Wiki", "This is the code related to the wiki entry about realm.io on the MPAndroidChart github page."));

        MyAdapter adapter = new MyAdapter(this, objects);

        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);

        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
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
            case 2:
                i = new Intent(this, RealmDatabaseActivityHorizontalBar.class);
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
            case 5:
                i = new Intent(this, RealmDatabaseActivityBubble.class);
                startActivity(i);
                break;
            case 6:
                i = new Intent(this, RealmDatabaseActivityPie.class);
                startActivity(i);
                break;
            case 7:
                i = new Intent(this, RealmDatabaseActivityRadar.class);
                startActivity(i);
                break;
            case 8:
                i = new Intent(this, RealmWikiExample.class);
                startActivity(i);
                break;
        }

        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.realm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://realm.io"));
        startActivity(i);

        return super.onOptionsItemSelected(item);
    }
}
