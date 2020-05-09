package com.example.fyp.Interface;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.CustomAdapter;
import com.example.fyp.HistoryTab.HistoryActivity;
import com.example.fyp.LockInValue;
import com.example.fyp.MaxHRPointValue;
import com.example.fyp.PointValue;
import com.example.fyp.R;
import com.example.fyp.StepsPointValue;
import com.example.fyp.UserProfile;
import com.example.fyp.WeeklyReportPointValue;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.fyp.App.CHANNEL_1_ID;



/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private Button btnClosePopup;
    private LinearLayout myPopup, overbox;
    private ImageView trophy;
    private TextView showpopupNoti;
    Animation fromsmall,fromnothing, fortrophy,togo;

    private static final String TAG = "LineChartActivity";
    private TextView stepsCount, HeartRate, maxHeartrate, ratedMaxHR, stepsFromCompetitors, moderateMins, WeeklyModerateMinsTV;
    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<Float> mModerateMinsArray;
    private ArrayList<String> currentTimeA;
    private ArrayList<String> activityAvrHeartRate;
    private ArrayList<Integer> image;
    private ArrayList <Integer> avrHeartRate = new ArrayList();
    private ArrayList <Integer> sumOf = new ArrayList();
    private ArrayList<Integer> avrStepsFromCompetitors;
    private ArrayList<Entry> yValues;
    private ArrayList<Float> weeklyModerateMins;
    private String time;
    private String message, steps, heart, max_HeartRate, notiRadioText, activityTrackheartRate;
    private CircularProgressBar circularProgressBar;
    private NotificationManagerCompat notificationManager;
    private int currentHeartRate=0, MaxHeartRate, currentStepsCount=0, databaseHeart;
    private Button stepbtn;

    private static final String GET_firebase_steps = "firebaseStepsCount";
    private static final String GET_firebase_moderatemins = "firebaseWeeklyModerateMins";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, stepsDataBaseRef, lockinDataBaseRef, maxHRDataref, dataRefStepsFromCompetitors, weeklymoderateminsdataref;


    private SharedPreferences prefs;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
    private GraphView graphView;
    private LineGraphSeries lineGraphSeries;

    //private Activity activity = getActivity();

    private String date;
    private String week;

    private Activity activity;

    FloatingActionButton fab;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        activity=getActivity();
        yValues= new ArrayList<>();

        graphView=v.findViewById(R.id.graphView);
        lineGraphSeries=new LineGraphSeries();
        graphView.addSeries(lineGraphSeries);

        mrecyclerView = v.findViewById(R.id.activity_RV);
        stepsCount=v.findViewById(R.id.tvStepsCount);
        HeartRate=v.findViewById(R.id.tvResting_value);
        maxHeartrate=v.findViewById(R.id.tvMAX_value);
        ratedMaxHR=v.findViewById(R.id.tvAvgResting_value);
        circularProgressBar = v.findViewById(R.id.circularProgressBar);
        stepsFromCompetitors=v.findViewById(R.id.tv_avrStepsOfCompetitors);
        moderateMins=v.findViewById(R.id.tvModerateMinsToday);
        WeeklyModerateMinsTV=v.findViewById(R.id.tvWeeklyModerateMins);

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

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        circularProgressBar.setProgressMax(7500);

        //lineChart=v.findViewById(R.id.lineChart);
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat weekFormat = new SimpleDateFormat("u");
        week = weekFormat.format(currentDate.getTime());
        date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        databaseReference = firebaseDatabase.getReference("Chart Values/" + currentuser +"/" + date);
        stepsDataBaseRef=firebaseDatabase.getReference("Steps Count/" +currentuser + "/" + date );
        lockinDataBaseRef = firebaseDatabase.getReference("Activity Tracker/" +currentuser + "/" + date );
        maxHRDataref = firebaseDatabase.getReference("MaxHeartRate/" +currentuser + "/" + date );
        dataRefStepsFromCompetitors = firebaseDatabase.getReference();
        weeklymoderateminsdataref=firebaseDatabase.getReference("Weekly Moderate Mins/" + currentuser+"/");


        notificationManager = NotificationManagerCompat.from(getActivity());
        getRadioText();
        Refresh();
        //getting data from firebase
        getDataRefOfStepsOfCompetitors();
        retrieveStepsData();
        retrieveData();
        RetrieveLockInData();
        retrieveMaxHR();
        showGraph();


        //get Max heart rate for each individual age
        DatabaseReference mydatabaseRef = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid());
        mydatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                MaxHeartRate= 220 - Integer.parseInt(userProfile.getUserAge().replaceAll("[\\D]",""));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        ExpandableTextView expTv1 = v.findViewById(R.id.expand_text_view).findViewById(R.id.expand_text_view);
        circularProgressBar.setRoundBorder(true);
        expTv1.setText(getString(R.string.intensity_workout_details));
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

        if(prefs.getInt(GET_firebase_steps, -1)>=7500){
            trophy.setVisibility(View.VISIBLE);
            trophy.startAnimation(fortrophy);
            overbox.setAlpha(1);
            overbox.startAnimation(fromnothing);
            myPopup.setAlpha(1);
            myPopup.startAnimation(fromsmall);
            showpopupNoti.setText("You have completed 7500 steps today.");


        }
        if(prefs.getFloat(GET_firebase_moderatemins, -1)>=150){
            trophy.setVisibility(View.VISIBLE);
            trophy.startAnimation(fortrophy);
            overbox.setAlpha(1);
            overbox.startAnimation(fromnothing);
            myPopup.setAlpha(1);
            myPopup.startAnimation(fromsmall);
            showpopupNoti.setText("You have completed 150 mins of moderate exercise this week.");

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
        return v;
    }

    //for getDataRefOfStepsOfCompetitors to convert long to int
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
    private void getDataRefOfStepsOfCompetitors(){
        dataRefStepsFromCompetitors.child("Steps Count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                avrStepsFromCompetitors = new ArrayList<>();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        if(myDataSnapshot.hasChildren() && (myDataSnapshot.child(date).getValue()!=null)) {
                            avrStepsFromCompetitors.add(safeLongToInt((Long) myDataSnapshot.child(date +"/steps").getValue()));
                            stepsFromCompetitors.setText(String.format("%.1f", calculateAverageStepsOfCompetitors(avrStepsFromCompetitors)) + "/7500 steps");
                        }else{
                            //Toast.makeText(getContext(), "Error in getting participants steps", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "Error in getting participants steps", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showGraph() {
        graphView.setTitle("Heart Rate(BPM)");
        graphView.getViewport().setMinX(new Date().getTime()-1000000);
        graphView.getViewport().setMaxX(new Date().getTime());
        graphView.getViewport().setMinY(50);
        graphView.getViewport().setMaxY(170);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        lineGraphSeries.setDrawBackground(true);
        lineGraphSeries.setBackgroundColor(R.drawable.fade_red);
        lineGraphSeries.setColor(Color.BLACK);
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(15);
        lineGraphSeries.setThickness(10);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return simpleDateFormat.format(new Date((long) value));
                }else {
                    return super.formatLabel(value, isValueX);
                }

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
        String id = databaseReference.push().getKey();
        long x=new Date().getTime();
        int y=currentHeartRate;
        PointValue pointValue = new PointValue(x,y);
        databaseReference.child(id).setValue(pointValue);
        //retrieveData();
    }

    private void retrieveData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dataVals = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index =0;

                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
                        dataVals[index] = new DataPoint(pointValue.getxValue(),pointValue.getyValue());
                        index++;


                        avrHeartRate.add(pointValue.getyValue());
                        ratedMaxHR.setText(String.format("%.1f", calculateAverage(avrHeartRate)) + "BPM");
                    }
                    lineGraphSeries.resetData(dataVals);

                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void insertStepsData() {
        StepsPointValue pointSteps = new StepsPointValue(currentStepsCount);
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
                    stepsCount.setText(String.valueOf(stepsPointValue.getSteps()));
                    circularProgressBar.setProgressWithAnimation(Float.parseFloat(String.valueOf(stepsPointValue.getSteps()))); // =1s

                    if (!prefs.contains(GET_firebase_steps)) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(GET_firebase_steps, 0);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt(GET_firebase_steps, currentStepsCount);
                        edit.commit();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void insertLockInData() {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String cTime = format.format(currentTime.getTime());
        String id = lockinDataBaseRef.push().getKey();
        LockInValue lockInValue = new LockInValue(message,time,cTime);
        lockinDataBaseRef.child(id).setValue(lockInValue);
        RetrieveLockInData();
    }

    private void RetrieveLockInData() {
        lockinDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSet = new ArrayList<>();
                mTimeSet=new ArrayList<>();
                image = new ArrayList<>();
                currentTimeA = new ArrayList<>();
                activityAvrHeartRate = new ArrayList<>();
                mModerateMinsArray=new ArrayList<>();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        LockInValue lockInValue = myDataSnapshot.getValue(LockInValue.class);
                        mTimeSet.add(lockInValue.getDuration());
                        mDataSet.add(lockInValue.getActivity());
                        currentTimeA.add(lockInValue.getcTime());
                        //activityAvrHeartRate.add(lockInValue.getAvrHeartRate());
                        InsertRecyclerView();

                        if(lockInValue.getDuration()!=null) {
                            int duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
                            float mins = duration / 100;
                            float sec = duration % 100;
                            mModerateMinsArray.add(mins+(sec/60));
                            moderateMins.setText("Minutes of moderate exercise today: " + String.format("%.1f", calculateSumOfModerateMins(mModerateMinsArray)) + "mins");
                            insertWeeklyModerateMins();
                        }
                    }
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

    private void insertWeeklyModerateMins(){
        WeeklyReportPointValue weeksPointValue = new WeeklyReportPointValue(String.format("%.1f", calculateSumOfModerateMins(mModerateMinsArray)));
        weeklymoderateminsdataref.child(week).setValue(weeksPointValue);
        retrieveWeeklyModerateMins();
    }
    private void retrieveWeeklyModerateMins(){
        weeklymoderateminsdataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weeklyModerateMins=new ArrayList<>();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        WeeklyReportPointValue weeksModerateMinsPointValue = myDataSnapshot.getValue(WeeklyReportPointValue.class);
                        weeklyModerateMins.add(Float.valueOf(weeksModerateMinsPointValue.getModerateMins()));
                        WeeklyModerateMinsTV.setText("Sum of moderate exercises in the past week: " + String.format("%.1f", calculateSumOfWeeklyModerateMins(weeklyModerateMins)) + "mins");

                        if (!prefs.contains(GET_firebase_moderatemins)) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putFloat(GET_firebase_moderatemins, 0);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putFloat(GET_firebase_moderatemins, (float) calculateSumOfWeeklyModerateMins(weeklyModerateMins));
                            edit.commit();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void insertMaxHR() {
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
                        maxHeartrate.setText(maxHRPointValue.getHr() + "BPM");
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

                if (intent.getStringExtra("message") != null || intent.getStringExtra("timing") != null)
                        //|| intent.getStringExtra("activityTrackerHeartRate")!=null)
                {
                    if (intent.getStringExtra("timing") != null) {
                        time = intent.getStringExtra("timing");
                    }
                    else if ((intent.getStringExtra("message") != null) &&
                    ((intent.getStringExtra("message")!=prefs.getString("ActivityFromWear","null")))) {
                        message = intent.getStringExtra("message");
                        Log.v(TAG, "Main activity received message: " + message);
                        insertLockInData();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ActivityFromWear", message);
                        editor.commit();

                    }
                    //get heart rate from each activity
//                    if(intent.getStringExtra("activityTrackerHeartRate")!=null){
//                        activityTrackheartRate = intent.getStringExtra("activityTrackerHeartRate");
//                    }
                } else if (intent.getStringExtra("heartRate") != null ){
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

                    currentStepsCount = Integer.parseInt(steps);
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

        //calculate avr heart rate per day
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

        private double calculateSumOfWeeklyModerateMins(ArrayList<Float> weeklymoderatemins){
            double sum = 0;
            for(int i = 0; i < weeklymoderatemins.size(); i++)
                sum += weeklymoderatemins.get(i);
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
                Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_1_ID)
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
            if(currentHeartRate!=0) {
                if (currentHeartRate > MaxHeartRate) {
                    sendOnChannel1(null);

                }
            }
            else if(currentStepsCount!=0) {
                //insert different timings here for prompt of steps count
                if ((currentStepsCount < (7500 / 2)) && (currentTime.get(Calendar.HOUR_OF_DAY) == 12) && (currentTime.get(Calendar.MINUTE) == 00)) {
                    sendOnChannel2(null);

                }

                getRadioText();
                if ("4pm".equals(notiRadioText)) {
                    if ((currentTime.get(Calendar.HOUR_OF_DAY) == 16) && (currentTime.get(Calendar.MINUTE) == 00) && (currentStepsCount < 7500)) {
                        sendOnChannel3(null);

                    }
                } else if ("7pm".equals(notiRadioText)) {
                    if ((currentStepsCount < 7500) && (currentTime.get(Calendar.HOUR_OF_DAY) == 19) && (currentTime.get(Calendar.MINUTE) == 00)) {
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
            mAdapter = new CustomAdapter(mDataSet, mTimeSet, currentTimeA, image);
            mrecyclerView.setLayoutManager(mlayoutManager);
            mrecyclerView.setAdapter(mAdapter);
        }

         @Override
         public void onStart() {
        super.onStart();
        retrieveData();
        getDataRefOfStepsOfCompetitors();
    }
}

