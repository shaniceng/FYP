package com.example.fyp.Interface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fyp.MainActivity;
import com.example.fyp.R;
import com.example.fyp.StepsPointValue;
import com.example.fyp.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Button editProfile, logout;
    private TextView name, email, age, gender, height, weight, birthday, batt, radioButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String battery;
    private DatabaseReference stepsDataBaseRef;
    private int dataSteps, count=0;
    private SharedPreferences prefs;
    private static final String Current_check_key = "CheckCurrentStepsToFindBadge";
    private ImageView iv3days, iv1week, iv3weeks, iv1month, iv3months, iv6months;
    private int thisDay, lastDay, counterOfConsecutiveDays;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        name=v.findViewById(R.id.tvName);
        email=v.findViewById(R.id.tvEmail);
        age=v.findViewById(R.id.tvAge);
        gender=v.findViewById(R.id.tvGender);
        height=v.findViewById(R.id.tvHeight);
        weight=v.findViewById(R.id.tvWeight);
        birthday=v.findViewById(R.id.tvBirthday);
        batt=v.findViewById(R.id.tvBattery);
        radioButton=v.findViewById(R.id.tvRadioButton);

        iv3days=v.findViewById(R.id.iV3_days);
        iv1week=v.findViewById(R.id.iV1_week);
        iv3weeks=v.findViewById(R.id.iV_3_weeks);
        iv1month=v.findViewById(R.id.iV_1_month);
        iv3months=v.findViewById(R.id.iV3_months);
        iv6months=v.findViewById(R.id.iV6_months);

        logout=v.findViewById(R.id.btnLogout);
        firebaseAuth=FirebaseAuth.getInstance();
        editProfile = v.findViewById(R.id.btnEditProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        retrieveStepsCount();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Users/"+firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                name.setText("Hello, "+userProfile.getUserName());
                email.setText(userProfile.getUserEmail());
                age.setText(userProfile.getUserAge() + " years old");
                if(userProfile.getUserGender() ==  "F"){gender.setText("Female"); }
                else if(userProfile.getUserGender() ==  "M"){gender.setText("Male"); }
                else {gender.setText(userProfile.getUserGender());}
                height.setText( userProfile.getUserHeight() + " cm");
                weight.setText(userProfile.getUserWeight() +" kg");
                birthday.setText(userProfile.getUserBirthday());
                radioButton.setText(userProfile.getRadiotext());
                //age.setText(String.valueOf(prefs.getInt("YOUR COUNTER PREF KEY", 0)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        SettingsFragment.MessageReceiver messageReceiver = new SettingsFragment.MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);


        return v;
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("battPercentageLife") != null) {
                 battery = intent.getStringExtra("battPercentageLife");
                batt.setText(battery + "%");

            }
        }
    }

    public void retrieveStepsCount(){
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        stepsDataBaseRef=firebaseDatabase.getReference("Steps Count/" +currentuser + "/" + date );


        if (!prefs.contains("YOUR DATE PREF KEY")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("YOUR DATE PREF KEY", 0);
            editor.commit();
        }
        if (!prefs.contains("YOUR COUNTER PREF KEY")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("YOUR COUNTER PREF KEY", 0);
            editor.commit();
        }
        if (!prefs.contains("3DAY STREAK")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("3DAY STREAK", 0);
            editor.commit();
        }

        //SharedPreferences sharedPreferences = getContext().getSharedPreferences("YOUR PREF KEY", Context.MODE_PRIVATE);
        Calendar c = Calendar.getInstance();
        thisDay = c.get(Calendar.DAY_OF_YEAR); // GET THE CURRENT DAY OF THE YEAR
        lastDay = prefs.getInt("YOUR DATE PREF KEY", 0); //If we don't have a saved value, use 0.
        counterOfConsecutiveDays = prefs.getInt("YOUR COUNTER PREF KEY", 0); //If we don't have a saved value, use 0.

        // Initialize if it is the first time use

        stepsDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){
                    StepsPointValue stepsPointValue = dataSnapshot.getValue(StepsPointValue.class);
                    dataSteps = stepsPointValue.getSteps();


                if ((lastDay == thisDay -1) && (dataSteps >= 7500)) { //testing at 100 steps a day first must EDIT HERE
                    // CONSECUTIVE DAYS
                    //if today hit >7500, count up
                    counterOfConsecutiveDays = counterOfConsecutiveDays + 1;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("YOUR DATE PREF KEY", thisDay);
                    editor.putInt("YOUR COUNTER PREF KEY", counterOfConsecutiveDays);
                    editor.commit();

                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("YOUR DATE PREF KEY", thisDay);
                    editor.putInt("YOUR COUNTER PREF KEY", 0);
                    editor.commit();
                }



                if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 3 || prefs.getInt("3DAY STREAK", 0) ==1) {
                    //change greyscale to colour for 3days streak
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("3DAY STREAK", 1);
                    editor.commit();
                    iv3days.setImageResource(R.drawable.badges_day3_color);
                }
                if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 7 || prefs.getInt("1WEEK STREAK", 0) ==1) {
                    //change greyscale to colour for 7days streak
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("1WEEK STREAK", 1);
                    editor.commit();
                    iv1week.setImageResource(R.drawable.badges_week1_color);
                }
                if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 21 || prefs.getInt("3WEEK STREAK", 0) ==1) {
                    //change greyscale to colour for 3 weeks streak
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("3WEEK STREAK", 1);
                    editor.commit();
                    iv3weeks.setImageResource(R.drawable.badges_week3_color);
                }
                if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 30|| prefs.getInt("1MONTH STREAK", 0) ==1) {
                    //change greyscale to colour for 1month streak
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("1MONTH STREAK", 1);
                    editor.commit();
                    iv1month.setImageResource(R.drawable.badges_colour);
                }
                if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 90|| prefs.getInt("3MONTH STREAK", 0) ==1) {
                    //change greyscale to colour for 3month streak
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("3MONTH STREAK", 1);
                    editor.commit();
                    iv3months.setImageResource(R.drawable.badges_colour);
                }
                if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 180|| prefs.getInt("6MONTH STREAK", 0) ==1) {
                    //change greyscale to colour for 6month streak
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("6MONTH STREAK", 1);
                    editor.commit();
                    iv6months.setImageResource(R.drawable.badges_colour);
                }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
