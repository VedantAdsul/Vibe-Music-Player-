package com.vedant.vibe.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vedant.vibe.AboutUsActivity;
import com.vedant.vibe.Database.DatabaseHelper;
import com.vedant.vibe.EditProfileActivity;
import com.vedant.vibe.MainActivity;
import com.vedant.vibe.R;

public class profileFragment extends Fragment {

    private View view;
    private TextView tvUsername, tvEmail;
    private ImageView profileImage;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private long currentUserId;
    private View optionPlaylists, optionFavourites, optionSettings, optionAbout, optionLogout, optiondelete;
    private MaterialButton editProfileButton;
    private static final String PREF_NAME = "user_data";



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new DatabaseHelper(getContext());
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);

        findID();
        setupOptions();
        setupEditProfileButton();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUserId != -1) {
            loadUserProfile();
        } else {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).signOut();
            }
        }
    }

    public void findID(){
        tvUsername = view.findViewById(R.id.user_name);
        tvEmail = view.findViewById(R.id.user_email);
        profileImage = view.findViewById(R.id.profile_image);
        editProfileButton = view.findViewById(R.id.edit_profile_button);

        optionPlaylists = view.findViewById(R.id.option_playlists);
        optionFavourites = view.findViewById(R.id.option_favourites);
        optionSettings = view.findViewById(R.id.option_settings);
        optionAbout = view.findViewById(R.id.option_about);
        optionLogout = view.findViewById(R.id.option_logout);
        optiondelete = view.findViewById(R.id.option_delete_account);
    }

    private void setupOptions() {

        setupOption(optionPlaylists, R.drawable.ic_playlist, "My Playlists", R.color.icon_bg_playlists, v -> {
            Toast.makeText(getContext(), "Playlists clicked", Toast.LENGTH_SHORT).show();
        });
        setupOption(optionFavourites, R.drawable.ic_favorites, "Favourite Songs", R.color.icon_bg_favourites, v -> {
            if (getActivity() != null) {
                Fragment favouritesFragment = new FavouriteFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, favouritesFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        setupOption(optionSettings, R.drawable.ic_settings, "Settings", R.color.icon_bg_settings, v -> {
            Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });
        setupOption(optionAbout, R.drawable.ic_about, "About Us", R.color.icon_bg_about, v -> {
            startActivity(new Intent(getActivity(), AboutUsActivity.class));
        });

        setupOption(optiondelete, R.drawable.ic_delete_user, "delete account", R.color.icon_bg_settings, v -> {
            showDeleteAccountDialogue();
        });

        setupOption(optionLogout, R.drawable.ic_logout, "Logout", R.color.icon_bg_logout, v -> {
            showLogoutDialog();
        });
    }
    private void setupOption(View view, int iconResId, String text, int colorResId, View.OnClickListener listener) {
        if (view == null) return;

        ImageView icon = view.findViewById(R.id.icon);
        TextView optionText = view.findViewById(R.id.option_text);
        View iconBackground = view.findViewById(R.id.icon_background);

        icon.setImageResource(iconResId);
        optionText.setText(text);
        if (getContext() != null) {
            iconBackground.getBackground().setTint(ContextCompat.getColor(getContext(), colorResId));
        }

        view.setOnClickListener(listener);
    }

    private void setupEditProfileButton() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        Cursor cursor = dbHelper.getUserById(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EMAIL));
            String profileImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PROFILE_IMAGE_URI));

            tvUsername.setText(username);
            tvEmail.setText(email);

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(requireContext()).load(Uri.parse(profileImageUrl)).placeholder(R.drawable.ic_user).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_user);
            }
            cursor.close();
        } else {
            Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        if (getContext() == null) return;
        Context dialogContext = new ContextThemeWrapper(requireActivity(), R.style.Theme_Vibe_Dialog_Dark);
        new MaterialAlertDialogBuilder(dialogContext)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).signOut();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showDeleteAccountDialogue() {
        if (getContext() == null) return;
        Context dialogContext = new ContextThemeWrapper(requireActivity(), R.style.Theme_Vibe_Dialog_Dark);
        new MaterialAlertDialogBuilder(dialogContext)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (getActivity() instanceof MainActivity) {
                        Toast.makeText(requireContext(),"Account deleted",Toast.LENGTH_SHORT).show();
                        dbHelper.deleteUser(currentUserId);
                        sharedPreferences.edit().remove("userId").apply();
                        sharedPreferences.edit().putBoolean("isLoisLoggedIn", false).apply();
                        ((MainActivity) getActivity()).signOut();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
