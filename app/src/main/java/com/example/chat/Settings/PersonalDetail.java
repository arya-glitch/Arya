package com.example.chat.Settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.chat.R;
import com.example.chat.SignIn;
import com.example.chat.Signup.Email01;
import com.example.chat.Signup.PhoneNoLogin;
import com.example.chat.Signup.Signup00;
import com.example.chat.User;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class PersonalDetail extends AppCompatActivity {


    TextView name,username,gender,phoneno,email,dob;
    RelativeLayout change_password;
    FirebaseUser fuser;
    ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_details);

        name=findViewById(R.id.name);
        username=findViewById(R.id.username_profile);
        gender=findViewById(R.id.gender);
        phoneno=findViewById(R.id.phoneno);
        email=findViewById(R.id.email);
        dob=findViewById(R.id.dob);
        change_password=findViewById(R.id.change_password);

        Toolbar toolbar=findViewById(R.id.toolbar_detail);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        readValues();

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             change_name();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_username();
            }
        });

        fuser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("User").
                child(fuser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getEmail().equals("default")){
                    change_password.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(PersonalDetail.this, gender);
                popup.getMenuInflater().inflate(R.menu.gender, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        AlertDialog.Builder b = new AlertDialog.Builder(PersonalDetail.this)
                                .setTitle("For changing your gender you will have to upload medical certificate")
                                .setPositiveButton("Upload",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent intent = new Intent(PersonalDetail.this, Signup00.class);
                                                startActivity(intent);
                                            }
                                        }
                                )
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        }
                                );
                        b.show();
                        return true;
                    }
                });

                popup.show();
            }
        });


        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changepassword();

            }
        });

    }

    private void changepassword() {

        final AlertDialog builder=new AlertDialog.Builder(PersonalDetail.this).create();
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.setuppassword,null);

        final EditText oldPassword,newPassword,confirmPassword;
        Button save,cancel;

        oldPassword=view.findViewById(R.id.old_signup_pass);
        newPassword=view.findViewById(R.id.new_signup_pass);
        confirmPassword=view.findViewById(R.id.signup_pass_confirm);
        save=view.findViewById(R.id.save_password);
        cancel=view.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().isEmpty() || newPassword.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password cannot be less than 6 character ", Toast.LENGTH_LONG).show();
                } else if (isPassword(newPassword.getText().toString())) {
                    Toast.makeText(PersonalDetail.this, "Password Not Valid", Toast.LENGTH_SHORT).show();

                } else if (!(confirmPassword.getText().toString().equals(confirmPassword.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "Confirm Password Doesn't match", Toast.LENGTH_LONG).show();
                }else {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").
                        child(fuser.getUid());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), oldPassword.getText().toString());

                        fuser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog = new ProgressDialog(PersonalDetail.this);
                                dialog.setMessage("Changing Password");
                                dialog.show();
                                if (task.isSuccessful()) {
                                    fuser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(PersonalDetail.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();

                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(PersonalDetail.this, "Failed to change Password", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(PersonalDetail.this, "Old Password was Incorrect", Toast.LENGTH_SHORT).show();
                                }
                                builder.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            }
        });


        builder.setView(view);
        builder.show();

    }

    private void readValues() {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                username.setText(user.getUsername());
                dob.setText(user.getDob());
                gender.setText(user.getGender());

                if(user.getEmail().equals("default")){
                    phoneno.setText(user.getPhoneno());
                    email.setText("Not Linked");
                }else {
                    email.setText(user.getEmail());
                    phoneno.setText("Not Linked");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z\\s]+");
    }

    public boolean isPassword(String password){
        return password.matches( "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$");
    }


    protected void change_name(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(PersonalDetail.this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_profile_name, null);

        final EditText New_name = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialogView.findViewById(R.id.buttonSave);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!New_name.getText().toString().isEmpty()) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("User");

                    FirebaseAuth fAuth= FirebaseAuth.getInstance();
                    myRef.child(fAuth.getCurrentUser().getUid()).child("name").setValue(New_name.getText().toString());
                    myRef.child(fAuth.getCurrentUser().getUid()).child("search").setValue(New_name.getText().toString());

                    dialogBuilder.dismiss();
                }
                else{
                    Toast.makeText(PersonalDetail.this,"Name cannot be empty",Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    protected void change_username(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(PersonalDetail.this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_user_name, null);

        final EditText New_name = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialogView.findViewById(R.id.buttonSave);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!New_name.getText().toString().isEmpty()) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("User");

                    FirebaseAuth fAuth= FirebaseAuth.getInstance();
                    myRef.child(fAuth.getCurrentUser().getUid()).child("username").setValue(New_name.getText().toString());

                    dialogBuilder.dismiss();
                }
                else{
                    Toast.makeText(PersonalDetail.this,"Name cannot be empty",Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
