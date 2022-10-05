package com.karankulx.von.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.karankulx.von.GridImageViewer;
import com.karankulx.von.Models.Message;
import com.karankulx.von.R;
import com.karankulx.von.databinding.ItemReceiveBinding;
import com.karankulx.von.databinding.ItemSendBinding;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            if (message.getMessage().equals("©°¶•ë™æ")) {
                viewHolder.binding.idExoPlayerVIew.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
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
                        Intent fp = new Intent(context, GridImageViewer.class);
                        fp.putExtra("productImage", message.getImageUrl());
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
            if (message.getMessage().equals("©°¶•ë™æ")) {
                viewHolder.binding.idExoPlayerVIew.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
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
                        Intent fp = new Intent(context, GridImageViewer.class);
                        fp.putExtra("productImage", message.getImageUrl());
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

        ItemSendBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
