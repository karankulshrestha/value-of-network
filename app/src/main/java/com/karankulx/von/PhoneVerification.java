package com.karankulx.von;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivityPhoneVerificationBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {

    ActivityPhoneVerificationBinding binding;
    private String verificationId;
    private String imageUri, name, email, password, phoneNumber, status, uid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    ProgressDialog progressDialog;
    ProgressDialog resendProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        name = getIntent().getStringExtra("name");
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        status = getIntent().getStringExtra("status");
        imageUri = getIntent().getStringExtra("profileUri");


        progressDialog = new ProgressDialog(PhoneVerification.this);
        progressDialog.setTitle("verifying Otp");
        progressDialog.setMessage("we are verifying your phone number.");

        resendProgressBar = new ProgressDialog(PhoneVerification.this);
        resendProgressBar.setTitle("resending Otp");
        resendProgressBar.setMessage("resending Otp to your phone number.");

        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        binding.phoneDisplay.setText("Otp sent to +91 " + phoneNumber);

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                binding.timer.setText(f.format(min) + ":" + f.format(sec));
            }
            public void onFinish() {
                binding.timer.setText("00:00");
                binding.resendBtn.setVisibility(View.VISIBLE);
            }
        }.start();

        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String code = binding.firstPinView.getText().toString();

                if(TextUtils.isEmpty(code)) {
                    progressDialog.dismiss();
                    Toast.makeText(PhoneVerification.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                }

                if(verificationId != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Users user = new Users(imageUri, name, email, password, password, phoneNumber, status, uid);
                                        database.getReference().child("users")
                                                .child(uid).setValue(user);

                                        Intent intent = new Intent(PhoneVerification.this, SignIn.class);
                                        startActivity(intent);
                                        Toast.makeText(PhoneVerification.this, "Sing Up successFull.", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        finish();

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(PhoneVerification.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PhoneVerification.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        binding.resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendProgressBar.show();

                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        resendProgressBar.dismiss();
                        return;
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        resendProgressBar.dismiss();
                        Toast.makeText(PhoneVerification.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String newVerificationId, PhoneAuthProvider.ForceResendingToken token) {
                        resendProgressBar.dismiss();
                        verificationId = newVerificationId;
                        Toast.makeText(PhoneVerification.this, "OTP Send Successfully!", Toast.LENGTH_SHORT).show();
                    }
                };

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91"+phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(PhoneVerification.this)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }

        });
    }
}