package com.example.fyp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class StepsCountActivity extends WearableActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView mTextViewSteps;
    private static final String TAG = "FitActivity";
    private String step;
    private String msg;
    private static final String Current_Steps_Now = "CurrentStepsCount";
    private static final String Initial_Count_Key = "FootStepInitialCount";
    String stepsPath = "/steps-count-path";

    private Calendar currentTime;
    private SharedPreferences prefs;

    private CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_count);

        mTextViewSteps = findViewById(R.id.tvStepsCount);
        // Enables Always-on
        setAmbientEnabled();
        //Initializing step count sensor
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        getStepCount();


        circularProgressBar= findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgressMax(7500);
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
        currentTime = Calendar.getInstance();
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            // Initialize if it is the first time use
            if(!prefs.contains(Initial_Count_Key) || ((int) event.values[0] == 0)){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Initial_Count_Key, (int) event.values[0]);
                editor.commit();
            }
            if(!prefs.contains("dailyCurrentSteps") || ((int) event.values[0] == 0)){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("dailyCurrentSteps", (int) event.values[0]);
                editor.commit();
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Current_Steps_Now, (int) event.values[0]);
            editor.commit();

            int startingStepCount = prefs.getInt(Initial_Count_Key, -1);
            int stepCount = (int) event.values[0] - startingStepCount;

            //to send to mainActivity so can send to app
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("dailyCurrentSteps", stepCount);
            edit.commit();


            step = String.valueOf(prefs.getInt("dailyCurrentSteps", -1));
            msg = step + "\nsteps";
            mTextViewSteps.setText(msg);
            circularProgressBar.setProgressWithAnimation(prefs.getInt("dailyCurrentSteps", -1));
            Log.d(TAG, msg);

            //display starting steps count to phone app database
        } else
            Log.d(TAG, "Unknown sensor type");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    protected void onResume() { super.onResume(); }

    @Override
    protected void onPause() { super.onPause(); }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.registerListener(this, this.sensor, 3);
    }


}
