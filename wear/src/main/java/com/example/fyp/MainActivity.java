package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private Button trackActivity, heartRate, stepsCount;

   // private ScrollView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView =  findViewById(R.id.textView_);
        trackActivity = findViewById(R.id.btnWTrackActivity);
        heartRate = findViewById(R.id.btnWHeartRate);
        stepsCount = findViewById(R.id.btnWStepsCount);
        //myView= (ScrollView)  getLayoutInflater().inflate(R.layout.myview,null);

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


        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = "Current Time:" + format.format(currentTime.getTime());
        mTextView.setText(time);

        int second = currentTime.get(Calendar.SECOND);
        int minute = currentTime.get(Calendar.MINUTE);
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        //24 hour format


        int setHour = 16;
        int setMin = 49;
        int setSec = 00;


        //while ((currentTime.get(Calendar.HOUR_OF_DAY) == setHour)) { //&& (currentTime.get(Calendar.MINUTE) == setMin) && (currentTime.get(Calendar.SECOND) == setSec)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("TEsting alert dialog").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("ALERT!!!");
                alertDialog.show();


        //}

        // Enables Always-on
        setAmbientEnabled();
    }


}
