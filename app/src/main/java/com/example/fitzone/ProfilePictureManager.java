package com.example.fitzone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Manages user profile picture storage and retrieval
 */
public class ProfilePictureManager {
    private static final String PREFS_NAME = "fitzone_profile";
    private static final String KEY_PROFILE_IMAGE = "profile_image_base64";

    private final SharedPreferences sharedPreferences;

    public ProfilePictureManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save profile picture as Base64 string
     */
    public void saveProfilePicture(Bitmap bitmap) {
        try {
            // Compress bitmap to reduce size
            Bitmap scaledBitmap = scaleImage(bitmap, 200, 200);
            
            // Convert to Base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_PROFILE_IMAGE, base64String);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get profile picture as Bitmap
     */
    public Bitmap getProfilePicture() {
        try {
            String base64String = sharedPreferences.getString(KEY_PROFILE_IMAGE, "");
            if (base64String.isEmpty()) {
                return null;
            }

            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if profile picture exists
     */
    public boolean hasProfilePicture() {
        String base64String = sharedPreferences.getString(KEY_PROFILE_IMAGE, "");
        return !base64String.isEmpty();
    }

    /**
     * Delete profile picture
     */
    public void deleteProfilePicture() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_PROFILE_IMAGE);
        editor.apply();
    }

    /**
     * Scale image to specified dimensions
     */
    private Bitmap scaleImage(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}

