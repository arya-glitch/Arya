package com.example.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.MessageAdapter;
import com.example.chat.Interface.APIService;
import com.example.chat.Notification.Client;
import com.example.chat.Notification.Data;
import com.example.chat.Notification.MyResponse;
import com.example.chat.Notification.Sender;
import com.example.chat.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    public static final String KEY_REPLY ="key_reply" ;


    int[]location=new int[2];
    private static final String CHANNEL_ID = "abc" ;
    ImageView profile_image;
    TextView username,status;
    public static FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;
    ImageButton btn_send,btn_attach;
    Chat chat;
    String imageurl="default",message,videourl="default";

    EditText text_send;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;

    String userid;
    APIService apiService;
    boolean notify= false;
    ImageView Back_button;
    AlertDialog dialogBuilder;


    ValueEventListener seenListener;
    private String filename;
    private RelativeLayout relativeLayout;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        clearExistingNotifications();



        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);



        recyclerView=findViewById(R.id.recyler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        relativeLayout=findViewById(R.id.R_chat);
        
        profile_image=findViewById(R.id.profile_image_chat_display);

        username=findViewById(R.id.user_profile_name_chat_display);
        status=(TextView) findViewById(R.id.status_message);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        Back_button=findViewById(R.id.back_button);
        btn_attach=findViewById(R.id.btn_attach);
       Bundle bundle0=new Bundle();
       bundle0=getIntent().getExtras();
       userid=bundle0.getString("userid");
       imageurl=bundle0.getString("imageurl");
       message=bundle0.getString("message");
       videourl=bundle0.getString("videourl");


       if(!imageurl.equals("default")&&(videourl.equals("default"))){
           Long timeStamp1 = System.currentTimeMillis()/1000;
           sendMessage(fuser.getUid(),userid,message,timeStamp1,imageurl,"image");

       }
       else  if(!videourl.equals("default")&&(imageurl.equals("default"))){
           Long timeStamp1 = System.currentTimeMillis()/1000;
           sendMessage(fuser.getUid(),userid,message,timeStamp1,videourl,"video");
       }


        chat=new Chat();





        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String mag=text_send.getText().toString();
                if(!mag.equals("")){
                    Long timeStamp = System.currentTimeMillis()/1000;
                    //String currentTimeStamp = timeStamp.toString();
                    sendMessage(fuser.getUid(),userid,mag,timeStamp,"default","null");
                }else{
                    Toast.makeText(getApplicationContext(),"You cannot send empty messages",Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });






        btn_attach.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                                          , 100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);

              /*  dialogBuilder = new AlertDialog.Builder(MessageActivity.this).create();
                LayoutInflater inflater =(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.attach, null);
                ImageView image = dialogView.findViewById(R.id.image);
                ImageView video = dialogView.findViewById(R.id.video);
                ImageView audio = dialogView.findViewById(R.id.audio);
                ImageView gif = dialogView.findViewById(R.id.gif);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1000);

                    }
                });
                video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);

                    }
                });


                dialogBuilder.setView(dialogView);
                dialogBuilder.show();*/
            }
        });







        recyclerView.setOnTouchListener(new OnSwipeTouchListener(MessageActivity.this) {

            public void onSwipeLeft() {

                Intent intent = new Intent(getApplicationContext(), Camera.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", userid);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });












        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });










        reference= FirebaseDatabase.getInstance().getReference("User").child(userid);
        
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                username.setText(user.getName());
                if(!user.getImageurl().equals("default")){
                   Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);
                }

                status.setText(user.getStatus());
                status.setVisibility(View.VISIBLE);
                readMessages(fuser.getUid(),userid);
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    private  void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciever().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=RESULT_CANCELED) {
            if (requestCode == 1000) {
                switch (resultCode) {
                    case RESULT_OK:
                        Uri imageuri = data.getData();

                        String path = imageuri.toString();

                        filename = path.substring(path.lastIndexOf("%") + 1);

                        opensendmedia(imageuri, filename, "image");


                }
            } else if (requestCode == 1) {

                if (resultCode == RESULT_OK) {

                    Uri videouri = data.getData();
                    String path = videouri.toString();

                    filename = path.substring(path.lastIndexOf("%") + 1);

                    opensendmedia(videouri, filename, "video");
                }

            }
        }else {
            dialogBuilder.dismiss();
        }


    }


    private void opensendmedia(final Uri uri, final String filename,String media) {


                    if(media.equals("image")) {
                        Intent intent = new Intent(getApplicationContext(), Send_image.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("imageuri", uri.toString());
                        bundle.putString("userid", userid);
                        bundle.putString("filename", filename);
                        bundle.putString("videouri","default");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else if(media.equals("video")){
                        Intent intent = new Intent(getApplicationContext(), Send_image.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("videouri", uri.toString());
                        bundle.putString("userid", userid);
                        bundle.putString("filename", filename);
                        bundle.putString("imageuri","default");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

    }





















    public void sendMessage(String sender, final String reciever, String message, long timestamp,String url,String media){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();


        if(media.equals("image")) {

            hashMap.put("sender", sender);
            hashMap.put("reciever", reciever);
            hashMap.put("message", message);
            hashMap.put("isseen", false);
            hashMap.put("timestamp", timestamp);
            hashMap.put("imageurl", url);
            hashMap.put("videourl","default");


        } else if(media.equals("video")){
            hashMap.put("sender", sender);
            hashMap.put("reciever", reciever);
            hashMap.put("message", message);
            hashMap.put("isseen", false);
            hashMap.put("timestamp", timestamp);
            hashMap.put("videourl", url);
            hashMap.put("imageurl","default");

        }else if(media.equals("null")){

            hashMap.put("sender", sender);
            hashMap.put("reciever", reciever);
            hashMap.put("message", message);
            hashMap.put("isseen", false);
            hashMap.put("timestamp", timestamp);
            hashMap.put("imageurl", "default");
            hashMap.put("videourl","default");

        }

        reference.child("Chats").push().setValue(hashMap);







        final  String msg =message;
        reference= FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                if(notify) {
                    sendNotification(reciever, user.getName(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }










    private void sendNotification(String reciever, final String username, final String message) {

        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query query =tokens.orderByKey().equalTo(reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token=snapshot.getValue(Token.class);

                    Data data =new Data(fuser.getUid(),R.drawable.logo2,username+ ": " +message,"You have a new message",userid);
                    Sender sender =new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if (response.body().success!=1){
                                            Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readMessages(final String myid, final String userid){
        mChat=new ArrayList<>();


        reference =FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if((chat.getReciever().equals(myid)&& chat.getSender().equals(userid))||
                            (chat.getReciever().equals(userid)&&chat.getSender().equals(myid))){
                        mChat.add(chat);

                    }
                    messageAdapter=new MessageAdapter(getApplicationContext(),mChat,userid);

                            recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private  void status(String status){
        fuser=FirebaseAuth.getInstance().getCurrentUser();


        reference= FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        seenMessage(userid);
        status("(online)");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("(offline)");
    }




    private void clearExistingNotifications() {
        int notificationId = getIntent().getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }



}



