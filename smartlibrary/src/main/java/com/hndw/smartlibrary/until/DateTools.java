package com.hndw.smartlibrary.until;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTools {
    /**
     * 获取当天日期
     *
     * @param format
     * @return
     */
    public static String getCurDayString(String format) {
        Date date = getCurDayTime();
        SimpleDateFormat sdtf = new SimpleDateFormat(format == null ? "yyyy-MM-dd" : format);
        String dateString = sdtf.format(date);
        return dateString;
    }

    /**
     * 获取当天时间
     *
     * @return
     */
    public static Date getCurDayTime() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 0);//如果把0修改为-1就代表昨天
        date = calendar.getTime();
        return date;
    }

    /**
     * 格式化时间
     *
     * @param format
     * @param dayTime
     * @return
     */
    public static String formatDayTime(String format, long dayTime) {
        Date date = new Date();
        date.setTime(dayTime);
        SimpleDateFormat sdtf = new SimpleDateFormat(format == null ? "HH:mm:ss" : format);
        return sdtf.format(date);
    }


    /**
     * 时间格式转换
     *
     * @param time
     * @param format
     * @return
     */
    public static String getDateString(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat sdtf = new SimpleDateFormat(format == null ? "yyyy-MM-dd" : format);
        return sdtf.format(date);
    }
}
