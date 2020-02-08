package com.example.huangzilin.contact.Record;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.huangzilin.contact.Record.CallLogInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.example.huangzilin.contact.Record.RecordActivity.MY_PERMISSIONS_REQUEST_READ_CONTACTS;

public class ContactsMsgUtils {
    public static final String BAIDU_PHONE_URL = "http://mobsec-dianhua.baidu.com/dianhua_api/open/location";
    private String callName;
    private String callNumber;
    private String place;
    private CallLogInfo call;
    private List<CallLogInfo> infos;

    static String getInputStreamText(InputStream is) throws Exception {
        InputStreamReader isr = new InputStreamReader(is, "utf8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb=new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public String getJson(String apiUrl) throws Exception{
        URL url= new URL(apiUrl);
        URLConnection open = url.openConnection();
        InputStream inputStream = open.getInputStream();
        return getInputStreamText(inputStream);
    }
    public List<CallLogInfo> getCallLog(Activity activity) {
        infos = new ArrayList<CallLogInfo>();
        ContentResolver cr = activity.getContentResolver();
        Cursor cs;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                    .READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                    .READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        cs = cr.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
                        CallLog.Calls.DATE, CallLog.Calls.DURATION},
                null, null, CallLog.Calls.DEFAULT_SORT_ORDER);


        if (cs != null && cs.getCount() > 0) {
            cs.moveToFirst();
            while (!cs.isAfterLast()) {
                callName = cs.getString(0);
                callNumber = cs.getString(1);
                place = "未知";
                long date = cs.getLong(3);
                int callType = Integer.parseInt(cs.getString(2));
                if (callName == null || callName.equals("")) {
                    //从通讯录中获取名字
                    String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
                    String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='" +
                            callNumber + "'";
                    Cursor cursor = cr.query(ContactsContract
                                    .CommonDataKinds.Phone.CONTENT_URI,
                            cols, selection, null, null);
                    int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup
                            .DISPLAY_NAME);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        callName = cursor.getString(nameFieldColumnIndex);
                    }
                    cursor.close();
                }
                if (callName == null || callName.equals("")){
                    call = new CallLogInfo(callNumber, date, callType, place);
                }
                else{
                    call = new CallLogInfo(callNumber, date, callType, place);
                    call.editName(callName);
                }
                String url = BAIDU_PHONE_URL + "?tel=" + callNumber;
                try{
                    String result = getJson(url);
                    JSONObject object = new JSONObject(result);
                    JSONObject numberObj = object.getJSONObject("response");
                    JSONObject detailObj = numberObj.getJSONObject(callNumber).getJSONObject("detail");
                    String province = detailObj.getString("province");
                    String operator = detailObj.getString("operator");
                    place = operator + " " + province;
                }  catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("CREATION", place);
                call.editPlace(place);
                infos.add(call);
                cs.moveToNext();
            }
        }
        cs.close();
        return infos;
    }


}
