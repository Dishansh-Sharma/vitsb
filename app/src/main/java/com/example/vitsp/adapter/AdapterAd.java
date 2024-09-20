//package com.example.vitsp.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.vitsp.FilterAd;
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.activity.AdDetailsActivity;
//import com.example.vitsp.databinding.RowAdBinding;
//import com.example.vitsp.models.ModelAd;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//public class AdapterAd extends RecyclerView.Adapter<AdapterAd.HolderAd> implements Filterable {
//    private Context context;
//    public ArrayList<ModelAd> adArrayList;
//    private ArrayList<ModelAd> filterList;
//    private FilterAd filter;
//    private RowAdBinding binding;
//    private static final String TAG = "Adapter_Ad_TAG";
//    private FirebaseAuth firebaseAuth;
//
//    public AdapterAd(Context context, ArrayList<ModelAd> adArrayList) {
//        this.context = context;
//        this.adArrayList = adArrayList;
//        this.filterList = new ArrayList<>(adArrayList); // Copy of original list for filtering
//        firebaseAuth = FirebaseAuth.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public HolderAd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false);
//        return new HolderAd(binding.getRoot());
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HolderAd holder, int position) {
//        ModelAd modelAd = adArrayList.get(position);
//
//        String title = modelAd.getTitle();
//        String description = modelAd.getDescription();
//        String condition = modelAd.getCondition();
//        String price = modelAd.getPrice();
//        long timestamp = modelAd.getTimestamp();
//        String date = Utils.formatTimestampDate(timestamp);
//
//        loadAdFirstImage(modelAd, holder);
//
//        if (firebaseAuth.getCurrentUser() != null) {
//            checkIsFavorite(modelAd, holder);
//        }
//
//        holder.titleiv.setText(title);
//        holder.descriptionTv.setText(description);
//        holder.conditionTv.setText(condition);
//        holder.priceTv.setText(price);
//        holder.dateTv.setText(date);
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, AdDetailsActivity.class);
//                intent.putExtra("adId", modelAd.getId());
//                context.startActivity(intent);
//            }
//        });
//
//        holder.favbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean favorite = modelAd.isFavorite();
//
//                if (favorite) {
//                    Utils.removeFavourite(context, modelAd.getId());
//                } else {
//                    Utils.addToFavourite(context, modelAd.getId());
//                }
//            }
//        });
//    }
//
//    private void checkIsFavorite(ModelAd modelAd, HolderAd holder) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid()).child("Favorites").child(modelAd.getId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        boolean favorite = snapshot.exists();
//                        modelAd.setFavorite(favorite);
//                        if (favorite) {
//                            holder.favbtn.setImageResource(R.drawable.ic_fav_yes);
//                        } else {
//                            holder.favbtn.setImageResource(R.drawable.ic_fav_no);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void loadAdFirstImage(ModelAd modelAd, HolderAd holder) {
//        Log.d(TAG, "loadAdFirstImage: ");
//        String adId = modelAd.getId();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
//        reference.child(adId).child("images").limitToFirst(1)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            String imageUrl = "" + ds.child("imageUrl").getValue();
//                            Log.d(TAG, "onDataChange: imageUrl:" + imageUrl);
//                            try {
//                                Glide.with(context)
//                                        .load(imageUrl)
//                                        .into(holder.imageiv);
//                            } catch (Exception e) {
//                                Log.e(TAG, "onDataChange: ", e);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    @Override
//    public int getItemCount() {
//        return adArrayList.size();
//    }
//
//    @Override
//    public Filter getFilter() {
//        if (filter == null) {
//            filter = new FilterAd(this, filterList);
//        }
//        return filter;
//    }
//
//    class HolderAd extends RecyclerView.ViewHolder {
//
//        ShapeableImageView imageiv;
//        TextView titleiv, descriptionTv, conditionTv, priceSymbolTv, priceTv, dateTv;
//        ImageButton favbtn;
//
//        public HolderAd(@NonNull View itemView) {
//            super(itemView);
//            imageiv = binding.imageiv;
//            titleiv = binding.titleiv;
//            descriptionTv = binding.descriptionTv;
//            favbtn = binding.favbtn;
//            conditionTv = binding.conditionTv;
//            priceSymbolTv = binding.priceSymbolTv;
//            priceTv = binding.priceTv;
//            dateTv = binding.dateTv;
//        }
//    }
//}





































package com.example.vitsp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vitsp.databinding.RowAdBinding;

import com.bumptech.glide.Glide;
import com.example.vitsp.FilterAd;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.activity.AdDetailsActivity;

import com.example.vitsp.models.ModelAd;
import com.example.vitsp.models.ModelImageSlider;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterAd extends RecyclerView.Adapter<AdapterAd.HolderAd> implements Filterable {
    private Context context;
    public ArrayList<ModelAd> adArrayList;
    private ArrayList<ModelAd> filterList;
    private FilterAd filter;
//    private RowAdBinding binding;
    private RowAdBinding binding;
    private static final String TAG = "Adapter_Ad_TAG";
    private FirebaseAuth firebaseAuth;

    public AdapterAd(Context context, ArrayList<ModelAd> adArrayList) {
        this.context = context;
        this.adArrayList = adArrayList;
        this.filterList = new ArrayList<>(adArrayList); // Copy of original list for filtering
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderAd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderAd(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAd holder, int position) {
        ModelAd modelAd = adArrayList.get(position);

        String title = modelAd.getTitle();
        String description = modelAd.getDescription();
        String condition = modelAd.getCondition();
        String price = modelAd.getPrice();
        long timestamp = modelAd.getTimestamp();
        String formatteddate = Utils.formatTimestampDate(timestamp);

        loadAdFirstImage(modelAd, holder);

        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite(modelAd, holder);
        }



        holder.titleiv.setText(title);
        holder.descriptionTv.setText(description);
        holder.conditionTv.setText(condition);
        holder.priceTv.setText(price);
        holder.dateTv.setText(formatteddate);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdDetailsActivity.class);
            intent.putExtra("adId", modelAd.getId());
            context.startActivity(intent);
        });

        holder.favbtn.setOnClickListener(v -> {
            boolean favorite = modelAd.isFavorite();

            if (favorite) {
                Utils.removeFavourite(context, modelAd.getId());
            } else {
                Utils.addToFavourite(context, modelAd.getId());
            }
        });
    }

    private void checkIsFavorite(ModelAd modelAd, HolderAd holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(modelAd.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean favorite = snapshot.exists();
                        modelAd.setFavorite(favorite);
                        if (favorite) {
                            holder.favbtn.setImageResource(R.drawable.ic_fav_yes);
                        } else {
                            holder.favbtn.setImageResource(R.drawable.ic_fav_no);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void loadAdFirstImage(ModelAd modelAd, HolderAd holder) {
//        Log.d(TAG, "loadAdFirstImage: ");
//        String adId = modelAd.getId();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
//        reference.child(adId).child("images").limitToFirst(1)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            String imageUrl = "" + ds.child("imageUrl").getValue();
//                            Log.d(TAG, "onDataChange: imageUrl:" + imageUrl);
//                            try {
//                                Glide.with(context)
//                                        .load(imageUrl)
//                                        .placeholder(R.drawable.ic_image_gray) // Placeholder image
//                                        .into(holder.imageiv);
//                            } catch (Exception e) {
//                                Log.e(TAG, "onDataChange: ", e);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }



//    private void loadAdFirstImage(ModelAd modelAd, HolderAd holder) {
//        Log.d(TAG, "loadAdFirstImage: ");
//        String adId = modelAd.getId();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
//        reference.child(adId).child("images").limitToFirst(1)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            String imageUrl = "" + ds.child("imageUrl").getValue();
//                            Log.d(TAG, "onDataChange: imageUrl:" + imageUrl);
//                            try {
//                                Glide.with(context)
//                                        .load(imageUrl)
//                                        .placeholder(R.drawable.ic_image_gray) // Placeholder image
//                                        .into(holder.imageiv);
//                            } catch (Exception e) {
//                                Log.e(TAG, "onDataChange: ", e);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }



    private void loadAdFirstImage(ModelAd modelAd, HolderAd holder) {
        Log.d(TAG, "loadAdFirstImage: ");
        String adId = modelAd.getId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
        reference.child(adId).child("Images").limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);
                                if (modelImageSlider != null) {
                                    String imageUrl = modelImageSlider.getImageUrl();
                                    Log.d(TAG, "onDataChange: imageUrl: " + imageUrl);
                                    try {
                                        Glide.with(context)
                                                .load(imageUrl)
                                                .placeholder(R.drawable.ic_image_gray) // Placeholder image
                                                .into(holder.imageiv);
                                    } catch (Exception e) {
                                        Log.e(TAG, "onDataChange: ", e);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "No images found for this ad.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: ", error.toException());
                    }
                });
    }



    @Override
    public int getItemCount() {
        return adArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterAd(this, filterList);
        }
        return filter;
    }

    class HolderAd extends RecyclerView.ViewHolder {

        ShapeableImageView imageiv;
        TextView titleiv, descriptionTv, conditionTv, priceSymbolTv, priceTv, dateTv;
        ImageButton favbtn;

        public HolderAd(@NonNull View itemView) {
            super(itemView);

            imageiv = binding.imageiv;
            titleiv = binding.titleiv;
            descriptionTv = binding.descriptionTv;
            favbtn = binding.favbtn;
            conditionTv = binding.conditionTv;
            priceSymbolTv = binding.priceSymbolTv;
            priceTv = binding.priceTv;
            dateTv = binding.dateTv;
        }
    }
}