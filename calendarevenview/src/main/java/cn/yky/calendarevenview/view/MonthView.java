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
import cn.yky.calendarevenview.inter.OnMonthClickListener;
import cn.yky.calendarevenview.utils.DateUtil;

/**
 * Created by yukuoyuan on 2017/6/6.
 * 这是一个月历控件
 */
public class MonthView extends View {
    /**
     * 有几列
     */
    private static final int NUM_COLUMNS = 7;
    /**
     * 有几行
     */
    private static final int NUM_ROWS = 6;
    private Paint mPaint;
    private int mNormalDayColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mLastOrNextMonthTextColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    /**
     * 分别是每一列的宽度,每一行的高度,以及选中的圆的大小
     */
    private int mColumnSize, mRowSize, mSelectCircleSize;
    private int mDaySize;
    private int mWeekRow; // 当前月份第几周
    private int[][] mDaysText;
    private DisplayMetrics mDisplayMetrics;
    private OnMonthClickListener mDateClickListener;
    private GestureDetector mGestureDetector;
    private int SelectedRow = 10;//选中的哪一行

    private Paint redCirclePaint;

    public MonthView(Context context, int year, int month) {
        this(context, null, year, month);
    }

    public MonthView(Context context, TypedArray array, int year, int month) {
        this(context, array, null, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int year, int month) {
        this(context, array, attrs, 0, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, int year, int month) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, year, month);
        initPaint();
        initMonth();
        initGestureDetector();
    }

    /**
     * 初始化触摸事件
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    /**
     * 初始化各个属性
     *
     * @param array
     * @param year
     * @param month
     */
    private void initAttrs(TypedArray array, int year, int month) {
        if (array != null) {
            mSelectBGTodayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_today_color, Color.parseColor("#605bf5"));
            mNormalDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_text_color, Color.parseColor("#333333"));
            mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_today_text_color, Color.parseColor("#FFFFFF"));
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#999999"));
            mDaySize = array.getInteger(R.styleable.MonthCalendarView_month_day_text_size, 13);
        } else {
            /**
             * 今天的背景颜色
             */
            mSelectBGTodayColor = Color.parseColor("#605bf5");
            /**
             * 正常天的颜色
             */
            mNormalDayColor = Color.parseColor("#333333");
            /**
             * 当前天的颜色
             */
            mCurrentDayColor = Color.parseColor("#FFFFFF");
            /**
             * 夸月的颜色
             */
            mLastOrNextMonthTextColor = Color.parseColor("#999999");
            /**
             * 天字体的大小
             */
            mDaySize = 13;
        }
        mSelYear = year;
        mSelMonth = month;
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
     * 初始化月份
     */
    private void initMonth() {
        Calendar calendar = Calendar.getInstance();
        /**
         * 当前年
         */
        mCurrYear = calendar.get(Calendar.YEAR);
        /**
         * 当前月
         */
        mCurrMonth = calendar.get(Calendar.MONTH);
        /**
         * 当前日
         */
        mCurrDay = calendar.get(Calendar.DATE);
        /**
         * 如果选中是当前年的当前月
         */
        if (mSelYear == mCurrYear && mSelMonth == mCurrMonth) {
            /**
             * 设置选中为当前的时间
             */
            setSelectYearMonth(mSelYear, mSelMonth, mCurrDay);
        } else {
            setSelectYearMonth(mSelYear, mSelMonth, 1);
        }
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
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        clearData();
        drawLastMonth(canvas, false);
        drawThisMonth(canvas);
        drawNextMonth(canvas);
        initReadCircle();
    }

    /**
     * 获取红点的数据
     */
    private void initReadCircle() {

    }

    /**
     * 初始化大小
     */
    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
        mSelectCircleSize = (int) (mColumnSize / 3.2);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

    /**
     * 清楚数据
     */
    private void clearData() {
        mDaysText = new int[6][7];
    }

    /**
     * 绘制上一个月
     *
     * @param canvas
     * @param b
     */
    private void drawLastMonth(Canvas canvas, boolean b) {
        int lastYear, lastMonth;
        /**
         * 如果当前的选中的月份
         */
        if (mSelMonth == 0) {
            lastYear = mSelYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mSelYear;
            lastMonth = mSelMonth - 1;
        }
        /**
         * 获取当月多少天
         */
        int monthDays = DateUtil.instance().getMonthDays(lastYear, lastMonth);
        /**
         * 获取当月1号位于周几
         */
        int weekNumber = DateUtil.instance().getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < weekNumber - 1; day++) {
            mPaint.setColor(mLastOrNextMonthTextColor);
            mDaysText[0][day] = monthDays - weekNumber + day + 2;
            String dayString = String.valueOf(mDaysText[0][day]);
            int startX = (int) (mColumnSize * day + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
        if (b) {
            for (int day = 0; day < weekNumber - 1; day++) {
                /**
                 * 判断是否是第一行选中了
                 */
                if (SelectedRow == 0) {
                    mPaint.setColor(Color.parseColor("#33605bf5"));
                    int startRecX = mColumnSize * day;
                    int startRecY = 0;
                    int endRecX = startRecX + mColumnSize;
                    int endRecY = startRecY + mRowSize;
                    canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
                }
            }
        }
    }

    /**
     * 绘制当前月
     *
     * @param canvas
     */
    private void drawThisMonth(Canvas canvas) {
        String dayString;
        /**
         * 获取当月多少天
         */
        int monthDays = DateUtil.instance().getMonthDays(mSelYear, mSelMonth);
        /**
         * 获取当月1号位于周几
         */
        int weekNumber = DateUtil.instance().getFirstDayWeek(mSelYear, mSelMonth);

        for (int day = 0; day < monthDays; day++) {
            /**
             * 一个月的某一天
             */
            dayString = String.valueOf(day + 1);
            /**
             * 列
             */
            int column = (day + weekNumber - 1) % 7;
            /**
             *行
             */
            int row = (day + weekNumber - 1) / 7;
            mDaysText[row][column] = day + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            /**
             * 设置如果是当前天绘制圆
             */
            if (dayString.equals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "")
                    && Calendar.getInstance().get(Calendar.YEAR) == mSelYear
                    && Calendar.getInstance().get(Calendar.MONTH) == mSelMonth) {
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                mPaint.setColor(mSelectBGTodayColor);
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
            }

            /**
             * 如果当前天被选中了
             */
            if (dayString.equals(String.valueOf(mSelDay))) {
                SelectedRow = row;
            }
            /**
             * 设置字体的颜色
             */
            if (dayString.equals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "")
                    && Calendar.getInstance().get(Calendar.YEAR) == mSelYear
                    && Calendar.getInstance().get(Calendar.MONTH) == mSelMonth) {
                /**
                 * 当天
                 */
                mPaint.setColor(mCurrentDayColor);
            } else {
                /**
                 * 其他天
                 */
                mPaint.setColor(mNormalDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
        }
        for (int day = 0; day < monthDays; day++) {
            /**
             * 列
             */
            int column = (day + weekNumber - 1) % 7;
            /**
             *行
             */
            int row = (day + weekNumber - 1) / 7;
            /**
             * 如果当前行是选中的行的话
             */
            if (row == SelectedRow) {
                mPaint.setColor(Color.parseColor("#33605bf5"));
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
            }
        }
        drawLastMonth(canvas, true);
    }

    /**
     * 绘制下一个月
     *
     * @param canvas
     */
    private void drawNextMonth(Canvas canvas) {
        int monthDays = DateUtil.instance().getMonthDays(mSelYear, mSelMonth);
        int weekNumber = DateUtil.instance().getFirstDayWeek(mSelYear, mSelMonth);
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        int nextMonth = mSelMonth + 1;
        int nextYear = mSelYear;
        if (nextMonth == 12) {
            nextMonth = 0;
            nextYear += 1;
        }
        for (int day = 0; day < nextMonthDays; day++) {
            mPaint.setColor(mLastOrNextMonthTextColor);
            int column = (monthDays + weekNumber - 1 + day) % 7;
            int row = 5 - (nextMonthDays - day - 1) / 7;
            try {
                mDaysText[row][column] = day + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dayString = String.valueOf(mDaysText[row][column]);
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
            /**
             * 判断是否是第一行选中了
             */
            if (SelectedRow == row) {
                mPaint.setColor(Color.parseColor("#33605bf5"));
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
            }
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

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
        invalidate();
    }

    /**
     * 做点击事件的操作
     *
     * @param x
     * @param y
     */
    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int row = y / mRowSize;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;
        /**
         * 第一行被点击之后
         */
        if (row == 0) {
            if (mDaysText[row][column] >= 23) {
                /**
                 * 跳转到上个月
                 */
                if (mSelMonth == 0) {
                    clickYear = mSelYear - 1;
                    clickMonth = 11;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth - 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickLastMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                /**
                 * 点击了当月
                 */
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        } else {
            int monthDays = DateUtil.instance().getMonthDays(mSelYear, mSelMonth);
            int weekNumber = DateUtil.instance().getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            /**
             * 如果点击了下个月
             */
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                if (mSelMonth == 11) {
                    clickYear = mSelYear + 1;
                    clickMonth = 0;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth + 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickNextMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                /**
                 * 如果点击了不是这个月的日期
                 */
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        }
        /**
         * 点击的行数即选中的行
         */
        SelectedRow = row;
    }

    /**
     * 跳转到某日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void clickThisMonth(int year, int month, int day) {
        if (mDateClickListener != null) {
            mDateClickListener.onClickThisMonth(year, month, day);
        }
        setSelectYearMonth(year, month, day);
    }

    /**
     * 获取当前选择年
     *
     * @return
     */
    public int getSelectYear() {
        return mSelYear;
    }

    /**
     * 获取当前选择月
     *
     * @return
     */
    public int getSelectMonth() {
        return mSelMonth;
    }

    /**
     * 获取当前选择日
     *
     * @return
     */
    public int getSelectDay() {
        return this.mSelDay;
    }

    public int getRowSize() {
        return mRowSize;
    }

    public int getWeekRow() {
        return SelectedRow;
    }

    /**
     * 设置点击日期监听
     *
     * @param dateClickListener
     */
    public void setOnDateClickListener(OnMonthClickListener dateClickListener) {
        this.mDateClickListener = dateClickListener;
    }

    /**
     * 设置选择的行
     */
    public void setSelectedRow(int day) {
        this.mSelDay = day;
        postInvalidate();

    }

    /**
     * 这是一个获取开始时间
     *
     * @return
     */
    public DateTime getStartData() {
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (mDaysText == null) {
            /**
             * 得到1号是周几,然后补全
             */
            int week = DateUtil.instance().getDayWeek(mSelYear, mSelMonth, 1);
            return new DateTime(mSelYear, mSelMonth + 1, 1, 0, 0).minusDays(week - 1);
        }
        if (mDaysText[0][0] >= 23) {
            /**
             * 不是本月的数据
             */
            if (mSelMonth == 0) {
                clickYear = mSelYear - 1;
                clickMonth = 11;
            } else {
                clickYear = mSelYear;
                clickMonth = mSelMonth - 1;
            }
            return new DateTime(clickYear, clickMonth + 1, mDaysText[0][0], 0, 0);
        } else {
            return new DateTime(clickYear, clickMonth + 1, mDaysText[0][0], 0, 0);
        }
    }

    /**
     * 这是一个获取结束时间
     *
     * @return
     */
    public DateTime getEndData() {
        if (mDaysText == null) {
            int week = DateUtil.instance().getDayWeek(mSelYear, mSelMonth, 1);
            return new DateTime(mSelYear, mSelMonth + 1, 1, 0, 0).minusDays(week - 1).plusDays(41);
        }
        /**
         * 如果不是本月的话
         */
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (mDaysText[5][6] <= 14) {
            if (mSelMonth == 11) {
                clickYear = mSelYear + 1;
                clickMonth = 0;
            } else {
                clickYear = mSelYear;
                clickMonth = mSelMonth + 1;
            }
            return new DateTime(clickYear, clickMonth + 1, mDaysText[5][6], 0, 0);
        } else {
            return new DateTime(clickYear, clickMonth + 1, mDaysText[5][6], 0, 0);
        }
    }
}

