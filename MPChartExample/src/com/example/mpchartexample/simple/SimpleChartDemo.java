
package com.example.mpchartexample.simple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Demonstrates how to keep your charts straight forward, simple and beautiful with the MPAndroidChart library.
 * 
 * @author Philipp Jahoda
 */
public class SimpleChartDemo extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ViewPager pager = new ViewPager(this);  
        pager.setId(1000);
        pager.setOffscreenPageLimit(3);
        setContentView(pager);
        
        PageAdapter a = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(a);
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
                f = LineChartFrag.newInstance();
                break;
            case 1:
                f = BarChartFrag.newInstance();
                break;
            case 2:
                f = ScatterChartFrag.newInstance();
                break;
            case 3:
                f = PieChartFrag.newInstance();
                break;
            }

            return f;
        }

        @Override
        public int getCount() {
            return 4;
        }       
    }
}
