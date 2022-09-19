package com.karankulx.von.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.FirebaseDatabase;
import com.karankulx.von.HomeActivity;
import com.karankulx.von.Models.Status;
import com.karankulx.von.Models.userStatus;
import com.karankulx.von.R;
import com.karankulx.von.databinding.ItemStatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class topStatusAdapter extends RecyclerView.Adapter<topStatusAdapter.topStatusViewHolder> {

    Context context;
    ArrayList<userStatus> userStatuses;
    FirebaseDatabase database;

    public topStatusAdapter(Context context, ArrayList<userStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    };

    @NonNull
    @Override
    public topStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new topStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull topStatusViewHolder holder, int position) {

        userStatus us = userStatuses.get(position);
        database = FirebaseDatabase.getInstance();

        Status lastStatus = us.getStatuses().get(us.getStatuses().size() - 1);

        Glide.with(context).load(lastStatus.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.binding.imagew);

        holder.binding.circularStatusView.setPortionsCount(us.getStatuses().size());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : us.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                };

                new StoryView.Builder(((HomeActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(us.getName()) // Default is Hidden
                        .setTitleLogoUrl(us.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class topStatusViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;

        public topStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    };

}
