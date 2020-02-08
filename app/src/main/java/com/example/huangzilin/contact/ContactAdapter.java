package com.example.huangzilin.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by 陈剑锋 on 2019/6/24.
 */

public class ContactAdapter extends BaseAdapter {

    private ArrayList<Contact> mContacts;

    ContactAdapter(ArrayList<Contact> contacts) {

        if (contacts == null) {
            this.mContacts = new ArrayList<Contact>();
        } else {
            this.mContacts = contacts;
        }
    }

    @Override
    public int getCount() {
        return mContacts == null ? 0 : mContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String name = mContacts.get(position).getName();
        final String number = mContacts.get(position).getNumber();
        final String pinyin = mContacts.get(position).getPinyin().substring(0, 1);
        if (position == 0) {
            holder.tvPinYin.setVisibility(View.VISIBLE);
        } else {
            //首字母是否与前面一个一致
            final String prePinyin = mContacts.get(position - 1).getPinyin().substring(0, 1);
            if (pinyin.equals(prePinyin)) {
                holder.tvPinYin.setVisibility(View.GONE);
            } else {
                holder.tvPinYin.setVisibility(View.VISIBLE);
            }
        }

        holder.tvName.setText(name);
        holder.tvNumber.setText(number);
        holder.tvPinYin.setText(pinyin);
        return convertView;
    }

    private static class ViewHolder {
        private TextView tvName;
        private TextView tvNumber;
        private TextView tvPinYin;

        ViewHolder(View itemView) {
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvNumber = (TextView) itemView.findViewById(R.id.number);
            tvPinYin = (TextView) itemView.findViewById(R.id.pinyin);
        }
    }
    //获取首字母所在位置
    public int getPositionForSection(int section) {

        for (int i = 0; i < getCount(); i++) {
            String sortStr = mContacts.get(i).pinyin;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
}
