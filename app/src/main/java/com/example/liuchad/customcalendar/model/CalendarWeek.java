package com.example.liuchad.customcalendar.model;

/**
 * Created by liuchad on 16/3/21.
 */

    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;

/**
 * 某一周的日历对象
 * Created by Yetwish on 2015/7/28.
 */
public class CalendarWeek {

    /**
     * 一周的总天数
     */
    public static final int DAYS = 7;

    /**
     * 该周第一天的年份
     */
    public int year;

    /**
     * 该周第一天的月份 取值：0-11 0表示1月，11表示12月
     */
    public int month;

    /**
     * 表示该周第一天的星期数
     */
    public int weekDay ;

    /**
     * 该周第一天的日期 MONTH_OF_DAY
     */
    public int day;

    public long beginTime;

    public long endTime;

    /**
     * 判断是否跨月
     */
    public boolean isCross;

    /**
     * 记录本周所有天数的日期
     */
    public List<Integer> dates = new ArrayList<Integer>(DAYS);


    public CalendarWeek(Calendar calendar){
        //获取第一天的年、月、日 和星期数
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.weekDay = calendar.get(Calendar.DAY_OF_WEEK);  //1-7 1是周天 -> 周六
        this.dates.add(day);
        for(int i = 1 ; i < DAYS;i++){
            calendar.add(Calendar.DAY_OF_MONTH,1);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dates.add(day);
        }
        if(calendar.get(Calendar.MONTH) != month){
            isCross = true;
        }else {
            isCross = false;
        }
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        this.endTime = calendar.getTimeInMillis();
        calendar.set(year,month,day,0,0,0);
        calendar.set(Calendar.MILLISECOND,0);
        this.beginTime = calendar.getTimeInMillis();
    }

    @Override
    public String toString() {
        String str ="";
        for(int i = 0; i < dates.size() ; i++){
            str += dates.get(i) + "\t";
        }
        return "["+str +"]";
    }
}
