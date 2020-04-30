package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TimerActivity extends WearableActivity {

    private final static String TAG = "Wear MainActivity";
    private TextView trackName;
    private  Chronometer chronometer;
    private ImageButton startTimer, stopTimer, pauseTimer;
    String datapath = "/message_path";
    String chromoPath = "/chromo-path";

    private boolean isResume;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int sec,min,milliSec;
    private String chronometertext;
    private String TrackText;

    private boolean running;
    private long pauseOffset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        Intent intent = getIntent();
        TrackText = intent.getStringExtra(TrackActivity.EXTRA_TEXT);

        startTimer = findViewById(R.id.startBtn);
        pauseTimer=findViewById(R.id.pauseBtn);
        stopTimer=findViewById(R.id.stopBtn);

        // Enables Always-on
        setAmbientEnabled();

        trackName=findViewById(R.id.tvTrackName);
        chronometer = findViewById(R.id.chronometer);
       // startTimer = findViewById(R.id.btnStartTimer);
       // stopTimer = findViewById(R.id.btnStopTimer);

        trackName.setText(TrackText);
        pauseTimer.setVisibility(View.GONE);

        handler = new Handler();

/*        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isResume){
                    tStart=SystemClock.uptimeMillis();
                    handler.postDelayed(runnable,0);
                    chronometer.start();
                    isResume=true;
                    stopTimer.setVisibility(View.GONE);
                    startTimer.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }else{
                    tBuff += tMilliSec;
                    handler.removeCallbacks(runnable);
                    chronometer.stop();
                    isResume=false;
                    stopTimer.setVisibility(View.VISIBLE);
                    startTimer.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }
        });

        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isResume){

                    startTimer.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    tMilliSec=0L;
                    tStart=0L;
                    tBuff=0L;
                    tUpdate=0L;
                    sec=0;
                    min = 0;
                    milliSec=0;
                    new TimerActivity.SendThread(datapath, TrackText).start();
                    chronometertext = chronometer.getText().toString();
                    chronometer.setText("00:00:00");

                    //Requires a new thread to avoid blocking the UI

                    new TimerActivity.SendThread(chromoPath, chronometertext).start();

                    AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
                    builder.setMessage("Activity Saved, you have been doing " + TrackText + " for " + chronometertext )
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(new Intent(TimerActivity.this, TrackActivity.class));
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Good Job!");
                    alertDialog.show();
                }
            }
        });*/
    }

    public void startChronometer(View v){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime()- pauseOffset);
            chronometer.start();
            running = true;
            startTimer.setVisibility(View.GONE);
            stopTimer.setVisibility(View.GONE);
            pauseTimer.setVisibility(View.VISIBLE);
        }
    }

    public void pauseChronometer(View v){
        if(running){
            chronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            running=false;
            startTimer.setVisibility(View.VISIBLE);
            stopTimer.setVisibility(View.VISIBLE);
            pauseTimer.setVisibility(View.GONE);
        }

    }
    public void resetChronometer(View v){

        pauseChronometer(v);
        chronometertext = chronometer.getText().toString();
        new TimerActivity.SendThread(datapath, TrackText).start();
        new TimerActivity.SendThread(chromoPath, chronometertext).start();

        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset=0;

        AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
        builder.setMessage("Activity Saved, you have been doing " + TrackText + " for " + chronometertext )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(TimerActivity.this, TrackActivity.class));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Good Job!");
        alertDialog.show();



    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMilliSec= SystemClock.uptimeMillis()-tStart;
            tUpdate=tBuff+tMilliSec;
            sec = (int) (tUpdate/1000);
            min= sec/60;
            sec=sec%60;
            milliSec = (int) (tUpdate%100);
            chronometer.setText(String.format("%02d", min)+ ":" + String.format("%02d",sec) + ":" +String.format("%02d", milliSec));
            handler.postDelayed(this, 60);
        }
    };

    //This actually sends the message to the wearable device.
    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
            path = p;
            message = msg;
        }

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, so no problem.
        public void run() {
            //first get all the nodes, ie connected wearable devices.
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                List<Node> nodes = Tasks.await(nodeListTask);

                //Now send the message to each device.
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(TimerActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        Integer result = Tasks.await(sendMessageTask);
                        Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

                    } catch (ExecutionException exception) {
                        Log.e(TAG, "Task failed: " + exception);

                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Interrupt occurred: " + exception);
                    }

                }

            } catch (ExecutionException exception) {
                Log.e(TAG, "Task failed: " + exception);

            } catch (InterruptedException exception) {
                Log.e(TAG, "Interrupt occurred: " + exception);
            }
        }
    }


}

