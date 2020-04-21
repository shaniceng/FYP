package com.example.fyp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HeartRateActivity extends WearableActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView mTextViewHeart, textViewDate, textViewTime;
    private static final String TAG = "FitActivity";
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        mTextViewHeart = (TextView) findViewById(R.id.tvHR);
        textViewDate=findViewById(R.id.text_view_dateH);


        // Enables Always-on
        setAmbientEnabled();

        sensorManager = ((SensorManager)
                getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        getStepCount();

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
                            textViewTime=findViewById(R.id.text_view_timeH);
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss a");
                            String dateString = sdf.format(date);
                            textViewTime.setText(dateString);
                        }
                    });
                }
            }
        };
        thread.start();

    }
    private void getStepCount() {
        SensorManager mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            mTextViewHeart.setText(msg + "BPM");
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
        //finish();
        sensorManager.registerListener(this, this.sensor, 3);
    }
}
