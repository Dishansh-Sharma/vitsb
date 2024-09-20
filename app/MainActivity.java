package com.example.vitsp;

import static android.content.Intent.makeMainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.vitsp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
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
                } else if (itemId == R.id.menu_chats) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        startLoginOptions();
                        return false;
                    } else {
                        showChatsFragment();
                        return true;
                    }
                } else if (itemId == R.id.menu_my_ads) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        startLoginOptions();
                        return false;
                    } else {
                        showMyAdsFragment();
                        return true;
                    }
                } else if (itemId == R.id.menu_account) {
                    showAccountFragment();
                    return true;
                } else {
                    return false;
                }
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
}
