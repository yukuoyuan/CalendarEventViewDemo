package cn.yky.calendarevenview.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by yukuoyuan on 2017/5/22.
 * 这是一个处理字体的工具类
 */
public class StringUtils {


    /**
     * 这是一个格式化名字的工具类
     *
     * @param name
     * @param length
     * @return
     */
    public static String formatName(String name, int length) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        if (name.length() > length) {
            return name.substring(0, length) + "...";
        }
        return name;
    }

    /**
     * 格式化时间
     *
     * @param monthOfYear
     * @return
     */
    public static String formatData(int monthOfYear) {
        if (monthOfYear < 10) {
            return "0" + monthOfYear;
        }
        return monthOfYear + "";
    }


}
