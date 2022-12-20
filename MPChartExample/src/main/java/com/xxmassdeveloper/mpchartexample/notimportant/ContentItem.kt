package com.xxmassdeveloper.mpchartexample.notimportant

class ContentItem<T : DemoBase> {
    @JvmField
    val name: String
    @JvmField
    val desc: String
    @JvmField
    var isSection = false
    @JvmField
    var clazz: Class<T>? = null

    constructor(n: String) {
        name = n
        desc = ""
        isSection = true
    }

    constructor(n: String, d: String, clazzName: Class<T>) {
        name = n
        desc = d
        clazz = clazzName
    }
}
