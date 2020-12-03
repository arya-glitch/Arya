package com.example.chat.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chat.ProfilePostTabs.GridPost;
import com.example.chat.ProfilePostTabs.ListPost;
import com.example.chat.ProfilePostTabs.TaggedPost;

public class ViewPagerAdapter2 extends FragmentPagerAdapter {

    private int nooftabs;
    private String userid;



    public ViewPagerAdapter2(@NonNull FragmentManager fm,int nooftabs,String userid) {
        super(fm);
        this.nooftabs=nooftabs;
        this.userid=userid;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new GridPost(userid);

            case 1:
                return new ListPost(userid);
            case 2:
                return new TaggedPost(userid);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return nooftabs;
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
