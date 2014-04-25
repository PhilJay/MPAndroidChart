
package com.example.mpchartexample;

import android.app.Activity;
import android.content.Intent;
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
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
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
                i = new Intent(this, MultipleChartsActivity.class);
                startActivity(i);
                break;
        }
    }
}
