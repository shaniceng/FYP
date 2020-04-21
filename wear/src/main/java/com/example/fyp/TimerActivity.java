package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class TimerActivity extends WearableActivity {

    private TextView trackName;
    private  Chronometer chronometer;
    private ImageButton startTimer, stopTimer, pauseTimer;

    private boolean isResume;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int sec,min,milliSec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        Intent intent = getIntent();
        final String TrackText = intent.getStringExtra(TrackActivity.EXTRA_TEXT);

        // Enables Always-on
        setAmbientEnabled();

        trackName=findViewById(R.id.tvTrackName);
        chronometer = findViewById(R.id.chronometer);
        startTimer = findViewById(R.id.btnStartTimer);
        stopTimer = findViewById(R.id.btnStopTimer);

        trackName.setText(TrackText);

        handler = new Handler();

        startTimer.setOnClickListener(new View.OnClickListener() {
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
                    String chronometertext;
                    chronometertext = chronometer.getText().toString();
                    chronometer.setText("00:00:00");
                    AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
                    builder.setMessage("Activity Saved, you have been doing " + TrackText + " for " + chronometertext )
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Good Job!");
                    alertDialog.show();
                }
            }
        });
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

}

