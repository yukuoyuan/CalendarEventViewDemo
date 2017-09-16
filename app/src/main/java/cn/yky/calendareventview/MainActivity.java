package cn.yky.calendareventview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import cn.yky.calendarevenview.activity.CalendarEventViewActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTestCalendarEventView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initlisterner();
    }

    private void initlisterner() {
        tvTestCalendarEventView.setOnClickListener(this);
    }

    private void initView() {
        tvTestCalendarEventView = (TextView) findViewById(R.id.tv_test_calendar_event_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_test_calendar_event_view:
                startActivity(new Intent(this, CalendarEventViewActivity.class));
                break;
        }
    }
}
