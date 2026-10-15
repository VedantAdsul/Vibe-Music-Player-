package com.vedant.vibe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vedant.vibe.Database.DatabaseHelper;
import com.vedant.vibe.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private Uri selectedImageUri;
    private DatabaseHelper dbHelper;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private long currentUserId;
    private static final String PREF_NAME = "user_data";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(binding.profileImageEdit);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        loadInitialData();
        setupClickListeners();
    }

    private void loadInitialData() {
        Cursor cursor = dbHelper.getUserById(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EMAIL));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PROFILE_IMAGE_URI));

            binding.usernameEditText.setText(username);
            binding.emailEditText.setText(email);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(EditProfileActivity.this).load(Uri.parse(imageUrl)).placeholder(R.drawable.ic_user).into(binding.profileImageEdit);
            } else {
                binding.profileImageEdit.setImageResource(R.drawable.ic_user);
            }
            cursor.close();
        } else {
            Toast.makeText(EditProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.profileImageContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
        binding.saveChangesButton.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String newUsername = binding.usernameEditText.getText().toString().trim();
        String newEmail = binding.emailEditText.getText().toString().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        String imageUriString = null;
        if (selectedImageUri != null) {
            imageUriString = selectedImageUri.toString();
        } else {
            Cursor cursor = dbHelper.getUserById(currentUserId);
            if (cursor != null && cursor.moveToFirst()) {
                imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PROFILE_IMAGE_URI));
                cursor.close();
            }
        }


        int rowsAffected = dbHelper.updateUser(currentUserId, newUsername, newEmail, imageUriString);
        progressDialog.dismiss();

        if (rowsAffected > 0) {
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userEmail", newEmail);
            editor.apply();
            finish();
        } else {
            Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
        }
    }
}
