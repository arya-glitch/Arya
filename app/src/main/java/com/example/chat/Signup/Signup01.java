package com.example.chat.Signup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.SignIn;
import com.example.chat.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Signup01 extends AppCompatActivity {

    String Gender;
    int age;
    DatabaseReference databaseReference,databaseReference1;
    private FirebaseUser firebaseUser;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up1);


        final EditText Naam = (EditText) findViewById(R.id.naam);
        final EditText username = (EditText) findViewById(R.id.username_sign_in);
        final EditText dob = (EditText) findViewById(R.id.dob);
        final RadioButton male = findViewById(R.id.male);
        final RadioButton female = findViewById(R.id.female);
        final ImageView terms =findViewById(R.id.terms);
        final Button next = findViewById(R.id.next_button);

        final boolean isemail=getIntent().getBooleanExtra("isemail",false);

        if(isemail){
            Toast.makeText(Signup01.this, "Sign Up with email id", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(Signup01.this, "Sign Up with phone number", Toast.LENGTH_SHORT).show();
        }




        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(Signup01.this).create();
                final View customLayout = getLayoutInflater().inflate(R.layout.terms, null);
                dialogBuilder.setView(customLayout);
                dialogBuilder.show();

                TextView understand=customLayout.findViewById(R.id.understand);
                understand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                });

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(Signup01.this);
                dialog.setMessage("Validating Data");
                dialog.show();
                next.setClickable(false);
                if (male.isChecked()) {
                    Gender= "Male";
                } else if (female.isChecked()) {
                    Gender = "Female";
                }else {
                    Gender="not checked";
                }

                age=getAge(dob.getText().toString());

                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username cannot be kept empty", Toast.LENGTH_LONG).show();
                }
                else if(age<12){
                    if(!dob.getText().toString().isEmpty()){
                        Toast.makeText(Signup01.this, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Signup01.this, "You dont have appropriate age for this app", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if (Naam.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name cannot be empty ", Toast.LENGTH_LONG).show();
                    Naam.setError("Name not valid");
                }else if(!isAlpha(Naam.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Name can only contain Letters,or check for spaces",Toast.LENGTH_SHORT).show();
                }else if(!isusername(username.getText().toString())||!startswith(username.getText().toString())){
                    Toast.makeText(Signup01.this, "User name not valid see the terms ", Toast.LENGTH_SHORT).show();
                    username.setError("Username not valid");
                }else if(Gender.equals("not checked")){
                    Toast.makeText(Signup01.this, "Please Select a gender", Toast.LENGTH_SHORT).show();
                }
                else {

                    databaseReference=FirebaseDatabase.getInstance().getReference("User");
                    databaseReference.orderByChild("username").equalTo(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Toast.makeText(Signup01.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            }else {


                                        if(isemail) {
                                            Intent intent = new Intent(Signup01.this, Email01.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("name", Naam.getText().toString());
                                            bundle.putString("username", username.getText().toString());
                                            bundle.putString("dob", dob.getText().toString());
                                            bundle.putString("gender", Gender);
                                            bundle.putInt("age", age);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Intent intent = new Intent(Signup01.this, PhoneNo.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("name", Naam.getText().toString());
                                            bundle.putString("username", username.getText().toString());
                                            bundle.putString("dob", dob.getText().toString());
                                            bundle.putString("gender", Gender);
                                            bundle.putInt("age", age);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                dialog.dismiss();
                next.setClickable(true);
            }
        });
    }

    private boolean startswith(String string) {

        if(Character.isLetter(string.charAt(0))){
            return true;
        }
        return false;
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z\\s]+");
    }
    public boolean isusername(String name) {
        return name.matches("^(?=.{4,20}$)(?:[a-zA-Z\\d]+(?:(?:\\.|-|_)[a-zA-Z\\d])*)+$");
    }
    public boolean hasSpace(String name) {
        return name.matches("\\s");
    }
    private int getAge(String dobString){

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }



        return age;
    }


}



