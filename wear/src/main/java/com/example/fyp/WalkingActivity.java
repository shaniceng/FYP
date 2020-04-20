package com.example.fyp;

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

public class WalkingActivity extends WearableActivity {

    private  Chronometer chronometer;
    private TextView mTextView;
    private TextView timer;
    private ImageButton startTimer, stopTimer;

    private boolean isResume;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int sec,min,milliSec;

    // on the stopwatch.
    private int seconds = 0;
    // Is the stopwatch running?
    private boolean running;
    private boolean wasRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        // Enables Always-on
        setAmbientEnabled();

        chronometer = findViewById(R.id.chronometer);
        startTimer = findViewById(R.id.btnStartTimer);
        stopTimer = findViewById(R.id.btnStopTimer);
        timer = findViewById(R.id.tvWTimer);


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
                    chronometer.setText("00:00:00");
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
       /*startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CountUpTimer timer = new CountUpTimer(30000) {
                    @Override
                    public void onTick(long elapsedTime) {

                    }
                };

                timer.start();
            }
        });

        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountUpTimer timer = new CountUpTimer(30000) {
                    @Override
                    public void onTick(long elapsedTime) {

                    }
                };

                timer.stop();
            }
        });*/



        /*if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds
                    = savedInstanceState
                    .getInt("seconds");
            running
                    = savedInstanceState
                    .getBoolean("running");
            wasRunning
                    = savedInstanceState
                    .getBoolean("wasRunning");
        }
        onClickStart();
        onClickStop();
        runTimer();*/



/*
    // Save the state of the stopwatch
    // if it's about to be destroyed.
   @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState)
    {
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("running", running);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
    }

    // If the activity is paused,
    // stop the stopwatch.

    @Override
    protected void onPause()
    {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.

    @Override
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }

    // Start the stopwatch running
    // when the Start button is clicked.
    // Below method gets called
    // when the Start button is clicked.

    public void onClickStart()
    {
        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
            }
        });

    }

    // Stop the stopwatch running
    // when the Stop button is clicked.
    // Below method gets called
    // when the Stop button is clicked.
    public void onClickStop()
    {
        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
            }
        });

    }

    // Reset the stopwatch when
    // the Reset button is clicked.
    // Below method gets called
    // when the Reset button is clicked.
    public void onClickReset(View view)
    {
        running = false;
        seconds = 0;
    }

    // Sets the NUmber of seconds on the timer.
    // The runTimer() method uses a Handler
    // to increment the seconds and
    // update the text view.

    private void runTimer()
    {

        // Get the text view.
        final TextView timeView
                = (TextView)findViewById(
                R.id.tvWTimer);

        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                // Set the text view text.
                timer.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }*/
}

