package com.example.huangzilin.contact.Memorandum;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.huangzilin.contact.R;
import com.example.huangzilin.contact.Memorandum.datepicker.CustomDatePicker;
import com.example.huangzilin.contact.Memorandum.datepicker.DateFormatUtils;

public class MemorandumAddActivity extends AppCompatActivity implements View.OnClickListener {
    private DBOpenHandler dbOpenHandler;
    private SQLiteDatabase db;
    private TextView  mTvSelectedTime;
    private CustomDatePicker mTimerPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memorandum_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新增备忘录");


        //setCustomActionBar();

        //ImageButton back_btn = findViewById(R.id.back_btn);
        dbOpenHandler = new DBOpenHandler(this, "memorandumDb.db3", null, 1);
        db = dbOpenHandler.getWritableDatabase();
        /*back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        /*
        TextView save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String begin_time = mTvSelectedTime.getText().toString();
                TextView memorandum_thing_tv_ = findViewById(R.id.memorandum_thing);
                String memorandum_thing = memorandum_thing_tv_.getText().toString();
                Toast.makeText(MemorandumAddActivity.this, begin_time, Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put("thing", memorandum_thing);
                cv.put("work_time", begin_time);
                db.insert("things",null, cv);
                finish();
            }
        });*/

        findViewById(R.id.ll_time).setOnClickListener(MemorandumAddActivity.this);
        mTvSelectedTime = findViewById(R.id.tv_selected_time);
        initTimerPicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time:
                // 日期格式为yyyy-MM-dd HH:mm
                mTimerPicker.show(mTvSelectedTime.getText().toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimerPicker != null)
            mTimerPicker.onDestroy();
    }

    private void initTimerPicker() {
        String beginTime = "2000-01-01 00:00";
        String now = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = "3000-12-31 23:59";

        mTvSelectedTime.setText(now);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
            }
        }, beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }
/*
    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.add_actionbar_layout, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent();
                setResult(3, intent1);
                finish();
                break;
            case R.id.add_save:
                String begin_time = mTvSelectedTime.getText().toString();
                TextView memorandum_thing_tv_ = findViewById(R.id.memorandum_thing);
                String memorandum_thing = memorandum_thing_tv_.getText().toString();
                //Toast.makeText(MemorandumAddActivity.this, begin_time, Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put("thing", memorandum_thing);
                cv.put("work_time", begin_time);
                db.insert("things",null, cv);
                Cursor cursor=db.query("things",null,"thing=? and work_time=?",new String[] {memorandum_thing, begin_time},null,null,null);
                cursor.moveToNext();
                Intent intent2 = new Intent();
                intent2.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
                intent2.putExtra("thing",memorandum_thing);
                intent2.putExtra("time", begin_time);
                setResult(1, intent2);
                cursor.close();

                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
