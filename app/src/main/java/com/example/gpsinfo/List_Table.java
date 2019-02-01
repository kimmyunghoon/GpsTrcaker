package com.example.gpsinfo;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class List_Table {
    private int _id;
    private String title;
    private String daytime;
    private Drawable icon;
    private String startAdress;
    private String endAdress;
    private String runningTime;
    private String locationLa;
    private String locationLo;


    public String getTitle() {
        return title;
    }

    public String getDaytime() {
        return  daytime;
    }
    public String getRunningTime() {
        return runningTime ;
    }
    public String getStartAdress() {
        return startAdress ;
    }
    public String getEndAdress() {
        return endAdress;
    }

    public String getLocationla() {
        return locationLa;
    }
    public String getLocationlo() {
        return locationLo;
    }

    public void setDaytime(String s) {
        daytime =s;
        Log.v("입력 체크 ", daytime);
    }
    public void setRunningTime(String s) {
        runningTime =s;
        //Log.v("입력 체크 ", runningTime);
    }
    public void setStartAdress(String s) {
        startAdress =s;
        //Log.v("입력 체크 ", startAdress);
    }
    public void setEndAdress(String s) {
        endAdress =s;
        //Log.v("입력 체크 ", endAdress);
    }

    public void setLocationla(String s) {
        locationLa =s;
        //Log.v("입력 체크 ", locationLa);
    }
    public void setLocationlo(String s) {

        locationLo =s;
        //Log.v("입력 체크 ", locationLo);
    }
    public void setTitle(String s) {
        title =s;
        Log.v("입력 체크 ",  title);
    }

    public void set_id(int anInt) {
        _id=anInt;
    }
    public Drawable getIcon() {
        return icon;
    }

    public int getId() {
        return _id;
    }
}
