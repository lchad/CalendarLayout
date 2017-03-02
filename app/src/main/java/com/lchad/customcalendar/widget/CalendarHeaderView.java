package com.lchad.customcalendar.widget;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lchad.customcalendar.BaseApplication;
import com.lchad.customcalendar.R;
import com.lchad.customcalendar.util.CommonUtils;

public class CalendarHeaderView extends View {

    private Paint mPaint;
    private String[] weekArray;
    private float mMarginRight = 0f;
    private float mMarginLeft = 0f;
    private int mMarginTop;

    public CalendarHeaderView(Context context) {
        this(context, null);
    }

    public CalendarHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(getResources().getDimensionPixelSize(
                R.dimen.calendar_header_text_size));
        mPaint.setAntiAlias(true);
        weekArray = getResources().getStringArray(R.array.full_week);
        mPaint.setColor(getResources().getColor(R.color.header_text));
        mPaint.setAntiAlias(true);
        mMarginTop = getResources().getDimensionPixelOffset(R.dimen.calendar_month_header_marginTop);
    }

    public void changeModeToWeek(int dayOfWeek) {
        if (mMarginLeft != 0) return;
        mMarginLeft = getResources().getDimension(R.dimen.calendar_time_width) * 2 / 3;
        mMarginRight = mMarginLeft / 2f;
        //change weekArray
        String[] dest = new String[7];
        for (int i = 0; i < weekArray.length; i++) {
            dest[i] = weekArray[(dayOfWeek + i) % weekArray.length];
        }
        weekArray = dest;
        //重绘视图
        invalidate();
    }

    public void changeModeToMonth() {
        if (mMarginLeft == 0) return;
        mMarginLeft = 0f;
        mMarginRight = 0f;
        //change weekArray
        weekArray = getResources().getStringArray(R.array.full_week);
        //重绘视图
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float weekTextX = (getWidth() - mMarginLeft - mMarginRight) / 7f;

        for (int i = 0; i < weekArray.length; i++) {
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int y = (getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top - mMarginTop + CommonUtils.dip2px(BaseApplication.getInstance(), 14);

            float x = mMarginLeft + weekTextX * i + (weekTextX - mPaint.measureText(weekArray[i])) / 2f;
            canvas.drawText(weekArray[i], x, y, mPaint);
        }
    }
}