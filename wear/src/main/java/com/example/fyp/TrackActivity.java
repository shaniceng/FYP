package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrackActivity extends WearableActivity {

    private TextView mTextView;
    private Button walk, jog, run, swim, taichi, yoga,zumba,sports,strength_training,others;
    public static final String EXTRA_TEXT = "com.example.application.fyp.EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mTextView = (TextView) findViewById(R.id.text);
        walk=findViewById(R.id.btnWBriskWalking);
        jog = findViewById(R.id.btnWJogging);
        run=findViewById(R.id.btnWRunning);
        swim=findViewById(R.id.btnWSwimming);
        taichi=findViewById(R.id.btnWTaichi);
        yoga =findViewById(R.id.btnWYoga);
        zumba=findViewById(R.id.btnWZumba);
        sports=findViewById(R.id.btnWSports);
        strength_training=findViewById(R.id.btnWStrength);
        others=findViewById(R.id.btnWOthers);



        // Enables Always-on
        setAmbientEnabled();

         walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String walking = walk.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,walking);
                startActivity(intent);

            }
        });
        jog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jogging = jog.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,jogging);
                startActivity(intent);

            }
        });
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String running = run.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,running);
                startActivity(intent);

            }
        });
        swim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String swimming = swim.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,swimming);
                startActivity(intent);

            }
        });
        taichi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taiching = taichi.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,taiching);
                startActivity(intent);

            }
        });
        yoga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yogaing = yoga.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,yogaing);
                startActivity(intent);
            }
        });
        zumba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zumbaing = zumba.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,zumbaing);
                startActivity(intent);
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sporting = sports.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,sporting);
                startActivity(intent);
            }
        });
        strength_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String training = strength_training.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,training);
                startActivity(intent);
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String others_ = others.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,others_);
                startActivity(intent);
            }
        });

    }
}
