package com.example.fitzone;

public class DashboardStats {
    private final int caloriesBurned;
    private final int stepsTaken;
    private final int caloriesLeft;
    private final float bmi;
    private final int workoutProgressPercent;
    private final int remainingWorkoutSeconds;
    private final boolean workoutRunning;

    public DashboardStats(
            int caloriesBurned,
            int stepsTaken,
            int caloriesLeft,
            float bmi,
            int workoutProgressPercent,
            int remainingWorkoutSeconds,
            boolean workoutRunning
    ) {
        this.caloriesBurned = caloriesBurned;
        this.stepsTaken = stepsTaken;
        this.caloriesLeft = caloriesLeft;
        this.bmi = bmi;
        this.workoutProgressPercent = workoutProgressPercent;
        this.remainingWorkoutSeconds = remainingWorkoutSeconds;
        this.workoutRunning = workoutRunning;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public int getCaloriesLeft() {
        return caloriesLeft;
    }

    public float getBmi() {
        return bmi;
    }

    public int getWorkoutProgressPercent() {
        return workoutProgressPercent;
    }

    public int getRemainingWorkoutSeconds() {
        return remainingWorkoutSeconds;
    }

    public boolean isWorkoutRunning() {
        return workoutRunning;
    }

    public DashboardStats copy(
            Integer caloriesBurned,
            Integer stepsTaken,
            Integer caloriesLeft,
            Float bmi,
            Integer workoutProgressPercent,
            Integer remainingWorkoutSeconds,
            Boolean workoutRunning
    ) {
        return new DashboardStats(
                caloriesBurned != null ? caloriesBurned : this.caloriesBurned,
                stepsTaken != null ? stepsTaken : this.stepsTaken,
                caloriesLeft != null ? caloriesLeft : this.caloriesLeft,
                bmi != null ? bmi : this.bmi,
                workoutProgressPercent != null ? workoutProgressPercent : this.workoutProgressPercent,
                remainingWorkoutSeconds != null ? remainingWorkoutSeconds : this.remainingWorkoutSeconds,
                workoutRunning != null ? workoutRunning : this.workoutRunning
        );
    }
}

