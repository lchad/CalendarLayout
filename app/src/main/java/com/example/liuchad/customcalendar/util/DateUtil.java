package com.example.liuchad.customcalendar.util;

/**
 * Created by liuchad on 16/3/21.
 */

import java.util.Calendar;
import java.util.Locale;

public class DateUtil {

    /**
     * 获取日期的month String
     */
    final public static String calToMonthStr(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" + String.format("%02d", cal.get(Calendar.MONTH) + 1);
    }

    final public static long[] calToDayTime(Calendar cal) {
        long time[] = new long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(cal.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        time[0] = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        time[1] = calendar.getTimeInMillis();
        return time;
    }

    final public static boolean isInTheDay(long time, Calendar cal) {
        long[] dayTime = calToDayTime(cal);
        if (time >= dayTime[0] && time <= dayTime[1]) {
            return true;
        }
        return false;
    }

    public static boolean isValidTime(int year, int month, int day, long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, month, day);
        if (calendar.getTimeInMillis() > startTime && calendar.getTimeInMillis() < endTime) {
            return true;
        }
        return false;
    }
}