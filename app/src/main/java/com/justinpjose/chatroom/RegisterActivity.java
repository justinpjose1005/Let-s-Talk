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

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText register_screen_username, register_screen_email, register_screen_password;
    ProgressBar register_screen_progressBar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_screen_username = findViewById(R.id.register_screen_username);
        register_screen_email = findViewById(R.id.register_screen_email);
        register_screen_password = findViewById(R.id.register_screen_password);
        register_screen_progressBar = findViewById(R.id.register_screen_progressBar);

        sharedPreferences = getSharedPreferences("userID", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void register_screen_login_button(View view) {
        register_screen_progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 2000);
    }

    public void register_screen_register_button(View view) {
        register_screen_progressBar.setVisibility(View.VISIBLE);
        String name = register_screen_username.getText().toString();
        String email = register_screen_email.getText().toString();
        String password = register_screen_password.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            register_screen_progressBar.setVisibility(View.GONE);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DatabaseReference reference_to_session = FirebaseDatabase.getInstance().getReference("session");
                    Query query = reference_to_session.orderByChild("name").equalTo(name);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(RegisterActivity.this, "Username exists", Toast.LENGTH_SHORT).show();
                                register_screen_progressBar.setVisibility(View.GONE);
                                register_screen_username.requestFocus();
                            } else {
                                User user = new User(name,email,password,"logged out",0);
                                String key = reference_to_session.push().getKey();
                                reference_to_session.child(key).setValue(user);
                                Toast.makeText(RegisterActivity.this, "Account creation successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }, 4000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}