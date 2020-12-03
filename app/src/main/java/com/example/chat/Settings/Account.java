package com.example.chat.Settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.R;
import com.example.chat.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Account extends AppCompatActivity {

    RelativeLayout save_post,save_chat;
    Switch switch1,switch2;
    RelativeLayout delete;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        save_post=findViewById(R.id.save_post);
        save_chat=findViewById(R.id.save_chat);
        delete=findViewById(R.id.delete_acount);
        switch1=findViewById(R.id.switch1);
        switch2=findViewById(R.id.switch2);
        Toolbar toolbar=findViewById(R.id.toolbar_account);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked()){
                    switch1.setChecked(false);
                }else {
                    switch1.setChecked(true);
                }
            }
        });

        save_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch2.isChecked()){
                    switch2.setChecked(false);
                }else {
                    switch2.setChecked(true);
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAccount();

            }
        });

    }

    private void deleteAccount() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(Account.this).create();
        LayoutInflater inflater = Account.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.confirm_delete, null);

        final EditText typeDelete = (EditText) dialogView.findViewById(R.id.type_delete);
        Button button1 = (Button) dialogView.findViewById(R.id.delete);
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

                if(typeDelete.getText().toString().isEmpty()) {
                    Toast.makeText(Account.this, "Cannot delete account", Toast.LENGTH_SHORT).show();
                    dialogBuilder.dismiss();
                }
                else if(typeDelete.getText().toString().equals("DELETE")){



                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            databaseReference=FirebaseDatabase.getInstance().getReference("User")
                                    .child(uid);
                            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Account.this, "Acount Deleted Successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Account.this, SignIn.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        }
                    });

                }
                else {
                    Toast.makeText(Account.this, "Text Doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }
}
