package com.example.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat.MainActivity;
import com.example.chat.Post;
import com.example.chat.R;
import com.example.chat.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Send_image extends AppCompatActivity {

    private EditText description;
    private ImageView image_msg;
    private ImageButton btn_send;
    private VideoView videoView;
    ProgressBar progressBar;
    private ProgressDialog dialog;
    MediaController mediaController;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_image1);

        description=findViewById(R.id.description);
        image_msg=findViewById(R.id.image_send);
        btn_send=findViewById(R.id.btn_send);
        progressBar=findViewById(R.id.progressBar7);
        videoView=findViewById(R.id.video_send1);
        //mediaController=new MediaController(this);
        //videoView.setMediaController(mediaController);
        //mediaController.setAnchorView(videoView);
       // mediaController.setMediaPlayer(videoView);
        videoView.setZOrderOnTop(true);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        /*
                         * add media controller
                         */
                        mediaController = new MediaController(Send_image.this);
                        videoView.setMediaController(mediaController);
                        /*
                         * and set its position on screen
                         */
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });


        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        String imageuri_string =bundle.getString("imageuri");
        final String userid=bundle.getString("userid");
        final String filename=bundle.getString("filename");
        final String videouri_string=bundle.getString("videouri");
        final Uri imageuri=Uri.parse(imageuri_string);
        final Uri videouri=Uri.parse(videouri_string);


        if(videouri_string.equals("default")&&(!imageuri_string.equals("default"))) {
            Glide.with(getApplicationContext()).load(imageuri).listener(new RequestListener<Drawable>() {
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
            }).into(image_msg);
        }
        else if((!videouri_string.equals("default"))&&(imageuri_string.equals("default"))){

            image_msg.setVisibility(View.GONE);
            videoView.setVideoURI(videouri);
            videoView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


        }





     btn_send.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             Long timeStamp = System.currentTimeMillis() / 1000;

             if (videouri_string.equals("default")) {
                 dialog = new ProgressDialog(Send_image.this);
                 dialog.setMessage("Sending picture...");
                 dialog.show();


             final StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                     .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                     .child("image_msg").child(filename + Long.toString(timeStamp));

             fileRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                     fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                             Bundle bundle1 = new Bundle();
                             bundle1.putString("message", description.getText().toString());
                             bundle1.putString("imageurl", uri.toString());
                             bundle1.putString("userid", userid);
                             bundle1.putString("videourl","default");
                             intent.putExtras(bundle1);
                             startActivity(intent);
                             finish();


                         }
                     });

                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG).show();
                 }
             });



         } else {
                 dialog = new ProgressDialog(Send_image.this);
                 dialog.setMessage("Sending video...");
                 dialog.show();

                 final StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                         .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                         .child("video_msg").child(Long.toString(timeStamp)+filename);

                 fileRef.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                         fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                 Bundle bundle1 = new Bundle();
                                 bundle1.putString("message", description.getText().toString());
                                 bundle1.putString("videourl", uri.toString());
                                 bundle1.putString("userid", userid);
                                 bundle1.putString("imageurl","default");
                                 intent.putExtras(bundle1);
                                 startActivity(intent);
                                 finish();


                             }
                         });

                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG).show();
                     }
                 });
             }





              }
     });







    }
}