package com.example.fyp;

public class StepsValue {

    //private int mImageResource;
    private String mSteps,mDate;

    public StepsValue(String steps, String date){
        //mImageResource = imageResource;
        mSteps=steps;
        mDate=date;
    }

    /*public int getImageResource(){
        return mImageResource;
    } */

    public String getmSteps(){
        return mSteps;
    }
    public void setmSteps(String steps) {
        mSteps = steps;
    }

    public String getmDate(){
        return mDate;
    }

    public void setmDate(String date) {
        mDate = date;
    }
}
