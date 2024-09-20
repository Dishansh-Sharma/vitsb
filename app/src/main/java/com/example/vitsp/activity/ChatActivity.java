//
//
//package com.example.vitsp.activity;
//
//import android.Manifest;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import com.bumptech.glide.Glide;
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.adapter.AdapterChat;
//import com.example.vitsp.databinding.ActivityChatBinding;
//import com.example.vitsp.models.ModelChat;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class ChatActivity extends AppCompatActivity {
//
//    private ActivityChatBinding binding;
//    private static final String TAG = "Chat_TAG";
//    private ProgressDialog progressDialog;
//    private FirebaseAuth firebaseAuth;
//
//    private String receiptUid = "";
//    private String myUid = "";
//    private String chatPath = "";
//    private Uri imageUri = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityChatBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Initialize Firebase Auth
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        // Initialize ProgressDialog
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//
//        // Retrieve intent extras
//        receiptUid = getIntent().getStringExtra("receiptUid");
//        myUid = firebaseAuth.getUid();
//
//        // Check for null values
//        if (receiptUid == null || myUid == null) {
//            Log.e(TAG, "Error: receiptUid or myUid is null");
//            Utils.toast(ChatActivity.this, "An error occurred: User data is missing.");
//            finish(); // Close the activity if either value is null
//            return;
//        }
//
//        chatPath = Utils.chatPath(receiptUid, myUid);
//        Log.d(TAG, "onCreate: receiptUid: " + receiptUid);
//        Log.d(TAG, "onCreate: myUid: " + myUid);
//        Log.d(TAG, "onCreate: chatPath: " + chatPath);
//
//        loadReciptsDetails();
//        loadMessages();
//
//        // Set toolbar back button click listener
//        binding.toolbarBackBtn.setOnClickListener(v -> finish());
//
//        // Set send button click listener
//        binding.sendBtn.setOnClickListener(view -> validateData());
//    }
//
//    private void validateData() {
//        Log.d(TAG, "validateData: ");
//        String message = binding.messageET.getText().toString().trim();
//        long timestamp = Utils.getTimestamp();
//
//        if (message.isEmpty()) {
//            Utils.toast(ChatActivity.this, "Message cannot be empty");
//        } else {
//            sendMessage(Utils.MESSAGE_TYPE_TEXT, message, timestamp);
//        }
//    }
//
//    private void sendMessage(String messageType, String message, long timestamp) {
//        Log.d(TAG, "sendMessage: messageType : " + messageType);
//        Log.d(TAG, "sendMessage: message : " + message);
//        Log.d(TAG, "sendMessage: timestamp : " + timestamp);
//
//        progressDialog.setMessage("Sending Message...");
//        progressDialog.show();
//
//        DatabaseReference refChat = FirebaseDatabase.getInstance().getReference("Chats");
//
//        String keyId = refChat.push().getKey();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("messageId", keyId);
//        hashMap.put("fromUid", myUid);
//        hashMap.put("toUid", receiptUid);
//        hashMap.put("message", message);
//        hashMap.put("timestamp", timestamp);
//        hashMap.put("messageType", messageType);
//
//        refChat.child(chatPath).child(keyId).setValue(hashMap)
//                .addOnSuccessListener(unused -> {
//                    binding.messageET.setText("");
//                    progressDialog.dismiss();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "onFailure: ", e);
//                    progressDialog.dismiss();
//                    Utils.toast(ChatActivity.this, "Failed to send message due to " + e.getMessage());
//                });
//    }
//
//    private void loadReciptsDetails() {
//        Log.d(TAG, "loadReciptsDetails: ");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(receiptUid)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            String name = "" + snapshot.child("name").getValue();
//                            String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
//
//                            Log.d(TAG, "onDataChange: name: " + name);
//                            Log.d(TAG, "onDataChange: profileImageUrl: " + profileImageUrl);
//
//                            binding.toolbarTitleTv.setText(name);
//                            try {
//                                Glide.with(ChatActivity.this)
//                                        .load(profileImageUrl)
//                                        .placeholder(R.drawable.ic_person_gray)
//                                        .error(R.drawable.ic_image_broken_gray)
//                                        .into(binding.toolbarProfileiv);
//                            } catch (Exception e) {
//                                Log.e(TAG, "onDataChange: ", e);
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "onDataChange: ", e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e(TAG, "onCancelled: ", error.toException());
//                    }
//                });
//    }
//
//    private void loadMessages() {
//        Log.d(TAG, "loadMessages: ");
//        ArrayList<ModelChat> chatArrayList = new ArrayList<>();
//        AdapterChat adapterChat = new AdapterChat(this, chatArrayList);
//
//        binding.chatRv.setLayoutManager(new LinearLayoutManager(this));
//        binding.chatRv.setAdapter(adapterChat);
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
//        ref.child(chatPath).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                chatArrayList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    try {
//                        ModelChat modelChat = ds.getValue(ModelChat.class);
//                        chatArrayList.add(modelChat);
//                    } catch (Exception e) {
//                        Log.e(TAG, "onDataChange: ", e);
//                    }
//                }
//                adapterChat.notifyDataSetChanged(); // Notify adapter of data change
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "onCancelled: ", error.toException());
//            }
//        });
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//






















//
//package com.example.vitsp.activity;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.PopupMenu;
//import android.Manifest;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import com.bumptech.glide.Glide;
//
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.adapter.AdapterChat;
//import com.example.vitsp.databinding.ActivityChatBinding;
//import com.example.vitsp.models.ModelChat;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ChatActivity extends AppCompatActivity {
//
//    private ActivityChatBinding binding;
//    private static final String TAG = "Chat_TAG";
//    private ProgressDialog progressDialog;
//    private FirebaseAuth firebaseAuth;
//
//    private String receiptUid = "";
//    private String myUid = "";
//    private String chatPath = "";
//
//    private Uri imageUri = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityChatBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//
//        firebaseAuth =FirebaseAuth.getInstance();
//
//
//
//
//        progressDialog =new ProgressDialog(this);
//        progressDialog.setTitle("Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//
//
//        receiptUid =getIntent().getStringExtra("receiptUid");
//        myUid = firebaseAuth.getUid();
//
//        chatPath = Utils.chatPath(receiptUid, myUid);
//
//
//        Log.d(TAG, "onCreate: receiptUid: " + receiptUid);
//        Log.d(TAG, "onCreate: myUid: " + myUid);
//        Log.d(TAG, "onCreate: chatPath: " + chatPath);
//
//
//
//
//        loadReciptsDetails();
//        loadMessages();
//
//
//
//
//
//        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        binding.attachFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imagePickDialog();
//            }
//        });
//
//
//
//        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateData();
//            }
//        });
//
//    }
//
//    private void imagePickDialog() {
//
//        PopupMenu popupMenu =new PopupMenu(this, binding.attachFab);
//
//        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
//        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");
//        popupMenu.show();
//
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                int itemId = item.getItemId();
//
//                if (itemId == 1){
//                    Log.d(TAG, "onMenuItemClick: Camera click, Check if camera permissions are granted or not");
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA});
//                    }
//                    else {
//                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE});
//                    }
//                }
//
//                else if (itemId == 2){
//                    Log.d(TAG, "onMenuItemClick: Gallery click");
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//                        pickImageGallery();
//                    }
//                    else{
//                        requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    }
//                }
//
//
//
//                return true;
//
//            }
//        });
//    }
//
//
//    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
//            new ActivityResultContracts.RequestMultiplePermissions(),
//            new ActivityResultCallback<Map<String, Boolean>>() {
//                @Override
//                public void onActivityResult(Map<String, Boolean> result) {
//                    Log.d(TAG, "onActivityResult: "+result);
//                    boolean areAllGranted = true;
//                    for(Boolean isGranted : result.values()){
//                        areAllGranted = areAllGranted && isGranted;
//                    }
//
//                    if(areAllGranted){
//                        Log.d(TAG, "onActivityResult: ");
//                        pickImageCamera();
//                    }else{
//                        Log.d(TAG, "onActivityResult: Camera or Storage or both permissions not granted....");
//                        Utils.toast(ChatActivity.this, "Camera or Storage or both permissions not granted....");
////                        finish();
//
//                    }
//
//                }
//            }
//    );
//
//
//
//    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            new ActivityResultCallback<Boolean>() {
//                @Override
//                public void onActivityResult(Boolean isGranted) {
//                    Log.d(TAG, "onActivityResult: isGranted" + isGranted);
//
//                    if(isGranted){
//                        pickImageGallery();
//                    }else{
//                        Utils.toast(ChatActivity.this, "Permission not granted....");
//                    }
//
//                }
//            }
//
//    );
//
//    private void pickImageCamera(){
//        Log.d(TAG, "pickImageCamera: ");
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE,"CHAT_IMAGE_TEMP");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"CHAT_IMAGE_TEMP_DESCRIPTION");
//
//        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//        cameraActivityResultLauncher.launch(intent);
//
//
//    }
//    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//
//                    if(result.getResultCode() == Activity.RESULT_OK){
//
//                        Log.d(TAG, "onActivityResult: imageUri: "+imageUri);
//                        uploadTOFirebaseStorage();
//
//                    }
//                    else{
//                        Utils.toast(ChatActivity.this, "Camcelled....");
//                    }
//
//                }
//            }
//    );
//
//
//
//
//    private void pickImageGallery(){
//        Log.d(TAG,"pickImageGallery : ");
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        galleryActivityResultLauncher.launch(intent);
//
//
//    }
//
//
//
//    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
//
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//
//                    if(result.getResultCode() == Activity.RESULT_OK){
//
//                        Intent data = result.getData();
//                        imageUri = data.getData();
//                        Log.d(TAG, "onActivityResult: imageUri: "+imageUri);
//                        uploadTOFirebaseStorage();
//
//                    }
//                    else{
//                        Utils.toast(ChatActivity.this, "Camcelled....");
//                    }
//
//                }
//            }
//
//
//    );
//
//
//    private  void uploadTOFirebaseStorage(){
//        Log.d(TAG, "uploadTOFirebaseStorage: ");
//        progressDialog.setMessage("Uploading Image...");
//        progressDialog.show();
//
//
//        long timestamp = Utils.getTimestamp();
//        String filePathAndName = "ChatImages/"+timestamp;
//
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
//
//        storageReference.putFile(imageUri)
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//
//                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//                        progressDialog.setMessage("Uploading Image.Progress: "+(int)progress+"%");
//
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//
//                        while (!uriTask.isSuccessful()){
//                            String imageUrl = uriTask.getResult().toString();
//
//                            if(uriTask.isSuccessful()){
//                                sendMessage(Utils.MESSAGE_TYPE_IMAGE, imageUrl, timestamp);
//                            }
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Log.e(TAG, "onFailure: ", e);
//                        progressDialog.dismiss();
//                        Utils.toast(ChatActivity.this, "Failed to upload  due to "+e.getMessage());
//
//                    }
//                });
//
//    }
//    private void validateData() {
//        Log.d(TAG, "validateData: ");
//        String message = binding.messageET.getText().toString().trim();
//        long timestamp = Utils.getTimestamp();
//
//        if (message.isEmpty()) {
//            Utils.toast(this, "Enter message to send...");
//        } else {
//            sendMessage(Utils.MESSAGE_TYPE_TEXT, message, timestamp);
//        }
//    }
//
//
//
//
//
//
//
//
//    private  void sendMessage(String messageType, String message, long timestamp){
//        Log.d(TAG,"sendMessage: messageType: "+messageType);
//        Log.d(TAG,"sendMessage: message: "+message);
//        Log.d(TAG,"sendMessage: timestamp: "+timestamp);
//        progressDialog.setMessage("Sending Message...");
//        progressDialog.show();
//
//        DatabaseReference refChat = FirebaseDatabase.getInstance().getReference("Chats");
//
//        String keyId = ""+refChat.push().getKey();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("messageId", keyId);
//        hashMap.put("fromUid", myUid);
//        hashMap.put("toUid", receiptUid);
//        hashMap.put("message", message);
//        hashMap.put("timestamp", timestamp);
//        hashMap.put("messageType", messageType);
//
//        refChat.child(chatPath).
//                child(keyId).
//                setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                        binding.messageET.setText("");
//                        progressDialog.dismiss();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Log.e(TAG, "onFailure: ", e);
//                        progressDialog.dismiss();
//                        Utils.toast(ChatActivity.this, "Failed to send message due to " + e.getMessage());
//                    }
//                });
//    }
//
//
//
//
//    private void loadReciptsDetails() {
//        Log.d(TAG, "loadReciptsDetails: ");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(receiptUid)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            String name = "" + snapshot.child("name").getValue();
//                            String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
//
//                            Log.d(TAG, "onDataChange: name: " + name);
//                            Log.d(TAG, "onDataChange: profileImageUrl: " + profileImageUrl);
//
//                            binding.toolbarTitleTv.setText(name);
//                            try {
//                                Glide.with(ChatActivity.this)
//                                        .load(profileImageUrl)
//                                        .placeholder(R.drawable.ic_person_gray)
//                                        .error(R.drawable.ic_image_broken_gray)
//                                        .into(binding.toolbarProfileiv);
//                            } catch (Exception e) {
//                                Log.e(TAG, "onDataChange: ", e);
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "onDataChange: ", e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e(TAG, "onCancelled: ", error.toException());
//                    }
//                });
//
//
//    }
//
//
//
//    private void loadMessages() {
//        Log.d(TAG, "loadMessages: ");
//        ArrayList<ModelChat> chatArrayList = new ArrayList<>();
//        AdapterChat adapterChat = new AdapterChat(ChatActivity.this, chatArrayList);
//
//        binding.chatRv.setLayoutManager(new LinearLayoutManager(this));
//        binding.chatRv.setAdapter(adapterChat);
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
//        ref.child(chatPath)
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                chatArrayList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    try {
//                        ModelChat modelChat = ds.getValue(ModelChat.class);
//                        chatArrayList.add(modelChat);
//                    } catch (Exception e) {
//                        Log.e(TAG, "onDataChange: ", e);
//                    }
//                }
//                adapterChat.notifyDataSetChanged(); // Notify adapter of data change
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "onCancelled: ", error.toException());
//            }
//        });
//    }
//
//
//
//
//
//}






















































































package com.example.vitsp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.adapter.AdapterChat;
import com.example.vitsp.databinding.ActivityChatBinding;
import com.example.vitsp.models.ModelChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private static final String TAG = "Chat_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private String receiptUid = "";
    private String myUid = "";
    private String chatPath = "";

    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        receiptUid = getIntent().getStringExtra("receiptUid");
        myUid = firebaseAuth.getUid();
        chatPath = Utils.chatPath(receiptUid, myUid);

        Log.d(TAG, "onCreate: receiptUid: " + receiptUid);
        Log.d(TAG, "onCreate: myUid: " + myUid);
        Log.d(TAG, "onCreate: chatPath: " + chatPath);

        loadReciptsDetails();
        loadMessages();

        binding.toolbarBackBtn.setOnClickListener(v -> finish());

        binding.attachFab.setOnClickListener(v -> imagePickDialog());

        binding.sendBtn.setOnClickListener(v -> validateData());
    }

    private void imagePickDialog() {
        PopupMenu popupMenu = new PopupMenu(this, binding.attachFab);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == 1) {
                Log.d(TAG, "onMenuItemClick: Camera click");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA});
                } else {
                    requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            } else if (itemId == 2) {
                Log.d(TAG, "onMenuItemClick: Gallery click");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImageGallery();
                } else {
                    requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
            return true;
        });
    }

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Log.d(TAG, "onActivityResult: " + result);
                boolean areAllGranted = result.values().stream().allMatch(Boolean::booleanValue);
                if (areAllGranted) {
                    Log.d(TAG, "onActivityResult: Permissions granted");
                    pickImageCamera();
                } else {
                    Log.d(TAG, "onActivityResult: Permissions not granted");
                    Utils.toast(ChatActivity.this, "Camera or Storage or both permissions not granted.");
                }
            }
    );

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                Log.d(TAG, "onActivityResult: isGranted" + isGranted);
                if (isGranted) {
                    pickImageGallery();
                } else {
                    Utils.toast(ChatActivity.this, "Permission not granted.");
                }
            }
    );

    private void pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "CHAT_IMAGE_TEMP");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "CHAT_IMAGE_TEMP_DESCRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                    uploadTOFirebaseStorage();
                } else {
                    Utils.toast(ChatActivity.this, "Cancelled.");
                }
            }
    );

    private void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                        uploadTOFirebaseStorage();
                    } else {
                        Utils.toast(ChatActivity.this, "No image selected.");
                    }
                } else {
                    Utils.toast(ChatActivity.this, "Cancelled.");
                }
            }
    );

    private void uploadTOFirebaseStorage() {
        Log.d(TAG, "uploadTOFirebaseStorage: ");
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        String filePathAndName = "ChatImages/" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

        storageReference.putFile(imageUri)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading Image. Progress: " + (int) progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        sendMessage(Utils.MESSAGE_TYPE_IMAGE, imageUrl, Utils.getTimestamp());
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(ChatActivity.this, "Failed to get image URL.");
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(ChatActivity.this, "Failed to upload image due to " + e.getMessage());
                });
    }

    private void validateData() {
        Log.d(TAG, "validateData: ");
        String message = binding.messageET.getText().toString().trim();
        long timestamp = Utils.getTimestamp();

        if (message.isEmpty()) {
            Utils.toast(this, "Enter message to send...");
        } else {
            sendMessage(Utils.MESSAGE_TYPE_TEXT, message, timestamp);
        }
    }

    private void sendMessage(String messageType, String message, long timestamp) {
        Log.d(TAG, "sendMessage: messageType: " + messageType);
        Log.d(TAG, "sendMessage: message: " + message);
        Log.d(TAG, "sendMessage: timestamp: " + timestamp);
        progressDialog.setMessage("Sending Message...");
        progressDialog.show();

        DatabaseReference refChat = FirebaseDatabase.getInstance().getReference("Chats");

        String keyId = refChat.push().getKey();
        if (keyId != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("messageId", keyId);
            hashMap.put("fromUid", myUid);
            hashMap.put("toUid", receiptUid);
            hashMap.put("message", message);
            hashMap.put("timestamp", timestamp);
            hashMap.put("messageType", messageType);

            refChat.child(chatPath)
                    .child(keyId)
                    .setValue(hashMap)
                    .addOnSuccessListener(unused -> {
                        binding.messageET.setText("");
                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(ChatActivity.this, "Failed to send message due to " + e.getMessage());
                    });
        } else {
            progressDialog.dismiss();
            Utils.toast(ChatActivity.this, "Failed to generate message ID.");
        }
    }

    private void loadReciptsDetails() {
        Log.d(TAG, "loadReciptsDetails: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(receiptUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String name = "" + snapshot.child("name").getValue();
                            String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();

                            Log.d(TAG, "onDataChange: name: " + name);
                            Log.d(TAG, "onDataChange: profileImageUrl: " + profileImageUrl);

                            binding.toolbarTitleTv.setText(name);
                            try {
                                Glide.with(ChatActivity.this)
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.ic_person_gray)
                                        .error(R.drawable.ic_image_broken_gray)
                                        .into(binding.toolbarProfileiv);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: ", error.toException());
                    }
                });
    }

    private void loadMessages() {
        Log.d(TAG, "loadMessages: ");
        ArrayList<ModelChat> chatArrayList = new ArrayList<>();
        AdapterChat adapterChat = new AdapterChat(ChatActivity.this, chatArrayList);

        binding.chatRv.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRv.setAdapter(adapterChat);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.child(chatPath)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                ModelChat modelChat = ds.getValue(ModelChat.class);
                                if (modelChat != null) {
                                    chatArrayList.add(modelChat);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }
                        adapterChat.notifyDataSetChanged(); // Notify adapter of data change
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: ", error.toException());
                    }
                });
    }
}
