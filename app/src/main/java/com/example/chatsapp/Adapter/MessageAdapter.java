package com.example.chatsapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Models.Message;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.SampleReceiverBinding;
import com.example.chatsapp.databinding.SampleSenderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageAdapter extends RecyclerView.Adapter {
    // declaring  variables
    Context context;
    ArrayList<Message> messages;
    String senderRoom , receiverRoom;

    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;

    // setting constructor
    public MessageAdapter(Context context, ArrayList<Message> messages,String senderRoom,String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }

    // inflating raw_conversation layout and
    // passing to respective ViewHolder class to set binding
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        } else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Message message = messages.get(position);

        // checking class of msg to set the content
        // according to msg type(receiver msg or sender msg)
        if (holder.getClass() == SenderViewHolder.class){

            SenderViewHolder viewHolder = (SenderViewHolder)holder;

            // if msg is in img form then setting visibility of text and img
            if (message.getMessage().equals("")){
                viewHolder.binding.senderText.setVisibility(View.GONE);
                viewHolder.binding.imageTxt.setVisibility(View.VISIBLE);

                Glide.with(context).load(message.getImageUrl())
                       .into(viewHolder.binding.imageTxt);
            } else{
                // if it is text
                viewHolder.binding.senderText.setVisibility(View.VISIBLE);
                viewHolder.binding.imageTxt.setVisibility(View.GONE);
                viewHolder.binding.senderText.setText(message.getMessage());

            }



        }
        else{
            // checking class of msg to set the content
            // according to msg type(receiver msg or sender msg)
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            if (message.getMessage().equals("")){
                // if msg is in img form then setting visibility of text and img
                viewHolder.binding.imageTxt.setVisibility(View.VISIBLE);
                viewHolder.binding.receiverText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .into(viewHolder.binding.imageTxt);
            }
            else{
                // if it is text
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

    // binding with SampleSender xml
    public class SenderViewHolder extends RecyclerView.ViewHolder {
       SampleSenderBinding binding;
       public SenderViewHolder(@NonNull View itemView) {
           super(itemView);
           binding=SampleSenderBinding.bind(itemView);
       }
   }
    // binding with SampleReceiver xml
   public class ReceiverViewHolder extends RecyclerView.ViewHolder {
       SampleReceiverBinding binding;
       public ReceiverViewHolder(@NonNull View itemView) {
           super(itemView);
           binding=SampleReceiverBinding.bind(itemView);
       }
   }
}
