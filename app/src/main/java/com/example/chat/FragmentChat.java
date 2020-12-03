package com.example.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.GridUserFriendAdapter;
import com.example.chat.Adapter.OnlineUserAdapter;
import com.example.chat.Adapter.UserAdapter;
import com.example.chat.Adapter.UserChatAdapter;
import com.example.chat.Interface.OnBackPressedListener;
import com.example.chat.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentChat extends Fragment {


    private RecyclerView recyclerView,recyclerView_online_users;
    OnlineUserAdapter onlineUserAdapter;
    DatabaseReference databaseReference;
    private UserChatAdapter userChatAdapter;
    private List<User> mUsers,mUsers1;
    FirebaseUser fuser;
     DatabaseReference reference;
     FirebaseAuth mAuth;
     private List<String> usersList;
     CircleImageView chatdp,greendot;
     RelativeLayout Rstatus;
     List<String> frindList;
     RelativeLayout relativeLayout;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        final Activity activity=getActivity();

        recyclerView=view.findViewById(R.id.recyler_view);

        chatdp=view.findViewById(R.id.chatdp);
        Rstatus=view.findViewById(R.id.Rstatus);
        greendot=view.findViewById(R.id.greenDot);
        relativeLayout=view.findViewById(R.id.chat_text);
        recyclerView_online_users=view.findViewById(R.id.recyclerView_online_users);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView_online_users.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fuser=FirebaseAuth.getInstance().getCurrentUser();

        usersList=new ArrayList<>();
        mUsers=new ArrayList<>();
        mUsers1=new ArrayList<>();
        frindList=new ArrayList<>();
        readOnlineUser();
        readUser();
        showStatus();


        updateUserList();

        updateToken(FirebaseInstanceId.getInstance().getToken());



        return view;
    }


    private void showStatus() {

        Rstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(getContext(),Rstatus);
                popupMenu.getMenuInflater().inflate(R.menu.chatmenu,popupMenu.getMenu());
                popupMenu.show();

            }
        });

    }

    private void readUser() {

        fuser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    User user=dataSnapshot.getValue(User.class);
                    if(!user.getImageurl().equals("default")){
                        if(getActivity()==null){
                            return;
                        }
                        Glide.with(getContext()).load(user.getImageurl()).into(chatdp);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readOnlineUser() {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                frindList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = "" + snapshot.getRef().getKey();
                    frindList.add(uid);
                }
                showOnlineUser(frindList);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showOnlineUser(final List<String> frindList) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User");

        reference.orderByChild("status").equalTo("(online)").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers1.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    for(String uid: frindList){
                        if(user.getUid().equals(uid)){
                            mUsers1.add(user);
                        }
                    }
                }
                onlineUserAdapter = new OnlineUserAdapter(getContext(), mUsers1);
                recyclerView_online_users.setAdapter(onlineUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void layoutAnimationRtL(RecyclerView recyclerView){

        Context context=recyclerView.getContext();
        LayoutAnimationController layoutAnimationController=
                AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_right_to_left);

        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    private void updateUserList() {

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat= snapshot.getValue(Chat.class);
                    if(chat.getSender().equals(fuser.getUid())){
                        usersList.add(chat.getReciever());
                    }
                    if(chat.getReciever().equals(fuser.getUid())){
                        usersList.add(chat.getSender());
                    }


                }

                for (int i = 0; i < usersList.size(); i++) {
                    for (int j =i+ 1; j < usersList.size(); j++) {
                        if (usersList.get(i).equals( usersList.get(j))) {
                            usersList.remove(j);
                            j--;
                        }
                    }
                }
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Cannot add chat list",Toast.LENGTH_LONG).show();

            }
        });

    }


    private void layoutAnimation(RecyclerView recyclerView){

        Context context=recyclerView.getContext();
        LayoutAnimationController layoutAnimationController=
                AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_down_to_up);

        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void updateToken(String token){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }


    private void readChat() {

        reference=FirebaseDatabase.getInstance().getReference("User");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (String id : usersList){
                        if(user.getUid().equals(id)){
                            mUsers.add(user);
                        }
                    }
                }

                /*for (int i = 0; i < mUsers.size(); i++) {
                    for (int j = i+1; j < mUsers.size(); j++) {
                        if (mUsers.get(i) == mUsers.get(j)) {
                            mUsers.remove(j);
                            j--;
                        }
                    }
                }*/
                if (mUsers.size()==0){
                    relativeLayout.setVisibility(View.VISIBLE);
                }else{
                    relativeLayout.setVisibility(View.GONE);
                }
                userChatAdapter=new UserChatAdapter(getContext(),mUsers);
                recyclerView.setAdapter(userChatAdapter);
                layoutAnimation(recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Failed to load chats",Toast.LENGTH_LONG).show();

            }
        });

    }
}
