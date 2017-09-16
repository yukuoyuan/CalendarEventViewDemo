package cn.yky.calendarevenview.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.ParsePosition;
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

    private void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }

    private String printDay(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取两年之间月份总和
     *
     * @param sYear
     * @param eYear
     * @return
     */
    public int getMonthOfYears(int sYear, int eYear) {
        return (eYear - sYear) * 12;
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
     * 将字符串时间格式化成date
     *
     * @param dateString
     * @return
     * @edit chengqi
     */
    public Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    /**
     * 根据Date 获取年月日时分秒
     *
     * @param date
     * @return
     */
    public String getYearMonthDayHourMinutes(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = sdf.format(date);
        return str;
    }

    /**
     * 根据Date 获取年月日
     *
     * @param date
     * @return
     */
    public String getYearMonthDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;
    }

    public String getDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String str = sdf.format(date);
        return str;
    }

    public String formatDate(String date) {
        String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return formatDate;
    }

    /**
     * 获取当前时间的年月日时分秒
     */
    public String getNowDate() {
        try {
            return getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间的年月日时分秒
     */
    public String getNowDatess() {
        try {
            return getFormatDate(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间的年月日时分秒
     */
    public String getNowDatess2() {
        try {
            return getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd'T'HH:mm:ss");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间的年月日时分秒
     */
    public String getNowDate1() {
        try {
            return getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            return null;
        }
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
     * 这是一个获取当前的年月日的方法
     *
     * @return
     */
    public String getNowYearMonthDay() {
        try {
            return getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd");
        } catch (Exception e) {
            return null;
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
     * 这是一个处理时间的一个工具类
     *
     * @param time
     * @return 年月日
     */
    public String subTime(String time) {
        String date = time.substring(0, 10);
        String times = time.substring(11, 19);
        return date + "-" + times;
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


    public String getYearMonthDayHourMinutes(String time) {
        String date = null;
        if (!TextUtils.isEmpty(time)) {
            date = getYearMonthDay(time) + " " + getHourMinutes(time);
        } else {
            date = "";
        }
        return date;
    }

    public String getYearMonthDayHourMinutessecond(String time) {
        String date = null;
        if (!TextUtils.isEmpty(time)) {
            date = getYearMonthDay(time) + " " + getTime(time);
        } else {
            date = "";
        }
        return date;
    }

    /**
     * 这是一个获取年月的方法
     *
     * @param time
     * @return
     */
    public String getYearMonth(String time) {
        String date = null;
        if (!TextUtils.isEmpty(time)) {
            date = time.substring(0, 7);
        } else {
            date = "";
        }
        return date;
    }

    /**
     * 这是一个获取时分秒的方法
     *
     * @param time
     * @return 时分秒
     */
    public String getTime(String time) {
        String date = time.substring(11, 19);
        return date;
    }

    /**
     * 获取时分的方法
     *
     * @return 时分
     */
    public String getHourMinutes(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String date = time.substring(11, 16);
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
    public String getyear(String time) {
        String date = time.substring(0, 4);
        return date;
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
    public String getMonth(String time) {
        String date = time.substring(5, 7);
        return date;
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
    public String getday(String time) {
        String date = time.substring(8, 10);
        return date;
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
     * 获取月日
     *
     * @param time
     * @return
     */
    public String getMonthDay(String time) {
        String date = time.substring(5, 10);
        return date;
    }

    /**
     * 获取该年份的天数
     *
     * @param year
     * @return
     */
    public int getDaysInYear(int year) {
        int days;
        if (isLeepYear(year)) {
            days = 366;
        } else {
            days = 365;
        }
        return days;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 选中的日期yyyy-MM-dd
     * @return 周几
     */
    public String dayForWeek(String pTime) {
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return weekDays[dayForWeek - 1];
    }


    public boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }


    @SuppressLint({"SimpleDateFormat"})
    public Date StringToDate(String var0, String var1) {
        SimpleDateFormat var2 = new SimpleDateFormat(var1);
        Date var3 = null;

        try {
            var3 = var2.parse(var0);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return var3;
    }

    private final long INTERVAL_IN_MILLISECONDS = 30000L;

    @SuppressLint({"DefaultLocale"})
    public String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", Integer.valueOf(var1), Integer.valueOf(var3));
    }

    @SuppressLint({"DefaultLocale"})
    public String toTimeBySecond(int var0) {
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", Integer.valueOf(var1), Integer.valueOf(var3));
    }

    public String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public String getNowMonth() {
        return String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public int getNowMonths() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public int getNowDays() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public String getNowDay() {
        return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 是否大于当前时间
     *
     * @return
     */
    public boolean isbefoNowDate(Date date) {
        return date.getTime() > System.currentTimeMillis();
    }

    /**
     * 是否大于当前时间
     *
     * @return
     */
    public boolean isAfterNowDate(Date date) {
        return date.getTime() > parseDate(getNowDates()).getTime();
    }

    /**
     * 减去指定天数的工具类
     *
     * @param startime 开始的时间
     * @param days     减去的天数
     */
    public String toDeletedays(String startime, int days) {
        long starttime = parseDate(startime, "yyyy-MM-dd").getTime();
        long todeletetime = days * 24l * 60 * 60l * 1000l;
        return getDate(new Date(starttime - todeletetime));
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
     * 根据日期获取年龄
     *
     * @param date
     * @return
     */
    public int getAge(Date date) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(date);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * 根据年龄获得日期
     *
     * @param age
     * @return
     */
    public Date getBirthdayByAge(int age) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.getInstance().get(Calendar.YEAR) - age, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 是否是今天
     *
     * @param s
     * @return
     */
    public boolean isToday(String s) {
        return s.equals(getNowDates());
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

    /**
     * 两个日期是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
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
     * 根据时间字符串,获得毫秒值
     *
     * @param editTime
     * @return
     */
    public long getLongTime(String editTime) {
        Date date = new Date(getIntyear(editTime) - 1900, getIntMonth(editTime) - 1, getIntday(editTime), getIntHour(editTime), getIntMinutes(editTime));
        return date.getTime();
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public int getNowHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

    }

    /**
     * 获取当前分钟
     *
     * @return
     */
    public int getNowMinutes() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 获取两个时间的时间差
     *
     * @param time
     * @param minute
     * @param startTime
     * @param endTime   @return
     */
    public String getSection(String time, String minute, String startTime, String endTime) {
        String tip = "";
        int starthour = Integer.parseInt(time);
        int endhour = Integer.parseInt(startTime);
        int startminutes = Integer.parseInt(minute);
        int endminutes = Integer.parseInt(endTime);
        int hour = 0;
        hour = endhour - starthour;
        if (endminutes - startminutes == 0) {
            tip = hour + "小时";
        } else if (endminutes - startminutes < 0) {
            hour = hour - 1;
            if (hour == 0) {
                tip = "30分钟";
            } else {
                tip = hour + "小时" + "30分钟";
            }
        } else {
            if (hour == 0) {
                tip = "30分钟";
            } else {
                tip = hour + "小时" + "30分钟";
            }
        }
        return tip;
    }

}
