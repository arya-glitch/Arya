package com.example.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Chat;
import com.example.chat.MessageActivity;
import com.example.chat.Notification.Data;
import com.example.chat.Profile;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {



    private Context mcontext;
    private List<User>mUsers;
    String theLastmessage;
    FirebaseUser fUser;
    ColorStateList defaultColor;

    DatabaseReference databaseReference;

    public UserChatAdapter(Context mcontext, List<User> mUsers){
        setHasStableIds(true);
        this.mUsers=mUsers;
        this.mcontext=mcontext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_display_layout_chat,parent,false);
        return new UserChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final User user =mUsers.get(position);
        holder.username.setText(user.getName());
        if(!user.getImageurl().equals("default")){
            Glide.with(mcontext).load(user.getImageurl()).into(holder.profileimage);

        }


        defaultColor=holder.time.getTextColors();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(mcontext, MessageActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("userid",user.getUid());
                    bundle.putString("imageurl", "default");
                    bundle.putString("videourl","default");
                    bundle.putString("message", "");
                    intent.putExtras(bundle);
                    mcontext.startActivity(intent);

            }
        });

        lastmessage(user.getUid(),holder.last_msg,holder.seen,holder.delivered,holder.bluedot,holder.time);

        fUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1=dataSnapshot.getValue(User.class);
                if(user1.getStatus().equals("(online)")){
                    holder.greendot.setVisibility(View.VISIBLE);
                }else {
                    holder.greendot.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public CircleImageView profileimage;
        private TextView last_msg,time;
        ImageView seen,delivered,bluedot,greendot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.user_profile_name_chat);
            profileimage=itemView.findViewById(R.id.profile_image_chat);
            seen=itemView.findViewById(R.id.seen_icon);
            delivered=itemView.findViewById(R.id.delivered);
            bluedot=itemView.findViewById(R.id.bluedot);
            greendot=itemView.findViewById(R.id.greenDot);
            last_msg=itemView.findViewById(R.id.last_msg);
            time=itemView.findViewById(R.id.time_chat);
        }


    }

    private void lastmessage(final String userid, final TextView last_msg, final ImageView seen, final ImageView delivered
    , final ImageView bluedot, final TextView time){
        theLastmessage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat= snapshot.getValue(Chat.class);
                    if(chat.getReciever().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)){
                        theLastmessage=chat.getMessage();
                        seen.setVisibility(View.GONE);
                        delivered.setVisibility(View.GONE);
                        if(chat.getIsseen()){
                            bluedot.setVisibility(View.GONE);
                            time.setTextColor(defaultColor);
                            last_msg.setTextColor(defaultColor);
                        }else {
                            bluedot.setVisibility(View.VISIBLE);
                            time.setTextColor(mcontext.getResources().getColor(R.color.AppColor));
                            last_msg.setTextColor(mcontext.getResources().getColor(R.color.AppColor));
                        }

                    }else if(chat.getReciever().equals(userid)&&chat.getSender().equals(firebaseUser.getUid())){
                        theLastmessage=chat.getMessage();
                        if (chat.getIsseen()) {
                            seen.setVisibility(View.VISIBLE);
                            delivered.setVisibility(View.GONE);

                        } else {
                            seen.setVisibility(View.GONE);
                            delivered.setVisibility(View.VISIBLE);
                        }
                    }
                }

                switch (theLastmessage){
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastmessage);
                        break;
                }
                theLastmessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
