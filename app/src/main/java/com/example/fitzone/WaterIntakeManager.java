package com.example.fitzone;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Manages water intake tracking with SharedPreferences
 */
public class WaterIntakeManager {
    private static final String PREFS_NAME = "fitzone_water";
    private static final String KEY_WATER_INTAKE = "water_intake_";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int DAILY_GOAL = 8; // 8 glasses per day
    private static final int GLASS_SIZE = 250; // ml per glass

    private final SharedPreferences sharedPreferences;

    public WaterIntakeManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Add a glass of water to today's intake
     */
    public void addWater() {
        int currentIntake = getTodayWaterIntake();
        if (currentIntake < DAILY_GOAL) {
            saveTodayWaterIntake(currentIntake + 1);
        }
    }

    /**
     * Remove a glass of water from today's intake
     */
    public void removeWater() {
        int currentIntake = getTodayWaterIntake();
        if (currentIntake > 0) {
            saveTodayWaterIntake(currentIntake - 1);
        }
    }

    /**
     * Get today's water intake in glasses
     */
    public int getTodayWaterIntake() {
        String today = getTodayDate();
        return sharedPreferences.getInt(KEY_WATER_INTAKE + today, 0);
    }

    /**
     * Get water intake for a specific date
     */
    public int getWaterIntakeForDate(String date) {
        return sharedPreferences.getInt(KEY_WATER_INTAKE + date, 0);
    }

    /**
     * Save water intake for today
     */
    private void saveTodayWaterIntake(int glasses) {
        String today = getTodayDate();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_WATER_INTAKE + today, glasses);
        editor.apply();
    }

    /**
     * Get today's date as string
     */
    private String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Get daily goal
     */
    public int getDailyGoal() {
        return DAILY_GOAL;
    }

    /**
     * Get glass size in ml
     */
    public int getGlassSize() {
        return GLASS_SIZE;
    }

    /**
     * Get progress percentage (0-100)
     */
    public int getProgressPercentage() {
        int current = getTodayWaterIntake();
        return (current * 100) / DAILY_GOAL;
    }

    /**
     * Get total ml consumed today
     */
    public int getTotalMilliliters() {
        return getTodayWaterIntake() * GLASS_SIZE;
    }

    /**
     * Check if daily goal is reached
     */
    public boolean isDailyGoalReached() {
        return getTodayWaterIntake() >= DAILY_GOAL;
    }

    /**
     * Reset today's water intake
     */
    public void resetTodayIntake() {
        String today = getTodayDate();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_WATER_INTAKE + today);
        editor.apply();
    }

    /**
     * Manually set water intake
     */
    public void setWaterIntake(int glasses) {
        if (glasses >= 0 && glasses <= DAILY_GOAL) {
            saveTodayWaterIntake(glasses);
        }
    }
}

