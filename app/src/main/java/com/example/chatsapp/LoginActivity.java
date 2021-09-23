package com.example.chatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatsapp.News.NewsMainActivity;
import com.example.chatsapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;

public class LoginActivity extends AppCompatActivity {
ActivityLoginBinding binding;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth= FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            Intent intent=new Intent(LoginActivity.this, NewsMainActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();
        binding.etMobNum.requestFocus();



        binding.btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = binding.etMobNum.getText().toString();
                Intent intent = new Intent(LoginActivity.this,OTPActivity.class);
                intent.putExtra("mobNum",number);
                startActivity(intent);
            }
        });
    }
}