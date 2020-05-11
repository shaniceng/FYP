package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();


    private ArrayList<StepsValue> mstepsvalue;

    public static class StepsViewHolder extends RecyclerView.ViewHolder {
        //public ImageView mImageView;
        public TextView tvSteps, tvDate;
        private RecyclerView rvHistoryActivity;

        public StepsViewHolder(@NonNull View itemView) {
            super(itemView);
            //mImageView=itemView.findViewById(R.id.imageViewPark);
            tvSteps=itemView.findViewById(R.id.tvInsetSteps);
            tvDate=itemView.findViewById(R.id.tvDate);
            rvHistoryActivity = itemView.findViewById(R.id.rvHistory_Activity);
        }
    }

    public StepsAdapter(ArrayList<StepsValue> stepsValue){
        mstepsvalue=stepsValue;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout,parent,false);
        StepsViewHolder pvh=new StepsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        StepsValue currentItem = mstepsvalue.get(position);

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.tvSteps.setText(currentItem.getmSteps());
        holder.tvDate.setText(currentItem.getmDate());


        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rvHistoryActivity.getContext(),LinearLayoutManager.VERTICAL,false);
        layoutManager.setInitialPrefetchItemCount(currentItem.getmHistoryActviityName().size());

        HistoryActivityAdapter mhistoryadapter = new HistoryActivityAdapter(currentItem.getmHistoryActviityName());
        holder.rvHistoryActivity.setLayoutManager(layoutManager);
        holder.rvHistoryActivity.setAdapter(mhistoryadapter);
        holder.rvHistoryActivity.setRecycledViewPool(viewPool);


    }



    @Override
    public int getItemCount() {
        return mstepsvalue.size();
    }
}
