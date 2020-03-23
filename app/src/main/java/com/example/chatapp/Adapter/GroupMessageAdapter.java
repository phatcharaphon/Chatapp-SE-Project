package com.example.chatapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.GroupMessage;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<GroupMessage> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public GroupMessageAdapter(Context mContext, List<GroupMessage> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageAdapter.ViewHolder holder, int position) {

        GroupMessage chat = mChat.get(position);

        holder.txt_seen.setVisibility(View.GONE);
        if(chat.getType().equals("image")){
            holder.show_message.setText("");
            holder.show_image.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);

            Glide.with(mContext).load(chat.getMessage()).into(holder.show_image);
        }
        else if(chat.getType().equals("file")){
            holder.show_message.setClickable(true);
            holder.show_message.setMovementMethod(LinkMovementMethod.getInstance());
            String[] file = chat.getMessage().split("uploads%");
            file = file[1].split("\\?");

            String text = "<a href="+ chat.getMessage() +"'>" + file[0] + "</a>";
            holder.show_message.setText(Html.fromHtml(text));
        } else {
            holder.show_message.setText(chat.getMessage());
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(chat.getSender());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(fuser.getUid()!=user.getId())holder.username.setText(user.getUsername());
                imageurl = user.getImageURL();
                if (imageurl.equals("default")) {
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext).load(imageurl).into(holder.profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView username;
        public ImageView show_image;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            username = itemView.findViewById(R.id.username);
            show_image = itemView.findViewById(R.id.show_image);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
