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


public class monthly_history_fragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private String daymonth01, daymonth02,daymonth03,daymonth04,daymonth05,daymonth06,daymonth07,daymonth08,daymonth09,daymonth10,daymonth11,daymonth12,daymonth13,daymonth14,daymonth15,daymonth16,daymonth17,daymonth18,
            daymonth19,daymonth20,daymonth21,daymonth22,daymonth23,daymonth24,daymonth25,daymonth26,daymonth27,daymonth28,daymonth29,daymonth30,daymonth31;
    private String steps;
    private String currentuser,avgWeekSteps;
    private String dataDate=null;
    private int maxday,maxmonth;

    private int duration, totalduration,TotalHoursDuration,TotalMinutesDuration,TotalSecondsDuration,totalsteps;
    private float avgdurationmin,avgduration,avgweeksteps;

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
        View v= inflater.inflate(R.layout.history_tab_monthly, container, false);
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
        dayOfMonth=1;
        if (month == 1||month == 3||month == 5||month == 7||month == 8||month == 10||month == 12){ maxday=31+1; }
        else if (month == 2){
            if(((year%4==0) && (year%100!=0) || (year%400==0))) { maxday = 29+1; }
            else { maxday = 28 + 1; }
        }
        else{ maxday = 30+1; }
        maxmonth = 12+1;
        String startdate = "01"+ String.format("%02d", month) + year;
        String enddate = maxday+String.format("%02d", month) + year;

        dataDate ="Month selected: " + String.format("%02d", month) + "/" + year;

        daymonth01 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth02 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth03 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth04 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth05 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth06 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth07 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth08 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth09 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth10 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth11 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth12 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth13 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth14 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth15 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth16 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth17 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth18 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth19 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth20 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth21 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth22 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth23 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth24 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth25 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth26 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth27 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth28 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        if ( dayOfMonth != maxday) {
            daymonth29 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
            dayOfMonth = dayOfMonth + 1;
            if ( dayOfMonth != maxday) {
                daymonth30 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
                dayOfMonth = dayOfMonth + 1;
                if ( dayOfMonth != maxday) {
                    daymonth31 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
                }
                else { daymonth31 = "0";}
            }
            else { daymonth30 = "0";}
        }
        else { daymonth29 = "0";}

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


                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth01).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth01).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth01).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth01,buildHistoryActivityName(activityname,activityduration)));





                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth02).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth02).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else { steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth02).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps, daymonth02,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth03).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth03).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else { steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth03).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps, daymonth03,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth04).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth04).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else { steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth04).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps, daymonth04,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth05).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth05).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else { steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth05).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps, daymonth05,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth06).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth06).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else { steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth06).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;


                    }
                    stepsValue.add(new StepsValue(steps, daymonth06,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth07).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth07).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else { steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth07).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;

                    }
                    stepsValue.add(new StepsValue(steps, daymonth07,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth08).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth08).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth08).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth08,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth09).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth09).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth09).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth09,buildHistoryActivityName(activityname,activityduration)));



                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth10).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth10).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth10).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth10,buildHistoryActivityName(activityname,activityduration)));


                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth11).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth11).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth11).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth11,buildHistoryActivityName(activityname,activityduration)));


                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth12).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth12).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth12).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth12,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth13).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth13).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth13).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth13,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth14).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth14).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth14).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth14,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth15).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth15).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth15).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth15,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth16).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth16).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth16).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth16,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth17).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth17).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth17).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth17,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth18).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth18).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth18).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth18,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth19).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth19).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth19).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth19,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth20).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth20).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth20).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth20,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth21).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth21).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth21).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth21,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth22).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth22).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth22).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth22,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth23).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth23).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth23).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth23,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth24).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth24).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth24).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth24,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth25).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth25).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth25).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth25,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth26).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth26).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth26).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth26,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth27).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth27).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth27).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth27,buildHistoryActivityName(activityname,activityduration)));

                    if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth28).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth28).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps); }
                    else {steps = "0"; }
                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth28).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);
                        if(getSecondsFromDurationString(durdaily)!=null) {
                            duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                        }
                        totalduration = totalduration + duration;
                    }
                    stepsValue.add(new StepsValue(steps, daymonth28,buildHistoryActivityName(activityname,activityduration)));

                    if(daymonth29!= "0") {
                        if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth29).hasChild("steps")) {
                            steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth29).child("steps").getValue().toString();
                            totalsteps = totalsteps + Integer.valueOf(steps);
                        } else {
                            steps = "0";
                        }
                        for (DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth29).getChildren()) {

                            String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                            String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                            activityname.add(actdaily);
                            activityduration.add(durdaily);
                            if(getSecondsFromDurationString(durdaily)!=null) {
                                duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                            }
                            totalduration = totalduration + duration;
                        }
                        stepsValue.add(new StepsValue(steps, daymonth29, buildHistoryActivityName(activityname, activityduration)));
                    }

                    if(daymonth30!= "0") {
                        if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth30).hasChild("steps")) {
                            steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth30).child("steps").getValue().toString();
                            totalsteps = totalsteps + Integer.valueOf(steps);
                        } else {
                            steps = "0";
                        }
                        for (DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth30).getChildren()) {

                            String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                            String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                            activityname.add(actdaily);
                            activityduration.add(durdaily);
                            if(getSecondsFromDurationString(durdaily)!=null) {
                                duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                            }
                            totalduration = totalduration + duration;
                        }
                        stepsValue.add(new StepsValue(steps, daymonth30, buildHistoryActivityName(activityname, activityduration)));
                    }

                    if(daymonth31!= "0") {
                        if (dataSnapshot.child("Steps Count").child(currentuser).child(daymonth31).hasChild("steps")) {
                            steps = dataSnapshot.child("Steps Count").child(currentuser).child(daymonth31).child("steps").getValue().toString();
                            totalsteps = totalsteps + Integer.valueOf(steps);
                        } else {
                            steps = "0";
                        }
                        for (DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(daymonth31).getChildren()) {

                            String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                            String durdaily = String.valueOf(myDataSnapshot.child("duration").getValue());
                            activityname.add(actdaily);
                            activityduration.add(durdaily);
                            if(getSecondsFromDurationString(durdaily)!=null) {
                                duration = Integer.valueOf(getSecondsFromDurationString(durdaily));
                            }
                            totalduration = totalduration + duration;

                        }
                        stepsValue.add(new StepsValue(steps, daymonth31, buildHistoryActivityName(activityname, activityduration)));
                    }



                    totalstepText.setText("Total steps: " +totalsteps);
                    avgweeksteps = (float)totalsteps/(maxday-1)*7;
                    avgText.setText("Average steps: "+String.format("%.2f", avgweeksteps) + " steps/week");

                    TotalHoursDuration = totalduration / 3600;
                    if (TotalHoursDuration>1 || TotalHoursDuration==1){
                        TotalMinutesDuration = (totalduration % 3600) / 60;
                        TotalSecondsDuration = totalduration % 60;
                        totaldurationText.setText("Total duration: "+ TotalHoursDuration+ ":" + String.format("%02d", TotalMinutesDuration) + ":" + String.format("%02d", TotalSecondsDuration)  + " (hh:mm:ss)");
                    }
                    else {
                        TotalMinutesDuration = (totalduration % 3600) / 60;
                        TotalSecondsDuration = totalduration % 60;
                        totaldurationText.setText("Total duration: " + TotalMinutesDuration + ":" + String.format("%02d", TotalSecondsDuration) + " (mm:ss)");
                    }

                    avgduration = (totalduration / (maxday-1))*7;
                    avgdurationmin = avgduration/60;
                    avgDText.setText("Average duration: "+String.format("%.2f", avgdurationmin)+"minutes/week");

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

    public monthly_history_fragment() {
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
