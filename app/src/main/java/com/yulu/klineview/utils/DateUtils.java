package com.yulu.klineview.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */

public class DateUtils {
    /**
     * 判断dateA、dateB是否为一年
     */
    public static boolean isSameYear(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR);
    }

    /**
     * 判断dateA、dateB是否为一天
     */
    public static boolean isSameDay(Date dateA, Date dateB) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dateA);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dateB);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    /**
     * 计算给定时间的前一天
     */
    public static String getNextDay(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeS = "--";
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        timeS = df.format(calendar.getTime());
        return timeS;
    }
    /**
     * 计算给定时间的小时和分钟
     */
    public static long getLongTime(String time, String type) {
        SimpleDateFormat df = new SimpleDateFormat(type);
        try {
            Date  date = df.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算给定时间的小时和分钟
     */
    public static String getMinutes(String time, String type) {
        try {
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat df2 = new SimpleDateFormat(type);
            Date date = df1.parse(time);
            time = df2.format(date);
        } catch (Exception e) {
            time = "--";
            e.printStackTrace();
        }
        return time;
    }

    public static String getMinutes(String time, String type, String lastTime) {
        try {
            SimpleDateFormat df1 = new SimpleDateFormat(lastTime);
            SimpleDateFormat df2 = new SimpleDateFormat(type);
            Date date = df1.parse(time);
            time = df2.format(date);
        } catch (Exception e) {
            time = "--";
            e.printStackTrace();
        }
        return time;
    }

    public static String getMinutes(long time, String type) {
        String timeS;
        try {
            SimpleDateFormat df2 = new SimpleDateFormat(type);
            Date date = new Date(time);
            timeS = df2.format(date);
        } catch (Exception e) {
            timeS = "--";
            e.printStackTrace();
        }
        return timeS;
    }

//
//    public static boolean isSameYear(){
//
//    }
}
