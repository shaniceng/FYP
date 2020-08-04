package com.example.fyp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
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
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
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
    private CircularProgressBar circularProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        mTextViewHeart = findViewById(R.id.tvHR);
        // Enables Always-on
        setAmbientEnabled();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        getHartRate();

        circularProgressBar= findViewById(R.id.circularProgressBarHeart);
        circularProgressBar.setProgressMax(200);
        circularProgressBar.setProgressBarWidth(7f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(3f); // in DP
        circularProgressBar.setRoundBorder(true);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale .setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
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
                circularProgressBar.setProgressWithAnimation(Float.parseFloat(msg));
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
