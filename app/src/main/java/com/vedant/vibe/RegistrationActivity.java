package com.vedant.vibe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.vedant.vibe.Database.DatabaseHelper;

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextUser, editTextEmail, editTextPassword;
    Button btnRegister;
    TextView tvLoginLink;
    ImageView ivTogglePassword;
    AppCompatButton btnGoogleSignIn;
    final boolean[] isPasswordVisible = {false};
    SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private static final String TAG = "RegistrationActivity";
    private static final String PREF_NAME = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_registration);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
            return;
        }

        FindId();
        setupListeners();
        configureGoogleSignIn();
    }

    private void FindId(){
        editTextUser = findViewById(R.id.editTextUser);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
    }

    private void setupListeners() {
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.eye_off);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.eye_on);
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
            isPasswordVisible[0] = !isPasswordVisible[0];
        });
        btnRegister.setOnClickListener(v -> registerUser());
        tvLoginLink.setOnClickListener(v -> redirectToLogin());
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    } else {
                        Toast.makeText(this, "Google Sign-In cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();
            String username = account.getDisplayName();

            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "Could not get email from Google account.", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor userCursor = dbHelper.getUserByEmail(email);
            if (userCursor.getCount() > 0) {
                userCursor.moveToFirst();
                long userId = userCursor.getLong(userCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
                userCursor.close();
                loginUser(userId, email, "Login Successful!");
            } else {
                userCursor.close();
                String password = "google_sign_in_user_" + System.currentTimeMillis();
                long userId = dbHelper.addUser(username, email, password);
                if (userId > 0) {
                    loginUser(userId, email, "Registration Successful!");
                } else {
                    Toast.makeText(RegistrationActivity.this, "Failed to register Google user.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign-In Failed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void registerUser() {
        String username = editTextUser.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            editTextUser.setError("Username is required");
            editTextUser.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        Cursor userCursor = dbHelper.getUserByEmail(email);
        if (userCursor.getCount() > 0) {
            Toast.makeText(RegistrationActivity.this, "User with this email already exists.", Toast.LENGTH_LONG).show();
            userCursor.close();
            return;
        }
        userCursor.close();


        long userId = dbHelper.addUser(username, email, password);

        if (userId > 0) {
            loginUser(userId, email, "Registration Successful!");
        } else {
            Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void loginUser(long userId, String email, String message) {
        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putLong("userId", userId);
        editor.putString("userEmail", email);
        editor.apply();

        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
