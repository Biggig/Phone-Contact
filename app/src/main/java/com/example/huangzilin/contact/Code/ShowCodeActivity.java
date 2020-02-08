package com.example.huangzilin.contact.Code;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.huangzilin.contact.R;

public class ShowCodeActivity extends AppCompatActivity {
    private String name = "https://www.baidu.com";
    private String phone = "https://www.baidu.com";
    private String mail = "https://www.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_code);
        ImageView mImageView = (ImageView) findViewById(R.id.code);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        mail = intent.getStringExtra("mail");
        String result = name + ',' + phone + ',' + mail;
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(result, 960, 960);
        mImageView.setImageBitmap(mBitmap);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//返回home键
        actionBar.setTitle("二维码");
    }
}
