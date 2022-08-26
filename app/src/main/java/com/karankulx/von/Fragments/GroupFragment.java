package com.karankulx.von.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.extractor.ts.DtsReader;
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
    public int status = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        GroupAdapter adapter = new GroupAdapter(getContext(), groups);
        binding.chatRecyclerview.setAdapter(adapter);

        database.getReference().child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groups.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Groups group = dataSnapshot1.getValue(Groups.class);
                        ArrayList<Users> users = group.getUser();
                        for (Users users1 : users) {
                            if (uid.equals(users1.getUid())) {
                                status = 1;
                            };
                            if (status == 1) {
                                groups.add(group);
                            }
                            status = 0;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
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