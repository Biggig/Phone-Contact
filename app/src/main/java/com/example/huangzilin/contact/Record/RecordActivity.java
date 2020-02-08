package com.example.huangzilin.contact.Record;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.huangzilin.contact.MainActivity;
import com.example.huangzilin.contact.Memorandum.MemorandumActivity;
import com.example.huangzilin.contact.R;

import java.util.List;

public class RecordActivity extends AppCompatActivity {
    static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    private Context mContext;
    private ListView lv; //通话记录
    private RecordAdapter adapter;

    private RadioGroup rgGroup;//下册导航栏
    private RadioButton persons;//联系人
    private RadioButton call_records;//通话记录
    private RadioButton memory;//备忘录
    private List<CallLogInfo> infos;


    //定义一个Handler用来更新页面：
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    showCallLog();
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        lv = (ListView)findViewById(R.id.callView);
        mContext = this;
        radiobutton_initView();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("通话记录");
        Thread thread = new Thread(){
            @Override
            public void run(){
                getCallLog();
            }
        };
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_support, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.time:
                Intent intent_3 = new Intent(RecordActivity.this, StatActivity.class);
                RecordActivity.this.startActivity(intent_3);
                break;
        }
        return true;
    }

    public void getCallLog(){
        ContactsMsgUtils utils = new ContactsMsgUtils();
       infos = utils.getCallLog(this);
        handler.sendEmptyMessage(0x001);
    }

    public void showCallLog(){
        adapter = new RecordAdapter(infos, mContext);
        lv.setAdapter(adapter);
    }

    //下侧导航栏视图初始化
    private void radiobutton_initView() {
        rgGroup = (RadioGroup) findViewById(R.id.radiogroup_);
        //联系人
        persons = (RadioButton) findViewById(R.id.persons_);
        //通话记录
        call_records = (RadioButton) findViewById(R.id.call_records_);
        //备忘录
        memory = (RadioButton) findViewById(R.id.memory_);

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
        rgGroup.check(R.id.call_records_);// 默认勾选通话记录首页

        //设置跳转
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(rgGroup.getCheckedRadioButtonId()){
                    case R.id.persons_:
                        call_records.setChecked(true);
                        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                        //intent.setAction("android.intent.action.MAIN");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        RecordActivity.this.startActivity(intent);
                        break;
                    case R.id.call_records_:
                        break;
                    case R.id.memory_:
                        call_records.setChecked(true);
                        Intent intent_ = new Intent(RecordActivity.this, MemorandumActivity.class);
                        //intent.setAction("android.intent.action.MAIN");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        RecordActivity.this.startActivity(intent_);
                        break;
                }
            }
        });
    }
}

