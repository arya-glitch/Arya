package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.chat.Interface.OnBackPressedListener;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentMain.onFragmentBtnSelected{

    FirebaseAuth mAuth= FirebaseAuth.getInstance();


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    private String profile_name_main_activity ;
    private Object Plus;
    private FirebaseAuth firebaseAuth;
    private FacebookSdk facebookSdk;
    private GoogleSignInClient mGoogleSignInClient;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    protected OnBackPressedListener onBackPressedListener;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        firebaseAuth.getInstance();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();





        if((!isLoggedIn)&&(firebaseAuth.getInstance().getCurrentUser()==null)) {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            finish();
        }


        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));








        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment,new FragmentMain());
        fragmentTransaction.commit();



    }
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }


    private  void status(String status){

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("(online)");
    }

    @Override
    protected void onPause() {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        super.onPause();
        if(firebaseUser!=null) {
            status("(offline)");
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId()==R.id.home )
        {
            fragmentManager=getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new FragmentMain());
            fragmentTransaction.commit();

        }
        if (item.getItemId()==R.id.chat)
        {
            fragmentManager=getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new FragmentChat() );
            fragmentTransaction.commit();

        }
        if (item.getItemId()==R.id.profile)
        {
            fragmentManager=getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new ProfileFragment());
            fragmentTransaction.commit();

        }
        if(item.getItemId()==R.id.logout)
        {
            status("(offline)");

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if(acct!=null) {
                GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
            }
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if(accessToken!=null){
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
            }
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),SignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();

        }


        if (item.getItemId()==R.id.find_friends)
        {
            fragmentManager=getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new FragmentUser());
            fragmentTransaction.commit();

        }

        return true;
    }

    @Override
    public void onFragmentBtnSelected() {
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment,new FragmentMain());
        fragmentTransaction.commit();

    }



}
