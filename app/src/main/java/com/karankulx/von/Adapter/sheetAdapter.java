package com.karankulx.von.Adapter;
import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.FirebaseDatabase;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.ItemSheetBinding;
import com.karankulx.von.databinding.ItemStatusBinding;

import java.util.ArrayList;


public class sheetAdapter extends RecyclerView.Adapter<sheetAdapter.sheetViewHolder> {

    Context context;
    ArrayList<Users> users;
    FirebaseDatabase database;

    public sheetAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
    };

    @NonNull
    @Override
    public sheetAdapter.sheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sheet, parent, false);
        return new sheetAdapter.sheetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull sheetAdapter.sheetViewHolder holder, int position) {
        Users user = users.get(position);
        holder.binding.chaterName.setText(user.getName() + " ~ " + user.getPhoneNumber());
        Glide.with(context).load(user.getProfilePic())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class sheetViewHolder extends RecyclerView.ViewHolder {

        ItemSheetBinding binding;

        public sheetViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSheetBinding.bind(itemView);
        }
    };

}
