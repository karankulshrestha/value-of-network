package com.karankulx.von.Adapter;

import android.content.Context;
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

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    Context context;
    ArrayList<Groups> groups;

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
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Groups group = groups.get(position);
        RequestOptions myOptions = new RequestOptions()
                .override(100, 100);
        Glide.with(context).asBitmap()
                .apply(myOptions)
                .load(group.gProfile).
                diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);
        holder.binding.chaterName.setText(group.gName);
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