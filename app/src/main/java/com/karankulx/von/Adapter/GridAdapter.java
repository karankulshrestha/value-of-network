package com.karankulx.von.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karankulx.von.Models.Product;
import com.karankulx.von.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<Product> products;

    LayoutInflater inflater;

    public GridAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        };
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, null);
        };

        ImageView imageView = view.findViewById(R.id.grid_Image);
        TextView textView = view.findViewById(R.id.item_name);

        Glide.with(context).load(products.get(i).getPhotourl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        long time = products.get(i).getTimeStamp();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm a");
        textView.setText(dateFormat.format(new Date(time)));

        return view;
    }
}
