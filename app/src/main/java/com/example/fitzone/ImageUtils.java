package com.example.fitzone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Utility class for generating demo images
 */
public class ImageUtils {

    /**
     * Generate a demo workout image
     */
    public static Bitmap generateWorkoutImage(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Background gradient simulation
        Paint paint = new Paint();
        paint.setColor(0xFF6200EE); // Primary color
        canvas.drawRect(0, 0, width, height, paint);
        
        // Draw text
        paint.setColor(Color.WHITE);
        paint.setTextSize(48f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("💪 Workout", width / 2, height / 2, paint);
        
        return bitmap;
    }

    /**
     * Generate a demo meal image
     */
    public static Bitmap generateMealImage(int width, int height, String mealType) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Background
        Paint paint = new Paint();
        paint.setColor(0xFFE8F5E9); // Light green
        canvas.drawRect(0, 0, width, height, paint);
        
        // Draw emoji based on meal type
        paint.setColor(Color.BLACK);
        paint.setTextSize(60f);
        paint.setTextAlign(Paint.Align.CENTER);
        
        String emoji = "🍽️";
        if (mealType != null) {
            if (mealType.contains("Breakfast")) emoji = "🥞";
            else if (mealType.contains("Lunch")) emoji = "🍗";
            else if (mealType.contains("Dinner")) emoji = "🍲";
        }
        
        canvas.drawText(emoji, width / 2, height / 2 + 20, paint);
        
        return bitmap;
    }

    /**
     * Generate a demo profile image
     */
    public static Bitmap generateProfileImage(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Background circle
        Paint paint = new Paint();
        paint.setColor(0xFF2196F3); // Blue
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        
        // Draw avatar emoji
        paint.setColor(Color.WHITE);
        paint.setTextSize(60f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("👤", width / 2, height / 2 + 20, paint);
        
        return bitmap;
    }

    /**
     * Generate a demo stats image
     */
    public static Bitmap generateStatsImage(int width, int height, String stat) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Background
        Paint paint = new Paint();
        int color = 0xFFFF9800; // Orange
        if (stat != null && stat.contains("steps")) color = 0xFF4CAF50; // Green
        if (stat != null && stat.contains("water")) color = 0xFF2196F3; // Blue
        
        paint.setColor(color);
        canvas.drawRect(0, 0, width, height, paint);
        
        // Draw emoji
        paint.setColor(Color.WHITE);
        paint.setTextSize(60f);
        paint.setTextAlign(Paint.Align.CENTER);
        
        String emoji = "📊";
        if (stat != null) {
            if (stat.contains("calories")) emoji = "🔥";
            else if (stat.contains("steps")) emoji = "👟";
            else if (stat.contains("water")) emoji = "💧";
        }
        
        canvas.drawText(emoji, width / 2, height / 2 + 20, paint);
        
        return bitmap;
    }
}

