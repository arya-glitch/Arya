package com.example.chat.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.R;
import com.example.chat.Signup.Signup00;

public class SafetyAndPrivacy extends AppCompatActivity {


    RelativeLayout standard,custom,customR0,blocked_user,restricted_user;
    TextView see_dp,see_post,send_msg,see_friends;
    Switch s1,s2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safety_and_privacy);

        standard=findViewById(R.id.standard1);
        custom=findViewById(R.id.custom_R2);
        customR0=findViewById(R.id.custom_R0);
        blocked_user=findViewById(R.id.blocked_user);
        restricted_user=findViewById(R.id.restricted_user);
        s1=findViewById(R.id.switch1);
        s2=findViewById(R.id.switch2);
        see_dp=findViewById(R.id.a);
        see_post=findViewById(R.id.b);
        send_msg=findViewById(R.id.c);
        see_friends=findViewById(R.id.d);


        Toolbar toolbar=findViewById(R.id.toolbar_safety);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                standard(s1.isChecked());
            }
        });



        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom(s2.isChecked());
            }
        });



        see_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(SafetyAndPrivacy.this,see_dp);

                popupMenu.getMenuInflater().inflate(R.menu.dp_visibility,popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.everyone:
                                see_dp.setText("Everyone");
                                return true;
                            case R.id.friends:
                                see_dp.setText("My Friends");
                                return true;
                            case R.id.only_me:
                                see_dp.setText("Only Me");
                                return true;
                            default:
                                return true;
                        }

                    }
                });
                popupMenu.show();

            }
        });

        see_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(SafetyAndPrivacy.this,see_post);

                popupMenu.getMenuInflater().inflate(R.menu.post_visibility,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.everyone:
                                see_post.setText("Everyone");
                                return true;
                            case R.id.friends:
                                see_post.setText("My Friends");
                                return true;
                            case R.id.onl_me:
                                see_post.setText("Only Me");
                                return true;
                            case R.id.select_people:
                                see_post.setText("Selected People");
                                return true;
                            default:
                                return true;
                        }

                    }
                });

                popupMenu.show();
            }
        });

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(SafetyAndPrivacy.this,send_msg);

                popupMenu.getMenuInflater().inflate(R.menu.message_send,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.everyone:
                                send_msg.setText("Everyone");
                                return true;
                            case R.id.friends:
                                send_msg.setText("My Friends");
                                return true;
                            default:
                                return true;
                        }

                    }
                });

                popupMenu.show();
            }
        });


     see_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(SafetyAndPrivacy.this,see_friends);

                popupMenu.getMenuInflater().inflate(R.menu.friend_visibility,popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.everyone:
                                see_friends.setText("Everyone");
                                return true;
                            case R.id.friends:
                                see_friends.setText("My Friends");
                                return true;
                            case R.id.only_me:
                                see_friends.setText("Only Me");
                            default:
                                return true;
                        }

                    }
                });
                popupMenu.show();
            }
        });

    }

    private void custom(boolean isChecked) {
        if(isChecked){
            s2.setChecked(false);
            customR0.setVisibility(View.GONE);
        }else {
            s1.setChecked(false);
            s2.setChecked(true);
            customR0.setVisibility(View.VISIBLE);

        }
    }

    public void standard(boolean isChecked){

        if(isChecked){
            s1.setChecked(false);
        }else {
            s1.setChecked(true);
            s2.setChecked(false);
            customR0.setVisibility(View.GONE);
        }
    };
}
