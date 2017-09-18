package cn.yky.calendareventview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import cn.yky.calendarevenview.activity.CalendarEventViewActivity;
import cn.yky.calendarevenview.activity.CheckedStartTimeAndEndTimeActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTestCalendarEventView;
    private TextView tvTestCalendarStartEnd;
    private int CHECKEDTIME = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initlisterner();
    }

    private void initlisterner() {
        tvTestCalendarEventView.setOnClickListener(this);
        tvTestCalendarStartEnd.setOnClickListener(this);
    }

    private void initView() {
        tvTestCalendarEventView = (TextView) findViewById(R.id.tv_test_calendar_event_view);
        tvTestCalendarStartEnd = (TextView) findViewById(R.id.tv_test_calendar_start_end);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_test_calendar_event_view:
                startActivity(new Intent(this, CalendarEventViewActivity.class));
                break;
            case R.id.tv_test_calendar_start_end:
                startActivityForResult(new Intent(this, CheckedStartTimeAndEndTimeActivity.class), CHECKEDTIME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHECKEDTIME) {
                DateTime start = (DateTime) data.getSerializableExtra("StartTime");
                DateTime end = (DateTime) data.getSerializableExtra("EndTime");
                Toast.makeText(this, start.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(this, end.toString(), Toast.LENGTH_LONG).show();

            }
        }
    }
}
