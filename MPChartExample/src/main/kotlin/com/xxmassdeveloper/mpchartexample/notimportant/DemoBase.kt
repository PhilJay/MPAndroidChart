package com.xxmassdeveloper.mpchartexample.notimportant

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.Chart
import com.google.android.material.snackbar.Snackbar
import com.xxmassdeveloper.mpchartexample.R

abstract class DemoBase : AppCompatActivity() {

    protected val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    protected val parties = arrayOf(
        "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
        "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
        "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
        "Party Y", "Party Z"
    )

    protected lateinit var tfRegular: Typeface
    protected lateinit var tfLight: Typeface

    private val storagePermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            saveToGallery()
        } else {
            Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        tfRegular = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        tfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")
    }

    private fun hideStatusBar() {
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        view?.padForWindowInsets()
    }

    protected fun getRandom(range: Float, start: Float): Float {
        return (Math.random() * range).toFloat() + start
    }

    @Suppress("DEPRECATION")
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity)
    }

    protected fun saveChartToGalleryWithPermission(chart: Chart<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            saveToGallery()
        } else {
            requestStoragePermission(chart)
        }
    }

    protected fun requestStoragePermission(view: View) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(view, "Write permission is required to save image to gallery", Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok) {
                    storagePermissionRequest.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                .show()
        } else {
            Toast.makeText(applicationContext, "Permission Required!", Toast.LENGTH_SHORT).show()
            storagePermissionRequest.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    protected fun saveToGallery(chart: Chart<*>, name: String) {
        val message = if (chart.saveToGallery("${name}_${System.currentTimeMillis()}", quality = 70)) "Saving SUCCESSFUL!" else "Saving FAILED!"
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    protected abstract fun saveToGallery()
}
