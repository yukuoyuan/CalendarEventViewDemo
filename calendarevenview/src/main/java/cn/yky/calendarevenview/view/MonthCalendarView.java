package cn.yky.calendarevenview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.adapter.MonthAdapter;
import cn.yky.calendarevenview.inter.OnCalendarClickListener;
import cn.yky.calendarevenview.inter.OnMonthClickListener;

/**
 * Created by yukuoyuan on 2017/6/6.
 * 这是一个月历的控件
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;
    /**
     * 医生id
     */
    private String doctorId;
    /**
     * 筛选的类型
     */
    private int type;
    public Map<String, Object> mObjectHashMap = new HashMap<>();
    private Context context;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        this.context = context;
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    private void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array, this);
        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, false);
    }

    @Override
    public void onClickThisMonth(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day, 0, new DateTime(), new DateTime());
        }
    }

    /**
     * 当上个月份点击之后,跳转到上个月
     *
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onClickLastMonth(int year, int month, int day) {
//        addOnPageChangeListener(null);
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() - 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
        }
        setCurrentItem(getCurrentItem() - 1, true);
//        addOnPageChangeListener(mOnPageChangeListener);
    }

    /**
     * 当下个月份点击之后,跳转到下个月
     *
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onClickNextMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() + 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
        }
        setCurrentItem(getCurrentItem() + 1, true);
    }

    /**
     * 设置当某一个月被选中的时候的点击事件监听
     */
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
            if (monthView != null) {
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 跳转到今天
     */
    public void go2Today() {
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, true);
        MonthView monthView = mMonthAdapter.getViews().get(mMonthAdapter.getMonthCount() / 2);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<MonthView> getMonthViews() {
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

    /**
     * 增加一个月
     */
    public void plusMonth() {
        setCurrentItem(getCurrentItem() + 1);
    }

    /**
     * 减少一个月
     */
    public void SubtractMonth() {
        setCurrentItem(getCurrentItem() - 1);
    }
}
