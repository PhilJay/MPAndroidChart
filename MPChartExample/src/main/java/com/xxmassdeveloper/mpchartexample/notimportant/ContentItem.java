package com.xxmassdeveloper.mpchartexample.notimportant;

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
class ContentItem {

    final String name;
    final String desc;
    final boolean isSection;
    final int id;
    final Class klass;

    ContentItem(String n, int id) {
        name = n;
        desc = "";
        isSection = true;
        this.id = id;
        this.klass = null;
    }

    ContentItem(String n, String d, int id, Class klazz) {
        name = n;
        desc = d;
        isSection = false;
        this.id = id;
        this.klass = klazz;
    }
}
