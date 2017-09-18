package cn.yky.calendarevenview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.joda.time.DateTime;

import cn.yky.calendarevenview.utils.DateUtil;

/**
 * Created by yukuoyuan on 2017/6/29.
 * 这是一个可以选择开始和结束时间的自定义View
 */
public class CheckedStartAndEndMonthView extends View {
    /**
     * 有几列
     */
    private static final int NUM_COLUMNS = 7;
    /**
     * 有几行
     */
    private static final int NUM_ROWS = 6;
    /**
     * 事件的监听
     */
    private GestureDetector mGestureDetector;
    /**
     * 每个月的开始日期
     */
    private DateTime mMonthStartTime;
    /**
     * 选中的开始时间
     */
    private DateTime mSelectedStartTime;
    /**
     * 选中的结束时间
     */
    private DateTime mSelectedEndTime;
    /**
     * 每个月开始的你那
     */
    private int mMonthStartYear;
    /**
     * 开始的月
     */
    private int mMonthStartMonth;
    /**
     * 开始的日
     */
    private int mMonthStartDay;
    /**
     * 获取屏幕的一些参数
     */
    private DisplayMetrics mDisplayMetrics;
    /**
     * 列的宽度
     */
    private int mColumnWidth;
    /**
     * 行高
     */
    private int mRowHeight;
    /**
     * 存储某天某行的天
     */
    private int[][] mDaysText;
    private Paint mPaint;
    /**
     * 天字体的大小
     */
    private int mDaySize;

    /**
     * 上下文内容
     */
    private Context context;

    /**
     * 日期点击的监听
     */
    private OnDateClickListener onDateClickListener;

    public CheckedStartAndEndMonthView(Context context) {
        this(context, null);
    }

    public CheckedStartAndEndMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckedStartAndEndMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPaint();
        initGestureDetector();
    }

    /**
     * 设置当月的开始时间
     *
     * @param monthStartTime
     */
    public void setmMonthStartTime(DateTime monthStartTime) {
        this.mMonthStartTime = monthStartTime;
        /**
         * 初始化当前月的数据
         */
        initMonth();
        invalidate();
    }

    /**
     * 初始化当前月的数据
     */
    private void initMonth() {
        mMonthStartYear = mMonthStartTime.getYear();
        mMonthStartMonth = mMonthStartTime.getMonthOfYear() - 1;
        mMonthStartDay = 1;
    }

    /**
     * 初始化事件的监听
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 根据点击的xy进行事件的处理
     *
     * @param x
     * @param y
     */
    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int row = y / mRowHeight;
        int column = x / mColumnWidth;
        column = Math.min(column, 6);
        Log.d("点击日期", mDaysText[row][column] + "");
        if (onDateClickListener != null) {
            if (mDaysText[row][column] == 0) {
                return;
            }
            DateTime dateTime = new DateTime(mMonthStartYear, mMonthStartMonth + 1, mDaysText[row][column], 0, 0);
            onDateClickListener.OnDateClick(dateTime);
        }
    }

    public interface OnDateClickListener {
        void OnDateClick(DateTime dateTime);
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;

    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        /**
         * 获取屏幕的一些参数
         */
        /**
         * 天字体的大小
         */
        mDaySize = 14;
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

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
        super.onDraw(canvas);
        initSize();
        drawThisMonth(canvas);
    }


    /**
     * 绘制当前月
     *
     * @param canvas
     */
    private void drawThisMonth(Canvas canvas) {
        if (mMonthStartTime == null) {
            return;
        }
        String dayString;
        /**
         * 获取当月多少天
         */
        int monthDays = DateUtil.instance().getMonthDays(mMonthStartYear, mMonthStartMonth);
        /**
         * 获取当月1号位于周几
         */
        int weekNumber = DateUtil.instance().getFirstDayWeek(mMonthStartYear, mMonthStartMonth);

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
            int startX = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowHeight * row + mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2) - dip2px(context, 5);
            /**
             *绘制每一行的线
             */
            mPaint.setColor(Color.parseColor("#dbdbdb"));
            mPaint.setStyle(Paint.Style.FILL);
            if (row == 0) {
                mPaint.setStrokeWidth(2f);
                canvas.drawLine(0, mRowHeight * row + 1, getWidth(), mRowHeight * row + 1, mPaint);
            } else {
                mPaint.setStrokeWidth(1f);
                canvas.drawLine(0, mRowHeight * row + 1, getWidth(), mRowHeight * row + 1, mPaint);
            }
            Log.d("瞄了几次边", row + "-");
            /**
             *根据选中的开始和结束时间,绘制不同的背景颜色
             */
            DateTime CurrentdateTime = new DateTime(mMonthStartYear, mMonthStartMonth + 1, mDaysText[row][column], 0, 0);
            Log.d("选择的开始日期", mSelectedStartTime.getYear() + "-" + mSelectedStartTime.getMonthOfYear() + "-" + mSelectedStartTime.getDayOfMonth());

            /**
             * 根据开始时间进行绘制
             */
            if (mSelectedStartTime != null) {
                /**
                 * 如果当前的时间跟选择的时间一样的话
                 */
                if (CurrentdateTime.isEqual(mSelectedStartTime.toDate().getTime())) {
                    RectF rect = new RectF(column * mColumnWidth, row * mRowHeight + 1, (1 + column) * mColumnWidth, (1 + row) * mRowHeight - 1);
                    mPaint.setColor(Color.parseColor("#aca6ff"));
                    canvas.drawRoundRect(rect, 20, 20, mPaint);

                    //盖住另一半圆角的地方
                    canvas.drawRect(column * mColumnWidth + 50, row * mRowHeight + 1, (1 + column) * mColumnWidth, (1 + row) * mRowHeight - 1, mPaint);
                    mPaint.setColor(Color.parseColor("#ffffff"));
                    mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                    canvas.drawText(dayString, startX, startY, mPaint);



                    mPaint.setColor(Color.parseColor("#ffffff"));
                    mPaint.setTextSize(10 * mDisplayMetrics.scaledDensity);
                    int startx = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText("开始")) / 2);
                    int starty = (int) (mRowHeight * row + mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2) + dip2px(context, 12);


                    canvas.drawText("开始", startx, starty, mPaint);

                } else if (CurrentdateTime.isAfter(mSelectedStartTime.toDate().getTime())) {
                    /**
                     * 如果当前时间在选择的开始时间之后的话
                     */
                    /**
                     * 判断当前的时间和选择的结束时间进行比较判断
                     */
                    if (mSelectedEndTime != null) {
                        /**
                         * 如果当前的时间跟
                         */
                        if (CurrentdateTime.isEqual(mSelectedEndTime.toDate().getTime())) {

                            /**
                             * 如果当钱时间跟结束时间一样的话
                             */
                            RectF rect = new RectF(column * mColumnWidth, row * mRowHeight + 1, (1 + column) * mColumnWidth, (1 + row) * mRowHeight - 1);
                            mPaint.setColor(Color.parseColor("#aca6ff"));
                            canvas.drawRoundRect(rect, 20, 20, mPaint);


                            //盖住另一半圆角的地方
                            canvas.drawRect(column * mColumnWidth, row * mRowHeight + 1, (1 + column) * mColumnWidth - 50, (1 + row) * mRowHeight - 1, mPaint);
                            mPaint.setColor(Color.parseColor("#ffffff"));
                            mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                            canvas.drawText(dayString, startX, startY, mPaint);


                            mPaint.setColor(Color.parseColor("#ffffff"));
                            mPaint.setTextSize(10 * mDisplayMetrics.scaledDensity);
                            int startx = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText("结束")) / 2);
                            int starty = (int) (mRowHeight * row + mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2) + dip2px(context, 12);


                            canvas.drawText("结束", startx, starty, mPaint);
                        } else if (CurrentdateTime.isBefore(mSelectedEndTime.toDate().getTime())) {
                            /**
                             * 如果当前的时间在选中的结束时间只前的话
                             */
                            /**
                             * 如果当钱时间跟结束时间一样的话
                             */
                            RectF rect = new RectF(column * mColumnWidth, row * mRowHeight + 1, (1 + column) * mColumnWidth, (1 + row) * mRowHeight - 1);
                            mPaint.setColor(Color.parseColor("#f4f7fe"));
                            canvas.drawRect(rect, mPaint);

                            mPaint.setColor(Color.parseColor("#605bf5"));
                            mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                            canvas.drawText(dayString, startX, startY, mPaint);

                        } else {
                            /**
                             * 没有选择的开始和结束时间的话,就按正常的描绘
                             */
                            int dayweek = DateUtil.instance().getDayWeek(mMonthStartYear, mMonthStartMonth, mDaysText[row][column]);
                            if (dayweek == 1 || dayweek == 7) {
                                /**
                                 * 设置字体的颜色
                                 */
                                mPaint.setColor(Color.parseColor("#999999"));
                            } else {
                                /**
                                 * 设置字体的颜色
                                 */
                                mPaint.setColor(Color.parseColor("#333333"));
                            }
                            mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                            canvas.drawText(dayString, startX, startY, mPaint);
                        }
                    } else {
                        /**
                         * 没有选择的开始和结束时间的话,就按正常的描绘
                         */
                        int dayweek = DateUtil.instance().getDayWeek(mMonthStartYear, mMonthStartMonth, mDaysText[row][column]);
                        if (dayweek == 1 || dayweek == 7) {
                            /**
                             * 设置字体的颜色
                             */
                            mPaint.setColor(Color.parseColor("#999999"));
                        } else {
                            /**
                             * 设置字体的颜色
                             */
                            mPaint.setColor(Color.parseColor("#333333"));
                        }
                        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                        canvas.drawText(dayString, startX, startY, mPaint);
                    }
                } else {
                    /**
                     * 没有选择的开始和结束时间的话,就按正常的描绘
                     */
                    int dayweek = DateUtil.instance().getDayWeek(mMonthStartYear, mMonthStartMonth, mDaysText[row][column]);
                    if (dayweek == 1 || dayweek == 7) {
                        /**
                         * 设置字体的颜色
                         */
                        mPaint.setColor(Color.parseColor("#999999"));
                    } else {
                        /**
                         * 设置字体的颜色
                         */
                        mPaint.setColor(Color.parseColor("#333333"));
                    }
                    mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                    canvas.drawText(dayString, startX, startY, mPaint);
                }
            } else {
                /**
                 * 没有选择的开始和结束时间的话,就按正常的描绘
                 */
                int dayweek = DateUtil.instance().getDayWeek(mMonthStartYear, mMonthStartMonth, mDaysText[row][column]);
                if (dayweek == 1 || dayweek == 7) {
                    /**
                     * 设置字体的颜色
                     */
                    mPaint.setColor(Color.parseColor("#999999"));
                } else {
                    /**
                     * 设置字体的颜色
                     */
                    mPaint.setColor(Color.parseColor("#333333"));
                }
                mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

                canvas.drawText(dayString, startX, startY, mPaint);
            }
        }

    }

    /**
     * 初始化大小
     */
    private void initSize() {
        /**
         * 初始化每一个的宽度
         */
        mColumnWidth = getWidth() / NUM_COLUMNS;
        /**
         * 初始化行高
         */
        mRowHeight = getHeight() / NUM_ROWS;
        mDaysText = new int[6][7];
    }

    /**
     * 设置选中的开始时间和结束时间
     *
     * @param startdateTime
     * @param enddateTime
     */
    public void setSelectedStartTimeAndEndTime(DateTime startdateTime, DateTime enddateTime) {
        this.mSelectedStartTime = startdateTime;
        this.mSelectedEndTime = enddateTime;
        invalidate();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = 0;
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }


}
