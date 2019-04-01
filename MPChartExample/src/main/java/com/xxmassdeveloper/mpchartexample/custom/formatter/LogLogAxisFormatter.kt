package com.xxmassdeveloper.mpchartexample.custom.formatter

import android.util.Log

import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class LogLogAxisFormatter : LargeValueFormatter(), ICodecFormatter {

    /**
     * Transform a data value 'y' to a log log value for the axis
     */
    override fun encode(y: Float): Float {
        val f = Math.log(Math.log(y.toDouble())).toFloat()
        Log.i("LogLogAxisFormatter", String.format("encode %.4f -> %.4f", y, f))
        return f
    }

    /**
     * Transform an axis value 'y' to its data value
     */
    override fun decode(y: Float): Float {
        val f = Math.pow(Math.E, Math.pow(Math.E, y.toDouble())).toFloat()
        Log.i("LogLogAxisFormatter", String.format("decode %.4f -> %.4f", y, Math.pow(Math.E, Math.pow(Math.E, y.toDouble())).toFloat()))
        return f
    }


    override fun encode(ary: FloatArray): FloatArray {
        val result: MutableList<Float> = mutableListOf()
        ary.map {
            result.add(encode(it))
        }
        return result.toFloatArray()
    }

    override fun decode(ary: FloatArray): FloatArray {
        val result: MutableList<Float> = mutableListOf()
        ary.map { y ->
            result.add(decode(y))
        }
        return result.toFloatArray()
    }

    override fun getFormattedValue(value: Float): String {
        return super.getFormattedValue(decode(value))
    }
}

