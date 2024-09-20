package com.example.vitsp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vitsp.R;
import com.example.vitsp.adapter.AdapterChats;
import com.example.vitsp.databinding.FragmentChatsBinding;
import com.example.vitsp.models.ModelChats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChatsFragment extends Fragment {



    private FragmentChatsBinding binding;
    private static final String TAG = "CHATS_TAG";
    private FirebaseAuth firebaseAuth;
    private String myUid;
    private Context mContext;
    private ArrayList<ModelChats> chatsArrayList;
    private AdapterChats adapterChats;


    @Override
    public void onAttach(@NonNull Context context) {

        this.mContext = context;
        super.onAttach(context);

    }

    public ChatsFragment(){

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();

        Log.d(TAG, "onViewCreated: myUid: " + myUid);


        loadChats();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    String query =s.toString();
                    adapterChats.getFilter().filter(query);
                }catch (Exception e){
                    Log.e(TAG, "onTextChanged: ",e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadChats() {

        chatsArrayList = new ArrayList<>();

//        adapterChats = new AdapterChats(mContext, chatsArrayList);
//        binding.chatsRv.setAdapter(adapterChats);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatsArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    String chatkey =""+ds.getKey();

                    Log.d(TAG,"onDataChange: chatkey: "+chatkey);

                    if(chatkey.contains(myUid)){
                        Log.d(TAG, "onDataChange: Contains");

                        ModelChats modelChats = new ModelChats();
                        modelChats.setChatkey(chatkey);
                        chatsArrayList.add(modelChats);

                    }
                    else{
                        Log.d(TAG, "onDataChange: Not Contains");

                    }
                }

                adapterChats = new AdapterChats(mContext, chatsArrayList);
                binding.chatsRv.setAdapter(adapterChats);

                sort();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sort(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.sort(chatsArrayList , (model1 ,model2) -> Long.compare(model2.getTimestamp(), model1.getTimestamp()));
                adapterChats.notifyDataSetChanged();
            }
        },1000);

    }
}