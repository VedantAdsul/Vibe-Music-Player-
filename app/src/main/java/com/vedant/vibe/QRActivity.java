package com.vedant.vibe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {

    private static final String TAG = "QRActivity";

    private ImageView qrCodeImageView;
    private TextView nameTextView;
    private Button scanQrButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupListeners();
        checkUserStatus();
    }

    private void initializeViews() {
        qrCodeImageView = findViewById(R.id.qr_code_imageview);
        nameTextView = findViewById(R.id.name_textview);
        scanQrButton = findViewById(R.id.scan_qr_button);
    }

    private void setupListeners() {
        scanQrButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR Code");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            nameTextView.setText("Scanned: " + result.getContents());
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserProfileData(FirebaseUser currentUser) {
        DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(QRActivity.this, "User data not found.", Toast.LENGTH_LONG).show();
                    return;
                }
                String username = dataSnapshot.child("username").getValue(String.class);

                if (username == null || username.isEmpty()) {
                    Log.w(TAG, "Username is null or empty for user: " + currentUser.getUid());
                    Toast.makeText(QRActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                generateAndDisplayQrCode(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUserProfile:onCancelled", databaseError.toException());
                Toast.makeText(QRActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void generateAndDisplayQrCode(String text) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "Error generating QR code", e);
            Toast.makeText(this, "Could not generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUser.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fetchUserProfileData(currentUser);
            } else {
                Log.w(TAG, "User reload failed.", task.getException());
                Toast.makeText(QRActivity.this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}