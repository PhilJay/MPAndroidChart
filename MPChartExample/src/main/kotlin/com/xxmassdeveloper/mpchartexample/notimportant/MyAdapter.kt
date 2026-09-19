package com.xxmassdeveloper.mpchartexample.notimportant

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.xxmassdeveloper.mpchartexample.databinding.ListItemBinding
import com.xxmassdeveloper.mpchartexample.databinding.ListItemSectionBinding

class MyAdapter(context: Context, objects: List<ContentItem>) : ArrayAdapter<ContentItem>(context, 0, objects) {

    private val typeFaceLight: Typeface = Typeface.createFromAsset(context.assets, "OpenSans-Light.ttf")
    private val typeFaceRegular: Typeface = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val inflater = LayoutInflater.from(context)

        return if (item != null && item.isSection) {
            val binding = ListItemSectionBinding.inflate(inflater, parent, false)
            binding.tvName.typeface = typeFaceRegular
            binding.tvDesc.typeface = typeFaceLight
            binding.tvName.text = item.name
            binding.tvDesc.text = item.desc
            binding.root
        } else {
            val binding = ListItemBinding.inflate(inflater, parent, false)
            binding.tvName.typeface = typeFaceLight
            binding.tvDesc.typeface = typeFaceLight
            binding.tvName.text = item?.name
            binding.tvDesc.text = item?.desc
            binding.root
        }
    }
}
