package com.example.fyp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        CircularProgressBar circularProgressBar = view.findViewById(R.id.circularProgressBar);

        circularProgressBar.setProgressWithAnimation(7000); // =1s
        circularProgressBar.setProgressMax(7500);

        circularProgressBar.setProgressBarColor(Color.BLUE);

        circularProgressBar.setBackgroundProgressBarColor(Color.GRAY);

        circularProgressBar.setProgressBarWidth(7f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(3f); // in DP

        circularProgressBar.setRoundBorder(true);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

        return view;
    }
}
