package cn.yky.calendarevenview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.adapter.WeekAdapter;
import cn.yky.calendarevenview.inter.OnCalendarClickListener;

/**
 *
 * Created by yukuoyuan on 2017/6/6.
 * 这是一个可以左右滑动的周历控件
 */
public class WeekCalendarView extends ViewPager {

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;
    public Map<String, Object> mObjectHashMap = new HashMap<>();
    /**
     * 医生id
     */
    private String doctorId;
    /**
     * 筛选的类型
     */
    private int type;
    private Context context;

    public WeekCalendarView(Context context) {
        this(context, null);
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        this.context = context;
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarView));
    }

    private void initWeekAdapter(Context context, TypedArray array) {
        mWeekAdapter = new WeekAdapter(context, array, this);
        setAdapter(mWeekAdapter);
        setCurrentItem(mWeekAdapter.getWeekCount() / 2, false);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            WeekView weekView = mWeekAdapter.getViews().get(position);
            if (mOnCalendarClickListener != null) {
                mOnCalendarClickListener.onClickDate(weekView.getStratData().getYear(),
                        weekView.getStratData().getMonthOfYear() - 1,
                        weekView.getStratData().getDayOfMonth(), position, weekView.getStratData(), weekView.getEndData());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public SparseArray<WeekView> getWeekViews() {
        return mWeekAdapter.getViews();
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }


    public WeekView getCurrentWeekView() {
        return getWeekViews().get(getCurrentItem());
    }

    public WeekAdapter getWeekAdapter() {
        return mWeekAdapter;
    }

    /**
     * 滑动到下周
     */
    public void plusWeek() {
        setCurrentItem(getCurrentItem() + 1);
    }

    /**
     * 滑动到上周
     */
    public void SubtractWeek() {
        setCurrentItem(getCurrentItem() - 1);
    }

    /**
     * 滑动到今天
     */
    public void go2Today() {
        setCurrentItem(mWeekAdapter.getWeekCount() / 2);
    }


}
