package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.wear.widget.CircularProgressLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TimerActivity extends WearableActivity implements SensorEventListener{//, CircularProgressLayout.OnTimerFinishedListener, View.OnClickListener {

    private final static String TAG = "Wear MainActivity";
    private ImageButton pauseBtn;
    private TextView trackName, heartRate, tvHold, tvScroll;
    private  Chronometer chronometer;
    private ImageButton startTimer, stopTimer, pauseTimer;
    String datapath = "/message_path";
    String chromoPath = "/chromo-path";
    String activityHeartRatePath = "/activity_tracking_heart_rate_path";

    private static final String Initial_Count_Key = "FootStepInitialCount";
    private static final String Current_Steps_Now = "CurrentStepsCount";

    private ScrollView myview;
    private boolean isResume;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int sec,min,milliSec;
    private String chronometertext;
    private String TrackText;

    private boolean running;
    private long pauseOffset;

    private SensorManager sensorManager;
    private Sensor sensor;
    private String heartmsg;
    private SharedPreferences sharedPreferences;
    //private int avrHeartRate;
    private ArrayList<Integer> avrHeartRate = new ArrayList();

    private CircularProgressLayout circularProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);
        Intent intent = getIntent();
        TrackText = intent.getStringExtra(TrackActivity.EXTRA_TEXT);
        startTimer = findViewById(R.id.startBtn);
        pauseTimer = findViewById(R.id.pauseBtn);
        stopTimer = findViewById(R.id.stopBtn);
        trackName = findViewById(R.id.tvTrackName);
        chronometer = findViewById(R.id.chronometer);
        myview=findViewById(R.id.myScrollView);
        heartRate=findViewById(R.id.hrTV);
        trackName.setText(TrackText);
        pauseTimer.setVisibility(View.GONE);
        tvHold=findViewById(R.id.tv_hold);
        tvHold.setVisibility(View.GONE);
        tvScroll=findViewById(R.id.tv_scroll);
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        pauseTimer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(running){
            chronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            running=false;
            startTimer.setVisibility(View.VISIBLE);
            stopTimer.setVisibility(View.VISIBLE);
            pauseTimer.setVisibility(View.GONE);
            tvHold.setVisibility(View.GONE);
            tvScroll.setVisibility(View.VISIBLE);
        }
                return false;
            }
        });

        handler = new Handler();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Enables Always-on
        setAmbientEnabled();
    }


    public void startChronometer(View v){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime()- pauseOffset);
            chronometer.start();
            running = true;
            startTimer.setVisibility(View.GONE);
            stopTimer.setVisibility(View.GONE);
            tvScroll.setVisibility(View.GONE);
            pauseTimer.setVisibility(View.VISIBLE);
            tvHold.setVisibility(View.VISIBLE);
            getHartRate();
        }
    }
    public void pauseChronometer(){ //View v
//
        if(running){
            chronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            running=false;
            startTimer.setVisibility(View.VISIBLE);
            stopTimer.setVisibility(View.VISIBLE);
            tvScroll.setVisibility(View.VISIBLE);
            pauseTimer.setVisibility(View.GONE);
            tvHold.setVisibility(View.GONE);
        }


    }


    public void resetChronometer(View v){
        pauseChronometer();
        sensorManager.unregisterListener(this);
        chronometertext = chronometer.getText().toString();
        new TimerActivity.SendActivity(chromoPath, chronometertext).start();
        chronometer.setBase(SystemClock.elapsedRealtime());
        String getHR=heartRate.getText().toString();
        new TimerActivity.SendActivity(activityHeartRatePath, getHR).start();
        pauseOffset=0;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
            }
        }, 500);

        AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
        builder.setMessage("Activity Saved, you have been doing " + TrackText + " for " + chronometertext )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        //startActivity(new Intent(TimerActivity.this, MainActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Good Job!");
        alertDialog.show();
        new TimerActivity.SendActivity(datapath, TrackText).start();

    }

    //This actually sends the message to the wearable device.
    class SendActivity extends Thread {
        String path;
        String message;

        //constructor
        SendActivity(String p, String msg) {
            path = p;
            message = msg;
        }

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, so no problem.
        public void run() {
            //first get all the nodes, ie connected wearable devices.
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                List<Node> nodes = Tasks.await(nodeListTask);

                //Now send the message to each device.
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(TimerActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        Integer result = Tasks.await(sendMessageTask);
                        Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

                    } catch (ExecutionException exception) {
                        Log.e(TAG, "Task failed: " + exception);

                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Interrupt occurred: " + exception);
                    }

                }

            } catch (ExecutionException exception) {
                Log.e(TAG, "Task failed: " + exception);

            } catch (InterruptedException exception) {
                Log.e(TAG, "Interrupt occurred: " + exception);
            }
        }
    }

    private void getHartRate() {
        SensorManager mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener((SensorEventListener) this, mHeartRateSensor, 5000000);

        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //suggesting android to take data in every 5s, if nth to do, android will auto collect data.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            heartmsg = "" + (int) event.values[0];
            if(heartmsg != null) {
                //heartRate.setText(heartmsg + "BPM");
                Log.d(TAG, heartmsg);

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
                avrHeartRate.add((int) event.values[0]);
                heartRate.setText(String.format("%.1f", calculateAverage(avrHeartRate)) + "BPM");
            }

        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            // Initialize if it is the first time use
            if (!sharedPreferences.contains(Initial_Count_Key) || ((int) event.values[0] == 0)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Initial_Count_Key, (int) event.values[0]);
                editor.commit();
            }
            if (!sharedPreferences.contains("dailyCurrentSteps") || ((int) event.values[0] == 0)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("dailyCurrentSteps", (int) event.values[0]);
                editor.commit();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Current_Steps_Now, (int) event.values[0]);
            editor.commit();

            int startingStepCount = sharedPreferences.getInt(Initial_Count_Key, -1);
            int stepCount = (int) event.values[0] - startingStepCount;
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("dailyCurrentSteps", stepCount);
            edit.commit();
        }

        else
            Log.d(TAG, "Unknown sensor type");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    private double calculateAverage(List<Integer> avrHeartRate) {
        Integer sum = 0;
        if (!avrHeartRate.isEmpty()) {
            for (Integer avrHR : avrHeartRate) {
                sum += avrHR;
            }
            return sum.doubleValue() / avrHeartRate.size();
        }
        return sum;
    }

    @Override
// Activity
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
                // Do stuff
                if(!running){
                    chronometer.setBase(SystemClock.elapsedRealtime()- pauseOffset);
                    chronometer.start();
                    running = true;
                    startTimer.setVisibility(View.GONE);
                    stopTimer.setVisibility(View.GONE);
                    tvScroll.setVisibility(View.GONE);
                    pauseTimer.setVisibility(View.VISIBLE);
                    tvHold.setVisibility(View.VISIBLE);
                    getHartRate();
                }else{
                    chronometer.stop();
                    pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
                    running=false;
                    startTimer.setVisibility(View.VISIBLE);
                    stopTimer.setVisibility(View.VISIBLE);
                    tvScroll.setVisibility(View.VISIBLE);
                    pauseTimer.setVisibility(View.GONE);
                    tvHold.setVisibility(View.GONE);
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
                // Do stuff
                pauseChronometer();
                sensorManager.unregisterListener(this);
                chronometertext = chronometer.getText().toString();
                new TimerActivity.SendActivity(chromoPath, chronometertext).start();
                chronometer.setBase(SystemClock.elapsedRealtime());
                String getHR=heartRate.getText().toString();
                new TimerActivity.SendActivity(activityHeartRatePath, getHR).start();
                pauseOffset=0;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // yourMethod();
                    }
                }, 500);

                AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
                builder.setMessage("Activity Saved, you have been doing " + TrackText + " for " + chronometertext )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                                //startActivity(new Intent(TimerActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Good Job!");
                alertDialog.show();
                new TimerActivity.SendActivity(datapath, TrackText).start();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}

