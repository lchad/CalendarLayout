package com.lchad.customcalendar.util;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {

    /**
     * 获取日期的month String
     */
    public static String calToMonthStr(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" + String.format("%02d", cal.get(Calendar.MONTH) + 1);
    }

    public static long[] calToDayTime(Calendar cal) {
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

    public static boolean isInTheDay(long time, Calendar cal) {
        long[] dayTime = calToDayTime(cal);
        return time >= dayTime[0] && time <= dayTime[1];
    }

    public static boolean isValidTime(int year, int month, int day, long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, month, day);
        return calendar.getTimeInMillis() > startTime && calendar.getTimeInMillis() < endTime;
    }
}