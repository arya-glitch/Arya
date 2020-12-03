package com.example.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.mbms.StreamingServiceInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat.Chat;
import com.example.chat.MessageActivity;
import com.example.chat.R;
import com.example.chat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public  String uid="null";


    public static final  int MSG_TYPE_LEFT =0;
    public static final  int MSG_TYPE_RIGHT =1;
    private Context mcontext;
    private List<Chat> mChat;
    private DatabaseReference databaseReference;
    private FirebaseUser fuser;

    public MessageAdapter(Context mcontext,List<Chat> mChat,String uid){
        setHasStableIds(true);
        this.mChat=mChat;
        this.mcontext=mcontext;
        this.uid=uid;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view=LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }else {
            View view=LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);

        }


    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        Chat chat = mChat.get(position);

        if (!chat.getImageurl().equals("default")&&(chat.getVideourl().equals("default"))) {
            holder.show_message.setVisibility(View.GONE);

            if(!chat.getMessage().isEmpty()) {
                holder.image_mssg_text.setText(chat.getMessage());
                holder.image_mssg_text.setVisibility(View.VISIBLE);
            }


            holder.progressBar6.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(chat.getImageurl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                   holder.progressBar6.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar6.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.image_mssg);

            holder.image_mssg.setVisibility(View.VISIBLE);

        }else if(!chat.getVideourl().equals("default")&&(chat.getImageurl().equals("default"))){

            holder.show_message.setVisibility(View.GONE);

            if(!chat.getMessage().isEmpty()) {
                holder.image_mssg_text.setText(chat.getMessage());
                holder.image_mssg_text.setVisibility(View.VISIBLE);
            }


            holder.progressBar6.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(chat.getVideourl()));
            holder.videoView.setVisibility(View.VISIBLE);
        }
        else{
        holder.show_message.setText(chat.getMessage());
    }

        if(uid!=null) {

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User").child(uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user1 = dataSnapshot.getValue(User.class);
                        if (user1 != null) {
                            if (!user1.getImageurl().equals("default")) {
                                Glide.with(getApplicationContext()).load(user1.getImageurl()).into(holder.profileimage);

                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Cant load profile image",Toast.LENGTH_LONG).show();
                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        ManageSeen(position,holder.seen,holder.delivered,chat);



        long previousts=0;
        if(position>=1){
            Chat oldchat=mChat.get(position-1);
            previousts=oldchat.getTimestamp();
        }

        setTime(chat.getTimestamp(),previousts,holder.time);

    }

    private void setTime(long timestamp, long previousts, TextView time) {

        if(previousts==0){
            time.setVisibility(View.VISIBLE);
            time.setText(getDate(toDate(timestamp)));

        }else {

            boolean sameDay=toDate(timestamp).equals(toDate(previousts));
            if(sameDay){
                time.setVisibility(View.GONE);
            }else {
                time.setVisibility(View.VISIBLE);
                time.setText(getDate(toDate(timestamp)));
            }
        }
    }
    private String toDate(long timestamp) {
        Date date = new Date(timestamp * 1000);
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }


    private String getDate(String string){
        String date = null;
        String month;
        String day=string.substring(0,2);
        String month_number=string.substring(3,5);
        String year= string.substring(6,10);

        Long today=System.currentTimeMillis()/1000;
        String todayDate=toDate(today);
        if(todayDate.equals(string)){
            return "Today";

        }else {

            switch (month_number) {

                case "01":
                    month = "Jan";
                    break;
                case "02":
                    month = "Feb";
                    break;
                case "03":
                    month = "March";
                    break;
                case "04":
                    month = "April";
                    break;
                case "05":
                    month = "May";
                    break;
                case "06":
                    month = "June";
                    break;
                case "07":
                    month = "July";
                    break;
                case "08":
                    month = "August";
                    break;
                case "09":
                    month = "Sep";
                    break;
                case "10":
                    month = "Oct";
                    break;
                case "11":
                    month = "Nov";
                    break;
                case "12":
                    month = "Dec";
                    break;
                default:
                    month=null;
                    break;
            }

            date = day + " " + month + " " + year;

            return date;
        }

    }

    private void ManageSeen(int position, final ImageView seen, final ImageView delivered, final Chat chat) {

        fuser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());



        if(position==mChat.size()-1){
            if(getItemViewType(position)==MSG_TYPE_RIGHT) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        if(user.getStatus().equals("(online)")){
                            if (chat.getIsseen()) {
                                seen.setVisibility(View.VISIBLE);
                                delivered.setVisibility(View.GONE);

                            } else {
                                seen.setVisibility(View.GONE);
                                delivered.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }

        else{
           seen.setVisibility(View.GONE);
           delivered.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView show_message;
        ImageView delivered,seen;
        private CircleImageView profileimage;
        private ImageView image_mssg;
        private TextView image_mssg_text,time;
        private ProgressBar progressBar6;
        private VideoView videoView;
        MediaController mediaController;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message=itemView.findViewById(R.id.show_message);
            profileimage=itemView.findViewById(R.id.profile_image_chat_item_left);
            seen=itemView.findViewById(R.id.seen_icon);
            delivered=itemView.findViewById(R.id.delivered);

            image_mssg=itemView.findViewById(R.id.image_message);
            image_mssg_text=itemView.findViewById(R.id.image_message_text);
            progressBar6=itemView.findViewById(R.id.progressBar6);
            videoView=itemView.findViewById(R.id.video_message);
            mediaController=new MediaController(mcontext);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
            time=itemView.findViewById(R.id.time_msg);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                            progressBar6.setVisibility(View.GONE);
                            mp.start();
                        }
                    });



                    Typeface typeface = ResourcesCompat.getFont(mcontext,R.font.happy_monkey);
                    show_message.setTypeface(typeface);
                    time.setTypeface(typeface);

                }
            });


        }


    }

    @Override
    public int getItemViewType(int position){
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return  MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }



}
