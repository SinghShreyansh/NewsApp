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
        // To check if user is already login in then take the user to main page
        if (auth.getCurrentUser()!=null){
            Intent intent=new Intent(LoginActivity.this, NewsMainActivity.class);
            startActivity(intent);
            finish();
        }
        //To hide above toolbar
        getSupportActionBar().hide();
        //To open numerical keypad when user click on edittext box
        binding.etMobNum.requestFocus();


        //to set a click listener on continue button
        binding.btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Taking entered mobile number and converting to string
                String number = binding.etMobNum.getText().toString();
                //Passing intent to go on OTP verification page
                Intent intent = new Intent(LoginActivity.this,OTPActivity.class);
                //Passing mobile number from this page to otp verification page to show on UI
                intent.putExtra("mobNum",number);
                startActivity(intent);
            }
        });
    }
}