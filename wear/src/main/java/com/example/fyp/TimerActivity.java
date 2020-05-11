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
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TimerActivity extends WearableActivity implements SensorEventListener{

    private final static String TAG = "Wear MainActivity";
    private TextView trackName, heartRate;
    private  Chronometer chronometer;
    private ImageButton startTimer, stopTimer, pauseTimer;
    String datapath = "/message_path";
    String chromoPath = "/chromo-path";
    String activityHeartRatePath = "/activity_tracking_heart_rate_path";

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
        heartRate=findViewById(R.id.hrTV);
        trackName.setText(TrackText);
        pauseTimer.setVisibility(View.GONE);
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

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
            pauseTimer.setVisibility(View.VISIBLE);
            getHartRate();
        }
    }
    public void pauseChronometer(View v){
        if(running){
            chronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            running=false;
            startTimer.setVisibility(View.VISIBLE);
            stopTimer.setVisibility(View.VISIBLE);
            pauseTimer.setVisibility(View.GONE);
        }

    }
    public void resetChronometer(View v){
        pauseChronometer(v);
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
        new TimerActivity.SendActivity(datapath, TrackText).start();
        AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
        builder.setMessage("Activity Saved, you have been doing " + TrackText + " for " + chronometertext )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(TimerActivity.this, MainActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Good Job!");
        alertDialog.show();

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

}

