package com.example.chat.Adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Profile;
import com.example.chat.R;
import com.example.chat.SquareImageView;
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

public class GridUserFriendAdapter extends RecyclerView.Adapter<GridUserFriendAdapter.ViewHolder> {

    Context mContext;
    List<User> mUser;
    DatabaseReference databaseReference, databaseReference1;
    FirebaseUser firebaseUser;

    public GridUserFriendAdapter(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.griduserfriend, parent, false);
        return new GridUserFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        final User user1 = mUser.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Profile.class);
                intent.putExtra("userId", user1.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });


        if (user1.getName().length() > 9) {
            String name = user1.getName().substring(0, 9);
            name = name + "...";
            holder.username.setText(name);

        } else {
            holder.username.setText(user1.getName());
        }
        if (!user1.getImageurl().equals("default")) {
            Glide.with(mContext).load(user1.getImageurl()).into(holder.picture);

        }


    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SquareImageView picture;
        TextView username;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.imagepost_grid);
            username = itemView.findViewById(R.id.username_grid);

            Typeface typeface= ResourcesCompat.getFont(mContext,R.font.happy_monkey);
            username.setTypeface(typeface);


        }
    }
}
