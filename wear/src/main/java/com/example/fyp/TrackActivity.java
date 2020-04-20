package com.example.fyp;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrackActivity extends WearableActivity {

    private TextView mTextView;
    private Button walk, jog, run, swim, taichi, yoga,zumba,sports,strength_training,others;

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

         /*walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

    }
}
