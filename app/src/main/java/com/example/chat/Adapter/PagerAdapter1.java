package com.example.chat.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chat.PostTabs.FriendPost;
import com.example.chat.PostTabs.VerifiedPost;

public class PagerAdapter1 extends FragmentPagerAdapter {

    private  int nooftabs;

    public PagerAdapter1(@NonNull FragmentManager fm,int nooftabs) {
        super(fm);
        this.nooftabs=nooftabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FriendPost();
            case 1:
                return new VerifiedPost();
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
