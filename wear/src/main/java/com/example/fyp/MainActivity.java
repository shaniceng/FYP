package com.example.fyp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import static android.provider.CalendarContract.EXTRA_EVENT_ID;

public class MainActivity extends WearableActivity {

    private TextView mTextView, currentTime;
    private Button trackActivity, heartRate, stepsCount;
    private Calendar calendar;
    private ScrollView myView;

    private AlarmManager ambientUpdateAlarmManager;
    private PendingIntent ambientUpdatePendingIntent;
    private BroadcastReceiver ambientUpdateBroadcastReceiver;
    private float batteryPct;

    private static final String AMBIENT_UPDATE_ACTION = "com.your.package.action.AMBIENT_STEPS_UPDATE";
    private static final String TAG = "BattActivity";
    String battPath = "/batt-life-path";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView_);
        trackActivity = findViewById(R.id.btnWTrackActivity);
        heartRate = findViewById(R.id.btnWHeartRate);
        stepsCount = findViewById(R.id.btnWStepsCount);
        currentTime=findViewById(R.id.tvCurrentTime);
        myView= (ScrollView) findViewById(R.id.myview);

        calendar=Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        currentTime.setText(currentDate);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = "Current Time:" + format.format(calendar.getTime());
        mTextView.setText(time);


        // Enables Always-on
        setAmbientEnabled();
        trackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TrackActivity.class));
            }
        });

        heartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HeartRateActivity.class));
            }
        });

        stepsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepsCountActivity.class));
            }
        });

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = MainActivity.this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level * 100 / (float)scale;

        ambientUpdateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientUpdateIntent = new Intent(AMBIENT_UPDATE_ACTION);
        ambientUpdatePendingIntent = PendingIntent.getBroadcast(this, 0, ambientUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ambientUpdateBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { refreshDisplayAndSetNextUpdate(); }
        };

        Refresh();
        startAlarm();

    }


    public void Refresh(){
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        String time = "Current Time:" + format.format(currentTime.getTime());
        mTextView.setText(time);
        runnable(1000);
    }

    public void runnable(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Refresh();
            }
        };
        handler.postDelayed(runnable, milliseconds);
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
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

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

    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(3600000);
    private void refreshDisplayAndSetNextUpdate() {
        if (isAmbient()) {
            // Implement data retrieval and update the screen for ambient mode
            new MainActivity.SendThread(battPath, String.valueOf(batteryPct)).start();

        } else {
            // Implement data retrieval and update the screen for interactive mode
            new MainActivity.SendThread(battPath, String.valueOf(batteryPct)).start();
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
        refreshDisplayAndSetNextUpdate();
        startAlarm();

    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        refreshDisplayAndSetNextUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(AMBIENT_UPDATE_ACTION);
        registerReceiver(ambientUpdateBroadcastReceiver, filter);
        refreshDisplayAndSetNextUpdate();
        startAlarm();


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ambientUpdateBroadcastReceiver);
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent);
        refreshDisplayAndSetNextUpdate();
        startAlarm();

    }

    @Override
    protected void onStop() {
        super.onStop();
        refreshDisplayAndSetNextUpdate();
        startAlarm();

    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        Calendar firingCal= Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR_OF_DAY, 00); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 0); // Particular minute
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
}

