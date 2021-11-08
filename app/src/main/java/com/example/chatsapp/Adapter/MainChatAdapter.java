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

    // Declaring variables
    Context context;
    ArrayList<Users> users;


    // setting construction
    public MainChatAdapter(Context context,ArrayList<Users> users){
        this.context=context;
        this.users=users;

    }

    // inflating raw_conversation layout and passing to UsersViewHolder class to set binding
    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.raw_conversation,parent,false);

        return new UsersViewHolder(view);
    }

    // Once you have access of every element through binding ,
    // you can access it and can set according to you
    // this will create every element of MainActivity
    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        // getting position of users available in arraylist
        // received from database(firebase database)
    Users user = users.get(position);
    String senderID = FirebaseAuth.getInstance().getUid();

    String senderRoom = senderID + user.getUid();

    // getting all data of current user from senderRoom in database
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // getting and setting lastmsg

                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                           // long time = snapshot.child("lastMsgTime").getValue(Long.class);

                            holder.binding.tvLastMsg.setText(lastMsg);
                           // holder.binding.tvMsgTime.setText((int) time);
                        }else {
                            // in-case lastmsg is null
                            holder.binding.tvLastMsg.setText("Tap to chat");
                            holder.binding.tvMsgTime.setText("");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // setting users name
    holder.binding.tvUsername.setText(user.getName());

    // setting users profile pic with help of glide
        Glide.with(context).load(user.getProfilePic())
                .placeholder(R.drawable.ic_avatar_icon)
                .into(holder.binding.imageView2);

        // setting onClick on each user
        // to pass user data to chatActivity and open it
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

    // to set the count of item to which adapter will work
    @Override
    public int getItemCount() {
        return users.size();
    }

    // setting Viewholder which will be responsible for binding
    public class UsersViewHolder extends RecyclerView.ViewHolder{

            RawConversationBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= RawConversationBinding.bind(itemView);

        }
    }
}
