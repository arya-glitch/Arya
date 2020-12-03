package com.example.chat.Signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Verifying extends AppCompatActivity {

    FirebaseUser firebaseUser;
    ProgressDialog dialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Bundle bundle;
    Button done;
    TextView resend,timer;
    int i=1;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify);
        bundle=new Bundle();
        bundle=getIntent().getExtras();
        timer=findViewById(R.id.timer);
        resend=findViewById(R.id.resend);
        done=findViewById(R.id.done);


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    timer.setVisibility(View.VISIBLE);
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Verifying.this, "Sent", Toast.LENGTH_SHORT).show();
                            new CountDownTimer(30000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                    resend.setClickable(false);
                                    resend.setTextColor(getResources().getColor(R.color.grey));
                                    timer.setText("00: " + millisUntilFinished / 1000);
                                }

                                public void onFinish() {
                                    resend.setClickable(true);
                                    resend.setTextColor(getResources().getColor(R.color.dark_blue));
                                    timer.setVisibility(View.GONE);
                                }

                            }.start();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Verifying.this, "Failed to send Link", Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseUser.reload();
                dialog = new ProgressDialog(Verifying.this);
                dialog.setMessage("Creating Account");
                dialog.show();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(bundle.getString("email"),bundle.getString("password"));

                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    if (firebaseUser.isEmailVerified()) {

                                        Toast.makeText(Verifying.this, "Verified successfully", Toast.LENGTH_SHORT).show();

                                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("uid", firebaseUser.getUid());
                                        hashMap.put("name", bundle.getString("name"));
                                        hashMap.put("email", bundle.getString("email"));
                                        hashMap.put("phoneno", "default");
                                        hashMap.put("username", bundle.getString("username"));
                                        hashMap.put("gender", bundle.getString("gender"));
                                        hashMap.put("dob", bundle.getString("dob"));
                                        hashMap.put("age", bundle.getString("age"));
                                        hashMap.put("verified", false);
                                        hashMap.put("status", "(online)");
                                        hashMap.put("search", bundle.getString("name").toLowerCase());
                                        hashMap.put("imageurl", "default");

                                        databaseReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Intent intent = new Intent(Verifying.this, DP.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(Verifying.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }else {
                                        Toast.makeText(Verifying.this, "Not verified yet", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                dialog.dismiss();

            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();

        dialog = new ProgressDialog(Verifying.this);
        dialog.setMessage("Creating Account");
        dialog.show();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        AuthCredential credential = EmailAuthProvider
                .getCredential(bundle.getString("email"),bundle.getString("password"));

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if (firebaseUser.isEmailVerified()) {

                                Toast.makeText(Verifying.this, "Verified successfully", Toast.LENGTH_SHORT).show();

                                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", firebaseUser.getUid());
                                hashMap.put("name", bundle.getString("name"));
                                hashMap.put("email", bundle.getString("email"));
                                hashMap.put("phoneno", "default");
                                hashMap.put("username", bundle.getString("username"));
                                hashMap.put("gender", bundle.getString("gender"));
                                hashMap.put("dob", bundle.getString("dob"));
                                hashMap.put("age", bundle.getString("age"));
                                hashMap.put("verified", false);
                                hashMap.put("status", "(online)");
                                hashMap.put("search", bundle.getString("name").toLowerCase());
                                hashMap.put("imageurl", "default");

                                databaseReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Intent intent = new Intent(Verifying.this, DP.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(Verifying.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                Toast.makeText(Verifying.this, "Not verified yet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


        dialog.dismiss();
    }

    }



