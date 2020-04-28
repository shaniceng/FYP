package com.example.fyp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.fyp.App.CHANNEL_1_ID;



/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private static final String TAG = "LineChartActivity";
    private LineChart mChart;

    private TextView stepsCount, HeartRate, maxHeartrate, ratedMaxHR;
    protected Handler handler;
    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<Entry> yValues;
    private String time;
    private String message, steps, heart, max_HeartRate;
    private CircularProgressBar circularProgressBar;
    private NotificationManagerCompat notificationManager;
    private int currentHeartRate, MaxHeartRate;
    UserProfile userProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private YAxis leftAxis;
    private String timeDisplay;



    FloatingActionButton fab;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        yValues= new ArrayList<>();

        mrecyclerView = v.findViewById(R.id.activity_RV);
        stepsCount=v.findViewById(R.id.tvStepsCount);
        HeartRate=v.findViewById(R.id.tvResting_value);
        maxHeartrate=v.findViewById(R.id.tvMAX_value);
        ratedMaxHR=v.findViewById(R.id.tvAvgResting_value);
        circularProgressBar = v.findViewById(R.id.circularProgressBar);

        mChart=v.findViewById(R.id.lineChart);

        //get Max heart rate for each individual age
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                MaxHeartRate= 220 - Integer.parseInt(userProfile.getUserAge().replaceAll("[\\D]",""));
                ratedMaxHR.setText(String.valueOf(MaxHeartRate));
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

        /*//message handler for the send thread.
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                //logthis(stuff.getString("logthis"));
                return true;
            }
        });*/

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        mDataSet = new ArrayList<>();
        mTimeSet=new ArrayList<>();
        circularProgressBar.setProgressMax(7500);

        notificationManager = NotificationManagerCompat.from(getActivity());
        Refresh();
        DisplayChart();
        return v;
    }


    //setup a broadcast receiver to receive the messages from the wear device via the listenerService.
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getStringExtra("timing")==null){
                message = intent.getStringExtra("message");
                Log.v(TAG, "Main activity received message: " + message);

            }
            else if(intent.getStringExtra("message")==null) {
               time  = intent.getStringExtra("timing");

                mTimeSet.add(time);
                mDataSet.add(message);
                mlayoutManager=new LinearLayoutManager(getContext());
                mrecyclerView.setHasFixedSize(true);
                mAdapter = new CustomAdapter(mDataSet,mTimeSet);
                mrecyclerView.setLayoutManager(mlayoutManager);
                mrecyclerView.setAdapter(mAdapter);


            }
            if(intent.getStringExtra("heartRate")!=null){
                heart = intent.getStringExtra("heartRate");
                Log.v(TAG, "Main activity received message: " + message);
                HeartRate.setText(heart);
                currentHeartRate=Integer.parseInt(heart.replaceAll("[\\D]",""));
                ratedMaxHR.setText(String.valueOf(currentHeartRate));

                yValues.add(new Entry(0,currentHeartRate));


            }
            else if(intent.getStringExtra("countSteps")!=null){
                steps = intent.getStringExtra("countSteps");
                Log.v(TAG, "Main activity received message: " + message);
                stepsCount.setText(steps);
                circularProgressBar.setProgressWithAnimation(Float.parseFloat(steps)); // =1s

            }
            else if(intent.getStringExtra("maxHeartRate") !=null){
                max_HeartRate = intent.getStringExtra("maxHeartRate");
                maxHeartrate.setText(max_HeartRate);
            }
        }
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

    public void Refresh(){
        if (currentHeartRate>MaxHeartRate) {
            sendOnChannel1(null);
        }
        runnable(60000);
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


    public void DisplayChart(){
        //Display chart
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);

        //limit line
        LimitLine upper_limit = new LimitLine((float)MaxHeartRate, "Danger");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f,10f,0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(15f);

        //add fake data
        yValues.add(new Entry(1,90f));
        yValues.add(new Entry(2,70f));
        yValues.add(new Entry(3,80f));
        yValues.add(new Entry(4,100f));
        yValues.add(new Entry(5,60f));
        yValues.add(new Entry(6,65f));

        //display fake data
        LineDataSet set1 = new LineDataSet(yValues,"Heart Rate");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        set1.setValueTextSize(20f);
        set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mChart.getAxisLeft().getAxisMinimum();
            }
        });
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
        set1.setFillDrawable(drawable);

        //display x-axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(0.1f,100f,0);
        xAxis.setAvoidFirstLastClipping(true);
        //xAxis.setValueFormatter(new XAxisValueFormatter());

        //display y-axis
            //y-left-axis
        leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.setAxisMaximum(130f);
        leftAxis.setAxisMinimum(40f);
        leftAxis.enableGridDashedLine(10f,10f, 0);
        leftAxis.setDrawLimitLinesBehindData(true);

            //y-rightaxis
        mChart.getAxisRight().setEnabled(false);


        //display chart
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        mChart.setData(data);

        //getLegend
        Legend l =mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }
    
}
