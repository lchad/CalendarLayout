package com.lchad.customcalendar.widget;

/**
 * Created by liuchad on 16/3/21.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lchad.customcalendar.R;
import com.lchad.customcalendar.model.ScheduleVo;

import java.util.Calendar;
import java.util.List;

public abstract class CalendarBaseView extends View {

    protected static final int TYPE_NORMAL = 0;
    protected static final int TYPE_ADVANCED = 1;
    protected static final int TYPE_BOTH = 2;

    /**
     * 文字画笔。画日期
     */
    protected Paint mFontPaint;

    protected Canvas mCanvas;

    /**
     * 各种字体的颜色
     */
    protected static int inFontColor; //月内
    protected static int choseFontColor; //选中时
    protected static int todayFontColor; //今天

    /**
     * 表示事项的点
     */
    protected static int whiteDotColor; //白点
    protected static int redDotColor;  //红点
    protected static int grayDotColor;
    protected static int grayTextColor;

    protected Calendar today;

    /**
     * 圆圈画笔，用来画空心圆圈
     */
    protected Paint mCirclePaint;

    /**
     * 圆圈画笔，用来画实心圆圈
     */
    protected Paint mStrokePaint;

    /**
     * 圆点画笔，用来画圆点
     */
    protected Paint mDotPaint;

    protected float mStrokeWidth;

    protected float mDotWidth;

    protected float mDotMargin;

    /**
     * 文字的内间距
     */
    protected int mFontPadding;

    protected float mMarginTop;

    private int downPosition;

    protected int mCurrentSelected;

    public CalendarBaseView(Context context) {
        super(context);
        initView();
    }

    public CalendarBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化画笔 和date数据
     */
    private void initView() {

        mFontPadding = getResources().getDimensionPixelSize(R.dimen.calendar_left_padding);
        mMarginTop = getResources().getDimension(R.dimen.calendar_marginTop);
        mStrokeWidth = getResources().getDimension(R.dimen.calendar_stroke_width);
        mDotWidth = getResources().getDimension(R.dimen.calendar_dot_width);
        mDotMargin = getResources().getDimension(R.dimen.calendar_dot_margin);

        inFontColor = getResources().getColor(R.color.stroke_color);
        choseFontColor = getResources().getColor(R.color.chosen_font_color);
        todayFontColor = getResources().getColor(R.color.theme_pink);
        whiteDotColor = getResources().getColor(R.color.white_dot_color);
        grayTextColor = getResources().getColor(R.color.gray_text_color);
        redDotColor = getResources().getColor(R.color.theme_pink);
        grayDotColor = getResources().getColor(R.color.gray_dot_color);

        mFontPaint = new Paint();
        mFontPaint.setColor(inFontColor);
        mFontPaint.setAntiAlias(true);
        mFontPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.calendar_text_size));

        mCirclePaint = new Paint();
        mCirclePaint.setColor(getResources().getColor(R.color.stroke_color));
        mCirclePaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.calendar_circle_width));

        mStrokePaint = new Paint();
        mStrokePaint.setColor(getResources().getColor(R.color.stroke_circle_color));
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.FILL);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        mDotPaint = new Paint();
        mDotPaint.setColor(grayTextColor);
        mDotPaint.setAntiAlias(true);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setStrokeWidth(mDotWidth);

        if (today == null) {
            today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        //画日历
        drawCalendar();
    }

    /**
     * 设置选中第一天
     */
    protected abstract void setToDayOne();

    /**
     * 画整个日历
     */
    protected abstract void drawCalendar();

    /**
     * 捕捉点击事件，并通过回调接口传出
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPosition = getPosition(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upPosition = getPosition(x, y);
                if (upPosition == downPosition) {
                    handlerClickEvent(upPosition);
                }
                downPosition = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                downPosition = 0;
                break;
            default:
                break;
        }
        return true;
    }

    protected abstract int getPosition(float x, float y);

    protected abstract void handlerClickEvent(int position);

    protected abstract void drawSelectedDay(float offsetX, float offsetY, int day);

    protected abstract void drawCircle(float centerX, float centerY, int color);

    public abstract void notifyDataSetChanged(List<ScheduleVo> scheduleList);

    /**
     * 日历view 点击回调接口
     */
    public interface OnCalendarClickListener {

        void onCalendarClick(Calendar cal);
    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener) {
        mCalendarCLickListener = listener;
    }

    protected OnCalendarClickListener mCalendarCLickListener;
}