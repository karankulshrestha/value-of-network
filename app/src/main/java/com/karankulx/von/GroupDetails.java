package com.karankulx.von;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Adapter.groupUserAdapter;
import com.karankulx.von.Models.Users;
import com.karankulx.von.databinding.ActivityGroupDetailsBinding;

import java.util.ArrayList;

public class GroupDetails extends AppCompatActivity {

    ActivityGroupDetailsBinding binding;
    public ArrayList<Users> usersList;
    String groupName, profile, groupSummary, uid, groupCreator;
    public boolean isPrivate;
    groupUserAdapter adapter;
    private RecyclerView recyclerView;
    public Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupName = getIntent().getStringExtra("groupName");
        profile = getIntent().getStringExtra("profileImage");
        groupSummary = getIntent().getStringExtra("groupDetails");
        groupCreator = getIntent().getStringExtra("groupCreator");
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        usersList = (ArrayList<Users>) args.getSerializable("groupMembers");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        isPrivate = getIntent().getBooleanExtra("Private", false);


        for (Users users1 : usersList) {
            if (groupCreator.equals(users1.getUid())) {
                Users user = users1;
                usersList.remove(users1);
                usersList.add(0, user);
            };
        };



        context = getApplicationContext();

        String tMembers = ((String) ("Total members: " + (usersList.size())));

        Glide.with(this).load(profile).diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.thumnail);
        binding.groupDetails.setText(groupSummary);
        binding.numberUsers.setText(tMembers);
        recyclerView = findViewById(R.id.groupMembers);

        binding.thumnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GroupDetails.this, GridImageViewer.class);
                intent1.putExtra("productImage", profile);
                startActivity(intent1);
            }
        });

        // initializing our adapter class.
        adapter = new groupUserAdapter(GroupDetails.this, usersList, isPrivate, groupCreator);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        recyclerView.setLayoutManager(manager);

        // setting adapter to
        // our recycler view.
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        ArrayList<Users> filteredlist = new ArrayList<Users>();

        // running a for loop to compare elements.
        for (Users item : usersList) {
            // checking if the entered string matched with any item of our recycler view.
            if ((item.getName() + " ~ " + item.getPhoneNumber()).toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                if (!item.getUid().equals(groupCreator)) {
                    findViewById(R.id.creator).setVisibility(View.INVISIBLE);
                    findViewById(R.id.admin).setVisibility(View.INVISIBLE);
                    filteredlist.add(item);
                } else {
                    filteredlist.add(item);
                };
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
}