package com.example.fitzone;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Manages workout streak tracking with SharedPreferences
 */
public class WorkoutStreakManager {
    private static final String PREFS_NAME = "fitzone_streak";
    private static final String KEY_LAST_WORKOUT_DATE = "last_workout_date";
    private static final String KEY_CURRENT_STREAK = "current_streak";
    private static final String KEY_BEST_STREAK = "best_streak";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final SharedPreferences sharedPreferences;

    public WorkoutStreakManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Record a workout for today
     */
    public void recordWorkout() {
        String today = getTodayDate();
        String lastWorkoutDate = getLastWorkoutDate();

        int currentStreak = getCurrentStreak();
        int bestStreak = getBestStreak();

        // Check if workout already recorded today
        if (today.equals(lastWorkoutDate)) {
            return; // Already recorded today
        }

        // Check if yesterday had a workout (consecutive day)
        String yesterday = getYesterdayDate();
        if (yesterday.equals(lastWorkoutDate)) {
            // Consecutive day, increment streak
            currentStreak++;
        } else {
            // Not consecutive, reset to 1
            currentStreak = 1;
        }

        // Update best streak if current is higher
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
            saveBestStreak(bestStreak);
        }

        // Save the new streak and date
        saveCurrentStreak(currentStreak);
        saveLastWorkoutDate(today);
    }

    /**
     * Get current streak
     */
    public int getCurrentStreak() {
        return sharedPreferences.getInt(KEY_CURRENT_STREAK, 0);
    }

    /**
     * Get best streak
     */
    public int getBestStreak() {
        return sharedPreferences.getInt(KEY_BEST_STREAK, 0);
    }

    /**
     * Get last workout date
     */
    public String getLastWorkoutDate() {
        return sharedPreferences.getString(KEY_LAST_WORKOUT_DATE, "");
    }

    /**
     * Check if user worked out today
     */
    public boolean isWorkoutToday() {
        String today = getTodayDate();
        String lastWorkoutDate = getLastWorkoutDate();
        return today.equals(lastWorkoutDate);
    }

    /**
     * Check if streak is active (workout yesterday or today)
     */
    public boolean isStreakActive() {
        String today = getTodayDate();
        String yesterday = getYesterdayDate();
        String lastWorkoutDate = getLastWorkoutDate();

        return today.equals(lastWorkoutDate) || yesterday.equals(lastWorkoutDate);
    }

    /**
     * Get streak status message
     */
    public String getStreakMessage() {
        int streak = getCurrentStreak();
        if (streak == 0) {
            return "No streak yet. Start working out!";
        } else if (streak == 1) {
            return "Great start! 1 day streak";
        } else {
            return "Amazing! " + streak + " day streak";
        }
    }

    /**
     * Get days since last workout
     */
    public int getDaysSinceLastWorkout() {
        String lastDate = getLastWorkoutDate();
        if (lastDate.isEmpty()) {
            return Integer.MAX_VALUE;
        }

        try {
            LocalDate lastWorkout = LocalDate.parse(lastDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
            LocalDate today = LocalDate.now();
            return (int) ChronoUnit.DAYS.between(lastWorkout, today);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Get today's date as string
     */
    private String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Get yesterday's date as string
     */
    private String getYesterdayDate() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Save current streak
     */
    private void saveCurrentStreak(int streak) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CURRENT_STREAK, streak);
        editor.apply();
    }

    /**
     * Save best streak
     */
    private void saveBestStreak(int streak) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_BEST_STREAK, streak);
        editor.apply();
    }

    /**
     * Save last workout date
     */
    private void saveLastWorkoutDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_WORKOUT_DATE, date);
        editor.apply();
    }

    /**
     * Reset streak (for testing or if user chooses)
     */
    public void resetStreak() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_CURRENT_STREAK);
        editor.remove(KEY_LAST_WORKOUT_DATE);
        editor.apply();
    }

    /**
     * Check if streak will break if no workout today
     */
    public boolean willStreakBreakToday() {
        if (getCurrentStreak() == 0) {
            return false;
        }
        return !isWorkoutToday();
    }
}

