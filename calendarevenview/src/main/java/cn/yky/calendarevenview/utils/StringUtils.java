package cn.yky.calendarevenview.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
     * 格式化money
     *
     * @param data 格式化的钱(带小数点后两位)
     * @return
     */
    public static String formatString(double data) {
        if (String.valueOf(data).equals("0.0")) {
            return "0.00";
        }
        if (data == 0d) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#,###.00");
        String text = df.format(data);
        if (text.equals("NaN")) {
            return "0.00";
        }
        if (!text.contains(".")) {
            text = text + ".00";
        }
        if (text.indexOf("-") == 0 && text.indexOf(".") == 1) {
            return "-0" + text.replace("-", "");
        }
        if (text.indexOf(".") == 0) {
            return "0" + text;
        }
        return text;
    }

    /**
     * 格式化money
     *
     * @param data 格式化的钱(不带小数点)
     * @return
     */
    public static String formatString2(double data) {
        if (String.valueOf(data).equals("0.0")) {
            return "0";
        }
        if (data == 0d) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("#,###");
        String text = df.format(data);
        if (text.equals("NaN")) {
            return "";
        }
        return text;
    }

    /**
     * 格式化money
     *
     * @param data 格式化的钱(带小数点后两位,但是不带,)
     * @return
     */
    public static String formatString3(double data) {
        if (String.valueOf(data).equals("0.0")) {
            return "0.00";
        }
        if (data == 0d) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###.00");
        String text = df.format(data);
        if (text.equals("NaN")) {
            return "0.00";
        }
        if (!text.contains(".")) {
            text = text + ".00";
        }
        if (text.indexOf(".") == 0) {
            return "0" + text;
        }
        return text;
    }

    /**
     * 格式化金钱前边需加上+号
     *
     * @param price
     * @param
     * @return
     */
    public static String formatPrice(double price) {
        if (price == 0)
            return "+0.00";

        if (price == 0d) {
            return "+0.00";
        }
        DecimalFormat df = new DecimalFormat("#,###.##");
        String text = df.format(price);
        if (text.equals("NaN")) {
            return "+0.00";
        }
        if (!text.contains(".")) {
            text = text + ".00";
        }
        if (!text.contains("-")) {
            return "+" + text;
        }

        return text;
    }

    /**
     * 格式化金钱前边
     *
     * @param price
     * @param
     * @return
     */
    public static String formatPrice2(double price) {
        if (price == 0)
            return "0.00";
        if (String.valueOf(price).contains("-")) {
            price = Double.parseDouble(String.valueOf(price).replace("-", ""));
        }
        if (price >= 100000) {
//            DecimalFormat df = new DecimalFormat("#");
//            String text = df.format(price);
            long pr = (long) price;
//            pr = Long.valueOf(text);
            pr = pr - pr % 10000;
            String string = String.valueOf(pr);
            string = string.substring(0, string.length() - 4);
            DecimalFormat dfs = new DecimalFormat("#,###");
            String texts = dfs.format(Long.parseLong(string));
            return texts + "万+";
        }

        return formatString(price);
    }
    /**
     * 格式化金钱前边
     *
     * @param price
     * @param
     * @return
     */
    public static String formatPrice3(double price) {
        if (price == 0)
            return "0.00";
        if (String.valueOf(price).contains("-")) {
            price = Double.parseDouble(String.valueOf(price).replace("-", ""));
        }
        if (price >= 1000000) {
//            DecimalFormat df = new DecimalFormat("#");
//            String text = df.format(price);
            long pr = (long) price;
//            pr = Long.valueOf(text);
            pr = pr - pr % 10000;
            String string = String.valueOf(pr);
            string = string.substring(0, string.length() - 4);
            DecimalFormat dfs = new DecimalFormat("#,###");
            String texts = dfs.format(Long.parseLong(string));
            return texts + "万+";
        }
        DecimalFormat df = new DecimalFormat("#,###.##");
        String text = df.format(price);
        if (text.equals("NaN")) {
            return "0.00";
        }
        return text;
    }
    /**
     * 处理空数据
     *
     * @param string
     * @return
     */
    public static String formatEmptyString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        return string;
    }

    /**
     * 处理空数据
     *
     * @param string
     * @return - -
     */
    public static String formatEmptyString2(String string) {
        if (TextUtils.isEmpty(string)) {
            return "- -";
        }
        return string;
    }

    /**
     * 转换字符串为一个集合
     *
     * @param s
     * @return
     */
    public static ArrayList<String> text2List(String s) {
        ArrayList<String> list = new ArrayList();
        if (TextUtils.isEmpty(s)) {
            return list;
        }
//        if (!list.contains(",")) {
//            list.add(s);
//            return list;
//        }
        String[] a = s.split(",");
        list.addAll(Arrays.asList(a));
        return list;
    }

    /**
     * 这是一个处理字符串为0,应该显示00的处理
     *
     * @param minute
     * @return
     */
    public static String formatZero(String minute) {
        if (minute.equals("0")) {
            return "00";
        } else {
            return minute;
        }
    }

    /**
     * 格式化字符串为int 类型,并返回
     *
     * @param s
     * @return
     */
    public static int formartInt(String s) {
        if (TextUtils.isEmpty(s)) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    /**
     * 这是一个格式化金额的方法
     *
     * @param trim
     */
    public static double formartMoney(String trim) {
        String money = trim;
        if (money.contains(",")) {
            money = money.replace(",", "");
        }
        if (money.contains("￥")) {
            money = money.replace("￥", "");
        }
        if (TextUtils.isEmpty(money)) {
            return 0.00d;
        }
        if (money.indexOf(".") == 0) {
            return 0.00d;
        }
        if (money.indexOf(".") == (money.length() - 1)) {
            money = money.replace(".", "");
        }
        return Double.parseDouble(money);
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

    /**
     * 格式化日期
     *
     * @param minute
     * @return
     */
    public static String formartTime(String minute) {
        if (TextUtils.isEmpty(minute)) {
            return "00";
        }
        return formatData(Integer.parseInt(minute));
    }

    /**
     * 这是一个取整的方法
     */
    public static double formartIntMoney(double money, int whole) {
        return money - money % whole;
    }

    public static ArrayList<String> getToothName(ArrayList<String> tooths, int type) {
        if (ListUtils.isEmpty(tooths)) {
            return new ArrayList<>();
        }
        if (type == 1) {
            ArrayList<Integer> toothslist = new ArrayList<>();
            for (String s : tooths) {
                toothslist.add(Integer.parseInt(s));
            }
            /**
             * 排序
             */
            Collections.sort(toothslist, new Comparator<Integer>() {
                @Override
                public int compare(Integer s1, Integer s2) {
                    return s1 - s2;
                }
            });
            /**
             * 乳牙的数据
             */
            if (toothslist.get(0) > 50) {
                return new ArrayList<>();
            } else {
                return tooths;
            }
        } else {

            ArrayList<Integer> toothslist = new ArrayList<>();
            for (String s : tooths) {
                toothslist.add(Integer.parseInt(s));
            }
            /**
             * 排序
             */
            Collections.sort(toothslist, new Comparator<Integer>() {
                @Override
                public int compare(Integer s1, Integer s2) {
                    return s1 - s2;
                }
            });
            /**
             * 乳牙的数据
             */
            if (toothslist.get(0) > 50) {
                return tooths;
            } else {
                return new ArrayList<>();
            }
        }
    }

    /**
     * 格式化正负号的问题的显示
     *
     * @param money
     * @return
     */
    public static String formatString4(double money) {
        if (String.valueOf(money).equals("0.0")) {
            return "0.00";
        }
        if (money == 0d) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#,###.00");
        String text = df.format(money);
        if (text.equals("NaN")) {
            return "0.00";
        }
        if (!text.contains(".")) {
            text = text + ".00";
        }
        if (text.indexOf(".") == 0) {
            return "0" + text;
        }
        if (text.indexOf("-") == 0 && text.indexOf(".") == 1) {
            return "-￥0" + text.replace("-", "");
        }
        if (text.contains("-")) {
            return "-￥" + text.replace("-", "");
        }
        return "￥" + text;
    }


    /**
     * 设置输入框的输入的最大和最小值
     */
    public static class InputFilterMinMax implements InputFilter {

        private double min, max;

        public InputFilterMinMax(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Double.parseDouble(min);
            this.max = Double.parseDouble(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                                   int dend) {
            try {
                double input = Double.parseDouble(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(double a, double b, double c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }

    }

    public static boolean isLetter(char at) {
        return (at <= 'Z' && at >= 'A')
                || (at <= 'z' && at >= 'a');
    }

    public static String isEmpty(String str) {
        return TextUtils.isEmpty(str) ? "- -" : str;
    }


    public static String formatEmptyStringLin(String string) {
        if (TextUtils.isEmpty(string)) {
            return "- -";
        }
        return string;
    }
}
