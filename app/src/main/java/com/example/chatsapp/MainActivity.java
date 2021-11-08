package com.example.chatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatsapp.Adapter.MainChatAdapter;
import com.example.chatsapp.Models.Users;
import com.example.chatsapp.News.NewsMainActivity;
import com.example.chatsapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // declaring variables
  ActivityMainBinding binding;
  FirebaseDatabase database;
  ArrayList<Users> users;
  MainChatAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database=FirebaseDatabase.getInstance();
        // Taking arraylist of user class to store user msg details
        users=new ArrayList<>();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("token",token);
                        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                    }
                });
        // Passing arraylist of data and context to Adapter to show all required detail
        usersAdapter = new MainChatAdapter(this,users);
       // binding.RecyclerView.setLayoutManager(new LinearLayoutManager());   --> Already set in xml
        binding.RecyclerView.setAdapter(usersAdapter);
        // To shimmer effect whose library is added to gradle dependencies
        binding.RecyclerView.showShimmerAdapter();
        // Taking all the data from database and adding to arraylist and giving to userAdapter
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clearing previous data in arraylist
                users.clear();
                // Adding data from database node of current user to arraylist
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Users user = snapshot1.getValue(Users.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                    users.add(user);
                    }
                }
                // Once data receiving from database is completed , remove shimmer effect
                binding.RecyclerView.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //When MainActivity is resume or user is on this page
    @Override
    protected void onResume() {
        super.onResume();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }
    //When app is open in mobile but user is not on that app
    @Override
    protected void onPause() {
        super.onPause();
        String currentId= FirebaseAuth.getInstance().getUid();
       database.getReference().child("presence").child(currentId).setValue("Offline");
    }
    // Created menu with switch case to choose the page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.topSetting:
                Toast.makeText(this, "Topsetting clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Groups:
                Toast.makeText(this, "Groups clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,GroupChatActivity.class));
                break;
            case R.id.Chats:
                Toast.makeText(this, "You are in Chat Activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.News:
                Toast.makeText(this, "Invite Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, NewsMainActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // Taking menu from xml file to java show that it is shown
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}