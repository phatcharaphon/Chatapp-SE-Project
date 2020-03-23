package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    MaterialEditText group_name, group_id;
    Button btn_create;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                finish();
                startActivity(new Intent(CreateGroupActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users" ).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.isNull()){
                    return;
                }
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        group_name = findViewById(R.id.group_name);
        group_id = findViewById(R.id.group_id);
        btn_create = findViewById(R.id.btn_create);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = group_name.getText().toString();
                String idName = group_id.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(CreateGroupActivity.this, "Group Name is required", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Name", name);
                    hashMap.put("idName",idName);
                    hashMap.put("imageURL","default");

                    reference = FirebaseDatabase.getInstance().getReference().child("Groups").push();
                    hashMap.put("id", reference.getKey());
                    reference.setValue(hashMap);
                    reference.child("Users").child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());

                    final String groupKey = reference.getKey();
                    final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                            .child("Groups")
                            .child(groupKey);
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                userReference.child("id").setValue(groupKey);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    startActivity(new Intent(CreateGroupActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });

    }
}
