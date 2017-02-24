package com.lchad.customcalendar.adapter;

/**
 * Created by liuchad on 16/3/21.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lchad.customcalendar.R;
import com.lchad.customcalendar.model.ScheduleVo;

import java.util.List;

public class CalendarTaskListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScheduleVo> mList;

    public CalendarTaskListAdapter(Context context, List<ScheduleVo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ScheduleVo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_schedule,
                null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder {
        TextView tvTime;
    }
}

