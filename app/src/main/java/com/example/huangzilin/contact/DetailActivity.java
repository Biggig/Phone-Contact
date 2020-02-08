package com.example.huangzilin.contact;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private int rawContactId;

    private TextView contact_name;
    private TextView phone_num;
    private TextView email;
    ContentResolver resolver;
    private Map<String, String> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("联系人");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        resolver = this.getContentResolver();
        Intent intent_2 = getIntent();
        rawContactId = intent_2.getIntExtra("rawContactId", -1);
        contact_name = findViewById(R.id.contact_name);
        phone_num = findViewById(R.id.phone_num);
        email = findViewById(R.id.email);
        data = new HashMap<>();
        if(rawContactId == -1){
            data.put("name", intent_2.getStringExtra("name"));
            data.put("phone", intent_2.getStringExtra("phone"));
            data.put("email", intent_2.getStringExtra("mail"));
        }else{
            try {
                queryContact();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        showData();
    }

    /**
     * 查询rawContactId联系人的姓名、电话、邮箱
     */
    public void queryContact() throws Exception {
        Uri uri = Uri.parse("content://com.android.contacts/contacts/" + rawContactId + "/data");
        Cursor cursor = resolver.query(uri, new String[] {"mimetype", "data1", "data2"}, null, null, null);
        while (cursor.moveToNext()) {
            String data1 = cursor.getString(cursor.getColumnIndex("data1"));
            String mimeType = cursor.getString(cursor.getColumnIndex("mimetype"));
            if ("vnd.android.cursor.item/name".equals(mimeType)) {
                // 姓名
                data.put("name", data1);
            }
            else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) {
                // 姓名
                data.put("email", data1);
            }
            else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
                // 姓名
                data.put("phone", data1);
            }
        }
    }

    public void showData() {
        contact_name.setText(data.get("name"));
        phone_num.setText(data.get("phone"));
        email.setText(data.get("email"));
    }
}
