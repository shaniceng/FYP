package com.example.fyp;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StepsCountActivity extends WearableActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView mTextViewSteps, textViewDate, textViewTime;
    private static final String TAG = "FitActivity";
    private CircularProgressBar circularProgressBar;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_count);

        textViewDate=findViewById(R.id.text_view_date);
        mTextViewSteps = findViewById(R.id.tvStepsCount);
        // Enables Always-on
        setAmbientEnabled();
        sensorManager = ((SensorManager)
                getSystemService(SENSOR_SERVICE));

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        getStepCount();

        circularProgressBar= findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgressMax(7500);
        circularProgressBar.setProgressBarColor(Color.YELLOW);
        circularProgressBar.setBackgroundProgressBarColor(Color.GRAY);
        circularProgressBar.setProgressBarWidth(7f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(3f); // in DP
        circularProgressBar.setRoundBorder(true);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);


        calendar=Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        textViewDate.setText(currentDate);

        Thread thread = new Thread(){
            @Override
            public void run(){
                while (!isInterrupted()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tdate = findViewById(R.id.text_view_time);
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss a");
                            String dateString = sdf.format(date);
                            tdate.setText(dateString);
                        }
                    });
                }
            }
        };
        thread.start();

    }
    private void getStepCount() {
        SensorManager mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
      if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "Steps count:\n " + (int)event.values[0] + " steps";
            mTextViewSteps.setText(msg);
           circularProgressBar.setProgressWithAnimation((int)event.values[0]); // =1s
            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, this.sensor, 3);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.registerListener(this, this.sensor, 3);
    }
}
