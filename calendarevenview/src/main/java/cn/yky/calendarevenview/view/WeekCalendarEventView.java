package cn.yky.calendarevenview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.joda.time.DateTime;

import java.util.List;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.adapter.WeekEventAdapter;
import cn.yky.calendarevenview.bean.AppointMentAndSchduleListBean;
import cn.yky.calendarevenview.bean.ScheduleState;
import cn.yky.calendarevenview.bean.WeekCalendarEventBean;
import cn.yky.calendarevenview.inter.OnCalendarClickListener;

/**
 * Created by yukuoyuan on 2017/6/22.
 * 这是一个可以左右互动的日历事件的view
 */
public class WeekCalendarEventView extends ViewPager implements WeekEventView.EventClickListener, WeekEventView.EmptyClickListener {

    private WeekEventAdapter weekEventAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekEventView.EventClickListener eventClickListener;
    private WeekEventView.EmptyClickListener emptyClickListener;
    /**
     * 月历的状态
     */
    private ScheduleState mState;

    public WeekCalendarEventView(Context context) {
        this(context, null);

    }

    public WeekCalendarEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarEventView));
    }

    private void initWeekAdapter(Context context, TypedArray array) {
        weekEventAdapter = new WeekEventAdapter(context, array, this);
        setAdapter(weekEventAdapter);
        setCurrentItem(weekEventAdapter.getCount() / 2, false);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            WeekEventView weekEventView = weekEventAdapter.getViews().get(position);
            if (mOnCalendarClickListener != null) {
                mOnCalendarClickListener.onClickDate(weekEventView.getStratData().getYear(),
                        weekEventView.getStratData().getMonthOfYear() - 1,
                        weekEventView.getStratData().getDayOfMonth(), position, weekEventView.getStratData(), weekEventView.getEndData());
            }
            weekEventView.setOnEmptyClickListener(WeekCalendarEventView.this);
            weekEventView.setOnEventClickListener(WeekCalendarEventView.this);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 设置选择的界面
     *
     * @param position
     */
    public void setSelected(int position) {
        setCurrentItem(position);
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    /**
     * 设置空白区域点击的监听
     *
     * @param emptyClickListener
     */
    public void setOnEmptyClickListener(WeekEventView.EmptyClickListener emptyClickListener) {
        this.emptyClickListener = emptyClickListener;
    }

    public void setOnEventClickListener(WeekEventView.EventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;

    }

    public void setData(List<AppointMentAndSchduleListBean.ItemsBean> items) {
        WeekEventView weekEventView = weekEventAdapter.getViews().get(getCurrentItem());
        if (weekEventView==null){
            return;
        }
        weekEventView.setData(items);
    }

    public WeekEventAdapter getWeekEventAdapter() {
        return weekEventAdapter;
    }

    @Override
    public void onEventClick(WeekCalendarEventBean event) {
        if (eventClickListener != null) {
            eventClickListener.onEventClick(event);
        }
    }

    @Override
    public void onEmptyClick(DateTime dateTime) {
        if (emptyClickListener != null) {
            emptyClickListener.onEmptyClick(dateTime);
        }
    }

    /**
     * 跳转到指定时间
     *
     * @param nowHour
     */
    public void gotoHour(int nowHour) {
        WeekEventView weekEventView = weekEventAdapter.getViews().get(getCurrentItem());
        if (weekEventView != null) {
            weekEventView.gotoHour(nowHour);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mState == ScheduleState.OPEN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 设置状态
     *
     * @param mState
     */
    public void setState(ScheduleState mState) {
        this.mState = mState;
    }
}
