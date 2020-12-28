package com.justinpjose.chatroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    Context context;
    List<String> text, user;
    List<CharSequence> time;
    SharedPreferences sharedPreferences;

    public Adapter(Context context, List<String> text, List<String> user, List<CharSequence> time) {
        this.context = context;
        this.text = text;
        this.user = user;
        this.time = time;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String line1 = user.get(position);
        holder.message_user.setText(line1);
        CharSequence line2 = time.get(position);
        holder.message_time.setText(line2);
        String line3 = text.get(position);
        holder.message_text.setText(line3);

        sharedPreferences = context.getSharedPreferences("userID", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        if (line1.equals(name)) {
            holder.chat_layout.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        } else {
            holder.chat_layout.setBackgroundColor(Color.parseColor("#cccccc"));
        }
    }

    @Override
    public int getItemCount() {
        return text.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message_user;
        TextView message_time;
        TextView message_text;
        RelativeLayout chat_layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            chat_layout = itemView.findViewById(R.id.chat_layout);
            message_user = itemView.findViewById(R.id.message_user);
            message_time = itemView.findViewById(R.id.message_time);
            message_text = itemView.findViewById(R.id.message_text);
        }
    }
}
