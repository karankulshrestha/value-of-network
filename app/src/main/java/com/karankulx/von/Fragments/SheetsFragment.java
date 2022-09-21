package com.karankulx.von.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.karankulx.von.Adapter.sheetAdapter;
import com.karankulx.von.Models.Contact;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.FragmentSheetsBinding;

import java.util.ArrayList;
import java.util.HashSet;


public class SheetsFragment extends Fragment {
    public SheetsFragment() {};

    private static final String TAG = "details";
    public static final int REQUEST_READ_CONTACTS = 79;

    public Context context;

    public FirebaseDatabase database;
    public FirebaseAuth mAuth;
    public FragmentSheetsBinding binding;
    public ArrayList<Users> users;
    public ArrayList<Users> requiredContacts = new ArrayList<>();
    public ArrayList<Users> cloudContacts = new ArrayList<>();

    public sheetAdapter sAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSheetsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
        requiredContacts = new ArrayList<>();
        cloudContacts = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            filler();

            for (Users contact : cloudContacts) {
                Log.d("hvy", "onCreaterrView  Phone Number: name = " + contact.getName()
                        + " No = " + contact.getPhoneNumber());
            }

        } else {
            requestPermission();
        }


        return binding.getRoot();
    };

    private void filler() {
        ArrayList<Contact> lContacts = getContactList();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
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

                requiredContacts.clear();
                for (Contact lCon : lContacts) {
                    for (Users cCon : cloudContacts) {
                        if (lCon.getPhoneNumber().contains(cCon.getPhoneNumber())) {
                            requiredContacts.add(cCon);
                        }
                    }
                }
                Log.d("rajbaba", String.valueOf(requiredContacts.size()));
                sAdapter = new sheetAdapter(getActivity(), requiredContacts);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                binding.sheetRecyclerview.setHasFixedSize(true);
                binding.sheetRecyclerview.setLayoutManager(manager);
                binding.sheetRecyclerview.setAdapter(sAdapter);
                sAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
        ContentResolver cr = getActivity().getContentResolver();
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