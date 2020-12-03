package com.example.chat.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat.Post;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class GridApdapter extends RecyclerView.Adapter<GridApdapter.ViewHolder> {


    private Context mContext;
    private List<Post> mPost;
    FirebaseUser firebaseUser;


    public GridApdapter(Context mContext, List<Post> mPost) {
        setHasStableIds(true);
        this.mContext=mContext;
        this.mPost=mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(mContext).inflate(R.layout.grid,parent,false);
        return new GridApdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Post post=mPost.get(position);

        holder.caption.setText(post.getCaption());

        if(!post.getImageurl().get(0).equals("default")&&post.getVideourl().equals("default")){
            Glide.with(mContext).load(post.getImageurl().get(0)).into(holder.imagepost);
        }


    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView caption;
        ImageView imagepost;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            caption=itemView.findViewById(R.id.caption_grid);
            imagepost=itemView.findViewById(R.id.imagepost_grid);
            Typeface typeface = ResourcesCompat.getFont(mContext,R.font.satisfy);
            caption.setTypeface(typeface);
        }
    }
}

