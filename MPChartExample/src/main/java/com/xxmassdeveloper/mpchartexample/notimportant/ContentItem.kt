package com.xxmassdeveloper.mpchartexample.notimportant

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
internal class ContentItem {
    val name: String
    val desc: String
    var isSection = false

    constructor(n: String) {
        name = n
        desc = ""
        isSection = true
    }

    constructor(n: String, d: String) {
        name = n
        desc = d
    }
}