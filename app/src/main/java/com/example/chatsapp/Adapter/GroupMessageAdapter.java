package com.example.chatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Models.Message;
import com.example.chatsapp.Models.Users;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ItemreceiveGroupBinding;
import com.example.chatsapp.databinding.ItemsendGroupBinding;
import com.example.chatsapp.databinding.SampleReceiverBinding;
import com.example.chatsapp.databinding.SampleSenderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupMessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;

    public GroupMessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }
    // inflating raw_conversation layout and passing
    // to respective ViewHolder class to set binding
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.itemsend_group,parent,false);
            return new SenderViewHolder(view);
        } else{
            View view = LayoutInflater.from(context).inflate(R.layout.itemreceive_group,parent,false);
            return new ReceiverViewHolder(view);
        }

    }

    // method which will return view type
    @Override
    public int getItemViewType(int position) {
        Message message= messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderID())){
            return  ITEM_SENT;
        } else{
            return  ITEM_RECEIVE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        // if msg is by sender then setting msg with help of SenderViewHolder
        if (holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(message.getSenderID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Users user = snapshot.getValue((Users.class));
                                // Setting sender name
                                viewHolder.binding.name.setText("@"+user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            // if msg is in img form then setting visibility of text and img
            if (message.getMessage().equals("")){
                viewHolder.binding.senderText.setVisibility(View.GONE);
                viewHolder.binding.imageTxt.setVisibility(View.VISIBLE);

                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.ic_avatar_icon).into(viewHolder.binding.imageTxt);
            } else{
                viewHolder.binding.senderText.setVisibility(View.VISIBLE);
                viewHolder.binding.imageTxt.setVisibility(View.GONE);

                viewHolder.binding.senderText.setText(message.getMessage());

            }



        } else{
            // if msg is received then setting msg with help of ReceiverViewHolder
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(message.getSenderID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Users user = snapshot.getValue((Users.class));
                                viewHolder.binding.name.setText("@"+user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            if (message.getMessage().equals("")){
                viewHolder.binding.imageTxt.setVisibility(View.VISIBLE);
                viewHolder.binding.receiverText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.ic_avatar_icon).into(viewHolder.binding.imageTxt);
            }
            else{
                viewHolder.binding.receiverText.setVisibility(View.VISIBLE);
                viewHolder.binding.imageTxt.setVisibility(View.GONE);

                viewHolder.binding.receiverText.setText(message.getMessage());

            }
        }

    }
    // to set the count of item to which adapter will work
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // binding with ItemsendGroup xml
    public class SenderViewHolder extends RecyclerView.ViewHolder {
       ItemsendGroupBinding binding;
       public SenderViewHolder(@NonNull View itemView) {
           super(itemView);
           binding=ItemsendGroupBinding.bind(itemView);
       }
   }
    // binding with ItemreceiveGroup xml
   public class ReceiverViewHolder extends RecyclerView.ViewHolder {
       ItemreceiveGroupBinding binding;
       public ReceiverViewHolder(@NonNull View itemView) {
           super(itemView);
           binding=ItemreceiveGroupBinding.bind(itemView);
       }
   }
}
