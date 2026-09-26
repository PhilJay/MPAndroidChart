package com.xxmassdeveloper.mpchartexample.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.databinding.ActivityAwesomedesignBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class SimpleChartDemo : DemoBase() {

    private lateinit var binding: ActivityAwesomedesignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAwesomedesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "SimpleChartDemo"

        binding.pager.offscreenPageLimit = 3
        binding.pager.adapter = PageAdapter(supportFragmentManager)

        AlertDialog.Builder(this)
            .setTitle("This is a ViewPager.")
            .setMessage("Swipe left and right for more awesome design examples!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Named in full because a deprecated import warns even when the class is suppressed.
    @Suppress("DEPRECATION")
    private class PageAdapter(fm: FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> SineCosineFragment.newInstance()
            1 -> ComplexityFragment.newInstance()
            2 -> BarChartFrag.newInstance()
            3 -> ScatterChartFrag.newInstance()
            else -> PieChartFrag.newInstance()
        }

        override fun getCount(): Int = 5
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/fragments/SimpleChartDemo.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
