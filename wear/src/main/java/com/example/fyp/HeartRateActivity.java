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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HeartRateActivity extends WearableActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView mTextViewHeart, textViewDate, textViewTime;
    private static final String TAG = "FitActivity";
    Calendar calendar;
    String heartPath = "/heart-rate-path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        mTextViewHeart = (TextView) findViewById(R.id.tvHR);


        // Enables Always-on
        setAmbientEnabled();

        sensorManager = ((SensorManager)
                getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        getStepCount();
    }
    private void getStepCount() {
        SensorManager mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        mSensorManager.registerListener(this, mHeartRateSensor, 5000000);
        //suggesting android to take data in every 5s, if nth to do, android will auto collect data.
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            mTextViewHeart.setText(msg + "BPM");
            Log.d(TAG, msg);
            new HeartRateActivity.SendThread(heartPath, msg + "BPM").start();

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

        sensorManager.registerListener(this, this.sensor, 1000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        //sensorManager.registerListener(this, this.sensor, 5000000);
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
                            Wearable.getMessageClient(HeartRateActivity.this).sendMessage(node.getId(), path, message.getBytes());

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
}
