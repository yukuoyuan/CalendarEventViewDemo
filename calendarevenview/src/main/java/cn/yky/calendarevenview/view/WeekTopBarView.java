package cn.yky.calendarevenview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import cn.yky.calendarevenview.R;


/**
 * Created by yukuoyuan on 2017/6/6.
 * 绘制最顶部的周日到周六的展示
 */
public class WeekTopBarView extends View {

    private int mWeekTextColor;
    private int mWeekTextSize;
    private String[] mWeekTextString;
    private DisplayMetrics mDisplayMetrics;
    private Paint mPaint;

    public WeekTopBarView(Context context) {
        this(context, null);
    }

    public WeekTopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekTopBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setColor(mWeekTextColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mWeekTextSize * mDisplayMetrics.scaledDensity);
    }

    /**
     * 初始化控件的字体属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WeekTopBarView);
        mWeekTextColor = array.getColor(R.styleable.WeekTopBarView_week_text_color, Color.parseColor("#666666"));
        mWeekTextSize = array.getInteger(R.styleable.WeekTopBarView_week_text_size, 13);
        mWeekTextString = context.getResources().getStringArray(R.array.calendar_week_top);
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
            heightSize = mDisplayMetrics.densityDpi * 30;
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
        int width = getWidth();
        int height = getHeight();
        int columnWidth = width / 7;
        for (int i = 0; i < mWeekTextString.length; i++) {
            String text = mWeekTextString[i];
            int fontWidth = (int) mPaint.measureText(text);
            int startX = columnWidth * i + (columnWidth - fontWidth) / 2;
            int startY = (int) (height / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(text, startX, startY, mPaint);
        }
    }
}
