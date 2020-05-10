package com.example.fyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {
    private Calendar currentTime;
    private static final String GET_firebase_steps = "firebaseStepsCount";
    private static final String GET_firebase_moderatemins = "firebaseWeeklyModerateMins";
    private SharedPreferences prefs;
    @Override
    public void onReceive(Context context, Intent intent) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GET_firebase_steps, 0);
        editor.commit();

//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(2000);
        Toast.makeText(context, "This is a new day!", Toast.LENGTH_SHORT).show();
    }
}