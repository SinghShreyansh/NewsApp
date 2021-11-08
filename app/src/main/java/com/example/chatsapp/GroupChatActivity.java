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
    // Declaring variables
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
        // To set actionbar title
        getSupportActionBar().setTitle("Group Chat");
        // To show title on action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        // To show back arrow on left of toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // Getting Your own Uid(sender id) from firebase auth
        senderID= FirebaseAuth.getInstance().getUid();
        // getting instances
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        // code for dialog box
        dialog= new ProgressDialog(this);
        dialog.setMessage("Uploading Image ...");
        dialog.setCancelable(false);
        // creating new arraylist of message class
        messages = new ArrayList<>();
        // sending arraylist of message class to message adapter
        adapter=new GroupMessageAdapter(this,messages);
        // Activating recyclerview to show msg on this page with the help of msg adapter
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

        // Taking all the message data of group chat from database to msg typed arraylist
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
        // Onclick listener on send button
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // taking typed msg in messageTxt variable
                String messageTxt = binding.etMsg.getText().toString();
                // reseting typed msg to ""
                binding.etMsg.setText("");
                // Creating object of date class
                Date date =new Date();
                // creating object of msg type and setting all values
                Message message= new Message(messageTxt,senderID,date.getTime());
                // saving current msg details to database of group chat
                database.getReference().child("public")
                        .push()
                        .setValue(message);

            }
        });
        // onClick listener on attach btm
        //Passing intent to file which content images in device
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
    // Creating onActivityResult to save selected img to firebase storage
    // and showing in group chat activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Checking request code
        if (requestCode==35 || requestCode==125){
            // Checking any img is there in device file or not
            if (data!= null){
                // checking any img is selected or not
                if (data.getData()!=null){
                    // Taking selected img url to ChoosenImg variable which is of Uri type
                    Uri ChoosenImg = data.getData();
                    // Creating calender object and taking instance
                    Calendar calendar= Calendar.getInstance();
                    // Creating node in firebase storage with help of timestamp
                    StorageReference storageReference= storage.getReference().child("chats").child(calendar.getTimeInMillis()+ "");
                    // Showing dialog box showing uploading img...
                    dialog.show();
                    // adding Complete listener to check img is stored in storage
                    storageReference.putFile(ChoosenImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                // Once image is stored in storage downloading img url
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // once url is got , store in Imgpath variable and
                                        // setting all things as text is send
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


  // To go to back page when clicked on toolbar back arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}