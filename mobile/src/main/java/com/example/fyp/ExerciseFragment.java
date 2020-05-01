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

import java.util.TimerTask;

import static android.view.View.GONE;

public class ExerciseFragment extends AppCompatActivity implements  View.OnClickListener{

    Button back, walking,jogging,running,taichi,yoga,zumba,strength,others;
    String activityname;
    ActivityInsert activityInsert;
    TimerActivity name;
    Intent intent;


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
        intent = new Intent(ExerciseFragment.this,TimerActivity.class);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnWalking:
                intent.putExtra("NAME","Walking");
                startActivity(intent);
                break;
            case R.id.btnJogging:
                intent.putExtra("NAME","Jogging");
                startActivity(intent);
                break;
            case R.id.btnRunning:
                intent.putExtra("NAME","Running");
                startActivity(intent);
                break;
            case R.id.btnTaiChi:
                intent.putExtra("NAME","Tai Chi");
                startActivity(intent);
                break;
            case R.id.btnYoga:
                intent.putExtra("NAME","Yoga");
                startActivity(intent);
                break;
            case R.id.btnZumba:
                intent.putExtra("NAME","Zumba");
                startActivity(intent);
                break;
            case R.id.btnStrength:
                intent.putExtra("NAME","Strength");
                startActivity(intent);
                break;
            case R.id.btnOthers:
                intent.putExtra("NAME","Others");
                startActivity(intent);
                break;

        }

    }
}

