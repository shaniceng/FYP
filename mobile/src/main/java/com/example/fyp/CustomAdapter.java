package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> { //for recycler view

    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<String> currentTime;
    private ArrayList<Integer> image;
    private ArrayList<String> heartRateAvr;
    private int[] menuIcons = {R.drawable.ic_icon_awesome_walking, R.drawable.ic_icon_awesome_jogging,R.drawable.ic_awesome_running
            ,R.drawable.ic_awesome_taichi,R.drawable.ic_awesome_yoga,R.drawable.ic_awesome_zumba,
            R.drawable.ic_awesome_swimming,R.drawable.ic_awesome_strengthtraining,R.drawable.ic_awesome_others};


    public CustomAdapter(ArrayList<String> dataset, ArrayList<String> timeSet, ArrayList<String> currentTime, ArrayList<Integer> image, ArrayList<String> heartRateAvr){
        mDataSet=dataset;
        mTimeSet=timeSet;
        this.currentTime=currentTime;
        this.image=image;
        this.heartRateAvr=heartRateAvr;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listener,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        holder.title.setText(mDataSet.get(position));
        holder.time.setText(mTimeSet.get(position));
        holder.stopTime.setText(currentTime.get(position));
        if(heartRateAvr.get(position)!=null) {
            holder.activityHeartRatetv.setText(heartRateAvr.get(position));
        }

            if (mDataSet != null) {
                switch (String.valueOf(mDataSet.get(position))) {
                    case "Brisk Walking":
                        image.add(menuIcons[0]);
                        break;
                    case "Jogging":
                        image.add(menuIcons[1]);
                        break;
                    case "Running":
                        image.add(menuIcons[2]);
                        break;
                    case "Tai Chi":
                        image.add(menuIcons[3]);
                        break;
                    case "Yoga":
                        image.add(menuIcons[4]);
                        break;
                    case "Zumba":
                        image.add(menuIcons[5]);
                        break;
                    case "Swimming":
                        image.add(menuIcons[6]);
                        break;
                    case "Strength Training":
                        image.add(menuIcons[7]);
                        break;
                    default:
                        image.add(menuIcons[8]);
                }
            } else {
                image.add(menuIcons[8]);
            }

            holder.activityPic.setImageResource(image.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time, stopTime, activityHeartRatetv;
        public ImageView activityPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tvInsertDuration);
            title=itemView.findViewById(R.id.tvActivityName);
            activityPic=itemView.findViewById(R.id.ActivityImageView);
            stopTime=itemView.findViewById(R.id.tvStartTime);

            activityHeartRatetv=itemView.findViewById(R.id.tvActivityHeartRate);
           // heartRate=itemView.findViewById(R.id.tv_activityAvrHeartRate);

        }
    }
}
