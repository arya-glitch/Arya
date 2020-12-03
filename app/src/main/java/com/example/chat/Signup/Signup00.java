package com.example.chat.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.R;


public class Signup00 extends AppCompatActivity {

    TextView sup_with_email,sup_with_phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_option);

        sup_with_email=findViewById(R.id.sup_with_email);
        sup_with_phone=findViewById(R.id.sup_with_phone);

        sup_with_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup00.this,Signup01.class);
                intent.putExtra("isemail",true);
                startActivity(intent);
            }
        });

        sup_with_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup00.this,Signup01.class);
                intent.putExtra("isemail",false);
                startActivity(intent);

            }
        });


    }
}