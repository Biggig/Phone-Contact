package com.example.huangzilin.contact.Record;

public class CallLogInfo {
    public String number;
    public long date;
    public String name;
    public int type;
    public String place;
    //static String CALLIN = "来电";
    //static String CALLOUT = "去电";
    //static String CALLMISS = "未接";
    public CallLogInfo(String number, long date, int type, String place){
        super();
        this.number = number;
        this.name = number;
        this.date = date;
        this.type = type;
        if(place == null || place.equals("")) {
            this.place = "未知";
        }
        else {
            this.place = place;
        }
    }
    public CallLogInfo(){
        super();
    }
    public void editName(String name){
        this.name = name;
    }

    public void editPlace(String place){
        this.place = place;
    }

}
