package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Button editProfile, logout;
    private TextView name, email, age, gender, height, weight, birthday;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        name=v.findViewById(R.id.tvName);
        email=v.findViewById(R.id.tvEmail);
        age=v.findViewById(R.id.tvAge);
        gender=v.findViewById(R.id.tvGender);
        height=v.findViewById(R.id.tvHeight);
        weight=v.findViewById(R.id.tvWeight);
        birthday=v.findViewById(R.id.tvBirthday);

        logout=v.findViewById(R.id.btnLogout);
        firebaseAuth=FirebaseAuth.getInstance();
        editProfile = v.findViewById(R.id.btnEditProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                name.setText(userProfile.getUserName());
                email.setText(userProfile.getUserEmail());
                age.setText(userProfile.getUserAge());
                gender.setText(userProfile.getUserGender());
                height.setText(userProfile.getUserHeight());
                weight.setText(userProfile.getUserWeight());
                birthday.setText(userProfile.getUserBirthday());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
