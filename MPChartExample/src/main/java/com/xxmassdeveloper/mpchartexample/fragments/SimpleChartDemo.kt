package com.xxmassdeveloper.mpchartexample.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

/**
 * Demonstrates how to keep your charts straight forward, simple and beautiful with the MPAndroidChart library.
 *
 * @author Philipp Jahoda
 */
class SimpleChartDemo : DemoBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_awesomedesign)
        title = "SimpleChartDemo"
        val pager = findViewById<ViewPager>(R.id.pager)
        pager.offscreenPageLimit = 3
        val a = PageAdapter(supportFragmentManager)
        pager.adapter = a
        val b = AlertDialog.Builder(this)
        b.setTitle("This is a ViewPager.")
        b.setMessage("Swipe left and right for more awesome design examples!")
        b.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
        b.show()
    }

    private inner class PageAdapter internal constructor(fm: FragmentManager?) :
        FragmentPagerAdapter(
            fm!!
        ) {
        override fun getItem(pos: Int): Fragment {
            var f: Fragment? = null
            when (pos) {
                0 -> f = SineCosineFragment.Companion.newInstance()
                1 -> f = ComplexityFragment.Companion.newInstance()
                2 -> f = BarChartFrag.Companion.newInstance()
                3 -> f = ScatterChartFrag.Companion.newInstance()
                4 -> f = PieChartFrag.Companion.newInstance()
            }
            return f!!
        }

        override fun getCount(): Int {
            return 5
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/fragments/SimpleChartDemo.java")
                startActivity(i)
            }
        }
        return true
    }

    public override fun saveToGallery() { /* Intentionally left empty */
    }
}