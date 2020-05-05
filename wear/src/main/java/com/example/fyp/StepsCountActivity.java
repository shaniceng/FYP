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
    private CircularProgressBar circularProgressBar;
    private String step;
    private String msg;
    private static final String Initial_Count_Key = "FootStepInitialCount";
    private static final String Current_Steps_Now = "CurrentStepsCount";
    String stepsPath = "/steps-count-path";

    private static final String AMBIENT_UPDATE_ACTION = "com.your.package.action.AMBIENT_STEPS_UPDATE";

    private AlarmManager ambientUpdateAlarmManager;
    private PendingIntent ambientUpdatePendingIntent;
    private BroadcastReceiver ambientUpdateBroadcastReceiver;

    private Calendar currentTime;
    private SharedPreferences prefs;
    private int currentSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_count);

        mTextViewSteps = findViewById(R.id.tvStepsCount);
        // Enables Always-on
        setAmbientEnabled();
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
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

        ambientUpdateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientUpdateIntent = new Intent(AMBIENT_UPDATE_ACTION);
        ambientUpdatePendingIntent = PendingIntent.getBroadcast(this, 0, ambientUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ambientUpdateBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { refreshDisplayAndSetNextUpdate(); }
        };


        currentTime = Calendar.getInstance();
        if((currentTime.get(Calendar.HOUR_OF_DAY) == 00) && (currentTime.get(Calendar.MINUTE) == 00)){ //&& (currentTime.get(Calendar.SECOND) == 00)) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Initial_Count_Key, prefs.getInt(Current_Steps_Now, -1));
            editor.commit();
        }

       // resetSteps();
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
        //resetSteps();
        currentTime = Calendar.getInstance();
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            // Initialize if it is the first time use
            if(!prefs.contains(Initial_Count_Key) || ((int) event.values[0] == 0)){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Initial_Count_Key, (int) event.values[0]);
                editor.commit();
            }
           /* if((currentTime.get(Calendar.HOUR_OF_DAY) == 00) && (currentTime.get(Calendar.MINUTE) == 00)){ //&& (currentTime.get(Calendar.SECOND) == 00)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Initial_Count_Key, (int) event.values[0]);
                editor.commit();
            }*/
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Current_Steps_Now, (int) event.values[0]);
            editor.commit();

            //currentSteps = (int) event.values[0];

            int startingStepCount = prefs.getInt(Initial_Count_Key, -1);
            int stepCount = (int) event.values[0] - startingStepCount;


            step = String.valueOf(stepCount);
            msg = "Steps count:\n " + step + " steps";
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

        IntentFilter filter = new IntentFilter(AMBIENT_UPDATE_ACTION);
        registerReceiver(ambientUpdateBroadcastReceiver, filter);
        refreshDisplayAndSetNextUpdate();
       // startAlarm();
        //resetSteps();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if((currentTime.get(Calendar.HOUR_OF_DAY)==00) && (currentTime.get(Calendar.MINUTE) == 01)){ //&& (currentTime.get(Calendar.SECOND) == 00)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Initial_Count_Key, currentSteps);
            editor.commit();
        }
        unregisterReceiver(ambientUpdateBroadcastReceiver);
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent);
        refreshDisplayAndSetNextUpdate();
        //startAlarm();
        //resetSteps();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.registerListener(this, this.sensor, 3);
        if((currentTime.get(Calendar.HOUR_OF_DAY) == 00) && (currentTime.get(Calendar.MINUTE) == 01)){ //&& (currentTime.get(Calendar.SECOND) == 00)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Initial_Count_Key, currentSteps);
            editor.commit();
        }
        refreshDisplayAndSetNextUpdate();
       // cancelAlarm();
        //resetSteps();
    }




    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
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
                            Wearable.getMessageClient(StepsCountActivity.this).sendMessage(node.getId(), path, message.getBytes());

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

    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(600000);
    private void refreshDisplayAndSetNextUpdate() {
        if (isAmbient()) {
            // Implement data retrieval and update the screen for ambient mode
            sensorManager.registerListener(this, this.sensor, 3);
            if(msg != null) {
                new StepsCountActivity.SendThread(stepsPath, step).start();

            }

        } else {
            // Implement data retrieval and update the screen for interactive mode
            sensorManager.registerListener(this, this.sensor, 3);
            if(msg != null) {
                new StepsCountActivity.SendThread(stepsPath, step).start();
            }
        }

        long timeMs = System.currentTimeMillis();
        // Schedule a new alarm
        if (isAmbient()) {
            // Calculate the next trigger time
            long delayMs = AMBIENT_INTERVAL_MS - (timeMs % AMBIENT_INTERVAL_MS);
            long triggerTimeMs = timeMs + delayMs;
            ambientUpdateAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    ambientUpdatePendingIntent);
        } else {
            // Calculate the next trigger time for interactive mode
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        if((currentTime.get(Calendar.HOUR_OF_DAY) == 00) && (currentTime.get(Calendar.MINUTE) == 01)){ //&& (currentTime.get(Calendar.SECOND) == 00)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Initial_Count_Key, currentSteps);
            editor.commit();
        }
        refreshDisplayAndSetNextUpdate();
        //startAlarm();
        //resetSteps();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        refreshDisplayAndSetNextUpdate();
        //resetSteps();
    }

    @Override
    public void onDestroy() {
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent);
        super.onDestroy();
    }


    /*private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        Calendar firingCal= Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR_OF_DAY, 20); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 24); // Particular minute
        firingCal.set(Calendar.SECOND, 0); // particular second

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if(intendedTime >= currentTime){
            // you can add buffer time too here to ignore some small differences in milliseconds
            // set from today
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        } else{
            // set from next day
            // you might consider using calendar.add() for adding one day to the current day
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        //mTextView.setText("Alarm canceled");
    }*/

   /* public void resetSteps(){
        Intent myIntent = new Intent(StepsCountActivity.this, MyAlarmReceiver.class);
        myIntent.putExtra("id", Initial_Count_Key);
        myIntent.putExtra("count",currentSteps);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepsCountActivity.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar firingCal= Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR_OF_DAY, 18); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 05); // Particular minute
        firingCal.set(Calendar.SECOND, 0); // particular second

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if(intendedTime >= currentTime){
            // you can add buffer time too here to ignore some small differences in milliseconds
            // set from today
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        } else{
            // set from next day
            // you might consider using calendar.add() for adding one day to the current day
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }*/

}
