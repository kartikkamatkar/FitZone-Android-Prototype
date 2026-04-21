package com.example.fitzone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePostActivity extends AppCompatActivity {

    private TextInputLayout postTextInputLayout;
    private TextInputEditText postTextEditText;
    private ImageView postImagePreview;
    private Uri selectedImageUri;
    private FitZoneDbHelper dbHelper;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) {
                    return;
                }
                selectedImageUri = uri;
                bindPostPreview(uri);
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    imagePickerLauncher.launch("image/*");
                } else {
                    Toast.makeText(this, R.string.error_permission_required, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        dbHelper = new FitZoneDbHelper(this);
        sessionManager = new SessionManager(this);

        postTextInputLayout = findViewById(R.id.postTextInputLayout);
        postTextEditText = findViewById(R.id.postTextEditText);
        postImagePreview = findViewById(R.id.postImagePreview);
        postImagePreview.setImageResource(R.drawable.exercise_placeholder);

        MaterialButton pickImageButton = findViewById(R.id.pickImageButton);
        MaterialButton publishPostButton = findViewById(R.id.publishPostButton);

        pickImageButton.setOnClickListener(v -> pickImageFromGallery());
        publishPostButton.setOnClickListener(v -> publishPost());
    }

    private void pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imagePickerLauncher.launch("image/*");
            return;
        }

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*");
            return;
        }
        permissionLauncher.launch(permission);
    }

    private void bindPostPreview(Uri uri) {
        try {
            postImagePreview.setImageURI(uri);
            if (postImagePreview.getDrawable() == null) {
                postImagePreview.setImageResource(R.drawable.exercise_placeholder);
            }
        } catch (Exception e) {
            postImagePreview.setImageResource(R.drawable.exercise_placeholder);
        }
        postImagePreview.setVisibility(View.VISIBLE);
    }

    private void publishPost() {
        postTextInputLayout.setError(null);

        String postText = readText(postTextEditText);
        if (TextUtils.isEmpty(postText)) {
            postTextInputLayout.setError(getString(R.string.post_error_text_required));
            return;
        }

        String userEmail = sessionManager.getUserEmail();
        long userId = dbHelper.getUserIdByEmail(userEmail);
        if (userId <= 0L) {
            userId = dbHelper.getOrCreateDefaultUserId();
        }
        if (userId == -1L) {
            Toast.makeText(this, R.string.post_publish_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = selectedImageUri != null ? selectedImageUri.toString() : "";
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());

        long rowId = dbHelper.insertPost(userId, postText, imagePath, date);
        if (rowId == -1L) {
            Toast.makeText(this, R.string.post_publish_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.post_publish_success, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private String readText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
