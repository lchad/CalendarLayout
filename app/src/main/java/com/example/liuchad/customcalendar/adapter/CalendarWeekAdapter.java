package com.example.liuchad.customcalendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.example.liuchad.customcalendar.R;
import com.example.liuchad.customcalendar.model.CalendarWeek;
import com.example.liuchad.customcalendar.model.ScheduleVo;
import com.example.liuchad.customcalendar.ui.CalendarFragment;
import com.example.liuchad.customcalendar.widget.CalendarWeekView;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuchad on 16/3/21.
 */
public class CalendarWeekAdapter extends RecyclingPagerAdapter implements CalendarFragment.OnDataChangedListener {

    private Context mContext;
    private List<CalendarWeek> mDataList;
    private CalendarWeekView.OnCalendarClickListener mOnCLickListener;
    private CalendarWeekView.OnItemClickListener mItemClickListener;
    private float itemHeight;
    private float dateHeight;
    private Map<Integer, CalendarWeekView> viewMap;
    private boolean isBackToOne;
    private List<ScheduleVo> dataToLoad;
    private Calendar calToSet;
    private int positionToSet = -1;
    private boolean isSet;

    public CalendarWeekAdapter(Context context, List<CalendarWeek> list, CalendarWeekView.OnCalendarClickListener listener,
        CalendarWeekView.OnItemClickListener itemClickListener) {
        this.mContext = context;
        this.mDataList = list;
        mOnCLickListener = listener;
        mItemClickListener = itemClickListener;
        viewMap = new HashMap<>();
        itemHeight = context.getResources().getDimension(R.dimen.calendar_week_item_height);
        dateHeight = context.getResources().getDimension(R.dimen.calendar_week_date_height);
    }

    public void setViewSelectedDayOne(int position) {
        isBackToOne = true;
        positionToSet = position;
        if (viewMap.containsKey(position)) {
            viewMap.get(position).setToDayOne();
        }
    }

    public void setViewSelectedDayByCal(int itemPosition, Calendar cal) {
        calToSet = cal;
        positionToSet = itemPosition;
        if (viewMap.containsKey(itemPosition)) {
            viewMap.get(itemPosition).setSelectedDayByCal(cal);
        }
    }

    @Override
    public void onDataChanged(List<ScheduleVo> scheduleList, int position) {
        dataToLoad = scheduleList;
        positionToSet = position;
        if (viewMap.containsKey(position)) {
            viewMap.get(position).notifyDataSetChanged(scheduleList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        CalendarWeekView view;
        if (convertView == null) {
            view = new CalendarWeekView(mContext);
        } else {
            view = (CalendarWeekView) convertView;
        }
        viewMap.put(position, view);
        CalendarWeek cal = mDataList.get(position);
        view.setCal(cal);
        view.clearData();
        isSet = false;
        if (isBackToOne && positionToSet == position) {
            isBackToOne = false;
            view.setToDayOne();
            isSet = true;
        }
        if (dataToLoad != null && position == positionToSet) {
            view.notifyDataSetChanged(dataToLoad);
            dataToLoad = null;
            isSet = true;
        }
        if (calToSet != null && positionToSet == position) {
            view.setSelectedDayByCal(calToSet);
            calToSet = null;
            isSet = true;
        }
        if (isSet) {
            positionToSet = -1;
        }
        view.setLayoutParams(
            new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) (itemHeight * 3 + dateHeight)));
        view.setHeight(dateHeight, itemHeight);
        view.setOnCalendarClickListener(mOnCLickListener);
        view.setOnItemClickListener(mItemClickListener);
        return view;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    public int getHeight() {
        return (int) (itemHeight * 3 + dateHeight);
    }
}
