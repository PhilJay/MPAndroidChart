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

internal class MenuAdapter(context: Context, objects: List<ContentItem<*>?>?) : ArrayAdapter<ContentItem<*>?>(context, 0, objects!!) {
    private val mTypeFaceLight: Typeface = Typeface.createFromAsset(context.assets, "OpenSans-Light.ttf")
    private val mTypeFaceRegular: Typeface = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = getItem(position)

        val holder = ViewHolder()

        val inflatedView = if (c != null && c.isSection) {
            LayoutInflater.from(context).inflate(R.layout.list_item_section, null)
        } else {
            LayoutInflater.from(context).inflate(R.layout.list_item, null)
        }

        holder.tvName = inflatedView.findViewById(R.id.tvName)
        holder.tvDesc = inflatedView.findViewById(R.id.tvDesc)

        inflatedView.tag = holder

        if (c != null && c.isSection)
            holder.tvName?.setTypeface(mTypeFaceRegular)
        else
            holder.tvName?.setTypeface(mTypeFaceLight)
        holder.tvDesc?.setTypeface(mTypeFaceLight)

        holder.tvName?.text = c?.name
        holder.tvDesc?.text = c?.desc

        return inflatedView
    }

    private inner class ViewHolder {
        var tvName: TextView? = null
        var tvDesc: TextView? = null
    }
}
