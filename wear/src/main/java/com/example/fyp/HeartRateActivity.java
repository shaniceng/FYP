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
    private TextView mTextViewHeart, textViewDate, textViewTime;
    private static final String TAG = "FitActivity";
    Calendar calendar;
    String heartPath = "/heart-rate-path";
    String maxheartpath = "/max-heart-path";
    //private int userMaxHeartRate;
    private String msg;
    private SharedPreferences sharedPreferences;

    private static final String AMBIENT_UPDATE_ACTION = "com.your.package.action.AMBIENT_UPDATE";

    private AlarmManager ambientUpdateAlarmManager;
    private PendingIntent ambientUpdatePendingIntent;
    private BroadcastReceiver ambientUpdateBroadcastReceiver;

    private SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        mTextViewHeart = (TextView) findViewById(R.id.tvHR);
        // Enables Always-on
        setAmbientEnabled();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        getHartRate();

//        ambientUpdateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent ambientUpdateIntent = new Intent(AMBIENT_UPDATE_ACTION);
//        ambientUpdatePendingIntent = PendingIntent.getBroadcast(this, 0, ambientUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        ambientUpdateBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) { refreshDisplayAndSetNextUpdate(); }
//        };
    }
    private void getHartRate() {
        mSensorManager= ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, 5000000);
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

//    protected void onResume() {
//        super.onResume();
//        //sensorManager.registerListener(this, this.sensor, 1000);
//        IntentFilter filter = new IntentFilter(AMBIENT_UPDATE_ACTION);
//        registerReceiver(ambientUpdateBroadcastReceiver, filter);
//        refreshDisplayAndSetNextUpdate();
//
//    }
//
    @Override
    protected void onPause() {
        super.onPause();
        //finish();
//        refreshDisplayAndSetNextUpdate();
//        unregisterReceiver(ambientUpdateBroadcastReceiver);
//        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent);
        mSensorManager.unregisterListener(this);
    }
//
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);

    }

//    class SendThread extends Thread {
//        String path;
//        String message;
//
//        //constructor
//        SendThread(String p, String msg) {
//            path = p;
//            message = msg;
//        }
//
//        //sends the message via the thread.  this will send to all wearables connected, but
//        //since there is (should only?) be one, so no problem.
//        public void run() {
//            //first get all the nodes, ie connected wearable devices.
//            Task<List<Node>> nodeListTask =
//                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
//            try {
//                // Block on a task and get the result synchronously (because this is on a background
//                // thread).
//                List<Node> nodes = Tasks.await(nodeListTask);
//
//                //Now send the message to each device.
//                for (Node node : nodes) {
//                    Task<Integer> sendMessageTask =
//                            Wearable.getMessageClient(HeartRateActivity.this).sendMessage(node.getId(), path, message.getBytes());
//
//                    try {
//                        // Block on a task and get the result synchronously (because this is on a background
//                        // thread).
//                        Integer result = Tasks.await(sendMessageTask);
//                        Log.v(TAG, "SendThread: message send to " + node.getDisplayName());
//
//                    } catch (ExecutionException exception) {
//                        Log.e(TAG, "Task failed: " + exception);
//
//                    } catch (InterruptedException exception) {
//                        Log.e(TAG, "Interrupt occurred: " + exception);
//                    }
//
//                }
//
//            } catch (ExecutionException exception) {
//                Log.e(TAG, "Task failed: " + exception);
//
//            } catch (InterruptedException exception) {
//                Log.e(TAG, "Interrupt occurred: " + exception);
//            }
//        }
//    }
//
//
//    private static final long AMBIENT_INTERVAL_MS = TimeUnit.MINUTES.toMillis(10);
//    private void refreshDisplayAndSetNextUpdate() {
//        if (isAmbient()) {
//            // Implement data retrieval and update the screen for ambient mode
//            sensorManager.registerListener(this, this.sensor, 5000000);
//            if(msg != null) {
//                new HeartRateActivity.SendThread(heartPath, msg + "BPM").start();
//                new HeartRateActivity.SendThread(maxheartpath, sharedPreferences.getInt("getMaxcurrentHeartRate", -1) + "BPM").start();
//            }
//        } else {
//            // Implement data retrieval and update the screen for interactive mode
//            sensorManager.registerListener(this, this.sensor, 5000000);
//            if(msg != null) {
//                new HeartRateActivity.SendThread(heartPath, msg + "BPM").start();
//                new HeartRateActivity.SendThread(maxheartpath, sharedPreferences.getInt("getMaxcurrentHeartRate", -1) + "BPM").start();
//            }
//        }
//        long timeMs = System.currentTimeMillis();
//        // Schedule a new alarm
//        if (isAmbient()) {
//            // Calculate the next trigger time
//            long delayMs = AMBIENT_INTERVAL_MS - (timeMs % AMBIENT_INTERVAL_MS);
//            long triggerTimeMs = timeMs + delayMs;
//            ambientUpdateAlarmManager.setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTimeMs,
//                    ambientUpdatePendingIntent);
//        } else {
//            // Calculate the next trigger time for interactive mode
//        }
//    }
//
//    @Override
//    public void onEnterAmbient(Bundle ambientDetails) {
//        super.onEnterAmbient(ambientDetails);
//        refreshDisplayAndSetNextUpdate();
//    }
//
//    @Override
//    public void onUpdateAmbient() {
//        super.onUpdateAmbient();
//        //refreshDisplayAndSetNextUpdate();
//    }


}
