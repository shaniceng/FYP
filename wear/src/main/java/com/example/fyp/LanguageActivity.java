package com.example.fyp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

public class LanguageActivity extends WearableActivity {

    private Button engbtn, chibtn;
    private TextView selectedlanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();


        setContentView(R.layout.activity_language);

        engbtn = findViewById(R.id.btn_eng);
        chibtn = findViewById(R.id.btn_chi);
        selectedlanguage=findViewById(R.id.tvSelectedLanguage);



        engbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });
        chibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("zh");
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);

        if(lang == "en")
        {selectedlanguage.setText("Selected Language: English");}
        if(lang == "zh")
        {selectedlanguage.setText("Selected Language: 中文");}

        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
}
