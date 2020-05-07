package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkViewHolder> {

    private ArrayList<ParkName> mparkName;

    public static class ParkViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1, mTextView2;

        public ParkViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.imageViewPark);
            mTextView1=itemView.findViewById(R.id.tvPark);
            mTextView2=itemView.findViewById(R.id.tvPark2);
        }
    }

    public ParkAdapter(ArrayList<ParkName> parkName){
        mparkName=parkName;
    }

    @NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.park_name,parent,false);
        ParkViewHolder pvh=new ParkViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder holder, int position) {

        ParkName currentItem = mparkName.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());


    }

    @Override
    public int getItemCount() {
        return mparkName.size();
    }
}
