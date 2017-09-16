package cn.yky.calendarevenview.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by yukuoyuan on 2017/9/16.
 */
public class DateUtil {
    private static DateUtil sInstance;
    /**
     * 第一天是周日
     */
    private static final int FIRST_DAY = Calendar.MONDAY;

    private DateUtil() {
    }

    public static DateUtil instance() {
        if (sInstance == null) {
            synchronized (DateUtil.class) {
                if (sInstance == null) {
                    sInstance = new DateUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据详细的日期,返回是周几
     *
     * @param year  年
     * @param month 月份
     * @param day   天
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public int getDayWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得两个日期距离几周
     *
     * @return
     */
    public int getWeeksAgo(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        Calendar lastClickDay = Calendar.getInstance();
        lastClickDay.set(lastYear, lastMonth, lastDay);
        int week = lastClickDay.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar clickDay = Calendar.getInstance();
        clickDay.set(year, month, day);
        if (clickDay.getTimeInMillis() > lastClickDay.getTimeInMillis()) {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + week * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        } else {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + (week - 6) * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        }
    }

    /**
     * 获得两个日期距离几个月
     *
     * @return
     */
    public int getMonthsAgo(int lastYear, int lastMonth, int year, int month) {
        return (year - lastYear) * 12 + (month - lastMonth);
    }

    public int getWeekRow(int year, int month, int day) {
        int week = getFirstDayWeek(year, month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (lastWeek == 7)
            day--;
        return (day + week - 1) / 7;
    }

    /**
     * 判断该年份是不是闰年
     *
     * @param year
     * @return
     */
    public boolean isLeepYear(int year) {
        boolean isLeep = false;
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            isLeep = true;
        }
        return isLeep;
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr    时间字符串
     * @param dataFormat 当前时间字符串的格式。
     * @return Date 日期 ,转换异常时返回null。
     */
    public Date parseDate(String dateStr, String dataFormat) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat(dataFormat);
            Date date = dateFormat.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public Date parseDate(String dateStr) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public Date parseDate2(String dateStr) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = dateFormat.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据Date 获取年月日
     *
     * @param date
     * @return
     */
    public String getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;
    }


    /**
     * 获取当前时间的年月日
     */
    public String getNowDates() {
        try {
            return getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间的年月日
     */
    public long getNowDatesTime() {
        try {
            return parseDate(getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd")).getTime();
        } catch (Exception e) {
            return 0l;
        }
    }

    /**
     * 获取当前时间的年月日
     */
    public long getNowDatesTime(String date) {
        try {
            return parseDate(date).getTime();
        } catch (Exception e) {
            return 0l;
        }
    }


    /**
     * 这是一个给定特殊的日期格式,返回一个把毫秒值转换为字符串的日期
     *
     * @param date         给定日期毫秒值
     * @param formatString 给定需要格式化的日期格式例如:yyyy-MM-dd HH:mm:ss
     * @return
     */
    public String getFormatDate(long date, String formatString) {
        String formatData;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        formatData = simpleDateFormat.format(date);
        return formatData;
    }



    /**
     * 返回一个年月日的方法
     *
     * @param time
     * @return 年月日
     */
    public String getYearMonthDay(String time) {
        String date = null;
        if (!TextUtils.isEmpty(time)) {
            date = time.substring(0, 10);
        } else {
            date = "";
        }
        return date;
    }


    /**
     * 获取时的方法
     *
     * @return 时
     */
    public String getHour(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String date = time.substring(11, 13);
        return date;
    }

    /**
     * 获取时的方法
     *
     * @return 时
     */
    public int getIntHour(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        String date = time.substring(11, 13);
        return Integer.parseInt(date);
    }

    /**
     * 获取分的方法
     *
     * @return 分
     */
    public String getMinutes(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String date = time.substring(14, 16);
        return date;
    }

    /**
     * 获取分的方法
     *
     * @return 分
     */
    public int getIntMinutes(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        String date = time.substring(14, 16);
        return Integer.parseInt(date);

    }
    /**
     * 这是一个获取年的方法
     *
     * @param time
     * @return 年
     */
    public int getIntyear(String time) {
        String date = time.substring(0, 4);
        return Integer.parseInt(date);
    }
    /**
     * 这是一个获取月的方法
     *
     * @param time
     * @return 月
     */
    public int getIntMonth(String time) {
        String date = time.substring(5, 7);
        return Integer.parseInt(date);
    }

    /**
     * 这是一个获取日的方法
     *
     * @param time
     * @return 日
     */
    public int getIntday(String time) {
        String date = time.substring(8, 10);
        return Integer.parseInt(date);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public int getNowMonths() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public int getNowYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    /**
     * 这是一个分割是简单的方法
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public HashMap<DateTime, DateTime> getDateList(String startTime, String endTime) {
        HashMap<DateTime, DateTime> Datelist = new HashMap<>();
        DateTime startDate = DateTime.parse(startTime);
        DateTime endDate = DateTime.parse(endTime);

        while (!getYearMonthDay(startDate).equals(getYearMonthDay(endDate))) {
            Datelist.put(startDate, new DateTime(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth(), 23, 59));
            startDate = new DateTime(startDate.plusDays(1).getYear(), startDate.plusDays(1).getMonthOfYear(), startDate.plusDays(1).getDayOfMonth(), 0, 0);
        }
        Datelist.put(startDate, endDate);
        return Datelist;

    }

    public String getYearMonthDay(DateTime time) {
        return time.getYear() + "-" + time.getMonthOfYear() + "-" + time.getDayOfMonth();
    }
}
