package com.karankulx.von;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karankulx.von.Adapter.ContactAdapter;
import com.karankulx.von.Adapter.FragmentsAdapter;
import com.karankulx.von.Adapter.topStatusAdapter;
import com.karankulx.von.Fragments.ChatsFragment;
import com.karankulx.von.Models.Contact;
import com.karankulx.von.Models.Status;
import com.karankulx.von.Models.Users;
import com.karankulx.von.Models.userStatus;
import com.karankulx.von.databinding.ActivityHomeBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    public FirebaseAuth firebaseAuth;
    public topStatusAdapter statusAdapter;
    public ArrayList<userStatus> userStatuses;
    ProgressDialog progressDialog;
    public FirebaseDatabase database;
    public StorageReference storageReference;
    Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading status");
        progressDialog.setCancelable(false);
        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();


        userStatuses = new ArrayList<>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        statusAdapter = new topStatusAdapter(this, userStatuses);
        binding.statusList.setAdapter(statusAdapter);

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String profilePic = snapshot.child("profilePic").getValue(String.class);

                        database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                                .child("name").setValue(name);
                        database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                                .child("profileImage").setValue(profilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userStatuses.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if(snapshot1.exists()) {
                            userStatus us = new userStatus();
                            String uid = snapshot1.getKey();
                            us.setName(snapshot1.child("name").getValue(String.class));
                            us.setProfileImage(snapshot1.child("profileImage").getValue(String.class));
                            us.setLastUpdated(snapshot1.child("lastUpdated").getValue(Long.class));

                            ArrayList<Status> statuses = new ArrayList<>();

                            if (snapshot1.child("statuses").exists()) {
                                for (DataSnapshot statusSnapshot : snapshot1.child("statuses").getChildren()) {
                                    Status sampleStatus = statusSnapshot.getValue(Status.class);
                                    statuses.add(sampleStatus);
                                };

                                us.setStatuses(statuses);
                                if (FirebaseAuth.getInstance().getUid().equals(uid)) {
                                    userStatuses.add(0, us);
                                } else  {
                                    userStatuses.add(us);
                                }
                            };
                            if (statusAdapter != null) {
                                statusAdapter.notifyDataSetChanged();
                            };
                        };
                    };

                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                Intent intent = new Intent(HomeActivity.this, InProfile.class);
                startActivity(intent);
                break;

            case R.id.signOut:
                Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
                firebaseAuth.signOut();
                finish();
                break;

            case R.id.setStatus:
                showDialog(this, "My Status", "select the relevent option for you");
                break;

            case R.id.mySheet:
                Intent intent1 = new Intent(HomeActivity.this, productsPage.class);
                intent1.putExtra("userId", firebaseAuth.getUid());
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("NEW STATUS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent1, 75);
            }
        });
        builder.setNegativeButton("DELETE STATUS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomeActivity.this, statusActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {
                progressDialog.show();
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                Uri uri = data.getData();
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] data1 = baos.toByteArray();
                    Date d = new Date();
                    storageReference = firebaseStorage.getReference().child("status").child(d.getTime() + "");
                    storageReference.putBytes(data1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                user = snapshot.getValue(Users.class);
                                                userStatus us = new userStatus();
                                                us.setName(user.getName());
                                                us.setProfileImage(user.getProfilePic());
                                                us.setLastUpdated(d.getTime());

                                                Log.d("jaana", String.valueOf(uri));

                                                HashMap<String, Object> obj = new HashMap<>();
                                                obj.put("name", us.getName());
                                                obj.put("profileImage", us.getProfileImage());
                                                obj.put("lastUpdated", us.getLastUpdated());

                                                String imageUrl = uri.toString();
                                                Status status = new Status(imageUrl, us.getLastUpdated(), FirebaseAuth.getInstance().getUid());

                                                db.getReference().child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .updateChildren(obj);

                                                db.getReference().child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("statuses")
                                                        .push()
                                                        .setValue(status);

                                                progressDialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            } else {
                                Log.d("jaana", task.getException().toString());
                            };
                        }
                    });
                } catch (IOException e) {
                    Log.d("jaana", e.getMessage().toString());
                }
            };
        };
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}