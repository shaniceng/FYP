package com.example.fyp;

public class ParkName {

    //private int mImageResource;
    private String mName;
    private Double mDistance;

    public ParkName(String name, Double distance){
        //mImageResource = imageResource;
        mName=name;
        mDistance=distance;
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

    public Double getmDistance(){
        return mDistance;
    }
    public void setmDistance(Double distance) {
        mDistance = distance;
    }

}
