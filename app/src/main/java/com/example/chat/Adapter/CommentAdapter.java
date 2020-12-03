package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Chat;
import com.example.chat.Comments;
import com.example.chat.Post;
import com.example.chat.R;
import com.example.chat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mcontext;
    private List<Comments> mComment;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    public CommentAdapter(Context mcontext, List<Comments> mComment){
        setHasStableIds(true);
        this.mcontext = mcontext;
        this.mComment=mComment;

    }


    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.comment_user_layout,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {

        final Comments comment= mComment.get(position);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(comment.getSender());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mcontext).load(user.getImageurl()).into(holder.circleImageView);
                holder.name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.comment.setText(comment.getComment());





    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private  CircleImageView circleImageView;
        private TextView comment;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.profile_image_comment_adapter);
            comment=itemView.findViewById(R.id.comment_adapter);
            name=itemView.findViewById(R.id.user_profile_name_comment);


        }
    }
}