package com.karankulx.von.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karankulx.von.ChatActivity;
import com.karankulx.von.Fragments.ChatsFragment;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.RowConversationBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    Context context;
    ArrayList<String> users;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public UsersAdapter(Context context, ArrayList<String> users) {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new UsersViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        String user = users.get(position);
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String senderRoom = senderId + user;

        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                if (snapshot.exists()) {
                                    Long time = snapshot.child("lastMsgTime").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    holder.binding.lastMessage.setText(lastMsg);
                                    holder.binding.lastMsgTime.setText(dateFormat.format(new Date(time)));

                                } else {
                                    holder.binding.lastMessage.setText("Tap to chat");
                                };
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        FirebaseDatabase.getInstance().getReference().child("users").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    holder.binding.chaterName.setText(users.getName());
                    holder.binding.phoneNumber.setText(" ~ " + users.getPhoneNumber());
                    Glide.with(context).load(users.getProfilePic())
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.profileImage);

                    holder.binding.mainBody.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            holder.binding.lastMessage.setTypeface(null, Typeface.NORMAL);
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("username", holder.binding.chaterName.getText().toString());
                            intent.putExtra("profileUri", users.getProfilePic());
                            intent.putExtra("uid", users.getUid());
                            intent.putExtra("phoneNumber", users.getPhoneNumber());
                            intent.putExtra("token", users.getToken());
                            context.startActivity(intent);
                        }
                    });
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
