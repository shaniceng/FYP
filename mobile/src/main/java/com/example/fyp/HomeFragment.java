package com.example.fyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private TextView stepsCount, HeartRate, maxHeartrate;
    protected Handler handler;
    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private String time;
    private String message, steps, heart, max_HeartRate;
    private CircularProgressBar circularProgressBar;

    FloatingActionButton fab;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mrecyclerView = v.findViewById(R.id.activity_RV);
        stepsCount=v.findViewById(R.id.tvStepsCount);
        HeartRate=v.findViewById(R.id.tvResting_value);
        maxHeartrate=v.findViewById(R.id.tvMAX_value);


      circularProgressBar = v.findViewById(R.id.circularProgressBar);

        ExpandableTextView expTv1 = v.findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);
        circularProgressBar.setRoundBorder(true);
        expTv1.setText(getString(R.string.intensity_workout_details));

        fab = (FloatingActionButton) v.findViewById(R.id.btnAddActivity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ExerciseFragment.class));



            }
        });

        //message handler for the send thread.
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                //logthis(stuff.getString("logthis"));
                return true;
            }
        });

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        mDataSet = new ArrayList<>();
        mTimeSet=new ArrayList<>();




        circularProgressBar.setProgressMax(7500);



        return v;
    }


    //setup a broadcast receiver to receive the messages from the wear device via the listenerService.
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getStringExtra("timing")==null){
                message = intent.getStringExtra("message");
                Log.v(TAG, "Main activity received message: " + message);

            }
            else if(intent.getStringExtra("message")==null) {
               time  = intent.getStringExtra("timing");

                mTimeSet.add(time);
                mDataSet.add(message);
                mlayoutManager=new LinearLayoutManager(getContext());
                mrecyclerView.setHasFixedSize(true);
                mAdapter = new CustomAdapter(mDataSet,mTimeSet);
                mrecyclerView.setLayoutManager(mlayoutManager);
                mrecyclerView.setAdapter(mAdapter);


            }
            if(intent.getStringExtra("heartRate")!=null){
                heart = intent.getStringExtra("heartRate");
                Log.v(TAG, "Main activity received message: " + message);
                HeartRate.setText(heart);

            }
            else if(intent.getStringExtra("countSteps")!=null){
                steps = intent.getStringExtra("countSteps");
                Log.v(TAG, "Main activity received message: " + message);
                stepsCount.setText(steps);
                circularProgressBar.setProgressWithAnimation(Float.parseFloat(steps)); // =1s

            }
            else if(intent.getStringExtra("maxHeartRate") !=null){
                max_HeartRate = intent.getStringExtra("maxHeartRate");
                maxHeartrate.setText(max_HeartRate);
            }



        }
    }



}
