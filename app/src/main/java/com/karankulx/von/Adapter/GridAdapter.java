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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karankulx.von.ChatActivity;
import com.karankulx.von.GridImageViewer;
import com.karankulx.von.GroupBuildingActivity;
import com.karankulx.von.Models.MainViewModel;
import com.karankulx.von.Models.Product;
import com.karankulx.von.Models.Users;
import com.karankulx.von.R;
import com.karankulx.von.databinding.GridItemBinding;
import com.karankulx.von.productListener;
import com.karankulx.von.productsPage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    Context context;
    ArrayList<Product> products;
    boolean isEnabled = false;
    boolean isSelectAll = false;
    MainViewModel mainViewModel;
    ArrayList<Product> selectedProducts = new ArrayList<>();

    public GridAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindProduct(products.get(position));
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

        public void bindProduct(Product product) {
            Long time = product.getTimeStamp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm a");
            binding.itemName.setText(dateFormat.format(new Date(time)));

            Glide.with(context).load(product.getPhotourl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.gridImage);


            binding.gridImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isEnabled) {
                        //when action mode is not enabled
                        //initialize action mode
                        ActionMode.Callback callback = new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                //initialize menu Inflator
                                MenuInflater menuInflater = actionMode.getMenuInflater();
                                //inflate menu
                                menuInflater.inflate(R.menu.context_menu, menu);

                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                // when action mode is enabled
                                // set isEnabled true
                                isEnabled= true;
                                //create method
                                click(product);

                                //set observer on get text method
                                mainViewModel.getText().observe((LifecycleOwner) context,
                                        new Observer<String>() {
                                            @Override
                                            public void onChanged(String s) {
                                                //when text changes
                                                //set text on action mode title
                                                actionMode.setTitle(String.format("%s Selected",s));
                                            }
                                        });

                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                // when click on action mode item
                                int id = menuItem.getItemId();
                                //Use switch condition
                                switch (id) {
                                    case R.id.groupBtn:
                                        if (selectedProducts.size() > 0) {
                                            Intent intent = new Intent(context, GroupBuildingActivity.class);
                                            intent.putExtra("gUser list", selectedProducts);
                                            context.startActivity(intent);
                                        }
                                        break;
                                    case R.id.selectAll:
                                        if (selectedProducts.size() == products.size()) {
                                            isSelectAll = false;
                                            //Clear select Array List
                                            selectedProducts.clear();
                                        } else {
                                            // when all items are not selected
                                            isSelectAll = true;
                                            selectedProducts.clear();
                                            selectedProducts.addAll(products);
                                        }
                                        mainViewModel.setText(String.valueOf(selectedProducts.size()));
                                        notifyDataSetChanged();
                                        break;
                                }
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode actionMode) {
                                //When action mode is destroyed
                                //set isEnabled false
                                isEnabled = false;
                                //set isSelectAll false
                                isSelectAll = false;
                                selectedProducts.clear();
                                notifyDataSetChanged();
                            }
                        };

                        ((AppCompatActivity) view.getContext()).startActionMode(callback);

                    } else {
                        // if action mode is already enabled
                        click(product);
                    }

                    return true;
                }
            });

            binding.gridImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEnabled) {
                        click(product);
                    } else {
//                        Intent intent = new Intent(context, ChatActivity.class);
//                        intent.putExtra("username", holder.binding.chaterName.getText().toString());
//                        intent.putExtra("profileUri", modal.getProfilePic());
//                        intent.putExtra("uid", modal.getUid());
//                        context.startActivity(intent);
                    }
                }
            });

            if (isSelectAll) {
                binding.selectTick1.setVisibility(View.VISIBLE);
            } else {
                binding.selectTick1.setVisibility(View.INVISIBLE);
            }
        };

        public void click(Product product) {
            if (product.getSelected()) {
                binding.selectTick1.setVisibility(View.INVISIBLE);
                selectedProducts.remove(product);
                product.setSelected(false);
            } else {
                binding.selectTick1.setVisibility(View.VISIBLE);
                selectedProducts.add(product);
                product.setSelected(true);
            };
            mainViewModel.setText(String.valueOf(selectedProducts.size()));
        };

    }
}
