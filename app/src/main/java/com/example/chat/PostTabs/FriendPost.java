package com.example.chat.PostTabs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chat.Adapter.PostAdapter;
import com.example.chat.Post;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendPost extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> mpost;
    private List<String>friendList;
    RelativeLayout posttext;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,databaseReference1;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendpost,container,false);




        posttext=view.findViewById(R.id.posttext);
        recyclerView=view.findViewById(R.id.recyclerView_friend_post);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout=view.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               checkFriend();
               swipeRefreshLayout.setRefreshing(false);
            }
        });

        checkFriend();


        return view;
    }



    private void layoutAnimation(RecyclerView recyclerView){

        Context context=recyclerView.getContext();
        LayoutAnimationController layoutAnimationController=
                AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_down_to_up);

        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    private void checkFriend() {
        friendList=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference1= FirebaseDatabase.getInstance().getReference("Friend").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();
                friendList.add(firebaseUser.getUid());
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    friendList.add(""+snapshot.getRef().getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readPost() {
        mpost=new ArrayList<>();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        databaseReference= FirebaseDatabase.getInstance().getReference("Post");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mpost.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    for(String id:friendList){
                        if(post.getUid().equals(id)) {
                            mpost.add(post);
                        }
                    }

                }
                if(!mpost.isEmpty()){
                    posttext.setVisibility(View.GONE);
                }
                for( int i=0; i<mpost.size(); i++){
                    for (int j=i+1; j<mpost.size(); j++){
                        if(mpost.get(i).getTimestamp()<mpost.get(j).getTimestamp()){
                            Post post=new Post();
                            post=mpost.get(i);
                            mpost.set(i,mpost.get(j));
                            mpost.set(j,post);
                        }
                    }
                }
                postAdapter=new PostAdapter(getContext(),mpost);
                recyclerView.setAdapter(postAdapter);
                layoutAnimation(recyclerView);
                //postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }

}
