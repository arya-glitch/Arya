package com.example.chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.ViewPagerAdapter2;
import com.example.chat.Listener.AppBarStateChangeListener;
import com.example.chat.Settings.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment {

    ImageView picture,setting;
    TextView username,noofpost,nooffollowing,nooffriends;
    StorageReference storageReference;
    FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth fAuth;
    FirebaseStorage fstore;
    ProgressBar progressBar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabItem tab1,tab2,tab3;
    ViewPagerAdapter2 viewPagerAdapter2;
    FloatingActionButton shareProfile;
    CircleImageView collapsed_dp;
    private DatabaseReference databaseReference;
    private ProgressDialog progress;
    Uri resultUri;
    CollapsingToolbarLayout collapsingToolbarLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile,container,false);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseStorage.getInstance();
        viewPager=view.findViewById(R.id.viewpager_profile_fragment);
        storageReference=FirebaseStorage.getInstance().getReference();
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar9);
        setting=view.findViewById(R.id.setting);
        nooffollowing=view.findViewById(R.id.no_of_following);
        nooffriends=view.findViewById(R.id.no_of_friends);
        noofpost=view.findViewById(R.id.no_of_post);
        username=view.findViewById(R.id.username);
        tabLayout=view.findViewById(R.id.tablayout_profile);
        tab1=view.findViewById(R.id.tab1);
        tab2=view.findViewById(R.id.tab2);
        tab3=view.findViewById(R.id.tab3);
        picture=view.findViewById(R.id.dp);
        username= view.findViewById(R.id.username);
        shareProfile=view.findViewById(R.id.shareprofile);
        collapsed_dp=view.findViewById(R.id.collapsed_dp);


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar_01);
        collapsingToolbarLayout= view.findViewById(R.id.collapsing_toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if(state==State.IDLE||state==State.COLLAPSED){
                    shareProfile.setVisibility(View.INVISIBLE);
                    if(state==State.COLLAPSED){
                        collapsed_dp.setVisibility(View.VISIBLE);
                    }else {
                        collapsed_dp.setVisibility(View.GONE);
                    }
                }else {

                    shareProfile.setVisibility(View.VISIBLE);
                    collapsed_dp.setVisibility(View.GONE);
                }
            }
        });


         readUserData();
         initialiseViewPager();

         nooffriends.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(getActivity(),Friend.class);
                 intent.putExtra("userid",fuser.getUid());
                 startActivity(intent);
                 CustomIntent.customType(getContext(),"bottom-to-up");
             }
         });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , 100);
                    return;
                }

                Intent intent = CropImage.activity()
                        .setAspectRatio(1,1)
                        .getIntent(getContext());
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
                //CropImage.startPickImageActivity(getActivity());


            }
        });




        return view;



    }

    private void initialiseViewPager() {
        String userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewPagerAdapter2=new ViewPagerAdapter2(getChildFragmentManager(),tabLayout.getTabCount(),userid);
        viewPager.setAdapter(viewPagerAdapter2);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    viewPagerAdapter2.notifyDataSetChanged();

                }
                else if (tab.getPosition()==1){
                    viewPagerAdapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }



    private void readUserData() {

        loadimage();
        setName();
        getExtraData();

    }

    private void getExtraData() {
        databaseReference=FirebaseDatabase.getInstance().getReference("Friend").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int n= (int) dataSnapshot.getChildrenCount();
                nooffriends.setText(Integer.toString(n));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.orderByChild("uid").equalTo(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int n= (int) dataSnapshot.getChildrenCount();
                noofpost.setText(Integer.toString(n));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setName() {

        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user=dataSnapshot.getValue(User.class);

                collapsingToolbarLayout.setTitle(user.getName());
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadimage() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);


                if (!user.getImageurl().equals("default")) {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(picture);
                    float density = getApplicationContext().getResources().getDisplayMetrics().density;
                    int px = (int) (1 * density);
                    collapsed_dp.setPadding(px,px,px,px);
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(collapsed_dp);


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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            progressBar.setVisibility(View.VISIBLE);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uploadImagetofirebase(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressBar.setVisibility(View.GONE);
                Exception error = result.getError();
                Toast.makeText(getContext(), (CharSequence) error, Toast.LENGTH_SHORT).show();

            }else if(resultCode==RESULT_CANCELED){
                progressBar.setVisibility(View.GONE);
            }
        }
       /* if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {

            progressBar.setVisibility(View.VISIBLE);

            Uri imageuri=CropImage.getPickImageResultUri(getApplicationContext(),data);
            if(CropImage.isReadExternalStoragePermissionsRequired(getApplicationContext(),imageuri)){
                uri=imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else{
                startCrop(imageuri);
            }
        }else if(resultCode==RESULT_CANCELED){
            progressBar.setVisibility(View.GONE);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){

                uploadImagetofirebase(result.getUri());

            }
        }else {
            progressBar.setVisibility(View.GONE);
        }*/
        progressBar.setVisibility(View.GONE);
    }

    private void startCrop(Uri imageuri) {

        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setMultiTouchEnabled(true)
                .start(getActivity());
    }

    private void uploadImagetofirebase(Uri imageuri) {

        progress=new ProgressDialog(getContext());
        progress.setMessage("Updating Profile Picture..");
        progress.show();
        final StorageReference fileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");

        fileRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        Picasso.get().load(uri).into(picture, new Callback() {
                            @Override
                            public void onSuccess() {

                                String imageurl= uri.toString();
                                User object_user= new User();
                                object_user.setImageurl(imageurl);
                                FirebaseDatabase.getInstance().getReference("User")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageurl")
                                        .setValue(object_user.getImageurl() ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                   Toast.makeText(getContext(),"Profile image updated",Toast.LENGTH_LONG).show();
                                    }
                                });
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                        setUserProfile(uri);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Failed ",Toast.LENGTH_LONG).show();
            }
        });
        progress.dismiss();

    }



        private void setUserProfile(Uri uri){
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest request= new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build();

            user.updateProfile(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),"Profile Image Changed succesfully",Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Profile Image failed",Toast.LENGTH_LONG).show();
                        }
                    });
    }




}
