package cn.yky.calendarevenview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.bean.AppointMentAndSchduleListBean;
import cn.yky.calendarevenview.bean.WeekCalendarEventBean;
import cn.yky.calendarevenview.utils.DateUtil;
import cn.yky.calendarevenview.utils.ListUtils;
import cn.yky.calendarevenview.utils.StringUtils;

/**
 * Created by yukuoyuan on 2017/6/21.
 * 这是一个带展示的周历的控件
 */
public class WeekEventView extends View {
    /**
     * 左侧时间字体的画笔
     */
    private Paint mLeftHourTextPaint;
    /**
     * 左侧时间字体的高度
     */
    private float mLeftHourTextHeight;
    /**
     * 左侧时间字体的宽度
     */
    private float mLeftHourTextWidth;
    private int mMinimumFlingVelocity;
    /**
     * 左侧时间字体的大小
     */
    private int mLeftHourTextSize = 22;
    /**
     * 左侧时间字体所占区域的宽度
     */
    private int mHourWidth = 84;
    /**
     * 时间刻度的高度
     */
    private int mHourHeight = 70;
    /**
     * 每天横向可用的宽度
     */
    private float mWidthPerDay;
    /**
     * 小时分割线的画笔
     */
    private Paint mHourSeparatorPaint;

    /**
     * 数据源
     */
    private List<AppointMentAndSchduleListBean.ItemsBean> items = new ArrayList<>();
    /**
     * 过去时间的画笔
     */
    private Paint mPastTimePaint;
    /**
     * 预约的画笔
     */
    private Paint mAppointmentPaint;
    /**
     * 日程的画笔
     */
    private Paint mSchdulePaint;
    /**
     * 日程的字体的画笔
     */
    private Paint mSchduleTextPaint;
    /**
     * 预约字体的画笔
     */
    private Paint mAppointmentTextPaint;
    /**
     * 当前时间的线
     */
    private Paint mNowLinePaint;
    private Paint mAcorssLinePaint;


    /**
     * 滑动状态的枚举
     */
    private enum Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    /**
     * 默认滑动状态
     */
    private Direction mCurrentScrollDirection = Direction.NONE;
    /**
     * 默认用户突然的行为
     */
    private Direction mCurrentFlingDirection = Direction.NONE;

    /**
     * 上下文内容
     */
    private Context mContext;
    /**
     * 滑动监听
     */
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    /**
     * 当前的原点位置
     */
    private PointF mCurrentOrigin = new PointF(0f, 0f);
    /**
     * 每周开始的日期
     */
    private DateTime mStartDate;
    /**
     * 事件点击的监听
     */
    private EventClickListener eventClickListener;
    /**
     * 空白区域的监听
     */
    private EmptyClickListener emptyClickListener;
    /**
     * 所有事件块的集合
     */
    private List<WeekCalendarEventBean> weekCalendarEventBeanList = new ArrayList<>();
    /**
     * 滑动的监听器
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (mCurrentScrollDirection) {
                case NONE: {
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        if (distanceX > 0) {
                            mCurrentScrollDirection = Direction.LEFT;
                        } else {
                            mCurrentScrollDirection = Direction.RIGHT;
                        }
                    } else {
                        mCurrentScrollDirection = Direction.VERTICAL;
                    }
                    break;
                }
                case LEFT: {
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        mCurrentScrollDirection = Direction.RIGHT;
                    }
                    break;
                }
                case RIGHT: {
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        mCurrentScrollDirection = Direction.LEFT;
                    }
                    break;
                }
            }
            switch (mCurrentScrollDirection) {
                case VERTICAL:
                    mCurrentOrigin.y -= distanceY;
//                    LogUtil.d("移动的距离", mCurrentOrigin.y + "");
                    ViewCompat.postInvalidateOnAnimation(WeekEventView.this);
                    break;
            }
            return true;
        }

        /**
         * 用户按下,快速滑动后松开
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.forceFinished(true);
            mCurrentFlingDirection = mCurrentScrollDirection;
            switch (mCurrentFlingDirection) {
                case VERTICAL:
                    mScroller.fling(0,
                            (int) mCurrentOrigin.y,
                            0,
                            (int) velocityY,
                            0,
                            0,
                            (int) -(mHourHeight * 49 + mLeftHourTextHeight / 2 - getHeight()),
                            0);
                    break;
            }
            ViewCompat.postInvalidateOnAnimation(WeekEventView.this);
            return true;
        }

        /**
         * 按下屏幕
         * @param e
         * @return
         */
        @Override
        public boolean onDown(MotionEvent e) {
            goToNearestOrigin();
            return true;
        }

        /**'
         * 当单机的时候调用的方法
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!ListUtils.isEmpty(items) || eventClickListener != null || !ListUtils.isEmpty(weekCalendarEventBeanList)) {
                List<WeekCalendarEventBean> list = new ArrayList<>();

                for (WeekCalendarEventBean weekCalendarEventBean : weekCalendarEventBeanList) {
                    if (e.getX() > weekCalendarEventBean.left && e.getX() < weekCalendarEventBean.right && e.getY() > weekCalendarEventBean.top && e.getY() < weekCalendarEventBean.bottom) {
                        list.add(weekCalendarEventBean);
                    }
                }
                if (!ListUtils.isEmpty(list)) {
                    eventClickListener.onEventClick(list.get(list.size() - 1));
                    return super.onSingleTapUp(e);
                }
            }
            if (emptyClickListener != null && e.getX() > mHourWidth) {
                DateTime selectedTime = getTimeFromPoint(e.getX(), e.getY());

                emptyClickListener.onEmptyClick(selectedTime);
            }

            return super.onSingleTapUp(e);

        }
    };

    /**
     * 根据坐标轴获取时间
     *
     * @param x
     * @param y
     * @return
     */
    private DateTime getTimeFromPoint(float x, float y) {
        ArrayList<String> timelist = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int index = 0;
        while (index < 24) {
            calendar.set(01, 1, 1, index, 0, 0);
            Date d = calendar.getTime();
            calendar.add(Calendar.MINUTE, 30);
            Date d30 = calendar.getTime();
            SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm");
            timelist.add(myFmt.format(d));
            timelist.add(myFmt.format(d30));
            index++;
        }
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            float startx = mHourWidth + mWidthPerDay * (i + 1);
            if (x < startx) {
                /**
                 * 确定是哪一天的日期
                 */
                for (int hourNumber = 0; hourNumber <= 48; hourNumber++) {
                    float top = mCurrentOrigin.y + mHourHeight * hourNumber + mLeftHourTextHeight / 2 + mHourHeight;
                    if (y < top) {
                        if (hourNumber == 0) {
                            return null;
                        }
                        return new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), getHour(timelist.get(hourNumber - 1)), getMInutes(timelist.get(hourNumber - 1)));
                    }
                }
            }
        }
        return null;
    }


    public WeekEventView(Context context, DateTime dateTime) {
        this(context, null, dateTime);
    }

    public WeekEventView(Context mContext, TypedArray mArray, DateTime dateTime) {
        this(mContext, mArray, null, dateTime);
    }

    public WeekEventView(Context context, TypedArray mArray, AttributeSet attrs, DateTime dateTime) {
        this(context, mArray, attrs, 0, dateTime);
    }

    public WeekEventView(Context context, TypedArray mArray, AttributeSet attrs, int defStyleAttr, DateTime dateTime) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(mArray, dateTime);
        /**
         * 初始化控件的各个属性
         */
        //  TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekEventView, 0, 0);
        init();
        /**
         * 开始时间
         */
        mStartDate = dateTime;
    }

    private void initAttrs(TypedArray mArray, DateTime dateTime) {
        /**
         * 左侧字体的字体大小
         */
        mLeftHourTextSize = mArray.getDimensionPixelSize(R.styleable.WeekCalendarEventView_leftHourTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mLeftHourTextSize, mContext.getResources().getDisplayMetrics()));
        /**
         * 左侧时间刻度的高度
         */
        mHourHeight = mArray.getDimensionPixelSize(R.styleable.WeekCalendarEventView_mHourHeight, mHourHeight);
        /**
         * 左侧时间刻度的宽度
         */
        mHourWidth = mArray.getDimensionPixelSize(R.styleable.WeekCalendarEventView_mHourWidth, mHourWidth);
    }

    /**
     * 初始化
     */
    private void init() {
        /**
         * 初始化滑动监听
         */
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mScroller = new OverScroller(mContext, new FastOutLinearInInterpolator());
        mMinimumFlingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();

        /**
         * 初始化左侧时间字体
         */
        mLeftHourTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftHourTextPaint.setTextAlign(Paint.Align.RIGHT);
        mLeftHourTextPaint.setTextSize(mLeftHourTextSize);
        mLeftHourTextPaint.setColor(Color.parseColor("#666666"));
        Rect rect = new Rect();
        mLeftHourTextPaint.getTextBounds("00:00", 0, "23:00".length(), rect);
        mLeftHourTextHeight = rect.height();
        /**
         * 初始化分割线的画笔
         */
        mHourSeparatorPaint = new Paint();
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(1);
        mHourSeparatorPaint.setColor(Color.parseColor("#dbdbdb"));
        /**
         * 过去时间的画笔
         */
        mPastTimePaint = new Paint();
        mPastTimePaint.setColor(Color.parseColor("#7Fdbdbdb"));
        /**
         * 当前时间的线
         */
        mNowLinePaint = new Paint();
        mNowLinePaint.setColor(Color.parseColor("#FF0000"));
        /**
         *这是一个预约的画笔
         */
        mAppointmentPaint = new Paint();
        mAppointmentPaint.setColor(Color.parseColor("#7a80fa"));
        /**
         * 这是一个日程的 画笔
         */
        mSchdulePaint = new Paint();
        mSchdulePaint.setColor(Color.parseColor("#fed1aa"));
        /**
         * 这是一个日程的字体的画笔
         */
        mSchduleTextPaint = new Paint();
        mSchduleTextPaint.setStyle(Paint.Style.FILL);   //
        mSchduleTextPaint.setColor(Color.parseColor("#ffffff"));
        mSchduleTextPaint.setTextSize(26);
        /**
         * 这是一个预约的字体的画笔
         */
        mAppointmentTextPaint = new Paint();
        mAppointmentTextPaint.setStyle(Paint.Style.FILL);   //
        mAppointmentTextPaint.setColor(Color.parseColor("#ffffff"));
        mAppointmentTextPaint.setTextSize(26);
        /**
         * 绘制交叉区域的线
         */
        mAcorssLinePaint = new Paint();
        mAcorssLinePaint.setColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 绘制左侧的时间刻度
         */
        drawLeftHoursTime(canvas);
        /**
         * 绘制所有的线
         */
        drawLines(canvas);
        /**
         * 绘制所有的事件区域
         */
        drawAllEvents(canvas);
    }

    /**
     * 绘制所有的事件区域
     *
     * @param canvas
     */
    private void drawAllEvents(Canvas canvas) {
        weekCalendarEventBeanList.clear();
        /**
         * 计算每天的宽度
         */
        mWidthPerDay = (getWidth() - mHourWidth) / 7;
        /**
         * 绘制过去的时间的颜色为暗色
         */
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            /**
             * 看这一天是否是过去的时间
             */
            if (date.toDate().getTime() < DateUtil.instance().getNowDatesTime()) {
                /**
                 * 过去的时间
                 */
                float botoom = mCurrentOrigin.y + mHourHeight * 48 + mLeftHourTextHeight / 2 + mHourHeight;
                canvas.drawRect(mHourWidth + mWidthPerDay * i,
                        mCurrentOrigin.y,
                        mHourWidth + mWidthPerDay * (i + 1),
                        botoom,
                        mPastTimePaint);

            } else if (DateUtil.instance().getNowDatesTime(DateUtil.instance().getDate(date.toDate())) == DateUtil.instance().getNowDatesTime()) {
                /**
                 * 当前天
                 */
                if (Calendar.getInstance().get(Calendar.MINUTE) / 30f > 0 && Calendar.getInstance().get(Calendar.MINUTE) / 30f < 1) {
                    canvas.drawRect((mHourWidth + mWidthPerDay * i),
                            mCurrentOrigin.y,
                            mHourWidth + mWidthPerDay * (i + 1),
                            mHourHeight * (1 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 2f + 1)
                                    + mCurrentOrigin.y + mLeftHourTextHeight / 2, mPastTimePaint);
                } else {
                    canvas.drawRect((mHourWidth + mWidthPerDay * i),
                            mCurrentOrigin.y,
                            mHourWidth + mWidthPerDay * (i + 1),
                            mHourHeight * (1 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 2f + 2)
                                    + mCurrentOrigin.y + 1 + mLeftHourTextHeight / 2, mPastTimePaint);
                }

            } else {
                /**
                 * 未来的时间
                 */
            }

        }
        /**
         * 开始绘制所有的预约或者日程的事件
         */

        if (!ListUtils.isEmpty(items)) {
            /**
             * 绘制日程
             */
            for (AppointMentAndSchduleListBean.ItemsBean itemsBean : items) {
                /**
                 * 矩形的顶部
                 */
                float top = 0f;
                /**
                 * 矩形的左边
                 */
                float left = 0f;
                /**
                 * 矩形的右边
                 */
                float right = 0f;
                /**
                 * 矩形的底部
                 */
                float bottom = 0f;
                /**
                 * 首先判断开始时间
                 */
                for (int i = 0; i < 7; i++) {
//                    LogUtil.d("事件2",itemsBean.Id);

                    DateTime date = mStartDate.plusDays(i);
                    /**
                     * 如果开始时间是这一天的话
                     */
                    if (DateUtil.instance().getYearMonthDay(itemsBean.StartTime).equals(DateUtil.instance().getYearMonthDay(itemsBean.EndTime))) {
                        if (DateUtil.instance().getYearMonthDay(itemsBean.StartTime).equals(DateUtil.instance().getDate(date.toDate()))) {
                            /**
                             * 判断结束时间
                             */
                            top = mLeftHourTextHeight / 2 + mHourHeight * (1 + Integer.parseInt(DateUtil.instance().getHour(itemsBean.StartTime)) * 2f) + mHourHeight * 2 * (Integer.parseInt(DateUtil.instance().getMinutes(itemsBean.StartTime)) / 60f)
                                    + mCurrentOrigin.y;
                            /**
                             * 如果结束的时间也是当天的话
                             */
                            if (DateUtil.instance().getYearMonthDay(itemsBean.EndTime).equals(DateUtil.instance().getDate(date.toDate()))) {
                                left = mHourWidth + mWidthPerDay * i;
                                right = mHourWidth + mWidthPerDay * (i + 1);
                                bottom = mLeftHourTextHeight / 2 + mHourHeight * (1 + Integer.parseInt(DateUtil.instance().getHour(itemsBean.EndTime)) * 2f) + mHourHeight * 2 * (Integer.parseInt(DateUtil.instance().getMinutes(itemsBean.EndTime)) / 60f)
                                        + mCurrentOrigin.y;
                                /**
                                 * 预约的
                                 */
                                if (itemsBean.Type == 3) {
                                    /**
                                     * 是否是过去时间设置不同的颜色
                                     */
//                                    if (date.toDate().getTime() < DateUtil.getNowDatesTime()) {
//                                        mSchdulePaint.setColor(Color.parseColor("#7Ffed1aa"));
//                                    } else {
                                    mSchdulePaint.setColor(Color.parseColor("#fed1aa"));
//                                    }
                                    WeekCalendarEventBean weekCalendarEventBean = new WeekCalendarEventBean();
                                    weekCalendarEventBean.left = left;
                                    weekCalendarEventBean.top = top;
                                    weekCalendarEventBean.right = right;
                                    weekCalendarEventBean.bottom = bottom;
                                    weekCalendarEventBean.id = itemsBean.Id;
                                    weekCalendarEventBean.type = itemsBean.Type;
                                    weekCalendarEventBeanList.add(weekCalendarEventBean);
                                    /**
                                     * 日程的
                                     */
                                    canvas.drawRect(left, top, right - 1, bottom, mSchdulePaint);
                                    /**
                                     * 绘制字体
                                     */
                                    Rect rect = new Rect();
                                    mSchduleTextPaint.getTextBounds(StringUtils.formatName(itemsBean.Name, 5), 0, StringUtils.formatName(itemsBean.Name, 5).length(), rect);
                                    float texttop = top + (bottom - top - rect.height()) / 2 + rect.height();
                                    float textwidth = 0f;
                                    textwidth = Math.max(textwidth, mSchduleTextPaint.measureText(StringUtils.formatName(itemsBean.Name, 5)));
                                    float textleft = left + (right - left - textwidth) / 2;
                                    canvas.drawText(StringUtils.formatName(itemsBean.Name, 5), textleft, texttop, mSchduleTextPaint);
                                    /**
                                     * 绘制白线
                                     */
                                    canvas.drawLine(left, top - 1, right - 1, top - 1, mAcorssLinePaint);
                                }
                            }
                        }
                    } else {
                        /**
                         * 结束的时候是跨天的情况的话,
                         */
                        HashMap<DateTime, DateTime> dateTimeHashMap = DateUtil.instance().getDateList(itemsBean.StartTime, itemsBean.EndTime);
                        for (DateTime key : dateTimeHashMap.keySet()) {
                            DateTime value = dateTimeHashMap.get(key);
//                        Log.d("时间区域", dateTimeHashMap.size() + "Key = " + DateUtil.getYearMonthDay(key) + ", Value = " + DateUtil.getYearMonthDay(value));
                            if (DateUtil.instance().getYearMonthDay(date).equals(DateUtil.instance().getYearMonthDay(key))) {
                                if (key.getHourOfDay() == 0) {
                                    top = mCurrentOrigin.y;
                                } else {
                                    top = mLeftHourTextHeight / 2 + mHourHeight * ((1 + key.getHourOfDay()) * 2f) + mHourHeight * 2 * (key.getMinuteOfHour() / 60f)
                                            + mCurrentOrigin.y;
                                }
                                left = mHourWidth + mWidthPerDay * i;
                                right = mHourWidth + mWidthPerDay * (i + 1);
                                bottom = mLeftHourTextHeight / 2 + mHourHeight * ((1 + value.getHourOfDay()) * 2f) + mHourHeight * 2 * (value.getMinuteOfHour() / 60f)
                                        + mCurrentOrigin.y;
                                /**
                                 * 预约的
                                 */
                                if (itemsBean.Type == 3) {
                                    /**
                                     * 是否是过去时间设置不同的颜色
                                     */
                                    mSchdulePaint.setColor(Color.parseColor("#fed1aa"));
                                    WeekCalendarEventBean weekCalendarEventBean = new WeekCalendarEventBean();
                                    weekCalendarEventBean.left = left;
                                    weekCalendarEventBean.top = top;
                                    weekCalendarEventBean.right = right;
                                    weekCalendarEventBean.bottom = bottom;
                                    weekCalendarEventBean.id = itemsBean.Id;
                                    weekCalendarEventBean.type = itemsBean.Type;
                                    weekCalendarEventBeanList.add(weekCalendarEventBean);
                                    /**
                                     * 日程的
                                     */
                                    canvas.drawRect(left, top, right - 1, bottom, mSchdulePaint);
                                    /**
                                     * 绘制字体
                                     */
                                    Rect rect = new Rect();
                                    mSchduleTextPaint.getTextBounds(StringUtils.formatName(itemsBean.Name, 5), 0, StringUtils.formatName(itemsBean.Name, 5).length(), rect);
                                    float texttop = top + (bottom - top - rect.height()) / 2 + rect.height();
                                    float textwidth = 0f;
                                    textwidth = Math.max(textwidth, mSchduleTextPaint.measureText(StringUtils.formatName(itemsBean.Name, 5)));
                                    float textleft = left + (right - left - textwidth) / 2;
                                    canvas.drawText(StringUtils.formatName(itemsBean.Name, 5), textleft, texttop, mSchduleTextPaint);
                                    /**
                                     * 绘制白线
                                     */
                                    canvas.drawLine(left, top - 1, right - 1, top - 1, mAcorssLinePaint);
                                }
                            }
                        }
                    }
                }
            }
            /**
             * 绘制预约
             */
            for (AppointMentAndSchduleListBean.ItemsBean itemsBean : items) {
                /**
                 * 矩形的顶部
                 */
                float top = 0f;
                /**
                 * 矩形的左边
                 */
                float left = 0f;
                /**
                 * 矩形的右边
                 */
                float right = 0f;
                /**
                 * 矩形的底部
                 */
                float bottom = 0f;
                /**
                 * 首先判断开始时间
                 */
                for (int i = 0; i < 7; i++) {
                    DateTime date = mStartDate.plusDays(i);
                    /**
                     * 如果开始时间是这一天的话
                     */
                    if (DateUtil.instance().getYearMonthDay(itemsBean.StartTime).equals(DateUtil.instance().getYearMonthDay(itemsBean.EndTime))) {
                        if (DateUtil.instance().getYearMonthDay(itemsBean.StartTime).equals(DateUtil.instance().getDate(date.toDate()))) {
                            /**
                             * 判断结束时间
                             */
                            top = mLeftHourTextHeight / 2 + mHourHeight * (1 + Integer.parseInt(DateUtil.instance().getHour(itemsBean.StartTime)) * 2f) + mHourHeight * 2 * (Integer.parseInt(DateUtil.instance().getMinutes(itemsBean.StartTime)) / 60f)
                                    + mCurrentOrigin.y;
                            /**
                             * 如果结束的时间也是当天的话
                             */
                            if (DateUtil.instance().getYearMonthDay(itemsBean.EndTime).equals(DateUtil.instance().getDate(date.toDate()))) {
                                left = mHourWidth + mWidthPerDay * i;
                                right = mHourWidth + mWidthPerDay * (i + 1);
                                bottom = mLeftHourTextHeight / 2 + mHourHeight * (1 + Integer.parseInt(DateUtil.instance().getHour(itemsBean.EndTime)) * 2f) + mHourHeight * 2 * (Integer.parseInt(DateUtil.instance().getMinutes(itemsBean.EndTime)) / 60f)
                                        + mCurrentOrigin.y;
                                /**
                                 * 预约的
                                 */
                                if (itemsBean.Type == 1) {
                                    /**
                                     * 是否是过去时间设置不同的颜色
                                     */
                                    mAppointmentPaint.setColor(Color.parseColor("#7a80fa"));
                                    WeekCalendarEventBean weekCalendarEventBean = new WeekCalendarEventBean();
                                    weekCalendarEventBean.left = left;
                                    weekCalendarEventBean.top = top;
                                    weekCalendarEventBean.right = right;
                                    weekCalendarEventBean.bottom = bottom;
                                    weekCalendarEventBean.id = itemsBean.Id;
                                    weekCalendarEventBean.type = itemsBean.Type;
                                    weekCalendarEventBeanList.add(weekCalendarEventBean);
                                    canvas.drawRect(left, top, right - 1, bottom, mAppointmentPaint);
                                    /**
                                     * 绘制字体
                                     */
                                    Rect rect = new Rect();
                                    mAppointmentTextPaint.getTextBounds(StringUtils.formatName(itemsBean.Patient.PatientName, 5), 0, StringUtils.formatName(itemsBean.Patient.PatientName, 5).length(), rect);
                                    float texttop = top + (bottom - top - rect.height()) / 2 + rect.height();
                                    float textwidth = 0f;
                                    textwidth = Math.max(textwidth, mSchduleTextPaint.measureText(StringUtils.formatName(itemsBean.Patient.PatientName, 5)));
                                    float textleft = left + (right - left - textwidth) / 2;
                                    canvas.drawText(StringUtils.formatName(itemsBean.Patient.PatientName, 5), textleft, texttop, mAppointmentTextPaint);
                                    /**
                                     * 绘制白线
                                     */
                                    canvas.drawLine(left, top - 1, right - 1, top - 1, mAcorssLinePaint);
                                }
                            }
                        }
                    } else {
                        /**
                         * 结束的时候是跨天的情况的话,
                         */
                        HashMap<DateTime, DateTime> dateTimeHashMap = DateUtil.instance().getDateList(itemsBean.StartTime, itemsBean.EndTime);
                        for (DateTime key : dateTimeHashMap.keySet()) {
                            DateTime value = dateTimeHashMap.get(key);
//                        Log.d("时间区域", dateTimeHashMap.size() + "Key = " + DateUtil.getYearMonthDay(key) + ", Value = " + DateUtil.getYearMonthDay(value));
                            if (DateUtil.instance().getYearMonthDay(date).equals(DateUtil.instance().getYearMonthDay(key))) {
                                if (key.getHourOfDay() == 0) {
                                    top = mCurrentOrigin.y;
                                } else {
                                    top = mLeftHourTextHeight / 2 + mHourHeight * ((1 + key.getHourOfDay()) * 2f) + mHourHeight * 2 * (key.getMinuteOfHour() / 60f)
                                            + mCurrentOrigin.y;
                                }
                                left = mHourWidth + mWidthPerDay * i;
                                right = mHourWidth + mWidthPerDay * (i + 1);
                                bottom = mLeftHourTextHeight / 2 + mHourHeight * ((1 + value.getHourOfDay()) * 2f) + mHourHeight * 2 * (value.getMinuteOfHour() / 60f)
                                        + mCurrentOrigin.y;
                                /**
                                 * 预约的
                                 */
                                if (itemsBean.Type == 1) {
                                    /**
                                     * 是否是过去时间设置不同的颜色
                                     */
//                                    if (date.toDate().getTime() < DateUtil.getNowDatesTime()) {
//                                        mAppointmentPaint.setColor(Color.parseColor("#7F7a80fa"));
//                                    } else {
                                    mAppointmentPaint.setColor(Color.parseColor("#7a80fa"));
//                                    }
                                    WeekCalendarEventBean weekCalendarEventBean = new WeekCalendarEventBean();
                                    weekCalendarEventBean.left = left;
                                    weekCalendarEventBean.top = top;
                                    weekCalendarEventBean.right = right;
                                    weekCalendarEventBean.bottom = bottom;
                                    weekCalendarEventBean.id = itemsBean.Id;
                                    weekCalendarEventBean.type = itemsBean.Type;
                                    weekCalendarEventBeanList.add(weekCalendarEventBean);
                                    canvas.drawRect(left, top, right - 1, bottom, mAppointmentPaint);
                                    /**
                                     * 绘制字体
                                     */
                                    Rect rect = new Rect();
                                    mAppointmentTextPaint.getTextBounds(StringUtils.formatName(itemsBean.Patient.PatientName, 5), 0, StringUtils.formatName(itemsBean.Patient.PatientName, 5).length(), rect);
                                    float texttop = top + (bottom - top - rect.height()) / 2 + rect.height();
                                    float textwidth = 0f;
                                    textwidth = Math.max(textwidth, mSchduleTextPaint.measureText(StringUtils.formatName(itemsBean.Patient.PatientName, 5)));
                                    float textleft = left + (right - left - textwidth) / 2;
                                    canvas.drawText(StringUtils.formatName(itemsBean.Patient.PatientName, 5), textleft, texttop, mAppointmentTextPaint);
                                    /**
                                     * 绘制白线
                                     */
                                    canvas.drawLine(left, top - 1, right - 1, top - 1, mAcorssLinePaint);
                                }
                            }
                        }
                    }
                }
            }
        }
        /**
         * 绘制过去的时间的颜色为暗色
         */
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            if (DateUtil.instance().getNowDatesTime(DateUtil.instance().getDate(date.toDate())) == DateUtil.instance().getNowDatesTime()) {
                /**
                 * 绘制当前时间的线
                 */
                mNowLinePaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mHourWidth + mWidthPerDay * i, mHourHeight * (1 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 2f) + mHourHeight * 2 * (Calendar.getInstance().get(Calendar.MINUTE) / 60f)
                        + mCurrentOrigin.y + mLeftHourTextHeight / 2, 5, mNowLinePaint);
                canvas.drawLine(mHourWidth + mWidthPerDay * i,
                        mHourHeight * (1 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 2f) + mHourHeight * 2 * (Calendar.getInstance().get(Calendar.MINUTE) / 60f)
                                + mCurrentOrigin.y + mLeftHourTextHeight / 2,
                        mHourWidth + mWidthPerDay * (i + 1),
                        mHourHeight * (1 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 2f) + mHourHeight * 2 * (Calendar.getInstance().get(Calendar.MINUTE) / 60f)
                                + mCurrentOrigin.y + mLeftHourTextHeight / 2, mNowLinePaint);
            }
        }

    }

    /**
     * 绘制线
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {

        /**
         * 准备迭代每个小时来画小时线。
         */
        //绘制第一条线
        canvas.drawLine(0, 0, getWidth(), 1, mHourSeparatorPaint);
        for (int hourNumber = 0; hourNumber <= 48; hourNumber++) {
            float top = mCurrentOrigin.y + mHourHeight * hourNumber + mLeftHourTextHeight / 2 + mHourHeight;
            canvas.drawLine(mHourWidth, top, getWidth(), top + 1, mHourSeparatorPaint);
        }
        /**
         * 准备迭代绘制竖线
         */
        for (int i = 0; i <= 7; i++) {
            float startx = mHourWidth + mWidthPerDay * i;
            canvas.drawLine(startx - 1, mCurrentOrigin.y, startx, 48 * mHourHeight, mHourSeparatorPaint);
        }
    }

    /**
     * 绘制左侧的时间刻度
     *
     * @param canvas 画布
     */
    private void drawLeftHoursTime(Canvas canvas) {
        /**
         * 计算每天的宽度
         */
        mWidthPerDay = (getWidth() - mHourWidth) / 7;
        /**
         * 如果当前位置无效,使其有效
         */
        if (mCurrentOrigin.y < getHeight() - (mHourHeight * 49 + mLeftHourTextHeight / 2))
            mCurrentOrigin.y = getHeight() - (mHourHeight * 49 + mLeftHourTextHeight / 2);
        /**
         * 不要放一个“else if”，因为它会在完全放大时触发一个小错误。
         */
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }
        /**
         * 绘制时间刻度
         */
        ArrayList<String> timelist = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int index = 0;
        while (index < 24) {
            calendar.set(01, 1, 1, index, 0, 0);
            Date d = calendar.getTime();
            calendar.add(Calendar.MINUTE, 30);
            Date d30 = calendar.getTime();
            SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm");
            timelist.add(myFmt.format(d));
            timelist.add(myFmt.format(d30));
            index++;
        }
        mLeftHourTextWidth = 0;
        for (int i = 0; i < timelist.size(); i++) {
            float top = mHourHeight + mCurrentOrigin.y + mHourHeight * i;
            /**
             * 绘制时间刻度文本
             */
            String time = timelist.get(i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            /**
             * 左侧时间的宽度
             */
            mLeftHourTextWidth = Math.max(mLeftHourTextWidth, mLeftHourTextPaint.measureText(time));
            float hourspading = (mHourWidth - mLeftHourTextWidth) / 2f;
            if (top < getHeight())
                canvas.drawText(time, mLeftHourTextWidth + hourspading, top + mLeftHourTextHeight, mLeftHourTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consume = mGestureDetector.onTouchEvent(event);
        return consume;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        /**
         * 判断滑动是否完成
         */
        if (mScroller.isFinished()) {
            if (mCurrentFlingDirection != Direction.NONE) {
                // 猛然的行为停止后
                goToNearestOrigin();
            }
        } else {
            if (mCurrentFlingDirection != Direction.NONE && mScroller.getCurrVelocity() <= mMinimumFlingVelocity) {
                goToNearestOrigin();
            } else if (mScroller.computeScrollOffset()) {
                mCurrentOrigin.y = mScroller.getCurrY();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    /**
     * 到达最近得起源位置
     */
    private void goToNearestOrigin() {
        mScroller.forceFinished(true);
        ViewCompat.postInvalidateOnAnimation(WeekEventView.this);
        // 重置所有的状态
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }

    /**
     * 滑动指定的小时
     */
    public void gotoHour(int hour) {
        int verticalOffset = 0;
        if (hour > 24) {
            verticalOffset = mHourHeight * 48;
        } else if (hour > 0) {
            verticalOffset = (mHourHeight * hour * 2);
        }
        if (verticalOffset > mHourHeight * 48 - getHeight())
            verticalOffset = (mHourHeight * 48 - getHeight());
        mCurrentOrigin.y = -verticalOffset;
        invalidate();
    }

    /**
     * 获取每一周的第一天
     *
     * @return
     */
    public DateTime getStratData() {
        return mStartDate;
    }

    /**
     * 获取每一周的最后一天
     *
     * @return
     */
    public DateTime getEndData() {
        return mStartDate.plusDays(7);
    }

    /**
     * 设置数据源
     *
     * @param items
     */
    public void setData(List<AppointMentAndSchduleListBean.ItemsBean> items) {
        this.items.clear();
        this.items.addAll(items);
        /**
         * 重绘界面
         */
        invalidate();
    }

    /**
     * 获取小时
     *
     * @param time
     * @return
     */
    public int getHour(String time) {
        return Integer.parseInt(time.substring(0, 2));
    }

    /**
     * 获取分钟
     *
     * @param time
     * @return
     */
    public int getMInutes(String time) {
        return Integer.parseInt(time.substring(3, 5));

    }
    /**
     * 以下是监听器---------------------------------------------------以下是监听器
     */
    /**
     * 监听事件的监听器
     */
    public interface EventClickListener {
        /**
         * 这是一个点击事件返回的方法
         *
         * @param event 返回的事件
         */
        void onEventClick(WeekCalendarEventBean event);
    }

    /**
     * 监听空白区域的点击事件的监听
     */
    public interface EmptyClickListener {
        /**
         * 这是一个监听空白区域回调的方法
         *
         * @param dateTime
         */
        void onEmptyClick(DateTime dateTime);
    }

    /**
     * 设置事件点击的监听
     *
     * @param eventClickListener
     */
    public void setOnEventClickListener(EventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;
    }

    /**
     * 设置空白区域点击的监听
     *
     * @param emptyClickListener
     */
    public void setOnEmptyClickListener(EmptyClickListener emptyClickListener) {
        this.emptyClickListener = emptyClickListener;
    }

    public boolean getScollY() {
        return mCurrentOrigin.y < 1 && mCurrentOrigin.y > -25;

//        return  mCurrentOrigin.y == 0;
    }

}
