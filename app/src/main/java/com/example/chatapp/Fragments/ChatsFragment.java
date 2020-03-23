package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.GroupAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.Group;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView group_recyclerView;

    private UserAdapter userAdapter;
    private GroupAdapter groupAdapter;
    private List<User> mUsers;
    private List<Group> mGroups;

    FirebaseUser fuser;
    DatabaseReference reference;
    DatabaseReference groupReference;

    /*
    private List<String> usersList;
     */
    private List<Chatlist> usersList;
    private List<String> groupList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        group_recyclerView = view.findViewById(R.id.group_recycler_view);
        group_recyclerView.setHasFixedSize(true);
        group_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupList = new ArrayList<>();

        groupReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Groups");
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String group = snapshot.getKey();
                    groupList.add(group);
                }

                groupList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }

    private void chatlist(){
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for(Chatlist chatlist:usersList){
                        if(user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void groupList(){
        mGroups = new ArrayList<>();
        groupReference = FirebaseDatabase.getInstance().getReference("Groups");
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroups.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Group group = snapshot.getValue(Group.class);
                    for(String group1 : groupList){
                        if(group1.equals(snapshot.getKey())){
                            mGroups.add(group);
                        }
                    }
                }
                groupAdapter = new GroupAdapter(getContext(), mGroups);
                group_recyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // don't use

    /*
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid()) && !usersList.contains(chat.getReceiver())){
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid()) && !usersList.contains(chat.getSender())){
                        usersList.add(chat.getSender());
                    }
                }

                readChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    /*
    private void readChats(){
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    for(String id : usersList){
                        if(user.getId().equals(id)){

                            if(!mUsers.contains(user)) {
                                mUsers.add(user);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
