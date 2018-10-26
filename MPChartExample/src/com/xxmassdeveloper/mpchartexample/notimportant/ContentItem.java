package com.xxmassdeveloper.mpchartexample.notimportant;

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
public class ContentItem {

    String name;
    String desc;
    boolean isNew = false;
    boolean isSection = false;

    public ContentItem(String n) {
        name = n;
        desc = "";
        isSection = true;
    }

    public ContentItem(String n, String d) {
        name = n;
        desc = d;
    }
}
