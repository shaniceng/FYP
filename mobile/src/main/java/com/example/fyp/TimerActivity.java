package com.example.fyp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

public class TimerActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private boolean running,pressed;
    private long pauseOffset;
    private FirebaseAuth firebaseAuth;
    String usern,saveCurrentDate,saveCurrentTime,duration;
    ImageButton playbtn;
    DatabaseReference reff;
    ActivityInsert activityInsert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        playbtn= (ImageButton)findViewById(R.id.playbtn);

        usern = firebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("KK:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        playbtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        chronometer=findViewById(R.id.chronometer);


        activityInsert = new ActivityInsert();
        reff= FirebaseDatabase.getInstance().getReference().child("Activity Tracker").child(usern).child(saveCurrentDate);

    }

    public void StartPauseChronometer(View v){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime()- pauseOffset);

            chronometer.start();
            Toast.makeText(TimerActivity.this,String.valueOf(chronometer.getText()), Toast.LENGTH_SHORT).show();
            running = true;
            playbtn.setImageResource(R.drawable.ic_pause_black_24dp);
        }
        else if(running){
            chronometer.stop();
            Toast.makeText(TimerActivity.this,String.valueOf(chronometer.getText()), Toast.LENGTH_SHORT).show();

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
        duration = getSecondsFromDurationString(String.valueOf(chronometer.getText()));
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset=0;
        pauseChronometer(v);
        playbtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        



        activityInsert.setActivity("Jogging");
        activityInsert.setcDuration(duration);
        activityInsert.setcTime(saveCurrentTime);
        reff.push().setValue(activityInsert);
        Toast.makeText(TimerActivity.this,"Session has been recorded sucessfully",Toast.LENGTH_LONG).show();



        if(pauseOffset>5){


        }
        else{
            //Toast.makeText(TimerActivity.this,"Session too short to log in", Toast.LENGTH_SHORT).show();
        }

    }

    public String getSecondsFromDurationString(String value){

        String [] parts = value.split(":");

        // Wrong format, no value for you.
        if(parts.length < 2 || parts.length > 3)
            return null;

        int seconds = 0, minutes = 0, hours = 0;

        if(parts.length == 2){
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        }
        else if(parts.length == 3){
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[0]);
        }

        //return seconds + (minutes*60) + (hours*3600);
        /*if(hours!=0){
            return (String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds));}
        else {

            return (String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds));
        }*/

        return (String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds));
    }
}
