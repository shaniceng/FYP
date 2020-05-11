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

import com.example.fyp.HistoryActivityAdapter;
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


public class daily_history_fragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private String currentdate,selecteddate,currentuser,steps;
    private String dataDate=null;

    private DatabaseReference DataBaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<String> activityname,activityduration;
    private ArrayList<StepsValue> stepsValue= new ArrayList<>();
    private List<HistoryActivityName> HistoryActivityNameList = new ArrayList<>();

    private TextView dateText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.history_tab_daily, container, false);
        dateText = v.findViewById(R.id.date_text);
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
        dataDate = String.format("%02d", dayOfMonth) + " " + String.format("%02d", month) + "," + year;
        final String datetxt = "Date selected: " + String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year;
        final String datetxt2 = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year;
        selecteddate = String.format("%02d", dayOfMonth) + String.format("%02d", month) + year;

        dateText.setText(datetxt);


        if (dataDate != null) {

            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            currentdate = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]", "");
            currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            stepsValue = new ArrayList<>();
            HistoryActivityNameList= new ArrayList<>();

            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);


            DataBaseRef=FirebaseDatabase.getInstance().getReference();
            DataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    activityname = new ArrayList<>();
                    activityduration= new ArrayList<>();

                    if(dataSnapshot.child("Steps Count").child(currentuser).child(selecteddate).hasChild("steps")){
                        steps = dataSnapshot.child("Steps Count").child(currentuser).child(selecteddate).child("steps").getValue().toString(); }
                    else {
                        steps = "0"; }

                    for(DataSnapshot myDataSnapshot : dataSnapshot.child("Activity Tracker").child(currentuser).child(selecteddate).getChildren()){

                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("cDuration").getValue());
                        activityname.add(actdaily);
                        activityduration.add(durdaily);

                    }
                    stepsValue.add(new StepsValue(steps, datetxt2,buildHistoryActivityName(activityname,activityduration)));

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

        for (int i = 0; i < mactivityname.size(); i++) {
            //Log.i("ValueDCTT", mactivityname.get(i)+","+mactivityduration.get(i));
            HistoryActivityNameList.add(new HistoryActivityName( mactivityname.get(i), mactivityduration.get(i)));
        }

        return HistoryActivityNameList;
    }

    public daily_history_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
