package com.example.fitzone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment for creating and sharing fitness posts
 */
public class CreatePostFragment extends Fragment {

    private EditText postContentEdit;
    private EditText postTitleEdit;
    private Button shareButton;
    private Button cancelButton;
    private MaterialCardView previewCard;
    private FitZoneDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new FitZoneDbHelper(requireContext());
        bindViews(view);
        setupListeners();
    }


    private void bindViews(View view) {
        postTitleEdit = view.findViewById(R.id.postTitleEdit);
        postContentEdit = view.findViewById(R.id.postContentEdit);
        shareButton = view.findViewById(R.id.shareButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        previewCard = view.findViewById(R.id.previewCard);
    }

    private void setupListeners() {
        shareButton.setOnClickListener(v -> createPost());
        cancelButton.setOnClickListener(v -> goBack());
        
        // Update preview as user types
        postContentEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreview();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void createPost() {
        String title = postTitleEdit.getText().toString().trim();
        String content = postContentEdit.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(title)) {
            postTitleEdit.setError("Title required");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            postContentEdit.setError("Content required");
            return;
        }

        // Create post
        try {
            // Combine title and content for the post text
            String postText = title + "\n\n" + content;
            
            // Get current date in readable format
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());
            
            // Insert post to database
            long postId = dbHelper.insertPost(
                    1,                  // User ID (default user)
                    postText,           // Post text (title + content)
                    null,              // No image path
                    currentDate        // Current date
            );

            if (postId > 0) {
                Toast.makeText(requireContext(), "Post shared! 🎉", Toast.LENGTH_SHORT).show();
                goBack();
            } else {
                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePreview() {
        // Update preview in real-time
        String content = postContentEdit.getText().toString();
        if (!content.isEmpty()) {
            previewCard.setVisibility(View.VISIBLE);
        } else {
            previewCard.setVisibility(View.GONE);
        }
    }

    private void goBack() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}

