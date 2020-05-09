package com.example.fyp;

public class WeeklyReportPointValue {
    String moderateMins;
    int stepsCount;

    public WeeklyReportPointValue(){

    }

    public WeeklyReportPointValue(String moderateMins, int stepsCount){
        this.moderateMins=moderateMins;
        this.stepsCount=stepsCount;

    }
    public String getModerateMins() {
        return moderateMins;
    }

    public void setModerateMins(String moderateMins) {
        this.moderateMins = moderateMins;
    }

    public void setStepsCount(int stepsCount) {
        this.stepsCount = stepsCount;
    }

    public int getStepsCount() {
        return stepsCount;
    }
}
