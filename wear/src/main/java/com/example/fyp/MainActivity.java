package com.example.fyp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat.WearableExtender;

import static android.provider.CalendarContract.EXTRA_EVENT_ID;

public class MainActivity extends WearableActivity {

    private TextView mTextView, currentTime;
    private Button trackActivity, heartRate, stepsCount;
    private Calendar calendar;
    private ScrollView myView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextView = findViewById(R.id.textView_);
        trackActivity = findViewById(R.id.btnWTrackActivity);
        heartRate = findViewById(R.id.btnWHeartRate);
        stepsCount = findViewById(R.id.btnWStepsCount);
        currentTime=findViewById(R.id.tvCurrentTime);
        myView= (ScrollView) findViewById(R.id.myview);

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
        createNoti();
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

    public void createNoti(){
        int notificationId = 001;
        // The channel ID of the notification.
        String id = "my_channel_01";
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        viewIntent.putExtra(EXTRA_EVENT_ID, 002);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        // Notification channel ID is ignored for Android 7.1.1
        // (API level 25) and lower.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.drawable.ic_message)
                        .setContentTitle("Alert!!!")
                        .setContentText("You have not reached the specific target!")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Issue the notification with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }


}

