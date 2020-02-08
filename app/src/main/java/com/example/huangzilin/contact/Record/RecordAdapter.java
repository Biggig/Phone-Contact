package com.example.huangzilin.contact.Record;

import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huangzilin.contact.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecordAdapter extends BaseAdapter{
    private List<CallLogInfo> infos;
    private LayoutInflater inflater;
    private Context mContext;

    public RecordAdapter(List<CallLogInfo> infos, Context mContext){
        super();
        this.infos = infos;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object getItem(int position){
        return infos.get(position);
    }

    @Override
    public int getCount(){
        return infos.size();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View concertView, ViewGroup parent){
        View view = inflater.inflate(R.layout.call_log_item, null);
        TextView tv_number = (TextView)view.findViewById(R.id.name);
        TextView tv_date = (TextView)view.findViewById((R.id.time));
        TextView tv_place = (TextView)view.findViewById(R.id.place);
        ImageView icon = (ImageView)view.findViewById(R.id.icon);
        CallLogInfo info = infos.get(position);
        String name = info.name;
        if(name == null || name.equals("")){
            tv_number.setText(info.number);
        }
        else{
            tv_number.setText(info.name);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = format.format(info.date);
        tv_date.setText(dateStr);
        tv_place.setText(info.place);
        switch(info.type){
            case CallLog.Calls.INCOMING_TYPE:
                icon.setImageResource(R.drawable.in);
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                icon.setImageResource(R.drawable.out);
                break;
            case CallLog.Calls.MISSED_TYPE:
                icon.setImageResource(R.drawable.miss);
                break;
            default:
                icon.setImageResource(R.drawable.in);
                break;
        }
        return view;
    }
}
