package com.example.huangzilin.contact.Record;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.huangzilin.contact.R;

import java.util.Date;


public class StatActivity extends AppCompatActivity {

    ContentResolver resolver;
    private String[] columns = {CallLog.Calls.DATE, CallLog.Calls.DURATION};
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private final long day_mm =  24 * 60 * 60 * 1000; // 一天的毫秒数
    private final long year_mm = 365 * day_mm;
    private final long month_mm = 30 * day_mm;
    private final long week_mm = 7 * day_mm;

    private long year_count = 0;
    private long month_count = 0;
    private long week_count = 0;

    private long year_total_time = 0;
    private long month_total_time = 0;
    private long week_total_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("通话统计");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        resolver = this.getContentResolver();
        queryData();
        showData();
    }

    public void queryData() {
        ContentResolver cr = this.getContentResolver();
        Cursor cs;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        // 获取去年时间戳
        long now = new Date().getTime();
        String cond = String.valueOf(now - year_mm);

        cs = cr.query(CallLog.Calls.CONTENT_URI, columns, CallLog.Calls.DATE + ">=?"
                , new String[] {cond}, CallLog.Calls.DEFAULT_SORT_ORDER);
        while (cs.moveToNext()) {
            long date = cs.getLong(cs.getColumnIndex(CallLog.Calls.DATE));
            long total_time = cs.getLong(cs.getColumnIndex(CallLog.Calls.DURATION));
            if (date > now - year_mm) {
                if (date > now - month_mm) {
                    if (date > now - week_mm) {
                        week_count++;
                        week_total_time += total_time;
                    }
                    month_count++;
                    month_total_time += total_time;
                }
                year_count++;
                year_total_time += total_time;
            }
        }
    }

    public void showData() {
        TextView
                week_tv1 = findViewById(R.id.week_counts),
                week_tv2 = findViewById(R.id.week_total_time),
                month_tv1 = findViewById(R.id.month_counts),
                month_tv2 = findViewById(R.id.month_total_time),
                year_tv1 = findViewById(R.id.year_counts),
                year_tv2 = findViewById(R.id.year_total_time);
        week_tv1.setText(String.valueOf(week_count));
        week_tv2.setText(longToTime(week_total_time));
        month_tv1.setText(String.valueOf(month_count));
        month_tv2.setText(longToTime(month_total_time));
        year_tv1.setText(String.valueOf(year_count));
        year_tv2.setText(longToTime(year_total_time));
    }

    public String longToTime(long callDuration) {
        long min = callDuration / 60;
        long sec = callDuration % 60;
        String callDurationStr = "";
        if (sec > 0) {
            if (min > 0) {
                callDurationStr = min + "分" + sec + "秒";
            } else {
                callDurationStr = sec + "秒";
            }
        }
        else {
            callDurationStr = "0秒";
        }
        return callDurationStr;
    }
}
