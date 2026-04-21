package com.example.fitzone;

public class WorkoutItem {

    private final int imageResId;
    private final String name;
    private final int sets;
    private final int reps;
    private final int durationSeconds;
    private final String category;
    private boolean started;
    private int remainingSeconds;

    public WorkoutItem(int imageResId, String name, int sets, int reps, int durationSeconds, String category) {
        this.imageResId = imageResId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.durationSeconds = durationSeconds;
        this.category = category;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getCategory() {
        return category;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}

