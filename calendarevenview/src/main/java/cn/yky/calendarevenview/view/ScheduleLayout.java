package cn.yky.calendarevenview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.joda.time.DateTime;

import java.util.Calendar;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.anims.ScheduleAnimation;
import cn.yky.calendarevenview.bean.ScheduleState;
import cn.yky.calendarevenview.inter.OnCalendarClickListener;
import cn.yky.calendarevenview.utils.DateUtil;

/**
 * Created by yukuoyuan on 2017/6/24.
 * 这是一个处理日历和月历控件显示隐藏的布局
 */
public class ScheduleLayout extends FrameLayout {


    private MonthCalendarView mcv_booking_management_month_calendar;
    private WeekCalendarView wcv_booking_management_week_calendar;
    private RelativeLayout rl_booking_management_month_calendar;
    private WeekCalendarEventView wcev_booking_management_week_event_view;

    private int mCurrentSelectYear;
    private int mCurrentSelectMonth;
    private int mCurrentSelectDay;
    private int mWeekCalendarHeight;
    private int mMinDistance;
    private int mAutoScrollDistance;
    /**
     * 按下的时候的位置
     */
    private float mDownPosition[] = new float[2];
    private boolean mIsScrolling = false;

    private ScheduleState mState;
    private OnCalendarClickListener mOnCalendarClickListener;
    private GestureDetector mGestureDetector;
    private boolean weekCalendarEventViewTop;
    /**
     * 医生id
     */
    private String doctorId;
    /**
     * 筛选的类型
     */
    private int type;

    public ScheduleLayout(Context context) {
        this(context, null);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
        initDate();
        initGestureDetector();
    }

    /**
     * 初始化各个属性
     */
    private void initAttrs() {
        mWeekCalendarHeight = getResources().getDimensionPixelSize(R.dimen.d_48);
        mMinDistance = getResources().getDimensionPixelSize(R.dimen.d_2);
        mAutoScrollDistance = getResources().getDimensionPixelSize(R.dimen.d_15);
    }

    /**
     * 事件view是否在顶部(WeekCalendarViw)
     *
     * @return
     */
    public boolean isWeekCalendarEventViewTop() {
        return wcev_booking_management_week_event_view.getWeekEventAdapter().getViews().
                get(wcev_booking_management_week_event_view.getCurrentItem()).getScollY();
    }

    private class OnScheduleScrollListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * 滑动处理
             */
            onCalendarScroll(distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    /**
     * 初始化事件的监听
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new OnScheduleScrollListener());
    }

    /**
     * 默认选择当前的时间
     */
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        resetCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 当xml文件加载完毕的时候调用的方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mcv_booking_management_month_calendar = (MonthCalendarView) findViewById(R.id.mcv_booking_management_month_calendar);
        wcv_booking_management_week_calendar = (WeekCalendarView) findViewById(R.id.wcv_booking_management_week_calendar);
        rl_booking_management_month_calendar = (RelativeLayout) findViewById(R.id.rl_booking_management_month_calendar);
        wcev_booking_management_week_event_view = (WeekCalendarEventView) findViewById(R.id.wcev_booking_management_week_event_view);
        bindingMonthAndWeekCalendar();
    }

    /**
     * 绑定月历和周历控件
     */
    private void bindingMonthAndWeekCalendar() {
        mcv_booking_management_month_calendar.setOnCalendarClickListener(mMonthCalendarClickListener);
        wcv_booking_management_week_calendar.setOnCalendarClickListener(mWeekCalendarClickListener);
        wcev_booking_management_week_event_view.setOnCalendarClickListener(mWeekCalendarEventClickListener);

        // 初始化视图
        wcv_booking_management_week_calendar.setVisibility(VISIBLE);
        /**
         * 默认关闭事件视图(默认的话是月历隐藏,周历显示)
         */
        mState = ScheduleState.CLOSE;
        Calendar calendar = Calendar.getInstance();
        int row = DateUtil.instance().getWeekRow(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        /**
         * 向上移动多少行的高度
         */
        rl_booking_management_month_calendar.setY(-row * mWeekCalendarHeight);
        /**
         * 设置事件控件向上移动多少
         */
        wcev_booking_management_week_event_view.setY(wcev_booking_management_week_event_view.getY() - 5 * mWeekCalendarHeight);
//        rl_booking_management_week_calendar_event_view.setY(rl_booking_management_week_calendar_event_view.getY() - 5 * mWeekCalendarHeight);
    }

    /**
     * 重置选中的年月日
     *
     * @param year
     * @param month
     * @param day
     */
    private void resetCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    /**
     * 周历事件view的监听回调
     */
    private OnCalendarClickListener mWeekCalendarEventClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day, int position, DateTime stratData, DateTime endData) {
            wcv_booking_management_week_calendar.setCurrentItem(position, false);
        }
    };
    /**
     * 月历的监听回调
     */
    private OnCalendarClickListener mMonthCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day, int position, DateTime stratData, DateTime endData) {
            initOnMonthCalendarChecked(year, month, day);
        }
    };
    /**
     * 周历的监听回调
     */
    private OnCalendarClickListener mWeekCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day, int position, DateTime stratData, DateTime endData) {
            initOnWeekCalendarChecked(year, month, day, position, stratData, endData);
        }

    };

    /**
     * 初始化当月历选中的时候调用的方法
     *
     * @param year
     * @param month
     * @param day
     */
    private void initOnMonthCalendarChecked(int year, int month, int day) {
        /**
         * 取消周历的点击事件监听
         */
        /**
         * 获取两个日期距离几周
         */
        int weeks = DateUtil.instance().getWeeksAgo(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay, year, month, day);
        /**
         * 重置选择的日期
         */
        resetCurrentSelectDate(year, month, day);
        /**
         * 如果不是在一周的话
         */
        int positions = wcv_booking_management_week_calendar.getCurrentItem();
        if (weeks != 0) {
            positions = wcv_booking_management_week_calendar.getCurrentItem() + weeks;
            wcv_booking_management_week_calendar.setCurrentItem(positions, false);
        }
    }


    /**
     * 初始化当周历控件选中之后的做的操作
     *
     * @param year
     * @param month
     * @param day
     * @param position
     * @param stratData
     * @param endData
     */
    public void initOnWeekCalendarChecked(int year, int month, int day, int position, DateTime stratData, DateTime endData) {
        wcev_booking_management_week_event_view.setOnCalendarClickListener(null);
        /**
         * 取消月历控件的注册
         */
        mcv_booking_management_month_calendar.setOnCalendarClickListener(null);
        /**
         * 计算两个日期距离几个月
         */
        int months = DateUtil.instance().getMonthsAgo(mCurrentSelectYear, mCurrentSelectMonth, year, month);
        /**
         * 设置当前的选中时间
         */
        resetCurrentSelectDate(year, month, day);
        /**
         * 如果不是同一个月份
         */
        if (months != 0) {
            /**
             * 改变选中的月份
             */
            int positions = mcv_booking_management_month_calendar.getCurrentItem() + months;
            /**
             * 如果选中的是当前选中的月份的话,那就不要再去选中了
             */
            if (positions != mcv_booking_management_month_calendar.getCurrentItem()) {
                mcv_booking_management_month_calendar.setCurrentItem(positions, false);
            }
        }
        /**
         * 重新设置
         */
        wcev_booking_management_week_event_view.setCurrentItem(position, false);
        wcev_booking_management_week_event_view.setOnCalendarClickListener(mWeekCalendarEventClickListener);
        mcv_booking_management_month_calendar.setOnCalendarClickListener(mMonthCalendarClickListener);
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day, position, stratData, endData);
        }
        /**
         * 设置选中的月历的选中的日期行
         *
         */
        MonthView monthView = mcv_booking_management_month_calendar.getCurrentMonthView();
        monthView.setSelectedRow(day);
    }

    /**
     * '测绘高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);

        resetViewHeight(wcev_booking_management_week_event_view, height - mWeekCalendarHeight);
        resetViewHeight(this, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 重置view的高度
     *
     * @param view
     * @param height
     */
    private void resetViewHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.height != height) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 只要有触摸事件都会调用这个方法
     *
     * @param ev 事件
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = ev.getRawX();
                mDownPosition[1] = ev.getRawY();
                mGestureDetector.onTouchEvent(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否拦截事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsScrolling) {
            return true;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                /**
                 * 判断滑动的位置,如果说月历是战士的话,那么就不让下边的事件view进行滑动
                 */
//                if (mState == ScheduleState.OPEN) {
//                    return true;
//                }
                wcev_booking_management_week_event_view.setState(mState);
                float x = ev.getRawX();
                float y = ev.getRawY();
                float distanceX = Math.abs(x - mDownPosition[0]);
                float distanceY = Math.abs(y - mDownPosition[1]);
                if (distanceY > mMinDistance && distanceY > distanceX * 2.0f) {
                    return (y > mDownPosition[1] && mState == ScheduleState.CLOSE && isWeekCalendarEventViewTop()) ||
                            (y < mDownPosition[1] && mState == ScheduleState.OPEN && isWeekCalendarEventViewTop());
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = event.getRawX();
                mDownPosition[1] = event.getRawY();
                resetCalendarPosition();
                return true;
            case MotionEvent.ACTION_MOVE:
                transferEvent(event);
                mIsScrolling = true;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                transferEvent(event);
                changeCalendarState();
                resetScrollingState();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void transferEvent(MotionEvent event) {
        if (mState == ScheduleState.CLOSE) {
            mcv_booking_management_month_calendar.setVisibility(VISIBLE);
            wcv_booking_management_week_calendar.setVisibility(INVISIBLE);
            mGestureDetector.onTouchEvent(event);
        } else {
            mGestureDetector.onTouchEvent(event);
        }
    }

    /**
     * 改变日历的状态
     */
    private void changeCalendarState() {
        if (wcev_booking_management_week_event_view.getY() > mWeekCalendarHeight * 2 &&
                wcev_booking_management_week_event_view.getY() < mcv_booking_management_month_calendar.getHeight() - mWeekCalendarHeight) { // 位于中间
            ScheduleAnimation animation = new ScheduleAnimation(this, mState, mAutoScrollDistance);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    changeState();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            wcev_booking_management_week_event_view.startAnimation(animation);
        } else if (wcev_booking_management_week_event_view.getY() <= mWeekCalendarHeight * 2) { // 位于顶部
            ScheduleAnimation animation = new ScheduleAnimation(this, ScheduleState.OPEN, mAutoScrollDistance);
            animation.setDuration(50);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == ScheduleState.OPEN) {
                        changeState();
                    } else {
                        resetCalendar();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            wcev_booking_management_week_event_view.startAnimation(animation);
        } else {
            ScheduleAnimation animation = new ScheduleAnimation(this, ScheduleState.CLOSE, mAutoScrollDistance);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == ScheduleState.CLOSE) {
                        mState = ScheduleState.OPEN;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            wcev_booking_management_week_event_view.startAnimation(animation);
        }
    }

    private void resetCalendarPosition() {
        if (mState == ScheduleState.OPEN) {
            rl_booking_management_month_calendar.setY(0);
            wcev_booking_management_week_event_view.setY(mcv_booking_management_month_calendar.getHeight());
        } else {
            rl_booking_management_month_calendar.setY(-DateUtil.instance().getWeekRow(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay) * mWeekCalendarHeight);
            wcev_booking_management_week_event_view.setY(mWeekCalendarHeight);
        }
    }

    /**
     * 重置控件的状态
     */
    private void resetCalendar() {
        if (mState == ScheduleState.OPEN) {
            mcv_booking_management_month_calendar.setVisibility(VISIBLE);
            wcv_booking_management_week_calendar.setVisibility(INVISIBLE);
        } else {
            mcv_booking_management_month_calendar.setVisibility(INVISIBLE);
            wcv_booking_management_week_calendar.setVisibility(VISIBLE);
        }
    }

    /**
     * 改变状态
     */
    private void changeState() {
        if (mState == ScheduleState.OPEN) {
            mState = ScheduleState.CLOSE;
            mcv_booking_management_month_calendar.setVisibility(INVISIBLE);
            wcv_booking_management_week_calendar.setVisibility(VISIBLE);
            /**
             * 动画结束之后设置的高度
             */
            rl_booking_management_month_calendar.setY((-mcv_booking_management_month_calendar.getCurrentMonthView().getWeekRow()) * mWeekCalendarHeight);
        } else {
            mState = ScheduleState.OPEN;
            mcv_booking_management_month_calendar.setVisibility(VISIBLE);
            wcv_booking_management_week_calendar.setVisibility(INVISIBLE);
            rl_booking_management_month_calendar.setY(0);
        }
    }

    /**
     * 重置滚动的状态
     */
    private void resetScrollingState() {
        mDownPosition[0] = 0;
        mDownPosition[1] = 0;
        mIsScrolling = false;
    }

    /**
     * 处理页面的滑动
     *
     * @param distanceY
     */
    public void onCalendarScroll(float distanceY) {
        /**
         * 得到月历控件
         */
        MonthView monthView = mcv_booking_management_month_calendar.getCurrentMonthView();
        distanceY = Math.min(distanceY, mAutoScrollDistance);
        float calendarDistanceY = distanceY / 5.0f;
        int row = monthView.getWeekRow();
        int calendarTop = -row * mWeekCalendarHeight;
        /**
         * 这是一个下边事件的距上的高度
         */
        int scheduleTop = mWeekCalendarHeight;
        float calendarY = rl_booking_management_month_calendar.getY() - calendarDistanceY * row;
        calendarY = Math.min(calendarY, 0);
        calendarY = Math.max(calendarY, calendarTop);
        rl_booking_management_month_calendar.setY(calendarY);
        float scheduleY = wcev_booking_management_week_event_view.getY() - distanceY;
        scheduleY = Math.min(scheduleY, mcv_booking_management_month_calendar.getHeight());
        scheduleY = Math.max(scheduleY, scheduleTop);
        wcev_booking_management_week_event_view.setY(scheduleY);
    }

    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    /**
     * 增加一周
     */
    public void plusWeek() {
        wcv_booking_management_week_calendar.plusWeek();
    }

    /**
     * 增加一个月
     */
    public void plusMonth() {
        mcv_booking_management_month_calendar.plusMonth();
    }

    /**
     * 减少一周
     */
    public void SubtractWeek() {
        wcv_booking_management_week_calendar.SubtractWeek();
    }

    /**
     * 减少一周
     */
    public void SubtractMonth() {
        mcv_booking_management_month_calendar.SubtractMonth();
    }

    /**
     * 跳转到当天
     */
    public void go2Today() {
        wcv_booking_management_week_calendar.go2Today();
        mcv_booking_management_month_calendar.go2Today();
    }
}
