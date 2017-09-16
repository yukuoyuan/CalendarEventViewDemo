package cn.yky.calendarevenview.inter;

/**
 * Created by yukuoyuan on 2017/6/6.
 */
public interface OnMonthClickListener {
    void onClickThisMonth(int year, int month, int day);
    void onClickLastMonth(int year, int month, int day);
    void onClickNextMonth(int year, int month, int day);
}
