package com.example.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.chatsapp.Models.Users;
import com.example.chatsapp.News.NewsMainActivity;
import com.example.chatsapp.databinding.ActivityProfileSetupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileSetupActivity extends AppCompatActivity {
ActivityProfileSetupBinding binding;
FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
Uri uri;
ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        // Getting instances
        database= FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        // Dialogue box
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading profile...");
        dialog.setCancelable(false);
        // On click listener on Plus button
        // to select image from gallery
        binding.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,35);
            }
        });
        // On click listener on Continue button
        binding.btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Taking typed text from edittext to name variable
                String name = binding.etName.getText().toString();
                //If user is not entered any text and clicking on continue btm
                // then this will show error
                if (name.isEmpty()){
                    binding.etName.setError("Please type a name");
                    return;
                }
                dialog.show();
                // To add selected img to firebase Storage and show on UI and add userdata on database
                if (uri!=null){
                    // Taking reference from firebase storage to make one node with user ID to save img data
                    StorageReference reference=storage.getReference().child("Profiles").child(auth.getUid());
                    // adding onComplete listener on img storage and getting image url from firebase
                    reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String uid= auth.getUid();
                                        String name = binding.etName.getText().toString();
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        String imageUrl = uri.toString();

                                        Users user = new Users(uid,name,phone,imageUrl);
                                        // Adding userdata to firebase database
                                        database.getReference().child("users")
                                                .child(auth.getUid())
                                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Intent intent =new Intent(ProfileSetupActivity.this, NewsMainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    // Adding userdata to database if user not select any img
                    String uid= auth.getUid();
                    name = binding.etName.getText().toString();
                    String phone = auth.getCurrentUser().getPhoneNumber();
                    String imageUrl = "No Image";

                    Users user = new Users(uid,name,phone,imageUrl);
                    database.getReference().child("users")
                            .child(auth.getUid())
                            .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            Intent intent =new Intent(ProfileSetupActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

    }
    // onActivityResult to get image url
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if (data.getData()!=null){
                binding.ProfileImage.setImageURI(data.getData());
                uri=data.getData();

            }
        }
    }
}