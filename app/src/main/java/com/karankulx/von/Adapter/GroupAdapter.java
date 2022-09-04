package com.karankulx.von.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.karankulx.von.Models.Groups;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.GroupConversationBinding;
import com.karankulx.von.databinding.RowConversationBinding;
import com.karankulx.von.groupChatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    Context context;
    public Groups group;
    ArrayList<Groups> groups;
    ArrayList<Users> users;

    public GroupAdapter(Context context, ArrayList<Groups> groups) {
        this.context = context;
        this.groups = groups;
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new GroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, @SuppressLint("RecyclerView") int position) {
        group = groups.get(position);
        users = new ArrayList<Users>();
        RequestOptions myOptions = new RequestOptions()
                .override(100, 100);
        Glide.with(context).asBitmap()
                .apply(myOptions)
                .load(group.gProfile).
                diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);
        holder.binding.chaterName.setText(group.gName);

        holder.binding.mainBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Groups group1 = groups.get(position);
                Intent intent = new Intent(context, groupChatActivity.class);
                intent.putExtra("groupName", holder.binding.chaterName.getText().toString());
                users.addAll(group1.getUser());
                Bundle args = new Bundle();
                args.putSerializable("userDetails",(Serializable) users);
                intent.putExtra("BUNDLE",args);
                intent.putExtra("profileImage", group1.getgProfile());
                intent.putExtra("groupDetails", group1.getgSummary());
                intent.putExtra("groupId", group1.getGroupId());
                intent.putExtra("groupCreator", group1.getGroupCreator());
                intent.putExtra("isPrivate", group1.isPrivate());
                context.startActivity(intent);
                users.clear();
                Log.d("rajo", String.valueOf(users.size()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        GroupConversationBinding binding;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GroupConversationBinding.bind(itemView);
        }
    }
}