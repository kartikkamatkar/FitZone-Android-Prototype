package com.example.fitzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_AGE = "extra_age";
    public static final String EXTRA_HEIGHT = "extra_height";
    public static final String EXTRA_WEIGHT = "extra_weight";

    private TextInputLayout nameInputLayout;
    private TextInputLayout ageInputLayout;
    private TextInputLayout heightInputLayout;
    private TextInputLayout weightInputLayout;

    private TextInputEditText nameEditText;
    private TextInputEditText ageEditText;
    private TextInputEditText heightEditText;
    private TextInputEditText weightEditText;
    private ImageView profileAvatarPreview;

    private FitZoneDbHelper dbHelper;
    private long userId;

    private static final String PREFS_PROFILE = "fitzone_profile";
    private static final String KEY_PROFILE_AVATAR_URI = "profile_avatar_uri";

    private final ActivityResultLauncher<String[]> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }
                try {
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                } catch (SecurityException ignored) {
                    // Some providers do not grant persistable permission; best-effort only.
                }
                applyAvatarUri(uri);
                saveAvatarUri(uri.toString());
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new FitZoneDbHelper(this);
        bindViews();
        hydrateForm();

        findViewById(R.id.profileAvatarRow).setOnClickListener(v -> pickAvatar());
        findViewById(R.id.profileAvatarCameraButton).setOnClickListener(v -> pickAvatar());
        profileAvatarPreview.setOnClickListener(v -> pickAvatar());

        MaterialButton saveProfileButton = findViewById(R.id.saveProfileButton);
        saveProfileButton.setOnClickListener(v -> saveProfile());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void bindViews() {
        nameInputLayout = findViewById(R.id.editNameInputLayout);
        ageInputLayout = findViewById(R.id.editAgeInputLayout);
        heightInputLayout = findViewById(R.id.editHeightInputLayout);
        weightInputLayout = findViewById(R.id.editWeightInputLayout);

        nameEditText = findViewById(R.id.editNameEditText);
        ageEditText = findViewById(R.id.editAgeEditText);
        heightEditText = findViewById(R.id.editHeightEditText);
        weightEditText = findViewById(R.id.editWeightEditText);
        profileAvatarPreview = findViewById(R.id.profileAvatarPreview);
    }

    private void hydrateForm() {
        userId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
        if (userId <= 0L) {
            userId = dbHelper.getOrCreateDefaultUserId();
        }

        String name = getIntent().getStringExtra(EXTRA_NAME);
        int age = getIntent().getIntExtra(EXTRA_AGE, 0);
        float height = getIntent().getFloatExtra(EXTRA_HEIGHT, 0f);
        float weight = getIntent().getFloatExtra(EXTRA_WEIGHT, 0f);

        if (TextUtils.isEmpty(name) || age <= 0 || height <= 0f || weight <= 0f) {
            UserProfile profile = dbHelper.getUserProfileById(userId);
            if (profile != null) {
                name = profile.getName();
                age = profile.getAge();
                height = profile.getHeight();
                weight = profile.getWeight();
            }
        }

        nameEditText.setText(name == null ? "" : name);
        if (age > 0) {
            ageEditText.setText(String.valueOf(age));
        }
        if (height > 0f) {
            heightEditText.setText(String.format(Locale.US, "%.0f", height));
        }
        if (weight > 0f) {
            weightEditText.setText(String.format(Locale.US, "%.1f", weight));
        }

        loadSavedAvatar();
    }

    private void pickAvatar() {
        avatarPickerLauncher.launch(new String[]{"image/*"});
    }

    private void loadSavedAvatar() {
        String uriString = getAvatarUri();
        if (uriString == null || uriString.trim().isEmpty()) {
            profileAvatarPreview.setImageResource(R.drawable.profile_placeholder);
            return;
        }
        applyAvatarUri(Uri.parse(uriString));
    }

    private void applyAvatarUri(Uri uri) {
        try {
            profileAvatarPreview.setImageURI(uri);
            if (profileAvatarPreview.getDrawable() == null) {
                profileAvatarPreview.setImageResource(R.drawable.profile_placeholder);
            }
        } catch (Exception e) {
            profileAvatarPreview.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void saveAvatarUri(String uri) {
        SharedPreferences prefs = getSharedPreferences(PREFS_PROFILE, MODE_PRIVATE);
        prefs.edit().putString(KEY_PROFILE_AVATAR_URI, uri).apply();
        if (userId > 0L) {
            dbHelper.updateUserAvatar(userId, uri);
        }
    }

    private String getAvatarUri() {
        SharedPreferences prefs = getSharedPreferences(PREFS_PROFILE, MODE_PRIVATE);
        return prefs.getString(KEY_PROFILE_AVATAR_URI, "");
    }

    private void saveProfile() {
        clearErrors();

        String name = readText(nameEditText);
        String ageText = readText(ageEditText);
        String heightText = readText(heightEditText);
        String weightText = readText(weightEditText);

        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError(getString(R.string.error_name_required));
            valid = false;
        }

        Integer age = parseInt(ageText);
        if (age == null || age <= 0) {
            ageInputLayout.setError(getString(R.string.error_invalid_age));
            valid = false;
        }

        Float height = parseFloat(heightText);
        if (height == null || height <= 0f) {
            heightInputLayout.setError(getString(R.string.error_invalid_height));
            valid = false;
        }

        Float weight = parseFloat(weightText);
        if (weight == null || weight <= 0f) {
            weightInputLayout.setError(getString(R.string.error_invalid_weight));
            valid = false;
        }

        if (!valid || userId <= 0L) {
            return;
        }

        boolean updated = dbHelper.updateUserProfile(userId, name, age, height, weight);
        if (!updated) {
            Toast.makeText(this, R.string.profile_update_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.profile_update_success, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void clearErrors() {
        nameInputLayout.setError(null);
        ageInputLayout.setError(null);
        heightInputLayout.setError(null);
        weightInputLayout.setError(null);
    }

    private String readText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

