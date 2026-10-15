package com.vedant.vibe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.vedant.vibe.Database.DatabaseHelper;

public class CompleteProfileActivity extends AppCompatActivity {

    EditText editTextUsername;
    TextView tvGreeting;
    AppCompatButton btnCompleteProfile;
    private DatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        dbHelper = new DatabaseHelper(this);

        findId();
        setupGreeting();
        setupListeners();
    }
    private void findId(){
        editTextUsername = findViewById(R.id.editTextUsername);
        btnCompleteProfile = findViewById(R.id.btnCompleteProfile);
        tvGreeting = findViewById(R.id.tvGreeting);
    }
    private void setupGreeting() {
        String userName = getIntent().getStringExtra("USER_NAME");
        userId = getIntent().getLongExtra("USER_ID", -1);

        if (userName != null && !userName.isEmpty()) {
            tvGreeting.setText("Welcome, " + userName + "!");
        }
    }
    private void setupListeners() {
        btnCompleteProfile.setOnClickListener(v -> saveUsernameAndProceed());
    }

    private void saveUsernameAndProceed() {
        String username = editTextUsername.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username cannot be empty.");
            return;
        }

        if (userId != -1) {
            int rowsAffected = dbHelper.updateUsername(userId, username);
            if (rowsAffected > 0) {
                Toast.makeText(CompleteProfileActivity.this, "Profile complete!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CompleteProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(CompleteProfileActivity.this, "Failed to save username.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: User ID not found. Cannot save profile.", Toast.LENGTH_LONG).show();
        }
    }
}
