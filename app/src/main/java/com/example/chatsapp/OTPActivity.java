package com.example.chatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.chatsapp.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
ActivityOtpactivityBinding binding;
FirebaseAuth auth;
ProgressDialog dialog;

String verificationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending OTP ...");
        dialog.setCancelable(false);
        dialog.show();

        String number=getIntent().getStringExtra("mobNum");
        binding.tvMobNUM.setText("Verify "+number);

        auth=FirebaseAuth.getInstance();
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);
                        dialog.dismiss();
                        verificationID=verifyID;
                        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        binding.otpView.requestFocus();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
        
        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,otp);
                
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(OTPActivity.this, "Logged in.", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(OTPActivity.this,ProfileSetupActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else{
                            Toast.makeText(OTPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OTPActivity.this,ProfileSetupActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }
}