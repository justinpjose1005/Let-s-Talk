package com.justinpjose.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterForMainActivity.ItemClickListener {
    Boolean logged_in = true, empty_chat = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String mName, mKey, mPassword, mRoom;
    DatabaseReference chats_rf, session_rf;
    ArrayList<String> usersRooms = new ArrayList<>();
    RecyclerView recyclerView;
    AdapterForMainActivity adapter;
    TextView tv_loading;
    TextView tv_empty_chat;
    ProgressBar pb_line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_empty_chat = findViewById(R.id.tv_empty_chat);
        tv_loading = findViewById(R.id.tv_loading);
        pb_line = findViewById(R.id.pb_line);
        tv_loading.setVisibility(View.VISIBLE);
        pb_line.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences("userID", Context.MODE_PRIVATE);
        mName = sharedPreferences.getString("name", "");
        mKey = sharedPreferences.getString("key", "");
        mPassword = sharedPreferences.getString("password", "");
        mRoom = sharedPreferences.getString("room", "");
        editor = sharedPreferences.edit();
        editor.apply();
        if (mName.isEmpty() || mPassword.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setTitle("Chat Room");
        recyclerView = findViewById(R.id.main_activity_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        chats_rf = FirebaseDatabase.getInstance().getReference().child("chats");
        session_rf = FirebaseDatabase.getInstance().getReference("session");
        Query query = session_rf.child(mKey).child("room");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("justinlog","Not an empty chat");
                }
                else {
                    tv_empty_chat.setVisibility(View.VISIBLE);
                    tv_loading.setVisibility(View.GONE);
                    pb_line.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Log.d("justinlog", "yes snapshot");
                    String name = snapshot.child("name").getValue().toString();
                    usersRooms.add(name);
                }
                Log.d("justinlog", "updateRecyclerView");
                updateRecyclerView();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue().toString();
                    usersRooms.add(name);
                }
                updateRecyclerView();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateRecyclerView() {
        // set up the RecyclerView
        adapter = new AdapterForMainActivity(this, usersRooms);
        Log.d("justinlog", "final:-" + usersRooms);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        tv_empty_chat.setVisibility(View.GONE);
        tv_loading.setVisibility(View.GONE);
        pb_line.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        String r = adapter.getItem(position);
        editor.putString("room",r);
        editor.commit();
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (logged_in) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (logged_in) {
        }
    }

    // INFLATES MENU FOR LOGOUT
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_room, menu);
        return true;
    }

    // GIVES MENU LOGOUT THE FUNCTION TO LOGOUT
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(this, AddRoomActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_exit) {
            logged_in = false;
            Toast.makeText(MainActivity.this, "You have been signed out.", Toast.LENGTH_LONG).show();
            session_rf.child(mKey).child("status").setValue("logged out");
            editor.putString("name", "");
            editor.putString("key", "");
            editor.putString("password", "");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}