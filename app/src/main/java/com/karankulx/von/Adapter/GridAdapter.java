package com.karankulx.von.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karankulx.von.GroupBuildingActivity;
import com.karankulx.von.Models.MainViewModel;
import com.karankulx.von.Models.Product;
import com.karankulx.von.R;
import com.karankulx.von.databinding.GridItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    Context context;
    boolean isEnabled = false;
    boolean isSelectAll = false;
    MainViewModel mainViewModel;
    public ArrayList<Product> products = new ArrayList<>();
    public ArrayList<Product> selectedProducts = new ArrayList<>();

    public GridAdapter(Context context, ArrayList<Product> products, ArrayList<Product> selectedProducts) {
        this.context = context;
        this.products = products;
        this.selectedProducts = selectedProducts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = products.get(position);
        long time = product.getTimeStamp();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm a");
        holder.binding.itemName.setText(dateFormat.format(new Date(time)));
        Glide.with(context).load(product.getPhotourl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.gridImage);

        if(selectedProducts.contains(products.get(position)))
            holder.binding.selectTick1.setVisibility(View.VISIBLE);
        else
            holder.binding.selectTick1.setVisibility(View.INVISIBLE);

    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        GridItemBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GridItemBinding.bind(itemView);
        }
    }
}
