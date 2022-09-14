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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivityInProfileBinding;


public class InProfile extends AppCompatActivity {

    ActivityInProfileBinding binding;
    DatabaseReference reference;
    Uri selectedImage;
    EditText name;
    String imageUrl;
    String uid;
    ImageView profile;
    EditText status;
    TextView phoneNumber;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(InProfile.this);
        progressDialog.setTitle("updating..");
        progressDialog.setMessage("please wait.");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = binding.nameField;
        status = binding.statusField;
        phoneNumber = binding.phoneField;
        profile = binding.profileImage;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        reference.keepSynced(true);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                imageUrl = users.getProfilePic();
                name.setText(users.getName());
                phoneNumber.setText(users.getPhoneNumber());
                status.setText(users.getStatus());
                Glide.with(InProfile.this)
                        .load(imageUrl)
                        .into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startForResult.launch(intent);
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(selectedImage != null) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profiles").child(uid);
                    storageReference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                                        userRef.child(uid).child("profilePic").setValue(uri.toString());
                                        userRef.child(uid).child("name").setValue(name.getText().toString());
                                        userRef.child(uid).child("status").setValue(status.getText().toString());

                                        Toast.makeText(InProfile.this, "profile updated.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
                } else if(selectedImage == null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                    userRef.child(uid).child("name").setValue(name.getText().toString());
                    userRef.child(uid).child("status").setValue(status.getText().toString());
                    progressDialog.dismiss();
                    Toast.makeText(InProfile.this, "profile updated.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(InProfile.this, "profile already updated.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}