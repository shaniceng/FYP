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


public class daily_history_fragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private String currentdate,selecteddate,currentuser;
    private String steps;
    private DatabaseReference stepsDataBaseRef,actDataBaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView mRecyclerView,nRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager, nLayoutManager;
    private RecyclerView.Adapter mAdapter, nAdapter;
    //private ArrayList<String> mDate;
    //private ArrayList<String> mSteps = new ArrayList();
    private String dataDate=null;
    private ArrayList<StepsValue> stepsValue= new ArrayList<>();
    private ArrayList<HistoryActivityName> HistoryAct= new ArrayList<>();
    private TextView dateText,actText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.history_tab_daily, container, false);
        dateText = v.findViewById(R.id.date_text);
        //dataText = findViewById(R.id.data_text);
        actText = v.findViewById(R.id.tvAct);
        mRecyclerView = v.findViewById(R.id.steps_value);
        //nRecyclerView=v.findViewById(R.id.History_Activity);

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
        //HistoryList.clear();
        //HistoryAdapter.notifyDataSetChanged();


        if (dataDate != null) {


            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            currentdate = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]", "");
            currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //stepsDataBaseRef = firebaseDatabase.getReference("Steps Count/" + currentuser + date);


            stepsValue = new ArrayList<>();
            HistoryAct= new ArrayList<>();
            mLayoutManager = new LinearLayoutManager(getActivity());
           // nLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            //nRecyclerView.setLayoutManager(nLayoutManager);


            stepsDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Steps Count").child(currentuser).child(selecteddate);
            stepsDataBaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("steps")) {
                        steps = dataSnapshot.child("steps").getValue().toString();

                        //Toast.makeText(getActivity(), steps, Toast.LENGTH_SHORT).show();
                        // stepsValue.add(new StepsValue(steps, selecteddate));
                        //StepsAdapter.notifyDataSetChanged();
                        stepsValue.add(new StepsValue(steps, datetxt2));


                    } else {
                        steps = "0";
                        stepsValue.add(new StepsValue(steps, datetxt2));
                    }
                    //stepsValue.add(new StepsValue(steps, datetxt2));
                    mAdapter = new StepsAdapter(stepsValue);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setHasFixedSize(true);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            actDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Activity Tracker").child(currentuser).child(selecteddate);
            actDataBaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        String actdaily = String.valueOf(myDataSnapshot.child("activity").getValue());
                        String durdaily = String.valueOf(myDataSnapshot.child("cDuration").getValue());

                        //HistoryAct.add(new HistoryActivityName(actdaily, durdaily));
                        actText.setText(actdaily);
                        Log.i("ValueACTT", actdaily);

                    }

                    //nAdapter = new HistoryActivityAdapter(HistoryAct);
                    //nRecyclerView.setAdapter(nAdapter);
                    //nRecyclerView.setHasFixedSize(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //stepsDataBaseRef = firebaseDatabase.getReference("Steps Count/" + "NDnXWC4MHfYFBaayGPcJ2SghYVF2");
        /*Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String cTime = format.format(currentTime.getTime());
        String id = stepsDataBaseRef.push().getKey();
        StepsValue stepsvalue = new StepsValue(steps);
       stepsDataBaseRef.child(id).setValue(stepsvalue);
        //mSteps = new ArrayList<>();
        */

            mAdapter = new StepsAdapter(stepsValue);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);

            //nAdapter = new HistoryActivityAdapter(HistoryAct);
            //nRecyclerView.setAdapter(nAdapter);
            //nRecyclerView.setHasFixedSize(true);


        }
    }

    public daily_history_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
