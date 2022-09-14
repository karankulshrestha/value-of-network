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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Adapter.UsersAdapter;
import com.karankulx.von.ContactSync;
import com.karankulx.von.Models.Contact;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.HashSet;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    FirebaseDatabase database;

    public static final int REQUEST_READ_CONTACTS = 79;

    FirebaseAuth mAuth;
    Context context;

    public ArrayList<Users> requiredContacts;
    public ArrayList<Users> cloudContacts;
    public UsersAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

        cloudContacts = new ArrayList<>();
        requiredContacts = new ArrayList<>();


        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                    android.Manifest.permission.READ_CONTACTS
            }, 10);
            filler();

            for (Users contact : cloudContacts) {
                Log.d("hvy", "onCreaterrView  Phone Number: name = " + contact.getName()
                        + " No = " + contact.getPhoneNumber());
            }
        }else {
            requestPermission();
        }


        binding.normalFAB.setOnClickListener((View v) -> {
            startActivity(new Intent(getActivity(), ContactSync.class));
        });

        return binding.getRoot();
    }


    public void filler() {
        ArrayList<Contact> lContacts = getContactList();
        ArrayList<Users> mainUsers = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Users user = data.getValue(Users.class);
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (!currentUser.getUid().equals(data.getKey())) {
                        cloudContacts.add(user);
                        Log.d("lolly", data.getKey());
                    }
                }

                for (Contact lCon : lContacts) {
                    for (Users cCon : cloudContacts) {
                        if (lCon.getPhoneNumber().contains(cCon.getPhoneNumber())) {
                            cCon.setName(lCon.getName());
                            mainUsers.add(cCon);
                        }
                    }
                }
                Log.d("jamesbond", String.valueOf(mainUsers.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Handler handler = new Handler();
        int delay = 10; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){

                if(!mainUsers.isEmpty())//checking if the data is loaded or not
                {

                    bada(mainUsers);
                }
                else
                    handler.postDelayed(this, delay);
            }
        }, delay);

        Log.d("hudi", String.valueOf(requiredContacts.size()));

    }

    public void bada(ArrayList<Users> mainUsers) {
        ArrayList<Users> mainUsers1 = new ArrayList<>();
        adapter = new UsersAdapter(getContext(), mainUsers1);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        binding.chatRecyclerview.setHasFixedSize(true);
        binding.chatRecyclerview.setLayoutManager(manager);
        binding.chatRecyclerview.setAdapter(adapter);

        for (Users users : mainUsers) {
            String senderRoom = (FirebaseAuth.getInstance().getCurrentUser().getUid() + users.getUid());
            database.getReference().child("chats").child(senderRoom).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mainUsers1.add(users);
                    };
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    };
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        };
    };



    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(context, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    filler();
                } else {
                    Toast.makeText(context, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private ArrayList<Contact> getContactList() {
        ContentResolver cr = requireActivity().getContentResolver();
        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    if (!mobileNoSet.contains(number)) {
                        mobileNoSet.add(number);
                        contacts.add(new Contact(name, number));
                    }

                }
            } finally {
                cursor.close();
            }
        }
        return contacts;
    }

}