package com.example.fyp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HeartRateActivity extends WearableActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView mTextViewHeart;
    private String msg;
    private SharedPreferences sharedPreferences;
    private SensorManager mSensorManager;
    private static final String TAG = "FitActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        mTextViewHeart = findViewById(R.id.tvHR);
        // Enables Always-on
        setAmbientEnabled();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        getHartRate();

    }
    private void getHartRate() {
        mSensorManager= ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //suggesting android to take data in every 5s, if nth to do, android will auto collect data.
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
                msg = "" + (int) event.values[0];
            if(msg != null) {
                mTextViewHeart.setText(msg + "BPM");
                Log.d(TAG, msg);

                if(!sharedPreferences.contains("getMaxcurrentHeartRate")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("getMaxcurrentHeartRate", 0);
                    editor.commit();
                }
                else if(sharedPreferences.getInt("getMaxcurrentHeartRate", -1)<((int) event.values[0])){
                     SharedPreferences.Editor edit = sharedPreferences.edit();
                     edit.putInt("getMaxcurrentHeartRate", (int) event.values[0]);
                     edit.commit();
                 }
            }
        }

        else
            Log.d(TAG, "Unknown sensor type");
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
//
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

}
