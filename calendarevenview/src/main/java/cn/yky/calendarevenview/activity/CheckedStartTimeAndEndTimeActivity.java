package cn.yky.calendarevenview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.adapter.CheckedStartTimeAndEndTimeAdapter;

import static android.R.attr.start;


/**
 * Created by yukuoyuan on 2017/6/29.
 * 这是一个选择开始和结束时间的界面
 */
public class CheckedStartTimeAndEndTimeActivity extends AppCompatActivity implements View.OnClickListener {

    private DateTime StartTime;
    private DateTime EndTime;

    private DateTime st;
    private DateTime end;
    private RecyclerView rcvCheckedStartTimeEndTime;
    private TextView leftBtn;
    private TextView txTitle;
    private TextView rightBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_start_time_end_time);
        if (getIntent().getExtras() != null) {
            st = (DateTime) getIntent().getSerializableExtra("start");
            end = (DateTime) getIntent().getSerializableExtra("end");
        }
        initView();
        initData();
        initListener();
    }


    public void initData() {
        StartTime = new DateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 0, 0);
        rcvCheckedStartTimeEndTime.setLayoutManager(new LinearLayoutManager(this));
        DateTime startdateTime = new DateTime();
        startdateTime = startdateTime.minusYears(2);
        DateTime enddateTime = new DateTime();
        List<DateTime> dateTimeArrayList = new ArrayList<>();
        for (int i = 1; i <= 48; i++) {
            enddateTime = startdateTime.plusMonths(i);
            dateTimeArrayList.add(enddateTime);
        }
        final CheckedStartTimeAndEndTimeAdapter checkedStartTimeAndEndTimeAdapter = new CheckedStartTimeAndEndTimeAdapter(this, dateTimeArrayList);
        rcvCheckedStartTimeEndTime.setAdapter(checkedStartTimeAndEndTimeAdapter);

        checkedStartTimeAndEndTimeAdapter.setOnItemClickListener(new CheckedStartTimeAndEndTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DateTime dateTime) {
                /**
                 * 如果第一次选择时间的话
                 */
                if (StartTime == null) {
                    /**
                     * 设置开始时间为第一次选择的时间
                     */
                    StartTime = dateTime;
                } else {
                    if (EndTime == null) {
                        /**
                         * 如果已经有开始时间的话,进行判断
                         */
                        if (StartTime.isAfter(dateTime.toDate().getTime())) {
                            /**
                             * 如果当前选择的开始时间是在现在选择时间的后边的话
                             */
                            StartTime = dateTime;
                        } else if (StartTime.isBefore(dateTime.toDate().getTime())) {
                            /**
                             * 如果当前选择的开始时间是在选择时间的前边的话,那么现在选择的是结束时间
                             */
                            EndTime = dateTime;
                        }
                    } else {
                        EndTime = null;
                        StartTime = dateTime;
                    }
                }
                if (StartTime != null) {
                    Log.d("选择的开始日期", StartTime.getYear() + "-" + StartTime.getMonthOfYear() + "-" + StartTime.getDayOfMonth());
                }
                if (EndTime != null) {
                    Log.d("选择的结束日期", EndTime.getYear() + "-" + EndTime.getMonthOfYear() + "-" + EndTime.getDayOfMonth());
                }
                /**
                 * 设置选中的开始时间和结束时间
                 */
                checkedStartTimeAndEndTimeAdapter.setStartTimeAndEndTime(StartTime, EndTime);

            }
        });
        /**
         * 根据当前时间滚动到选中的时间
         */


        if (st != null) {
            for (int i = 0; i < dateTimeArrayList.size(); i++) {
                if (dateTimeArrayList.get(i).getYear() == st.getYear() &&
                        dateTimeArrayList.get(i).getMonthOfYear() == st.getMonthOfYear()) {
                    rcvCheckedStartTimeEndTime.scrollToPosition(i);
                    /**
                     * 设置选中的开始时间和结束时间
                     */
                    checkedStartTimeAndEndTimeAdapter.setStartTimeAndEndTime(st, end);
                }
            }
            StartTime = st;
            EndTime = end;

        } else {
            for (int i = 0; i < dateTimeArrayList.size(); i++) {
                if (dateTimeArrayList.get(i).getYear() == Calendar.getInstance().get(Calendar.YEAR) &&
                        dateTimeArrayList.get(i).getMonthOfYear() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                    rcvCheckedStartTimeEndTime.scrollToPosition(i);
                    /**
                     * 设置选中的开始时间和结束时间
                     */
                    checkedStartTimeAndEndTimeAdapter.setStartTimeAndEndTime(StartTime, EndTime);
                }
            }
        }

    }

    protected void initListener() {
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    private void initView() {
        rcvCheckedStartTimeEndTime = (RecyclerView) findViewById(R.id.rcv_checked_start_time_end_time);
        leftBtn = (TextView) findViewById(R.id.left_btn);
        txTitle = (TextView) findViewById(R.id.tx_title);
        rightBtn = (TextView) findViewById(R.id.right_btn);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.left_btn) {
            setResult(RESULT_CANCELED);
            finish();

        } else if (i == R.id.right_btn) {
            if (StartTime == null) {
                Toast.makeText(this, "请选择开始时间", Toast.LENGTH_LONG).show();
                return;
            }
            if (EndTime == null) {
                Toast.makeText(this, "请选择结束时间", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("StartTime", StartTime);
            bundle.putSerializable("EndTime", EndTime);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}
