package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

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

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,walking);
                startActivity(intent);
                finish();

            }
        });
        jog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jogging = jog.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,jogging);
                startActivity(intent);
                finish();

            }
        });
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String running = run.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,running);
                startActivity(intent);
                finish();

            }
        });
        swim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String swimming = swim.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,swimming);
                startActivity(intent);
                finish();

            }
        });
        taichi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taiching = taichi.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,taiching);
                startActivity(intent);
                finish();

            }
        });
        yoga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yogaing = yoga.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,yogaing);
                startActivity(intent);
                finish();
            }
        });
        zumba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zumbaing = zumba.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,zumbaing);
                startActivity(intent);
                finish();
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();

            }
        });
        strength_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String training = strength_training.getText().toString();

                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,training);
                startActivity(intent);
                finish();
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();

                /*String others_ = others.getText().toString();
                finish();
                Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
                intent.putExtra(EXTRA_TEXT,others_);
                startActivity(intent);*/
            }
        });

    }
    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, 1002);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText

            finish();
            Intent intent = new Intent(TrackActivity.this, TimerActivity.class);
            intent.putExtra(EXTRA_TEXT,spokenText);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
