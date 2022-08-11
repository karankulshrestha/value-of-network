package com.karankulx.von;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karankulx.von.Fragments.GroupFragment;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivityGroupBuildingBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

public class GroupBuildingActivity extends AppCompatActivity {

    ActivityGroupBuildingBinding binding;
    public FirebaseDatabase database;
    Uri selectedImage;
    public ArrayList<Users> gUsers;
    public String uid;
    public String groupName, groupSummary;
    ProgressDialog progressDialog;
    private TextView gName, gSum;
    public FirebaseStorage storage;
    public DatabaseReference mDatabase;
    public ActivityResultLauncher<Intent> startForResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupBuildingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentFirebaseUser.getUid();

        gName = binding.gName;
        gSum = binding.gSum;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("creating group..");
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        gUsers = intent.getParcelableArrayListExtra("gUser list");
        Log.d("james420", String.valueOf(gUsers.size()));
        for (Users user : gUsers) {
            Log.d("sames420", user.getName());
        }


        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        startForResult = registerForActivityResult
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

        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String rand = getRandomString(10);
                groupName = gName.getText().toString();
                groupSummary = gSum.getText().toString();
                if (gName.length() != 0) {
                    if (gSum.length() != 0) {
                        if (selectedImage != null) {

                            database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Users user = dataSnapshot.getValue(Users.class);
                                        String mUid = user.getUid();
                                        if (mUid.equals(uid)) {
                                            gUsers.add(user);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            Calendar calender = Calendar.getInstance();
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                byte[] data = baos.toByteArray();
                                StorageReference reference = storage.getReference().child("groups").child("profile-photos").child(calender.getTimeInMillis() + "");
                                reference.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String filePath = uri.toString();
                                                    Glide.with(GroupBuildingActivity.this).load(filePath).diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .into(binding.profileImage);

                                                    for (Users user : gUsers) {
                                                        mDatabase.child("groups").child(uid).child(groupName + "-" + rand).child("group Name").setValue(groupName);
                                                        mDatabase.child("groups").child(uid).child(groupName + "-" + rand).child("group Summary").setValue(groupSummary);
                                                        mDatabase.child("groups").child(uid).child(groupName + "-" + rand).child("group profile").setValue(filePath);
                                                        mDatabase.child("groups").child(uid).child(groupName + "-" + rand).child("group Members").push().setValue(user);
                                                        Toast.makeText(GroupBuildingActivity.this, "group created", Toast.LENGTH_SHORT).show();
                                                        Intent mainIntent = new Intent(GroupBuildingActivity.this, HomeActivity.class);
                                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        Log.d("james420", String.valueOf(selectedImage));
                                                        startActivity(mainIntent);
                                                    }

                                                    progressDialog.dismiss();

                                                }
                                            });
                                        }
                                    }
                                });

                            } catch (IOException e) {
                                Toast.makeText(GroupBuildingActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(GroupBuildingActivity.this, "Please select group Image", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(GroupBuildingActivity.this, "Please enter group summary", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(GroupBuildingActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}