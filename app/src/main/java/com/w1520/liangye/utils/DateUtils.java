package com.w1520.liangye.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类.
 * <p/>
 * <p/>
 * Created by puruidong on 8/16/15.
 */
public class DateUtils {

    /**
     * 日期格式化.
     */
    public static final String DATE_FORMAT_YYMM = "yyyyMM";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    //--
    public static final String DATE_FORMAT_YYYY_MM = "yyyy-MM";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    //-

    public static final String DATE_FORMAT_X_YYYY_MM = "yyyy/MM";
    public static final String DATE_FORMAT_X_YYYY_MM_DD = "yyyy/MM/dd";
    //------END.

    //
    private SimpleDateFormat simpleDateFormat = null;
    private Date date = null;
    private Calendar calendar = null;

    private static DateUtils utils = null;

    private DateUtils() {
    }

    /**
     * 获取实例.
     *
     * @return
     */
    public static DateUtils getInstance() {
        if (utils == null) {
            return utils = new DateUtils();
        }
        return utils;
    }

    /**
     * 获取毫秒数.
     *
     * @return
     */
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }


    /**
     * 获取毫秒数.-5位.
     *
     * @return
     */
    public String getCurrentTimeById() {
        long time = System.currentTimeMillis();
        return (time > 10000) ? String.valueOf(time).substring(0, 5) : String.valueOf(time);
    }


    /**
     * 按照当前时间格式化后返回.
     *
     * @param format 日期格式
     * @return
     */
    public String getDate(String format) {
        date = new Date(getCurrentTime());
        simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 按照 时间格式化后返回.
     *
     * @param format 日期格式
     * @return
     */
    public String getDate(Date date, String format) {
        this.date = date;
        simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 对时间进行加减操作.
     *
     * @return
     */
    public Calendar rollDate(Date date, int field, int value) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, value);
        return calendar;
    }


    /**
     *
     *
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    public Calendar createCalendar(int year,int month,int day,int hour,int minute){
        Calendar ca = Calendar.getInstance();
        ca.set(year,month,day,hour,minute);
        return ca;
    }


    /**
     *
     *
     *
     * @param hour
     * @param minute
     * @return
     */
    public Calendar createCalendar(int hour,int minute){
        Calendar ca =Calendar.getInstance();
        ca.setTimeInMillis(getCurrentTime());
        ca.set(Calendar.HOUR_OF_DAY,hour);
        ca.set(Calendar.MINUTE,minute);
        return ca;
    }

}
