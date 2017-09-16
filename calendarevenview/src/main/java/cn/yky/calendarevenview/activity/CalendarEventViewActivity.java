package cn.yky.calendarevenview.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.List;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.bean.AppointMentAndSchduleListBean;
import cn.yky.calendarevenview.bean.WeekCalendarEventBean;
import cn.yky.calendarevenview.inter.OnCalendarClickListener;
import cn.yky.calendarevenview.utils.DataUtils;
import cn.yky.calendarevenview.utils.DateUtil;
import cn.yky.calendarevenview.utils.StringUtils;
import cn.yky.calendarevenview.view.MonthCalendarView;
import cn.yky.calendarevenview.view.ScheduleLayout;
import cn.yky.calendarevenview.view.WeekCalendarEventView;
import cn.yky.calendarevenview.view.WeekCalendarView;
import cn.yky.calendarevenview.view.WeekEventView;

/**
 * Created by yukuoyuan on 2017/9/16.
 * 这是一个测试控件的界面
 */
public class CalendarEventViewActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvBookingManagementWeekCalendarToday;
    private TextView tvBookingManagementWeekCalendarDate;
    private TextView tvBookingManagementWeekCalendarDateLeft;
    private TextView tvBookingManagementWeekCalendarDateRight;
    private ScheduleLayout sclBookingManagement;
    private RelativeLayout rlBookingManagementMonthCalendar;
    private MonthCalendarView mcvBookingManagementMonthCalendar;
    private WeekCalendarView wcvBookingManagementWeekCalendar;
    private WeekCalendarEventView wcevBookingManagementWeekEventView;
    /**
     * 开始时间
     */
    private DateTime mStartDate;
    /**
     * 结束时间
     */
    private DateTime mEndDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_eventview);
        initView();
        initListener();
        initdata();
    }

    private void initListener() {
        tvBookingManagementWeekCalendarToday.setOnClickListener(this);
        tvBookingManagementWeekCalendarDateLeft.setOnClickListener(this);
        tvBookingManagementWeekCalendarDateRight.setOnClickListener(this);

    }

    private void initdata() {
        sclBookingManagement.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day, int position, DateTime stratData, DateTime endData) {
                mStartDate = stratData;
                mEndDate = endData;
                tvBookingManagementWeekCalendarDate.setText(mStartDate.plusDays(6).getYear() + "-" + StringUtils.formatData(mStartDate.plusDays(6).getMonthOfYear()));

            }
        });
        /**
         * 设置事件view点击到空白区域的时候调用的方法
         */
        wcevBookingManagementWeekEventView.setOnEmptyClickListener(new WeekEventView.EmptyClickListener() {
            @Override
            public void onEmptyClick(DateTime dateTime) {
                if (dateTime == null) {
                    return;
                }
                if (dateTime.isAfterNow()) {
                    /**
                     * 点击了当前之后的时间区域
                     */
                    Toast.makeText(CalendarEventViewActivity.this, "点击了空白区域", Toast.LENGTH_LONG).show();
                }
            }
        });
        /**
         * 设置事件view点击到事件的时候调用的方法
         */
        wcevBookingManagementWeekEventView.setOnEventClickListener(new WeekEventView.EventClickListener() {
            @Override
            public void onEventClick(WeekCalendarEventBean event) {
                /**
                 *点击了事件区域
                 */
                Toast.makeText(CalendarEventViewActivity.this, "点击了事件", Toast.LENGTH_LONG).show();
            }
        });
        /**
         * 设置默认的选中的事件view
         */
        mStartDate = new DateTime();
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
        mEndDate = mStartDate.plusDays(7);
        /**
         * 默认选中当天的数据
         */
        initAllDoctorListData();
        tvBookingManagementWeekCalendarDate.setText(mStartDate.plusDays(6).getYear() + "-" + StringUtils.formatData(mStartDate.plusDays(6).getMonthOfYear()));
    }

    /**
     * 获取数据
     */
    private void initAllDoctorListData() {


        new Handler().postDelayed(new Runnable() {
            public void run() {
                /**
                 * 跳转到当前时间
                 */
                wcevBookingManagementWeekEventView.gotoHour(DateUtil.instance().getNowHour());
            }
        }, 100);
        new Handler().postDelayed(new Runnable() {
            public void run() {

                /**
                 * 制造点假数据
                 */
                final List<AppointMentAndSchduleListBean.ItemsBean> tete = DataUtils.instance().getData();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        wcevBookingManagementWeekEventView.setData(tete);
                    }
                });
            }
        }, 6000);
    }

    private void initView() {
        tvBookingManagementWeekCalendarToday = (TextView) findViewById(R.id.tv_booking_management_week_calendar_today);
        tvBookingManagementWeekCalendarDate = (TextView) findViewById(R.id.tv_booking_management_week_calendar_date);
        tvBookingManagementWeekCalendarDateLeft = (TextView) findViewById(R.id.tv_booking_management_week_calendar_date_left);
        tvBookingManagementWeekCalendarDateRight = (TextView) findViewById(R.id.tv_booking_management_week_calendar_date_right);
        sclBookingManagement = (ScheduleLayout) findViewById(R.id.scl_booking_management);
        rlBookingManagementMonthCalendar = (RelativeLayout) findViewById(R.id.rl_booking_management_month_calendar);
        mcvBookingManagementMonthCalendar = (MonthCalendarView) findViewById(R.id.mcv_booking_management_month_calendar);
        wcvBookingManagementWeekCalendar = (WeekCalendarView) findViewById(R.id.wcv_booking_management_week_calendar);
        wcevBookingManagementWeekEventView = (WeekCalendarEventView) findViewById(R.id.wcev_booking_management_week_event_view);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_booking_management_week_calendar_today) {
            if (mStartDate.plusDays(6).getYear() == DateUtil.instance().getNowYear() && mStartDate.plusDays(6).getMonthOfYear() == DateUtil.instance().getNowMonths()) {
                return;
            }
            /**
             * 跳转到今天的数据
             */
            sclBookingManagement.go2Today();

        } else if (i == R.id.tv_booking_management_week_calendar_date_left) {/**
         * 切换月-
         */
            sclBookingManagement.SubtractMonth();

        } else if (i == R.id.tv_booking_management_week_calendar_date_right) {/**
         * 切换月+
         */
            sclBookingManagement.plusMonth();
        }
    }
}
