package com.xxmassdeveloper.mpchartexample.notimportant;

import android.content.Context;
import android.graphics.Typeface;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ContentItem c = getItem(position);

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            holder.tvNew = (TextView) convertView.findViewById(R.id.tvNew);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNew.setTypeface(mTypeFaceRegular);
        holder.tvName.setTypeface(mTypeFaceLight);
        holder.tvDesc.setTypeface(mTypeFaceLight);

        holder.tvName.setText(c.name);
        holder.tvDesc.setText(c.desc);

        if(c.isNew)
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
