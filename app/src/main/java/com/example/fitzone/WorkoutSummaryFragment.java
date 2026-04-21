package com.example.fitzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment displaying workout summary and statistics after completion
 */
public class WorkoutSummaryFragment extends Fragment {

    private TextView workoutNameSummary;
    private TextView durationSummary;
    private TextView caloriesSummary;
    private TextView distanceSummary;
    private ProgressBar summaryProgressBar;
    private Button finishButton;
    private ImageView summaryIcon;
    private WorkoutStreakManager streakManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        streakManager = new WorkoutStreakManager(requireContext());
        bindViews(view);
        setupUI();
    }


    private void bindViews(View view) {
        workoutNameSummary = view.findViewById(R.id.workoutNameSummary);
        durationSummary = view.findViewById(R.id.durationSummary);
        caloriesSummary = view.findViewById(R.id.caloriesSummary);
        distanceSummary = view.findViewById(R.id.distanceSummary);
        summaryProgressBar = view.findViewById(R.id.summaryProgressBar);
        finishButton = view.findViewById(R.id.finishButton);
        summaryIcon = view.findViewById(R.id.summaryIcon);
    }

    private void setupUI() {
        // Get data from arguments if passed
        Bundle args = getArguments();
        String workoutName = "Full Body Workout";
        long durationMs = 30 * 60 * 1000; // 30 minutes

        if (args != null) {
            workoutName = args.getString("workoutName", "Full Body Workout");
            durationMs = args.getLong("duration", 30 * 60 * 1000);
        }

        // Display workout info
        workoutNameSummary.setText(workoutName);

        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        durationSummary.setText(String.format("%02d:%02d", minutes, seconds % 60));

        // Display estimated stats
        int caloriesBurned = (int) ((minutes / 30.0) * 350); // ~350 cal per 30 min
        caloriesSummary.setText(String.format("%d cal", caloriesBurned));

        float distance = (float) (minutes / 30.0 * 2.5); // ~2.5 km per 30 min
        distanceSummary.setText(String.format("%.1f km", distance));

        // Set progress to 100% (completed)
        summaryProgressBar.setProgress(100);

        // Set icon
        summaryIcon.setImageResource(android.R.drawable.ic_dialog_info);

        // Finish button
        finishButton.setOnClickListener(v -> finishWorkout());
    }

    private void finishWorkout() {
        // Show success message
        Toast.makeText(requireContext(), "Workout completed! Great job! 💪", Toast.LENGTH_SHORT).show();

        // Return to home
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
