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

        database= FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading profile...");
        dialog.setCancelable(false);

        binding.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,35);
            }
        });
        binding.btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString();
                if (name.isEmpty()){
                    binding.etName.setError("Please type a name");
                    return;
                }
                dialog.show();

                if (uri!=null){
                    StorageReference reference=storage.getReference().child("Profiles").child(auth.getUid());
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