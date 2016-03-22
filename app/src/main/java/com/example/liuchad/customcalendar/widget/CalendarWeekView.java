package com.example.liuchad.customcalendar.widget;

/**
 * Created by liuchad on 16/3/21.
 */

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.example.liuchad.customcalendar.R;
import com.example.liuchad.customcalendar.model.CalendarWeek;
import com.example.liuchad.customcalendar.model.ScheduleVo;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarWeekView extends CalendarBaseView {

    private final static int TIME_MORNING_START = 0;

    private final static int TIME_AFTERNOON_START = 12;

    private final static int TIME_NIGHT_START = 18;

    private final static int TIME_DAY_END = 24;

    private final static int COLS = CalendarWeek.DAYS;

    /**
     * 行数
     */
    public final static int ROWS = 4;

    private Map<Integer, Integer> typeMap;

    /**
     * 每个日期（格子）的宽度
     */
    private float mCellWidth;

    /**
     * 日期的高度
     */
    private float mDateHeight;

    /**
     * 子项高度
     */
    private float mItemHeight;

    /**
     * 日历view的右边距
     */
    private float mMarginRight;

    /**
     * 该view对应的日历对象
     */
    private CalendarWeek mCal;

    /**
     * 线画笔
     */
    private Paint mHoriLinePaint;

    private Paint mVerLinePaint;

    private Paint rectPaint;

    /**
     * 子项文字画笔
     */
    private Paint mItemTextPaint;

    private float mCenterX;

    private float mCenterY;

    private float mItemCircleRadius;

    private float mTextHeight;

    private int mItemCurrentSelected;

    private static int itemColor;//子项颜色

    /**
     * 记录下面格子内是否有TYPE_ADVANCE级别的日程.用字体颜色区分.
     */
    private boolean[] prioritySet;

    /**
     * 日历项点击监听
     */
    private OnItemClickListener mItemClickListener;

    /**
     * 设置该view对应的日历对象
     */
    public void setCal(CalendarWeek cal) {
        this.mCal = cal;
    }

    public void setHeight(float dateHeight, float itemHeight) {
        this.mDateHeight = dateHeight;
        this.mItemHeight = itemHeight;
    }

    public CalendarWeekView(Context context) {
        super(context);
        init();
    }

    public CalendarWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        itemColor = getResources().getColor(R.color.circle_color);
        mMarginRight = getResources().getDimension(R.dimen.calendar_dp5);
        mItemCircleRadius = getResources().getDimension(R.dimen.calendar_item_circle_radius);
        mHoriLinePaint = new Paint(mCirclePaint);
        mHoriLinePaint.setColor(getResources().getColor(R.color.line_color));
        mHoriLinePaint.setStyle(Paint.Style.STROKE);
        mHoriLinePaint.setStrokeWidth(1);

        mVerLinePaint = new Paint(mCirclePaint);
        mVerLinePaint.setColor(getResources().getColor(R.color.stroke_color));
        mVerLinePaint.setStyle(Paint.Style.STROKE);
        mVerLinePaint.setStrokeWidth(1);
        mVerLinePaint.setAlpha(99);

        rectPaint = new Paint(mHoriLinePaint);
        rectPaint.setColor(getResources().getColor(R.color.rect_paint_color));
        rectPaint.setStyle(Paint.Style.FILL);
        mItemTextPaint = new Paint(mFontPaint);
        mItemTextPaint.setColor(itemColor);
        mItemTextPaint.setTextSize(getResources().getDimension(R.dimen.calendar_week_item_text_size));
        typeMap = new HashMap<>(28);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    protected void handlerClickEvent(int position) {
        if (mCal == null) return;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(mCal.year, mCal.month, mCal.dates.get(0));
        calendar.add(Calendar.DAY_OF_MONTH, position % 7);
        if (position <= 6) {
            mCurrentSelected = position;
            invalidate();
            if (mCalendarCLickListener != null) {
                mCalendarCLickListener.onCalendarClick(calendar);
            }
        } else if (mItemClickListener != null) {
            mCurrentSelected = position % 7;
            mItemCurrentSelected = position;
            invalidate();
            switch (position / COLS) {
                case 1:
                    mItemClickListener.onItemClick(calendar, TIME_MORNING_START, TIME_AFTERNOON_START);
                    break;
                case 2:
                    mItemClickListener.onItemClick(calendar, TIME_AFTERNOON_START, TIME_NIGHT_START);
                    break;
                case 3:
                    mItemClickListener.onItemClick(calendar, TIME_NIGHT_START, TIME_DAY_END);
                    break;
            }
        }
    }

    /**
     * 清除数据
     */
    public void clearData() {
        typeMap.clear();
    }

    @Override
    protected int getPosition(float x, float y) {
        int column = (int) ((x + mMarginRight * 1.8) / mCellWidth);
        int row;
        if (y <= mDateHeight) {
            row = 0;
        } else {
            row = (int) ((y - mDateHeight) / mItemHeight) + 1;
        }
        return (COLS * row + column);
    }

    /**
     * 设置选中为第一天
     */
    @Override
    public void setToDayOne() {
        mCurrentSelected = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(mCal.year, mCal.month, mCal.dates.get(0));
        if (mCalendarCLickListener != null) {
            mCalendarCLickListener.onCalendarClick(calendar);
        }
        invalidate();
    }

    public void setSelectedDayByCal(Calendar calendar) {
        mCurrentSelected = mCal.dates.indexOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (mCalendarCLickListener != null) {
            mCalendarCLickListener.onCalendarClick(calendar);
        }
        invalidate();
    }

    @Override
    protected void drawCalendar() {
        drawCalLine();
        if (mCellWidth == 0) {
            mCellWidth = getWidth() * 1f / COLS;
            Paint.FontMetricsInt fontMetricsInt = mFontPaint.getFontMetricsInt();
            mTextHeight = (fontMetricsInt.bottom - fontMetricsInt.top) / 2;
        }
        float offsetY = mTextHeight + mMarginTop;
        for (int i = 0; i < mCal.dates.size(); i++) {
            float offsetX = mCellWidth * i + (mCellWidth - mFontPaint.measureText(mCal.dates.get(i) + "")) / 2 - mMarginRight;
            mCenterX = offsetX + mFontPaint.measureText(mCal.dates.get(i) + "") / 2;
            mCenterY = offsetY - mFontPadding;
            if (i == mCurrentSelected) {
                mFontPaint.setColor(choseFontColor);
                drawSelectedDay(offsetX, offsetY, i);
            } else {
                mFontPaint.setColor(inFontColor);
                mCanvas.drawText(mCal.dates.get(i) + "", offsetX, offsetY - mDotMargin / 2, mFontPaint);
            }
            if (mItemCurrentSelected != 0) {
                drawItemRect(mItemCurrentSelected);
            }
        }
        //遍历map
        for (Integer key : typeMap.keySet()) {
            if (key < 7) {
                if (typeMap.get(key) == TYPE_BOTH) {
                    drawCircle(key, 2, grayDotColor);
                } else if (typeMap.get(key) == TYPE_NORMAL) {
                    drawCircle(key, 1, grayDotColor);
                } else {
                    drawCircle(key, 1, redDotColor);
                }
            } else {
                drawItemText(key, typeMap.get(key), prioritySet[key]);
            }
        }
        mItemCurrentSelected = 0;
    }

    private void drawCalLine() {
        mDateHeight = getResources().getDimension(R.dimen.calendar_week_date_height);
        mItemHeight = getResources().getDimension(R.dimen.calendar_week_item_height);
        for (int i = 0; i < ROWS; i++) {
            mCanvas.drawLine(0, mDateHeight + mItemHeight * i, getWidth(), mDateHeight + mItemHeight * i, mHoriLinePaint);
        }
        for (int i = 0; i <= 7; i++) {
            mCanvas.drawLine((getWidth() / 7f) * i, mDateHeight, (getWidth() / 7f) * i, mDateHeight + mItemHeight * 3,
                mVerLinePaint);
        }
    }

    @Override
    public void drawSelectedDay(float offsetX, float offsetY, int position) {
        if (mCal.dates.get(position) == today.get(Calendar.DAY_OF_MONTH)) {
            mStrokePaint.setColor(redDotColor);
        } else {
            mStrokePaint.setColor(getResources().getColor(R.color.stroke_color));
        }
        mCanvas.drawCircle(mCenterX, mCenterY - mDotWidth - mDotMargin / 2, mStrokeWidth / 2, mStrokePaint);
        mCanvas.drawText(mCal.dates.get(position) + "", offsetX, offsetY - mDotMargin / 2, mFontPaint);
        if (mCurrentSelected != 0 &&
            mCal.day == today.get(Calendar.DAY_OF_MONTH) &&
            mCal.month == today.get(Calendar.MONTH) &&
            mCal.year == today.get(Calendar.YEAR)) {
            float x = (mCellWidth - mFontPaint.measureText(mCal.day + "")) / 2 - mMarginRight;
            mFontPaint.setColor(todayFontColor);
            mCanvas.drawText(mCal.day + "", x, offsetY - mDotMargin / 2, mFontPaint);
        }
    }

    private void drawCircle(int position, int count, int color) {
        float y = mTextHeight + mMarginTop - mFontPadding;
        float x = mCellWidth * position + mCellWidth / 2 - mMarginRight;

        if (count == 2) {
            drawCircle(x, y, redDotColor);
        } else {
            drawCircle(x, y, color);
        }
    }

    @Override
    protected void drawCircle(float centerX, float centerY, int color) {
        mDotPaint.setColor(color);
        mCanvas.drawCircle(centerX, centerY + mDotMargin * 2.0f, mDotWidth, mDotPaint);
    }

    private void drawItemText(int position, int count, boolean advance) {
        int row = position / 7;
        if (row == 0) return;
        int column = position % 7;
        if (advance) {
            mItemTextPaint.setColor(getResources().getColor(R.color.theme_blue));
        } else {
            mItemTextPaint.setColor(itemColor);
        }
        float textWidth = mItemTextPaint.measureText(count + "") / 2;
        float offsetX = mCellWidth + mCellWidth * column - mItemCircleRadius - textWidth;
        float offsetY = mMarginTop + mDateHeight + mItemHeight * (row - 1) + mItemHeight / 2 - mFontPadding;
        mCanvas.drawText(count + "", offsetX, offsetY, mItemTextPaint);
    }

    private void drawItemRect(int position) {
        int row = position / 7;
        if (row == 0) return;
        int column = position % 7;
        float centerX = mCellWidth / 2 + mCellWidth * column - mItemCircleRadius / 2;
        float centerY = mMarginTop + mDateHeight + mItemHeight * (row - 1) + mItemHeight / 4 - mTextHeight / 2;
        mCanvas.drawCircle(centerX, centerY, mItemCircleRadius, mHoriLinePaint);

        mCanvas.drawRect(column * ((getWidth() - 6) / 7) + column + 1, (row - 1) * mItemHeight + mDateHeight + 1,
            (column + 1) * ((getWidth() - 6) / 7) + column + 1, row * mItemHeight + mDateHeight, rectPaint);
    }

    private int getItemRow(Calendar cal) {
        if (cal.get(Calendar.HOUR_OF_DAY) < TIME_AFTERNOON_START) {
            return 1;
        }
        if (cal.get(Calendar.HOUR_OF_DAY) < TIME_NIGHT_START) {
            return 2;
        }
        return 3;
    }

    @Override
    public void notifyDataSetChanged(List<ScheduleVo> scheduleList) {
        typeMap.clear();
        if (scheduleList == null) return;
        Calendar cal = Calendar.getInstance();
        int day;
        int row;
        prioritySet = null;
        prioritySet = new boolean[28];
        //更新数据
        for (int i = 0; i < scheduleList.size(); i++) {
            cal.setTimeInMillis(scheduleList.get(i).scheduleTime);
            if (!mCal.dates.contains(cal.get(Calendar.DAY_OF_MONTH))) continue;
            day = mCal.dates.indexOf(cal.get(Calendar.DAY_OF_MONTH));
            row = getItemRow(cal);
            if (typeMap.containsKey(day)) {
                if (typeMap.get(day) != TYPE_BOTH) {
                    int priority = scheduleList.get(i).priority;
                    if (priority == 0 && typeMap.get(day) == TYPE_ADVANCED || priority == 1 && typeMap.get(day) == TYPE_NORMAL) {
                        typeMap.put(day, TYPE_BOTH);
                    }
                }
            } else {
                typeMap.put(day, scheduleList.get(i).priority);
            }
            if (typeMap.containsKey(day + row * 7)) {
                typeMap.put(day + row * 7, typeMap.get(day + row * 7) + 1);
            } else {
                typeMap.put(day + row * 7, 1);
            }
            if (scheduleList.get(i).priority == TYPE_ADVANCED) {
                prioritySet[row * 7 + day] = true;
            }
        }
        invalidate();
    }

    /**
     * 子项点击监听接口
     */
    public interface OnItemClickListener {

        void onItemClick(Calendar cal, int start, int end);
    }
}
