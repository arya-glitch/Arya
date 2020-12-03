package com.example.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.ViewPagerAdapter1;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostOption extends AppCompatActivity {

    int i=1,k=0;
   ViewPager2 viewPager;

    WormDotsIndicator dot2;
    ViewPagerAdapter1 viewPagerAdapter;
    FirebaseUser firebaseUser;
    DatabaseReference reference,reference1;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressBar progressBar;
    String filename;
    List<Uri>imageuri;
    List<String>imageurl;
    Long timestamp;
    EditText caption;
    ImageView cross;
    ImageView addimage,playbtn;
    Button post;
    int position;
    MediaController mediaController;
    Post post1=new Post();
    public int isr=0;
    private int n=2;
    private ProgressDialog dialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);


        dot2=findViewById(R.id.dot2);
        caption=findViewById(R.id.caption_post_option);
        viewPager=findViewById(R.id.viewpager2);
        playbtn=findViewById(R.id.play);
        addimage=findViewById(R.id.add_image);
        post=findViewById(R.id.post_option_post_button);
        progressBar=findViewById(R.id.progressBar3);
        cross=findViewById(R.id.cross);
        toolbar=findViewById(R.id.toolbar_addpost);
        //mediaController= new MediaController(this);
        //videoView.setMediaController(mediaController);
       // mediaController.setAnchorView(videoView);
        //videoView.setZOrderOnTop(true);

       // videoView.seekTo(2000);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(caption.getText().toString().length()>110){
                    caption.setError("Maximum 110 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageuri=new ArrayList<>();


        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(PostOption.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PostOption.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            ,100);
                    return;
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1000);



            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            if(!(caption.getText().toString().length() >200)){
                if ( (viewPager.getVisibility() == View.VISIBLE)) {

                    uploadImage();


                }  else {
                    Toast.makeText(getApplicationContext(), "Select a file to upload", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Caption should not be more than 200 characters", Toast.LENGTH_SHORT).show();
            }

            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   viewPager.setVisibility(View.GONE);
                   cross.setVisibility(View.GONE);
                   progressBar.setVisibility(View.GONE);
                   dot2.setVisibility(View.GONE);
                   addimage.setVisibility(View.VISIBLE);

            }
        });



    }

    private void uploadImage() {

        imageurl=new ArrayList<>();
        dialog = new ProgressDialog(PostOption.this);
        dialog.setMessage("Posting...");
        dialog.show();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        final String postid = reference.child("Post").push().getKey();
        imageurl.clear();
        for(i=0; i<imageuri.size(); i++){

            final String path = imageuri.get(i).toString();

            filename = path.substring(path.lastIndexOf("%") + 1);
            filename=filename+System.currentTimeMillis()/1000;

            storageReference = FirebaseStorage.getInstance().getReference("users/");

            final StorageReference fileRef = storageReference.child(FirebaseAuth.getInstance().getUid()).child("Post")
                    .child(filename);

            fileRef.putFile(imageuri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageurl.add(uri.toString());
                            k++;
                           // saveImageToExtrnalStorage(Uri.parse(path),i);
                            if(k==imageuri.size()) {

                                HashMap<String, Object> hashMap = new HashMap<>();

                                hashMap.put("uid", firebaseUser.getUid());
                                hashMap.put("caption", caption.getText().toString());
                                hashMap.put("imageurl", imageurl);
                                hashMap.put("videourl", "default");
                                timestamp = System.currentTimeMillis() / 1000;
                                hashMap.put("timestamp", timestamp);
                                hashMap.put("postid", postid);
                                reference.child("Post").child(postid)
                                        .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Intent intent = new Intent(PostOption.this, MainActivity.class);
                                        startActivity(intent);
                                        isr=1;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostOption.this, "Failed to add post", Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        }
                    });


                }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to get download url", Toast.LENGTH_LONG).show();
                }
            });


        }

    }

   /*private void saveImageToExtrnalStorage(Uri uri,int n) {


        File filepath= Environment.getExternalStorageDirectory();
        File dir=new File(filepath.getAbsolutePath()+"/Chat/"+File.separator+"Media");
        if(!dir.exists()){
        dir.mkdir();
        }
        File file1=new File(dir,"MyPost");
        if(!file1.exists()){
            file1.mkdir();
        }
        File file=new File(file1,n+System.currentTimeMillis()+".jpg");

        try {
            bitmap = MediaStore.Images.Media.getBitmap(PostOption.this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            outputStream=new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageurl=new ArrayList<>();

        if(requestCode==1000 && data!=null) {
           // imageuri=new ArrayList<>();
            switch (resultCode) {
                case RESULT_OK:
                    ClipData clipData = data.getClipData();
                    if(clipData!=null) {


                      if (!(clipData.getItemCount() > 10)) {
                          imageuri.clear();
                          imageurl.clear();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                imageuri.add(clipData.getItemAt(i).getUri());
                            }

                          for (int i = 0; i < clipData.getItemCount(); i++) {
                              imageurl.add(clipData.getItemAt(i).getUri().toString());
                          }

                            viewPagerAdapter=new ViewPagerAdapter1(getApplicationContext(),imageurl);
                            viewPager.setAdapter(viewPagerAdapter);

                          dot2.setViewPager2(viewPager);
                          if(imageurl.size()!=1) {
                              dot2.setVisibility(View.VISIBLE);
                          }

                            viewPager.setVisibility(View.VISIBLE);
                            cross.setVisibility(View.VISIBLE);
                            addimage.setVisibility(View.GONE);


                        }else {
                            Toast.makeText(getApplicationContext(),"You can only select maximum 10 images",Toast.LENGTH_LONG).show();
                        }

                    }else if(data.getData()!=null){

                        imageurl.clear();
                       imageurl.add(data.getData().toString());
                        viewPagerAdapter=new ViewPagerAdapter1(getApplicationContext(),imageurl);
                        viewPager.setAdapter(viewPagerAdapter);
                        viewPager.setVisibility(View.VISIBLE);
                        cross.setVisibility(View.VISIBLE);
                        addimage.setVisibility(View.GONE);
                    }
            }
        }



    }


}
