package com.justinpjose.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomActivity extends AppCompatActivity {
    Boolean menu_clickable = false, progress_bar_visibility = false, send_button_clicked = false;
    int chat_clear_counter, start_stop_flag = 0;
    long chat_length_counter;
    long last_read_line;
    String message;
    String users_online;
    String mName, mKey, mPassword, mRoom;
    TextView msg_field1;
    TextView room_screen_users_online;
    TextInputEditText input;
    ProgressBar progress_bar_circle;
    FloatingActionButton fab;
    String dayName;
    RecyclerView recyclerView1;
    List<String> text = new ArrayList<>();
    List<String> user = new ArrayList<>();
    List<CharSequence> time = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference chats_rf, session_rf, room_rf;
    Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // HELP ADAPTER TO IDENTIFY THE USER NAME
        sharedPreferences = getSharedPreferences("userID", Context.MODE_PRIVATE);
        mName = sharedPreferences.getString("name", "");
        mKey = sharedPreferences.getString("key", "");
        mPassword = sharedPreferences.getString("password", "");
        mRoom = sharedPreferences.getString("room", "");
        editor = sharedPreferences.edit();
        editor.apply();

        setTitle(mRoom);
        chats_rf = FirebaseDatabase.getInstance().getReference().child("room").child(mRoom).child("chat");
        session_rf = FirebaseDatabase.getInstance().getReference("session");
        room_rf = FirebaseDatabase.getInstance().getReference("room");

        LocalDate date = LocalDate.now();
        DayOfWeek dow = date.getDayOfWeek();
        dayName = dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        msg_field1 = findViewById(R.id.msg_field1);
        fab = findViewById(R.id.fab);
        input = findViewById(R.id.input);
        progress_bar_circle = findViewById(R.id.progress_bar_circle);
        progress_bar_circle.setVisibility(View.VISIBLE);
        recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view1);
        room_screen_users_online = findViewById(R.id.room_screen_users_online);

        chats_rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("chatslog", "exists");
                } else {
                    Log.d("chatslog", "nope");
                    chats_rf.push().setValue(new ChatMessage("Welcome, to Chat Room.", "System"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        session_rf.child(mKey).child("status").setValue("online");
        // SETTING "status" AS "online" FOR BOTH NEW N EXISTING USERS
        session_rf.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    input.setFocusableInTouchMode(true);
                    menu_clickable = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // ADDS CONTENT IN UPWARD MANNER
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView1.setLayoutManager(layoutManager);

        // ON CLICKING SEND
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IF MESSAGE IS NOT EMPTY
                if (!input.getText().toString().isEmpty()) {
                    message = input.getText().toString();
                    if (message.equals("#WHOAMI")) {
                        Toast.makeText(RoomActivity.this, "Name: " + mName + "\nPassword: " + mPassword, Toast.LENGTH_LONG).show();
//                            chats_rf.removeValue();
//                            finish();
//                            startActivity(getIntent());
//                            input.setText("");
//                            ChatMessage chatMessage = new ChatMessage("Welcome to Let's Talk!", "System");
//                            chats_rf.push().setValue(chatMessage);
                    } else if (message.equals("#TOAST")) {
                        Toast.makeText(RoomActivity.this, "CODES\n" + "#WHOAMI - Displays your info.", Toast.LENGTH_LONG).show();
                    } else {
                        chats_rf.push().setValue(new ChatMessage(input.getText().toString(), mName));
                        msg_field1.setVisibility(View.GONE);
                    }
                    input.setText("");
                }
            }
        });

        // SHOW ONLINE USERS
        Query query = session_rf.orderByChild("status").equalTo("online");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users_online = "";
                    users_online = "Online:- ";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        users_online += "#" + user.getName() + " ";
                    }
                    room_screen_users_online.setText(users_online);
//                        Log.d("Online users1",users_online);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // BRINGS THE RECYCLER VIEW ABOVE THE KEYBOARD
        recyclerView1.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView1.smoothScrollToPosition(myAdapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RoomActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
        method_display_chat();
    } // onCreate() ENDS HERE

    @Override
    protected void onStop() {
        super.onStop();
        session_rf.child(mKey).child("status").setValue("offline");

    }

    @Override
    protected void onStart() {
        super.onStart();
        progress_bar_circle.setVisibility(View.GONE);
        session_rf.child(mKey).child("status").setValue("online");
    }

    // FUNCTION TO DISPLAY CHAT ON SCREEN
    private void method_display_chat() {
        chats_rf.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    text.add(snapshot.child("messageText").getValue().toString());
                    user.add(snapshot.child("messageUser").getValue().toString());
                    time.add(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", snapshot.child("messageTime").getValue(Long.class)));
                    myAdapter = new Adapter(RoomActivity.this, text, user, time);
                    recyclerView1.setAdapter(myAdapter);
                    // SHOWS THE LAST ITEM IN RECYCLER VIEW
                    recyclerView1.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView1.smoothScrollToPosition(myAdapter.getItemCount());
                        }
                    });
                } else {
//                    Toast.makeText(RoomActivity.this, "Snapshot does not exist", Toast.LENGTH_SHORT).show();
                }
                msg_field1.setVisibility(View.GONE);
                progress_bar_circle.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
//                    Toast.makeText(RoomActivity.this, "Chat Changed", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(RoomActivity.this, "Chat did not Change", Toast.LENGTH_SHORT).show();
                }
                msg_field1.setVisibility(View.GONE);
                progress_bar_circle.setVisibility(View.GONE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    Toast.makeText(RoomActivity.this, "Chat Cleared", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(RoomActivity.this, "Chat did not Clear", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
//                    Toast.makeText(RoomActivity.this, "Chat Moved", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(RoomActivity.this, "Chat did not Move", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(RoomActivity.this, "Chat was cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // INFLATES MENU FOR LOGOUT
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // GIVES MENU LOGOUT THE FUNCTION TO LOGOUT
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (menu_clickable) {
            if (item.getItemId() == R.id.menu_leave_room) {
                leaveRoom();
            }
        }
        return true;
    }

    private void leaveRoom() {
        Query query = session_rf.child(mKey).child("room").orderByChild("name").equalTo(mRoom);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String key = dataSnapshot.getKey();
                        String admin = dataSnapshot.child("status").getValue().toString();
                        Log.d("justinlog1", dataSnapshot.getKey());
                        Log.d("justinlog1", dataSnapshot.child("status").getValue().toString());
                        removeRoomKey(key,admin);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeRoomKey(String key, String admin) {
        session_rf.child(mKey).child("room").child(key).removeValue();
        if (admin.equals("yes")) {
            room_rf.child(mRoom).child("chat").removeValue();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}