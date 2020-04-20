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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HeartRateActivity extends WearableActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView mTextViewHeart;
    private static final String TAG = "FitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        mTextViewHeart = (TextView) findViewById(R.id.tvHR);


        // Enables Always-on
        setAmbientEnabled();

        sensorManager = ((SensorManager)
                getSystemService(SENSOR_SERVICE));
        sensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        getStepCount();

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
            mTextViewHeart.setText(msg);
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
        finish();
        //sensorManager.registerListener(this, this.sensor, 3);
    }
}
