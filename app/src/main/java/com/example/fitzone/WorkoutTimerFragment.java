package com.example.fitzone;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment for displaying workout timer with stop functionality
 */
public class WorkoutTimerFragment extends Fragment {

    private TextView timerDisplay;
    private TextView workoutNameDisplay;
    private ProgressBar workoutProgressBar;
    private Button stopButton;
    private Button pauseButton;
    private View createPostButton;
    private CountDownTimer countDownTimer;
    private long totalTimeMs = 30 * 60 * 1000; // Default 30 minutes
    private long remainingTimeMs;
    private boolean isRunning = false;
    private String workoutName = "Full Body Workout";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get arguments if passed
        if (getArguments() != null) {
            workoutName = getArguments().getString("workoutName", "Full Body Workout");
            totalTimeMs = getArguments().getLong("duration", 30 * 60 * 1000);
        }

        bindViews(view);
        setupUI();
        startTimer();
    }


    private void bindViews(View view) {
        timerDisplay = view.findViewById(R.id.timerDisplay);
        workoutNameDisplay = view.findViewById(R.id.workoutNameDisplay);
        workoutProgressBar = view.findViewById(R.id.workoutProgressBar);
        stopButton = view.findViewById(R.id.stopButton);
        pauseButton = view.findViewById(R.id.pauseButton);
        createPostButton = view.findViewById(R.id.timerCreatePostButton);
    }

    private void setupUI() {
        workoutNameDisplay.setText(workoutName);
        remainingTimeMs = totalTimeMs;
        updateTimerDisplay();

        stopButton.setOnClickListener(v -> stopWorkout());
        pauseButton.setOnClickListener(v -> togglePause());
        if (createPostButton != null) {
            createPostButton.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), CreatePostActivity.class)));
        }
    }

    private void startTimer() {
        isRunning = true;
        pauseButton.setText("Pause");

        countDownTimer = new CountDownTimer(remainingTimeMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMs = millisUntilFinished;
                updateTimerDisplay();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                remainingTimeMs = 0;
                updateTimerDisplay();
                updateProgressBar();
                onWorkoutComplete();
            }
        }.start();
    }

    private void togglePause() {
        if (isRunning) {
            pauseWorkout();
        } else {
            resumeWorkout();
        }
    }

    private void pauseWorkout() {
        isRunning = false;
        countDownTimer.cancel();
        pauseButton.setText("Resume");
        Toast.makeText(requireContext(), "Workout Paused", Toast.LENGTH_SHORT).show();
    }

    private void resumeWorkout() {
        isRunning = true;
        pauseButton.setText("Pause");
        startTimer();
    }

    private void stopWorkout() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;

        // Show completion dialog
        Toast.makeText(requireContext(), "Workout Stopped", Toast.LENGTH_SHORT).show();

        // Open summary so user can optionally create a post from completion flow.
        navigateToWorkoutSummary();
    }

    private void onWorkoutComplete() {
        Toast.makeText(requireContext(), "Workout Complete! 🎉", Toast.LENGTH_LONG).show();

        // Navigate to workout summary fragment
        navigateToWorkoutSummary();
    }

    /**
     * Navigate to workout summary fragment
     */
    private void navigateToWorkoutSummary() {
        try {
            WorkoutSummaryFragment summaryFragment = new WorkoutSummaryFragment();

            // Pass workout details
            Bundle args = new Bundle();
            args.putString("workoutName", workoutName);
            args.putLong("duration", totalTimeMs);
            summaryFragment.setArguments(args);

            // Replace fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, summaryFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTimerDisplay() {
        long seconds = remainingTimeMs / 1000;
        long minutes = seconds / 60;
        long secs = seconds % 60;
        timerDisplay.setText(String.format("%02d:%02d", minutes, secs));
    }

    private void updateProgressBar() {
        int progress = (int) (100 - ((remainingTimeMs * 100) / totalTimeMs));
        workoutProgressBar.setProgress(progress);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
