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
import com.karankulx.von.databinding.ContactRowBinding;
import com.karankulx.von.databinding.RowConversationBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    public ArrayList<Users> contactDetails = new ArrayList<>();
    private Context context;
    MainViewModel mainViewModel;
    public ArrayList<Users> multiselect_list = new ArrayList<>();

    public ContactAdapter(ArrayList<Users> contactDetails, ArrayList<Users> multiselect_list, Context context) {
        this.contactDetails = contactDetails;
        this.multiselect_list = multiselect_list;
        this.context = context;
    }

    public void filterList(ArrayList<Users> filterlist) {
        contactDetails = filterlist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ContactViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Users modal = contactDetails.get(position);
        holder.binding.chaterName.setText(modal.getName());
        holder.binding.phoneNumber.setText(modal.getPhoneNumber());
        Glide.with(context).load(modal.getProfilePic()).placeholder(R.drawable.userprofile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);

        holder.binding.mainBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("username", holder.binding.chaterName.getText().toString());
                intent.putExtra("profileUri", modal.getProfilePic());
                intent.putExtra("phoneNumber", modal.getPhoneNumber());
                intent.putExtra("uid", modal.getUid());
                context.startActivity(intent);
            }
        });

        if(multiselect_list.contains(contactDetails.get(position)))
            holder.binding.selectTick.setVisibility(View.VISIBLE);
        else
            holder.binding.selectTick.setVisibility(View.INVISIBLE);

    }


    @Override
    public int getItemCount() {
        // returning the size of array list.
        return contactDetails.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        ContactRowBinding binding;

        public ContactViewHolder(View itemView) {
            super(itemView);
            binding = ContactRowBinding.bind(itemView);

        }
    }

}
