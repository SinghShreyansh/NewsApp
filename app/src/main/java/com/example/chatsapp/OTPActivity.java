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
    // Declaring variable
ActivityOtpactivityBinding binding;
FirebaseAuth auth;
ProgressDialog dialog;
String verificationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding OTPActivity.java to xml page
        binding=ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // removing toolbar
        getSupportActionBar().hide();
         // Creating progress bar for Showing Sending OTP...
        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending OTP ...");
        dialog.setCancelable(false);
        dialog.show();
        //Taking Mobile number entered on previous page
        String number=getIntent().getStringExtra("mobNum");
        //Showing mobile number on UI
        binding.tvMobNUM.setText("Verify "+number);
        // Taking firebaseAuth instance so we can access in FirebaseAuth
        auth=FirebaseAuth.getInstance();

        //Creating PhoneAuthOptions class object so after setting all required value
        // we can give it to verifyPhoneNumber class for verify the number and send the OTP
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
        // This will verify and send the OTP
        PhoneAuthProvider.verifyPhoneNumber(options);
        // This will invoke when you finish entering OTP
        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,otp);
                //This method will check OTP entered and OTP send(Verification ID) is matched or not
                // If it will match then it will move on next page
                //or it will show toast showing "Failed"
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
       // After OTP if verified you can click on continue button to move on next page
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