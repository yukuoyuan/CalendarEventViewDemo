package cn.yky.calendarevenview.inter;

import org.joda.time.DateTime;

/**
 * Created by yukuoyuan on 2017/6/6.
 */
public interface OnCalendarClickListener {
    void onClickDate(int year, int month, int day, int position, DateTime stratData, DateTime endData);
}
