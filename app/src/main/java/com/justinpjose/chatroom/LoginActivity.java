package com.justinpjose.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText login_screen_username, login_screen_password;
    ProgressBar login_screen_progressBar;
    int counter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_screen_username = findViewById(R.id.login_screen_username);
        login_screen_password = findViewById(R.id.login_screen_password);
        login_screen_progressBar = findViewById(R.id.login_screen_progressBar);

        sharedPreferences = getSharedPreferences("userID", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void login_screen_register_button(View view) {
        login_screen_progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 2000);
    }

    public void login_screen_login_button(View view) {
        login_screen_progressBar.setVisibility(View.VISIBLE);
        String name = login_screen_username.getText().toString();
        String password = login_screen_password.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            login_screen_progressBar.setVisibility(View.GONE);
        } else {
            DatabaseReference reference_to_session = FirebaseDatabase.getInstance().getReference("session");
            Query query = reference_to_session.orderByChild("name").equalTo(name);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String pass = dataSnapshot.child("password").getValue().toString();
                            if (password.equals(pass)) {
//                                Log.d("user key", dataSnapshot.getKey());
                                String key = dataSnapshot.getKey();
                                editor.putString("key", key);
                                editor.putString("name", name);
                                editor.putString("password", password);
                                editor.apply();
                                counter = 1;
                                break;
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (counter == 1) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Name does not exist in database", Toast.LENGTH_SHORT).show();
                    }
                    login_screen_progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}