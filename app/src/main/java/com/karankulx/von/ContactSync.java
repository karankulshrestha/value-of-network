package com.karankulx.von;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Adapter.ContactAdapter;
import com.karankulx.von.Models.Contact;
import com.karankulx.von.Models.Product;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivityContactSyncBinding;
import com.karankulx.von.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashSet;

public class ContactSync extends AppCompatActivity {

    private static final String TAG = "details";
    ActivityContactSyncBinding binding;

    public static final int REQUEST_READ_CONTACTS = 79;

    FirebaseAuth mAuth;
    ActionMode mActionMode;
    Menu context_menu;

    private RecyclerView localContactDetails;
    private ContactAdapter adapter;
    ArrayList<Users> requiredContacts;
    boolean isMultiSelect = false;
    boolean isSelectAll = false;
    ArrayList<Users> cloudContacts;
    ArrayList<Users> multiselect_list = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sync);
        binding = ActivityContactSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        localContactDetails = findViewById(R.id.localContactDetails);

        cloudContacts = new ArrayList<>();
        requiredContacts = new ArrayList<>();

        filler();
    }



    private void filler() {
        ArrayList<Contact> lContacts = getContactList();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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
                                requiredContacts.add(cCon);
                            }
                        }
                    }


                    adapter = new ContactAdapter(requiredContacts, multiselect_list, ContactSync.this);
                    LinearLayoutManager manager = new LinearLayoutManager(ContactSync.this);
                    localContactDetails.setHasFixedSize(true);
                    localContactDetails.setLayoutManager(manager);
                    localContactDetails.setAdapter(adapter);
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.localContactDetails.addOnItemTouchListener(new RecyclerItemClickListener(this, localContactDetails, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                };
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Users>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    };
                };
                multi_select(position);
            }
        }));


    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(requiredContacts.get(position)))
                multiselect_list.remove(requiredContacts.get(position));
            else
                multiselect_list.add(requiredContacts.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.groupBtn:
                    if (multiselect_list.size() > 0) {
                        Intent intent = new Intent(ContactSync.this, GroupBuildingActivity.class);
                        intent.putExtra("gUser list", multiselect_list);
                        Log.d("jamesbhai", String.valueOf(multiselect_list.size()));
                        startActivity(intent);
                    }
                    break;
                case R.id.selectAll:
                    if (multiselect_list.size() == requiredContacts.size()) {
                        isSelectAll = false;
                        //Clear select Array List
                        multiselect_list.clear();
                    } else {
                        // when all items are not selected
                        isSelectAll = true;
                        multiselect_list.clear();
                        multiselect_list.addAll(requiredContacts);
                    }
                    mActionMode.setTitle("" + multiselect_list.size());
                    refreshAdapter();
                    break;
                default:
                    return false;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Users>();
            refreshAdapter();
        }
    };

    public void refreshAdapter()
    {
        adapter.multiselect_list=multiselect_list;
        adapter.contactDetails=requiredContacts;
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Users> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Users item : requiredContacts) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }


    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private ArrayList<Contact> getContactList() {
        ContentResolver cr = getContentResolver();
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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}