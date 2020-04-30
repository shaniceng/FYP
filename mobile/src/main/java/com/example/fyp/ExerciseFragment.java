package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.view.View.GONE;

public class ExerciseFragment extends AppCompatActivity implements  View.OnClickListener{

    Button back, walking,jogging,running,taichi,yoga,zumba,strength,others;
    String activityname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

       /* back =findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(GONE);

               //For fragment to fragment
               //   FragmentManager fm = getSupportFragmentManager();
                //  Fragment fragment =new HomeFragment();
                //  HomeFragment homefragment = new HomeFragment();
               //   FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //  transaction.replace(R.id.exercise_layout,homefragment);
              //    transaction.commit();

                FragmentManager fm = getSupportFragmentManager();
                HomeFragment homefragment = new HomeFragment();
                fm.beginTransaction().replace(R.id.exercise_layout,homefragment).commit();

            }
        }); */

        walking =findViewById(R.id.btnWalking);
        jogging =findViewById(R.id.btnJogging);
        running=findViewById(R.id.btnRunning);
        taichi=findViewById(R.id.btnTaiChi);
        yoga=findViewById(R.id.btnYoga);
        zumba=findViewById(R.id.btnZumba);
        strength=findViewById(R.id.btnStrength);
        others=findViewById(R.id.btnOthers);

        walking.setOnClickListener(this);
        jogging.setOnClickListener(this);
        running.setOnClickListener(this);
        taichi.setOnClickListener(this);
        yoga.setOnClickListener(this);
        zumba.setOnClickListener(this);
        strength.setOnClickListener(this);
        others.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnWalking:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                activityname = "walking";
                break;
            case R.id.btnJogging:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;
            case R.id.btnRunning:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;
            case R.id.btnTaiChi:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;
            case R.id.btnYoga:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;
            case R.id.btnZumba:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;
            case R.id.btnStrength:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;
            case R.id.btnOthers:
                startActivity(new Intent(ExerciseFragment.this,TimerActivity.class));
                break;

        }

    }
}

