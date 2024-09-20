package com.example.vitsp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vitsp.Utils;
import com.example.vitsp.databinding.ActivityLoginEmailBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends AppCompatActivity {

    private ActivityLoginEmailBinding binding;
    private static final String TAG = "LOGIN_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login_email);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        super.onCreate(savedInstanceState);

        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(v -> onBackPressed());

        binding.noAccountTv.setOnClickListener(v -> startActivity(new Intent(LoginEmailActivity.this, RegisterEmailActivity.class)));

        binding.forgotPasswordTv.setOnClickListener(v -> startActivity(new Intent(LoginEmailActivity.this, ForgotPasswordActivity.class)));

        binding.loginBtn.setOnClickListener(v -> validateData());
    }

    private String email, password;

    private void validateData() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString();

        // Check if email is in the correct domain
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@vitbhopal.ac.in")) {
            binding.emailEt.setError("Invalid Email. Only @vitbhopal.ac.in domain is allowed.");
            binding.emailEt.requestFocus();
        } else if (password.isEmpty()) {
            binding.passwordEt.setError("Enter Password");
            binding.passwordEt.requestFocus();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Log.d(TAG, "onSuccess: Logged In...");
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                        finishAffinity();
                    } else {
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        Utils.toast(LoginEmailActivity.this, "Please verify your email address.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    Utils.toast(LoginEmailActivity.this, "Failure due to " + e.getMessage());
                    progressDialog.dismiss();
                });
    }
}

