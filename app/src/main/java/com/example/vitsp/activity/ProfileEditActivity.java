package com.example.vitsp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.databinding.ActivityProfileEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private  static final  String TAG ="PROFILE_EDIT_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri = null;
    private String myUserType ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_profile_edit);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        binding =ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        loadMyInfo();


        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.profileImagePickFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatedata();
            }
        });

    }

    private String name ="";
    private String email ="";
    private String phoneCode ="";
    private String phoneNumber ="";
    private String dob ="";

//    private void validatedata(){
//        name = binding.nameEt.getText().toString().trim();
//        dob = binding.dobEt.getText().toString().trim();
//        email = binding.emailEt.getText().toString().trim();
//        phoneCode =binding.CountryCodePicker.getSelectedCountryCodeWithPlus();
//        phoneNumber = binding.phoneNumberEt.getText().toString().trim();
//
//        if(imageUri == null){
//            updateProfileDb(null);
//
//        }
//        else {
//            uploadProfileImageStorage();
//        }
//
//
//    }



    private void validatedata() {
        name = binding.nameEt.getText().toString().trim();
        dob = binding.dobEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phoneCode = binding.CountryCodePicker.getSelectedCountryCodeWithPlus();
        phoneNumber = binding.phoneNumberEt.getText().toString().trim();

        if (email.isEmpty()) {
            email = null;
            email = "";
        }

        if (phoneNumber.isEmpty()) {
            phoneNumber = null;
            phoneNumber = "";
        }


        if (imageUri == null) {
            updateProfileDb(null);
            updateProfileDb("");

        } else {
            uploadProfileImageStorage();
        }
    }












//    private  void uploadProfileImageStorage(){
//
//        Log.d(TAG,"uploadProfileImageStorage: ");
//        progressDialog.setMessage("Uploading user profile image...");
//        progressDialog.show();
//        String  filePathAndName = "UserImages/"+"profile_"+firebaseAuth.getUid();
//
//        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
//        ref.putFile(imageUri)
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                        double progress =(100.0 * snapshot.getBytesTransferred()) /snapshot.getTotalByteCount();
//
//                        Log.d(TAG,"onProgress: Progress: "+progress);
//                        progressDialog.setMessage("Uploading profile image. Progress: "+(int)progress+"%");
//                    }
//                })
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Log.d(TAG,"onSuccess: Uploaded");
//
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while(!uriTask.isSuccessful());
//                        String uploadedImageUrl = uriTask.getResult().toString();
//
//
//                        if(uriTask.isSuccessful()){
//                            updateProfileDb(uploadedImageUrl);
//                        }
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG,"onFailure: ",e);
//                        progressDialog.dismiss();
//                        Utils.toast(ProfileEditActivity.this,"Failed to update info due to "+e.getMessage());
//
//
//                    }
//                });
//
//
//    }

    private void uploadProfileImageStorage() {
        Log.d(TAG, "uploadProfileImageStorage: ");
        progressDialog.setMessage("Uploading user profile image...");
        progressDialog.show();
        String filePathAndName = "UserImages/" + "profile_" + firebaseAuth.getUid();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: Progress: " + progress);
                        progressDialog.setMessage("Uploading profile image. Progress: " + (int) progress + "%");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Uploaded");

                        // Get the download URL
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String uploadedImageUrl = task.getResult().toString();
                                    Log.d(TAG, "onSuccess: Uploaded image URL: " + uploadedImageUrl);
                                    updateProfileDb(uploadedImageUrl);
                                } else {
                                    Log.e(TAG, "onFailure: Failed to get download URL", task.getException());
                                    progressDialog.dismiss();
                                    Utils.toast(ProfileEditActivity.this, "Failed to get download URL: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(ProfileEditActivity.this, "Failed to update info due to " + e.getMessage());
                    }
                });
    }

//    private  void updateProfileDb(String imageurl){
//
//        progressDialog.setMessage("Updating user info...");
//        progressDialog.show();
//        HashMap<String,Object> hashMap = new HashMap<>();
//        hashMap.put("name",""+name);
//        hashMap.put("dob",""+dob);
//        if(imageurl != null){
//            hashMap.put("profileImageUrl",""+imageurl);
//        }
//
//
//        if(myUserType.equalsIgnoreCase("Phone") ){
//            hashMap.put("email",""+email);
//        }
//        else if(myUserType.equalsIgnoreCase("Email") || myUserType.equalsIgnoreCase("Google")){
//
//
//            hashMap.put("phoneCode",""+phoneCode);
//            hashMap.put("phoneNumber",""+phoneNumber);
//        }
//
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(firebaseAuth.getUid())
//                .updateChildren(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG,"onSuccess: Info updated");
//                        progressDialog.dismiss();
//                        Utils.toast(ProfileEditActivity.this,"Profile Updated...");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG,"onFailure: ",e);
//                        progressDialog.dismiss();
//                        Utils.toast(ProfileEditActivity.this,"Failed to update info due to "+e.getMessage());
//
//
//                    }
//                });
//    }


    private void updateProfileDb(String imageurl) {
        progressDialog.setMessage("Updating user info...");
        progressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("dob", dob);

        if (imageurl != null) {
            hashMap.put("profileImageUrl", imageurl);
        }

        if (myUserType.equalsIgnoreCase("Phone")) {
            if (email != null) {
                hashMap.put("email", email);
            }
        } else if (myUserType.equalsIgnoreCase("Email") || myUserType.equalsIgnoreCase("Google")) {
            if (phoneCode != null) {
                hashMap.put("phoneCode", phoneCode);
            }
            if (phoneNumber != null) {
                hashMap.put("phoneNumber", phoneNumber);
            }
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Info updated");
                        progressDialog.dismiss();
                        Utils.toast(ProfileEditActivity.this, "Profile Updated...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(ProfileEditActivity.this, "Failed to update info due to " + e.getMessage());
                    }
                });
    }








//    private void  loadMyInfo() {
//        Log.d(TAG,"loadMyInfo: ");
//
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String dob = "" +snapshot.child("dob").getValue();
//                        String email = "" +snapshot.child("email").getValue();
//
//                        String name = "" +snapshot.child("name").getValue();
//                        String phoneCode = "" +snapshot.child("phoneCode").getValue();
//                        String phoneNumber = "" +snapshot.child("phoneNumber").getValue();
//                        String profileImageUrl = "" +snapshot.child("profileImageUrl").getValue();
//
//                        String timestamp = "" +snapshot.child("timestamp").getValue();
//                        myUserType = "" +snapshot.child("userType").getValue();
//                        String phone = phoneCode+phoneNumber;
//
//                        if(myUserType.equalsIgnoreCase("Email") || myUserType.equalsIgnoreCase("Google")){
//                            binding.emailTil.setEnabled(false);
//                            binding.emailEt.setEnabled(false);
//                        }
//                        else{
//                            binding.phoneNumberTil.setEnabled(false);
//                            binding.phoneNumberEt.setEnabled(false);
//                            binding.CountryCodePicker.setEnabled(false);
//                        }
//                        binding.nameEt.setText(name);
//                        binding.emailEt.setText(email);
//                        binding.phoneNumberEt.setText(phone);
//                        binding.dobEt.setText(dob);
//                        try{
//                            int phoneCodeInt  = Integer.parseInt(phoneCode.replace("+",""));
//                            binding.CountryCodePicker.setCountryForPhoneCode(phoneCodeInt);
//                        }
//                        catch (Exception e){
//                            Log.e(TAG,"onDATAChange: ",e);
//                        }
//
//                        try{
//                            Glide.with(ProfileEditActivity.this)
//                                    .load(profileImageUrl)
//                                    .placeholder(R.drawable.ic_person_white)
//                                    .into(binding.profileiv);
//                        }
//                        catch (Exception e){
//                            Log.e(TAG,"onDATAChange: ",e);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }




//    private void loadMyInfo() {
//        Log.d(TAG, "loadMyInfo: ");
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String dob = "" + snapshot.child("dob").getValue();
//                        String email = "" + snapshot.child("email").getValue();
//                        String name = "" + snapshot.child("name").getValue();
//                        String phoneCode = "" + snapshot.child("phoneCode").getValue();
//                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
//                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
//                        String timestamp = "" + snapshot.child("timestamp").getValue();
//                        myUserType = "" + snapshot.child("userType").getValue();
//                        String phone = phoneCode + phoneNumber;
//
//                        if (myUserType.equalsIgnoreCase("Email") || myUserType.equalsIgnoreCase("Google")) {
//                            binding.emailTil.setEnabled(false);
//                            binding.emailEt.setEnabled(false);
//                        } else {
//                            binding.phoneNumberTil.setEnabled(false);
//                            binding.phoneNumberEt.setEnabled(false);
//                            binding.CountryCodePicker.setEnabled(false);
//                        }
//                        binding.nameEt.setText(name);
//                        binding.emailEt.setText(email);
//                        binding.phoneNumberEt.setText(phone);
//                        binding.dobEt.setText(dob);
//
//                        // Ensure phoneCode is not null or empty before parsing
//                        if (phoneCode != null && !phoneCode.isEmpty() && !phoneCode.equals("null")) {
//                            try {
//                                int phoneCodeInt = Integer.parseInt(phoneCode.replace("+", ""));
//                                binding.CountryCodePicker.setCountryForPhoneCode(phoneCodeInt);
//                            } catch (NumberFormatException e) {
//                                Log.e(TAG, "onDataChange: Invalid phone code format", e);
//                            }
//                        }
//
//                        try {
//                            Glide.with(ProfileEditActivity.this)
//                                    .load(profileImageUrl)
//                                    .placeholder(R.drawable.ic_person_white)
//                                    .into(binding.profileiv);
//                        } catch (Exception e) {
//                            Log.e(TAG, "onDataChange: ", e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle database error
//                    }
//                });
//    }


    private void loadMyInfo() {
        Log.d(TAG, "loadMyInfo: ");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String dob = "" + snapshot.child("dob").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String phoneCode = "" + snapshot.child("phoneCode").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        myUserType = "" + snapshot.child("userType").getValue();
                        String phone = phoneCode + phoneNumber;

                        if (myUserType.equalsIgnoreCase("Email") || myUserType.equalsIgnoreCase("Google")) {
                            binding.emailTil.setEnabled(false);
                            binding.emailEt.setEnabled(false);
                        } else {
                            binding.phoneNumberTil.setEnabled(false);
                            binding.phoneNumberEt.setEnabled(false);
                            binding.CountryCodePicker.setEnabled(false);
                        }
                        binding.nameEt.setText(name);
                        binding.emailEt.setText(email != null ? email : "");
                        binding.phoneNumberEt.setText(phone != null ? phone : "");
                        binding.dobEt.setText(dob);

                        if (phoneCode != null && !phoneCode.isEmpty() && !phoneCode.equals("null")) {
                            try {
                                int phoneCodeInt = Integer.parseInt(phoneCode.replace("+", ""));
                                binding.CountryCodePicker.setCountryForPhoneCode(phoneCodeInt);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "onDataChange: Invalid phone code format", e);
                            }
                        }

                        try {
                            Glide.with(ProfileEditActivity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.profileiv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }












    private void imagePickDialog(){
        PopupMenu popupMenu = new PopupMenu(this, binding.profileImagePickFab);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == 1){
                    Log.d(TAG,"OnMenuItemClick: Camera Clicked,check if camera permission(s) granted or not");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        requestCameraPermissions.launch(new String[]{android.Manifest.permission.CAMERA});

                    }
                    else{
                        requestCameraPermissions.launch(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE});

                    }
                } else if (itemId == 2) {

                    Log.d(TAG,"OnMenuItemClick: Check if Stroage permission(s) granted or not");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        pickImageGallery();
                    }
                    else{
                        requestStoragepermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    }

                }

                return false;
            }
        });
    }

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG,"onActivityResult: "+result.toString());
                    boolean areAllGranted = true;
                    for(Boolean isGranted : result.values()){
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if(areAllGranted){
                        Log.d(TAG,"onActivityResult: All Granted e.g. Camera , Stroage");
                        pickImageCamera();
                    }
                    else{
                        Log.d(TAG,"onActivityResult: All Granted e.g. Camera , Stroage");
                        Utils.toast(ProfileEditActivity.this,"Camera or Storage or both permission denied...");

                    }
                }
            }
    );




    private ActivityResultLauncher<String>  requestStoragepermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG,"onActivityResult: isGranted: "+isGranted);
                    if(isGranted){
                        pickImageGallery();
                    }
                    else {
                        Utils.toast(ProfileEditActivity.this,"Stroage permission denied...!");
                    }

                }
            }
    );




    private void pickImageCamera(){
        Log.d(TAG,"pickImageCamera: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"TEMP_TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"TEMP_DESCRIPTION");

        imageUri =getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);


    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG,"onActivityResult : Image Captured: "+imageUri);
                        try{
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.profileiv);
                        }
                        catch (Exception e){
                            Log.d(TAG,"onActivityResult :  ",e);

                        }
                    }
                    else{
                        Utils.toast(ProfileEditActivity.this,"Cancelled...");
                    }
                }
            }
    );


    private void pickImageGallery(){
        Log.d(TAG,"pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);


    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data =result.getData();
                        imageUri =data.getData();
                        Log.d(TAG,"onActivityResult : Image Picked From Gallery: "+imageUri);

                        try{
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.profileiv);
                        }
                        catch (Exception e){
                            Log.d(TAG,"onActivityResult :  ",e);

                        }

                    }
                    else{
                        Utils.toast(ProfileEditActivity.this,"Cancelled...");
                    }
                }
            }
    );




}
