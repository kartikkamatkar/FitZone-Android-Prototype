package com.example.fitzone;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment {
    private final List<WorkoutItem> allWorkouts = new ArrayList<>();
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private WorkoutAdapter workoutAdapter;
    private DashboardViewModel dashboardViewModel;
    private String categoryAll;
    private boolean timerRunning;
    private WorkoutItem activeWorkoutItem;
    private TextView completionPercentText;
    private LinearProgressIndicator completionProgress;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            boolean hasActiveWorkout = false;
            for (WorkoutItem item : allWorkouts) {
                if (!item.isStarted()) {
                    continue;
                }

                hasActiveWorkout = true;
                int remaining = item.getRemainingSeconds();
                if (remaining > 0) {
                    item.setRemainingSeconds(remaining - 1);
                }

                if (item.getRemainingSeconds() <= 0) {
                    item.setRemainingSeconds(0);
                    item.setStarted(false);
                }
            }

            if (workoutAdapter != null) {
                workoutAdapter.refresh();
            }
            if (hasActiveWorkout) {
                timerHandler.postDelayed(this, 1000L);
            } else {
                timerRunning = false;
                activeWorkoutItem = null;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        categoryAll = getString(R.string.workout_category_all);

        completionPercentText = view.findViewById(R.id.workoutCompletionPercent);
        completionProgress = view.findViewById(R.id.workoutCompletionProgress);

        setupRecyclerView(view);
        seedWorkouts();
        setupCategoryFilters(view);
        applyFilter(categoryAll);
        observeDashboardStats();

        return view;
    }


    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.workoutRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        workoutAdapter = new WorkoutAdapter(workoutItem -> {
            if (!startWorkout(workoutItem)) {
                return;
            }
            String message = getString(R.string.workout_started_format, workoutItem.getName());
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(workoutAdapter);
    }

    private void observeDashboardStats() {
        dashboardViewModel.getDashboardStats().observe(getViewLifecycleOwner(), stats -> {
            int progress = stats.getWorkoutProgressPercent();
            completionPercentText.setText(getString(R.string.workout_completion_percent_format, progress));
            completionProgress.setProgress(progress);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
        timerRunning = false;
    }

    private boolean startWorkout(WorkoutItem workoutItem) {
        if (activeWorkoutItem != null && activeWorkoutItem.isStarted() && activeWorkoutItem != workoutItem) {
            Toast.makeText(requireContext(), R.string.workout_single_active_warning, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (workoutItem.isStarted()) {
            // If this workout is already running and user taps again, stop it
            workoutItem.setStarted(false);
            workoutItem.setRemainingSeconds(0);
            activeWorkoutItem = null;
            if (workoutAdapter != null) {
                workoutAdapter.refresh();
            }

            // Stop dashboard session if it's running
            if (dashboardViewModel != null) {
                dashboardViewModel.stopWorkoutSession();
            }

            // Stop fragment-level timer if no other active workout
            timerHandler.removeCallbacks(timerRunnable);
            timerRunning = false;
            return true;
        }

        int duration = workoutItem.getDurationSeconds();
        workoutItem.setStarted(true);
        workoutItem.setRemainingSeconds(duration);
        activeWorkoutItem = workoutItem;
        workoutAdapter.refresh();

        dashboardViewModel.startWorkoutSession(duration);

        if (!timerRunning) {
            timerRunning = true;
            timerHandler.postDelayed(timerRunnable, 1000L);
        }
        return true;
    }

    private void setupCategoryFilters(View view) {
        Chip allChip = view.findViewById(R.id.chipAll);
        Chip chestChip = view.findViewById(R.id.chipChest);
        Chip absChip = view.findViewById(R.id.chipAbs);
        Chip legsChip = view.findViewById(R.id.chipLegs);
        Chip armsChip = view.findViewById(R.id.chipArms);
        Chip backChip = view.findViewById(R.id.chipBack);
        Chip cardioChip = view.findViewById(R.id.chipCardio);
        Chip recoveryChip = view.findViewById(R.id.chipRecovery);

        allChip.setOnClickListener(v -> applyFilter(categoryAll));
        chestChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_chest)));
        absChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_abs)));
        legsChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_legs)));
        armsChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_arms)));
        backChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_back)));
        if (cardioChip != null)
            cardioChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_cardio)));
        if (recoveryChip != null)
            recoveryChip.setOnClickListener(v -> openDetail(getString(R.string.workout_category_recovery)));
    }

    private void openDetail(String category) {
        WorkoutDetailFragment detail = WorkoutDetailFragment.newInstance(category);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, detail)
                .addToBackStack(null)
                .commit();
    }

    private void applyFilter(String category) {
        if (categoryAll.equals(category)) {
            workoutAdapter.submitList(allWorkouts);
            return;
        }

        List<WorkoutItem> filtered = new ArrayList<>();
        for (WorkoutItem workoutItem : allWorkouts) {
            if (workoutItem.getCategory().equals(category)) {
                filtered.add(workoutItem);
            }
        }
        workoutAdapter.submitList(filtered);
    }

    private void seedWorkouts() {
        if (!allWorkouts.isEmpty()) {
            return;
        }

        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_bench_press),
                4,
                10,
                18 * 60,
                getString(R.string.workout_category_chest)));
        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_push_ups),
                3,
                15,
                12 * 60,
                getString(R.string.workout_category_chest)));

        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_crunches),
                4,
                20,
                10 * 60,
                getString(R.string.workout_category_abs)));
        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_plank),
                3,
                60,
                8 * 60,
                getString(R.string.workout_category_abs)));

        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_squats),
                5,
                8,
                20 * 60,
                getString(R.string.workout_category_legs)));
        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_lunges),
                3,
                12,
                14 * 60,
                getString(R.string.workout_category_legs)));

        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_bicep_curls),
                4,
                12,
                12 * 60,
                getString(R.string.workout_category_arms)));
        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_tricep_dips),
                3,
                15,
                12 * 60,
                getString(R.string.workout_category_arms)));

        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_pull_ups),
                4,
                8,
                15 * 60,
                getString(R.string.workout_category_back)));
        allWorkouts.add(new WorkoutItem(
                R.drawable.exercise_placeholder,
                getString(R.string.workout_deadlift),
                5,
                5,
                22 * 60,
                getString(R.string.workout_category_back)));
    }
}

