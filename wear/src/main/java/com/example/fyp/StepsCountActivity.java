package com.example.fyp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
   // private int steps;
    private String msg;
    private static final String Initial_Count_Key = "FootStepInitialCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_count);

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

        Calendar currentTime = Calendar.getInstance();
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            // Initialize if it is the first time use
            if(!prefs.contains(Initial_Count_Key)){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Initial_Count_Key, (int) event.values[0]);
                editor.commit();
            }

            if((currentTime.get(Calendar.HOUR_OF_DAY) == 00) && (currentTime.get(Calendar.MINUTE) == 00)){ //&& (currentTime.get(Calendar.SECOND) == 00)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Initial_Count_Key, (int) event.values[0]);
                editor.commit();
            }

            int startingStepCount = prefs.getInt(Initial_Count_Key, -1);
            int stepCount = (int) event.values[0] - startingStepCount;

            msg = "Steps count:\n " + stepCount + " steps";
            mTextViewSteps.setText(msg);
            circularProgressBar.setProgressWithAnimation(stepCount); // =1s
            Log.d(TAG, msg);

            //display starting steps count to phone app database
        } else
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
