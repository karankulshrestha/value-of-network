package com.karankulx.von.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
//import com.karankulx.von.Adapter.GroupAdapter;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Adapter.GroupAdapter;
import com.karankulx.von.Adapter.UsersAdapter;
import com.karankulx.von.ContactSync;
import com.karankulx.von.Models.Groups;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.FragmentChatsBinding;

import java.util.ArrayList;


public class GroupFragment extends Fragment {

    public GroupFragment() {
        // Required empty public constructor
    }
    FragmentChatsBinding binding;
    public String uid;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    ArrayList<Groups> groups = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();



        GroupAdapter adapter = new GroupAdapter(getContext(), groups);
        binding.chatRecyclerview.setAdapter(adapter);

        database.getReference().child("groups").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Groups group = dataSnapshot.getValue(Groups.class);
                    groups.add(group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.normalFAB.setOnClickListener((View v) -> {
            startActivity(new Intent(getActivity(), ContactSync.class));
        });

        return binding.getRoot();
    }

}