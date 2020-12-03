package com.example.chat.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chat.Tabs.AddFriend1;
import com.example.chat.Tabs.Request;

public class PageAdapter extends FragmentPagerAdapter {


    private int nooftabs;


    public PageAdapter(@NonNull FragmentManager fm,int nooftabs) {
        super(fm);
        this.nooftabs=nooftabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AddFriend1();
            case 1:
                return new Request();
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
