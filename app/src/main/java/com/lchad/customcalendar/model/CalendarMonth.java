package com.lchad.customcalendar.model;

/**
 * Created by liuchad on 16/3/21.
 */

import java.util.Calendar;

import java.util.Calendar;
import java.util.Locale;

/**
 * 某个月的日历对象
 */
public class CalendarMonth {

    /**
     * 年份
     */
    public int year;

    /**
     * 月份 取值：0-11 0表示1月，11表示12月
     */
    public int month;

    /**
     * 该月的日历的行数
     */
    public int row;

    /**
     * 该月第一天的星期数
     */
    public int weekFirst;

    public long firstDayOfCal;

    /**
     * 该月的天数
     */
    public int days;

    /**
     * 表示该月第一天0时0分的毫秒数
     */
    public long startTime;

    /**
     * 表示该月最后一天23时59分的毫秒数
     */
    public long endTime;

    public CalendarMonth(int year, int month) {
        this.year = year;
        this.month = month;

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, month, 1, 0, 0, 0);
        days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //获取startTime和endTime
        this.startTime = calendar.getTimeInMillis();
        weekFirst = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        calendar.set(year, month, days, 23, 59, 59);
        this.endTime = calendar.getTimeInMillis();

        int weekCount = days / 7;

        if (weekFirst == 0 && days == 28) {
            row = weekCount;
        } else if (weekFirst == 6 && days >= 30 || weekFirst == 5 && days == 31) {
            row = weekCount + 2;
        } else {
            row = weekCount + 1;
        }
        days = row * 7;

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1 * weekFirst);
        firstDayOfCal = calendar.getTimeInMillis();
    }
}
