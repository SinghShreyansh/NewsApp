package com.example.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.chatsapp.Adapter.GroupMessageAdapter;
import com.example.chatsapp.Adapter.MessageAdapter;
import com.example.chatsapp.Models.Message;
import com.example.chatsapp.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    GroupMessageAdapter adapter;
    ArrayList<Message> messages;
    FirebaseDatabase database;
    FirebaseStorage storage;

    String senderRoom,receiverRoom;
    ProgressDialog dialog;

    String senderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Group Chat");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        senderID= FirebaseAuth.getInstance().getUid();

        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog= new ProgressDialog(this);
        dialog.setMessage("Uploading Image ...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();
        adapter=new GroupMessageAdapter(this,messages);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

        database.getReference().child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Message message=  snapshot1.getValue(Message.class);
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageTxt = binding.etMsg.getText().toString();
                binding.etMsg.setText("");
                Date date =new Date();

                Message message= new Message(messageTxt,senderID,date.getTime());

                database.getReference().child("public")
                        .push()
                        .setValue(message);

            }
        });

        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,35);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==35 || requestCode==125){
            if (data!= null){
                if (data.getData()!=null){

                    Uri ChoosenImg = data.getData();

                    Calendar calendar= Calendar.getInstance();
                    StorageReference storageReference= storage.getReference().child("chats").child(calendar.getTimeInMillis()+ "");
                    dialog.show();
                    storageReference.putFile(ChoosenImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String Imgpath = uri.toString();
                                        binding.etMsg.setText("");
                                        String messageTxt = binding.etMsg.getText().toString();


                                        Date date =new Date();

                                        Message message= new Message(messageTxt,senderID,date.getTime());
                                        message.setImageUrl(Imgpath);


                                        String randomKey = database.getReference().push().getKey();


                                        database.getReference().child("public")
                                                .push()
                                                .setValue(message);

                                    }
                                });
                            }
                        }
                    });
                }
            }
        }







        final Handler handler = new Handler();
        binding.etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence").child(FirebaseAuth.getInstance().getUid()).setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(usersStoppedTyping,1000);

            }
            Runnable usersStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderID).setValue("Online");
                }
            };
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}