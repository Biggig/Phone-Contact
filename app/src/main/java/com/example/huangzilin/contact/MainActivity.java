package com.example.huangzilin.contact;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangzilin.contact.Code.ScanActivity;
import com.example.huangzilin.contact.Code.ShowCodeActivity;
import com.example.huangzilin.contact.Memorandum.MemorandumActivity;
import com.example.huangzilin.contact.Record.RecordActivity;
import com.example.huangzilin.contact.Setting.SettingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private SideBar sideBar;//右侧字母筛选栏
    private TextView dialog;//筛选字母显示
    private RadioGroup rgGroup;//下册导航栏
    private RadioButton persons;//联系人
    private RadioButton call_records;//通话记录
    private RadioButton memory;//备忘录
    private ListView lv_contacts;
    private ArrayList<Contact>contact_list;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder dialogBuilder = null;
    private ContactAdapter mAdapter;
    private SharedPreferences settings;
    private String name = "https://www.baidu.com";
    private String phone = "https://www.baidu.com";
    private String mail = "https://www.baidu.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取通讯录权限
        setRight();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);//返回home键
        actionBar.setTitle("联系人");
        //布局初始化
        sidebar_initView();
        radiobutton_initView();
        //获取联系人列表
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        //testshow_contacts();
        show_contacts();
        //导航栏跳转和联系人点击监听
        jumpListener();
        clickListener();
        //搜索回车键监听
        seacrhListener();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
    }
    //获取通讯录权限
    void  setRight() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 1);
        }
    }
    //搜索回车键监听
    private void seacrhListener(){
        final EditText editText_search  = (EditText)findViewById(R.id.et_search);
        Log.i("测试", "监听");
        editText_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    contact_list = getContacts();
                    String search_str = editText_search.getText().toString().replace(" ", "");
                    for (int i = contact_list.size() - 1; i >= 0; i--) {
                        String name_str = contact_list.get(i).getName().toString();
                        String number_str = contact_list.get(i).getNumber().toString();
                        if (!name_str.contains(search_str)&&!number_str.contains(search_str)) {
                            //Log.i("测试","不符合的："+str);
                            contact_list.remove(contact_list.get(i));
                        }
                    }
                    Collections.sort(contact_list, new Comparator<Contact>() {
                        @Override
                        public int compare(Contact lhs, Contact rhs) {
                            return lhs.getPinyin().compareTo(rhs.getPinyin());
                        }
                    });
                    if(contact_list.size()==0){
                        Toast.makeText(MainActivity.this, "无含 '" + search_str + "' 的联系人！", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    mAdapter = new ContactAdapter(contact_list);
                    //显示listview
                    lv_contacts.setAdapter(mAdapter);
                    if (search_str.equals("")) {
                        return false;
                    } else {
                        Toast.makeText(MainActivity.this, "搜索成功，已显示含 '" + search_str + "' 的联系人！", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
                return false;
            }
        });
    }

    //菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.add_contact://添加联系人
                add_contact();
                break;
            case R.id.menu1://事务1
                Intent intent = new Intent(MainActivity.this, ShowCodeActivity.class);
                name = settings.getString("edittext_key", "https://www.baidu.com");
                phone = settings.getString("edittext_key1", "https://www.baidu.com");
                mail = settings.getString("edittext_key2", "https://www.baidu.com");
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("mail", mail);
                startActivity(intent);
                break;
            case R.id.menu2://事务2
                Intent intent2 = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent2);
                break;
            case R.id.menu3://事务3
                Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    //访问手机的通讯录
    public ArrayList getContacts(){
        contact_list = new ArrayList<>();
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        Cursor cursor = getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        String name = "未命名";
        String number = "10086";
        while (cursor.moveToNext()) {
            int rawContactId = cursor.getInt(0);
            StringBuilder sb = new StringBuilder("contractID=");
            sb.append(rawContactId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + rawContactId + "/data");
            Cursor cursor1 = getContentResolver().query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            while (cursor1.moveToNext()) {
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(mimeType)) { //姓名
                    sb.append(",name=" + data1);
                    name = data1;
                } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { //邮件
                    sb.append(",email=" + data1);
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机号
                    sb.append(",phone=" + data1);
                    number = data1.replace(" ", "");
                }
            }
            //加入到列表中
            contact_list.add(new Contact(name,number,rawContactId));
            cursor1.close();
            Log.i("联系人信息", sb.toString());
        }
        cursor.close();
        return contact_list;
    }
    //联系人列表显示
    public void show_contacts(){
        contact_list = getContacts();
        Collections.sort(contact_list, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getPinyin().compareTo(rhs.getPinyin());
            }
        });
        mAdapter = new ContactAdapter(contact_list);
        Log.i("测试","排序完毕 ");
        //显示listview
        lv_contacts.setAdapter(mAdapter);
    }
    //联系人点击监测
    private void clickListener() {
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "短点击联系人名片，显示名片", Toast.LENGTH_SHORT).show();
                Intent intent_2 = new Intent(MainActivity.this, DetailActivity.class);
                int rawId = contact_list.get(position).getId();
                intent_2.putExtra("rawContactId", rawId);
                MainActivity.this.startActivity(intent_2);
            }
        });
        lv_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id){
                int len = lv_contacts.getHorizontalFadingEdgeLength();
                final PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.clickcontact, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            //删除联系人
                            case R.id.delete_contact:
                                delete_contact(position);
                                break;
                            //修改联系人
                            case R.id.alter_contact:
                                alter_contact(position);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
    }

    // 设置右侧[A-Z]快速导航栏触摸监听
    private void jumpListener(){
        Log.i("测试","开始点击");
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                Log.i("测试","获取位置"+position);
                if (position != -1) {
                    lv_contacts.setSelection(position);
                }
            }
        });
    }

    //添加联系人
    private void add_contact(){
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_window, null, false);
        //建立对话框 ppt 05.6
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        //设置对话框
        alertDialog = dialogBuilder
                .setTitle("添加联系人")
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override//取消按钮
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "已取消添加联系人", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override//确定按钮
                    public void onClick(DialogInterface dialog, int which) {
                        EditText input_name = (EditText)alertDialog.findViewById(R.id.input_name);
                        EditText input_number = (EditText) alertDialog.findViewById(R.id.input_number);
                        EditText input_email = (EditText) alertDialog.findViewById(R.id.input_email);

                        if(input_name.getText().toString().equals("") || input_number.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "添加联系人失败", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //插入raw_contacts表，并获取_id属性
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
                            values.put("data2", input_name.getText().toString());
                            values.put("data1", input_name.getText().toString());
                            getContentResolver().insert(uri2, values);
                            values.clear();
                            //add Phone
                            values.put("raw_contact_id", rawContactId);
                            values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/phone_v2");
                            values.put("data2", "2");
                            values.put("data1", input_number.getText().toString());
                            getContentResolver().insert(uri2, values);
                            values.clear();
                            //add email
                            values.put("raw_contact_id", rawContactId);
                            values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/email_v2");
                            values.put("data2", "2");
                            if(input_email.getText().toString().equals("")){
                                values.put("data1", " ");
                            }
                            else{
                                values.put("data1", input_email.getText().toString());
                            }
                            getContentResolver().insert(uri2, values);

                            show_contacts();
                            Toast.makeText(MainActivity.this, "成功添加联系人："+input_name.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .create();
        alertDialog.show();
        //更新显示
    }

    //删除联系人
    public void delete_contact(int position){
        //建立对话框 ppt 05.6
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        //设置对话框
        final String name_str = contact_list.get(position).getName().toString();
        alertDialog = dialogBuilder
                .setTitle("删除联系人")
                .setMessage("是否确定要删除联系人："+name_str+"?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override//取消按钮
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "已取消删除联系人", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override//确定按钮
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts"); // 表raw_contacts
                        Cursor cursor = getContentResolver().query(uri, new String[]{ContactsContract.Contacts.Data._ID},
                                "display_name=?",new String[]{name_str},null);
                        if(cursor.moveToNext()){
                            int id = cursor.getInt(0);//根据id删除data中的相应数据
                            getContentResolver().delete(uri, "display_name=?", new String[]{name_str});
                            uri = Uri.parse("content://com.android.contacts/data");
                            getContentResolver().delete(uri, "raw_contact_id=?", new String[]{""+id});
                        }
                        show_contacts();//刷新显示
                        Toast.makeText(MainActivity.this, "删除成功，已删除联系人"+name_str, Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog.show();
    }

    //修改联系人
    public void alter_contact(int position){
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_window, null, false);
        //建立对话框 ppt 05.6
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        //设置对话框
        final String name_str = contact_list.get(position).getName().toString();
        alertDialog = dialogBuilder
                .setTitle("修改联系人信息")
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override//取消按钮
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "已取消修改联系人", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override//确定按钮
                    public void onClick(DialogInterface dialog, int which) {
                        int id = -1;
                        EditText input_name = (EditText) alertDialog.findViewById(R.id.input_name);
                        EditText input_number = (EditText) alertDialog.findViewById(R.id.input_number);
                        EditText input_email = (EditText) alertDialog.findViewById(R.id.input_email);
                        if (input_name.getText().toString().equals("") || input_number.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "修改联系人失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Uri uri1 = Uri.parse("content://com.android.contacts/raw_contacts"); // 表raw_contacts

                            Cursor cursor = getContentResolver().query(uri1, new String[]{ContactsContract.Contacts.Data._ID},
                                    "display_name=?", new String[]{name_str}, null);
                            if (cursor.moveToNext()) {
                                id = cursor.getInt(0);//根据id删除data中的相应数据
                            }
                            if (id != -1) {
                                Uri uri2 = Uri.parse("content://com.android.contacts/data"); //
                                // 表data
                                ContentValues values = new ContentValues();
                                values.put("data2", input_name.getText().toString());
                                values.put("data1", input_name.getText().toString());
                                getContentResolver().update(uri2, values, "mimetype=? and raw_contact_id=?",
                                        new String[]{"vnd.android.cursor.item/name", "" + id});
                                values.clear();
                                values.put("data1", input_number.getText().toString());
                                getContentResolver().update(uri2, values, "mimetype=? and raw_contact_id=?",
                                        new String[]{"vnd.android.cursor.item/phone_v2", "" + id});

                                values.clear();
                                if(input_email.getText().toString().equals("")){
                                    values.put("data1", " ");
                                }
                                else{
                                    values.put("data1", input_email.getText().toString());
                                }
                                getContentResolver().update(uri2, values, "mimetype=? and raw_contact_id=?",
                                        new String[]{"vnd.android.cursor.item/email_v2", "" + id});
                                show_contacts();//刷新显示
                                Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    //右侧筛选栏视图初始化
    private void sidebar_initView() {
        sideBar = (SideBar) findViewById(R.id.sidebar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
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
        rgGroup.check(R.id.persons);// 默认勾选联系人首页，初始化时候让联系人首页默认勾
        //设置跳转
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(rgGroup.getCheckedRadioButtonId()){
                    case R.id.persons:
                        break;
                    case R.id.call_records:
                        persons.setChecked(true);
                        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                        //intent.setAction("android.intent.action.MAIN");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        MainActivity.this.startActivity(intent);
                        break;
                    case R.id.memory:
                        persons.setChecked(true);
                        Intent intent_ = new Intent(MainActivity.this, MemorandumActivity.class);
                        //intent.setAction("android.intent.action.MAIN");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        MainActivity.this.startActivity(intent_);
                        break;
                }
            }
        });
    }
}
