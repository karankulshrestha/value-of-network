package com.karankulx.von.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Adapter.UsersAdapter;
import com.karankulx.von.ContactSync;
import com.karankulx.von.Models.Contact;
import com.karankulx.von.Models.Message;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.FragmentChatsBinding;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class ChatsFragment extends Fragment  {

    public ChatsFragment() {
        // Required empty public constructor
    }

    Context context;
    FragmentChatsBinding binding;
    public UsersAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Map<Date, String> mainUsers = new HashMap<>();
    ArrayList<String> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase.getReference().child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (firebaseAuth.getUid().equals(snapshot1.getKey().substring(0, 28))) {
                        Long time = (Long) snapshot1.child("lastMsgTime").getValue();
                        Timestamp stamp = new Timestamp(time);
                        Date date = new Date(stamp.getTime());
                        mainUsers.put(date, snapshot1.getKey().substring(28, 56));
                        Log.d("yomen", String.valueOf(snapshot1.child("lastMsgTime").getValue()));
                    };
                };


                for (Map.Entry<Date, String> entry : mainUsers.entrySet()) {
                    Log.d("lole", String.valueOf(entry.getKey()));
                    users.add(entry.getValue());
                };



                Log.d("yomen", String.valueOf(mainUsers.size()));
                adapter = new UsersAdapter(getContext(), users);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                binding.chatRecyclerview.setHasFixedSize(true);
                binding.chatRecyclerview.setLayoutManager(manager);
                binding.chatRecyclerview.setAdapter(adapter);
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