package com.example.chat.Adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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

public class GridUserAdapter extends RecyclerView.Adapter<GridUserAdapter.ViewHolder> {

    Context mContext;
    List<User1> mUser;
    DatabaseReference databaseReference,databaseReference1;
    FirebaseUser firebaseUser;

    public GridUserAdapter(Context mContext, List<User1> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    static final DecelerateInterpolator DECELERATE_INTERPOLATOR=new DecelerateInterpolator();
    static final AccelerateInterpolator ACCELERATE_INTERPOLATOR=new AccelerateInterpolator();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.griduser,parent,false);
        return new GridUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User1 user1=mUser.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Profile.class);
                intent.putExtra("userId", user1.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        handleVisibility(holder.add,holder.added,user1.getUid());
        if(user1.getName().length()>9){
            String name= user1.getName().substring(0,9);
            name=name+"...";
            holder.username.setText(name);

        }else {
            holder.username.setText(user1.getName());
        }
        if(!user1.getImageurl().equals("default")){
            Glide.with(mContext).load(user1.getImageurl()).into(holder.picture);

        }

        final GestureDetector gd=new GestureDetector(mContext,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                heart(holder.circle_bg,holder.heart);
                addfriend(holder.add,holder.added,user1.getUid());

                handleVisibility(holder.add,holder.added,user1.getUid());

                return true;
            }



            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Intent intent = new Intent(mContext, Profile.class);
                intent.putExtra("userId", user1.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        final GestureDetector gd1=new GestureDetector(mContext,new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                heart(holder.circle_bg,holder.heart);
                addfriend(holder.add,holder.added,user1.getUid());

                handleVisibility(holder.add,holder.added,user1.getUid());
                return true;
            }
        });

        holder.touchlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });

        holder.RaddPerson.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd1.onTouchEvent(event);
            }
        });

    }

    private void handleVisibility(final ImageView add, final ImageView added, String userid) {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Request")
                .child(userid).child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    added.setVisibility(View.VISIBLE);
                    add.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addfriend(final ImageView add, final ImageView added, final String userid) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Request").child(firebaseUser.getUid())
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

                }
                else  {
                    databaseReference1 = FirebaseDatabase.getInstance().getReference("Request")
                            .child(userid).child(firebaseUser.getUid());
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Toast.makeText(mContext,"Requested Already",Toast.LENGTH_SHORT).show();
                            }else {
                                databaseReference1.child("isrequested").setValue(true);
                                added.setVisibility(View.VISIBLE);
                                add.setVisibility(View.GONE);
                                Toast.makeText(mContext,"Requested",Toast.LENGTH_SHORT).show();
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

    }


    private void heart(final View circle_bg, final ImageView heart) {
        circle_bg.setVisibility(View.VISIBLE);
        heart.setVisibility(View.VISIBLE);

        circle_bg.setScaleY(0.1f);
        circle_bg.setScaleX(0.1f);
        circle_bg.setAlpha(1f);
        heart.setScaleY(0.1f);
        heart.setScaleX(0.1f);


        AnimatorSet animatorSet=new AnimatorSet();

        ObjectAnimator bgScaleYAnim=ObjectAnimator.ofFloat(circle_bg,"scaleY",0.1f,1f);
        bgScaleYAnim.setDuration(500);

        bgScaleYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim=ObjectAnimator.ofFloat(circle_bg,"scaleX",0.1f,1f);
        bgScaleXAnim.setDuration(500);
        bgScaleXAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator bgAlphaAnim=ObjectAnimator.ofFloat(circle_bg,"alpha",1f,0f);
        bgAlphaAnim.setDuration(500);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim=ObjectAnimator.ofFloat(heart,"scaleY",0.1f,1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim=ObjectAnimator.ofFloat(heart,"scaleX",0.1f,1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECELERATE_INTERPOLATOR);


        ObjectAnimator imgScaleDownYAnim=ObjectAnimator.ofFloat(heart,"scaleY",1f,0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim=ObjectAnimator.ofFloat(heart,"scaleX",1f,0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);


        animatorSet.playTogether(bgScaleYAnim,bgScaleXAnim,bgAlphaAnim,imgScaleUpYAnim,imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reset(circle_bg,heart);
            }
        });
        animatorSet.start();


    }

    private void reset(View circle_bg, ImageView heart) {

        circle_bg.setVisibility(View.GONE);
        heart.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView heart,added,add;
        SquareImageView picture;
        View circle_bg;
        TextView username;
        FrameLayout touchlayout;
        RelativeLayout RaddPerson;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture=itemView.findViewById(R.id.imagepost_grid);
            heart=itemView.findViewById(R.id.heart);
            username=itemView.findViewById(R.id.username_grid);
            circle_bg=itemView.findViewById(R.id.circlebg);
            touchlayout=itemView.findViewById(R.id.touchlayout);
            add=itemView.findViewById(R.id.addperson1);
            added=itemView.findViewById(R.id.addperson2);
            RaddPerson=itemView.findViewById(R.id.R_addperson);
        }
    }
}
