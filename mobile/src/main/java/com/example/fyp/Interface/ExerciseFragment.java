package com.example.fyp.Interface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.ActivityInsert;
import com.example.fyp.R;

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

        back =findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back.setVisibility(GONE);
                onBackPressed();
               /*//For fragment to fragment
               //   FragmentManager fm = getSupportFragmentManager();
                //  Fragment fragment =new HomeFragment();
                //  HomeFragment homefragment = new HomeFragment();
               //   FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //  transaction.replace(R.id.exercise_layout,homefragment);
              //    transaction.commit();

                FragmentManager fm = getSupportFragmentManager();
                HomeFragment homefragment = new HomeFragment();
                fm.beginTransaction().replace(R.id.exercise_layout,homefragment).commit();
                */
            }
        });

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
                intent.putExtra("image",R.drawable.ic_icon_awesome_walking);
                startActivity(intent);
                break;
            case R.id.btnJogging:
                intent.putExtra("NAME","Jogging");
                intent.putExtra("image",R.drawable.ic_icon_awesome_jogging);
                startActivity(intent);
                break;
            case R.id.btnRunning:
                intent.putExtra("NAME","Running");
                intent.putExtra("image",R.drawable.ic_awesome_running);
                startActivity(intent);
                break;
            case R.id.btnTaiChi:
                intent.putExtra("NAME","Tai Chi");
                intent.putExtra("image",R.drawable.ic_awesome_taichi);
                startActivity(intent);
                break;
            case R.id.btnYoga:
                intent.putExtra("NAME","Yoga");
                intent.putExtra("image",R.drawable.ic_awesome_yoga);
                startActivity(intent);
                break;
            case R.id.btnZumba:
                intent.putExtra("NAME","Zumba");
                intent.putExtra("image",R.drawable.ic_awesome_zumba);
                startActivity(intent);
                break;
            case R.id.btnStrength:
                intent.putExtra("NAME","Strength");
                intent.putExtra("image",R.drawable.ic_awesome_strengthtraining);
                startActivity(intent);
                break;
            case R.id.btnOthers:
                intent.putExtra("NAME","Others");
                intent.putExtra("image",R.drawable.ic_awesome_others);
                startActivity(intent);
                break;

        }

    }
}

