package com.lchad.customcalendar.ui;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lchad.customcalendar.R;
import com.lchad.customcalendar.adapter.CalendarMonthAdapter;
import com.lchad.customcalendar.adapter.CalendarTaskListAdapter;
import com.lchad.customcalendar.adapter.CalendarWeekAdapter;
import com.lchad.customcalendar.model.CalendarMonth;
import com.lchad.customcalendar.model.CalendarWeek;
import com.lchad.customcalendar.model.ScheduleVo;
import com.lchad.customcalendar.util.CommonUtils;
import com.lchad.customcalendar.util.DateUtil;
import com.lchad.customcalendar.widget.CalendarBaseView;
import com.lchad.customcalendar.widget.CalendarHeaderView;
import com.lchad.customcalendar.widget.CalendarMonthView;
import com.lchad.customcalendar.widget.CalendarMoveLayout;
import com.lchad.customcalendar.widget.CalendarTimeView;
import com.lchad.customcalendar.widget.CalendarWeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 数据加载方式 用一个数组保存所有的日程对象，当点击某一日期时，从数组中筛选出当天的日期 并显示
 * <p>如果当前页面加载到数据则使用服务器中加载的数据，如果没有 则使用本地数据</br>
 * 从数据库中加载数据  数据库加载的数据不保存到缓存中</p>
 */
public class CalendarFragment extends Fragment implements CalendarBaseView.OnCalendarClickListener,
        CalendarWeekView.OnItemClickListener, CalendarMonthView.OnPageChangeListener {

    @BindView(R.id.rl_date_detail) RelativeLayout dateContainer;
    @BindView(R.id.moveLayout) CalendarMoveLayout mMoveLayout;
    @BindView(R.id.calendar_header) CalendarHeaderView mCalendarHeader;
    @BindView(R.id.calendar_viewpager) ViewPager mViewPager;
    @BindView(R.id.fl_calendar_container) LinearLayout mContainer;
    @BindView(R.id.schedule_time_view) CalendarTimeView scheduleTimeView;
    @BindView(R.id.tvDayText) TextView tvCurrentSelectedDate;
    @BindView(R.id.tvWeekDay) TextView tvWeekDay;
    @BindView(R.id.empty) RelativeLayout emptyView;

    /**
     * 事项列表
     */
    @BindView(R.id.lvTask) ListView lvTask;

    private CalendarMonthAdapter mMonthAdapter;
    /**
     * 存储 月视图 日历数据
     */
    private List<CalendarMonth> mMonthList = new ArrayList<>();

    /**
     * 当前显示的页数
     */
    private int mCurrentPosition;

    /**
     * 今天所在的月视图的页数
     */
    private int mMonthPosition;
    /**
     * 今天所在的周视图的页数
     */
    private int mWeekPosition;

    /**
     * 事项列表adapter
     */
    private CalendarTaskListAdapter mTaskAdapter;

    //常量，表示当前选中的视图  周视图/月视图
    public static final int VIEW_MONTH = 1;
    public static final int VIEW_WEEK = 2;

    //默认为月视图
    private int mCurrentView = VIEW_MONTH;

    private CalendarWeekAdapter mWeekAdapter;

    /**
     * 保存所有的日程对象
     */
    private List<ScheduleVo> mScheduleList;

    private Map<String, List<ScheduleVo>> mCache;

    private Map<String, List<ScheduleVo>> mCrossCache;

    /**
     * 显示出来的日程列表
     */
    private List<ScheduleVo> mShownScheduleList;

    /**
     * 存储 周视图  日历数据
     */
    private List<CalendarWeek> mWeekList = new ArrayList<>();

    private OnDataChangedListener mDataListener;

    /**
     * 目前选中的日期
     */
    private Calendar mCurSelectedCal;

    /**
     * 今天
     */
    private Calendar today;

    private boolean setDay;

    private int positionToSet;

    private boolean isCross;

    /**
     * 获取一个月的数据
     */
    private void loadMonthSchedule(CalendarMonth cal) {
        isCross = false;
        String monthTime = getKey(cal.year, cal.month);

        if (mCache.containsKey(monthTime)) {
            updateScheduleList(mCache.get(monthTime));
            mDataListener.onDataChanged(mCache.get(monthTime), mCurrentPosition);
        }
    }

    /**
     * 加载一周的数据
     */
    private void loadWeekSchedule(CalendarWeek calendarWeek) {
        String weekTime = getKey(calendarWeek.year, calendarWeek.month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarWeek.endTime);
        if (calendar.get(Calendar.MONTH) == calendarWeek.month) {//在同个月份内
            int position = getCalMonthPosition(calendar);
            if (position != -1) {
                loadMonthSchedule(mMonthList.get(position));
            }
        } else {//跨月
            isCross = true;
            if (mCrossCache.containsKey(weekTime)) {
                updateScheduleList(mCrossCache.get(weekTime));
                mDataListener.onDataChanged(mCrossCache.get(weekTime), mCurrentPosition);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewData();
    }

    private void initViewData() {
        mScheduleList = new ArrayList<>();
        mCache = new HashMap<>();
        mCrossCache = new HashMap<>();
        mShownScheduleList = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        //将默认选中日期设为今天
        mCurSelectedCal = calendar;
        today = calendar;
        mCurSelectedCal.set(Calendar.HOUR_OF_DAY, 9);
        mCurSelectedCal.set(Calendar.MINUTE, 0);
        //获取月视图数据
        for (int i = 2014; i <= 2099; i++) {
            for (int j = Calendar.JANUARY; j <= Calendar.DECEMBER; j++) {
                CalendarMonth date = new CalendarMonth(i, j);
                mMonthList.add(date);
                if (i == calendar.get(Calendar.YEAR) && j == calendar.get(Calendar.MONTH)) {
                    mMonthPosition = mMonthList.size() - 1;
                }
            }
        }
        //获取周视图数据
        Calendar newCalendar = (Calendar) mCurSelectedCal.clone();
        newCalendar.add(Calendar.DAY_OF_MONTH, -50 * 7);
        mWeekPosition = 50;
        for (int i = 1; i <= 100; i++) {
            //加载40周
            CalendarWeek week = new CalendarWeek(newCalendar);
            mWeekList.add(week);
            newCalendar.add(Calendar.DAY_OF_MONTH, 7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.calendar, null);
        ButterKnife.bind(this, view);

        lvTask.setEmptyView(emptyView);

        //设置头部
        mCalendarHeader.changeModeToWeek(today.get(Calendar.DAY_OF_WEEK) - 1);
        scheduleTimeView.setVisibility(View.VISIBLE);

        //初始化viewPager
        mWeekAdapter = new CalendarWeekAdapter(getActivity(), mWeekList, this, this);
        mDataListener = mWeekAdapter;
        mViewPager.setAdapter(mWeekAdapter);
        mViewPager.setCurrentItem(mWeekPosition);
        mCurrentPosition = mWeekPosition;
        mCurrentView = VIEW_WEEK;
        setViewPagerHeight(mWeekAdapter.getHeight());
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        setTextByCalendar();

        mTaskAdapter = new CalendarTaskListAdapter(getActivity(), mShownScheduleList);

        lvTask.setAdapter(mTaskAdapter);

        loadWeekSchedule(mWeekList.get(mWeekPosition));

        return view;
    }

    /**
     * 更新scheduleList
     */
    private void updateScheduleList(List<ScheduleVo> list) {
        mScheduleList.clear();
        mScheduleList.addAll(list);
    }

    /**
     * 设置viewPager高度
     */
    private void setViewPagerHeight(int height) {
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
    }

    /**
     * 设置日期栏
     */
    private void setTextByCalendar() {
        int order = mCurSelectedCal.get(Calendar.DAY_OF_WEEK) - 1;
        if (order < 0) {
            order = 0;
        }

        tvCurrentSelectedDate.setText(
                new StringBuilder()
                        .append(mCurSelectedCal.get(Calendar.YEAR))
                        .append(getResources().getString(R.string.year))
                        .append(mCurSelectedCal.get(Calendar.MONTH) + 1)
                        .append(getResources().getString(R.string.month))
                        .append(mCurSelectedCal.get(Calendar.DAY_OF_MONTH))
                        .append(getResources().getString(R.string.day))
                        .toString());

        tvWeekDay.setText(
                new StringBuilder()
                        .append(getResources().getString(R.string.week_head))
                        .append(getResources().getStringArray(R.array.week)[order])
                        .toString());
    }

    /**
     * 设置回第一天
     */
    public void setBackToToday() {
        Toast.makeText(getActivity(), R.string.go_back_today, Toast.LENGTH_SHORT).show();
        if (mCurrentView == VIEW_MONTH) {
            mViewPager.setCurrentItem(mMonthPosition);
            setViewPagerHeight(mMonthAdapter.getHeight(mMonthPosition));
            mMonthAdapter.setViewSelectedDayByCal(mMonthPosition, today);
        } else {
            mViewPager.setCurrentItem(mWeekPosition);
            mWeekAdapter.setViewSelectedDayOne(mWeekPosition);
            setViewPagerHeight(mWeekAdapter.getHeight());
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int metaState = 0;
            int[] coordinates = new int[2];
            dateContainer.getLocationOnScreen(coordinates);
            float x = CommonUtils.getScreenWidth(getActivity()) / 2;
            float y = coordinates[1] - CommonUtils.getStatusHeight(getActivity()) - getResources().getDimension(R.dimen.title_height) + 15;
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis() + 100;
            MotionEvent motionEventUp = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_UP,
                    x,
                    y,
                    metaState
            );
            mMoveLayout.dispatchTouchEvent(motionEventUp);
        }
    };

    /**
     * 切换视图
     */
    public void switchMonthAndWeek() {
        if (mCurrentView == VIEW_MONTH) {
            Toast.makeText(getActivity(), R.string.change_to_week, Toast.LENGTH_SHORT).show();
            //当前是月 切换到周视图
            mCalendarHeader.changeModeToWeek(today.get(Calendar.DAY_OF_WEEK) - 1);
            scheduleTimeView.setVisibility(View.VISIBLE);
            //EventBus.getDefault().post(new EventScheduleMonthOrWeek(EventScheduleMonthOrWeek.MONTH));
            mCurrentView = VIEW_WEEK;
            //设置viewPager adapter
            mDataListener = mWeekAdapter;
            mViewPager.setAdapter(mWeekAdapter);
            int position = getCalWeekPosition(mCurSelectedCal);
            if (position != -1) {
                mWeekAdapter.setViewSelectedDayByCal(position, mCurSelectedCal);
                setDay = true;
                positionToSet = position;
                mViewPager.setCurrentItem(position);
            }
            setViewPagerHeight(mWeekAdapter.getHeight());
            if (!mViewPager.isShown()) {
                mMoveLayout.scroll(mCalendarHeader.getHeight(),
                        mMonthAdapter.getHeight(mMonthPosition) + mCalendarHeader.getHeight() * 2);
                new Handler().postDelayed(runnable, 200);
            }
        } else {
            Toast.makeText(getActivity(), R.string.change_to_month, Toast.LENGTH_SHORT).show();
            //当前是周 切换到月视图
            mCalendarHeader.changeModeToMonth();
            scheduleTimeView.setVisibility(View.GONE);
            //EventBus.getDefault().post(new EventScheduleMonthOrWeek(EventScheduleMonthOrWeek.WEEK));
            mCurrentView = VIEW_MONTH;
            //设置adapter
            if (mMonthAdapter == null) {
                mMonthAdapter = new CalendarMonthAdapter(getActivity(), mMonthList, this, this);
            }
            //设置viewPager adapter
            mDataListener = mMonthAdapter;
            mViewPager.setAdapter(mMonthAdapter);
            int position = getCalMonthPosition(mCurSelectedCal);
            if (position != -1) {
                mMonthAdapter.setViewSelectedDayByCal(position, mCurSelectedCal);
                setDay = true;
                positionToSet = position;
                mViewPager.setCurrentItem(position);
            }
            if (mMoveLayout.getBaseTop() != 0) {
                mMoveLayout.scroll(mCalendarHeader.getHeight(), mWeekAdapter.getHeight()
                        + mCalendarHeader.getHeight() * 2);
                new Handler().postDelayed(runnable, 200);
            }
        }
    }

    /**
     * 获取当前日期所在页 （月视图）
     */
    private int getCalMonthPosition(Calendar cal) {
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        for (int i = 0; i < mMonthList.size(); i++) {
            if (mMonthList.get(i).month == month && mMonthList.get(i).year == year) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取当前日期所在页 （周视图）
     */
    private int getCalWeekPosition(Calendar cal) {
        for (int i = 0; i < mWeekList.size(); i++) {
            if (mWeekList.get(i).beginTime <= cal.getTimeInMillis() && mWeekList.get(i).endTime >= cal.getTimeInMillis()) {
                return i;
            }
        }
        return -1;
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mScheduleList.clear();
            mCurrentPosition = position;
            if (mCurrentView == VIEW_MONTH) { //月视图
                setViewPagerHeight(mMonthAdapter.getHeight(position));
                mViewPager.invalidate();
                loadMonthSchedule(mMonthList.get(mCurrentPosition));
                if (!setDay) {
                    mMonthAdapter.setViewSelectedDayOne(mCurrentPosition);
                } else if (position == positionToSet) {
                    setDay = false;
                }
            } else { //周视图
                loadWeekSchedule(mWeekList.get(mCurrentPosition));
                if (!setDay) {
                    mWeekAdapter.setViewSelectedDayOne(mCurrentPosition);
                } else if (position == positionToSet) {
                    setDay = false;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private String getKey(int year, int month) {
        return year + "-" + String.format("%02d", month + 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<ScheduleVo> getScheduleList() {
        List<ScheduleVo> scheduleList;
        if (isCross) {
            //跨月
            CalendarWeek cal = mWeekList.get(getCalWeekPosition(mCurSelectedCal));
            scheduleList = mCrossCache.get(getKey(cal.year, cal.month));
        } else {
            scheduleList = mCache.get(DateUtil.calToMonthStr(mCurSelectedCal));
        }
        return scheduleList;
    }

    /**
     * 更新listView数据
     */
    private void getDayShownSchedule() {
        mShownScheduleList.clear();
        List<ScheduleVo> scheduleList = getScheduleList();
        if (scheduleList == null) {
            return;
        }
        long time[] = DateUtil.calToDayTime(mCurSelectedCal);
        Calendar calendar = Calendar.getInstance();
        for (ScheduleVo scheduleVo : scheduleList) {
            calendar.setTimeInMillis(scheduleVo.scheduleTime);
            if (scheduleVo.scheduleTime >= time[0] && scheduleVo.scheduleTime <= time[1]) {
                mShownScheduleList.add(scheduleVo);
            }
        }
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCalendarClick(Calendar cal) {
        mCurSelectedCal = cal;
        mCurSelectedCal.set(Calendar.HOUR_OF_DAY, 9);
        mCurSelectedCal.set(Calendar.MINUTE, 0);
        setTextByCalendar();
        getDayShownSchedule();
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Calendar cal, int start, int end) {
        mCurSelectedCal = cal;
        mCurSelectedCal.set(Calendar.MINUTE, 0);
        if (start == 0) {
            mCurSelectedCal.set(Calendar.HOUR_OF_DAY, 9);
        } else if (start == 12) {
            mCurSelectedCal.set(Calendar.HOUR_OF_DAY, 14);
        } else if (start == 18) {
            mCurSelectedCal.set(Calendar.HOUR_OF_DAY, 19);
        }
        setTextByCalendar();
        mShownScheduleList.clear();
        List<ScheduleVo> scheduleList = getScheduleList();
        if (scheduleList == null) return;
        Calendar calendar = Calendar.getInstance();
        for (ScheduleVo schedule : scheduleList) {
            calendar.setTimeInMillis(schedule.scheduleTime);
            if (!DateUtil.isInTheDay(schedule.scheduleTime, cal)) continue;
            if (calendar.get(Calendar.HOUR_OF_DAY) >= start && calendar.get(Calendar.HOUR_OF_DAY) < end) {
                mShownScheduleList.add(schedule);
            }
        }
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageChange(int upOrDown, Calendar calendar) {
        if (upOrDown == PAGE_UP) {
            mCurrentPosition--;
        } else {
            mCurrentPosition++;
        }
        mMonthAdapter.setViewSelectedDayByCal(mCurrentPosition, calendar);
    }

    /**
     * 获取当前选中时间
     */
    public Calendar getCurrentSelectedCal() {
        return mCurSelectedCal;
    }

    /**
     * 数据加载完成回调接口
     */
    public interface OnDataChangedListener {
        void onDataChanged(List<ScheduleVo> scheduleList, int position);
    }
}
