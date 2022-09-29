package com.karankulx.von.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.ChatActivity;
import com.karankulx.von.ContactSync;
import com.karankulx.von.GroupBuildingActivity;
import com.karankulx.von.Models.Contact;
import com.karankulx.von.Models.MainViewModel;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.RowConversationBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    private ArrayList<Users> contactDetails;
    private Context context;
    boolean isEnabled = false;
    boolean isSelectAll = false;
    MainViewModel mainViewModel;
    ArrayList<Users> selectedUsers = new ArrayList<>();

    public ContactAdapter(ArrayList<Users> contactDetails, Context context) {
        this.contactDetails = contactDetails;
        this.context = context;
    }

    public void filterList(ArrayList<Users> filterlist) {
        contactDetails = filterlist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conversation, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return new ContactViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Users modal = contactDetails.get(position);
        holder.binding.chaterName.setText(modal.getName());
        Glide.with(context).load(modal.getProfilePic()).placeholder(R.drawable.userprofile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);

        holder.binding.mainBody.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEnabled) {
                    //when action mode is not enabled
                    //initialize action mode
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            //initialize menu Inflator
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            //inflate menu
                            menuInflater.inflate(R.menu.context_menu, menu);

                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            // when action mode is enabled
                            // set isEnabled true
                            isEnabled= true;
                            //create method
                            ClickItem(holder);

                            //set observer on get text method
                            mainViewModel.getText().observe((LifecycleOwner) context,
                                    new Observer<String>() {
                                        @Override
                                        public void onChanged(String s) {
                                            //when text changes
                                            //set text on action mode title
                                            actionMode.setTitle(String.format("%s Selected",s));
                                        }
                                    });

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            // when click on action mode item
                            int id = menuItem.getItemId();
                            //Use switch condition
                            switch (id) {
                                case R.id.groupBtn:
                                    if (selectedUsers.size() > 0) {
                                        Intent intent = new Intent(context, GroupBuildingActivity.class);
                                        intent.putExtra("gUser list", selectedUsers);
                                        context.startActivity(intent);
                                    }
                                    break;
                                case R.id.selectAll:
                                    if (selectedUsers.size() == contactDetails.size()) {
                                        isSelectAll = false;
                                        //Clear select Array List
                                        selectedUsers.clear();
                                    } else {
                                        // when all items are not selected
                                        isSelectAll = true;
                                        selectedUsers.clear();
                                        selectedUsers.addAll(contactDetails);
                                    }
                                    mainViewModel.setText(String.valueOf(selectedUsers.size()));
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            //When action mode is destroyed
                            //set isEnabled false
                            isEnabled = false;
                            //set isSelectAll false
                            isSelectAll = false;
                            selectedUsers.clear();
                            notifyDataSetChanged();
                        }
                    };

                    ((AppCompatActivity) view.getContext()).startActionMode(callback);

                } else {
                    // if action mode is already enabled
                    ClickItem(holder);
                }
                return true;
            }
        });

        holder.binding.mainBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnabled) {
                    ClickItem(holder);
                } else {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("username", holder.binding.chaterName.getText().toString());
                    intent.putExtra("profileUri", modal.getProfilePic());
                    intent.putExtra("uid", modal.getUid());
                    context.startActivity(intent);
                }
            }
        });

        if (isSelectAll) {
            holder.binding.selectTick.setVisibility(View.VISIBLE);
        } else {
            holder.binding.selectTick.setVisibility(View.INVISIBLE);
        }
    }

    private void ClickItem(ContactViewHolder holder) {
        //get selected item value
        Users user = contactDetails.get(holder.getBindingAdapterPosition());
        //check condition
        if (holder.binding.selectTick.getVisibility() == View.INVISIBLE) {
            //when item not selected
            //visible checkbox
            holder.binding.selectTick.setVisibility(View.VISIBLE);
            selectedUsers.add(user);
        } else {
            holder.binding.selectTick.setVisibility(View.INVISIBLE);
            selectedUsers.remove(user);
        }
        mainViewModel.setText(String.valueOf(selectedUsers.size()));

    }


    @Override
    public int getItemCount() {
        // returning the size of array list.
        return contactDetails.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public ContactViewHolder(View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);

        }
    }

}
