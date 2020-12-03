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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.ViewHolder> {

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    private Context mcontext;
    private List<User> mUsers;
    private boolean isrequest;

    public UserRequestAdapter(Context mcontext, List<User> mUsers) {
        setHasStableIds(true);
        this.mcontext = mcontext;
        this.mUsers = mUsers;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_display_layout_request,parent,false);
        return new UserRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

       // holder.setIsRecyclable(false);

        final User user =mUsers.get(position);


        holder.username.setText(user.getName());
        if(!user.getImageurl().equals("default")){
            Glide.with(mcontext).load(user.getImageurl()).into(holder.profileimage);

        }

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, Profile.class);
                intent.putExtra("userId", user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);
            }
        });


            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference= FirebaseDatabase.getInstance().getReference("Friend")
                            .child(firebaseUser.getUid()).child(user.getUid()).child("isfriend");
                    databaseReference.setValue(true);
                    databaseReference= FirebaseDatabase.getInstance().getReference("Friend").child(user.getUid())
                            .child(firebaseUser.getUid()).child("isfriend");
                    databaseReference.setValue(true);
                    holder.accept.setVisibility(View.GONE);
                    holder.cancel.setVisibility(View.GONE);
                    holder.check.setVisibility(View.VISIBLE);
                    holder.friend.setVisibility(View.VISIBLE);

                    databaseReference= FirebaseDatabase.getInstance().getReference("Request")
                            .child(firebaseUser.getUid()).child(user.getUid());
                    databaseReference.removeValue();


                }
            });

            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference= FirebaseDatabase.getInstance().getReference("Request")
                            .child(firebaseUser.getUid()).child(user.getUid());
                    databaseReference.removeValue();
                }
            });
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
        private TextView username,friend;
        private CircleImageView profileimage;
        private Button accept;
        private ImageView cancel,check;






        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name_friend);
            profileimage=itemView.findViewById(R.id.profile_image_friend);
            accept=itemView.findViewById(R.id.accept_btn);
            cancel=itemView.findViewById(R.id.decline);
            check=itemView.findViewById(R.id.check_user_display_layout_request);
            friend=itemView.findViewById(R.id.friend_user_display_layout_request);

        }
    }
}