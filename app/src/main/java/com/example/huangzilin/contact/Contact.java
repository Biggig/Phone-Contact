package com.example.huangzilin.contact;

/**
 * Created by 陈剑锋 on 2019/6/24.
 */

public class Contact {
    private String name;
    private String number;
    public String pinyin;
    public int id;

    Contact(String name,String number, int id) {
        this.name = name;//姓名
        this.number = number;//号码
        this.pinyin = PinYinUtils.getPinYin(name);//姓名拼音
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPinyin() {
        return pinyin;
    }

    public int getId() {
        return id;
    }

}
