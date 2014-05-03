
package com.example.mpchartexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main); 
        
        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);
        Button btn5 = (Button) findViewById(R.id.button5);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        
        Intent i;
        
        switch(v.getId()) {
            case R.id.button1:
                i = new Intent(this, LineChartActivity.class);
                startActivity(i);
                break;
            case R.id.button2:
                i = new Intent(this, BarChartActivity.class);
                startActivity(i);
                break;
            case R.id.button3:
                i = new Intent(this, PieChartActivity.class);
                startActivity(i);
                break;
            case R.id.button4:
                i = new Intent(this, MultipleChartsActivity.class);
                startActivity(i);
                break;
            case R.id.button5:
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PhilJay/MPAndroidChart"));
                startActivity(i);
                break;
        }
    }
}
