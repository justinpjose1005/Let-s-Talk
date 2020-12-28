package com.justinpjose.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddRoomActivity extends AppCompatActivity {
    TextInputEditText join_room_code, join_room_name;
    TextInputEditText add_room_code, add_room_name;
    DatabaseReference chats_rf, room_rf, session_rf;
    String mName, mKey, mPassword, mRoom;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String rn, rc, rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        join_room_code = findViewById(R.id.join_room_code);
        join_room_name = findViewById(R.id.join_room_name);
        add_room_code = findViewById(R.id.add_room_code);
        add_room_name = findViewById(R.id.add_room_name);
        chats_rf = FirebaseDatabase.getInstance().getReference().child("chats");
        session_rf = FirebaseDatabase.getInstance().getReference("session");
        room_rf = FirebaseDatabase.getInstance().getReference("room");
        sharedPreferences = getSharedPreferences("userID", Context.MODE_PRIVATE);
        mName = sharedPreferences.getString("name", "");
        mKey = sharedPreferences.getString("key", "");
        mPassword = sharedPreferences.getString("password", "");
        mRoom = sharedPreferences.getString("room", "");
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void join_room(View view) {
        rn = join_room_name.getText().toString();
        rc = join_room_code.getText().toString();
        if (rn.isEmpty() || rc.isEmpty()) {
            Toast.makeText(this, "Join Room Fields Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        checkRoomName();
    }

    private void checkRoomName() {
        room_rf.child(rn).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkRoomCode();
                }
                else {
                    Toast.makeText(AddRoomActivity.this, "Room Unavailable", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkRoomCode() {
        room_rf.child(rn).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String code = snapshot.child("code").getValue().toString();
                    rid = snapshot.child("id").getValue().toString();
                    if (code.equals(rc)) {
                        checkIfUserInRoom();
                    }
                    else {
                        Toast.makeText(AddRoomActivity.this, "Invalid Room Code", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfUserInRoom() {
        Query query = session_rf.child(mKey).child("room").child(rid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(AddRoomActivity.this, "You belong to this group", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddRoomActivity.this, "Joining Room", Toast.LENGTH_SHORT).show();
                    RoomUsers roomUsers = new RoomUsers(rn,"no");
                    session_rf.child(mKey).child("room").child(rid).setValue(roomUsers);
                    Intent intent = new Intent(AddRoomActivity.this,RoomActivity.class);
                    editor.putString("room",rn);
                    editor.commit();
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void add_room(View view) {
        String rn = add_room_name.getText().toString();
        String rc = add_room_code.getText().toString();
        if (rn.isEmpty() || rc.isEmpty()) {
            Toast.makeText(this, "Add Room Fields Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        room_rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String name = dataSnapshot.getKey();
                        if (name.equals(rn)) {
                            Toast.makeText(AddRoomActivity.this, "Room Name Exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                String id = room_rf.push().getKey();
                RoomInfo roomInfo = new RoomInfo(rn,rc,id);
                room_rf.child(rn).child("info").setValue(roomInfo);
                RoomUsers roomUsers = new RoomUsers(rn,"yes");
                session_rf.child(mKey).child("room").child(id).setValue(roomUsers);
                Toast.makeText(AddRoomActivity.this, "Room Created", Toast.LENGTH_SHORT).show();
                room_rf.child(id).removeValue();
                Intent intent = new Intent(AddRoomActivity.this,RoomActivity.class);
                editor.putString("room",rn);
                editor.commit();
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}