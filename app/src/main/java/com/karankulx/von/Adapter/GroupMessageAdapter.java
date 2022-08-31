package com.karankulx.von.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.FullScreenImage;
import com.karankulx.von.Models.Message;
import com.karankulx.von.Models.Users;
import com.karankulx.von.Models.groupMessage;
import com.karankulx.von.R;
import com.karankulx.von.databinding.GroupItemReceiveBinding;
import com.karankulx.von.databinding.GroupItemSendBinding;

import java.util.ArrayList;

public class GroupMessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<groupMessage> messages;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public GroupMessageAdapter(Context context, ArrayList<groupMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_item_send, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.group_item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        groupMessage message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        groupMessage message = messages.get(position);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("users")
                .child(message.getSenderId());

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    ((SentViewHolder) holder).binding.getInfo.setText(users.getName() + " ~ " + users.getPhoneNumber());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (message.getMessage().equals("©°¶•ë™æ")) {
                viewHolder.binding.message.setVisibility(View.GONE);
                viewHolder.binding.idExoPlayerVIew.setVisibility(View.VISIBLE);
                ExoPlayer player = new ExoPlayer.Builder(context).build();
                StyledPlayerView styledPlayerView = viewHolder.binding.idExoPlayerVIew;
                MediaItem mediaItem = MediaItem.fromUri(message.getVideoUrl());
                styledPlayerView.setPlayer(player);
                styledPlayerView.setShowNextButton(false);
                styledPlayerView.setShowPreviousButton(false);
                player.setMediaItem(mediaItem);
                player.prepare();
            } else {
                viewHolder.binding.idExoPlayerVIew.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(message.getMessage());
            }

            if (message.getMessage().equals("§£€®¾")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);

                Glide.with(context).load(message.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .placeholder(R.drawable.loadingimage)
                        .into(viewHolder.binding.image);

                viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent fp = new Intent(context, FullScreenImage.class);
                        fp.putExtra("imageUrl", message.getImageUrl());
                        context.startActivity(fp);
                    }
                });


            } else {
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(message.getMessage());
            }

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    ((ReceiverViewHolder) holder).binding.getInfo.setText(users.getName() + " ~ " + users.getPhoneNumber());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (message.getMessage().equals("©°¶•ë™æ")) {
                viewHolder.binding.message.setVisibility(View.GONE);
                viewHolder.binding.idExoPlayerVIew.setVisibility(View.VISIBLE);
                ExoPlayer player = new ExoPlayer.Builder(context).build();
                StyledPlayerView styledPlayerView = viewHolder.binding.idExoPlayerVIew;
                MediaItem mediaItem = MediaItem.fromUri(message.getVideoUrl());
                styledPlayerView.setPlayer(player);
                styledPlayerView.setShowNextButton(false);
                styledPlayerView.setShowPreviousButton(false);
                player.setMediaItem(mediaItem);
                player.prepare();
            } else {
                viewHolder.binding.idExoPlayerVIew.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(message.getMessage());
            }

            if (message.getMessage().equals("§£€®¾")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .placeholder(R.drawable.loadingimage)
                        .into(viewHolder.binding.image);

                viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent fp = new Intent(context, FullScreenImage.class);
                        fp.putExtra("imageUrl", message.getImageUrl());
                        context.startActivity(fp);
                    }
                });

            } else {
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        GroupItemSendBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GroupItemSendBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        GroupItemReceiveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GroupItemReceiveBinding.bind(itemView);
        }
    }

}
