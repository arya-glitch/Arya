package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public OnlineUserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.userchat,parent,false);
        return new OnlineUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       User user=mUsers.get(position);

        if(!user.getImageurl().equals("default")){
            Glide.with(mContext).load(user.getImageurl()).into(holder.dp);
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView dp,greenDot;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dp=itemView.findViewById(R.id.chatdp);
            greenDot=itemView.findViewById(R.id.greenDot);
        }
    }
}
