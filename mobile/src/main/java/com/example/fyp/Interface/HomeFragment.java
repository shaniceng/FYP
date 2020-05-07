package com.example.fyp.Interface;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.CustomAdapter;
import com.example.fyp.Interface.ExerciseFragment;
import com.example.fyp.LockInValue;
import com.example.fyp.MainActivity;
import com.example.fyp.MaxHRPointValue;
import com.example.fyp.PointValue;
import com.example.fyp.PopUpActivity;
import com.example.fyp.R;
import com.example.fyp.StepsPointValue;
import com.example.fyp.UserProfile;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.fyp.App.CHANNEL_1_ID;



/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private static final String TAG = "LineChartActivity";
    private TextView stepsCount, HeartRate, maxHeartrate, ratedMaxHR, stepsFromCompetitors;
    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<String> currentTimeA;
    private ArrayList<String> activityAvrHeartRate;
    private ArrayList<Integer> image;
    private ArrayList <Integer> avrHeartRate = new ArrayList();
    private ArrayList <Integer> sumOf = new ArrayList();
    private ArrayList<Integer> avrStepsFromCompetitors;
    private ArrayList<Entry> yValues;
    private String time;
    private String message, steps, heart, max_HeartRate, notiRadioText, activityTrackheartRate;
    private CircularProgressBar circularProgressBar;
    private NotificationManagerCompat notificationManager;
    private int currentHeartRate, MaxHeartRate, currentStepsCount, databaseHeart;

    private static final String GET_firebase_steps = "firebaseStepsCount";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, stepsDataBaseRef, lockinDataBaseRef, maxHRDataref, dataRefStepsFromCompetitors;
    private LineChart lineChart;
    private LineDataSet lineDataSet = new LineDataSet(null, null);
   private ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    private LineData lineData ;
    private YAxis leftAxis;

    private SharedPreferences prefs;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
    private GraphView graphView;
    private LineGraphSeries lineGraphSeries;


    private String date;

    FloatingActionButton fab;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

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

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        circularProgressBar.setProgressMax(7500);

        notificationManager = NotificationManagerCompat.from(getActivity());
        getRadioText();
        Refresh();


        //lineChart=v.findViewById(R.id.lineChart);
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        databaseReference = firebaseDatabase.getReference("Chart Values/" + currentuser +"/" + date);
        stepsDataBaseRef=firebaseDatabase.getReference("Steps Count/" +currentuser + "/" + date );
        lockinDataBaseRef = firebaseDatabase.getReference("Activity Tracker/" +currentuser + "/" + date );
        maxHRDataref = firebaseDatabase.getReference("MaxHeartRate/" +currentuser + "/" + date );
        dataRefStepsFromCompetitors = firebaseDatabase.getReference();

        getDataRefOfStepsOfCompetitors();
        retrieveStepsData();
        retrieveData();
        RetrieveLockInData();
        showGraph();


        //get Max heart rate for each individual age
        DatabaseReference mydatabaseRef = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid());
        mydatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                MaxHeartRate= 220 - Integer.parseInt(userProfile.getUserAge().replaceAll("[\\D]",""));
                //ratedMaxHR.setText(String.valueOf(MaxHeartRate));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        ExpandableTextView expTv1 = v.findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);
        circularProgressBar.setRoundBorder(true);
        expTv1.setText(getString(R.string.intensity_workout_details));
        fab = (FloatingActionButton) v.findViewById(R.id.btnAddActivity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ExerciseFragment.class));
            }
        });

        //display pop up whenever over 7500 steps
        if(prefs.getInt(GET_firebase_steps, -1)>=7500){
            startActivity(new Intent(getActivity(), PopUpActivity.class));
        }

        //retrieveMaxHR();
        return v;
    }
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
                        if(myDataSnapshot.hasChildren()) {
                            avrStepsFromCompetitors.add(safeLongToInt((Long) myDataSnapshot.child(date +"/steps").getValue()));
                            //String a = messageSnapshot.child("steps").getValue().toString();
                            //stepsFromCompetitors.setText(a);
                            stepsFromCompetitors.setText(String.format("%.1f", calculateAverageStepsOfCompetitors(avrStepsFromCompetitors)) + "/7500 steps");
                        }else{
                            Toast.makeText(getActivity(), "Error in getting participants steps", Toast.LENGTH_SHORT).show();
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

        graphView.getViewport().setMinX(100000);
        graphView.getViewport().setMaxX(500000);
        graphView.getViewport().setMinY(70);
        graphView.getViewport().setMaxY(150);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        lineGraphSeries.setDrawBackground(true);
        lineGraphSeries.setBackgroundColor(R.drawable.fade_red);
        lineGraphSeries.setColor(Color.BLACK);
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(10);
        lineGraphSeries.setThickness(8);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);
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
                Toast.makeText(getActivity(), "Heart Rate: "+dataPoint.getY() + "BPM", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void insertData() {
        String id = databaseReference.push().getKey();
        long x=new Date().getTime();
        int y=currentHeartRate;
        PointValue pointValue = new PointValue(x,y);
        databaseReference.child(id).setValue(pointValue);
        retrieveData();
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveData();
        getDataRefOfStepsOfCompetitors();
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
                                //.index(new Entry(pointValue.getxValue(), pointValue.getyValue()));
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
        //String id = stepsDataBaseRef.push().getKey();
        StepsPointValue pointSteps = new StepsPointValue(currentStepsCount);
        stepsDataBaseRef.setValue(pointSteps); //.child(id)

        retrieveStepsData();
    }

    private void retrieveStepsData() {
            stepsDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if(dataSnapshot.hasChildren()){
                    //for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
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
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        LockInValue lockInValue = myDataSnapshot.getValue(LockInValue.class);
                        mTimeSet.add(lockInValue.getDuration());
                        mDataSet.add(lockInValue.getActivity());
                        currentTimeA.add(lockInValue.getcTime());
                        //activityAvrHeartRate.add(lockInValue.getAvrHeartRate());
                        InsertRecyclerView();
                    }
                }else{
                    Toast.makeText(getActivity(),"No activity to retrieve", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error in retrieving activity", Toast.LENGTH_SHORT).show();
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
                MaxHRPointValue maxHRPointValue = dataSnapshot.getValue(MaxHRPointValue.class);

                if(maxHRPointValue.getHr()!=0) {
                    databaseHeart = maxHRPointValue.getHr();
                    maxHeartrate.setText(String.valueOf(maxHRPointValue.getHr()) + "BPM");
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

                if (intent.getStringExtra("message") != null || intent.getStringExtra("timing") != null
                        || intent.getStringExtra("activityTrackerHeartRate")!=null)
                {
                    if (intent.getStringExtra("message") != null) {
                        message = intent.getStringExtra("message");
                        Log.v(TAG, "Main activity received message: " + message);
                        insertLockInData();
                    }
                    else if (intent.getStringExtra("timing") != null) {
                        time = intent.getStringExtra("timing");
                    }
//                    if(intent.getStringExtra("activityTrackerHeartRate")!=null){
//                        activityTrackheartRate = intent.getStringExtra("activityTrackerHeartRate");
//                    }

                } else if (intent.getStringExtra("heartRate") != null) {
                    heart = intent.getStringExtra("heartRate");
                    Log.v(TAG, "Main activity received message: " + message);
                    HeartRate.setText(heart);
                    currentHeartRate = Integer.parseInt(heart.replaceAll("[\\D]", ""));
                    insertData();
                    avrHeartRate.add(currentHeartRate);
                    ratedMaxHR.setText(String.format("%.1f", calculateAverage(avrHeartRate)) + "BPM");
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

//
//        //calculate sum of moderate activity in a week (NOT DONE)
//        private double calculateSumofModerateActivity(List<Integer> avrHeartRate) {
//            Integer sum = 0;
//            if (!avrHeartRate.isEmpty()) {
//                for (Integer avrHR : avrHeartRate) {
//                    sum += avrHR;
//                }
//                return sum.doubleValue() / avrHeartRate.size();
//            }
//            return sum;
//        }

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

            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_message)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            notificationManager.notify(1, notification);
        }

        public void sendOnChannel2(View v) {
            String title = "Alert!!!";
            String message = "You have not reached half of the minimum steps today!\n Please exercise!";

            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
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

            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
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
            if (currentHeartRate > databaseHeart) {
                sendOnChannel1(null);

            }

            //insert different timings here for prompt of steps count
            if ((currentStepsCount < (7500 / 2)) && (currentTime.get(Calendar.HOUR_OF_DAY) == 12) && (currentTime.get(Calendar.MINUTE) == 00)) {
                sendOnChannel2(null);

            }

            getRadioText();
            //tv5.setText(notiRadioText);
            //sendOnChannel3(null);
            if ("4pm".equals(notiRadioText)) {
                if ((currentTime.get(Calendar.HOUR_OF_DAY) == 16) && (currentTime.get(Calendar.MINUTE) == 00) && (currentStepsCount < 7500)) {
                    sendOnChannel3(null);

                }
            } else if ("7pm".equals(notiRadioText)) {
                if ((currentStepsCount < 7500) && (currentTime.get(Calendar.HOUR_OF_DAY) == 19) && (currentTime.get(Calendar.MINUTE) == 00)) {
                    sendOnChannel3(null);

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

    }

