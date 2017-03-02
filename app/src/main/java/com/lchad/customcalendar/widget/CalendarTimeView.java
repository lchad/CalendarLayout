package com.lchad.customcalendar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lchad.customcalendar.R;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */
public class CalendarTimeView extends View {

    private Context mContext;

    /**
     * 该view绘制的起点的X坐标
     */
    private float mStartX;

    /**
     * 该view绘制的起点的X坐标
     */
    private float mStartY;

    /**
     * 该view每个子部分的高度
     */
    private float mChildHeight;

    private int mSpacing;

    /**
     * 该view每个子部分的高宽度
     */
    private float mChildWidth;

    /**
     * 字体大小
     */
    private float mFontSize;

    /**
     * 画文本的paint
     */
    private Paint mFontPaint;

    /**
     * 画上午背景的paint
     */
    private Paint mBgPaint;

    private int[] bgColor = {
            getResources().getColor(R.color.schedule_time_mor),
            getResources().getColor(R.color.schedule_time_aft),
            getResources().getColor(R.color.schedule_time_eve)
    };

    public CalendarTimeView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CalendarTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CalendarTimeView);
        mStartX = ta.getDimension(R.styleable.CalendarTimeView_startX, 0f);
        mStartY = ta.getDimension(R.styleable.CalendarTimeView_startY, getResources().getDimension(R.dimen.calendar_time_margin));
        mChildHeight = ta.getDimension(R.styleable.CalendarTimeView_view_height, getResources().getDimension(R.dimen.calendar_time_height));
        ta.recycle();
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        mFontPaint = new Paint();
        mFontPaint.setColor(Color.WHITE);
        mFontPaint.setAntiAlias(true);
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);

        mFontSize = getResources().getDimensionPixelSize(R.dimen.calendar_time_text_size);
        mFontPaint.setTextSize(mFontSize);

        mSpacing = getResources().getDimensionPixelOffset(R.dimen.calendar_time_spacing);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChildWidth == 0) {
            mChildWidth = getWidth();
        }
        drawBackgroundAndText(canvas);
    }

    private void drawBackgroundAndText(Canvas canvas) {
        float offsetX = mStartX + mFontSize * 0.3f;
        float offsetY = mStartY + mChildHeight * 0.5f;
        String str[] = getContext().getResources().getStringArray(R.array.full_day);

        for (int i = 0; i < str.length; i++) {
            mBgPaint.setColor(bgColor[i]);
            canvas.drawRect(
                    mStartX,
                    mStartY + i * (mChildHeight + mSpacing),
                    mStartX + mChildWidth,
                    mStartY + mChildHeight * (i + 1),
                    mBgPaint);

            canvas.drawText(str[i].substring(0, 1), offsetX, offsetY + mChildHeight * i, mFontPaint);
            canvas.drawText(str[i].substring(1, 2), offsetX, offsetY + mFontSize + mChildHeight * i, mFontPaint);
        }
    }
}
