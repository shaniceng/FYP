package com.example.fyp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {
 private Button save;
 private EditText newUserName, newUserAge, newUserGender, newUserHeight, newUserWeight, newUserBirthday;
 private TextView userEmail;
 private FirebaseAuth firebaseAuth;
 private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        newUserName=findViewById(R.id.etNewUserName);
        newUserAge=findViewById(R.id.etEditAge);
        newUserGender=findViewById(R.id.etEditGender);
        newUserHeight=findViewById(R.id.etEditHeight);
        newUserWeight=findViewById(R.id.etEditWeight);
        newUserBirthday=findViewById(R.id.etEditBirthday);
        userEmail=findViewById(R.id.tvEmail);

        save=findViewById(R.id.btnSave);


        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUserName.setText(userProfile.getUserName());
                userEmail.setText(userProfile.getUserEmail());
                newUserAge.setText(userProfile.getUserAge());
                newUserGender.setText(userProfile.getUserGender());
                newUserHeight.setText(userProfile.getUserHeight());
                newUserWeight.setText(userProfile.getUserWeight());
                newUserBirthday.setText(userProfile.getUserBirthday());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Fragment fragment=new SettingsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();*/

                String email = userEmail.getText().toString();
                String name = newUserName.getText().toString();
                String age = newUserAge.getText().toString();
                String gender = newUserGender.getText().toString();
                String height = newUserHeight.getText().toString();
                String weight = newUserWeight.getText().toString();
                String birthday = newUserBirthday.getText().toString();

                UserProfile userProfile=new UserProfile(name, email, age,gender,height,weight,birthday);
                databaseReference.setValue(userProfile);

                finish();


            }
        });
    }
}
