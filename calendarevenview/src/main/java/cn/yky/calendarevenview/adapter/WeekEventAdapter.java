package cn.yky.calendarevenview.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import cn.yky.calendarevenview.view.WeekCalendarEventView;
import cn.yky.calendarevenview.view.WeekEventView;

/**
 * Created by yukuoyuan on 2017/6/22.
 * 这是一个带事件的适配器
 */
public class WeekEventAdapter extends PagerAdapter {
    private SparseArray<WeekEventView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private int mWeekCount = 220;
    private DateTime mStartDate;
    private WeekCalendarEventView weekCalendarEventView;

    public WeekEventAdapter(Context context, TypedArray array, WeekCalendarEventView weekCalendarEventView) {
        mContext = context;
        mArray = array;
        this.weekCalendarEventView = weekCalendarEventView;
        mViews = new SparseArray<>();
        initStartDate();

    }

    @Override
    public int getCount() {
        return mWeekCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            instanceWeekView(position);
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public SparseArray<WeekEventView> getViews() {
        return mViews;
    }


    /**
     * 初始化开始的时间
     */
    private void initStartDate() {
        mStartDate = new DateTime();
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
    }

    public WeekEventView instanceWeekView(int position) {
        WeekEventView weekEventView = new WeekEventView(mContext,mArray, mStartDate.plusWeeks(position - mWeekCount / 2));
        weekEventView.setId(position);
        weekEventView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekEventView.invalidate();
        weekEventView.setOnEmptyClickListener(weekCalendarEventView);
        weekEventView.setOnEventClickListener(weekCalendarEventView);
        mViews.put(position, weekEventView);
        return weekEventView;
    }
}
