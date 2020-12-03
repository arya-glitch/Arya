package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.chat.Adapter.ViewPagerAdapter2;
import com.example.chat.Listener.AppBarStateChangeListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class Profile extends AppCompatActivity {


    private boolean appBarExpanded;
    AlertDialog.Builder builder;
    private Menu collapsedMenu,menuList;
    private String userid;
    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FloatingActionButton chat_button,addfriend,share,added;
    ImageView profile_pic;
    TextView postext,no_post,no_of_friends,no_of_following,username,friends;
    int number=0,number1=0;
    private DatabaseReference databaseReference,databaseReference1;
    CircleImageView collapsed_dp;
    FloatingActionButton shareProfile;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ViewPagerAdapter2 viewPagerAdapter2;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView back,menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.profile_another_user);

        final Intent intent = getIntent();
        userid = intent.getStringExtra("userId");

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        profile_pic = findViewById(R.id.dp);
        chat_button=findViewById(R.id.chat_button);
        addfriend=findViewById(R.id.addfriend);
        added=findViewById(R.id.added);
        username=findViewById(R.id.username);
        share=findViewById(R.id.shareprofile);
        tabLayout=findViewById(R.id.tablayout_profile);
        viewPager=findViewById(R.id.viewpager_profile_fragment);
        postext=findViewById(R.id.posttext);
        no_post=findViewById(R.id.no_of_post);
        no_of_following=findViewById(R.id.no_of_following);
        no_of_friends=findViewById(R.id.no_of_friends);
        shareProfile=findViewById(R.id.shareprofile);
        collapsed_dp=findViewById(R.id.collapsed_dp);
        back=findViewById(R.id.back_button);
        friends=findViewById(R.id.friendtext);
        menu=findViewById(R.id.profile_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar_01);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        builder=new AlertDialog.Builder(this);


        readUserData();
        initialiseViewPager();
        getMenu(menu);

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MessageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("userid",userid);
                bundle.putString("imageurl", "default");
                bundle.putString("videourl","default");
                bundle.putString("message", "");
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });


        ManangeFriend();

        added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Requested Already", Toast.LENGTH_SHORT).show();
            }
        });



        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addfriend.setVisibility(View.GONE);



                databaseReference=FirebaseDatabase.getInstance().getReference("Request").child(firebaseUser.getUid())
                        .child(userid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            databaseReference1=FirebaseDatabase.getInstance().getReference("Friend")
                                    .child(firebaseUser.getUid()).child(userid).child("isfriend");
                            databaseReference1.setValue(true);
                            databaseReference1=FirebaseDatabase.getInstance().getReference("Friend")
                                    .child(userid).child(firebaseUser.getUid()).child("isfriend");
                            databaseReference1.setValue(true);

                            databaseReference.removeValue();
                            share.setVisibility(View.VISIBLE);
                        }
                        else  {
                            databaseReference1 = FirebaseDatabase.getInstance().getReference("Request")
                                    .child(userid).child(firebaseUser.getUid()).child("isrequested");
                            databaseReference1.setValue(true);
                            added.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Requested",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        });




        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if(state==State.IDLE||state==State.COLLAPSED){
                    shareProfile.setVisibility(View.INVISIBLE);
                    addfriend.setVisibility(View.INVISIBLE);
                    added.setVisibility(View.INVISIBLE);
                    chat_button.setVisibility(View.INVISIBLE);
                    if(state==State.COLLAPSED){
                        collapsed_dp.setVisibility(View.VISIBLE);
                    }else {
                        collapsed_dp.setVisibility(View.GONE);
                    }
                }else {

                     ManangeFriend();
                    collapsed_dp.setVisibility(View.GONE);
                }
            }
        });


        no_of_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,Friend.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
                CustomIntent.customType(Profile.this,"bottom-to-up");
            }
        });



    }

    private void getMenu(final ImageView menu) {

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(Profile.this,menu);
                popupMenu.getMenuInflater().inflate(R.menu.profile_menu,popupMenu.getMenu());

                menuList=popupMenu.getMenu();
                databaseReference=FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid()).child(userid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            MenuItem item = menuList.findItem(R.id.unfriend);
                            hideMenu(item);
                            item=menuList.findItem(R.id.dont_show_post);
                            hideMenu(item);
                        }else {
                            MenuItem item = menuList.findItem(R.id.unfriend);
                            showMenu(item);
                            item = menuList.findItem(R.id.dont_show_post);
                            showMenu(item);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.unfriend:
                                builder.setMessage("You wont be able to see this user's post if you unfriend. Still unfriend?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                                databaseReference=FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid());
                                                databaseReference.child(userid).removeValue();
                                                databaseReference1=FirebaseDatabase.getInstance().getReference("Friend").child(userid);
                                                databaseReference1.child(firebaseUser.getUid()).removeValue();
                                                Toast.makeText(getApplicationContext(),"Unfriended",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                            }
                                        });
                                return true;

                            case R.id.report:
                                databaseReference=FirebaseDatabase.getInstance().getReference("Reported").child(firebaseUser.getUid())
                                        .child(userid);
                                databaseReference.child("isreported").setValue(true);
                                return true;

                            case R.id.block:
                                databaseReference=FirebaseDatabase.getInstance().getReference("Blocked").child(firebaseUser.getUid())
                                        .child(userid);
                                databaseReference.child("isblocked").setValue(true);
                                return true;

                            case R.id.dont_show_post:
                                databaseReference=FirebaseDatabase.getInstance().getReference("Hide Post").child(firebaseUser.getUid())
                                        .child(userid);
                                databaseReference.child("ishidden").setValue(true);
                                return true;
                            default:
                                return true;

                        }

                    }
                });

                popupMenu.show();


            }
        });
    }


    private void initialiseViewPager() {

        viewPagerAdapter2=new ViewPagerAdapter2(getSupportFragmentManager(),tabLayout.getTabCount(),userid);
        viewPager.setAdapter(viewPagerAdapter2);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    viewPagerAdapter2.notifyDataSetChanged();

                }
                else if (tab.getPosition()==1){
                    viewPagerAdapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }


    private void readUserData() {

        loadimage();
        setName();
        getExtraData();

    }


    private void getExtraData() {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("Friend").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int n= (int) dataSnapshot.getChildrenCount();
                no_of_friends.setText(Integer.toString(n));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.orderByChild("uid").equalTo(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int n= (int) dataSnapshot.getChildrenCount();
                no_post.setText(Integer.toString(n));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setName() {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user=dataSnapshot.getValue(User.class);

                collapsingToolbarLayout.setTitle(user.getName());
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadimage() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);


                if (!user.getImageurl().equals("default")) {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_pic);
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(collapsed_dp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readname(String userid, final CollapsingToolbarLayout collapsingToolbarLayout) {

        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              User user=dataSnapshot.getValue(User.class);
              collapsingToolbarLayout.setTitle(user.getName());
              username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void ManangeFriend() {



        databaseReference = FirebaseDatabase.getInstance().getReference("Request").child(userid).child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    added.setVisibility(View.VISIBLE);
                    addfriend.setVisibility(View.INVISIBLE);
                    chat_button.setVisibility(View.VISIBLE);
                }else {
                    addfriend.setVisibility(View.VISIBLE);
                    chat_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference1=FirebaseDatabase.getInstance().getReference("Friend")
                .child(userid).child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    share.setVisibility(View.VISIBLE);
                    addfriend.setVisibility(View.INVISIBLE);
                    chat_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menuList=menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        databaseReference=FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid()).child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    MenuItem item = menuList.findItem(R.id.unfriend);
                    hideMenu(item);
                    item=menuList.findItem(R.id.dont_show_post);
                    hideMenu(item);
                }else {
                    MenuItem item = menuList.findItem(R.id.unfriend);
                    showMenu(item);
                    item = menuList.findItem(R.id.dont_show_post);
                    showMenu(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.unfriend:
                builder.setMessage("You wont be able to see this user's post if you unfriend. Still unfriend?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                databaseReference=FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid());
                                databaseReference.child(userid).removeValue();
                                databaseReference1=FirebaseDatabase.getInstance().getReference("Friend").child(userid);
                                databaseReference1.child(firebaseUser.getUid()).removeValue();
                                Toast.makeText(getApplicationContext(),"Unfriended",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

            case R.id.report:
                databaseReference=FirebaseDatabase.getInstance().getReference("Reported").child(firebaseUser.getUid())
                        .child(userid);
                databaseReference.child("isreported").setValue(true);

            case R.id.block:
                databaseReference=FirebaseDatabase.getInstance().getReference("Blocked").child(firebaseUser.getUid())
                        .child(userid);
                databaseReference.child("isblocked").setValue(true);

            case R.id.dont_show_post:
                databaseReference=FirebaseDatabase.getInstance().getReference("Hide Post").child(firebaseUser.getUid())
                        .child(userid);
                databaseReference.child("ishidden").setValue(true);



            default: return super.onOptionsItemSelected(item);

        }

    }
    private void hideMenu(MenuItem item )
    {
        item.setVisible(false);
    }


    private void showMenu(MenuItem item )
    {

        item.setVisible(true);
    }





}