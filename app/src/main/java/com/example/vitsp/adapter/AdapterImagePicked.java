package com.example.vitsp.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.databinding.RowImagesPickedBinding;
import com.example.vitsp.models.ModelImagePicked;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterImagePicked extends RecyclerView.Adapter<AdapterImagePicked.HolderImagePicked> {

    private static final String TAG = "IMAGES_TAG";
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private Context context;
    private String adId;

    public AdapterImagePicked(ArrayList<ModelImagePicked> imagePickedArrayList, Context context, String adId) {
        this.imagePickedArrayList = imagePickedArrayList;
        this.context = context;
        this.adId = adId;
    }

    @NonNull
    @Override
    public HolderImagePicked onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowImagesPickedBinding binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderImagePicked(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImagePicked holder, int position) {
        ModelImagePicked model = imagePickedArrayList.get(position);

        if (model.getFromInternet()) {
            String imageUrl = model.getImageUrl(); // Corrected method name
            Log.d(TAG, "onBindViewHolder: imageUrl: " + imageUrl);
            try {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.imageiv);
            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            }
        } else {
            Uri imageUri = model.getImageUri();
            Log.d(TAG, "onBindViewHolder: imageUri: " + imageUri);
            try {
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.imageiv);
            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            }
        }

        holder.closeBtn.setOnClickListener(v -> {
            if (model.getFromInternet()) {
                deleteImageFirebase(model, holder, position);
            } else {
                imagePickedArrayList.remove(model);
                notifyItemRemoved(position);
            }
        });
    }

    private void deleteImageFirebase(ModelImagePicked model, HolderImagePicked holder, int position) {
        String imageId = model.getId();
        Log.d(TAG, "deleteImageFirebase: adId: " + adId);
        Log.d(TAG, "deleteImageFirebase: imageId: " + imageId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images").child(imageId).removeValue()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: Deleted");
                    Utils.toast(context, "Image Deleted!");
                    try {
                        imagePickedArrayList.remove(model);
                        notifyItemRemoved(position);
                    } catch (Exception e) {
                        Log.e(TAG, "onSuccess: ", e);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete image due to: " + e.getMessage()));
    }

    @Override
    public int getItemCount() {
        return imagePickedArrayList.size();
    }

    static class HolderImagePicked extends RecyclerView.ViewHolder {
        ImageView imageiv;
        ImageView closeBtn;

        public HolderImagePicked(RowImagesPickedBinding binding) {
            super(binding.getRoot());
            imageiv = binding.imageiv;
            closeBtn = binding.closeBtn;
        }
    }
}
