package com.example.fitzone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages daily workout plans based on the day of the week
 */
public class WorkoutManager {

    private static final Map<Integer, WorkoutPlan> weeklyWorkouts = new HashMap<>();

    static {
        initializeWorkoutPlans();
    }

    /**
     * Initialize all weekly workout plans
     */
    private static void initializeWorkoutPlans() {
        // Monday - Chest + Triceps
        weeklyWorkouts.put(Calendar.MONDAY, new WorkoutPlan(
                "Monday",
                "Chest + Triceps",
                getChestTricepsExercises(),
                450
        ));

        // Tuesday - Back + Biceps
        weeklyWorkouts.put(Calendar.TUESDAY, new WorkoutPlan(
                "Tuesday",
                "Back + Biceps",
                getBackBicepsExercises(),
                420
        ));

        // Wednesday - Legs
        weeklyWorkouts.put(Calendar.WEDNESDAY, new WorkoutPlan(
                "Wednesday",
                "Legs",
                getLegsExercises(),
                520
        ));

        // Thursday - Core
        weeklyWorkouts.put(Calendar.THURSDAY, new WorkoutPlan(
                "Thursday",
                "Core",
                getCoreExercises(),
                300
        ));

        // Friday - Full Body
        weeklyWorkouts.put(Calendar.FRIDAY, new WorkoutPlan(
                "Friday",
                "Full Body",
                getFullBodyExercises(),
                550
        ));

        // Saturday - Cardio + Core
        weeklyWorkouts.put(Calendar.SATURDAY, new WorkoutPlan(
                "Saturday",
                "Cardio + Core",
                getCardioExercises(),
                480
        ));

        // Sunday - Stretching & Recovery
        weeklyWorkouts.put(Calendar.SUNDAY, new WorkoutPlan(
                "Sunday",
                "Stretching & Recovery",
                getRecoveryExercises(),
                150
        ));
    }

    /**
     * Get today's workout plan
     */
    public static WorkoutPlan getTodayWorkout() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return weeklyWorkouts.get(dayOfWeek);
    }

    /**
     * Get workout for a specific day
     */
    public static WorkoutPlan getWorkoutForDay(int dayOfWeek) {
        return weeklyWorkouts.get(dayOfWeek);
    }

    /**
     * Get current day name
     */
    public static String getTodayName() {
        Calendar calendar = Calendar.getInstance();
        String[] dayNames = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
    }

    // Exercise lists for each workout
    private static List<String> getChestTricepsExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Bench Press - 4 sets x 8 reps");
        exercises.add("Incline Dumbbell Press - 3 sets x 10 reps");
        exercises.add("Chest Flyes - 3 sets x 12 reps");
        exercises.add("Dips - 3 sets x 8 reps");
        exercises.add("Tricep Pushdown - 3 sets x 12 reps");
        exercises.add("Overhead Tricep Extension - 3 sets x 10 reps");
        return exercises;
    }

    private static List<String> getBackBicepsExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Deadlifts - 4 sets x 6 reps");
        exercises.add("Bent-over Rows - 4 sets x 8 reps");
        exercises.add("Pull-ups - 3 sets x 8 reps");
        exercises.add("Lat Pulldown - 3 sets x 10 reps");
        exercises.add("Barbell Curls - 3 sets x 8 reps");
        exercises.add("Dumbbell Curls - 3 sets x 10 reps");
        exercises.add("Hammer Curls - 3 sets x 12 reps");
        return exercises;
    }

    private static List<String> getLegsExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Squats - 4 sets x 8 reps");
        exercises.add("Romanian Deadlifts - 3 sets x 8 reps");
        exercises.add("Leg Press - 3 sets x 10 reps");
        exercises.add("Leg Curls - 3 sets x 12 reps");
        exercises.add("Leg Extensions - 3 sets x 12 reps");
        exercises.add("Calf Raises - 3 sets x 15 reps");
        return exercises;
    }

    private static List<String> getCoreExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Planks - 3 sets x 60 seconds");
        exercises.add("Ab Crunches - 3 sets x 15 reps");
        exercises.add("Leg Raises - 3 sets x 12 reps");
        exercises.add("Russian Twists - 3 sets x 20 reps");
        exercises.add("Mountain Climbers - 3 sets x 30 seconds");
        exercises.add("Cable Crunches - 3 sets x 12 reps");
        return exercises;
    }

    private static List<String> getFullBodyExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Squats - 3 sets x 8 reps");
        exercises.add("Bench Press - 3 sets x 8 reps");
        exercises.add("Rows - 3 sets x 8 reps");
        exercises.add("Shoulder Press - 3 sets x 10 reps");
        exercises.add("Deadlifts - 2 sets x 5 reps");
        exercises.add("Pull-ups - 3 sets x 6 reps");
        return exercises;
    }

    private static List<String> getCardioExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Treadmill Running - 20 minutes");
        exercises.add("Cycling - 15 minutes");
        exercises.add("Jumping Jacks - 3 sets x 30 seconds");
        exercises.add("Burpees - 3 sets x 10 reps");
        exercises.add("Jump Rope - 3 sets x 1 minute");
        exercises.add("Plank Core Work - 3 sets x 45 seconds");
        return exercises;
    }

    private static List<String> getRecoveryExercises() {
        List<String> exercises = new ArrayList<>();
        exercises.add("Full Body Stretching - 10 minutes");
        exercises.add("Yoga Flow - 15 minutes");
        exercises.add("Foam Rolling - 10 minutes");
        exercises.add("Deep Breathing Exercises - 5 minutes");
        exercises.add("Light Walking - 15 minutes");
        exercises.add("Meditation - 5 minutes");
        return exercises;
    }
}

