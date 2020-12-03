package com.example.chat.ProfilePostTabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.GridApdapter;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class GridPost extends Fragment {


    RecyclerView recyclerView;
    GridApdapter gridApdapter;
    List<Post> mPost;
    FirebaseUser fuser;
    Post temp;
    RelativeLayout posttext;
    private DatabaseReference databaseReference;
    String userid;

    public GridPost(String userid) {
        this.userid = userid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.gridpost,container,false);
        recyclerView=view.findViewById(R.id.recyclerView_profile_fragment);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        posttext=view.findViewById(R.id.posttext2);



        readPosts();

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
    private void readPosts() {
        mPost=new ArrayList<>();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.orderByChild("uid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPost.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    mPost.add(post);
                }
                if(mPost.size()==0) {
                    posttext.setVisibility(View.VISIBLE);
                }else {
                    posttext.setVisibility(View.GONE);
                }
                temp=new Post();
                for(int i=0; i<mPost.size(); i++){
                    for(int j=i+1; j<mPost.size(); j++){
                        if(mPost.get(i).getTimestamp()<mPost.get(j).getTimestamp()){
                            temp=mPost.get(i);
                            mPost.set(i,mPost.get(j));
                            mPost.set(j,temp);
                        }
                    }
                }
                gridApdapter=new GridApdapter(getApplicationContext(),mPost);
                recyclerView.setAdapter(gridApdapter);
                layoutAnimation(recyclerView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
