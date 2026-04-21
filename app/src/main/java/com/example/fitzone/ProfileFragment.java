package com.example.fitzone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final String PREFS_PROFILE = "fitzone_profile";
    private static final String KEY_PROFILE_AVATAR_URI = "profile_avatar_uri";

    private FitZoneDbHelper dbHelper;
    private UserProfile currentProfile;
    private SessionManager sessionManager;
    private ProfilePictureManager profilePictureManager;

    private TextView profileNameText;
    private TextView profileAgeText;
    private TextView profileHeightText;
    private TextView profileWeightText;
    private ShapeableImageView profilePhoto;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadProfile();
                }
            });

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) {
                    return;
                }
                applyProfileAvatar(uri);
                saveAvatarUri(uri.toString());
                if (currentProfile != null) {
                    dbHelper.updateUserAvatar(currentProfile.getId(), uri.toString());
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    imagePickerLauncher.launch("image/*");
                } else {
                    Toast.makeText(requireContext(), R.string.error_permission_required, Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new FitZoneDbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        profilePictureManager = new ProfilePictureManager(requireContext());
        bindViews(view);
        bindActions(view);
        loadProfile();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void bindViews(View view) {
        profileNameText = view.findViewById(R.id.profileName);
        profileAgeText = view.findViewById(R.id.profileAgeValue);
        profileHeightText = view.findViewById(R.id.profileHeightValue);
        profileWeightText = view.findViewById(R.id.profileWeightValue);
        profilePhoto = view.findViewById(R.id.profilePhoto);
    }

    private void bindActions(View view) {
        view.findViewById(R.id.editProfileButton).setOnClickListener(v -> openEditProfile());
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> {
            // Clear login session
            sessionManager.logout();
            
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        profilePhoto.setOnClickListener(v -> pickProfileImage());
    }

    private void pickProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imagePickerLauncher.launch("image/*");
            return;
        }

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*");
            return;
        }
        permissionLauncher.launch(permission);
    }

    private void loadProfile() {
        currentProfile = dbHelper.getDefaultUserProfile();
        if (currentProfile == null) {
            Toast.makeText(requireContext(), R.string.profile_missing_data, Toast.LENGTH_SHORT).show();
            return;
        }

        profileNameText.setText(currentProfile.getName());
        profileAgeText.setText(getString(R.string.profile_age_value_format, currentProfile.getAge()));
        profileHeightText.setText(getString(
                R.string.profile_height_value_format,
                currentProfile.getHeight()));
        profileWeightText.setText(String.format(
                Locale.US,
                getString(R.string.profile_weight_value_format),
                currentProfile.getWeight()));

        loadSavedAvatar();
    }

    private void loadSavedAvatar() {
        String uriString = currentProfile != null ? dbHelper.getUserAvatar(currentProfile.getId()) : "";
        if (uriString == null || uriString.trim().isEmpty()) {
            uriString = getAvatarUri(); // backward compatibility with older local preference key
        }
        if (uriString == null || uriString.trim().isEmpty()) {
            profilePhoto.setImageResource(R.drawable.profile_placeholder);
            return;
        }

        try {
            applyProfileAvatar(Uri.parse(uriString));
        } catch (Exception e) {
            profilePhoto.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void applyProfileAvatar(Uri uri) {
        try {
            profilePhoto.setImageURI(uri);
            if (profilePhoto.getDrawable() == null) {
                profilePhoto.setImageResource(R.drawable.profile_placeholder);
            }
        } catch (Exception e) {
            profilePhoto.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void openEditProfile() {
        if (currentProfile == null) {
            Toast.makeText(requireContext(), R.string.profile_missing_data, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.EXTRA_USER_ID, currentProfile.getId());
        intent.putExtra(EditProfileActivity.EXTRA_NAME, currentProfile.getName());
        intent.putExtra(EditProfileActivity.EXTRA_AGE, currentProfile.getAge());
        intent.putExtra(EditProfileActivity.EXTRA_HEIGHT, currentProfile.getHeight());
        intent.putExtra(EditProfileActivity.EXTRA_WEIGHT, currentProfile.getWeight());
        editProfileLauncher.launch(intent);
    }

    private void saveAvatarUri(String uri) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_PROFILE, Activity.MODE_PRIVATE);
        prefs.edit().putString(KEY_PROFILE_AVATAR_URI, uri).apply();
    }

    private String getAvatarUri() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_PROFILE, Activity.MODE_PRIVATE);
        return prefs.getString(KEY_PROFILE_AVATAR_URI, "");
    }
}
