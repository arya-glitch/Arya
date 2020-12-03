package com.example.chat.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Profile;
import com.example.chat.R;
import com.example.chat.User;
import com.example.chat.User1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendAdapter extends RecyclerView.Adapter<UserFriendAdapter.ViewHolder> {

    DatabaseReference databaseReference,databaseReference1;
    FirebaseUser firebaseUser;

    private Context mcontext;
    private List<User1> mUsers;

    public UserFriendAdapter(Context mcontext, List<User1> mUsers) {
        setHasStableIds(true);
        this.mcontext = mcontext;
        this.mUsers = mUsers;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout_friend,parent,false);
        return new UserFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);


        final User1 user =mUsers.get(position);

        holder.username.setText(user.getName());
        if(!user.getImageurl().equals("default")){
            Glide.with(mcontext).load(user.getImageurl()).into(holder.profileimage);

        }

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        mcontext=holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, Profile.class);
                intent.putExtra("userId", user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);
            }
        });


            if(user.isIsfriend()==true){
                holder.addfriend.setVisibility(View.GONE);
                holder.check.setVisibility(View.VISIBLE);
                holder.friend.setVisibility(View.VISIBLE);
            }
            else {
                databaseReference1 = FirebaseDatabase.getInstance().getReference("Request")
                       .child(user.getUid()).child(firebaseUser.getUid());
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            holder.addedfriend.setVisibility(View.VISIBLE);
                            holder.addfriend.setVisibility(View.GONE);
                        }else {
                            databaseReference=FirebaseDatabase.getInstance().getReference("Friend")
                                    .child(firebaseUser.getUid()).child(user.getUid());
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        holder.check.setVisibility(View.VISIBLE);
                                        holder.friend.setVisibility(View.VISIBLE);
                                        holder.addfriend.setVisibility(View.GONE);
                                    }else {
                                        holder.addfriend.setVisibility(View.VISIBLE);
                                    }
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

                holder.addfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.addfriend.setVisibility(View.GONE);

                        databaseReference=FirebaseDatabase.getInstance().getReference("Request").child(firebaseUser.getUid())
                                .child(user.getUid());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    databaseReference1=FirebaseDatabase.getInstance().getReference("Friend")
                                            .child(firebaseUser.getUid()).child(user.getUid()).child("isfriend");
                                    databaseReference1.setValue(true);
                                    databaseReference1=FirebaseDatabase.getInstance().getReference("Friend")
                                            .child(user.getUid()).child(firebaseUser.getUid()).child("isfriend");
                                    databaseReference1.setValue(true);
                                    holder.friend.setVisibility(View.VISIBLE);
                                    holder.check.setVisibility(View.VISIBLE);
                                    holder.addedfriend.setVisibility(View.GONE);
                                   databaseReference.removeValue();
                                }
                                else  {
                                    databaseReference1 = FirebaseDatabase.getInstance().getReference("Request")
                                            .child(user.getUid()).child(firebaseUser.getUid()).child("isrequested");
                                    databaseReference1.setValue(true);
                                    holder.addedfriend.setVisibility(View.VISIBLE);
                                    holder.addfriend.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                });

        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public int getItemViewType(int position){
      return position;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username,friend,addfriend,addedfriend;
        private CircleImageView profileimage;
        private ImageView check;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name_friend);
            profileimage=itemView.findViewById(R.id.profile_image_friend);
            addfriend=itemView.findViewById(R.id.add_friend);
            addedfriend=itemView.findViewById(R.id.added_friend);
            check=itemView.findViewById(R.id.check_user_display_layout_friend);
            friend=itemView.findViewById(R.id.friend_user_display_layout_friend);

        }
    }
}