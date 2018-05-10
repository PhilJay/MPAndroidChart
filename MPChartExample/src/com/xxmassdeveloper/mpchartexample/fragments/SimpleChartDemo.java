
package com.xxmassdeveloper.mpchartexample.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

/**
 * Demonstrates how to keep your charts straight forward, simple and beautiful with the MPAndroidChart library.
 * 
 * @author Philipp Jahoda
 */
public class SimpleChartDemo extends DemoBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_awesomedesign);

        ViewPager pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        
        PageAdapter a = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(a);
        
        
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("This is a ViewPager.");
        b.setMessage("Swipe left and right for more awesome design examples!");
        b.setPositiveButton("OK", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();
    }
       
    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm); 
        }

        @Override
        public Fragment getItem(int pos) {  
            Fragment f = null;
            
            switch(pos) {
            case 0:
                f = SineCosineFragment.newInstance();
                break;
            case 1:
                f = ComplexityFragment.newInstance();
                break;
            case 2:
                f = BarChartFrag.newInstance();
                break;
            case 3:
                f = ScatterChartFrag.newInstance();
                break;
            case 4:
                f = PieChartFrag.newInstance();
                break;
            }

            return f;
        }

        @Override
        public int getCount() {
            return 5;
        }       
    }
}
