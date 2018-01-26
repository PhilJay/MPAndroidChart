package com.github.mikephil.charting.model

import com.github.mikephil.charting.data.Entry

data class GradientColor(var startColor: Int = android.R.color.holo_blue_dark,
                         var endColor: Int = android.R.color.holo_blue_bright) : Entry()