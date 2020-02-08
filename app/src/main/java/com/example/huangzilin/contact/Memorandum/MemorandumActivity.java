package com.example.huangzilin.contact.Memorandum;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangzilin.contact.MainActivity;
import com.example.huangzilin.contact.R;
import com.example.huangzilin.contact.Record.RecordActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MemorandumActivity extends AppCompatActivity {
    private ListView memorandum_listView;
    private ClearEditText clearEditText;
    MemorandumDb memorandumDb;
    private ListView listView;
    private RadioGroup rgGroup;//下册导航栏
    private RadioButton persons;//联系人
    private RadioButton call_records;//通话记录
    private RadioButton memory;//备忘录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memorandum);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("备忘录");

        //setCustomActionBar();

        memorandumDb = new MemorandumDb(this);
        listView = findViewById(R.id.memorandum_listView);

        initView();

        /*
        ImageButton addButton = findViewById(R.id.add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemorandumActivity.this, MemorandumAddActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        */
        clearEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 输入法中点击搜索
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //这里调用搜索方法
                    String target = clearEditText.getText().toString();
                    Toast.makeText(MemorandumActivity.this, "search", Toast.LENGTH_SHORT).show();
                    Cursor cursor = memorandumDb.query(null, "thing like ? or work_time like ?", new String[] {"%"+target+"%", "%"+target+"%"}, null);
                    ArrayList<Map<String, Object>> list;
                    list = new ArrayList<Map<String, Object>>();
                    List<String> memorandum_thing_list = new ArrayList<>();
                    List<String> memorandum_time_list = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        memorandum_thing_list.add(cursor.getString(cursor.getColumnIndex("thing")));
                        memorandum_time_list.add(cursor.getString(cursor.getColumnIndex("work_time")));
                        //Log.d("测试：",  ""+memorandumDb.getCount());
                    }
                    cursor.close();
                    for (int i = 0; i < memorandum_thing_list.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("thing", memorandum_thing_list.get(i));
                        map.put("work_time", memorandum_time_list.get(i));
                        list.add(map);
                    }
                    Log.d("测试：",  ""+memorandumDb.getCount());
                    SimpleAdapter adapter;
                    adapter = new SimpleAdapter(MemorandumActivity.this, list,
                            R.layout.list_item, new String[] { "thing","work_time"},
                            new int[] { R.id.memorandum_show_thing, R.id.memorandum_show_time });
                    listView.setAdapter(adapter);
                    return true;
                }
                return false;
            }
        });
        //su
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
/*
        memorandum_listView = findViewById(R.id.memorandum_listView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 100);
        memorandum_listView.setLayoutParams(lp);*/

        show();
        radiobutton_initView();
    }

    private void show() {
        final ArrayList<Map<String, Object>> list;
        list = new ArrayList<Map<String, Object>>();
        Cursor cursor = memorandumDb.query(null, null, null, null);
        List<String> memorandum_thing_list = new ArrayList<>();
        List<String> memorandum_time_list = new ArrayList<>();
        List<String> memorandum_id_list = new ArrayList<>();
        while (cursor.moveToNext()) {
            memorandum_thing_list.add(cursor.getString(cursor.getColumnIndex("thing")));
            memorandum_time_list.add(cursor.getString(cursor.getColumnIndex("work_time")));
            memorandum_id_list.add(cursor.getInt(cursor.getColumnIndex("_id"))+"");
            Log.d("测试geshu：",  ""+memorandum_id_list.get(memorandum_id_list.size()-1));
        }
        cursor.close();
        for (int i = 0; i < memorandum_thing_list.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("thing", memorandum_thing_list.get(i));
            map.put("work_time", memorandum_time_list.get(i));
            map.put("_id", memorandum_id_list.get(i));
            list.add(map);
        }
        Log.d("测试：",  ""+memorandumDb.getCount());
        SimpleAdapter adapter;
        adapter = new SimpleAdapter(MemorandumActivity.this, list,
                R.layout.list_item, new String[] { "thing","work_time", "_id"},
                new int[] { R.id.memorandum_show_thing, R.id.memorandum_show_time, R.id.memorandum_show_id});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MemorandumActivity.this, MemorandumUpdateActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("thing", list.get(position).get("thing").toString());
                bundle.putString("work_time", list.get(position).get("work_time").toString());
                bundle.putString("_id", list.get(position).get("_id").toString());
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
                //Toast.makeText(MainActivity.this,"Short Click: "+list.get(position).get("word").toString()+" "+id,Toast.LENGTH_SHORT).show();
            }
        });
        //长按
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int pos = position;
                PopupMenu popup = new PopupMenu(MemorandumActivity.this,view);
                popup.getMenuInflater().inflate(R.menu.menu_popup,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                TableLayout delete_word = (TableLayout)getLayoutInflater().inflate(R.layout.delete_memorandum, null);
                                TextView delete_text = delete_word.findViewById(R.id.delete_memorandum);
                                delete_text.setText("是否确定要删除当前备忘录?");
                                AlertDialog.Builder dialogBuilder;
                                AlertDialog alertDialog;
                                dialogBuilder = new AlertDialog.Builder(MemorandumActivity.this);
                                alertDialog = dialogBuilder
                                        .setIcon(R.drawable.ic_action_thing_delete)
                                        .setTitle("备忘录删除")
                                        .setView(delete_word)
                                        .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                memorandumDb.delete("_id=?", new String[] {list.get(position).get("_id").toString()});
                                                int id = Integer.parseInt(list.get(position).get("_id").toString());
                                                Intent intent = new Intent(MemorandumActivity.this, AutoReceiver.class);
                                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MemorandumActivity.this, id, intent, 0);
                                                am.cancel(pendingIntent);
                                                show();
                                            }
                                        })
                                        .create();
                                alertDialog.show();
                                //Toast.makeText(MainActivity.this, "删除", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                //Toast.makeText(MemorandumActivity.this,"Long Click: ",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initView() {
        clearEditText = (ClearEditText) findViewById(R.id.clear_input);
    }
/*
    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
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
        getMenuInflater().inflate(R.menu.memorandum_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_memorandum_add:
                Intent intent = new Intent(this,MemorandumAddActivity.class);
                startActivityForResult(intent,1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //Activity中的方法,重写就行
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private static long str2Long(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == 1) {
            Intent intent = new Intent(this, AutoReceiver.class);
            int id = Integer.parseInt(data.getStringExtra("id"));
            intent.setAction("VIDEO_TIMER");
            intent.putExtra("thing",data.getStringExtra("thing")) ;
            // PendingIntent这个类用于处理即将发生的事情 
            PendingIntent sender = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            // AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用相对时间
            // SystemClock.elapsedRealtime()表示手机开始到现在经过的时间
            Long begin_time = str2Long(data.getStringExtra("time"));
            am.set(AlarmManager.RTC_WAKEUP,begin_time,sender);
        }
        else if (resultCode == 2) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, AutoReceiver.class);
            int id = Integer.parseInt(data.getStringExtra("id"));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);
            am.cancel(pendingIntent);
            intent.setAction("VIDEO_TIMER");
            intent.putExtra("thing",data.getStringExtra("thing")) ;
            // PendingIntent这个类用于处理即将发生的事情 
            PendingIntent sender = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用相对时间
            // SystemClock.elapsedRealtime()表示手机开始到现在经过的时间
            Long begin_time = str2Long(data.getStringExtra("time"));
            am.set(AlarmManager.RTC_WAKEUP,begin_time,sender);
        }
        show();
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);

            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //下侧导航栏视图初始化
    private void radiobutton_initView() {
        rgGroup = (RadioGroup) findViewById(R.id.radiogroup);
        //联系人
        persons = (RadioButton) findViewById(R.id.persons);
        //通话记录
        call_records = (RadioButton) findViewById(R.id.call_records);
        //备忘录
        memory = (RadioButton) findViewById(R.id.memory);

        //定义底部标签图片大小
        Drawable drawable_persons = getResources().getDrawable(R.drawable.user);
        //第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        drawable_persons.setBounds(0, 0, 60, 60);
        persons.setCompoundDrawables(null, drawable_persons, null, null);//只放上面

        Drawable drawable_call_records = getResources().getDrawable(R.drawable.phone);
        drawable_call_records.setBounds(0, 0, 60, 60);
        call_records.setCompoundDrawables(null, drawable_call_records, null, null);

        Drawable drawable_memory = getResources().getDrawable(R.drawable.book);
        drawable_memory.setBounds(0, 0, 60, 60);
        memory.setCompoundDrawables(null, drawable_memory, null, null);

        //初始化底部标签
        rgGroup.check(R.id.memory);// 默认勾选联系人首页，初始化时候让联系人首页默认勾
        //设置跳转
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(rgGroup.getCheckedRadioButtonId()){
                    case R.id.persons:
                        memory.setChecked(true);
                        Intent intent = new Intent(MemorandumActivity.this, MainActivity.class);
                        //intent.setAction("android.intent.action.MAIN");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        MemorandumActivity.this.startActivity(intent);
                        break;
                    case R.id.call_records:
                        memory.setChecked(true);
                        Intent intent_ = new Intent(MemorandumActivity.this, RecordActivity.class);
                        //intent.setAction("android.intent.action.MAIN");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        MemorandumActivity.this.startActivity(intent_);
                        break;
                    case R.id.memory:
                        break;
                }
            }
        });
    }
}
