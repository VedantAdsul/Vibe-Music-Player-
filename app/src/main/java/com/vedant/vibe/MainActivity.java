package com.vedant.vibe;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vedant.vibe.Database.DatabaseHelper;
import com.vedant.vibe.Fragment.HomeFragment;
import com.vedant.vibe.Fragment.MusicPlayerFragment;
import com.vedant.vibe.Fragment.profileFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    private final FragmentManager fm = getSupportFragmentManager();
    private final HomeFragment homeFragment = new HomeFragment();
    private final MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();
    private final profileFragment profileFragment = new profileFragment();
    private Fragment activeFragment = homeFragment;
    private int currentTabId = R.id.home;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String PREF_NAME = "user_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .add(R.id.homeframelayout, profileFragment, "PROFILE").hide(profileFragment)
                    .add(R.id.homeframelayout, musicPlayerFragment, "MUSIC_PLAYER").hide(musicPlayerFragment)
                    .add(R.id.homeframelayout, homeFragment, "HOME")
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                switchFragment(homeFragment, itemId);
                return true;
            } else if (itemId == R.id.music_player) {
                switchFragment(musicPlayerFragment, itemId);
                return true;
            } else if (itemId == R.id.profile) {
                switchFragment(profileFragment, itemId);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
    private int getTabIndex(int tabId) {
        if (tabId == R.id.home) return 0;
        if (tabId == R.id.music_player) return 1;
        if (tabId == R.id.profile) return 2;
        return -1;
    }

    private void switchFragment(Fragment fragment, int newTabId) {
        if (newTabId == currentTabId) return;

        int enterAnim, exitAnim;
        int currentTabIndex = getTabIndex(currentTabId);
        int newTabIndex = getTabIndex(newTabId);

        if (newTabIndex > currentTabIndex) {
            enterAnim = R.anim.slide_in_right;
            exitAnim = R.anim.slide_out_left;
        } else {
            enterAnim = R.anim.slide_in_left;
            exitAnim = R.anim.slide_out_right;
        }

        fm.beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim)
                .hide(activeFragment)
                .show(fragment)
                .commit();

        activeFragment = fragment;
        currentTabId = newTabId;
    }

    public void openMusicPlayer(Bundle songData) {
        if (musicPlayerFragment instanceof MusicPlayerFragment) {
            int songIndex = songData.getInt("song_index", 0);
            musicPlayerFragment.setSong(songIndex);
        }
        if (currentTabId != R.id.music_player) {
            switchFragment(musicPlayerFragment, R.id.music_player);
        }

        bottomNavigationView.setSelectedItemId(R.id.music_player);
    }

    public void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

}
