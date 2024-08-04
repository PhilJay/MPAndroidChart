package com.xxmassdeveloper.mpchartexample.notimportant

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.mikephil.charting.charts.Chart
import com.google.android.material.snackbar.Snackbar
import com.xxmassdeveloper.mpchartexample.R
import java.text.DateFormatSymbols

abstract class DemoBase : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    @JvmField
    protected val parties: Array<String> = arrayOf(
        "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
        "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
        "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
        "Party Y", "Party Z"
    )

    @JvmField
    protected var tfRegular: Typeface? = null

    @JvmField
    protected var tfLight: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        optionMenus.clear()

        tfRegular = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        tfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            for (i in 0 until menu.size()) {
                val menuItem: MenuItem = menu.getItem(i)
                optionMenus.add(menuItem.title.toString())
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToGallery()
            } else {
                Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    protected fun requestStoragePermission(view: View?) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(view!!, "Write permission is required to save image to gallery", Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok) {
                    ActivityCompat.requestPermissions(
                        this@DemoBase,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_STORAGE
                    )
                }
                .show()
        } else {
            Toast.makeText(applicationContext, "Permission Required!", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this@DemoBase, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_STORAGE)
        }
    }

    protected fun saveToGallery(chart: Chart<*>?, name: String) {
        chart?.let {
            if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
                Toast.makeText(applicationContext, "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT).show()
        }
    }

    protected abstract fun saveToGallery()

    companion object {
        private const val PERMISSION_STORAGE = 0
        //  Jan, Feb,... Dec
        val months = DateFormatSymbols().months.toList().map { it.take(3) }
        val optionMenus: MutableList<String> = mutableListOf()
    }
}
