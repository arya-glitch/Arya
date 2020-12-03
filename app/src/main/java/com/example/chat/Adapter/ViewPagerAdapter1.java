package com.example.chat.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat.PostImageView;
import com.example.chat.R;
import com.example.chat.SquareImageView;

import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;
import static androidx.viewpager.widget.PagerAdapter.POSITION_UNCHANGED;

public class ViewPagerAdapter1 extends RecyclerView.Adapter<ViewPagerAdapter1.ViewHolder> {

    private Context mcContext;
    private List<String> imageurl;

    public ViewPagerAdapter1(Context mcContext, List<String> imageurl) {
        setHasStableIds(true);
        this.mcContext = mcContext;
        this.imageurl = imageurl;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mcContext).inflate(R.layout.image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

         holder.setIsRecyclable(false);
        final String url=imageurl.get(position);
        holder.current.setText(Integer.toString(position+1)+"/"+Integer.toString(imageurl.size()));

        if(imageurl.size()==1){
            holder.current.setVisibility(View.GONE);
        }

        Glide.with(mcContext).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return imageurl.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private PostImageView imageView;
        private TextView current;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            setIsRecyclable(false);
            imageView=itemView.findViewById(R.id.imagepost);
            current=itemView.findViewById(R.id.current_no);

            progressBar=itemView.findViewById(R.id.progressBar8);

        }
    }
}
