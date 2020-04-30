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

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> { //for recycler view

    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<ImageView> activityPic;
    private ArrayList<String> currentTime;

    public CustomAdapter(ArrayList<String> dataset, ArrayList<String> timeSet,ArrayList<String> currentTime){
        mDataSet=dataset;
        mTimeSet=timeSet;
        this.currentTime=currentTime;
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

/*        switch (mDataSet.get(position)) {
            case "Brisk Walking":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Jogging":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Running":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Tai Chi":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Yoga":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Zumba":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Swimming":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;
            case "Strength Training":
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
                break;

            default:
                holder.activityPic.setImageResource(R.drawable.ic_icon_awesome_walking);
        }*/
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time, stopTime;
        public ImageView activityPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.tvInsertDuration);
            title=itemView.findViewById(R.id.tvActivityName);
            activityPic=itemView.findViewById(R.id.ActivityImageView);
            stopTime=itemView.findViewById(R.id.tvStartTime);


        }
    }
}
