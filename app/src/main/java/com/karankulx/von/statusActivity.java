package com.karankulx.von;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Adapter.DeleteStatusAdapter;
import com.karankulx.von.Models.Status;
import com.karankulx.von.databinding.ActivityStatusBinding;

import java.util.ArrayList;

public class statusActivity extends AppCompatActivity {

    ActivityStatusBinding binding;
    FirebaseDatabase database;
    DeleteStatusAdapter deleteStatusAdapter;
    public ArrayList<Status> myStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        myStatus = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        deleteStatusAdapter = new DeleteStatusAdapter(this, myStatus);
        binding.statusRecyclerview.setAdapter(deleteStatusAdapter);

        database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myStatus.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Status temp = dataSnapshot.getValue(Status.class);
                                String key = dataSnapshot.getKey();
                                temp.setKey(key);
                                database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses").child(key).setValue(temp);
                                myStatus.add(temp);
                            };
                        };

                        if (deleteStatusAdapter != null) {
                            deleteStatusAdapter.notifyDataSetChanged();
                        };

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}