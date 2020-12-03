package com.example.chat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat.CommentActivity;
import com.example.chat.Comments;
import com.example.chat.FragmentMain;
import com.example.chat.Isliked;
import com.example.chat.MainActivity;
import com.example.chat.Post;
import com.example.chat.Profile;
import com.example.chat.R;
import com.example.chat.StartActivity;
import com.example.chat.User;
import com.example.chat.User_Who_Liked;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>  {
    private Context mcontext;
    private List<Post> mPost;
    FirebaseUser firebaseUser;

    DatabaseReference databaseReference,databaseReference1;



    public PostAdapter(Context mcontext, List<Post> mPost){
        setHasStableIds(true);
        this.mcontext = mcontext;
        this.mPost=mPost;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mcontext).inflate(R.layout.image_post1,parent,false);
        return new PostAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
       // holder.setIsRecyclable(false);


        final Post post =mPost.get(position);


        holder.caption.setText(post.getCaption());

        if(!post.getImageurl().get(0).equals("default")&&post.getVideourl().equals("default")){



            holder.progressBar.setVisibility(View.GONE);

            holder.viewPagerAdapter=new ViewPagerAdapter1(mcontext,post.getImageurl());
            holder.viewPager.setAdapter(holder.viewPagerAdapter);

            holder.dot.setViewPager2(holder.viewPager);
            if(post.getImageurl().size()==1){
                holder.dot.setVisibility(View.GONE);
            }else {
                holder.dot.setVisibility(View.VISIBLE);
            }



        }
        else if(post.getImageurl().get(0).equals("default")&&!post.getVideourl().equals("default")){

            holder.pos=holder.videoView.getCurrentPosition();
            holder.videoView.setVideoURI(Uri.parse(post.getVideourl()));
            holder.viewPager.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
            holder.videoView.seekTo(1);
            holder.playbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    video_action(holder.pos,holder.videoView,holder.playbtn);
                }
            });
            if(holder.videoView.getVisibility()==View.VISIBLE){
                if(holder.videoView.isPlaying()){
                    holder.playbtn.setVisibility(View.GONE);
                }else {
                    holder.playbtn.setVisibility(View.VISIBLE);
                }
            }


            holder.videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.videoView.isPlaying()) {
                        holder.videoView.pause();
                        holder.playbtn.setVisibility(View.VISIBLE);
                    }else {
                        video_action(holder.pos,holder.videoView,holder.playbtn);
                    }
                }


            });

            holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    holder.playbtn.setVisibility(View.VISIBLE);
                }
            });
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
                    float scaleX = videoRatio / screenRatio;
                    if (scaleX >= 1f) {
                        holder.videoView.setScaleX(scaleX);
                    } else {
                        holder.videoView.setScaleY(1f / scaleX);
                    }
                }
            });



        }



        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mcontext, holder.menu);
                //Inflating the Popup using xml file  
                popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(mcontext,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }

        });





        displayUser(holder.profile_pic,holder.profile_name,post.getUid());

        holder.profile_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(mcontext, Profile.class);
                    intent.putExtra("userId", post.getUid());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }
            }
        });

        holder.profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(mcontext, Profile.class);
                    intent.putExtra("userId", post.getUid());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }




            }
        });



        setLikes(holder.likes,post.getPostid());



        isliked(holder.lit,post.getPostid());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mcontext, CommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("postid",post.getPostid());

                intent.putExtras(bundle);
                mcontext.startActivity(intent);
            }
        });

        holder.comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mcontext, CommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("postid",post.getPostid());

                intent.putExtras(bundle);
                mcontext.startActivity(intent);
            }
        });



        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mcontext, User_Who_Liked.class);
                Bundle bundle=new Bundle();
                bundle.putString("postid",post.getPostid());

                intent.putExtras(bundle);
                mcontext.startActivity(intent);
            }
        });





        databaseReference=FirebaseDatabase.getInstance().getReference("Comments").child(post.getPostid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    long n = dataSnapshot.getChildrenCount();
                    holder.comment_text.setText(Long.toString(n) + " Comments");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        holder.lit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                databaseReference=FirebaseDatabase.getInstance().getReference("LikedUser")
                        .child(post.getPostid())
                        .child(firebaseUser.getUid());

                int tag=(int)holder.lit.getTag();
                if(tag==R.drawable.lit){

                    databaseReference.child("isliked").setValue(true);
                }


                else if(tag==R.drawable.lit1){

                    databaseReference.removeValue();


                }

            }
        });

    }



    private void video_action(int pos,VideoView videoView,ImageView playbtn) {
        if(pos==0||pos==1){
            videoView.start();
            playbtn.setVisibility(View.GONE);
        }else {
            videoView.resume();
            playbtn.setVisibility(View.GONE);
        }
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

    private void displayUser(final CircleImageView profile_pic, final TextView profile_name, final String uid) {
        databaseReference= FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()) {
                User user = dataSnapshot.child(uid).getValue(User.class);

                profile_name.setText(user.getName());
                if (!user.getImageurl().equals("default")) {
                    float density = mcontext.getResources().getDisplayMetrics().density;
                    int px = (int) (1 * density);
                    profile_pic.setPadding(px,px,px,px);

                    Glide.with(mcontext).load(user.getImageurl()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

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
                        lit.setImageResource(R.drawable.lit);
                        lit.setTag(R.drawable.lit);

                    }

                }else{
                  lit.setImageResource(R.drawable.lit);
                  lit.setTag(R.drawable.lit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ViewPager2 viewPager;
        WormDotsIndicator dot;
        ViewPagerAdapter1 viewPagerAdapter;
        private TextView caption;
        private CircleImageView profile_pic;
        private TextView profile_name;
        private ImageView lit;
        private ImageView comment,menu;
        private ImageView share;
        private ProgressBar progressBar;
        private TextView likes ;
        private TextView comment_text ;
        private VideoView videoView;
        private int pos;
        private ImageView playbtn;

       MediaController mediaController;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewPager=itemView.findViewById(R.id.viewpager1);
            dot=itemView.findViewById(R.id.dot);
            playbtn=itemView.findViewById(R.id.play);
            caption=itemView.findViewById(R.id.caption);
            profile_pic=itemView.findViewById(R.id.profile_image_post);
            profile_name=itemView.findViewById(R.id.user_profile_name_post);
            lit=itemView.findViewById(R.id.lit);
            comment=itemView.findViewById(R.id.comment);
            share=itemView.findViewById(R.id.send_post);
            progressBar=itemView.findViewById(R.id.progressBar4);
            likes=itemView.findViewById(R.id.likes);
            comment_text=itemView.findViewById(R.id.comments);
            videoView=itemView.findViewById(R.id.post_video);
            menu=itemView.findViewById(R.id.postmenu);





           // videoView.start();
            Typeface typeface = ResourcesCompat.getFont(mcontext,R.font.satisfy);
            caption.setTypeface(typeface);


        }
    }
}





