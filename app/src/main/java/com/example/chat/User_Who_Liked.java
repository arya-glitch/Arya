package com.example.chat;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class User_Who_Liked extends AppCompatActivity {
    private TextView people;
    private RecyclerView recyclerView;
    UserAdapter userAdapter;
    DatabaseReference databaseReference,reference;
    private List<User>mUser;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_who_liked);

        people=findViewById(R.id.people_lit);
        recyclerView=findViewById(R.id.recyclerView_users_who_liked);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mUser=new ArrayList<>();

        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        final String postid=bundle.getString("postid");

        databaseReference= FirebaseDatabase.getInstance().getReference("LikedUser").child(postid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                long count = dataSnapshot.getChildrenCount();
                people.setText(Long.toString(count) + " person gave you a lit");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String uid = ""+dataSnapshot1.getRef().getKey();


                    reference = FirebaseDatabase.getInstance().getReference("User").child(uid);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                mUser.add(user);
                            //}

                            userAdapter =new UserAdapter(getApplicationContext(),mUser);
                            recyclerView.setAdapter(userAdapter);
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
