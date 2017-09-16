package cn.yky.calendarevenview.anims;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import cn.yky.calendarevenview.bean.ScheduleState;
import cn.yky.calendarevenview.view.ScheduleLayout;

/**
 * Created by yukuoyuan on 2017/9/16.
 * 这是一个滑动的动画
 */
public class ScheduleAnimation extends Animation {

    private ScheduleLayout mScheduleLayout;
    private ScheduleState mState;
    private float mDistanceY;

    public ScheduleAnimation(ScheduleLayout scheduleLayout, ScheduleState state, float distanceY) {
        mScheduleLayout = scheduleLayout;
        mState = state;
        mDistanceY = distanceY;
        setDuration(200);
        setInterpolator(new DecelerateInterpolator(1.5f));
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (mState == ScheduleState.OPEN) {
            mScheduleLayout.onCalendarScroll(mDistanceY);
        } else {
            mScheduleLayout.onCalendarScroll(-mDistanceY);
        }
    }

}
