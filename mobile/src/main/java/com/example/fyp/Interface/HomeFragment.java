package com.example.fyp.Interface;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fyp.AlertReceiver;
import com.example.fyp.CustomAdapter;
import com.example.fyp.HistoryTab.HistoryActivity;
import com.example.fyp.LockInValue;
import com.example.fyp.MainActivity;
import com.example.fyp.MaxHRPointValue;
import com.example.fyp.PointValue;
import com.example.fyp.R;
import com.example.fyp.RetriveWeeklyHeartRatePointValue;
import com.example.fyp.StepsPointValue;
import com.example.fyp.UserProfile;
import com.example.fyp.WeeklyReportPointValue;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.fyp.App.CHANNEL_1_ID;



/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private Button btnClosePopup, btnShowDaily, btnShowWeekly;
    private LinearLayout myPopup, overbox;
    private ImageView trophy;
    private TextView showpopupNoti;
    Animation fromsmall,fromnothing, fortrophy,togo;

    private static final String TAG = "LineChartActivity";
    private TextView stepsCount, HeartRate, maxHeartrate, ratedMaxHR, stepsFromCompetitors, moderateMins, WeeklyModerateMinsTV, MaxFirebaseHR, tvTargetHR, tvModMins;
    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<Float> mModerateMinsArray;
    private ArrayList<String> currentTimeA;
    private ArrayList<Integer> image;
    private ArrayList <Integer> avrHeartRate = new ArrayList();
    private ArrayList <String> activityHeartRate;
    private ArrayList<Integer> avrStepsFromCompetitors;
    private ArrayList<Entry> yValues;
    private ArrayList<Float> weeklyModerateMins;
    private String time;
    private String message, steps, heart, max_HeartRate, notiRadioText, activityTrackheartRate;
    private CircularProgressBar circularProgressBar, circularProgressBarHR;
    private NotificationManagerCompat notificationManager;
    private int currentHeartRate=0, MaxHeartRate=0, currentStepsCount=0, databaseHeart;
    private Button stepbtn;

    private static final String GET_firebase_steps = "firebaseStepsCount";
    private static final String GET_firebase_moderatemins = "firebaseWeeklyModerateMins";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, stepsDataBaseRef, lockinDataBaseRef, lockinMODDataBaseRef, maxHRDataref, dataRefStepsFromCompetitors, weeklymoderateminsdataref;

    private String currentuser;
    private SharedPreferences prefs;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
    private SimpleDateFormat simpleWeekFormat = new SimpleDateFormat("u");
    private GraphView graphView;
    private LineGraphSeries lineGraphSeries, lineGraphWeekly;

    //private Activity activity = getActivity();

    private String date;
    private String week;

    private Activity activity;

    private float fifety, seventyfive;
    private int duration;
    private float mins, sec, activity_heartRate=0;
    private String activity_heart_ratey;

    FloatingActionButton fab;
    private boolean stopThread;
    private ArrayList<Integer> weeklyAvrHeartRate;
    private ArrayList<Double> calculateWeeklyAvrHeartRate;

    private SwipeRefreshLayout swipeRefreshLayout;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
    MessageReceiver messageReceiver = new MessageReceiver();

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        needsPermission();
        activity=getActivity();
        yValues= new ArrayList<>();
        startAlarm();


        graphView=v.findViewById(R.id.graphView);
        lineGraphSeries=new LineGraphSeries();
        lineGraphWeekly=new LineGraphSeries();

        swipeRefreshLayout=v.findViewById(R.id.refreshLayout);

//        mrecyclerView = v.findViewById(R.id.activity_RV);
        stepsCount=v.findViewById(R.id.tvStepsCount);
        HeartRate=v.findViewById(R.id.tvResting_value);
        maxHeartrate=v.findViewById(R.id.tvMAX_value);
        ratedMaxHR=v.findViewById(R.id.tvAvgResting_value);
        circularProgressBar = v.findViewById(R.id.circularProgressBar);
        circularProgressBarHR=v.findViewById(R.id.circularProgressBarModerate);
        //stepsFromCompetitors=v.findViewById(R.id.tv_avrStepsOfCompetitors);
//        moderateMins=v.findViewById(R.id.tvModerateMinsToday);
        WeeklyModerateMinsTV=v.findViewById(R.id.tvWeeklyModerateMins);
        MaxFirebaseHR=v.findViewById(R.id.tvMaxHR_fromAge);
        tvTargetHR=v.findViewById(R.id.tvTargetHr);
        tvModMins=v.findViewById(R.id.tvModMinutes);

//        btnShowDaily=v.findViewById(R.id.btnDailyHeartRate);
//        btnShowWeekly=v.findViewById(R.id.btnWeeklyHeartRate);

        //for pop up
        btnClosePopup=v.findViewById(R.id.btnclosePopUp);
        overbox=v.findViewById(R.id.popUpUI);
        myPopup=v.findViewById(R.id.myPopUp);
        showpopupNoti=v.findViewById(R.id.popUpnoti);
        trophy=v.findViewById(R.id.trophyPopup);
        fromsmall= AnimationUtils.loadAnimation(getContext(),R.anim.fromsmall);
        fromnothing= AnimationUtils.loadAnimation(getContext(),R.anim.fromnothing);
        fortrophy=AnimationUtils.loadAnimation(getContext(),R.anim.fortrophy);
        togo=AnimationUtils.loadAnimation(getContext(),R.anim.togo);
        myPopup.setAlpha(0);
        overbox.setAlpha(0);
        trophy.setVisibility(View.GONE);

        //for shared prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        //getting data from firebase
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat weekFormat = new SimpleDateFormat("u");
        week = weekFormat.format(currentDate.getTime());
        date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        databaseReference = firebaseDatabase.getReference("Chart Values/" + currentuser +"/"); //"NDnXWC4MHfYFBaayGPcJ2SghYVF2" for jovita
        stepsDataBaseRef=firebaseDatabase.getReference("Steps Count/" +currentuser + "/" + date );
        lockinDataBaseRef = firebaseDatabase.getReference("Activity Tracker/" +currentuser + "/" + date );
        maxHRDataref = firebaseDatabase.getReference("MaxHeartRate/" +currentuser + "/" + date );
        dataRefStepsFromCompetitors = firebaseDatabase.getReference();
        weeklymoderateminsdataref=firebaseDatabase.getReference("Weekly Moderate Mins/" + currentuser+"/");
        checkModMInsRefresh();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

       //insertStepsData(); //put this when testing with watch
        getMaxHR();
        startThread();
//        getDataRefOfStepsOfCompetitors();
//        retrieveStepsData();
//        retrieveData();
//          new LongRunningTask().execute();
//        retrieveMaxHR();
//        showGraph();
//        RetrieveLockInData();
        circularProgressBar.setProgressMax(7500);
        circularProgressBarHR.setProgressMax(150);

//        ExpandableTextView expTv1 = v.findViewById(R.id.expand_text_view).findViewById(R.id.expand_text_view);
        circularProgressBar.setRoundBorder(true);
        circularProgressBarHR.setRoundBorder(true);
//        expTv1.setText(getString(R.string.intensity_workout_details));
        fab = (FloatingActionButton) v.findViewById(R.id.btnAddActivity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ExerciseFragment.class));
            }
        });
        stepbtn = v.findViewById(R.id.step_btn);
        stepbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HistoryActivity.class));
            }
        });

        if (!prefs.contains("GET_TODAY_DATE")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("GET_TODAY_DATE", currentDate.get(Calendar.DAY_OF_YEAR)-1);
            editor.commit();
        }
        showCongrats();

        notificationManager = NotificationManagerCompat.from(getActivity());
        getRadioText();
        Refresh();

//        btnShowDaily.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                retrieveData();
//                showGraph();
//            }
//        });
//        btnShowWeekly.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startThread();
                //getDataRefOfStepsOfCompetitors();
//                checkModMInsRefresh();
//                retrieveStepsData();
//                retrieveData();
//                RetrieveLockInData();
//                retrieveWeeklyModerateMins();
//                retrieveMaxHR();
//                showGraph();
                showCongrats();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    public void checkModMInsRefresh(){
        if (!prefs.contains("weeklyModMinsCheck")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("weeklyModMinsCheck", 0);
            editor.commit();
        }
        if((Integer.parseInt(week)==7) && (prefs.getInt("weeklyModMinsCheck", 0) == 0)){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("weeklyModMinsCheck", 1);
            editor.commit();
            //firebase clear data
            weeklymoderateminsdataref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(activity, "This is a new week", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if((Integer.parseInt(week)!=7))
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("weeklyModMinsCheck", 0);
            editor.commit();
        }
    }


    public void needsPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //to simplify, call requestPermissions again
                Toast.makeText(getContext(),
                        "shouldShowRequestPermissionRationale",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }else{
            // permission granted

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                Toast.makeText(getContext(),
                        "Permission was granted, thx:)",
                        Toast.LENGTH_LONG).show();
            } else {
                // permission denied.
                Toast.makeText(getContext(),
                        "Permission denied! Oh:(",
                        Toast.LENGTH_LONG).show();
            }
            return;
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showCongrats(){
        Calendar currentDate = Calendar.getInstance();
        if(prefs.getInt("GET_TODAY_DATE",currentDate.get(Calendar.DAY_OF_YEAR))!=currentDate.get(Calendar.DAY_OF_YEAR)) {
            if (prefs.getInt(GET_firebase_steps, -1) >= 7500) {
                trophy.setVisibility(View.VISIBLE);
                trophy.startAnimation(fortrophy);
                overbox.setAlpha(1);
                overbox.startAnimation(fromnothing);
                myPopup.setAlpha(1);
                myPopup.startAnimation(fromsmall);
                showpopupNoti.setText("You have completed 7500 steps today.");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("GET_TODAY_DATE", currentDate.get(Calendar.DAY_OF_YEAR));
                editor.commit();
            }
            if (prefs.getFloat(GET_firebase_moderatemins, -1) >= 150) {
                trophy.setVisibility(View.VISIBLE);
                trophy.startAnimation(fortrophy);
                overbox.setAlpha(1);
                overbox.startAnimation(fromnothing);
                myPopup.setAlpha(1);
                myPopup.startAnimation(fromsmall);
                showpopupNoti.setText("You have completed 150 mins of moderate exercise this week.");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("GET_TODAY_DATE", currentDate.get(Calendar.DAY_OF_YEAR));
                editor.commit();

            }
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overbox.setAlpha(0);
                    myPopup.startAnimation(togo);
                    trophy.startAnimation(togo);
                    trophy.setVisibility(View.GONE);

                    ViewCompat.animate(myPopup).setStartDelay(1000).alpha(0).start();
                    ViewCompat.animate(overbox).setStartDelay(1000).alpha(0).start();

                }
            });

        }
    }

    public void getMaxHR(){
        //get Max heart rate for each individual age
        DatabaseReference mydatabaseRef = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid());
        mydatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    int agey = Integer.parseInt(userProfile.getUserAge().replaceAll("[\\D]", ""));
                    MaxHeartRate = 220 - agey;

                    if (!prefs.contains("GET_MAX_HEART_RATE_FROM_AGE")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("GET_MAX_HEART_RATE_FROM_AGE", MaxHeartRate);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("GET_MAX_HEART_RATE_FROM_AGE", MaxHeartRate);
                        editor.commit();
                    }
                    MaxFirebaseHR.setText(String.valueOf(prefs.getInt("GET_MAX_HEART_RATE_FROM_AGE", MaxHeartRate)) + "BPM");
                    fifety= (float) (0.5 * prefs.getInt("GET_MAX_HEART_RATE_FROM_AGE", MaxHeartRate));
                    if (!prefs.contains("GET_FIFETY_HEART_RATE_FROM_AGE")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("GET_FIFETY_HEART_RATE_FROM_AGE",  fifety);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("GET_FIFETY_HEART_RATE_FROM_AGE", fifety);
                        editor.commit();
                    }
                    seventyfive= (float) (0.75 * prefs.getInt("GET_MAX_HEART_RATE_FROM_AGE", MaxHeartRate));
                    if (!prefs.contains("GET_SEVENTYFIVE_HEART_RATE_FROM_AGE")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("GET_SEVENTYFIVE_HEART_RATE_FROM_AGE",  seventyfive);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("GET_SEVENTYFIVE_HEART_RATE_FROM_AGE", seventyfive);
                        editor.commit();
                    }
                    tvTargetHR.setText("Target activity HR: \n" + fifety + "BPM - " + seventyfive + "BPM");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //    //for getDataRefOfStepsOfCompetitors to convert long to int
//    public static int safeLongToInt(long l) {
//        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
//            throw new IllegalArgumentException
//                    (l + " cannot be cast to int without changing its value.");
//        }
//        return (int) l;
//    }
//    private void getDataRefOfStepsOfCompetitors(){
//        dataRefStepsFromCompetitors.child("Steps Count").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                avrStepsFromCompetitors = new ArrayList<>();
//                if(dataSnapshot.hasChildren()){
//                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
//                        if(myDataSnapshot.hasChildren() && (myDataSnapshot.child(date).getValue()!=null)) {
//                            int steppy = safeLongToInt((Long) myDataSnapshot.child(date +"/steps").getValue());
//                            avrStepsFromCompetitors.add(steppy);
//                            double steps=calculateAverageStepsOfCompetitors(avrStepsFromCompetitors);
//                            stepsFromCompetitors.setText(String.format("%.1f", steps ) + "/7500 steps");
//                        }else{
//                            //Toast.makeText(getContext(), "Error in getting participants steps", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }else{
//                    Toast.makeText(getActivity(), "Error in getting participants steps", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void showGraph() {
//        graphView.removeAllSeries();
//        graphView.addSeries(lineGraphSeries);
//        graphView.removeSeries(lineGraphWeekly);
//        graphView.setTitle("Heart Rate(BPM)");
//        graphView.getViewport().setMinX(new Date().getTime()-10800000);
//        graphView.getViewport().setMaxX(new Date().getTime());
//        graphView.getViewport().setMinY(50);
//        graphView.getViewport().setMaxY(170);
//        graphView.getViewport().setYAxisBoundsManual(true);
//        graphView.getViewport().setXAxisBoundsManual(true);
//        graphView.getViewport().setScrollable(true);
//        graphView.getViewport().setScalable(true);
//
//        lineGraphSeries.setDrawBackground(true);
//
//        lineGraphSeries.setBackgroundColor(Color.argb(100, 163, 180, 195));
//        lineGraphSeries.setColor(Color.argb(255, 0, 51, 102));
//        lineGraphSeries.setDrawDataPoints(true);
//        lineGraphSeries.setDataPointsRadius(15);
//        lineGraphSeries.setThickness(10);
//        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
//        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if(isValueX) {
//                    return simpleDateFormat.format(new Date((long) value));
//                }else {
//                    return super.formatLabel(value, isValueX);
//                }
//
//            }
//        });
//        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
//            @Override
//            public void onTap(Series series, DataPointInterface dataPoint) {
//                Toast.makeText(activity, "Heart Rate: "+dataPoint.getY() + "BPM", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void showWeeklyGraph() {
        graphView.removeAllSeries();
        graphView.addSeries(lineGraphWeekly);
        graphView.removeSeries(lineGraphSeries);
        graphView.setTitle("Heart Rate(BPM)");
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(6);
        graphView.getViewport().setMinY(50);
        graphView.getViewport().setMaxY(170);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        lineGraphWeekly.setDrawBackground(true);
        lineGraphWeekly.setBackgroundColor(Color.argb(100, 163, 180, 195));
        lineGraphWeekly.setColor(Color.argb(255, 0, 51, 102));
        lineGraphWeekly.setDrawDataPoints(true);
        lineGraphWeekly.setDataPointsRadius(15);
        lineGraphWeekly.setThickness(10);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Calendar calendar = Calendar.getInstance();
                    //Monday==1, sunday==7;
                    int day = calendar.get(Calendar.DAY_OF_WEEK)-1;
                    String printDay;
                    switch ((int) value) {
                        case 0:
                            // get 7days ago
                            day=day-6;
                            break;
                        case 1:
                            // get 6 days ago
                            day=day-5;
                            break;
                        case 2:
                            //
                            day=day-4;
                            break;
                        case 3:
                            // get 6 days ago
                            day=day-3;
                            break;
                        case 4:
                            // get 6 days ago
                            day=day-2;
                            break;
                        case 5:
                            // get 6 days ago
                            day=day-1;
                            break;
                        case 6:
                            // get 6 days ago
                            day=day;

                            break;
                    }
                    switch (day) {
                        case 1:
                            // get 7days ago
                            printDay="Monday";
                            break;
                        case 2:
                            // get 6 days ago
                            printDay="Tuesday";
                            break;
                        case 3:
                            //
                            printDay="Wednesday";
                            break;
                        case 4:
                            // get 6 days ago
                            printDay="Thursday";
                            break;
                        case 5:
                            // get 6 days ago
                            printDay="Friday";
                            break;
                        case 6:
                            // get 6 days ago
                            printDay="Saturday";
                            break;
                        case 7:
                            // get 6 days ago
                            printDay="Sunday";

                            break;
                        case 0:
                            // get 6 days ago
                            printDay="Sunday";
                            break;
                        case -1:
                            //
                            printDay="Saturday";
                            break;
                        case -2:
                            // get 6 days ago
                            printDay="Friday";
                            break;
                        case -3:
                            // get 6 days ago
                            printDay="Thursday";
                            break;
                        case -4:
                            // get 6 days ago
                            printDay="Wednesday";
                            break;
                        case -5:
                            // get 6 days ago
                            printDay="Tuesday";

                            break;
                        case -6:
                            // get 6 days ago
                            printDay="Monday";

                            break;

                        default:
                            printDay="none";
                            break;
                    }

                    return printDay;
                }
                return super.formatLabel(value, isValueX);
            }
        });
        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(activity, "Heart Rate: "+dataPoint.getY() + "BPM", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertData() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        databaseReference = firebaseDatabase.getReference("Chart Values/" + currentuser +"/");
        String id = databaseReference.child(date).push().getKey();
        long x=new Date().getTime();
        int y=currentHeartRate;
        PointValue pointValue = new PointValue(x,y);
        databaseReference.child(date).child(id).setValue(pointValue);
        retriveWeeklyData();
    }

//    private void retrieveData() {
//        databaseReference.child(date).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                DataPoint[] dataVals = new DataPoint[(int) dataSnapshot.getChildrenCount()];
//                int index =0;
//
//                if(dataSnapshot.hasChildren()){
//                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
//                        PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
//                        dataVals[index] = new DataPoint(pointValue.getxValue(),pointValue.getyValue());
//                        index++;
//
//                        avrHeartRate.add(pointValue.getyValue());
//                        double heartRate=calculateAverageStepsOfCompetitors(avrHeartRate);
//                        ratedMaxHR.setText(String.format("%.1f", heartRate) + "BPM");
//                    }
//                    lineGraphSeries.resetData(dataVals);
//
//                }else{
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    private void retriveWeeklyData() {
            Query chatQuery = databaseReference.orderByChild(currentuser).limitToLast(7);
            chatQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    weeklyAvrHeartRate = new ArrayList<>();
                    double heartRate=0;
                    if (dataSnapshot.hasChildren()) {
                        DataPoint[] dataVals = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                        int indexWeekly = 0;
                        for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                            weeklyAvrHeartRate = new ArrayList<>();
                            if (myDataSnapshot.hasChildren()) {
                                //to get all daily values
                                for (DataSnapshot lastdataSnapshot : myDataSnapshot.getChildren()) {
                                    RetriveWeeklyHeartRatePointValue pointValue = lastdataSnapshot.getValue(RetriveWeeklyHeartRatePointValue.class);
                                    //avr of each day
                                    weeklyAvrHeartRate.add(pointValue.getyValue());
                                }
                                heartRate = calculateAverageStepsOfCompetitors(weeklyAvrHeartRate);
                                switch ((int) dataSnapshot.getChildrenCount()){
                                    case 7:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly, heartRate);
                                        indexWeekly++;
                                        break;
                                    case 6:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly+1, heartRate);
                                        indexWeekly++;
                                        break;
                                    case 5:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly+2, heartRate);
                                        indexWeekly++;
                                        break;
                                    case 4:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly+3, heartRate);
                                        indexWeekly++;
                                        break;
                                    case 3:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly+4, heartRate);
                                        indexWeekly++;
                                        break;
                                    case 2:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly+5, heartRate);
                                        indexWeekly++;
                                        break;
                                    case 1:
                                        dataVals[indexWeekly] = new DataPoint(indexWeekly+6, heartRate);
                                        indexWeekly++;
                                        break;
                                }
                            }
                        }
                        lineGraphWeekly.resetData(dataVals);
                    }else{
                        Toast.makeText(activity, "Retrieve heart rate failed", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void insertStepsData() {
        if (!prefs.contains(GET_firebase_steps)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(GET_firebase_steps, 0);
            editor.commit();
        }
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        stepsDataBaseRef=firebaseDatabase.getReference("Steps Count/" +currentuser + "/" + date );
        StepsPointValue pointSteps = new StepsPointValue(prefs.getInt(GET_firebase_steps, 0));
        stepsDataBaseRef.setValue(pointSteps); //.child(id)

        retrieveStepsData();
    }

    private void retrieveStepsData() {
            stepsDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    StepsPointValue stepsPointValue = dataSnapshot.getValue(StepsPointValue.class);
                    currentStepsCount = stepsPointValue.getSteps();
                    stepsCount.setText(String.valueOf(currentStepsCount));
                    float steps = Float.parseFloat(String.valueOf(stepsPointValue.getSteps()));
                    circularProgressBar.setProgressWithAnimation(steps); // =1s

//                    if (!prefs.contains(GET_firebase_steps)) {
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putInt(GET_firebase_steps, 0);
//                        editor.commit();
//                    } else {
//                        SharedPreferences.Editor edit = prefs.edit();
//                        edit.putInt(GET_firebase_steps, currentStepsCount);
//                        edit.commit();
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void insertLockInData() {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        date = dateFormat.format(currentTime.getTime()).replaceAll("[\\D]","");
        lockinDataBaseRef = firebaseDatabase.getReference("Activity Tracker/" +currentuser + "/" + date );

        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String cTime = format.format(currentTime.getTime());
        String id = lockinDataBaseRef.push().getKey();
        LockInValue lockInValue = new LockInValue(message,time,cTime, activityTrackheartRate);
        lockinDataBaseRef.child(id).setValue(lockInValue);
        insertModerateMins();
        RetrieveLockInData();
    }

    private void RetrieveLockInData() {
        lockinDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.getChildrenCount();
                mDataSet = new ArrayList<>();
                mTimeSet=new ArrayList<>();
                image = new ArrayList<>();
                currentTimeA = new ArrayList<>();
                mModerateMinsArray=new ArrayList<>();
                activityHeartRate = new ArrayList();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        LockInValue lockInValue = myDataSnapshot.getValue(LockInValue.class);
                        mTimeSet.add(lockInValue.getDuration());
                        mDataSet.add(lockInValue.getActivity());
                        currentTimeA.add(lockInValue.getcTime());
                        if((lockInValue.getAvrHeartRate()==null) ){
                            activityHeartRate.add("No heart rate detected");
                        }
                        else {
                            activityHeartRate.add("Average heart rate: " +lockInValue.getAvrHeartRate());
                            activity_heart_ratey=lockInValue.getActivity();
                            activity_heartRate=Float.parseFloat(lockInValue.getAvrHeartRate().replaceAll("[^0-9.]", ""));
                        }
                        InsertRecyclerView();

                        if(lockInValue.getDuration()!=null) {
                            duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
                             mins = duration / 100;
                             sec = duration % 100;
                            mModerateMinsArray.add(mins+(sec/60));
                        }
                    }
                    if (!prefs.contains("CHECK_IF_DISPLAYED_DIALOG")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("CHECK_IF_DISPLAYED_DIALOG", count-1);
                        editor.commit();
                    }
                    if(prefs.getLong("CHECK_IF_DISPLAYED_DIALOG",count-1)!=count) {
                        ShowAlertDialogWhenCompleteActivity();
                        //
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("CHECK_IF_DISPLAYED_DIALOG", count);
                        editor.commit();
                    }
                    insertWeeklyModerateMins();
                    double mmarray=calculateSumOfModerateMins(mModerateMinsArray);
                    moderateMins.setText("Moderate exercise today: " + String.format("%.1f",mmarray ) + "mins");


                }else{
                    Toast.makeText(activity,"No activity to retrieve", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity,"Error in retrieving activity", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ShowAlertDialogWhenCompleteActivity(){
        MaxHeartRate= prefs.getInt("GET_MAX_HEART_RATE_FROM_AGE", MaxHeartRate);
        if((activity_heartRate!=0)&&(MaxHeartRate!=0)) {
            if (activity_heartRate>seventyfive || activity_heartRate<fifety) { //shud be in between 50%-75%
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Your average heart rate for " + activity_heart_ratey + " is: " + activity_heartRate + "BPM.\nPlease slow down as your average heart rate is very high.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Congratulations on completing a workout!");
                alertDialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Your average heart rate for " + activity_heart_ratey + " is: " + activity_heartRate
                        + "BPM.\nThis is a moderate intensity workout" + "\nYou are doing great!\nKeep it up! ")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Congratulations on completing a workout!");
                alertDialog.show();
            }
        }

    }

    private void insertModerateMins(){
        if((activity_heartRate!=0)&&(MaxHeartRate!=0)) {
            if (activity_heartRate <= seventyfive || activity_heartRate >= fifety) { //if hr is under this range
                //send data to firebase
                Calendar currentTime = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                date = dateFormat.format(currentTime.getTime()).replaceAll("[\\D]","");
                lockinMODDataBaseRef = firebaseDatabase.getReference("Moderate Mins/" +currentuser + "/" + date );

                SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                String cTime = format.format(currentTime.getTime());
                String id = lockinDataBaseRef.push().getKey();
                LockInValue lockInValue = new LockInValue(message,time,cTime, activityTrackheartRate);
                lockinMODDataBaseRef.child(id).setValue(lockInValue);
            }
        }
    }

    private void insertWeeklyModerateMins(){
        double mMarray = calculateSumOfModerateMins(mModerateMinsArray);
        WeeklyReportPointValue weeksPointValue = new WeeklyReportPointValue(String.format("%.1f", mMarray));
        weeklymoderateminsdataref.child(week).setValue(weeksPointValue);
        retrieveWeeklyModerateMins();
    }
    private void retrieveWeeklyModerateMins(){
        weeklymoderateminsdataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weeklyModerateMins=new ArrayList<>();
                double smmarray;
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        WeeklyReportPointValue weeksModerateMinsPointValue = myDataSnapshot.getValue(WeeklyReportPointValue.class);
                        weeklyModerateMins.add(Float.valueOf(weeksModerateMinsPointValue.getModerateMins()));
                        smmarray =calculateSumOfModerateMins(weeklyModerateMins);
                        //WeeklyModerateMinsTV.setText("Moderate exerecise this week: " + String.format("%.1f", smmarray) + "mins");
                        tvModMins.setText(String.format("%.1f", smmarray));

                        if (!prefs.contains(GET_firebase_moderatemins)) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putFloat(GET_firebase_moderatemins, 0);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putFloat(GET_firebase_moderatemins, (float) smmarray);
                            edit.commit();
                        }
                    }
                    circularProgressBarHR.setProgressWithAnimation(prefs.getFloat(GET_firebase_moderatemins,0)); // =1s
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void insertMaxHR() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");

        maxHRDataref = firebaseDatabase.getReference("MaxHeartRate/" +currentuser + "/" + date );

         MaxHRPointValue maxHRPointValue2 = new MaxHRPointValue(Integer.parseInt(max_HeartRate.replaceAll("[\\D]", "")));
         maxHRDataref.setValue(maxHRPointValue2);
         retrieveMaxHR();
    }

    private void retrieveMaxHR() {
        maxHRDataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    MaxHRPointValue maxHRPointValue = dataSnapshot.getValue(MaxHRPointValue.class);
                    if (maxHRPointValue.getHr() != 0) {
                        databaseHeart = maxHRPointValue.getHr();
                        maxHeartrate.setText(String.valueOf(databaseHeart) + "BPM");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        //setup a broadcast receiver to receive the messages from the wear device via the listenerService.
        public class MessageReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!prefs.contains("HeartRateFromWear")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("HeartRateFromWear", 0);
                    editor.commit();
                }
                if (!prefs.contains("ActivityFromWear")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("ActivityFromWear", "null");
                    editor.commit();
                }

                if (intent.getStringExtra("message") != null || intent.getStringExtra("timing") != null
                        || intent.getStringExtra("activityTrackerHeartRate")!=null)
                {
                    if (intent.getStringExtra("timing") != null) {
                        time = intent.getStringExtra("timing");
                    }
                    else if (intent.getStringExtra("activityTrackerHeartRate") != null) {
                        activityTrackheartRate = intent.getStringExtra("activityTrackerHeartRate");
                    }
                    else if ((intent.getStringExtra("message") != null) &&
                            ((intent.getStringExtra("message") != prefs.getString("ActivityFromWear", "null")))) {
                        message = intent.getStringExtra("message");
                        Log.v(TAG, "Main activity received message: " + message);
                            insertLockInData();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("ActivityFromWear", message);
                            editor.commit();
                    }
                }
                else if (intent.getStringExtra("heartRate") != null ){
                        heart = intent.getStringExtra("heartRate");
                        Log.v(TAG, "Main activity received message: " + message);
                        HeartRate.setText(heart);
                        currentHeartRate = Integer.parseInt(heart.replaceAll("[\\D]", ""));

                    if(currentHeartRate!=(prefs.getInt("HeartRateFromWear",-1))) {
                        insertData();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("HeartRateFromWear", currentHeartRate);
                        editor.commit();
                    }
                    //String.format("Value of a: %.2f", a)

                } else if (intent.getStringExtra("countSteps") != null) {
                    steps = intent.getStringExtra("countSteps");
                    Log.v(TAG, "Main activity received message: " + message);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(GET_firebase_steps, Integer.parseInt(steps));
                    editor.commit();
                    insertStepsData();

                } else if (intent.getStringExtra("maxHeartRate") != null) {
                    max_HeartRate = intent.getStringExtra("maxHeartRate");
                    insertMaxHR();
                }
            }
        }

        //calculate sum of mins per day
        private double calculateSumOfModerateMins(ArrayList<Float> moderateMins){
            double sum = 0;
            for(int i = 0; i < moderateMins.size(); i++)
                sum += moderateMins.get(i);
            return sum;
        }

        //calculate avr steps of other participants
        private double calculateAverageStepsOfCompetitors(ArrayList<Integer> avgStepsFromCompetitors) {
        Integer sum = 0;
        if (!avgStepsFromCompetitors.isEmpty() && avgStepsFromCompetitors!=null) {
            for (Integer avrStepsFromCompet : avgStepsFromCompetitors) {
                sum += avrStepsFromCompet;
            }
            return sum.doubleValue() / avgStepsFromCompetitors.size();
        }
        return sum;
    }

    //getting radio option for either 4pm or 7pm by user
        public void getRadioText() {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseRadio = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid());
            databaseRadio.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    notiRadioText = userProfile.getRadiotext();
                    //tv5.setText(notiRadioText);
                    //Refresh();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //notification
        public void sendOnChannel1(View v) {
            String title = "Alert!!!";
            String message = "You have exceeded the Maximum Heart Rate!\n Please slow down!";

            if(activity!=null) {
                Notification notification = new NotificationCompat.Builder(activity, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_message)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                notificationManager.notify(1, notification);
            }
        }

        public void sendOnChannel2(View v) {
            String title = "Alert!!!";
            String message = "You have not reached half of the minimum steps today!\n Please exercise!";

            Notification notification = new NotificationCompat.Builder(activity, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_message)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            notificationManager.notify(1, notification);
        }

        public void sendOnChannel3(View v) {
            String title = "Alert!!!";
            String message = "You have not reached the minimum steps today!\n Please exercise!";

            Notification notification = new NotificationCompat.Builder(activity, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_message)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            notificationManager.notify(1, notification);
        }

        public void Refresh() {
            Calendar currentTime = Calendar.getInstance();
            if(currentHeartRate!=0 && (MaxHeartRate!=0)) {
                if (currentHeartRate > MaxHeartRate) {
                    sendOnChannel1(null);

                }
            }

            if(currentStepsCount!=0) {
                //insert different timings here for prompt of steps count
                if ((currentStepsCount < (7500 / 2)) && (currentTime.get(Calendar.HOUR_OF_DAY) == 12) && (currentTime.get(Calendar.MINUTE) == 00)) {
                    sendOnChannel2(null);

                }

                getRadioText();
                int step = prefs.getInt(GET_firebase_steps, 0);
                if ((notiRadioText!=null)&& (notiRadioText.equals("4pm") )) {
                    if ((currentTime.get(Calendar.HOUR_OF_DAY) == 16) && (currentTime.get(Calendar.MINUTE) == 00) && (step < 7500))  {
                        sendOnChannel3(null);

                    }
                } else if ( (notiRadioText!=null)&&(notiRadioText.equals("7pm"))) {
                    if ((step < 7500) && (currentTime.get(Calendar.HOUR_OF_DAY) == 19) && (currentTime.get(Calendar.MINUTE) == 00)) {
                        sendOnChannel3(null);

                    }
                }
            }
            runnable(60000);
        }

        public void runnable(int milliseconds) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Refresh();
                }
            };
            handler.postDelayed(runnable, milliseconds);
        }

        //insert activity into home page
        public void InsertRecyclerView() {
            mlayoutManager = new LinearLayoutManager(getContext());
            mrecyclerView.setHasFixedSize(true);
            mAdapter = new CustomAdapter(mDataSet, mTimeSet, currentTimeA, image, activityHeartRate);
            mrecyclerView.setLayoutManager(mlayoutManager);
            mrecyclerView.setAdapter(mAdapter);
        }


    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        startAlarm();
        Refresh();
        //stopThread();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);
        startThread();
    }

    @Override
    public void onPause() {
        super.onPause();
        startAlarm();
        Refresh();
        // Register the local broadcast receiver
//        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
//        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);
        startThread();

    }

    @Override
    public void onStop() {
        super.onStop();
        startAlarm();
        Refresh();

        // Register the local broadcast receiver
//        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
//        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);
        startThread();

    }

    @Override
    public void onStart() {
        super.onStart();
        startAlarm();
        Refresh();

        // Register the local broadcast receiver
//        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
//        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);
        startThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        startAlarm();
        Refresh();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);
        startThread();
    }

    public void startThread() {
        stopThread = false;
        ExampleRunnable runnable = new ExampleRunnable(10);
        new Thread(runnable).start();
    }
    public void stopThread() {
        stopThread = true;
    }
    class ExampleRunnable implements Runnable {
        int seconds;
        ExampleRunnable(int seconds) {
            this.seconds = seconds;
        }
        @Override
        public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //getDataRefOfStepsOfCompetitors();
                            insertStepsData(); //put this when testing with watch
                            checkModMInsRefresh();
                            retrieveStepsData();
                            retrieveMaxHR();
                            retrieveWeeklyModerateMins();
                            RetrieveLockInData();
//                            retrieveData();
//                            showGraph();
                            retriveWeeklyData();
                            showWeeklyGraph();

                        }
                    });
//                }
//                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }




