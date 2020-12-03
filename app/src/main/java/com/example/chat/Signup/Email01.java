package com.example.chat.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Email01 extends AppCompatActivity {
    private static final String TAG = "SignUP";
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase ;
    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    Bundle bundle;


     FirebaseAuth mAuth =FirebaseAuth.getInstance();;

    //private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        final EditText sign_up_email = (EditText) findViewById(R.id.supemail);
        final EditText sign_up_pass = (EditText) findViewById(R.id.signup_pass);
        final EditText sign_up_pass_confirm=(EditText) findViewById(R.id.signup_pass_confirm);
        Button sign_up_button = (Button) findViewById(R.id.sign_up_keleya);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getInstance().getReference("User");

        bundle=new Bundle();
        bundle=getIntent().getExtras();



        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                if (sign_up_email.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Email Id cannot be kept empty", Toast.LENGTH_LONG).show();
                }
                else if (sign_up_pass.getText().toString().isEmpty()||sign_up_pass.getText().toString().length()<6) {
                    Toast.makeText(getApplicationContext(), "Password cannot be less than 6 character ", Toast.LENGTH_LONG).show();
                }else  if(isPassword(sign_up_pass.getText().toString())){
                    Toast.makeText(Email01.this, "Password Not Valid", Toast.LENGTH_SHORT).show();

                }else if(!(sign_up_pass.getText().toString().equals(sign_up_pass_confirm.getText().toString()))){
                    Toast.makeText(getApplicationContext(),"Confirm Password Doesn't match",Toast.LENGTH_LONG).show();
                }
                else if(!(sign_up_email.getText().toString().isEmpty()&&sign_up_pass.getText().toString().isEmpty())){
                    mAuth.createUserWithEmailAndPassword(sign_up_email.getText().toString(),sign_up_pass.getText().toString())
                            .addOnCompleteListener(Email01.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        firebaseUser=mAuth.getCurrentUser();
                                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent=new Intent(Email01.this,Verifying.class)  ;
                                                bundle.putString("email",sign_up_email.getText().toString());
                                                bundle.putString("password",sign_up_pass.getText().toString());
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });

                                    } else {

                                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });
                }
                else{
                    Toast.makeText(getApplicationContext(),"Unexpected Error occured",Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    public boolean isPassword(String password){
        return password.matches( "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$");
    }
}


