package com.example.liuchad.customcalendar.widget;

/**
 * Created by liuchad on 16/3/21.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;

import com.example.liuchad.customcalendar.R;
import com.example.liuchad.customcalendar.adapter.CalendarWeekAdapter;

public class CalendarMoveLayout extends ViewGroup {
    /**
     * 滚动的时间(ms)
     */
    private static final int ANIMATION_DURATION = 500;
    private static final int JUSTIFY = 200;
    /**
     * 滚动的起始纵坐标
     */
    private int mStartScrollY = 0;
    private Scroller mScroller;
    private int mBaseTop = 0;
    /**
     * 上面整体日历LinearLayout的高度.@+id/first_child.
     */
    private int firstChildHeight = 0;
    private boolean mCouldDragDown = true;
    private int mTouchSlopSquare;
    /**
     * 每次点击或拖动手指落下的纵坐标.
     */
    private int mCurrentDownY = 0;
    /**
     * listTask向上拉到不能拉的状态下,跟最顶部的距离.
     */
    private int listTaskTopMargin = 0;
    private ListView listTask;
    private ViewPager vpCalendar;
    private static final int STATE_UP = 1;
    private static final int STATE_DOWN = 2;
    private int mCurrentState = STATE_DOWN;

    public void setCouldDragDown(boolean couldDrag) {
        mCouldDragDown = couldDrag;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mScroller.computeScrollOffset();
            int cY = mScroller.getCurrY();
            int delta = cY - mStartScrollY;
            mStartScrollY = cY;
            if (delta != 0) {
                mBaseTop += delta;
                if (mCurrentState == STATE_UP) {
                    if (vpCalendar.getAdapter() instanceof CalendarWeekAdapter) {
                        vpCalendar.setVisibility(VISIBLE);
                    } else {
                        vpCalendar.setVisibility(GONE);
                    }
                } else {
                    vpCalendar.setVisibility(VISIBLE);
                }
                requestLayout();
            }
            if (!mScroller.isFinished()) {
                handler.sendEmptyMessage(msg.what);
            } else {
            }
        }
    };

    public CalendarMoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        /**
         * touchSlop:在被判定为滚动之前用户手指可以移动的最大值.
         */
        int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;
        listTaskTopMargin = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        measureChild(getChildAt(0), widthMeasureSpec, heightMeasureSpec);
        firstChildHeight = getChildAt(0).getMeasuredHeight();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height - firstChildHeight - mBaseTop, MeasureSpec.EXACTLY);
        getChildAt(1).measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (listTask == null) {
            listTask = (ListView) findViewById(R.id.lvTask);
        }
        if (vpCalendar == null) {
            vpCalendar = (ViewPager) findViewById(R.id.calendar_viewpager);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, getMeasuredWidth(), firstChildHeight);
        getChildAt(1).layout(0, firstChildHeight + mBaseTop, getMeasuredWidth(), getMeasuredHeight());
        parentScroll();
    }

    private void parentScroll() {
        listTask.setFocusable(!parentScroll);
        listTask.setLongClickable(!parentScroll);
        listTask.setClickable(!parentScroll);
        listTask.getEmptyView().setFocusable(!parentScroll);
        listTask.getEmptyView().setClickable(!parentScroll);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (innerTouch(ev)) {
            return true;
        } else if (parentScroll) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    int newY;
    int delayY;
    int step = 0;
    boolean parentScroll = false;
    boolean downHere = false;

    private boolean innerTouch(MotionEvent ev) {
        if (ev.getY() < mBaseTop + firstChildHeight && !downHere) {
            return false;
        }
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartScrollY = y;
                mCurrentDownY = y;
                downHere = true;
                handler.removeMessages(JUSTIFY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (vpCalendar.getAdapter() instanceof CalendarWeekAdapter) {
                    listTaskTopMargin = (int) getResources().getDimension(R.dimen.calendar_week_top_margin);
                } else {
                    listTaskTopMargin = 0;
                }
                vpCalendar.setVisibility(VISIBLE);
                newY = (int) ev.getY();
                delayY = newY - mStartScrollY;
                mStartScrollY = newY;
                final int deltaY = y - mCurrentDownY;
                int distance = deltaY * deltaY;
                if (distance > mTouchSlopSquare) {
                    if (delayY < 0) {
                        setCouldDragDown(true);
                        //向上
                        step = delayY;
                        if (mBaseTop + firstChildHeight == listTaskTopMargin) {
                            //刚好碰到顶
                            parentScroll = false;
                            return false;
                        } else if (mBaseTop + step + firstChildHeight < listTaskTopMargin) {
                            //过了一点
                            parentScroll = true;
                            mBaseTop = -firstChildHeight;
                            requestLayout();
                            return true;
                        } else {
                            parentScroll = true;
                            mBaseTop += step;
                            requestLayout();
                            return true;
                        }
                    } else if (delayY > 0 && mCouldDragDown) {
                        //向下
                        int firstPos = listTask.getFirstVisiblePosition();
                        if (firstPos == 0) {
                            View child = listTask.getChildAt(0);
                            if (child == null || child.getTop() >= 0) {
                                step = delayY;
                                if (mBaseTop == 0) {
                                    setCouldDragDown(false);
                                    //刚好下面
                                    parentScroll = false;
                                    return false;
                                } else if (mBaseTop + step > listTaskTopMargin) {
                                    setCouldDragDown(false);
                                    //过了一点
                                    parentScroll = true;
                                    mBaseTop = listTaskTopMargin;
                                    requestLayout();
                                    return true;
                                } else {
                                    parentScroll = true;
                                    mBaseTop += step;
                                    requestLayout();
                                    return true;
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                return tryJustify();
        }
        return false;
    }


    private boolean tryJustify() {
        if (vpCalendar.getAdapter() instanceof CalendarWeekAdapter) {
            listTaskTopMargin = (int) getResources().getDimension(R.dimen.calendar_week_top_margin);
        } else {
            listTaskTopMargin = 0;
        }
        parentScroll = false;
        downHere = false;
        if (mBaseTop == 0) {
            mCurrentState = STATE_DOWN;
            vpCalendar.setVisibility(VISIBLE);
            return false;
        } else if (mBaseTop + firstChildHeight == listTaskTopMargin) {
            mCurrentState = STATE_UP;
            if (vpCalendar.getAdapter() instanceof CalendarWeekAdapter) {
                vpCalendar.setVisibility(VISIBLE);
            } else {
                vpCalendar.setVisibility(GONE);
            }
            return false;
        } else {
            int dy;
            int y = Math.abs(mBaseTop);
            if (mBaseTop > 0) {
                dy = -y;
            } else if (y * 1.0 / firstChildHeight <= 1.0 / 3) {
                // 向下弹
                mCurrentState = STATE_DOWN;
                dy = y;
                if (!mCouldDragDown) {
                    return true;
                }
            } else {
                // 向上弹
                dy = y - firstChildHeight + listTaskTopMargin;
                mCurrentState = STATE_UP;
            }
            if (dy >= -100 && y <= 100) {
                //向下拉太多 往上弹，此时状态为下
                mCurrentState = STATE_DOWN;
            }
            mScroller.startScroll(0, mStartScrollY, 0, dy, ANIMATION_DURATION);
            handler.sendEmptyMessage(JUSTIFY);
            return true;
        }
    }

    public void scroll(int y, int dy) {
        mCurrentState = STATE_DOWN;
        mScroller.startScroll(0, y, 0, dy, ANIMATION_DURATION);
        handler.sendEmptyMessage(JUSTIFY);
    }

    public int getBaseTop() {
        return mBaseTop;
    }
}
