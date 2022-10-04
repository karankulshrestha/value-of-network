package com.karankulx.von.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.GroupDetails;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.GroupConversationBinding;
import com.karankulx.von.databinding.GroupRowMembersBinding;
import com.karankulx.von.databinding.RowConversationBinding;

import java.util.ArrayList;

public class groupUserAdapter extends RecyclerView.Adapter<groupUserAdapter.groupViewHolder> {

    private ArrayList<Users> groupUsers;
    Context context;
    boolean isPrivate;
    String groupCreator;

    public groupUserAdapter(Context context, ArrayList<Users> groupUsers, boolean isPrivate, String groupCreator) {
        this.groupUsers = groupUsers;
        this.context = context;
        this.isPrivate = isPrivate;
        this.groupCreator = groupCreator;
    };

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Users> filterlist) {
        groupUsers = filterlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public groupUserAdapter.groupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row_members, parent, false);
        return new groupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull groupUserAdapter.groupViewHolder holder, int position) {
        Users user = groupUsers.get(position);
        holder.binding.chaterName.setText(user.getName());
        holder.binding.phoneNumber.setText("~ " + user.getPhoneNumber());
        if (groupCreator.equals(user.getUid())) {
            holder.binding.creator.setVisibility(View.VISIBLE);
            if (isPrivate) {
                holder.binding.admin.setVisibility(View.VISIBLE);
            };
        };

        Glide.with(context).load(user.getProfilePic())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);
    }

    @Override
    public int getItemCount() {
        return groupUsers.size();
    }

    public class groupViewHolder extends RecyclerView.ViewHolder {

        GroupRowMembersBinding binding;

        public groupViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GroupRowMembersBinding.bind(itemView);
        }
    }

}
