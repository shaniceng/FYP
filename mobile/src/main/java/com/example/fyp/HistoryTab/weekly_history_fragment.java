package com.example.fyp.HistoryTab;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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


public class weekly_history_fragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private String selecteddate, selecteddateDay2,selecteddateDay3,selecteddateDay4,selecteddateDay5,selecteddateDay6;
    private String steps,steps2,steps3,steps4,steps5,steps6;
    private String currentuser;
    private DatabaseReference stepsDataBaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String dataDate=null;
    private ArrayList<StepsValue> stepsValue= new ArrayList<>();
    private TextView dateText,avgText;
    private int maxday,maxmonth,totalsteps;
    private float avgweeksteps;
    String avgWeekSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.history_tab_weekly, container, false);
        dateText = v.findViewById(R.id.date_text);
        avgText=v.findViewById(R.id.tvAvgSteps);
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

        selecteddate = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay2 = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay3 = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay4 = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay5 = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;
        dayOfMonth = dayOfMonth+1;
        if (dayOfMonth==maxday) {
            dayOfMonth=1;
            month=month+1;
            if(month == maxmonth){
                month=1;
                year = year+1;
            }
        }
        selecteddateDay6 = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;

        dateText.setText(dataDate);

        if (dataDate != null) {

            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            stepsValue = new ArrayList<>();
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            stepsDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Steps Count").child(currentuser);
            stepsDataBaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(selecteddate).hasChild("steps")) {
                        steps = dataSnapshot.child(selecteddate).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps);
                        stepsValue.add(new StepsValue(steps, selecteddate));
                    }
                    else {
                        steps = "0";
                        stepsValue.add(new StepsValue(steps, selecteddate));
                    }
                    if (dataSnapshot.child(selecteddateDay2).hasChild("steps")) {
                        steps2 = dataSnapshot.child(selecteddateDay2).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps2);
                        stepsValue.add(new StepsValue(steps2, selecteddateDay2));
                    }
                    else {
                        steps2 = "0";
                        stepsValue.add(new StepsValue(steps2, selecteddateDay2));
                    }
                    if (dataSnapshot.child(selecteddateDay3).hasChild("steps")) {
                        steps3 = dataSnapshot.child(selecteddateDay3).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps3);
                        stepsValue.add(new StepsValue(steps3, selecteddateDay3));
                    }
                    else {
                        steps3 = "0";
                        stepsValue.add(new StepsValue(steps3, selecteddateDay3));
                    }
                    if (dataSnapshot.child(selecteddateDay4).hasChild("steps")) {
                        steps4 = dataSnapshot.child(selecteddateDay4).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps4);
                        stepsValue.add(new StepsValue(steps4, selecteddateDay4));
                    }
                    else {
                        steps4 = "0";
                        stepsValue.add(new StepsValue(steps4, selecteddateDay4));
                    }
                    if (dataSnapshot.child(selecteddateDay5).hasChild("steps")) {
                        steps5 = dataSnapshot.child(selecteddateDay5).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps5);
                        stepsValue.add(new StepsValue(steps5, selecteddateDay5));
                    }
                    else {
                        steps5 = "0";
                        stepsValue.add(new StepsValue(steps5, selecteddateDay5));
                    }
                    if (dataSnapshot.child(selecteddateDay6).hasChild("steps")) {
                        steps6 = dataSnapshot.child(selecteddateDay6).child("steps").getValue().toString();
                        totalsteps = totalsteps+ Integer. valueOf(steps6);
                        stepsValue.add(new StepsValue(steps6, selecteddateDay6));
                    }
                    else {
                        steps6 = "0";
                        stepsValue.add(new StepsValue(steps6, selecteddateDay6));
                    }

                    avgweeksteps = totalsteps/7;
                    avgWeekSteps = String.format("%.2f", avgweeksteps);
                    avgText.setText("Average steps in selected week: "+avgWeekSteps + " steps");

                    totalsteps=0;
                    avgweeksteps=0;
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

    public weekly_history_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
