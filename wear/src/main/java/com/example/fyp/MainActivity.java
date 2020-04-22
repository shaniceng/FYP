package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends WearableActivity {

    private TextView mTextView, currentTime;
    private Button trackActivity, heartRate, stepsCount;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView_);
        trackActivity = findViewById(R.id.btnWTrackActivity);
        heartRate = findViewById(R.id.btnWHeartRate);
        stepsCount = findViewById(R.id.btnWStepsCount);
        currentTime=findViewById(R.id.tvCurrentTime);
        //myView= (ScrollView)  getLayoutInflater().inflate(R.layout.myview,null);

        calendar=Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        currentTime.setText(currentDate);

        // Enables Always-on
        setAmbientEnabled();
        trackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TrackActivity.class));
            }
        });

        heartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HeartRateActivity.class));
            }
        });

        stepsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepsCountActivity.class));
            }
        });

        Refresh();

             /* myView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_SCROLL && RotaryEncoder.isFromRotaryEncoder(ev)) {
                    // Don't forget the negation here
                    float delta = -RotaryEncoder.getRotaryAxisValue(ev) * RotaryEncoder.getScaledScrollFactor(
                            getApplicationContext());

                    // Swap these axes if you want to do horizontal scrolling instead
                    v.scrollBy(0, Math.round(delta));

                    return true;
                }

                return false;
            }
        });*/



    }


    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have not reached the minimum steps!\nWalk more!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("ALERT!!!");
        alertDialog.show();
    }

    public void Refresh(){
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = "Current Time:" + format.format(currentTime.getTime());
        mTextView.setText(time);

        //set fixed time : eg 7pm=19:00:00
        int setHour = 15; //testing times only
        int setMin = 12;
        int setSec = 00;

        if ((currentTime.get(Calendar.HOUR_OF_DAY) == setHour) && (currentTime.get(Calendar.MINUTE) == setMin) && (currentTime.get(Calendar.SECOND) == setSec)) {
            showAlertDialog();
        }
        runnable(1000);
    }

    public void runnable(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Refresh();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }
}

