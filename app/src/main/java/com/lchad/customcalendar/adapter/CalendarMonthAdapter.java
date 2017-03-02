package com.lchad.customcalendar.adapter;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.lchad.customcalendar.R;
import com.lchad.customcalendar.model.CalendarMonth;
import com.lchad.customcalendar.model.ScheduleVo;
import com.lchad.customcalendar.ui.CalendarFragment;
import com.lchad.customcalendar.widget.CalendarBaseView;
import com.lchad.customcalendar.widget.CalendarMonthView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarMonthAdapter extends RecyclingPagerAdapter implements CalendarFragment.OnDataChangedListener {

    private Context mContext;
    private List<CalendarMonth> mDataList;
    private int cellHeight;
    private int marginTop;
    private CalendarBaseView.OnCalendarClickListener mOnCLickListener;
    private CalendarMonthView.OnPageChangeListener mPageChangeListener;
    private Map<Integer, CalendarMonthView> viewMap;
    private Calendar today;
    private Calendar calToSet;
    private int positionToSet;
    private List<ScheduleVo> dataToLoad;
    private boolean isBackToOne;

    public CalendarMonthAdapter(Context context, List<CalendarMonth> list, CalendarBaseView.OnCalendarClickListener clickListener,
        CalendarMonthView.OnPageChangeListener pageChangeListener) {
        this.mContext = context;
        this.mDataList = list;
        today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        mOnCLickListener = clickListener;
        mPageChangeListener = pageChangeListener;
        viewMap = new HashMap<>();
        cellHeight = context.getResources().getDimensionPixelSize(
            R.dimen.calendar_cell_height);
        marginTop = context.getResources().getDimensionPixelSize(
            R.dimen.calendar_marginTop);
    }

    public void setViewSelectedDayByCal(int itemPosition, Calendar cal) {
        calToSet = cal;
        positionToSet = itemPosition;
        if (viewMap.containsKey(itemPosition)) {
            viewMap.get(itemPosition).setSelectedDayByCal(cal);
        }
    }

    public void setViewSelectedDayOne(int itemPosition) {
        isBackToOne = true;
        positionToSet = itemPosition;
        if (viewMap.containsKey(itemPosition)) {
            viewMap.get(itemPosition).setToDayOne();
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
        CalendarMonthView view;
        if (convertView == null) {
            view = new CalendarMonthView(mContext);
        } else {
            view = (CalendarMonthView) convertView;
        }
        viewMap.put(position, view);
        CalendarMonth cal = mDataList.get(position);
        view.setCal(cal);
        view.clearData();
        if (isBackToOne && positionToSet == position) {
            view.setToDayOne();
            isBackToOne = false;
        }
        if (calToSet != null && positionToSet == position) {
            view.setSelectedDayByCal(calToSet);
            calToSet = null;
        }
        if (dataToLoad != null && positionToSet == position) {
            view.notifyDataSetChanged(dataToLoad);
            dataToLoad = null;
        }
        view.setLayoutParams(
            new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, cellHeight * cal.row + marginTop));
        view.setCellHeight(cellHeight);
        view.setOnCalendarClickListener(mOnCLickListener);
        view.setPageChangeListener(mPageChangeListener);
        return view;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    public int getHeight(int position) {
        return mDataList.get(position).row * cellHeight + marginTop;
    }
}
