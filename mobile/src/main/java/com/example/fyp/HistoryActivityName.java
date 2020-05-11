package com.example.fyp;

public class HistoryActivityName {

    //private int mImageResource;
    private String mName;
    private String mDuration;

    public HistoryActivityName(String name, String duration){
        //mImageResource = imageResource;
        mName=name;
        mDuration=duration;
    }

    /*public int getImageResource(){
        return mImageResource;
    } */

    public String getmName(){
        return mName;
    }
    public void setmName(String name) {
        mName = name;
    }

    public String getmDuration(){
        return mDuration;
    }
    public void setmDuration(String duration) {
        mDuration = duration;
    }
}
