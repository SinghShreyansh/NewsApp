package com.example.chatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.ChatActivity;
import com.example.chatsapp.Models.Users;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.RawConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainChatAdapter extends RecyclerView.Adapter<MainChatAdapter.UsersViewHolder> {

    Context context;
    ArrayList<Users> users;


    public MainChatAdapter(Context context,ArrayList<Users> users){
        this.context=context;
        this.users=users;

    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.raw_conversation,parent,false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
    Users user = users.get(position);
    String senderID = FirebaseAuth.getInstance().getUid();

    String senderRoom = senderID + user.getUid();

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                           // long time = snapshot.child("lastMsgTime").getValue(Long.class);

                            holder.binding.tvLastMsg.setText(lastMsg);
                           // holder.binding.tvMsgTime.setText((int) time);
                        }else {
                            holder.binding.tvLastMsg.setText("Tap to chat");
                            holder.binding.tvMsgTime.setText("");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    holder.binding.tvUsername.setText(user.getName());

        Glide.with(context).load(user.getProfilePic())
                .placeholder(R.drawable.ic_avatar_icon)
                .into(holder.binding.imageView2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("uid",user.getUid());
                intent.putExtra("image",user.getProfilePic());
                intent.putExtra("token",user.getToken());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

            RawConversationBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= RawConversationBinding.bind(itemView);

        }
    }
}
