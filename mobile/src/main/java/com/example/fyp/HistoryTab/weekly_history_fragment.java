package com.example.fyp.HistoryTab;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.HistoryActivityName;
import com.example.fyp.R;
import com.example.fyp.StepsAdapter;
import com.example.fyp.StepsValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class weekly_history_fragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private String selecteddate, selecteddateDay2,selecteddateDay3,selecteddateDay4,selecteddateDay5,selecteddateDay6,selecteddateDay7;
    private String steps,steps2,steps3,steps4,steps5,steps6,steps7;
    private int duration, totalduration,TotalHoursDuration,TotalMinutesDuration,TotalSecondsDuration,totalsteps;
    private float avgdurationmin,avgduration,avgweeksteps;
    private String currentuser;
    private String dataDate=null;
    private int maxday,maxmonth;

    private DatabaseReference DataBaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private TextView dateText,avgText,totalstepText,avgDText,totaldurationText;

    private ArrayList<StepsValue> stepsValue= new ArrayList<>();
    private ArrayList<String> activityname,activityduration;
    private List<HistoryActivityName> HistoryActivityNameList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.history_tab_weekly, container, false);
        dateText = v.findViewById(R.id.date_text);

        totalstepText = v.findViewById(R.id.tvTotalSteps);
        avgText=v.findViewById(R.id.tvAvgSteps);
        avgDText = v.findViewById(R.id.tvAvgDur);
        totaldurationText=v.findViewById(R.id.tvTotalDuration);


        mRecyclerView = v.findViewById(R.id.steps_value);

        v.findViewById(R.id.date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDailog();
            }
        });
        return v;
    }

    private void showDatePickerDailog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month = month + 1;

        if (month == 1||month == 3||month == 5||month == 7||month == 8||month == 10||month == 12){ maxday=31+1; }
        else if (month == 2){
            if(((year%4==0) && (year%100!=0) || (year%400==0))) { maxday = 29+1; }
            else { maxday = 28 + 1; }
        }
        else{ maxday = 30+1; }
        maxmonth = 12+1;

        dataDate ="Week selected: " +String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year;
        //final String datetxt2 = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year;

        selecteddate = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay2 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay3 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay4 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay5 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay6 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay7 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);;

        dateText.setText(dataDate);

        if (dataDate != null) {

            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            stepsValue = new ArrayList<>();
            HistoryActivityNameList= new ArrayList<>();

            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            DataBaseRef = FirebaseDatabase.getInstance().getReference();
            DataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    activityname = new ArrayList<>();
                    activityduration= new ArrayList<>();

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddate).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddate).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddate).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps, selecteddate,buildHistoryActivityName(activityname,activityduration)));





                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay2).hasChild("steps")) {
                        steps2 = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay2).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps2); }
                    else { steps2 = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddateDay2).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps2, selecteddateDay2,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay3).hasChild("steps")) {
                        steps3 = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay3).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps3); }
                    else { steps3 = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddateDay3).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps3, selecteddateDay3,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay4).hasChild("steps")) {
                        steps4 = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay4).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps4); }
                    else { steps4 = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddateDay4).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps4, selecteddateDay4,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay5).hasChild("steps")) {
                        steps5 = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay5).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps5); }
                    else { steps5 = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddateDay5).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps5, selecteddateDay5,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay6).hasChild("steps")) {
                        steps6 = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay6).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps6); }
                    else { steps6 = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddateDay6).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps6, selecteddateDay6,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay7).hasChild("steps")) {
                        steps7 = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddateDay7).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps7); }
                    else { steps7 = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddateDay7).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        duration =Integer. valueOf( getSecondsFromDurationString(durdaily));
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps7, selecteddateDay7,buildHistoryActivityName(activityname,activityduration)));


                    totalstepText.setText("Total steps: " +totalsteps);
                    avgweeksteps = (float)totalsteps/7;
                    avgText.setText("Average steps: "+String.format("%.2f", avgweeksteps) + " steps/day");

                    TotalHoursDuration = totalduration / 3600;
                    if (TotalHoursDuration>1 || TotalHoursDuration==1){
                        TotalMinutesDuration = (totalduration % 3600) / 60;
                        TotalSecondsDuration = totalduration % 60;
                        Log.i("test","test");
                        totaldurationText.setText("Total duration: "+TotalHoursDuration+ ":" + String.format("%02d", TotalMinutesDuration) + ":" + String.format("%02d", TotalSecondsDuration)  + " (hh:mm:ss)");
                    }
                    else {
                        TotalMinutesDuration = (totalduration % 3600) / 60;
                        TotalSecondsDuration = totalduration % 60;
                        totaldurationText.setText("Total duration: " + TotalMinutesDuration + ":" + TotalSecondsDuration+ " (mm:ss)");
                    }

                    avgduration = totalduration / 7;
                    avgdurationmin = avgduration/60;
                    avgDText.setText("Average duration: "+String.format("%.2f", avgdurationmin)+"minutes/day");

                    totalsteps=0;
                    avgweeksteps=0;
                    totalduration=0;
                    avgduration=0;


                    mAdapter = new StepsAdapter(stepsValue);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setHasFixedSize(true);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            mAdapter = new StepsAdapter(stepsValue);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);

        }
    }

    private List<HistoryActivityName> buildHistoryActivityName(ArrayList<String> mactivityname,ArrayList<String> mactivityduration) {
        HistoryActivityNameList = new ArrayList<>();

        for (int i = 0; i < mactivityname.size(); i++) {
            Log.i("ValueDCTT", mactivityname.get(i)+","+mactivityduration.get(i) + "position"+i);
            HistoryActivityNameList.add(new HistoryActivityName( mactivityname.get(i), mactivityduration.get(i)));

        }

        activityname.clear();
        activityduration.clear();

        return HistoryActivityNameList;

    }

    public weekly_history_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public String getSecondsFromDurationString(String value) {

        String[] parts = value.split(":");

        // Wrong format, no value for you.
        if (parts.length < 2 || parts.length > 3)
            return null;

        int seconds = 0, minutes = 0, hours = 0;

        if (parts.length == 2) {
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        } else if (parts.length == 3) {
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[0]);
        }

        seconds = seconds + (minutes * 60) + (hours * 3600);
        return String.valueOf(seconds);
    }
}
