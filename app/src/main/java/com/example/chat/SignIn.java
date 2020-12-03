package com.example.chat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.Signup.PhoneNoLogin;
import com.example.chat.Signup.Signup00;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignIn extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    TextView signup,login_phone;
    ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


        username=findViewById(R.id.uname);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup001);
        login_phone=findViewById(R.id.login_with_phone);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Email Id cannot be kept empty", Toast.LENGTH_LONG).show();
                }
                else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password cannot be empty ", Toast.LENGTH_LONG).show();
                }  else {


                    dialog = new ProgressDialog(SignIn.this);
                    dialog.setMessage("Signing In");
                    dialog.show();
                    firebaseAuth=FirebaseAuth.getInstance();
                    firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        dialog.dismiss();
                                        finish();


                                    } else {
                                        Toast.makeText(getApplicationContext(), "Sign In Failed!!! Check your data", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }

                                }
                            });

                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this, Signup00.class);
                startActivity(intent);
            }
        });

        login_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,PhoneNoLogin.class);
                startActivity(intent);

            }
        });

    }




}








