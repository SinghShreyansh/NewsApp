package com.example.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chatsapp.Adapter.MessageAdapter;
import com.example.chatsapp.Models.Message;
import com.example.chatsapp.databinding.ActivityChatBinding;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    // Declaring variables
ActivityChatBinding binding;
MessageAdapter adapter;
ArrayList<Message> messages;
FirebaseDatabase database;
FirebaseStorage storage;

String senderRoom,receiverRoom;
ProgressDialog dialog;
String senderID,receiverID;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Taking instances
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        // Creating dialog box
        dialog= new ProgressDialog(this);
        dialog.setMessage("Uploading Image ...");
        dialog.setCancelable(false);

        // Taking intent which send by main page
        String name = getIntent().getStringExtra("name");
        String ProfileImg = getIntent().getStringExtra("image");
        String token =getIntent().getStringExtra("token");
        receiverID = getIntent().getStringExtra("uid");
        // taking  Uid from firebase
        senderID = FirebaseAuth.getInstance().getUid();
        // creatng senderRoom and recieverRoom
        senderRoom =senderID +receiverID;
        receiverRoom= receiverID + senderID;
        // creating new arraylist of msg class
        messages = new ArrayList<>();
        // setting adapter and recycler view
        adapter = new MessageAdapter(this,messages,senderRoom,receiverRoom);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);


          // setting toolbar which is created in xml
            setSupportActionBar(binding.toolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        // getSupportActionBar().setTitle(name);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          // Using glide library to load image online
           Glide.with(ChatActivity.this).load(ProfileImg).placeholder(R.drawable.ic_avatar_icon).into(binding.image);
           // setting  name of user
           binding.name.setText(name);
           // onClick listener on back arrow
           binding.backbtm.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   finish();
               }
           });
           // getting data from database that receiver is offline or online
           database.getReference().child("presence").child(receiverID).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.exists()){
                       String status = snapshot.getValue(String.class);
                       if (!status.isEmpty()){
                           if (status.equals("Offline")){
                               binding.indicator.setVisibility(View.GONE);
                           } else{
                           binding.indicator.setText(status);
                           binding.indicator.setVisibility(View.VISIBLE);}
                       }
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });


        // Taking  data of message from database into message arraylist
        database.getReference().child("chats").child(senderRoom).child("messages")
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


        // onClick Listener on send btn
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // taking typed msg int messageTxt var
                String messageTxt = binding.etMsg.getText().toString();
                // reseting "" in edittext
                binding.etMsg.setText("");
                // taking date object
                Date date =new Date();
                // creating new msg object with parameterized constructor
                Message message= new Message(messageTxt,senderID,date.getTime());

                String randomKey = database.getReference().push().getKey();

                // saving this new msg to database of users senderRoom and receiverRoom
                database.getReference().child("chats").child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification(name,message.getMessage(),token);

                            }
                        });
                        // creating hashmap object to store last msg and time
                        HashMap<String,Object> lastMsgObj = new HashMap<>();
                        lastMsgObj.put("lastMsg",message.getMessage());
                        lastMsgObj.put("lastMsgTime",date.getTime());
                        // saving this map object to database
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                    }
                });
            }
        });

        //onClick Listener on attach btn
        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,35);
            }
        });



//        binding.camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(this,CameraActivity.class);
//                startActivityForResult(intent,125);
//
//            }
//        });



    }

    void sendNotification(String name,String message,String token){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = " https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationData = new JSONObject();
            notificationData.put("notification",data);
            notificationData.put("to",token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String > map = new HashMap<>();
                    String key = "Key=AAAAHRzLBHc:APA91bFgC5fRuU7uYx0Wdp_7VQn-895pNWwiR0cIdTxpCJN3wCwsEpUqrsjYICOTl-94utxk6ULlZWcpBxoCiYeFiQG8rDmd6XQ03Vh9jp0uhqV0Wh0eCpXrRXHqvGtsLZVk4VMjAkAE";
                    map.put("Authorization",key );
                    map.put("Content-Type","application/json");
                    return map;
                }
            };
            queue.add(request);


        } catch(Exception e){

        }
    }

    // Code to save selected img to firebase storage and database
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


                                        database.getReference().child("chats").child(senderRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("chats").child(receiverRoom)
                                                        .child("messages")
                                                        .child(randomKey)
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                                HashMap<String,Object> lastMsgObj = new HashMap<>();
                                                lastMsgObj.put("lastMsg",message.getMessage());
                                                lastMsgObj.put("lastMsgTime",date.getTime());

                                                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });
                }
            }
        }






       // Code to save data in firebase that user is typing or not
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

    // tried to get image from camera but not working

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==125){
//            if (data!= null){
//                if (data.getData()!=null){
//                    Uri ChoosenImg = data.getData();
//                    Calendar calendar= Calendar.getInstance();
//                    StorageReference storageReference= storage.getReference().child("chats").child(calendar.getTimeInMillis()+ "");
//                    dialog.show();
//                    storageReference.putFile(ChoosenImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            dialog.dismiss();
//                            if (task.isSuccessful()){
//                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        String Imgpath = uri.toString();
//                                        binding.etMsg.setText("");
//                                        String messageTxt = binding.etMsg.getText().toString();
//
//
//                                        Date date =new Date();
//
//                                        Message message= new Message(messageTxt,senderID,date.getTime());
//                                        message.setImageUrl(Imgpath);
//
//
//                                        String randomKey = database.getReference().push().getKey();
//
//
//                                        database.getReference().child("chats").child(senderRoom)
//                                                .child("messages")
//                                                .child(randomKey)
//                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                database.getReference().child("chats").child(receiverRoom)
//                                                        .child("messages")
//                                                        .child(randomKey)
//                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void unused) {
//
//                                                    }
//                                                });
//                                                HashMap<String,Object> lastMsgObj = new HashMap<>();
//                                                lastMsgObj.put("lastMsg",message.getMessage());
//                                                lastMsgObj.put("lastMsgTime",date.getTime());
//
//                                                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
//                                                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
//                                            }
//                                        });
//
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }
//        }
//


    @Override
    protected void onResume() {
        super.onResume();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }


    @Override
    protected void onPause() {
        super.onPause();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}