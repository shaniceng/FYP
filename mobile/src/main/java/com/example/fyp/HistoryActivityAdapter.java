package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivityAdapter extends RecyclerView.Adapter<HistoryActivityAdapter.HistoryViewHolder> {

    private List<HistoryActivityName> mHistoryActivity;
    HistoryActivityAdapter(List<HistoryActivityName> mHistoryActivity){this.mHistoryActivity=mHistoryActivity;}

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        //public ImageView mImageView;
        public TextView mTextViewActivity, mTextViewDuration;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            //mImageView=itemView.findViewById(R.id.imageViewPark);
            mTextViewActivity=itemView.findViewById(R.id.tvHistoryActivity);
            mTextViewDuration=itemView.findViewById(R.id.tvHistoryDuration);
        }
    }

    public HistoryActivityAdapter(ArrayList<HistoryActivityName> historyActivityName){
        mHistoryActivity= historyActivityName;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_activity_layout,parent,false);
        HistoryViewHolder pvh=new HistoryViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        HistoryActivityName currentItem = mHistoryActivity.get(position);

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextViewActivity.setText(currentItem.getmName());
        holder.mTextViewDuration.setText(currentItem.getmDuration());


    }

    @Override
    public int getItemCount() {
        return mHistoryActivity.size();
    }
}
