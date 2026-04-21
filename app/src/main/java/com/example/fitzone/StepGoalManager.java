package com.example.fitzone;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Manages daily step goal tracking
 */
public class StepGoalManager {
    private static final String PREFS_NAME = "fitzone_steps";
    private static final String KEY_STEPS_TODAY = "steps_today_";
    private static final String KEY_STEP_GOAL = "step_goal";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int DEFAULT_STEP_GOAL = 10000;

    private final SharedPreferences sharedPreferences;

    public StepGoalManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get today's step count
     */
    public int getTodaySteps() {
        String today = getTodayDate();
        return sharedPreferences.getInt(KEY_STEPS_TODAY + today, 0);
    }

    /**
     * Set today's step count
     */
    public void setTodaySteps(int steps) {
        String today = getTodayDate();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_STEPS_TODAY + today, Math.max(0, steps));
        editor.apply();
    }

    /**
     * Add steps to today's count
     */
    public void addSteps(int steps) {
        int currentSteps = getTodaySteps();
        setTodaySteps(currentSteps + steps);
    }

    /**
     * Get daily step goal
     */
    public int getDailyGoal() {
        return sharedPreferences.getInt(KEY_STEP_GOAL, DEFAULT_STEP_GOAL);
    }

    /**
     * Set daily step goal
     */
    public void setDailyGoal(int goal) {
        if (goal > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_STEP_GOAL, goal);
            editor.apply();
        }
    }

    /**
     * Get progress percentage (0-100)
     */
    public int getProgressPercentage() {
        int current = getTodaySteps();
        int goal = getDailyGoal();
        if (goal <= 0) {
            return 0;
        }
        int percentage = (current * 100) / goal;
        return Math.min(percentage, 100); // Cap at 100%
    }

    /**
     * Check if daily goal is reached
     */
    public boolean isDailyGoalReached() {
        return getTodaySteps() >= getDailyGoal();
    }

    /**
     * Get steps remaining to reach goal
     */
    public int getStepsRemaining() {
        int current = getTodaySteps();
        int goal = getDailyGoal();
        int remaining = goal - current;
        return Math.max(0, remaining);
    }

    /**
     * Get step goal message
     */
    public String getGoalMessage() {
        if (isDailyGoalReached()) {
            return "Goal reached! 🎉";
        }
        int remaining = getStepsRemaining();
        return remaining + " steps to go";
    }

    /**
     * Get formatted progress text
     */
    public String getProgressText() {
        int current = getTodaySteps();
        int goal = getDailyGoal();
        return current + " / " + goal;
    }

    /**
     * Reset today's steps
     */
    public void resetToday() {
        String today = getTodayDate();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_STEPS_TODAY + today);
        editor.apply();
    }

    /**
     * Get today's date as string
     */
    private String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Get steps for a specific date
     */
    public int getStepsForDate(String date) {
        return sharedPreferences.getInt(KEY_STEPS_TODAY + date, 0);
    }
}

