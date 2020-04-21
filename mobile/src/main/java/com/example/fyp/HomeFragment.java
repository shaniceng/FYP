package com.example.fyp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;


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


       // circularProgressBar.setProgressBarColor(Color.BLUE);



       // circularProgressBar.setBackgroundProgressBarColor(Color.GRAY);

       //circularProgressBar.setProgressBarWidth(10f); // in DP
        //circularProgressBar.setBackgroundProgressBarWidth(8f); // in DP
        ExpandableTextView expTv1 = view.findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);
        circularProgressBar.setRoundBorder(true);
        //circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
        expTv1.setText(getString(R.string.intensity_workout_details));


        return view;
    }
}
