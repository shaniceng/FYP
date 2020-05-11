package com.example.fyp;

public class LockInValue {
    String activity, duration, cTime, avrHeartRate;

    public LockInValue() {
    }

    public LockInValue(String activity, String duration, String cTime, String avrHeartRate) {
        this.activity = activity;
        this.duration = duration;
        this.cTime = cTime;
        this.avrHeartRate=avrHeartRate;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    public String getAvrHeartRate() {
        return avrHeartRate;
    }

    public void setAvrHeartRate(String avrHeartRate) {
        this.avrHeartRate = avrHeartRate;
    }
}
