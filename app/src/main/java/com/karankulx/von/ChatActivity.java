package com.karankulx.von;


import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.karankulx.von.Adapter.MessagesAdapter;
import com.karankulx.von.Fragments.ChatsFragment;
import com.karankulx.von.Models.Message;
import com.karankulx.von.databinding.ActivityChatBinding;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class ChatActivity extends AppCompatActivity{
    Toolbar toolbar;
    TextView title;
    ImageView profile;
    ImageView aVideos;
    ImageView aImages;
    ImageView attach;
    View mLayout;

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;
    String senderUid, receiverUid;

    FirebaseDatabase database;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages);
        binding.chatRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerview.setAdapter(adapter);


        attach = findViewById(R.id.attachment);
        aImages = findViewById(R.id.photos);
        aVideos = findViewById(R.id.videos);
        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();

                        binding.chatRecyclerview.smoothScrollToPosition(binding.chatRecyclerview.getAdapter().getItemCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBox = binding.messageText.getText().toString();
                binding.messageText.setText("");
                Date date = new Date();
                Message message = new Message(messageBox, senderUid, date.getTime());

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom)
                        .updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom)
                        .updateChildren(lastMsgObj);

                database.getReference().child("recentChats").child(senderUid).child(receiverUid);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push()
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("recentChats").child(receiverUid).child(senderUid);
                                            }
                                        });

                            }
                        });
            }
        });


        View v = findViewById(R.id.popupIcons);
        v.animate().translationY(v.getHeight());

        mLayout = findViewById(R.id.mainLayout);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.purple_500));


        String name = getIntent().getStringExtra("username");
        String profileUri = getIntent().getStringExtra("profileUri");
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.username);
        profile = findViewById(R.id.profile_image);
        title.setText(name);
        Glide.with(this).load(profileUri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profile);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        attach.setOnClickListener(new View.OnClickListener() {
            int cond = 0;
            @Override
            public void onClick(View view) {
                if (cond == 0) {
                    v.setVisibility(View.VISIBLE);
                    cond = 1;
                } else {
                    v.setVisibility(View.GONE);
                    cond = 0;
                }

            }
        });

        aVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        VideoPicker();
                    }
                }
            }
        });


        aImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    imagePicker();
                }

            }
        });

    }


    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }



    private void imagePicker() {
        Intent intent = new Intent(ChatActivity.this, FilePickerActivity.class);
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

    private void VideoPicker() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Intent intent1 = new Intent(ChatActivity.this, FilePickerActivity.class);
                intent1.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowVideos(true)
                        .enableVideoCapture(true)
                        .setMaxSelection(1)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityIntent1.launch(intent1);
            } else { //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }


        } else {
            Intent intent = new Intent(ChatActivity.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(false)
                    .setShowVideos(true)
                    .enableVideoCapture(true)
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .build());
            startActivityIntent1.launch(intent);
        }

    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getData() != null) {
                        ArrayList<MediaFile> mediaFiles = result.getData().getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                        for (int i = 0; i < mediaFiles.size(); i++) {
                            ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
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
                                byte[] data = baos.toByteArray();
                                Log.d("malika", data.toString());
                                StorageReference reference = storage.getReference().child("chats").child("photos").child(calender.getTimeInMillis() + "");
                                reference.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String filePath = uri.toString();
                                                    String messageBox = binding.messageText.getText().toString();
                                                    binding.messageText.setText("");
                                                    Date date = new Date();
                                                    Message message = new Message(messageBox, senderUid, date.getTime());
                                                    message.setImageUrl(filePath);
                                                    message.setMessage("§£€®¾");
                                                    database.getReference().child("chats")
                                                            .child(senderRoom)
                                                            .child("messages")
                                                            .push()
                                                            .setValue(message)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    database.getReference().child("chats")
                                                                            .child(receiverRoom)
                                                                            .child("messages")
                                                                            .push()
                                                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                }
                                                                            });
                                                                }
                                                            });
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> startActivityIntent1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getData() != null) {
                        ArrayList<MediaFile> mediaFiles = result.getData().getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                        Uri uri = mediaFiles.get(0).getUri();
                        String rPath = getPathFromUri(ChatActivity.this, uri);
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(ChatActivity.this, uri);
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeInMillisec = Long.parseLong(time);
                        if (timeInMillisec > 70000) {
                            Log.d("burger", String.valueOf(timeInMillisec));
                            Toast.makeText(ChatActivity.this, "Max 1 minute video allowed", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("burger", String.valueOf(timeInMillisec));
                            Log.d("malkova", rPath);
                            LoadFfmpegLibrary(rPath, uri);
                        };
                    }
                }
            });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatSheet:
                Intent intent = new Intent(ChatActivity.this, productsPage.class);
                intent.putExtra("userId", receiverUid);
                startActivity(intent);
                break;

            case R.id.clear_chat:
                database.getReference().child("chats").child(senderRoom).removeValue();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }


    String commandArray[];
    String outputPath;
    private void LoadFfmpegLibrary(String originalPath, Uri uri){
        ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("sending video...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        File file = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        String path = file.getPath();
        Calendar calendar = Calendar.getInstance();
        outputPath = path + "/" + calendar.getTimeInMillis() + ".mp4";
        commandArray = new String[]{"-y", "-i", originalPath, "-s", "720x480", "-r", "25", "-vcodec", "libvpx-vp9", "-preset", "ultrafast", "-crf", "38", "-b:v", "300k", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputPath};
        long executionId = FFmpeg.executeAsync(commandArray, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    StorageReference reference = storage.getReference().child("chats").child("videos").child(calendar.getTimeInMillis() + "");
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("video/" + getExt(uri))
                            .build();
                    reference
                            .putFile(Uri.fromFile(new File(outputPath)), metadata)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String videoPath = uri.toString();
                                                    String messageBox = binding.messageText.getText().toString();
                                                    binding.messageText.setText("");
                                                    Date date = new Date();
                                                    Message message = new Message(messageBox, senderUid, date.getTime());
                                                    message.setMessage("©°¶•ë™æ");
                                                    message.setVideoUrl(videoPath);
                                                    Log.d("happu", videoPath);
                                                    database.getReference().child("chats")
                                                                    .child(senderRoom)
                                                                    .child("messages")
                                                                    .push()
                                                                    .setValue(message)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                        database.getReference().child("chats")
                                                                                        .child(receiverRoom)
                                                                                        .child("messages")
                                                                                        .push()
                                                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                        }
                                                                                });
                                                                                }
                                                                            });
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}



