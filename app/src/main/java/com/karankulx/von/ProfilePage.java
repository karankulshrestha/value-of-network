package com.karankulx.von;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivityProfilePageBinding;

import java.util.concurrent.TimeUnit;

public class ProfilePage extends AppCompatActivity {

    ActivityProfilePageBinding binding;

    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog progressDialog;

    String email, password, name, phoneNumber, status, imageUri, uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(ProfilePage.this);
        progressDialog.setTitle("Creating Profile");
        progressDialog.setMessage("we are creating your profile.");


        ActivityResultLauncher<Intent> startForResult = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    binding.profileImage.setImageURI(result.getData().getData());
                    selectedImage = result.getData().getData();
                }
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startForResult.launch(intent);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = getIntent().getStringExtra("email");
                password = getIntent().getStringExtra("password");
                uid = getIntent().getStringExtra("uid");
                name = binding.nameField.getText().toString();
                phoneNumber = binding.phoneField.getText().toString();
                status = binding.statusField.getText().toString();

                progressDialog.show();
                if(selectedImage != null) {
                    if (phoneNumber.matches("^\\d{10}$")) {
                        if(!name.isEmpty()) {
                            if(!status.isEmpty()) {

                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                                userRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null){
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfilePage.this, "user with this number already exist", Toast.LENGTH_SHORT).show();

                                        } else {
                                            StorageReference reference = storage.getReference().child("profiles").child(uid);
                                            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ProfilePage.this, "Profile added successfully", Toast.LENGTH_SHORT).show();
                                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                imageUri = uri.toString();
                                                                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider
                                                                        .OnVerificationStateChangedCallbacks() {
                                                                    @Override
                                                                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                                                                        progressDialog.dismiss();

                                                                    }

                                                                    @Override
                                                                    public void onVerificationFailed(FirebaseException e) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(ProfilePage.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    @Override
                                                                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                                                                        progressDialog.dismiss();
                                                                        Intent intent = new Intent(ProfilePage.this, PhoneVerification.class);
                                                                        intent.putExtra("verificationId", verificationId);
                                                                        intent.putExtra("email", email);
                                                                        intent.putExtra("password", password);
                                                                        intent.putExtra("name", name);
                                                                        intent.putExtra("status", status);
                                                                        intent.putExtra("phoneNumber", phoneNumber);
                                                                        intent.putExtra("profileUri", imageUri);
                                                                        intent.putExtra("uid", uid);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                };

                                                                PhoneAuthOptions options =
                                                                        PhoneAuthOptions.newBuilder(mAuth)
                                                                                .setPhoneNumber("+91" + phoneNumber)
                                                                                .setTimeout(60L, TimeUnit.SECONDS)
                                                                                .setActivity(ProfilePage.this)
                                                                                .setCallbacks(mCallbacks)
                                                                                .build();
                                                                PhoneAuthProvider.verifyPhoneNumber(options);

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ProfilePage.this, "add status!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ProfilePage.this, "add name!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ProfilePage.this, "Enter valid Number.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ProfilePage.this, "Select profile pic.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}