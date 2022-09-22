package com.karankulx.von;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class productsPage extends AppCompatActivity {

    ActivityProductsPageBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    public String userId;
    ArrayList<Product> products;
    ArrayList<Product> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getStringExtra("userId");
        products = new ArrayList<>();
        temp = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        Log.d("lolraj", userId);

        database.getReference().child("sheets").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    products.clear();
                    temp.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Product p = snapshot1.getValue(Product.class);
                        temp.add(p);
                    };
                };

                if (snapshot.exists()) {
                    if (temp.size() <= 30) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Product p = snapshot1.getValue(Product.class);
                            products.add(p);
                        };
                    } else {
                        Toast.makeText(productsPage.this, "max 30 items allowed, please delete items", Toast.LENGTH_SHORT).show();
                    };

                    Collections.reverse(products);
                    GridAdapter gridAdapter = new GridAdapter(productsPage.this, products);
                    binding.gridview.setAdapter(gridAdapter);
                };

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(productsPage.this, GridImageViewer.class);
                intent.putExtra("productImage", products.get(i).getPhotourl());
                startActivity(intent);
            }
        });


    }

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
                                                    Product p = new Product(filePath, calender.getTimeInMillis());
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
                };
                database.getReference().child("sheets").child(userId).removeValue();
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