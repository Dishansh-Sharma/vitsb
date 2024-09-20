package com.example.vitsp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vitsp.adapter.AdapterAd;
import com.example.vitsp.adapter.AdapterCategory;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.databinding.FragmentHomeBinding;
import com.example.vitsp.models.ModelAd;
import com.example.vitsp.models.ModelCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private static final String TAG = "Home_TAG";

    private Context mContext;

    private ArrayList<ModelAd> adArrayList;
    private AdapterAd adapterAd;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadCategories();
        loadAds("All");

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: Query: " + s);
                try {
                    String query = s.toString();
                    adapterAd.getFilter().filter(query);
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: ", e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Remove any location picker related code here
        // e.g., remove or comment out binding.locationCV setOnClickListener
    }

    private void loadAds(String category) {
        Log.d(TAG, "loadAds: Category:" + category);
        adArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelAd modelAd = ds.getValue(ModelAd.class);

                    // No location-based filtering
                    if (category.equals("All")) {
                        adArrayList.add(modelAd);
                    } else {
                        if (modelAd.getCategory().equals(category)) {
                            adArrayList.add(modelAd);
                        }
                    }
                }
                adapterAd = new AdapterAd(mContext, adArrayList);
                binding.adsRv.setAdapter(adapterAd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
    }

    private void loadCategories() {
        ArrayList<ModelCategory> categoryArrayList = new ArrayList<>();

        ModelCategory modelCategoryAll = new ModelCategory("All", R.drawable.ic_category_all);
        categoryArrayList.add(modelCategoryAll);

        for (int i = 0; i < Utils.categories.length; i++) {
            ModelCategory modelCategory = new ModelCategory(Utils.categories[i], Utils.categoryIcons[i]);
            categoryArrayList.add(modelCategory);
        }
        AdapterCategory adapterCategory = new AdapterCategory(mContext, categoryArrayList, modelCategory -> loadAds(modelCategory.getCategory()));
        binding.categoriesRv.setAdapter(adapterCategory);
    }
}
