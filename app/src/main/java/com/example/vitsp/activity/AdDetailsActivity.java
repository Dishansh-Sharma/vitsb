//package com.example.vitsp.activity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.PopupMenu;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.adapter.AdapterImageSlider;
//import com.example.vitsp.databinding.ActivityAdDetailsBinding;
//import com.example.vitsp.models.ModelAd;
//import com.example.vitsp.models.ModelImageSlider;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.dialog.MaterialAlertDialogBuilder;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class AdDetailsActivity extends AppCompatActivity {
//
//    private ActivityAdDetailsBinding binding;
//    private static final String TAG = "AD_DETAILS_TAG";
//    private ProgressDialog progressDialog;
//    private FirebaseAuth firebaseAuth;
//    private String adId = "";
//
//    private String sellerUid = null;
//    private String sellerPhone = "";
//
//    private boolean favorite = false;
//
//    private ArrayList<ModelImageSlider> imageSliderArrayList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        binding.toolbarBackBtn.setVisibility(View.GONE);
//        binding.toolbarDeleteBtn.setVisibility(View.GONE);
//        binding.callBtn.setVisibility(View.GONE);
//        binding.chatBtn.setVisibility(View.GONE);
//
//        adId = getIntent().getStringExtra("adId");
//
//        Log.d(TAG, "onCreate: adId: " + adId);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        if (firebaseAuth.getCurrentUser() != null) {
//            checkIsFavorite();
//        }
//
//        loadAdDetails();
//        loadAdImages();
//
//        binding.toolbarBackBtn.setOnClickListener(v -> onBackPressed());
//
//        binding.toolbarDeleteBtn.setOnClickListener(v -> {
//            MaterialAlertDialogBuilder materialAlertBuilder = new MaterialAlertDialogBuilder(AdDetailsActivity.this);
//
//            materialAlertBuilder.setTitle("Delete Ad")
//                    .setMessage("Are you sure you want to delete this Ad?")
//                    .setPositiveButton("DELETE", (dialog, which) -> deleteAd())
//                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
//                    .show();
//        });
//
//        binding.toolbarEditBtn.setOnClickListener(v -> {
//            Utils.toast(AdDetailsActivity.this, "Edit Clicked...");
//            editOptions();
//        });
//
//        binding.toolbarFavBtn.setOnClickListener(v -> {
//            if (favorite) {
//                Utils.removeFavourite(AdDetailsActivity.this, adId);
//            } else {
//                Utils.addToFavourite(AdDetailsActivity.this, adId);
//            }
//        });
//
//        binding.sellerProfileCv.setOnClickListener(v -> {
//            Intent intent = new Intent(AdDetailsActivity.this, AdSellerProfileActivity.class);
//            intent.putExtra("sellerUid", sellerUid);
//            startActivity(intent);
//        });
//
//        binding.callBtn.setOnClickListener(v -> Utils.callIntent(AdDetailsActivity.this, sellerPhone));
//
//        binding.chatBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(AdDetailsActivity.this, ChatActivity.class);
//            intent.putExtra("receiptUid", sellerUid);
//            startActivity(intent);
//        });
//    }
//
//    private void editOptions() {
//        Log.d(TAG, "editOptions: ");
//
//        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarEditBtn);
//        popupMenu.getMenu().add(0, 0, 0, "Edit");
//        popupMenu.getMenu().add(0, 1, 0, "Mark as Sold");
//        popupMenu.show();
//
//        popupMenu.setOnMenuItemClickListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == 0) {
//                Intent intent = new Intent(AdDetailsActivity.this, AdCreateActivity.class);
//                intent.putExtra("isEditMode", true);
//                intent.putExtra("adId", adId);
//                startActivity(intent);
//            } else if (itemId == 1) {
//                showMarkAsSoldDialog();
//            }
//            return false;
//        });
//    }
//
//    private void showMarkAsSoldDialog() {
//        MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(this);
//        alertBuilder.setTitle("Mark as Sold")
//                .setMessage("Are you sure you want to mark this ad as sold?")
//                .setPositiveButton("Sold", (dialog, which) -> {
//                    Log.d(TAG, "onClick: Sold Clicked...");
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("status", "" + Utils.AD_STATUS_SOLD);
//
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//                    ref.child(adId)
//                            .updateChildren(hashMap)
//                            .addOnSuccessListener(unused -> {
//                                Log.d(TAG, "onSuccess: Mark as Sold");
//                                Utils.toast(AdDetailsActivity.this, "Marked as Sold");
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.e(TAG, "onFailure: ", e);
//                                Utils.toast(AdDetailsActivity.this, "Failed to Mark as Sold due to " + e.getMessage());
//                            });
//                })
//                .setNegativeButton("CANCEL", (dialog, which) -> {
//                    Log.d(TAG, "onClick: CANCEL Clicked...");
//                    dialog.dismiss();
//                })
//                .show();
//    }
//
//    private void loadAdDetails() {
//        Log.d(TAG, "loadAdDetails: ");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//        ref.child(adId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            ModelAd modelAd = snapshot.getValue(ModelAd.class);
//
//                            sellerUid = modelAd.getUid();
//                            String title = modelAd.getTitle();
//                            String description = modelAd.getDescription();
//                            String price = modelAd.getPrice();
//                            String condition = modelAd.getCondition();
//                            String category = modelAd.getCategory();
//                            Long timestamp = modelAd.getTimestamp();
//                            String formattedDate = Utils.formatTimestampDate(timestamp);
//
//                            if (sellerUid.equals(firebaseAuth.getUid())) {
//                                binding.toolbarEditBtn.setVisibility(View.VISIBLE);
//                                binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
//                                binding.callBtn.setVisibility(View.VISIBLE);
//                                binding.chatBtn.setVisibility(View.VISIBLE);
//
//                                binding.sellerProfileLabelTv.setVisibility(View.GONE);
//                                binding.sellerProfileCv.setVisibility(View.GONE);
//                            } else {
//                                binding.toolbarEditBtn.setVisibility(View.GONE);
//                                binding.toolbarDeleteBtn.setVisibility(View.GONE);
//                                binding.callBtn.setVisibility(View.VISIBLE);
//                                binding.chatBtn.setVisibility(View.VISIBLE);
//
//                                binding.sellerProfileLabelTv.setVisibility(View.GONE);
//                                binding.sellerProfileCv.setVisibility(View.GONE);
//                            }
//
//                            binding.titleTv.setText(title);
//                            binding.descriptionTv.setText(description);
//                            binding.priceTv.setText(price);
//                            binding.dateTv.setText(formattedDate);
//                            binding.conditionTv.setText(condition);
//                            binding.categoryTv.setText(category);
//
//                            loadSellerDetails();
//                        } catch (Exception e) {
//                            Log.e(TAG, "onDataChange: ", e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle onCancelled
//                    }
//                });
//    }
//
//    private void loadSellerDetails() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(sellerUid)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String name = "" + snapshot.child("name").getValue();
//                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
//                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
//                        long timestamp = snapshot.child("timestamp").getValue(Long.class);
//                        String phoneCode = "" + snapshot.child("phoneCode").getValue();
//
//                        String formattedDate = Utils.formatTimestampDate(timestamp);
//                        sellerPhone = phoneCode + " " + phoneNumber;
//
//                        binding.sellerNameTv.setText(name);
//                        binding.memberSinceTv.setText(formattedDate);
//
//                        try {
//                            Glide.with(AdDetailsActivity.this)
//                                    .load(profileImageUrl)
//                                    .placeholder(R.drawable.ic_person_white)
//                                    .into(binding.sellerProfileIv);
//                        } catch (Exception e) {
//                            Log.e(TAG, "onDataChange: ", e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle onCancelled
//                    }
//                });
//    }
//
//    private void checkIsFavorite() {
//        Log.d(TAG, "checkIsFavorite: ");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid()).child("Favorites").child(adId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        favorite = snapshot.exists();
//                        Log.d(TAG, "onDataChange: " + favorite);
//                        if (favorite) {
//                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_yes);
//                        } else {
//                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_no);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle onCancelled
//                    }
//                });
//    }
//
//    private void loadAdImages() {
//        Log.d(TAG, "loadAdImages: ");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//        ref.child(adId).child("Images")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (imageSliderArrayList == null) {
//                            imageSliderArrayList = new ArrayList<>();
//                        }
//                        imageSliderArrayList.clear();
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);
//                            imageSliderArrayList.add(modelImageSlider);
//                        }
//
//                        AdapterImageSlider adapterImageSlider = new AdapterImageSlider(AdDetailsActivity.this, imageSliderArrayList);
//                        binding.imageSliderVp.setAdapter(adapterImageSlider);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle onCancelled
//                    }
//                });
//    }
//
//    private void deleteAd() {
//        Log.d(TAG, "deleteAd: ");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//        ref.child(adId)
//                .removeValue()
//                .addOnSuccessListener(unused -> {
//                    Log.d(TAG, "onSuccess: Ad Deleted...");
//                    Utils.toast(AdDetailsActivity.this, "Ad Deleted...");
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "onFailure: ", e);
//                    Utils.toast(AdDetailsActivity.this, "Failed to Delete due to " + e.getMessage());
//                });
//    }
//}

















































































package com.example.vitsp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.adapter.AdapterImageSlider;
import com.example.vitsp.databinding.ActivityAdDetailsBinding;
import com.example.vitsp.models.ModelAd;
import com.example.vitsp.models.ModelImageSlider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdDetailsActivity extends AppCompatActivity {

    private ActivityAdDetailsBinding binding;
    private static final String TAG = "AD_DETAILS_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String adId = "";

    private String sellerUid = null;
    private String sellerPhone = "";

    private boolean favorite = false;

    private ArrayList<ModelImageSlider> imageSliderArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.toolbarBackBtn.setVisibility(View.GONE);
        binding.toolbarBackBtn.setVisibility(View.VISIBLE);
        binding.toolbarDeleteBtn.setVisibility(View.GONE);
        binding.callBtn.setVisibility(View.GONE);
        binding.chatBtn.setVisibility(View.GONE);

        adId = getIntent().getStringExtra("adId");

        Log.d(TAG, "onCreate: adId: " + adId);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite();
        }

        loadAdDetails();
        loadAdImages();

        binding.toolbarBackBtn.setOnClickListener(v -> onBackPressed());

        binding.toolbarDeleteBtn.setOnClickListener(v -> {
            MaterialAlertDialogBuilder materialAlertBuilder = new MaterialAlertDialogBuilder(AdDetailsActivity.this);

            materialAlertBuilder.setTitle("Delete Ad")
                    .setMessage("Are you sure you want to delete this Ad?")
                    .setPositiveButton("DELETE", (dialog, which) -> deleteAd())
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        binding.toolbarEditBtn.setOnClickListener(v -> {
            Utils.toast(AdDetailsActivity.this, "Edit Clicked...");
            editOptions();
        });

        binding.toolbarFavBtn.setOnClickListener(v -> {
            if (favorite) {
                Utils.removeFavourite(AdDetailsActivity.this, adId);
            } else {
                Utils.addToFavourite(AdDetailsActivity.this, adId);
            }
        });

        binding.sellerProfileCv.setOnClickListener(v -> {
            Intent intent = new Intent(AdDetailsActivity.this, AdSellerProfileActivity.class);
            intent.putExtra("sellerUid", sellerUid);
            startActivity(intent);
        });

        binding.callBtn.setOnClickListener(v -> Utils.callIntent(AdDetailsActivity.this, sellerPhone));

        binding.chatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdDetailsActivity.this, ChatActivity.class);
            intent.putExtra("receiptUid", sellerUid);
            startActivity(intent);
        });
    }

    private void editOptions() {
        Log.d(TAG, "editOptions: ");

        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarEditBtn);
        popupMenu.getMenu().add(0, 0, 0, "Edit");
        popupMenu.getMenu().add(0, 1, 0, "Mark as Sold");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == 0) {
                Intent intent = new Intent(AdDetailsActivity.this, AdCreateActivity.class);
                intent.putExtra("isEditMode", true);
                intent.putExtra("adId", adId);
                startActivity(intent);
            } else if (itemId == 1) {
                showMarkAsSoldDialog();
            }
            return false;
        });
    }

    private void showMarkAsSoldDialog() {
        MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(this);
        alertBuilder.setTitle("Mark as Sold")
                .setMessage("Are you sure you want to mark this ad as sold?")
                .setPositiveButton("Sold", (dialog, which) -> {
                    Log.d(TAG, "onClick: Sold Clicked...");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", "" + Utils.AD_STATUS_SOLD);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                    ref.child(adId)
                            .updateChildren(hashMap)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "onSuccess: Mark as Sold");
                                Utils.toast(AdDetailsActivity.this, "Marked as Sold");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "onFailure: ", e);
                                Utils.toast(AdDetailsActivity.this, "Failed to Mark as Sold due to " + e.getMessage());
                            });
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    Log.d(TAG, "onClick: CANCEL Clicked...");
                    dialog.dismiss();
                })
                .show();
    }

    private void loadAdDetails() {
        Log.d(TAG, "loadAdDetails: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            ModelAd modelAd = snapshot.getValue(ModelAd.class);

                            if (modelAd == null) {
                                Log.e(TAG, "onDataChange: ModelAd is null");
                                return;
                            }

                            sellerUid = modelAd.getUid();
                            String title = modelAd.getTitle();
                            String description = modelAd.getDescription();
                            String price = modelAd.getPrice();
                            String condition = modelAd.getCondition();
                            String category = modelAd.getCategory();
                            Long timestamp = modelAd.getTimestamp();
                            String formattedDate = timestamp != null ? Utils.formatTimestampDate(timestamp) : "Date not available";

                            if (sellerUid.equals(firebaseAuth.getUid())) {
                                binding.toolbarEditBtn.setVisibility(View.VISIBLE);
                                binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
                                binding.callBtn.setVisibility(View.VISIBLE);
                                binding.chatBtn.setVisibility(View.VISIBLE);

                                binding.sellerProfileLabelTv.setVisibility(View.GONE);
                                binding.sellerProfileCv.setVisibility(View.GONE);
                            } else {
                                binding.toolbarEditBtn.setVisibility(View.GONE);
                                binding.toolbarDeleteBtn.setVisibility(View.GONE);
                                binding.callBtn.setVisibility(View.VISIBLE);
                                binding.chatBtn.setVisibility(View.VISIBLE);

                                binding.sellerProfileLabelTv.setVisibility(View.GONE);
                                binding.sellerProfileCv.setVisibility(View.GONE);
                            }

                            binding.titleTv.setText(title);
                            binding.descriptionTv.setText(description);
                            binding.priceTv.setText(price);
                            binding.dateTv.setText(formattedDate);
                            binding.conditionTv.setText(condition);
                            binding.categoryTv.setText(category);

                            loadSellerDetails();
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }

    private void loadSellerDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sellerUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = "" + snapshot.child("name").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
                        Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                        String phoneCode = "" + snapshot.child("phoneCode").getValue();

                        String formattedDate = timestamp != null ? Utils.formatTimestampDate(timestamp) : "Date not available";
                        sellerPhone = phoneCode + " " + phoneNumber;

                        binding.sellerNameTv.setText(name);
                        binding.memberSinceTv.setText(formattedDate);

                        try {
                            Glide.with(AdDetailsActivity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.sellerProfileIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }

    private void checkIsFavorite() {
        Log.d(TAG, "checkIsFavorite: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favorite = snapshot.exists();
                        if (favorite) {
                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_yes);
                        } else {
                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_no);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }

    private void loadAdImages() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        imageSliderArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);
                            imageSliderArrayList.add(modelImageSlider);
                        }

                        AdapterImageSlider adapterImageSlider = new AdapterImageSlider(AdDetailsActivity.this, imageSliderArrayList);
                        binding.imageSliderVp.setAdapter(adapterImageSlider);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }

    private void deleteAd() {
        Log.d(TAG, "deleteAd: ");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Deleting ad...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onSuccess: Deleted ad");
                    Utils.toast(AdDetailsActivity.this, "Ad Deleted");
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onFailure: ", e);
                    Utils.toast(AdDetailsActivity.this, "Failed to Delete Ad due to " + e.getMessage());
                });
    }
}














