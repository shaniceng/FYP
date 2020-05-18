package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkViewHolder> {

    private List<ParkName> mParkName;
    private OnParkListener mOnParkListener;
    public ParkAdapter(List<ParkName> mParkName, OnParkListener onParkListener){
        this.mOnParkListener=onParkListener;
        this.mParkName=mParkName;}

    public static class ParkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public ImageView mImageView;
        public TextView mTextViewParkName, mTextViewDistance;
        OnParkListener onParkListener;


        public ParkViewHolder(@NonNull View itemView, OnParkListener onParkListener) {
            super(itemView);
            //mImageView=itemView.findViewById(R.id.imageViewPark);
            mTextViewParkName=itemView.findViewById(R.id.tvParkName);
            mTextViewDistance=itemView.findViewById(R.id.tvParkDistance);
            this.onParkListener=onParkListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onParkListener.onParkClick(getAdapterPosition());

        }
    }

    public ParkAdapter(ArrayList<ParkName> parkName){
        mParkName= parkName;
    }

    @NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.park_name_layout,parent,false);
        ParkViewHolder pvh=new ParkViewHolder(v,mOnParkListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder holder, int position) {

        DecimalFormat precision = new DecimalFormat("0.00");

        ParkName currentItem = mParkName.get(position);

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextViewParkName.setText(currentItem.getmName());
        Double d = currentItem.getmDistance();
        holder.mTextViewDistance.setText(precision.format(d) +"km");

    }

    @Override
    public int getItemCount() {
        return mParkName.size();
    }

    public interface OnParkListener{
        void onParkClick(int position);
    }
}
