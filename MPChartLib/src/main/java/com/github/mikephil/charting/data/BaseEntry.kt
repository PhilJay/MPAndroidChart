package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable

abstract class BaseEntry {

    private var _y: Float = 0f
    open var y: Float
        get() = _y
        set(value) {
            _y = value
        }

    var data: Any? = null

    var icon: Drawable? = null

    constructor()

    constructor(y: Float) {
        this._y = y
    }

    constructor(y: Float, data: Any?) : this(y) {
        this.data = data
    }

    constructor(y: Float, icon: Drawable?) : this(y) {
        this.icon = icon
    }

    constructor(y: Float, icon: Drawable?, data: Any?) : this(y) {
        this.icon = icon
        this.data = data
    }
}
