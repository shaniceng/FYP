package com.example.fyp;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.fyp.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button signIn, createAcc;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog; //use newer version ltr
    private TextView forgotPassword;
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.etSIEmail);
        password=findViewById(R.id.etSIPassword);
        signIn=findViewById(R.id.btnSISignIn);
        createAcc=findViewById(R.id.btnSICreateAcc);
        forgotPassword=findViewById(R.id.tvForgotPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        notificationManager = NotificationManagerCompat.from(this);

        if(user!=null){
            finish();
            startActivity(new Intent(MainActivity.this, MenuActivity.class));
        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(email.getText().toString(),password.getText().toString());
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, CreateAccActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });

        Refresh();
    }

    private void validate(String userEmail, String userPassword){
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if(userEmail.isEmpty() || userPassword.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else {
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //This is to check if user have verified emnail or not, have not implemented it yet
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag= firebaseUser.isEmailVerified();

        if(emailflag){
            finish();
            startActivity(new Intent(MainActivity.this,MenuActivity.class));
        }else{
            Toast.makeText(this,"Verifify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    public void sendOnChannel1(View v) {
        String title = "Alert!!!";
        String message = "You have not reached the sufficient steps for today!";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void Refresh(){
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = "Current Time:" + format.format(currentTime.getTime());

        //set fixed time : eg 7pm=19:00:00
        int setHour = 13; //testing times only
        int setMin = 31;
        int setSec = 00;

        if ((currentTime.get(Calendar.HOUR_OF_DAY) == setHour) && (currentTime.get(Calendar.MINUTE) == setMin)  && (currentTime.get(Calendar.SECOND) == setSec)) {
            sendOnChannel1(null);
        }
        runnable(1000);
    }

    public void runnable(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Refresh();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }


}
