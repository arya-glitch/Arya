package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chat.Adapter.PageAdapter;
import com.example.chat.Adapter.PagerAdapter1;
import com.example.chat.Adapter.PostAdapter;
import com.example.chat.Adapter.ViewPagerAdapter2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class FragmentMain extends Fragment {

    private FloatingActionButton new_post;
    private TabLayout tabLayout;
    private FragmentMain.onFragmentBtnSelected listener;
    public ViewPager viewPager;
    private TabItem tab1,tab2;
    public PagerAdapter1 pagerAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main1,container,false);


        tabLayout=view.findViewById(R.id.tablayout_post);
        viewPager=view.findViewById(R.id.viewPager_post);
        tab1=view.findViewById(R.id.friend_post);
        tab2=view.findViewById(R.id.verify_post);


       readPost();



        new_post=view.findViewById(R.id.fab);

        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), PostOption.class);
                startActivity(intent);
            }
        });



        return view;
    }

    private void readPost() {
        pagerAdapter=new PagerAdapter1(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();

                }
                else if (tab.getPosition()==1){
                    pagerAdapter.notifyDataSetChanged();
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FragmentMain.onFragmentBtnSelected){
            listener = (FragmentMain.onFragmentBtnSelected) context;
        }
        else{
            throw new ClassCastException(context.toString() + "must implement listener");
        }
    }

    public interface onFragmentBtnSelected{

        public void onFragmentBtnSelected();
    }


}
