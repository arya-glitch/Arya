package com.example.chat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat.Adapter.CommentAdapter;
import com.example.chat.Adapter.ViewPagerAdapter1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {


    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    private CommentAdapter commentAdapter;
    private List<Comments>mComment;

    private ViewPager2 viewPager;
    WormDotsIndicator dot;
    private TextView caption;
    private CircleImageView profile_pic;
    private TextView profile_name;
    private ImageView lit;
    private ImageView share;
    private ProgressBar progressBar;
    private TextView likes ;
    private  CircleImageView circleImageView;
    private TextView text_send;
   private ImageButton  btn_send;
    private RecyclerView recyclerView;
    private  TextView no_of_comment;
    private ViewPagerAdapter1 viewPagerAdapter;
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);

        videoView=findViewById(R.id.videopost_comment);
        viewPager=findViewById(R.id.viewpager1);
        dot=findViewById(R.id.dot);
        no_of_comment=findViewById(R.id.comments);
        caption=findViewById(R.id.caption);
        profile_pic=findViewById(R.id.profile_image_post);
        profile_name=findViewById(R.id.user_profile_name_post);
        lit=findViewById(R.id.lit);
        share=findViewById(R.id.send_post);
        progressBar=findViewById(R.id.progressBar4);
        likes=findViewById(R.id.likes);
        circleImageView=findViewById(R.id.profile_pic_commentator);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        recyclerView=findViewById(R.id.comment_recylerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);






        Bundle bundle= new Bundle();
        bundle=getIntent().getExtras();
        final String postid=bundle.getString("postid");

        databaseReference=FirebaseDatabase.getInstance().getReference("Post").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final Post post=dataSnapshot.getValue(Post.class);



        caption.setText(post.getCaption());

        if(!post.getImageurl().get(0).equals("default")&&post.getVideourl().equals("default")){
          viewPagerAdapter=new ViewPagerAdapter1(getApplicationContext(),post.getImageurl());
           viewPager.setAdapter(viewPagerAdapter);
            dot.setViewPager2(viewPager);
            dot.setVisibility(View.VISIBLE);
           if(post.getImageurl().size()==1){
               dot.setVisibility(View.GONE);
           }



        }
        else if(post.getImageurl().get(0).equals("default")&&!post.getVideourl().equals("default")){

            videoView.setVideoURI(Uri.parse(post.getVideourl()));
            viewPager.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);


        }


        displayUser(profile_pic,profile_name,post.getUid(),progressBar);

        commentatorUser(circleImageView);




        setLikes(likes,post.getPostid());



        isliked(lit,post.getPostid());







        lit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                databaseReference= FirebaseDatabase.getInstance().getReference("LikedUser")
                        .child(post.getPostid())
                        .child(firebaseUser.getUid());

                int tag=(int)lit.getTag();
                if(tag==R.drawable.lit2){

                    databaseReference.child("isliked").setValue(true);
                }


                else if(tag==R.drawable.lit1){

                    databaseReference.removeValue();


                }

            }
        });




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (text_send.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Cannot Send Empty Comments", Toast.LENGTH_LONG).show();
                } else {

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Comments").child(post.getPostid());

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("sender", firebaseUser.getUid());
                    hashMap.put("receiver", post.getUid());
                    hashMap.put("comment", text_send.getText().toString());
                    Long timeStamp = System.currentTimeMillis() / 1000;
                    hashMap.put("timestamp", timeStamp);

                    String comment_id=reference.push().getKey();

                    hashMap.put("comment_id",comment_id);
                    reference.child(comment_id).setValue(hashMap);

                    text_send.setText("");


                }
            }
        });




        readComment(post.getPostid());

                databaseReference=FirebaseDatabase.getInstance().getReference("Comments").child(post.getPostid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            long n = dataSnapshot.getChildrenCount();
                            no_of_comment.setText(Long.toString(n) + " Comments");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });










    }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void readComment(String postid) {
        mComment=new ArrayList<>();

        databaseReference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComment.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comments comments=snapshot.getValue(Comments.class);
                    mComment.add(comments);
                }


                commentAdapter=new CommentAdapter(getApplicationContext(),mComment);
                recyclerView.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setLikes(final TextView likes, String postid) {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("LikedUser").child(postid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long n = dataSnapshot.getChildrenCount();
                    likes.setText(Long.toString(n)+" Lits");
                }else{
                    likes.setText("0"+" Lits");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void commentatorUser(final CircleImageView circleImageView) {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void displayUser(final CircleImageView profile_pic, final TextView profile_name, final String uid, final ProgressBar progressBar) {
        databaseReference= FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()) {
                User user = dataSnapshot.child(uid).getValue(User.class);

                profile_name.setText(user.getName());
                if (!user.getImageurl().equals("default")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext()).load(user.getImageurl()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void isliked(final ImageView lit, String postid) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("LikedUser").child(postid)
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    Isliked isliked = dataSnapshot.getValue(Isliked.class);
                    if (isliked.getIsliked() == true) {
                        lit.setImageResource(R.drawable.lit1);
                        lit.setTag(R.drawable.lit1);
                    }else{
                        lit.setImageResource(R.drawable.lit2);
                        lit.setTag(R.drawable.lit2);

                    }

                }else{
                    lit.setImageResource(R.drawable.lit2);
                    lit.setTag(R.drawable.lit2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
