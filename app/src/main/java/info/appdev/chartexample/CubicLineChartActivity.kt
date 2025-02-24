package info.appdev.chartexample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import info.appdev.chartexample.DataTools.Companion.getMuchValues
import info.appdev.chartexample.databinding.ActivityLinechartBinding
import info.appdev.chartexample.notimportant.DemoBase

class CubicLineChartActivity : DemoBase(), OnSeekBarChangeListener {
    private var tvX: TextView? = null
    private var tvY: TextView? = null

    private lateinit var binding: ActivityLinechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = "CubicLineChartActivity"

        tvX = findViewById(R.id.tvXMax)
        tvY = findViewById(R.id.tvYMax)

        binding.chart1.setViewPortOffsets(0f, 0f, 0f, 0f)
        binding.chart1.setBackgroundColor(Color.rgb(104, 241, 175))

        // no description text
        binding.chart1.getDescription().isEnabled = false

        // enable touch gestures
        binding.chart1.setTouchEnabled(true)

        // enable scaling and dragging
        binding.chart1.setDragEnabled(true)
        binding.chart1.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chart1.setPinchZoom(false)

        binding.chart1.setDrawGridBackground(false)
        binding.chart1.setMaxHighlightDistance(300f)

        val x = binding.chart1.getXAxis()
        x.isEnabled = false

        val y = binding.chart1.getAxisLeft()
        y.typeface = tfLight
        y.setLabelCount(6, false)
        y.textColor = Color.WHITE
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(false)
        y.axisLineColor = Color.WHITE

        binding.chart1.getAxisRight().isEnabled = false

        // add data
        binding.seekBarY.setOnSeekBarChangeListener(this)
        binding.seekBarX.setOnSeekBarChangeListener(this)

        // lower max, as cubic runs significantly slower than linear
        binding.seekBarX.setMax(700)

        binding.seekBarX.setProgress(45)
        binding.seekBarY.setProgress(100)

        binding.chart1.getLegend().isEnabled = false

        binding.chart1.animateXY(2000, 2000)

        // don't forget to refresh the drawing
        binding.chart1.invalidate()
    }

    private fun setData(count: Int, range: Float) {
        val values = ArrayList<Entry>()
        val sampleValues = getMuchValues(count)

        for (i in 0 until count) {
            val `val` = (sampleValues[i]!!.toFloat() * (range + 1)) + 20
            values.add(Entry(i.toFloat(), `val`))
        }

        val set1: LineDataSet

        if (binding.chart1.data != null &&
            binding.chart1.data!!.dataSetCount > 0
        ) {
            set1 = binding.chart1.data!!.getDataSetByIndex(0) as LineDataSet
            set1.entries = values
            binding.chart1.data!!.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")

            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.cubicIntensity = 0.2f
            set1.setDrawFilled(true)
            set1.setDrawCircles(false)
            set1.lineWidth = 1.8f
            set1.circleRadius = 4f
            set1.setCircleColor(Color.WHITE)
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.color = Color.WHITE
            set1.fillColor = Color.WHITE
            set1.fillAlpha = 100
            set1.setDrawHorizontalHighlightIndicator(false)
            set1.fillFormatter = object : IFillFormatter {
                override fun getFillLinePosition(dataSet: ILineDataSet?, dataProvider: LineDataProvider?): Float {
                    return binding.chart1.axisLeft.axisMinimum
                }
            }

            // create a data object with the data sets
            val data = LineData(set1)
            data.setValueTypeface(tfLight)
            data.setValueTextSize(9f)
            data.setDrawValues(false)

            // set data
            binding.chart1.data = data
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse("https://github.com/AppDevNext/AndroidChart/blob/master/app/src/main/java/com/xxmassdeveloper/mpchartexample/CubicLineChartActivity.java"))
                startActivity(i)
            }

            R.id.actionToggleValues -> {
                for (set in binding.chart1.data!!.dataSets) set.setDrawValues(!set.isDrawValuesEnabled)

                binding.chart1.invalidate()
            }

            R.id.actionToggleHighlight -> {
                if (binding.chart1.data != null) {
                    binding.chart1.data!!.isHighlightEnabled = !binding.chart1.data!!.isHighlightEnabled
                    binding.chart1.invalidate()
                }
            }

            R.id.actionToggleFilled -> {
                val sets = binding.chart1.data!!.getDataSets()

                for (iSet in sets) {
                    val set = iSet as LineDataSet

                    if (set.isDrawFilledEnabled) set.setDrawFilled(false)
                    else set.setDrawFilled(true)
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleCircles -> {
                val sets = binding.chart1.data!!.getDataSets()

                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    if (set.isDrawCirclesEnabled) set.setDrawCircles(false)
                    else set.setDrawCircles(true)
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleCubic -> {
                val sets = binding.chart1.data!!.getDataSets()

                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.CUBIC_BEZIER)
                        LineDataSet.Mode.LINEAR
                    else
                        LineDataSet.Mode.CUBIC_BEZIER
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleStepped -> {
                val sets = binding.chart1.data!!.getDataSets()

                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.STEPPED)
                        LineDataSet.Mode.LINEAR
                    else
                        LineDataSet.Mode.STEPPED
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleHorizontalCubic -> {
                val sets = binding.chart1.data!!.getDataSets()

                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.HORIZONTAL_BEZIER)
                        LineDataSet.Mode.LINEAR
                    else
                        LineDataSet.Mode.HORIZONTAL_BEZIER
                }
                binding.chart1.invalidate()
            }

            R.id.actionTogglePinch -> {
                if (binding.chart1.isPinchZoomEnabled) binding.chart1.setPinchZoom(false)
                else
                    binding.chart1.setPinchZoom(true)

                binding.chart1.invalidate()
            }

            R.id.actionToggleAutoScaleMinMax -> {
                binding.chart1.isAutoScaleMinMaxEnabled = !binding.chart1.isAutoScaleMinMaxEnabled
                binding.chart1.notifyDataSetChanged()
            }

            R.id.animateX -> {
                binding.chart1.animateX(2000)
            }

            R.id.animateY -> {
                binding.chart1.animateY(2000)
            }

            R.id.animateXY -> {
                binding.chart1.animateXY(2000, 2000)
            }

            R.id.actionSave -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery()
                } else {
                    requestStoragePermission(binding.chart1)
                }
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        tvX!!.text = binding.seekBarX.progress.toString()
        tvY!!.text = binding.seekBarY.progress.toString()

        setData(binding.seekBarX.progress, binding.seekBarY.progress.toFloat())

        // redraw
        binding.chart1.invalidate()
    }

    override fun saveToGallery() {
        saveToGallery(binding.chart1, "CubicLineChartActivity")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
