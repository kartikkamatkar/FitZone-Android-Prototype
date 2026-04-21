package com.example.fitzone;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a daily workout plan with exercises and calories burned
 */
public class WorkoutPlan {
    private String dayName;
    private String workoutName;
    private List<String> exercises;
    private int caloriesBurned;

    public WorkoutPlan(String dayName, String workoutName, List<String> exercises, int caloriesBurned) {
        this.dayName = dayName;
        this.workoutName = workoutName;
        this.exercises = exercises;
        this.caloriesBurned = caloriesBurned;
    }

    // Getters
    public String getDayName() {
        return dayName;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public List<String> getExercises() {
        return exercises;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    // Setters
    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    /**
     * Get exercises as a formatted string
     */
    public String getExercisesString() {
        if (exercises == null || exercises.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < exercises.size(); i++) {
            sb.append("• ").append(exercises.get(i));
            if (i < exercises.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "WorkoutPlan{" +
                "dayName='" + dayName + '\'' +
                ", workoutName='" + workoutName + '\'' +
                ", exercises=" + exercises +
                ", caloriesBurned=" + caloriesBurned +
                '}';
    }
}

