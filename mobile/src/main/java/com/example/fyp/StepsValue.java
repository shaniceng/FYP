package com.example.fyp;

import com.example.fyp.HistoryTab.HistoryActivity;

import java.util.List;

public class StepsValue {

    //private int mImageResource;
    private String mSteps,mDate;
    private List<HistoryActivityName> mHistoryActivityName;

    public StepsValue(String steps, String date, List<HistoryActivityName> mHistoryActivityName){
        //mImageResource = imageResource;
        mSteps=steps;
        mDate=date;
        this.mHistoryActivityName = mHistoryActivityName;
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

    public List<HistoryActivityName> getmHistoryActviityName(){
        return mHistoryActivityName;
    }
    public void setmHistoryActvityName(List<HistoryActivityName> mHistoryActvityName){
        this.mHistoryActivityName = mHistoryActivityName;
    }
}
