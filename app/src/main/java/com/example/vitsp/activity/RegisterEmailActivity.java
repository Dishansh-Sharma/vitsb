package com.example.vitsp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vitsp.Utils;
import com.example.vitsp.databinding.ActivityRegisterEmailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterEmailActivity extends AppCompatActivity {

    private ActivityRegisterEmailBinding binding;
    private static final String TAG = "Register_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private String email, password, cPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(v -> onBackPressed());

        binding.haveAccountTv.setOnClickListener(v -> onBackPressed());

        binding.registerBtn.setOnClickListener(v -> validateData());
    }

    private void validateData() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString();
        cPassword = binding.cPasswordEt.getText().toString();

        Log.d(TAG, "validateData: email: " + email);
        Log.d(TAG, "validateData: password: " + password);
        Log.d(TAG, "validateData: cPassword: " + cPassword);

        // Check if email is valid and has the correct domain
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@vitbhopal.ac.in")) {
            binding.emailEt.setError("Invalid Email. Only @vitbhopal.ac.in domain is allowed.");
            binding.emailEt.requestFocus();
        } else if (password.isEmpty()) {
            binding.passwordEt.setError("Enter Password");
            binding.passwordEt.requestFocus();
        } else if (!password.equals(cPassword)) {
            binding.cPasswordEt.setError("Password doesn't match");
            binding.cPasswordEt.requestFocus();
        } else {
            // Check if email already exists
            checkEmailExists(email);
        }
    }

    private void checkEmailExists(String email) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            registerUser();
                        } else {
                            binding.emailEt.setError("Email is already registered.");
                            binding.emailEt.requestFocus();
                        }
                    } else {
                        Log.e(TAG, "checkEmailExists: Error checking email existence", task.getException());
                        Utils.toast(RegisterEmailActivity.this, "Failed to check email existence.");
                    }
                });
    }

    private void registerUser() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "onSuccess: Register Success");
                    updateUserInfo(); // Call the method to update user info
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Registration failed", e);
                    Utils.toast(RegisterEmailActivity.this, "Failure due to " + e.getMessage());
                    progressDialog.dismiss();
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving User Info");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "");
        hashMap.put("phoneCode", "");
        hashMap.put("phoneNumber", "");
        hashMap.put("profileImageUrl", "");
        hashMap.put("dob", "");
        hashMap.put("userType", "Email");
        hashMap.put("TypingTo", "");
        hashMap.put("Timestamp", timestamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: User info saved...");
                    sendVerificationEmail(); // Proceed to send verification email
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Failed to save user info", e);
                    progressDialog.dismiss();
                    Utils.toast(RegisterEmailActivity.this, "Failed to save info due to " + e.getMessage());
                });
    }

    private void sendVerificationEmail() {
        firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSuccess: Verification Email Sent.");
                    progressDialog.dismiss();
                    Utils.toast(RegisterEmailActivity.this, "Verification email sent. Please verify and log in again.");
                    // Prompt the user to log in after verification
                    startActivity(new Intent(RegisterEmailActivity.this, LoginEmailActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Failed to send verification email", e);
                    Utils.toast(RegisterEmailActivity.this, "Failed to send verification email.");
                    progressDialog.dismiss();
                });
    }

    private static class User {
        public String uid;
        public String email;

        // Constructor
        public User(String uid, String email) {
            this.uid = uid;
            this.email = email;
        }
    }
}
