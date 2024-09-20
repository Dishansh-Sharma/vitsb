package com.example.vitsp.activity;

import android.content.Intent;
import android.os.StrictMode;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.Manifest;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import com.example.vitsp.fragment.AccountFragment;
import com.example.vitsp.fragment.ChatsFragment;
import com.example.vitsp.fragment.HomeFragment;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.databinding.ActivityMainBinding;
import com.example.vitsp.fragment.myadsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Uncomment this to enable edge-to-edge layout
//        EdgeToEdge.enable(this);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });







        // Initialize View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startLoginOptions();
        }
        else{
            updateFCMToken();
            askNotificationPermission();
        }

        // Show the home fragment by default
        showHomeFragment();

        // Handle bottom navigation item selection using if-else statements
        binding.bottomNv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_home) {
                    showHomeFragment();
                    return true;
                }
                else if (itemId == R.id.menu_chats) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this,"Login Required...");
                        startLoginOptions();
                        return false;
                    }
                    else {
                        showChatsFragment();
                        return true;
                    }
                }
                else if (itemId == R.id.menu_my_ads) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this,"Login Required...");
                        startLoginOptions();
                        return false;
                    } else {
                        showMyAdsFragment();
                        return true;
                    }
                } else if (itemId == R.id.menu_account) {
//                    Utils.toast(MainActivity.this,"Login Required...");
                    showAccountFragment();
                    return true;
                } else {
                    showAccountFragment();
                    return true;

                }
            }
        });


        binding.sellFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AdCreateActivity.class);
                intent.putExtra("isEditMode", false);

                startActivity(intent);
            }
        });
    }

    // Method to show the Home fragment
    private void showHomeFragment() {
        binding.toolbarTittleTv.setText("Home");
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentFl.getId(), fragment, "HomeFragment");
        fragmentTransaction.commit();
    }

    // Method to show the Chats fragment
    private void showChatsFragment() {
        binding.toolbarTittleTv.setText("Chats");
        ChatsFragment fragment = new ChatsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentFl.getId(), fragment, "ChatsFragment");
        fragmentTransaction.commit();
    }

    // Method to show the My Ads fragment
    private void showMyAdsFragment() {
        binding.toolbarTittleTv.setText("My Ads");
        myadsFragment fragment = new myadsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentFl.getId(), fragment, "mydsFragment");
        fragmentTransaction.commit();
    }

    // Method to show the Account fragment
    private void showAccountFragment() {
        binding.toolbarTittleTv.setText("Account");
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentFl.getId(), fragment, "AccountFragment");
        fragmentTransaction.commit();
    }

    // Method to start the LoginOptionsActivity
    private void startLoginOptions() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }


    public void updateFCMToken() {

        String myUid = ""+firebaseAuth.getUid();
        Log.d(TAG, "updateFCMToken: myUid: " + myUid);


        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d(TAG, "updateFCMToken: token: " + token);


                        HashMap<String , Object> hashMap = new HashMap();
                        hashMap.put("token", token);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(myUid)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Log.d(TAG, "onSuccess: Token Updated!....");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.e(TAG,"updateFCMToken: onFailure ",e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "updateFCMToken: onFailure: " , e);
                    }
                });


    }



    private void askNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    private ActivityResultLauncher<String>  requestNotificationPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {

                    Log.d(TAG, "onActivityResult: Notification Permission STATUS: " + isGranted);

                }
            }

    );






}


















