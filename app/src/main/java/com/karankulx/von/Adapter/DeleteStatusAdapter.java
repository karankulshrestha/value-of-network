package com.karankulx.von.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.Models.Status;
import com.karankulx.von.R;
import com.karankulx.von.databinding.StatusRowBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DeleteStatusAdapter extends RecyclerView.Adapter<DeleteStatusAdapter.DeleteStatusViewHolder> {
    Context context;
    ArrayList<Status> statuses;
    FirebaseDatabase database;

    public DeleteStatusAdapter(Context context, ArrayList<Status> statuses) {
        this.context = context;
        this.statuses = statuses;
    };

    @NonNull
    public DeleteStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.status_row, parent, false);
        return new DeleteStatusAdapter.DeleteStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteStatusAdapter.DeleteStatusViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Status us = statuses.get(position);
        long time = us.getTimeStamp();
        database = FirebaseDatabase.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY - hh:mm a");
        Log.d("rajbhai", String.valueOf(us.getTimeStamp()));
        holder.binding.chaterName.setText(dateFormat.format(new Date(time)));
        Glide.with(context).load(us.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);

        holder.binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Status us = statuses.get(position);
                database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                        .child("statuses").child(us.getKey()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class DeleteStatusViewHolder extends RecyclerView.ViewHolder {

        StatusRowBinding binding;

        public DeleteStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StatusRowBinding.bind(itemView);
        }
    };
}
