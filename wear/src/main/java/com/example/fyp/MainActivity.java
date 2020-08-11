package com.example.fyp;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import static android.provider.CalendarContract.EXTRA_EVENT_ID;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private ScrollView myView;
    private TextView mTextView, currentTime;
    private Button trackActivity, heartRate, stepsCount, offHeartRate, onHeartRate, googleMap;
    private Calendar calendar;
    private Boolean check=false;

    private AlarmManager ambientUpdateAlarmManager;
    private PendingIntent ambientUpdatePendingIntent;
    private BroadcastReceiver ambientUpdateBroadcastReceiver;
    private float batteryPct;

    private SensorManager sensorManager;
    private Sensor sensor;
    private static final String Initial_Count_Key = "FootStepInitialCount";
    private static final String Current_Steps_Now = "CurrentStepsCount";
    private Calendar nowTime;

    private static final String AMBIENT_UPDATE_ACTION = "com.your.package.action.AMBIENT_STEPS_UPDATE";
    private static final String TAG = "BattActivity";
    String battPath = "/batt-life-path";
    String stepsPath = "/steps-count-path";
    private SharedPreferences prefs;

    String heartPath = "/heart-rate-path";
    String maxheartpath = "/max-heart-path";
    private String msg;
    private Calendar time;
    private int heartrate=0;
    private Boolean ifalrOff = false;

    private  SensorManager mSensorManager;
    final int MY_PERMISSIONS_REQUEST_WRITE_SENSORS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        needsSENSORSPermission();

        myView= (ScrollView) findViewById(R.id.myview);
        myView.requestFocus();
        myView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_SCROLL && RotaryEncoder.isFromRotaryEncoder(ev)) {
                    // Don't forget the negation here
                    float delta = -RotaryEncoder.getRotaryAxisValue(ev) * RotaryEncoder.getScaledScrollFactor(
                            MainActivity.this);

                    // Swap these axes if you want to do horizontal scrolling instead
                    v.scrollBy(0, Math.round(delta));

                    return true;
                }

                return false;
            }
        });
        mTextView = findViewById(R.id.textView_);
        trackActivity = findViewById(R.id.btnWTrackActivity);
        heartRate = findViewById(R.id.btnWHeartRate);
        stepsCount = findViewById(R.id.btnWStepsCount);
        currentTime=findViewById(R.id.tvCurrentTime);

        offHeartRate=findViewById(R.id.btnOffHeartRate);
        onHeartRate=findViewById(R.id.btnOnHeartRate);

        googleMap=findViewById(R.id.btnGoogleMap);

        calendar=Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        currentTime.setText(currentDate);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(calendar.getTime());
        mTextView.setText(time);

        // Enables Always-on
        setAmbientEnabled();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getHartRate();
        if (!prefs.contains("PressedOnOrOff")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("PressedOnOrOff", 0);
            editor.commit();
        }
        trackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TrackActivity.class));
                //finish();
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


        googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GoogleMapsActivity.class));
            }
        });

        ambientUpdateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientUpdateIntent = new Intent(AMBIENT_UPDATE_ACTION);
        ambientUpdatePendingIntent = PendingIntent.getBroadcast(this, 0, ambientUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ambientUpdateBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { refreshDisplayAndSetNextUpdate(); }
        };

        offHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check) {
                    mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
                    mSensorManager.unregisterListener(MainActivity.this);
                    unregisterReceiver(ambientUpdateBroadcastReceiver);
                    ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent);
                    check=true;
                }

            }
        });
        onHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check) {
                    getStepsAndHeart();
                    IntentFilter filter = new IntentFilter(AMBIENT_UPDATE_ACTION);
                    registerReceiver(ambientUpdateBroadcastReceiver, filter);
                    refreshDisplayAndSetNextUpdate();
                    check=false;
                }
            }
        });

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = MainActivity.this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level * 100 / (float)scale;

        Refresh();
        startAlarm();
        //setRemindertoLockIn();
    }


    private void getHartRate() {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("PressedOnOrOff", 1);
        edit.commit();
        mSensorManager= ((SensorManager) getSystemService(SENSOR_SERVICE));
        time = Calendar.getInstance();

        //get time start of listening heart rate and end of lsitening heart rate, change HERE according to how nuh wants!!!
        //heart rate
        if((time.get(Calendar.HOUR_OF_DAY)>=6) && (time.get(Calendar.HOUR_OF_DAY)<=22)) {
            Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

            //stepsCount
            Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            mSensorManager.unregisterListener(this);
        }
    }

    private void getStepsAndHeart() {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("PressedOnOrOff", 2);
        edit.commit();
        mSensorManager= ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //stepsCount
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        time = Calendar.getInstance();
        if ((time.get(Calendar.HOUR_OF_DAY) >= 6) && (time.get(Calendar.HOUR_OF_DAY) <= 23)) {
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                msg = "" + (int) event.values[0];
                heartrate =(int) event.values[0];
                if (msg != null) {
                    if (!prefs.contains("getMaxcurrentHeartRate")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("getMaxcurrentHeartRate", 0);
                        editor.commit();
                    } else if (prefs.getInt("getMaxcurrentHeartRate", -1) < ((int) event.values[0])) {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("getMaxcurrentHeartRate", (int) event.values[0]);
                        edit.commit();
                    }
                    //heartRate.setText(msg);
                }
            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                //prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                // Initialize if it is the first time use
                if(!prefs.contains(Initial_Count_Key) || ((int) event.values[0] == 0)){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(Initial_Count_Key, (int) event.values[0]);
                    editor.commit();
                }
                if(!prefs.contains("dailyCurrentSteps") || ((int) event.values[0] == 0)){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("dailyCurrentSteps", (int) event.values[0]);
                    editor.commit();
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Current_Steps_Now, (int) event.values[0]);
                editor.commit();

                int startingStepCount = prefs.getInt(Initial_Count_Key, -1);
                int stepCount = (int) event.values[0] - startingStepCount;
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("dailyCurrentSteps", stepCount);
                //stepsCount.setText(String.valueOf(prefs.getInt("dailyCurrentSteps",(int) event.values[0])));
                edit.commit();
            } else
                Log.d(TAG, "Unknown sensor type");
        }else{
             mSensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void Refresh(){
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        String time = format.format(currentTime.getTime());
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

    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(60);
    private void refreshDisplayAndSetNextUpdate() {
        if(!prefs.contains("dailyCurrentSteps")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("dailyCurrentSteps", 0);
            editor.commit();
        }
        if(!prefs.contains("previousHeartRate")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("previousHeartRate", 0);
            editor.commit();
        }
        if(!prefs.contains("previousStepsCount")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("previousStepsCount", 0);
            editor.commit();
        }

        if (isAmbient()) {
            // Implement data retrieval and update the screen for ambient mode
            if(msg != null && (String.valueOf(prefs.getInt("previousHeartRate",-1))!=msg.replaceAll("[\\D]",""))) {
                new MainActivity.SendThread(heartPath, msg + "BPM").start();
                new MainActivity.SendThread(maxheartpath, prefs.getInt("getMaxcurrentHeartRate", -1) + "BPM").start();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("previousHeartRate", Integer.parseInt(msg.replaceAll("[\\D]","")));
                editor.commit();
            }
            if((String.valueOf(prefs.getInt("dailyCurrentSteps", -1))!=null) && (prefs.getInt("previousStepsCount",-1))!=prefs.getInt("dailyCurrentSteps", -1)){
                new MainActivity.SendThread(stepsPath, String.valueOf(prefs.getInt("dailyCurrentSteps", -1))).start();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("previousStepsCount", prefs.getInt("dailyCurrentSteps", -1));
                editor.commit();
            }

        } else {
            // Implement data retrieval and update the screen for interactive mode
            new MainActivity.SendThread(battPath, String.valueOf(batteryPct)).start();
            if((String.valueOf(prefs.getInt("dailyCurrentSteps", -1))!=null) && (prefs.getInt("previousStepsCount",-1))!=prefs.getInt("dailyCurrentSteps", -1)){
                new MainActivity.SendThread(stepsPath, String.valueOf(prefs.getInt("dailyCurrentSteps", -1))).start();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("previousStepsCount", prefs.getInt("dailyCurrentSteps", -1));
                editor.commit();
            }
            if(msg != null && (String.valueOf(prefs.getInt("previousHeartRate",-1))!=msg.replaceAll("[\\D]",""))) {
                new MainActivity.SendThread(heartPath, msg + "BPM").start();
                new MainActivity.SendThread(maxheartpath, prefs.getInt("getMaxcurrentHeartRate", -1) + "BPM").start();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("previousHeartRate", Integer.parseInt(msg.replaceAll("[\\D]","")));
                editor.commit();
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
        refreshDisplayAndSetNextUpdate();
        startAlarm();
        if(prefs.getInt("PressedOnOrOff", 0)==1) {
            getHartRate();
        }
        else if(prefs.getInt("PressedOnOrOff", 0)==2){
            getStepsAndHeart();
        }
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        setRemindertoLockIn();
        Log.d("Reminder", String.valueOf(prefs.getInt("ReminderToLockIn", 0)));
        refreshDisplayAndSetNextUpdate();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(prefs.getInt("PressedOnOrOff", 0)==1) {
            getHartRate();
        }
        else if(prefs.getInt("PressedOnOrOff", 0)==2){
            getStepsAndHeart();
        }
        //setRemindertoLockIn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs.getInt("PressedOnOrOff", 0)==1) {
            getHartRate();
        }
        else if(prefs.getInt("PressedOnOrOff", 0)==2){
            getStepsAndHeart();
        }
        IntentFilter filter = new IntentFilter(AMBIENT_UPDATE_ACTION);
        registerReceiver(ambientUpdateBroadcastReceiver, filter);
        refreshDisplayAndSetNextUpdate();
        startAlarm();
        //setRemindertoLockIn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ambientUpdateBroadcastReceiver);
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent);
        refreshDisplayAndSetNextUpdate();
        //startAlarm();

    }

    @Override
    protected void onStop() {
        super.onStop();
        refreshDisplayAndSetNextUpdate();
        startAlarm();
    }

    private void setRemindertoLockIn(){
        Log.d("Reminder", String.valueOf(prefs.getInt("ReminderToLockIn", 0)));
        if(!prefs.contains("ReminderToLockIn")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("ReminderToLockIn", 0);
            editor.commit();
        }
        if((heartrate!=0)&&(heartrate>=120) && (prefs.getInt("ReminderToLockIn", 0)==0)) { //change to 120
            Log.d("Reminder", "INSIDEEE");
            vibration();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder//.setMessage("Are you exercising now?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, TrackActivity.class));
                            finish();
                        }
                    })
                    .setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Are you exercising now?");
            alertDialog.show();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("ReminderToLockIn", 1);
            editor.commit();

        }

        new CountDownTimer(1800000, 1000) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("ReminderToLockIn", 0);
                editor.commit();
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();

//        final Handler handler = new Handler(); //next reminder in 30 mins
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putInt("ReminderToLockIn", 0);
//                editor.commit();
//                setRemindertoLockIn();
//            }
//        },60000); //30mins ,1800000
    }

    public Vibrator vibration() {

        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        long[] pattern = { 0, 2000, 2000 };

        v.vibrate(pattern, -1);
        return v;

    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        Calendar firingCal= Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR_OF_DAY, 0); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 1); // Particular minute
        firingCal.set(Calendar.SECOND, 00); // particular second

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startAlarm();
    }


    public void needsSENSORSPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,
                    Manifest.permission.BODY_SENSORS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //to simplify, call requestPermissions again
                Toast.makeText(MainActivity.this,
                        "shouldShowRequestPermissionRationale",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BODY_SENSORS},
                        MY_PERMISSIONS_REQUEST_WRITE_SENSORS);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BODY_SENSORS},
                        MY_PERMISSIONS_REQUEST_WRITE_SENSORS);
            }
        }else{
            // permission granted

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_SENSORS){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                Toast.makeText(MainActivity.this,
                        "Permission was granted :)",
                        Toast.LENGTH_LONG).show();
            } else {
                // permission denied.
                Toast.makeText(MainActivity.this,
                        "Permission denied!",
                        Toast.LENGTH_LONG).show();
            }
            return;
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
                startActivity(new Intent(MainActivity.this, TrackActivity.class));
            }
            else if (keyCode == KeyEvent.KEYCODE_STEM_2) {

            }
        }
        return super.onKeyDown(keyCode, event);
    }


}

