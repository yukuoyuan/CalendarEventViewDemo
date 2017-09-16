package cn.yky.calendarevenview.inter;

import org.joda.time.DateTime;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public interface OnCalendarClickListener {
    void onClickDate(int year, int month, int day, int position, DateTime stratData, DateTime endData);
}
