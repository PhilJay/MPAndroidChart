package com.xxmassdeveloper.mpchartexample.notimportant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xxmassdeveloper.mpchartexample.R;

import java.util.List;

/**
 * Created by philipp on 07/12/15.
 */
public class MyAdapter extends ArrayAdapter<ContentItem> {

    public MyAdapter(Context context, List<ContentItem> objects) {
        super(context, 0, objects);
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

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(c.name);
        holder.tvDesc.setText(c.desc);

        return convertView;
    }

    private class ViewHolder {

        TextView tvName, tvDesc;
    }
}
