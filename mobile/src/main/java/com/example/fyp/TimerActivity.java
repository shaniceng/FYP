package com.example.fyp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        chronometer=findViewById(R.id.chronometer);

    }

    public void startChronometer(View v){
    if(!running){
        chronometer.setBase(SystemClock.elapsedRealtime()- pauseOffset);
        chronometer.start();
        running = true;
    }
    }

    public void pauseChronometer(View v){
        if(running){
            chronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            running=false;
        }

    }
    public void resetChronometer(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset=0;
        pauseChronometer(v);

    }
}
