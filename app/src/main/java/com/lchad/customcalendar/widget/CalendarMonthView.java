package com.lchad.customcalendar.widget;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.lchad.customcalendar.R;
import com.lchad.customcalendar.model.CalendarMonth;
import com.lchad.customcalendar.model.ScheduleVo;
import com.lchad.customcalendar.util.DateUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarMonthView extends CalendarBaseView {

    private Map<Integer, Integer> typeMap;

    /**
     * 每一个日期（格子）的高度
     */
    private float mCellHeight;

    /**
     * 每一个日期（格子）的宽度
     */
    private float mCellWidth;

    private static int outFontColor; //月外paintColor

    private CalendarMonth mCal;

    private OnPageChangeListener mPageChangeListener;

    private float mCenterX;
    private float mCenterY;

    private float fontWidth;

    private static float todayX;
    private static float todayY;

    public void setPageChangeListener(OnPageChangeListener listener) {
        this.mPageChangeListener = listener;
    }

    public CalendarMonthView(Context context) {
        super(context);
        init();
    }

    public CalendarMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void clearData() {
        typeMap.clear();
    }

    private void init() {
        typeMap = new HashMap<>(31);
        outFontColor = getResources().getColor(R.color.out_font_color);
    }

    public CalendarMonth getCal() {
        return mCal;
    }

    public void setCal(CalendarMonth cal) {
        this.mCal = cal;
    }

    public void setCellHeight(float cellHeight) {
        this.mCellHeight = cellHeight;
    }

    /**
     * 设置当前选中的天数
     */
    public void setSelectedDayByCal(Calendar calendar) {
        mCurrentSelected = mCal.weekFirst + calendar.get(Calendar.DAY_OF_MONTH);
        if (mCalendarCLickListener != null) {
            mCalendarCLickListener.onCalendarClick(calendar);
        }
        invalidate();
    }

    /**
     * 设置选中为第一天
     */
    @Override
    public void setToDayOne() {
        mCurrentSelected = mCal.weekFirst + 1;
        invalidate();
        Calendar calendar = Calendar.getInstance();
        calendar.set(mCal.year, mCal.month, 1);
        if (mCalendarCLickListener != null) {
            mCalendarCLickListener.onCalendarClick(calendar);
        }
    }

    @Override
    protected void handlerClickEvent(int position) {
        if (mCal == null) return;
        if (mCalendarCLickListener != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mCal.firstDayOfCal);
            calendar.add(Calendar.DAY_OF_MONTH, position - 1);
            if (DateUtil.isValidTime(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mCal.startTime, mCal.endTime)) {
                mCurrentSelected = position;
                invalidate();
                mCalendarCLickListener.onCalendarClick(calendar);
            } else {
                if (mPageChangeListener != null) {
                    if (position <= 6) {
                        mPageChangeListener.onPageChange(OnPageChangeListener.PAGE_UP, calendar);
                    } else {
                        mPageChangeListener.onPageChange(OnPageChangeListener.PAGE_DOWN, calendar);
                    }
                }
            }
        }
    }

    protected int getPosition(float x, float y) {
        int row = (int) (y / mCellHeight);
        int column = (int) (x / mCellWidth + 1);
        return (7 * row + column);
    }

    /**
     * 画整个日历的文本
     */
    @Override
    protected void drawCalendar() {
        if (mCal == null) return;
        mCellWidth = getWidth() / 7f;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mCal.firstDayOfCal);
        for (int i = 1; i <= mCal.days; i++) {
            int x = (i - 1) % 7;
            int y = (i - 1) / 7;
            drawDateText(x, y, calendar, i);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //遍历map
        for (Integer key : typeMap.keySet()) {
            int position = key + mCal.weekFirst;
            if (typeMap.get(key) == TYPE_BOTH) {
                drawCircle(position, 2, grayDotColor);
            } else if (typeMap.get(key) == TYPE_NORMAL) {
                drawCircle(position, 1, grayDotColor);
            } else {
                drawCircle(position, 1, redDotColor);
            }
        }
    }

    /**
     * 画某一日期的文本
     */
    private void drawDateText(int x, int y, Calendar cal, int position) {

        Paint.FontMetricsInt fontMetrics = mFontPaint.getFontMetricsInt();

        if (DateUtil.isValidTime(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), mCal.startTime,
                mCal.endTime)) {
            mFontPaint.setColor(inFontColor);
        } else {
            mFontPaint.setColor(outFontColor);
        }
        String text = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        fontWidth = mFontPaint.measureText(text);

        float baseY = mCellHeight * y + mMarginTop;
        float offsetY = baseY + (fontMetrics.bottom - fontMetrics.top) / 2 + mFontPadding * 2;
        float offsetX = (int) (mCellWidth * x + (mCellWidth - fontWidth) / 2);
        mCenterX = offsetX + fontWidth / 2;
        mCenterY = baseY + mMarginTop - mFontPadding / 2;

        if (todayX == 0 && mCal.month == today.get(Calendar.MONTH) &&
                position == today.get(Calendar.DAY_OF_MONTH) + mCal.weekFirst && mCal.year == today.get(Calendar.YEAR)) {
            todayX = offsetX;
            todayY = offsetY;
            drawToday();
        }

        if (mCurrentSelected == position) {
            drawSelectedDay(offsetX, offsetY, cal.get(Calendar.DAY_OF_MONTH));
        } else {
            if (position != today.get(Calendar.DAY_OF_MONTH) + mCal.weekFirst || mCal.month != today.get(Calendar.MONTH)) {
                mCanvas.drawText(text, offsetX, offsetY - mCellHeight / 7, mFontPaint);
            }
        }
    }

    private void drawToday() {
        if ((mCurrentSelected != today.get(Calendar.DAY_OF_MONTH) + mCal.weekFirst
                && mCal.month == today.get(Calendar.MONTH)
                && mCal.year == today.get(Calendar.YEAR))) {
            mFontPaint.setColor(todayFontColor);
            mCanvas.drawText(today.get(Calendar.DAY_OF_MONTH) + "", todayX, todayY - mCellHeight / 7, mFontPaint);
        }
    }

    @Override
    protected void drawSelectedDay(float offsetX, float offsetY, int day) {
        if (day == today.get(Calendar.DAY_OF_MONTH)) {
            mStrokePaint.setColor(redDotColor);
        } else {
            mStrokePaint.setColor(getResources().getColor(R.color.stroke_color));
        }
        mFontPaint.setColor(choseFontColor);
        mCanvas.drawCircle(mCenterX, mCenterY - mCellHeight / 7, mStrokeWidth / 2, mStrokePaint);
        mCanvas.drawText(day + "", offsetX, offsetY - mCellHeight / 7, mFontPaint);
        if (mCurrentSelected != today.get(Calendar.DAY_OF_MONTH) + mCal.weekFirst
                && mCal.month == today.get(Calendar.MONTH)
                && mCal.year == today.get(Calendar.YEAR)) {
            drawToday();
        }
    }

    private void drawCircle(int position, int count, int color) {
        int col = (position - 1) % 7;
        int raw = (position - 1) / 7;
        float x = (int) (mCellWidth * col + (mCellWidth - fontWidth) / 2) + fontWidth / 2;
        float y = mCellHeight * raw + mMarginTop + mMarginTop - mFontPadding / 2;
        if (count == 2) {
            drawCircle(x, y, redDotColor);
        } else {
            drawCircle(x, y, color);
        }
    }

    @Override
    protected void drawCircle(float centerX, float centerY, int color) {
        mDotPaint.setColor(color);
        mCanvas.drawCircle(centerX, centerY + mDotMargin * 4 / 3 + mFontPadding, mDotWidth, mDotPaint);
    }

    @Override
    public void notifyDataSetChanged(List<ScheduleVo> scheduleList) {
        typeMap.clear();
        if (scheduleList == null) return;
        Calendar cal = Calendar.getInstance();
        int day;
        //更新数据
        for (int i = 0; i < scheduleList.size(); i++) {
            cal.setTimeInMillis(scheduleList.get(i).scheduleTime);
            day = cal.get(Calendar.DAY_OF_MONTH);
            if (typeMap.containsKey(day)) {
                if (typeMap.get(day) == TYPE_BOTH) continue;
                int priority = scheduleList.get(i).priority;
                if (priority == 0 && typeMap.get(day) == TYPE_ADVANCED || priority == 1 && typeMap.get(day) == TYPE_NORMAL) {
                    typeMap.put(day, TYPE_BOTH);
                }
            } else {
                typeMap.put(day, scheduleList.get(i).priority);
            }
        }
        invalidate();
    }

    public interface OnPageChangeListener {

        public static final int PAGE_UP = 1;
        public static final int PAGE_DOWN = 2;

        void onPageChange(int upOrDown, Calendar cal);
    }
}