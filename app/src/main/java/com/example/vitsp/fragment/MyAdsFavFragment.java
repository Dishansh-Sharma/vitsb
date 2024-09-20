package com.example.vitsp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vitsp.adapter.AdapterAd;
import com.example.vitsp.databinding.FragmentMyAdsFavBinding;
import com.example.vitsp.models.ModelAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdsFavFragment extends Fragment {

    private FragmentMyAdsFavBinding binding;
    private static final String TAG = "FAV_TAG";
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelAd> adArrayList;
    private AdapterAd adapterAd;

    public MyAdsFavFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyAdsFavBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAds();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String query = s.toString();
                    adapterAd.getFilter().filter(query);
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: ", e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
    }

    private void loadAds() {
        Log.d(TAG, "loadAds: ");
        adArrayList = new ArrayList<>();

        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Users");
        favRef.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String adId = "" + ds.child("adId").getValue();
                            Log.d(TAG, "onDataChange: adId: " + adId);

                            DatabaseReference adRef = FirebaseDatabase.getInstance().getReference("Ads");
                            adRef.child(adId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                ModelAd modelAd = snapshot.getValue(ModelAd.class);
                                                adArrayList.add(modelAd);
                                                if (adapterAd == null) {
                                                    adapterAd = new AdapterAd(getContext(), adArrayList);
                                                    binding.adsRv.setAdapter(adapterAd);
                                                } else {
                                                    adapterAd.notifyDataSetChanged();
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "onDataChange: ", e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e(TAG, "Database error: ", error.toException());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: ", error.toException());
                    }
                });
    }
}
