
package com.xxmassdeveloper.mpchartexample.notimportant;

import android.support.v4.app.FragmentActivity;

import com.xxmassdeveloper.mpchartexample.R;

/**
 * Baseclass of all Activities of the Demo Application.
 * 
 * @author Philipp Jahoda
 */
public abstract class DemoBase extends FragmentActivity {

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };


//    protected ArrayList<XAxisValue> getMonths() {
//
//        ArrayList<XAxisValue> m = new ArrayList<XAxisValue>();
//        m.add(new XAxisValue(0, "Jan"));
//        m.add(new XAxisValue(1, "Feb"));
//        m.add(new XAxisValue(2, "Mar"));
//        m.add(new XAxisValue(3, "Apr"));
//        m.add(new XAxisValue(4, "May"));
//        m.add(new XAxisValue(5, "Jun"));
//        m.add(new XAxisValue(6, "Jul"));
//        m.add(new XAxisValue(7, "Aug"));
//        m.add(new XAxisValue(8, "Sep"));
//        m.add(new XAxisValue(9, "Okt"));
//        m.add(new XAxisValue(10, "Nov"));
//        m.add(new XAxisValue(11, "Dec"));
//
//        return m;
//    }
//
//
//    protected ArrayList<XAxisValue> getQuarters() {
//
//        ArrayList<XAxisValue> q = new ArrayList<XAxisValue>();
//        q.add(new XAxisValue(0, "Quarter 1"));
//        q.add(new XAxisValue(1, "Quarter 2"));
//        q.add(new XAxisValue(2, "Quarter 3"));
//        q.add(new XAxisValue(3, "Quarter 4"));
//
//        return q;
//    }
//
//    protected List<XAxisValue> getYears() {
//        ArrayList<XAxisValue> years = new ArrayList<XAxisValue>();
//
//        years.add(new XAxisValue(0, "2013"));
//        years.add(new XAxisValue(1, "2014"));
//        years.add(new XAxisValue(2, "2015"));
//        years.add(new XAxisValue(3, "2016"));
//        years.add(new XAxisValue(4, "2017"));
//        years.add(new XAxisValue(5, "2018"));
//        years.add(new XAxisValue(6, "2019"));
//
//        return years;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
