package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Group;
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

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context mContext;
    private List<Group> mGroups;

    String theLastMessage;

    public GroupAdapter(Context mContext, List<Group> mGroups) {
        this.mContext = mContext;
        this.mGroups = mGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Group group = mGroups.get(position);
        holder.group_name.setText(group.getName());
        if(group.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(group.getImageURL()).into(holder.profile_image);
        }
        lastMessage(group.getId(), holder.last_msg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("group_id", group.getId());
                mContext.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView group_name;
        public ImageView profile_image;
        private TextView last_msg;

        public ViewHolder(View itemView){
            super(itemView);

            group_name = itemView.findViewById(R.id.group_name);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String groupid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupMessage").child(groupid);

        if(firebaseUser!=null) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final GroupMessage groupMessage = snapshot.getValue(GroupMessage.class);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(groupMessage.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (groupMessage.getSender().equals(firebaseUser.getUid())) {
                                    theLastMessage = "You";
                                } else {
                                    theLastMessage = user.getUsername();
                                }

                                if(groupMessage.getType().equals("image"))theLastMessage += " sent an image";
                                else if(groupMessage.getType().equals("file"))theLastMessage += " sent a file";
                                else theLastMessage += ": " + groupMessage.getMessage();

                                switch (theLastMessage) {
                                    case "default":
                                        last_msg.setText("No Message");
                                        break;

                                    default:
                                        last_msg.setText(theLastMessage);
                                        break;
                                }

                                theLastMessage = "default";
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
