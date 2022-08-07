package com.karankulx.von;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SignalStrengthUpdateRequest;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivitySingUpBinding;

public class SingUp extends AppCompatActivity {

    ActivitySingUpBinding binding;

    FirebaseAuth mAuth;

    String email;
    String password2;
    String password3;
    String uid;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(SingUp.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("we are creating your account.");

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                email = binding.SingupPassField.getText().toString();
                password2 = binding.editTextTextPassword2.getText().toString();
                password3 = binding.editTextTextPassword3.getText().toString();

                if(email.matches("^(.+)@(\\S+)$")) {
                    if(password2.length() != 0) {
                        if(password3.length() != 0) {
                            if (password2.matches(password3)) {
                                mAuth.createUserWithEmailAndPassword(email, password2)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()) {
                                                    uid = mAuth.getCurrentUser().getUid();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(SingUp.this, ProfilePage.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("password", password2);
                                                    intent.putExtra("uid", uid);
                                                    startActivity(intent);
                                                    mAuth.signOut();
                                                    finish();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SingUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(SingUp.this, "password must matched", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SingUp.this, "Enter Credentials Properly", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SingUp.this, "Enter Credentials Properly", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SingUp.this, "Enter email Properly", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingUp.this, SignIn.class);
                startActivity(intent);
            }
        });

    }
}