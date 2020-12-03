package com.example.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.GridUserFriendAdapter;
import com.example.chat.Adapter.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Friend extends AppCompatActivity {

    RecyclerView recyclerView,recyclerViewList;
    TextView friendtext;
     GridUserFriendAdapter gridUserFriendAdapter;
    private List<User> mUser;
    UserAdapter userAdapter;
    DatabaseReference databaseReference;
    String userid;
    ImageView grid,list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.fragment_friend);

    recyclerView=findViewById(R.id.friend_grid);
    recyclerViewList=findViewById(R.id.friend_list);
    grid=findViewById(R.id.grid_friend);
    list=findViewById(R.id.list_friend);
    friendtext=findViewById(R.id.friendtext);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(Friend.this,3);
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setHasFixedSize(true);

    recyclerViewList.setLayoutManager(new GridLayoutManager(Friend.this,1));
    mUser=new ArrayList<>();
    Toolbar toolbar=findViewById(R.id.toolbar_friend);
    setActionBar(toolbar);
    getSupportActionBar();
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

    userid=getIntent().getStringExtra("userid");

    readUser();
    list.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            list.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
            show(recyclerViewList,recyclerView);
        }
    });

    grid.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            grid.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            show(recyclerView,recyclerViewList);
        }
    });

    }

    private void layoutAnimation(RecyclerView recyclerView){

        Context context=recyclerView.getContext();
        LayoutAnimationController layoutAnimationController=
                AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_right_to_left);

        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void show(RecyclerView recyclerView1, RecyclerView recyclerView2) {

        recyclerView2.setVisibility(View.GONE);
        recyclerView1.setVisibility(View.VISIBLE);
        layoutAnimation(recyclerView1);

    }


    private void readUser() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference = FirebaseDatabase.getInstance().getReference("Friend").child(userid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String uid=""+snapshot.getRef().getKey();
                    reference.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user=dataSnapshot.getValue(User.class);
                            mUser.add(user);
                            if(mUser.isEmpty()){
                                friendtext.setVisibility(View.VISIBLE);
                            }

                          gridUserFriendAdapter=new GridUserFriendAdapter(getApplicationContext(),mUser);
                            recyclerView.setAdapter(gridUserFriendAdapter);
                            layoutAnimation(recyclerView);
                            userAdapter=new UserAdapter(getApplicationContext(),mUser);
                            recyclerViewList.setAdapter(userAdapter);
                            layoutAnimation(recyclerViewList);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
