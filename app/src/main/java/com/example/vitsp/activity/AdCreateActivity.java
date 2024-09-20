package com.example.vitsp.activity;

import android.Manifest;
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
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vitsp.adapter.AdapterImagePicked;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.databinding.ActivityAdCreateBinding;
import com.example.vitsp.models.ModelImagePicked;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class AdCreateActivity extends AppCompatActivity {
    private ActivityAdCreateBinding binding;
    private static final String TAG = "AD_CREATE_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private Uri imageUri = null;
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagePicked adapterImagePicked;

    private boolean isEditMode = false;
    private String adIdForEditing = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this, R.layout.row_category_act, Utils.categories);
        binding.categoryAct.setAdapter(adapterCategories);

        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this, R.layout.row_condition_act, Utils.conditions);
        binding.conditionAct.setAdapter(adapterConditions);

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            adIdForEditing = intent.getStringExtra("adId");
            loadAdDetails();
            binding.toolbarTitleTv.setText("Update Ad");
            binding.postAdBtn.setText("Update Ad");
        } else {
            binding.toolbarTitleTv.setText("Create Ad");
            binding.postAdBtn.setText("Post Ad");
        }

        imagePickedArrayList = new ArrayList<>();
        loadImages();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(v -> onBackPressed());

        binding.toolbarAddImageBtn.setOnClickListener(v -> showImagePickOptions());

        binding.postAdBtn.setOnClickListener(v -> validateData());
    }

    private void loadImages() {
        adapterImagePicked = new AdapterImagePicked(imagePickedArrayList, this, adIdForEditing);
        binding.imagesRv.setAdapter(adapterImagePicked);
    }

    private void showImagePickOptions() {
        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarAddImageBtn);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA});
                } else {
                    requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImageGallery();
                } else {
                    requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
            return false;
        });
    }

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean areAllGranted = true;
                for (Boolean isGranted : result.values()) {
                    areAllGranted = areAllGranted && isGranted;
                }
                if (areAllGranted) {
                    pickImageCamera();
                } else {
                    Utils.toast(AdCreateActivity.this, "Camera or Storage or both permissions denied...");
                }
            }
    );

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    pickImageGallery();
                } else {
                    Utils.toast(AdCreateActivity.this, "Storage permission denied...");
                }
            }
    );

    private void pickImageCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMPORARY_IMAGE_DESCRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String timestamp = "" + Utils.getTimestamp();
                    ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                    imagePickedArrayList.add(modelImagePicked);
                    loadImages();
                } else {
                    Utils.toast(AdCreateActivity.this, "Cancelled...");
                }
            }
    );

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageUri = result.getData().getData();
                    String timestamp = "" + System.currentTimeMillis();
                    ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                    imagePickedArrayList.add(modelImagePicked);
                    loadImages();
                } else {
                    Utils.toast(AdCreateActivity.this, "Cancelled...");
                }
            }
    );

    private String brand = "";
    private String category = "";
    private String condition = "";
    private String price = "";
    private String description = "";
    private String title = "";

    private void validateData() {
        brand = binding.brandEt.getText().toString().trim();
        category = binding.categoryAct.getText().toString().trim();
        condition = binding.conditionAct.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        title = binding.titleEt.getText().toString().trim();

        if (brand.isEmpty()) {
            binding.brandEt.setError("Enter Brand");
            binding.brandEt.requestFocus();
        } else if (category.isEmpty()) {
            binding.categoryAct.setError("Choose Category");
            binding.categoryAct.requestFocus();
        } else if (condition.isEmpty()) {
            binding.conditionAct.setError("Choose Condition");
            binding.conditionAct.requestFocus();
        } else if (title.isEmpty()) {
            binding.titleEt.setError("Enter Title");
            binding.titleEt.requestFocus();
        } else if (description.isEmpty()) {
            binding.descriptionEt.setError("Enter Description");
            binding.descriptionEt.requestFocus();
        } else if (imagePickedArrayList.isEmpty()) {
            Utils.toast(this, "Please select at least one image");
        } else {
            if (isEditMode) {
                updateAd();
            } else {
                postAd();
            }
        }
    }

    private void updateAd() {
        progressDialog.setMessage("Updating Ad...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("brand", brand);
        hashMap.put("category", category);
        hashMap.put("condition", condition);
        hashMap.put("price", price);
        hashMap.put("title", title);
        hashMap.put("description", description);

        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        refAds.child(adIdForEditing)
                .updateChildren(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    uploadImagesStorage(adIdForEditing);
                })
                .addOnFailureListener(e -> {
                    Utils.toast(AdCreateActivity.this, "Failure to update Ad due to " + e.getMessage());
                    progressDialog.dismiss();
                });
    }

    private void postAd() {
        progressDialog.setMessage("Publishing Ad...");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", keyId);
        hashMap.put("brand", brand);
        hashMap.put("category", category);
        hashMap.put("condition", condition);
        hashMap.put("price", price);
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", firebaseAuth.getUid());
        hashMap.put("isBlocked", false);
        hashMap.put("viewsCount", 0);

        refAds.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> uploadImagesStorage(keyId))
                .addOnFailureListener(e -> {
                    Utils.toast(AdCreateActivity.this, "Failure to publish Ad due to " + e.getMessage());
                    progressDialog.dismiss();
                });
    }

    private void uploadImagesStorage(String adId) {
        final ArrayList<ModelImagePicked> images = new ArrayList<>(imagePickedArrayList);
        for (int i = 0; i < images.size(); i++) {
            final int index = i;
            String imageId = images.get(i).getId();
            Uri imageUri = images.get(i).getImageUri();

            String filePathAndName = "Ads/" + adId + "/" + imageId;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", imageId);
                                hashMap.put("imageUrl", uri.toString());

                                refAds.child(adId).child("Images").child(imageId)
                                        .setValue(hashMap);
                            }))
                    .addOnFailureListener(e -> Log.d(TAG, "Failure to upload image due to " + e.getMessage()))
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading Image " + (index + 1) + " of " + images.size() + ": " + (int) progress + "%");
                    });
        }
        progressDialog.dismiss();
        Utils.toast(AdCreateActivity.this, "Ad Published Successfully");
        onBackPressed();
    }

    private void loadAdDetails() {
        // Implementation for loading existing ad details when in edit mode
    }
}
