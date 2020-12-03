package com.example.chat.Signup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneNoLogin extends AppCompatActivity {

    EditText phoneno;
    EditText otp;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String phone,Otp;
    FirebaseAuth mAuth;
    TextView timer;
    Bundle bundle;

    Button send,verify;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phoneno_login);

        send=findViewById(R.id.sendotp_button);

        verify=findViewById(R.id.verify);
        phoneno=findViewById(R.id.phone_no);
        otp=findViewById(R.id.otp);
        timer=findViewById(R.id.timer);


        bundle=new Bundle();
        bundle=getIntent().getExtras();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phoneno.getText().toString().isEmpty()){
                    Toast.makeText(PhoneNoLogin.this, "Phone no can not be empty", Toast.LENGTH_SHORT).show();
                }else {
                    final ProgressDialog dialog = new ProgressDialog(PhoneNoLogin.this);
                    dialog.setMessage("Signing In");
                    dialog.show();
                    send.setClickable(false);
                    send.setTextColor(getResources().getColor(R.color.grey));
                    databaseReference = FirebaseDatabase.getInstance().getReference("User");
                    databaseReference.orderByChild("phoneno").equalTo(phoneno.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        phone = phoneno.getText().toString();

                                        if (phone.length() == 10) {
                                            send.setText("RESEND");
                                            sendOtp(phone);
                                            timer.setVisibility(View.VISIBLE);
                                            new CountDownTimer(120000, 1000) {

                                                public void onTick(long millisUntilFinished) {

                                                    send.setClickable(false);
                                                    send.setTextColor(getResources().getColor(R.color.grey));

                                                    int minute = (int) ((millisUntilFinished / 1000) / 60);
                                                    int sec = (int) ((millisUntilFinished / 1000) % 60);
                                                    timer.setText(Integer.toString(minute) + ":" + Integer.toString(sec));

                                                }

                                                public void onFinish() {
                                                    send.setClickable(true);
                                                    send.setTextColor(Color.BLACK);
                                                    timer.setVisibility(View.GONE);
                                                }

                                            }.start();
                                        } else {
                                            Toast.makeText(PhoneNoLogin.this, "Check Your Phone no", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        dialog.dismiss();

                                        AlertDialog.Builder b = new AlertDialog.Builder(PhoneNoLogin.this)
                                                .setTitle("Account with this Number doesn't exist.Do you want to Sign Up?")
                                                .setPositiveButton("Yes",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                Intent intent = new Intent(PhoneNoLogin.this, Signup00.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                )
                                                .setNegativeButton("No",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                );
                                        b.show();
                                        send.setClickable(true);
                                        send.setTextColor(Color.BLACK);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    dialog.dismiss();
                }
            }
        });





        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().isEmpty()){
                    Toast.makeText(PhoneNoLogin.this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
                }else{
                verifyCode(otp.getText().toString());
            }
            }
        });







    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent=new Intent(PhoneNoLogin.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(PhoneNoLogin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {


            Toast.makeText(PhoneNoLogin.this, "Sent", Toast.LENGTH_SHORT).show();

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            // ...
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {


            String code=credential.getSmsCode();
            if(code!=null){
                otp.setText(code);
                verifyCode(code);

            }




        }

        @Override
        public void onVerificationFailed(FirebaseException e) {


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(PhoneNoLogin.this, "Invalid Request", Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(PhoneNoLogin.this, "To many Request", Toast.LENGTH_SHORT).show();
            }


        }


    };


    private void sendOtp(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                PhoneNoLogin.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



}
