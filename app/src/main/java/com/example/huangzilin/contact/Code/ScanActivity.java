package com.example.huangzilin.contact.Code;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.huangzilin.contact.DetailActivity;
import com.example.huangzilin.contact.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//返回home键
        actionBar.setTitle("扫描");
        IntentIntegrator intentIntegrator = new IntentIntegrator(ScanActivity.this);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "无内容", Toast.LENGTH_LONG).show();
            } else {
                content = result.getContents();
                String[] all = content.split(",");
                add_contact(all[0], all[1], all[2]);
                Intent intent = new Intent(ScanActivity.this, DetailActivity.class);
                intent.putExtra("name", all[0]);
                intent.putExtra("phone", all[1]);
                intent.putExtra("mail", all[2]);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void add_contact(String input_name, String input_number, String input_email){
        Uri uri1 = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        //Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
        long rawContactId = ContentUris.parseId(rawContactUri);

        //插入data表
        Uri uri2 = Uri.parse("content://com.android.contacts/data");
        //add Name
        values.put("raw_contact_id", rawContactId);
        values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/name");
        values.put("data2", input_name);
        values.put("data1", input_name);
        getContentResolver().insert(uri2, values);
        values.clear();
        //add Phone
        values.put("raw_contact_id", rawContactId);
        values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");
        values.put("data1", input_number);
        getContentResolver().insert(uri2, values);
        values.clear();
        //add email
        values.put("raw_contact_id", rawContactId);
        values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/email_v2");
        values.put("data2", "2");
        if(input_email.equals("")){
            values.put("data1", " ");
        }
        else{
            values.put("data1", input_email);
        }
        getContentResolver().insert(uri2, values);
    }
}
