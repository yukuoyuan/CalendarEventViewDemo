package cn.yky.calendarevenview.inter;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public interface OnMonthClickListener {
    void onClickThisMonth(int year, int month, int day);
    void onClickLastMonth(int year, int month, int day);
    void onClickNextMonth(int year, int month, int day);
}
