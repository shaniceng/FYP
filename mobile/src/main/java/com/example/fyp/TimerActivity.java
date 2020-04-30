package com.example.fyp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class TimerActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private boolean running,pressed;
    private long pauseOffset;
    ImageButton playbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        playbtn= (ImageButton)findViewById(R.id.playbtn);

        playbtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        chronometer=findViewById(R.id.chronometer);

    }

    public void StartPauseChronometer(View v){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime()- pauseOffset);
            chronometer.start();
            running = true;
            playbtn.setImageResource(R.drawable.ic_pause_black_24dp);
        }
        else if(running){
            chronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            running=false;
            playbtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
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
        playbtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);

    }
}
