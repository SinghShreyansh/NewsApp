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
    Context context;
    ArrayList<Message> messages;
    String senderRoom , receiverRoom;

    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;

    public MessageAdapter(Context context, ArrayList<Message> messages,String senderRoom,String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }

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

        if (holder.getClass() == SenderViewHolder.class){

            SenderViewHolder viewHolder = (SenderViewHolder)holder;

           // Delete chat method
           //            viewHolder.binding.msgLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                   // showing delete msg confirm dialog
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Delete");
//                    builder.setMessage("Are you sure to delete this message /");
//                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            deleteMessage(position);
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    builder.create().show();
//
//                }
//            });  //  --

            if (message.getMessage().equals("")){
                viewHolder.binding.senderText.setVisibility(View.GONE);
                viewHolder.binding.imageTxt.setVisibility(View.VISIBLE);

                Glide.with(context).load(message.getImageUrl())
                       .into(viewHolder.binding.imageTxt);
            } else{
                viewHolder.binding.senderText.setVisibility(View.VISIBLE);
                viewHolder.binding.imageTxt.setVisibility(View.GONE);
                viewHolder.binding.senderText.setText(message.getMessage());

            }



        }
        else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            if (message.getMessage().equals("")){
                viewHolder.binding.imageTxt.setVisibility(View.VISIBLE);
                viewHolder.binding.receiverText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .into(viewHolder.binding.imageTxt);
            }
            else{
                viewHolder.binding.receiverText.setVisibility(View.VISIBLE);
                viewHolder.binding.imageTxt.setVisibility(View.GONE);
                viewHolder.binding.receiverText.setText(message.getMessage());

            }
        }



    }
//Delete method
//    private void deleteMessage(int position) {
//
//
//        String senderId= FirebaseAuth.getInstance().getUid();
//        Long msgTimeStamp =  messages.get(position).getTimestamp();
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("chats").child("senderRoom");
//        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snapshot1:snapshot.getChildren()){
//                    if (snapshot1.child("senderID").getValue().equals(senderId)) {
//                        //1.)Remove the message from chats
//                       // snapshot1.getRef().removeValue();
//                        //2.)Set the value of message from chats
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put("message", "This message was deleted ...");
//                        snapshot1.getRef().updateChildren(map);
//                    }else {
//                        Toast.makeText(context,"You can delete only your message...",Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
       SampleSenderBinding binding;
       public SenderViewHolder(@NonNull View itemView) {
           super(itemView);
           binding=SampleSenderBinding.bind(itemView);
       }
   }
   public class ReceiverViewHolder extends RecyclerView.ViewHolder {
       SampleReceiverBinding binding;
       public ReceiverViewHolder(@NonNull View itemView) {
           super(itemView);
           binding=SampleReceiverBinding.bind(itemView);
       }
   }
}
