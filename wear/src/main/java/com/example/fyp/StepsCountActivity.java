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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    String stepsPath = "/steps-count-path";

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
            if(!prefs.contains(Initial_Count_Key) || ((int) event.values[0] == 0)){
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

            String step = String.valueOf(stepCount);
            msg = "Steps count:\n " + step + " steps";
            mTextViewSteps.setText(msg);
            circularProgressBar.setProgressWithAnimation(stepCount); // =1s
            Log.d(TAG, msg);
            new StepsCountActivity.SendThread(stepsPath, step).start();

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

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.registerListener(this, this.sensor, 3);
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

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        sensorManager.registerListener(this, this.sensor, 3);
    }
}
