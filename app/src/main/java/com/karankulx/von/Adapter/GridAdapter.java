package com.karankulx.von.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karankulx.von.GridImageViewer;
import com.karankulx.von.GroupBuildingActivity;
import com.karankulx.von.Models.Product;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.productsPage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    Context context;
    ArrayList<Product> products;

    public GridAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product p = products.get(position);
        Glide.with(context).load(products.get(position).getPhotourl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        Long time = p.getTimeStamp();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm a");
        holder.textView.setText(dateFormat.format(new Date(time)));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GridImageViewer.class);
                intent.putExtra("productImage", p.getPhotourl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_Image);
            textView = itemView.findViewById(R.id.item_name);
        }
    }
}
