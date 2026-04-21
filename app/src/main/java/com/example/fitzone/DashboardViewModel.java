package com.example.fitzone;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

public class DashboardViewModel extends ViewModel {

    private static final int DEFAULT_WORKOUT_SECONDS = 20 * 60;
    private final MutableLiveData<DashboardStats> dashboardStats = new MutableLiveData<>();
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private int elapsedWorkoutSeconds;
    private int totalWorkoutSeconds;
    private boolean workoutRunning;

    private final Runnable workoutTickRunnable = new Runnable() {
        @Override
        public void run() {
            DashboardStats current = dashboardStats.getValue();
            if (current == null || !workoutRunning) {
                return;
            }

            elapsedWorkoutSeconds++;
            int remaining = Math.max(0, totalWorkoutSeconds - elapsedWorkoutSeconds);
            int progress = totalWorkoutSeconds == 0
                    ? 0
                    : Math.min(100, (elapsedWorkoutSeconds * 100) / totalWorkoutSeconds);

            DashboardStats updated = current.copy(
                    current.getCaloriesBurned() + (2 + random.nextInt(3)),
                    current.getStepsTaken() + (18 + random.nextInt(20)),
                    Math.max(0, current.getCaloriesLeft() - (1 + random.nextInt(2))),
                    null,
                    progress,
                    remaining,
                    remaining > 0
            );
            dashboardStats.setValue(updated);

            if (remaining > 0) {
                timerHandler.postDelayed(this, 1000L);
            } else {
                workoutRunning = false;
            }
        }
    };

    public DashboardViewModel() {
        dashboardStats.setValue(new DashboardStats(560, 7800, 380, 0f, 0, 0, false));
    }

    public LiveData<DashboardStats> getDashboardStats() {
        return dashboardStats;
    }

    @MainThread
    public void refreshBaseStats(UserProfile profile) {
        DashboardStats current = dashboardStats.getValue();
        if (current == null) {
            return;
        }

        int caloriesBurned = workoutRunning
                ? current.getCaloriesBurned()
                : 450 + random.nextInt(280);
        int steps = workoutRunning
                ? current.getStepsTaken()
                : 5200 + random.nextInt(5200);
        int caloriesLeft = Math.max(120, 2200 - caloriesBurned - random.nextInt(250));

        float bmi = current.getBmi();
        if (profile != null && profile.getHeight() > 0f && profile.getWeight() > 0f) {
            float heightInMeters = profile.getHeight() / 100f;
            bmi = profile.getWeight() / (heightInMeters * heightInMeters);
        }

        dashboardStats.setValue(current.copy(
                caloriesBurned,
                steps,
                caloriesLeft,
                bmi,
                current.getWorkoutProgressPercent(),
                current.getRemainingWorkoutSeconds(),
                current.isWorkoutRunning()));
    }

    @MainThread
    public void startWorkoutSession() {
        startWorkoutSession(DEFAULT_WORKOUT_SECONDS);
    }

    @MainThread
    public void startWorkoutSession(int totalSeconds) {
        if (totalSeconds <= 0) {
            totalSeconds = DEFAULT_WORKOUT_SECONDS;
        }

        DashboardStats current = dashboardStats.getValue();
        if (current == null) {
            return;
        }

        if (workoutRunning) {
            return;
        }

        workoutRunning = true;
        elapsedWorkoutSeconds = 0;
        totalWorkoutSeconds = totalSeconds;

        dashboardStats.setValue(current.copy(
                null,
                null,
                null,
                null,
                0,
                totalWorkoutSeconds,
                true));

        timerHandler.removeCallbacks(workoutTickRunnable);
        timerHandler.postDelayed(workoutTickRunnable, 1000L);
    }


    @MainThread
    public void stopWorkoutSession() {
        DashboardStats current = dashboardStats.getValue();
        if (current == null) {
            return;
        }

        workoutRunning = false;
        timerHandler.removeCallbacks(workoutTickRunnable);

        // Reset workout-specific stats but keep others
        DashboardStats updated = current.copy(
                null,
                null,
                null,
                null,
                0,
                0,
                false
        );
        dashboardStats.setValue(updated);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        workoutRunning = false;
        timerHandler.removeCallbacks(workoutTickRunnable);
    }
}

