package com.karankulx.von;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.karankulx.von.Adapter.GridAdapter;
import com.karankulx.von.Models.Message;
import com.karankulx.von.Models.Product;
import com.karankulx.von.databinding.ActivityProductsPageBinding;
import com.karankulx.von.utils.RecyclerItemClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class productsPage extends AppCompatActivity {

    ActionMode mActionMode;
    Menu context_menu;

    ActivityProductsPageBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public String userId;
    boolean isMultiSelect = false;
    ArrayList<Product> products;
    ArrayList<Product> multiselect_list;
    public GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getStringExtra("userId");
        products = new ArrayList<>();
        multiselect_list = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        recyclerView = findViewById(R.id.productRecyclerview);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Log.d("lolraj", userId);

        database.getReference().child("sheets").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    products.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Product p = snapshot1.getValue(Product.class);
                        products.add(p);
                    };
                    Collections.reverse(products);
                    gridAdapter = new GridAdapter(productsPage.this, products, multiselect_list);
                    binding.productRecyclerview.setAdapter(gridAdapter);
                    gridAdapter.notifyDataSetChanged();
                };

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.productRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                } else {
                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
                };
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Product>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    };
                };
                multi_select(position);
            }
        }));


    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.groupBtn:
                    Toast.makeText(getApplicationContext(), "yo yo", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Product>();
            refreshAdapter();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.actionAdd:
                imagePicker();
                break;

            case R.id.actionDelete:
                showDialog(this, "Delete Items", "This will delete all items");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(products.get(position)))
                multiselect_list.remove(products.get(position));
            else
                multiselect_list.add(products.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }

    }

    public void refreshAdapter()
    {
        gridAdapter.selectedProducts=multiselect_list;
        gridAdapter.products=products;
        gridAdapter.notifyDataSetChanged();
    }



    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItems();
            }
        });
        builder.show();
    }

    private void imagePicker() {
        Intent intent = new Intent(productsPage.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowVideos(false)
                .enableImageCapture(true)
                .setMaxSelection(4)
                .setSkipZeroSizeFiles(true)
                .build());

        startActivityIntent.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getData() != null) {
                        ArrayList<MediaFile> mediaFiles = result.getData().getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                        for (int i = 0; i < mediaFiles.size(); i++) {
                            ProgressDialog progressDialog = new ProgressDialog(productsPage.this);
                            progressDialog.setMessage("wait sending...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            Calendar calender = Calendar.getInstance();
                            Uri uri = mediaFiles.get(i).getUri();
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                byte[] data1 = baos.toByteArray();
                                Log.d("malika", data1.toString());
                                StorageReference reference = firebaseStorage.getReference().child("sheets").child(userId).child(calender.getTimeInMillis() + "");
                                reference.putBytes(data1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String filePath = uri.toString();
                                                    Calendar calender = Calendar.getInstance();
                                                    Product p = new Product(filePath, calender.getTimeInMillis(), false);
                                                    database.getReference().child("sheets").child(userId).push().setValue(p);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });


    private void deleteItems() {
        database.getReference().child("sheets").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ProgressDialog progressDialog = new ProgressDialog(productsPage.this);
                        progressDialog.setMessage("deleting...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Product p = snapshot1.getValue(Product.class);
                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(p.getPhotourl());
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(productsPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    };
                    database.getReference().child("sheets").child(userId).removeValue();
                };
                refreshAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}