package com.example.chat.Tabs;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.GridUserAdapter;
import com.example.chat.Adapter.UserFriendAdapter;
import com.example.chat.R;
import com.example.chat.User;
import com.example.chat.User1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFriend1 extends Fragment  {

    private RecyclerView recyclerView;
    private GridUserAdapter gridUserAdapter;
    private List<User1> mUsers;
    private List<String>friendList;
    boolean check;

    DatabaseReference databaseReference;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.add_friend_tab, container, false);

        recyclerView=view.findViewById(R.id.userList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setHasFixedSize(true);



        mUsers=new ArrayList<>();

        checkFriendList();


        return view;
    }

    private void layoutAnimation(RecyclerView recyclerView){

        Context context=recyclerView.getContext();
        LayoutAnimationController layoutAnimationController=
                AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_right_to_left);

        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void checkFriendList() {
        friendList=new ArrayList<>();
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        friendList.add("" + snapshot.getRef().getKey());
                    }
                   friendList.add(firebaseUser.getUid());

                    readUser(friendList);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void readUser(final List<String>friendList) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    User1 user1=new User1(user.getName(),user.getEmail(),user.getUid(),user.getStatus()
                            ,user.getImageurl(),user.getSearch());
                    for(String id:friendList){
                        if(user1.getUid().equals(id)){
                            check=false;
                            break;
                        }else {
                            check=true;
                        }
                    }
                    if(check==true){
                        user1.setIsfriend(false);
                        mUsers.add(user1);
                    }


                }

                gridUserAdapter=new GridUserAdapter(getContext(),mUsers);
                recyclerView.setAdapter(gridUserAdapter);
                layoutAnimation(recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}



