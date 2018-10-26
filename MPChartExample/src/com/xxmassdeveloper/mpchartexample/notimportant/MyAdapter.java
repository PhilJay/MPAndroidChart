package com.xxmassdeveloper.mpchartexample.notimportant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xxmassdeveloper.mpchartexample.R;

import java.util.List;

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
public class MyAdapter extends ArrayAdapter<ContentItem> {

    private Typeface mTypeFaceLight;
    private Typeface mTypeFaceRegular;

    public MyAdapter(Context context, List<ContentItem> objects) {
        super(context, 0, objects);

        mTypeFaceLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
        mTypeFaceRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ContentItem c = getItem(position);

        ViewHolder holder;

        holder = new ViewHolder();

        if (c != null && c.isSection) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_section, null);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
        }

        holder.tvName = convertView.findViewById(R.id.tvName);
        holder.tvDesc = convertView.findViewById(R.id.tvDesc);
        holder.tvNew = convertView.findViewById(R.id.tvNew);

        convertView.setTag(holder);

        holder.tvNew.setTypeface(mTypeFaceRegular);
        if (c != null && c.isSection)
            holder.tvName.setTypeface(mTypeFaceRegular);
        else
            holder.tvName.setTypeface(mTypeFaceLight);
        holder.tvDesc.setTypeface(mTypeFaceLight);

        holder.tvName.setText(c != null ? c.name : null);
        holder.tvDesc.setText(c != null ? c.desc : null);

        if(c != null && c.isNew)
            holder.tvNew.setVisibility(View.VISIBLE);
        else
            holder.tvNew.setVisibility(View.GONE);

        return convertView;
    }

    private class ViewHolder {

        TextView tvName, tvDesc;
        TextView tvNew;
    }
}
