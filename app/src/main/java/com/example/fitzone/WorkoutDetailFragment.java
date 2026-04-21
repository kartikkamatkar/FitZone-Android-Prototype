package com.example.fitzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Sub-fragment displaying full exercise details for a specific workout category.
 */
public class WorkoutDetailFragment extends Fragment {

    public static final String ARG_CATEGORY = "category";

    private String category = "";

    // Simple immutable exercise detail model
    static class ExerciseDetail {
        final String name;
        final String sets;
        final String reps;
        final String muscleGroup;
        final String tip;

        ExerciseDetail(String name, String sets, String reps, String muscleGroup, String tip) {
            this.name = name;
            this.sets = sets;
            this.reps = reps;
            this.muscleGroup = muscleGroup;
            this.tip = tip;
        }
    }

    public static WorkoutDetailFragment newInstance(String category) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_detail, container, false);

        setupHeader(view);
        setupExerciseList(view, inflater);
        setupButtons(view);

        return view;
    }


    private void setupHeader(View view) {
        TextView titleText = view.findViewById(R.id.detailCategoryTitle);
        TextView statsText = view.findViewById(R.id.detailStatsText);

        List<ExerciseDetail> exercises = getExercises();
        titleText.setText(getString(R.string.workout_detail_title, category));
        statsText.setText(getString(
                R.string.workout_detail_stats,
                exercises.size(),
                getEstimatedMinutes(),
                getEstimatedCalories()));
    }

    private void setupExerciseList(View view, LayoutInflater inflater) {
        LinearLayout container = view.findViewById(R.id.exerciseListContainer);
        List<ExerciseDetail> exercises = getExercises();

        for (int i = 0; i < exercises.size(); i++) {
            ExerciseDetail ex = exercises.get(i);
            View card = inflater.inflate(R.layout.item_exercise_detail_card, container, false);

            ((TextView) card.findViewById(R.id.exerciseNumber)).setText(String.valueOf(i + 1));
            ((TextView) card.findViewById(R.id.exerciseName)).setText(ex.name);
            ((TextView) card.findViewById(R.id.exerciseSetsReps))
                    .setText(getString(R.string.workout_detail_sets_reps, ex.sets, ex.reps));
            ((TextView) card.findViewById(R.id.exerciseMuscles))
                    .setText(getString(R.string.workout_detail_muscles, "💪  " + ex.muscleGroup));
            ((TextView) card.findViewById(R.id.exerciseTip))
                    .setText(getString(R.string.workout_detail_tip, "💡  " + ex.tip));

            container.addView(card);
        }
    }

    private void setupButtons(View view) {
        // Back button
        view.findViewById(R.id.detailBackButton).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        // Start Workout button
        view.findViewById(R.id.detailStartButton).setOnClickListener(v -> {
            WorkoutTimerFragment timerFragment = new WorkoutTimerFragment();
            Bundle args = new Bundle();
            args.putString("workoutName", category + " Workout");
            args.putLong("duration", (long) getEstimatedMinutes() * 60 * 1000);
            timerFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, timerFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private int getEstimatedMinutes() {
        switch (category) {
            case "Chest": return 45;
            case "Back":  return 50;
            case "Legs":  return 55;
            case "Arms":  return 40;
            case "Abs":   return 25;
            case "Cardio": return 45;
            case "Recovery": return 40;
            default:      return 45;
        }
    }

    private int getEstimatedCalories() {
        switch (category) {
            case "Chest": return 400;
            case "Back":  return 420;
            case "Legs":  return 520;
            case "Arms":  return 350;
            case "Abs":   return 300;
            case "Cardio": return 480;
            case "Recovery": return 150;
            default:      return 400;
        }
    }

    private List<ExerciseDetail> getExercises() {
        List<ExerciseDetail> list = new ArrayList<>();
        switch (category) {
            case "Chest":
                list.add(new ExerciseDetail("Bench Press",       "4", "10", "Pectorals",         "Keep elbows at 45° from torso"));
                list.add(new ExerciseDetail("Incline DB Press",  "3", "12", "Upper Chest",        "Slow eccentric, squeeze at top"));
                list.add(new ExerciseDetail("Push-Ups",          "3", "15", "Chest & Triceps",    "Keep core tight throughout"));
                list.add(new ExerciseDetail("Cable Flyes",       "3", "12", "Inner Chest",        "Full stretch at the bottom"));
                list.add(new ExerciseDetail("Chest Dips",        "3", "10", "Lower Chest",        "Lean forward slightly"));
                break;
            case "Back":
                list.add(new ExerciseDetail("Pull-Ups",          "4", "8",  "Lats & Rhomboids",   "Full range of motion"));
                list.add(new ExerciseDetail("Bent-Over Row",     "4", "10", "Mid Back",           "Keep back flat, neutral spine"));
                list.add(new ExerciseDetail("Lat Pulldown",      "3", "12", "Lats",               "Pull to upper chest, not neck"));
                list.add(new ExerciseDetail("Deadlift",          "5", "5",  "Full Back",          "Keep bar close to legs"));
                list.add(new ExerciseDetail("Seated Cable Row",  "3", "12", "Mid Back",           "Retract shoulder blades fully"));
                break;
            case "Legs":
                list.add(new ExerciseDetail("Barbell Squats",    "5", "8",  "Quads & Glutes",     "Break parallel, chest up"));
                list.add(new ExerciseDetail("Leg Press",         "4", "12", "Quads",              "Full range, don't lock knees"));
                list.add(new ExerciseDetail("Romanian Deadlift", "4", "10", "Hamstrings",         "Feel the stretch, hinge at hips"));
                list.add(new ExerciseDetail("Leg Curls",         "3", "15", "Hamstrings",         "Control the negative"));
                list.add(new ExerciseDetail("Calf Raises",       "4", "20", "Calves",             "Pause and squeeze at top"));
                break;
            case "Arms":
                list.add(new ExerciseDetail("Bicep Curls",       "4", "12", "Biceps",             "Supinate the wrist at top"));
                list.add(new ExerciseDetail("Hammer Curls",      "3", "12", "Brachialis",         "Neutral grip throughout"));
                list.add(new ExerciseDetail("Tricep Dips",       "3", "15", "Triceps",            "Keep elbows close to body"));
                list.add(new ExerciseDetail("OH Tricep Extension","3","12", "Long Head Tricep",   "Keep elbows pointed up"));
                list.add(new ExerciseDetail("Preacher Curls",    "3", "10", "Biceps",             "Full extension at bottom"));
                break;
            case "Abs":
                list.add(new ExerciseDetail("Crunches",          "4", "20", "Rectus Abdominis",   "Don't pull on your neck"));
                list.add(new ExerciseDetail("Plank Hold",        "3", "60s","Core Stability",     "Keep hips level, breathe steadily"));
                list.add(new ExerciseDetail("Russian Twists",    "3", "20", "Obliques",           "Rotate from core, not arms"));
                list.add(new ExerciseDetail("Leg Raises",        "3", "15", "Lower Abs",          "Control the descent slowly"));
                list.add(new ExerciseDetail("Mountain Climbers", "3", "30s","Full Core",          "Keep hips down and level"));
                break;
            case "Cardio":
                list.add(new ExerciseDetail("Treadmill Run",     "1", "20min","Cardiovascular",   "Maintain 60–70% max heart rate"));
                list.add(new ExerciseDetail("Jump Rope",         "5", "2min", "Full Body",        "Keep elbows close to sides"));
                list.add(new ExerciseDetail("Cycling",           "1", "25min","Legs & Cardio",    "Moderate resistance, steady pace"));
                list.add(new ExerciseDetail("Burpees",           "4", "12",   "Full Body",        "Land softly, explode upward"));
                list.add(new ExerciseDetail("Box Jumps",         "4", "10",   "Explosive Power",  "Soft landing, absorb impact"));
                break;
            case "Recovery":
                list.add(new ExerciseDetail("Yoga Flow",         "1", "15min","Full Body Flex",   "Breathe deeply through each pose"));
                list.add(new ExerciseDetail("Foam Rolling",      "1", "10min","Muscle Recovery",  "Hold on tight spots 30–60s"));
                list.add(new ExerciseDetail("Static Stretching", "1", "15min","Flexibility",      "Hold each stretch 30 seconds"));
                list.add(new ExerciseDetail("Breathing Exercises","1","5min", "Relaxation",       "Diaphragmatic, belly breathing"));
                list.add(new ExerciseDetail("Light Walk",        "1", "30min","Active Recovery",  "Easy pace, enjoy the fresh air"));
                break;
            default:
                list.add(new ExerciseDetail("General Exercise",  "3", "15", "Full Body",          "Focus on form over weight"));
                break;
        }
        return list;
    }
}

