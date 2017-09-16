package cn.yky.calendarevenview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.joda.time.DateTime;

import java.util.Calendar;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.utils.DateUtil;

/**
 * Created by yukuoyuan on 2017/6/6.
 * 这是一个周历的控件
 */
public class WeekView extends View {
    /**
     * 每一行的个数
     */
    private static final int NUM_COLUMNS = 7;
    /**
     * 画笔
     */
    private Paint mPaint;
    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGTodayColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mColumnSize, mRowSize, mSelectCircleSize;
    /**
     * 天的字体的大小
     */
    private int mDaySize;
    /**
     * 每周开始的日期
     */
    private DateTime mStartDate;
    private DisplayMetrics mDisplayMetrics;
    private int mSelYear, mSelMonth, mSelDay;

    /**
     * 触摸事件的处理
     */
    private GestureDetector mGestureDetector;

    private Paint redCirclePaint;

    public WeekView(Context context, DateTime dateTime) {
        this(context, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, DateTime dateTime) {
        this(context, array, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, DateTime dateTime) {
        this(context, array, attrs, 0, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, DateTime dateTime) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, dateTime);
        initPaint();
        initWeek();
        initGestureDetector();
    }

    private void initAttrs(TypedArray array, DateTime dateTime) {
        if (array != null) {
            /**
             * 选中天的颜色
             */
            mSelectDayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_text_color, Color.parseColor("#FFFFFF"));
            /**
             * 选中的天的今天的颜色
             */
            mSelectBGTodayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_today_color, Color.parseColor("#605bf5"));
            /**
             * 正常天的颜色
             */
            mNormalDayColor = array.getColor(R.styleable.WeekCalendarView_week_normal_text_color, Color.parseColor("#575471"));
            /**
             * 天的字体的大小
             */
            mDaySize = array.getInteger(R.styleable.WeekCalendarView_week_day_text_size, 14);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGTodayColor = Color.parseColor("#605bf5");
            mNormalDayColor = Color.parseColor("#333333");
            /**
             * 天的字体的大小
             */
            mDaySize = 14;
        }
        /**
         * 开始时间
         */
        mStartDate = dateTime;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);
        /**
         * 绘制红点的画笔
         */
        redCirclePaint = new Paint();
    }

    /**
     * 初始化周
     */
    private void initWeek() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        /**
         * 加七天之后的日期
         */
        DateTime endDate = mStartDate.plusDays(7);
    }

    /**
     * 初始化触摸事件的监听
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        /**
         * 绘制这一周
         */
        drawThisWeek(canvas);
    }

    /**
     * 初始化大小
     */
    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight();
        mSelectCircleSize = (int) (mColumnSize / 3.2);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

    /**
     * 绘制这一周
     */
    private void drawThisWeek(Canvas canvas) {
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            int day = date.getDayOfMonth();
            String dayString = String.valueOf(day);
            int startX = (int) (mColumnSize * i + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            /**
             * 如果这一天是当前天的话设置圆形背景
             */
            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    && date.getMonthOfYear() == DateUtil.instance().getNowMonths()
                    && date.getYear() == DateUtil.instance().getNowYear()) {
                int startRecX = mColumnSize * i;
                int endRecX = startRecX + mColumnSize;
                if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2, mSelectCircleSize, mPaint);
            }
            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    && date.getMonthOfYear() == DateUtil.instance().getNowMonths()
                    && date.getYear() == DateUtil.instance().getNowYear()) {
                mPaint.setColor(mSelectDayColor);
            } else {
                mPaint.setColor(mNormalDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);

        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
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
        return mStartDate.plusDays(6);
    }


    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }
}
