package com.example.huangzilin.contact.Memorandum;

import android.content.ContentValues;
import android.content.Intent;
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

public class MemorandumUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private DBOpenHandler dbOpenHandler;
    private SQLiteDatabase db;
    private TextView  mTvSelectedTime;
    private CustomDatePicker  mTimerPicker;
    private TextView memorandum_thing_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memorandum_update);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("修改备忘录");

        //setCustomActionBar();

        //ImageButton back_btn = findViewById(R.id.back_btn_update);
        dbOpenHandler = new DBOpenHandler(this, "memorandumDb.db3", null, 1);
        db = dbOpenHandler.getWritableDatabase();
        /*
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView save = findViewById(R.id.save_update);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String begin_time = mTvSelectedTime.getText().toString();
                String memorandum_thing = memorandum_thing_tv.getText().toString();
                Toast.makeText(MemorandumUpdateActivity.this, begin_time, Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put("thing", memorandum_thing);
                cv.put("work_time", begin_time);
                db.update("things", cv, "_id=?", new String[] {(Integer.parseInt(MemorandumUpdateActivity.this.getIntent().getExtras().getString("_id"))+1)+""});
                finish();
            }
        });*/

        memorandum_thing_tv = findViewById(R.id.memorandum_thing_update);
        Bundle bundle = this.getIntent().getExtras();
        String thing = bundle.getString("thing");
        memorandum_thing_tv.setText(thing);
        memorandum_thing_tv.setSelectAllOnFocus(true);

        findViewById(R.id.ll_time_update).setOnClickListener(MemorandumUpdateActivity.this);
        mTvSelectedTime = findViewById(R.id.tv_selected_time_update);
        initTimerPicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time_update:
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
        Bundle bundle = this.getIntent().getExtras();
        String work_time = bundle.getString("work_time");
        String endTime = "3000-12-31 23:59";

        mTvSelectedTime.setText(work_time);

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
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.update_actionbar_layout, null);
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
        getMenuInflater().inflate(R.menu.update_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent();
                setResult(4, intent1);
                finish();
                break;
            case R.id.update_save:
                String begin_time = mTvSelectedTime.getText().toString();
                String memorandum_thing = memorandum_thing_tv.getText().toString();
                //Toast.makeText(MemorandumUpdateActivity.this, begin_time, Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put("thing", memorandum_thing);
                cv.put("work_time", begin_time);
                String id = MemorandumUpdateActivity.this.getIntent().getExtras().getString("_id");
                db.update("things", cv, "_id=?", new String[] {id});

                Intent intent2 = new Intent();
                intent2.putExtra("id", id);
                intent2.putExtra("thing",memorandum_thing);
                intent2.putExtra("time", begin_time);
                setResult(2, intent2);

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
