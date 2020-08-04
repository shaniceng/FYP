package com.example.fyp.Interface;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
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
import androidx.fragment.app.FragmentTransaction;
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
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Button editProfile, logout,engLang,chiLang;
    private TextView name, email, age, gender, height, weight, birthday, batt, radioButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String battery;
    private DatabaseReference stepsDataBaseRef;
    private int dataSteps, count=0;
    private SharedPreferences prefs;
    private static final String Current_check_key = "CheckCurrentStepsToFindBadge";
    private ImageView iv3days, iv1week, iv3weeks, iv1month, iv3months, iv6months;
    private int thisDay, lastDay, counterOfConsecutiveDays, newday;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadLocale();

        //ActionBar actionBar = getActivity().getSupportActionBar();
        //actionBar.setTitle(getResources().getString(R.string.app_name));

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
        batt=v.findViewById(R.id.tvBatteryPercentage);
        radioButton=v.findViewById(R.id.tvRadioButton);
        engLang=v.findViewById(R.id.En_btn);
        chiLang=v.findViewById(R.id.Chi_btn);


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

        engLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setLocale("en");
                refresh();

                    //recreate();

            }
        });
        chiLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("zh");
                refresh();
                //recreate();
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users/"+firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                name.setText(getResources().getString(R.string.Hello) + userProfile.getUserName());
                email.setText(userProfile.getUserEmail());
                age.setText(userProfile.getUserAge() + getResources().getString(R.string.years_old));
                if(userProfile.getUserGender() ==  "F"){gender.setText("Female"); }
                else if(userProfile.getUserGender() ==  "M"){gender.setText("Male"); }
                else {gender.setText(userProfile.getUserGender());}
                height.setText( userProfile.getUserHeight() + " cm");
                weight.setText(userProfile.getUserWeight() +" kg");
                birthday.setText(userProfile.getUserBirthday());
                radioButton.setText(userProfile.getRadiotext());
                //age.setText(String.valueOf(prefs.getInt("YOUR COUNTER PREF KEY", 0))); //rmb to delete this*******
//                age.setText(String.valueOf(prefs.getInt("ALREADYINCREASE", 0)));
//                age.setText(String.valueOf(lastDay));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        retrieveStepsCount();
        showbadges();


        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        SettingsFragment.MessageReceiver messageReceiver = new SettingsFragment.MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);


        return v;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale .setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }

    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String date = dateFormat.format(currentDate.getTime()).replaceAll("[\\D]","");
        stepsDataBaseRef=firebaseDatabase.getReference("Steps Count/" +currentuser + "/" + date );
        newday = currentDate.get(Calendar.DAY_OF_YEAR);

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
        if (!prefs.contains("ALREADYINCREASE")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("ALREADYINCREASE", 0);
            editor.commit();
        }

        thisDay = currentDate.get(Calendar.DAY_OF_YEAR); // GET THE CURRENT DAY OF THE YEAR
        lastDay = prefs.getInt("YOUR DATE PREF KEY", 0); //If we don't have a saved value, use 0.
        counterOfConsecutiveDays = prefs.getInt("YOUR COUNTER PREF KEY", 0); //If we don't have a saved value, use 0.


        //eg
//                    lastDay=142;
//                    thisDay=143;


        if (!prefs.contains("NEWDAY")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("NEWDAY", newday);
            editor.commit();
        }

        if(prefs.getInt("NEWDAY", newday) !=thisDay){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("ALREADYINCREASE", 0);
            editor.putInt("NEWDAY", thisDay);
            editor.commit();
        }


        // Initialize if it is the first time use
        stepsDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){
                    StepsPointValue stepsPointValue = dataSnapshot.getValue(StepsPointValue.class);
                    dataSteps = stepsPointValue.getSteps();

//
                    //find continuous days          //if done today then dun do again
                if ((lastDay == thisDay -1) &&(prefs.getInt("ALREADYINCREASE", 0) == 0)){ //testing at 100 steps a day first must EDIT HERE
                    // CONSECUTIVE DAYS
                    //if today hit >7500, count up
                    if(dataSteps>=7500) {
                        counterOfConsecutiveDays = counterOfConsecutiveDays + 1;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("YOUR DATE PREF KEY", thisDay);
                        editor.putInt("YOUR COUNTER PREF KEY", counterOfConsecutiveDays);
                        editor.putInt("ALREADYINCREASE", 1);
                        editor.commit();
                    }

                }
                else if(lastDay < thisDay-1){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("YOUR DATE PREF KEY", thisDay-1);
                    editor.putInt("YOUR COUNTER PREF KEY", 0);
                    editor.commit();
                }

                showbadges();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showbadges() {
        if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 3 || prefs.getInt("3DAY STREAK", 0) == 1) {
            //change greyscale to colour for 3days streak
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("3DAY STREAK", 1);
            editor.commit();
            iv3days.setImageResource(R.drawable.badges_day3_color);
        }
        if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 7 || prefs.getInt("1WEEK STREAK", 0) == 1) {
            //change greyscale to colour for 7days streak
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("1WEEK STREAK", 1);
            editor.commit();
            iv1week.setImageResource(R.drawable.badges_week1_color);
        }
        if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 21 || prefs.getInt("3WEEK STREAK", 0) == 1) {
            //change greyscale to colour for 3 weeks streak
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("3WEEK STREAK", 1);
            editor.commit();
            iv3weeks.setImageResource(R.drawable.badges_week3_color);
        }
        if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 30 || prefs.getInt("1MONTH STREAK", 0) == 1) {
            //change greyscale to colour for 1month streak
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("1MONTH STREAK", 1);
            editor.commit();
            iv1month.setImageResource(R.drawable.badges_month1_color);
        }
        if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 90 || prefs.getInt("3MONTH STREAK", 0) == 1) {
            //change greyscale to colour for 3month streak
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("3MONTH STREAK", 1);
            editor.commit();
            iv3months.setImageResource(R.drawable.badges_month3_color);
        }
        if (prefs.getInt("YOUR COUNTER PREF KEY", 0) >= 180 || prefs.getInt("6MONTH STREAK", 0) == 1) {
            //change greyscale to colour for 6month streak
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("6MONTH STREAK", 1);
            editor.commit();
            iv6months.setImageResource(R.drawable.badges_month6_color);
        }
    }
}
