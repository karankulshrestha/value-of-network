package com.karankulx.von;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karankulx.von.databinding.ActivityGridImageViewerBinding;

public class GridImageViewer extends AppCompatActivity {

    ActivityGridImageViewerBinding binding;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGridImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        image = getIntent().getStringExtra("productImage");

        Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.productImage);

        Log.d("lolchut", image);
    }
}