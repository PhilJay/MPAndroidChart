package com.xxmassdeveloper.mpchartexample.notimportant

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.xxmassdeveloper.mpchartexample.R

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
internal class MyAdapter(context: Context, objects: List<ContentItem>?) :
    ArrayAdapter<ContentItem?>(context, 0, objects!!) {
    private val mTypeFaceLight: Typeface
    private val mTypeFaceRegular: Typeface
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val c = getItem(position)
        val holder: ViewHolder
        holder = ViewHolder()
        convertView = if (c != null && c.isSection) {
            LayoutInflater.from(context).inflate(R.layout.list_item_section, null)
        } else {
            LayoutInflater.from(context).inflate(R.layout.list_item, null)
        }
        holder.tvName = convertView.findViewById(R.id.tvName)
        holder.tvDesc = convertView.findViewById(R.id.tvDesc)
        convertView.tag = holder
        if (c != null && c.isSection) holder?.tvName?.setTypeface(mTypeFaceRegular) else holder?.tvName?.setTypeface(
            mTypeFaceLight
        )
        holder?.tvDesc?.setTypeface(mTypeFaceLight)
        holder?.tvName?.text = c?.name
        holder?.tvDesc?.text = c?.desc
        return convertView
    }

    private inner class ViewHolder {
        var tvName: TextView? = null
        var tvDesc: TextView? = null
    }

    init {
        mTypeFaceLight = Typeface.createFromAsset(context.assets, "OpenSans-Light.ttf")
        mTypeFaceRegular = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")
    }
}