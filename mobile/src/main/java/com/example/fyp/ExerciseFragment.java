package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.view.View.GONE;

public class ExerciseFragment extends AppCompatActivity {

    Button back, running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);


        back =findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(GONE);

               // FragmentManager fm = getSupportFragmentManager();
               // Fragment fragment =new HomeFragment();

               // HomeFragment homefragment = new HomeFragment();
               // FragmentTransaction transaction = getFragmentManager().beginTransaction();
               // transaction.replace(R.id.exercise_layout,homefragment);
               // transaction.commit();

                FragmentManager fm = getSupportFragmentManager();
                HomeFragment homefragment = new HomeFragment();
                fm.beginTransaction().replace(R.id.exercise_layout,homefragment).commit();


            }
        });

        running =findViewById(R.id.btnRunning);
        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));


            }
        });

    }
}

