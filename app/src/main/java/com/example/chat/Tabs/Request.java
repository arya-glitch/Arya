package com.example.chat.Tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.UserFriendAdapter;
import com.example.chat.Adapter.UserRequestAdapter;
import com.example.chat.R;
import com.example.chat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Request extends Fragment {

    private List<User>mUsers;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    UserRequestAdapter userRequestAdapter;
    TextView requesttext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.request_tab, container, false);

        recyclerView=view.findViewById(R.id.request_list);
        requesttext=view.findViewById(R.id.request_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        readRequest();

        return view;
    }




    private void readRequest() {
        mUsers=new ArrayList<>();

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference1;
        databaseReference1= FirebaseDatabase.getInstance().getReference("User");
        databaseReference= FirebaseDatabase.getInstance().getReference("Request").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();

                    if(dataSnapshot.getChildrenCount()>0) {
                        requesttext.setText(Long.toString(dataSnapshot.getChildrenCount())+" "+"Requests");
                    }
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userid = ""+snapshot.getRef().getKey();
                        databaseReference1.child(userid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User user = dataSnapshot.getValue(User.class);
                                mUsers.add(user);
                                userRequestAdapter = new UserRequestAdapter(getContext(), mUsers);
                                recyclerView.setAdapter(userRequestAdapter);


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
