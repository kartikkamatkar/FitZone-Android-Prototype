package com.example.fitzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FitZoneDbHelper dbHelper;
    private PostFeedAdapter postFeedAdapter;
    private DashboardViewModel dashboardViewModel;
    private WaterIntakeManager waterIntakeManager;
    private WorkoutStreakManager workoutStreakManager;
    private SessionManager sessionManager;
    private long currentUserId = -1L;

    private TextView caloriesBurnedValue;
    private TextView caloriesLeftValue;
    private TextView bmiValue;
    private TextView workoutSubtitle;
    private TextView emptyFeedText;
    private TextView workoutTitleText;
    private TextView workoutExercisesText;
    private TextView workoutCaloriesText;
    private TextView waterIntakeLabel;
    private ProgressBar waterProgressBar;
    private MaterialButton addWaterButton;
    private TextView streakCounterText;
    private TextView streakMessageText;

    private final ActivityResultLauncher<Intent> createPostLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadPosts();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new FitZoneDbHelper(requireContext());
        waterIntakeManager = new WaterIntakeManager(requireContext());
        workoutStreakManager = new WorkoutStreakManager(requireContext());
        sessionManager = new SessionManager(requireContext());
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        resolveCurrentUserId();

        bindViews(view);
        setupFeedRecyclerView(view);
        setupInteractions(view);
        setupWaterIntakeTracker(view);
        observeDashboardStats();

        refreshDashboardStats();
        loadTodayWorkout();
        loadWaterIntake();
        loadWorkoutStreak();
        loadPosts();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshDashboardStats();
        loadTodayWorkout();
        loadWaterIntake();
        loadWorkoutStreak();
        loadPosts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void bindViews(View view) {
        caloriesBurnedValue = view.findViewById(R.id.homeCaloriesBurnedValue);
        caloriesLeftValue   = view.findViewById(R.id.homeCaloriesLeftValue);
        bmiValue            = view.findViewById(R.id.homeBmiValue);
        workoutSubtitle     = view.findViewById(R.id.workoutSubtitleText);
        workoutTitleText    = view.findViewById(R.id.workoutTitleText);
        workoutExercisesText = view.findViewById(R.id.workoutExercisesText);
        workoutCaloriesText = view.findViewById(R.id.workoutCaloriesText);
        emptyFeedText       = view.findViewById(R.id.homeEmptyFeedText);
        waterIntakeLabel    = view.findViewById(R.id.waterIntakeLabel);
        waterProgressBar    = view.findViewById(R.id.waterProgressBar);
        addWaterButton      = view.findViewById(R.id.addWaterButton);
        streakCounterText   = view.findViewById(R.id.streakCounterText);
        streakMessageText   = view.findViewById(R.id.streakMessageText);
    }

    private void setupFeedRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.homeFeedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);

        postFeedAdapter = new PostFeedAdapter(new PostFeedAdapter.OnPostActionListener() {
            @Override
            public void onLike(PostItem post) {
                handleLike(post);
            }

            @Override
            public void onComment(PostItem post) {
                showCommentDialog(post);
            }
        });
        recyclerView.setAdapter(postFeedAdapter);
    }

    private void setupInteractions(View view) {
        applyAnimatedClick(view.findViewById(R.id.homeHeaderAvatar), () -> openTab(R.id.nav_profile));
        applyAnimatedClick(view.findViewById(R.id.actionWorkoutsCard), () -> openTab(R.id.nav_workout));
        applyAnimatedClick(view.findViewById(R.id.actionDietCard), () -> openTab(R.id.nav_diet));
        applyAnimatedClick(view.findViewById(R.id.actionChatCard), () -> openTab(R.id.nav_chat));

        applyAnimatedClick(view.findViewById(R.id.bmiCard), this::openBmiCalculator);
        applyAnimatedClick(view.findViewById(R.id.actionTrackBmiCard), this::openBmiCalculator);

        applyAnimatedClick(view.findViewById(R.id.startWorkoutButton), () -> {
            workoutStreakManager.recordWorkout();
            loadWorkoutStreak();
            navigateToWorkoutTimer();
            Toast.makeText(requireContext(), R.string.workout_start, Toast.LENGTH_SHORT).show();
        });

        // Inline create-post card
        View createPostCard = view.findViewById(R.id.inlineCreatePostCard);
        if (createPostCard != null) {
            createPostCard.setOnClickListener(v -> launchCreatePostComposer());
        }

        View createPostFab = view.findViewById(R.id.createPostFab);
        if (createPostFab != null) {
            createPostFab.setOnClickListener(v -> launchCreatePostComposer());
        }
    }

    private void launchCreatePostComposer() {
        Intent intent = new Intent(requireContext(), CreatePostActivity.class);
        createPostLauncher.launch(intent);
    }

    private void observeDashboardStats() {
        dashboardViewModel.getDashboardStats().observe(getViewLifecycleOwner(), stats -> {
            caloriesBurnedValue.setText(getString(R.string.home_calories_dynamic_format, stats.getCaloriesBurned()));
            caloriesLeftValue.setText(getString(R.string.home_calories_dynamic_format, stats.getCaloriesLeft()));

            if (stats.getBmi() > 0f) {
                bmiValue.setText(String.format(Locale.US, "%.1f", stats.getBmi()));
            } else {
                bmiValue.setText(getString(R.string.home_bmi_unknown));
            }

            int minutes = Math.max(1, stats.getRemainingWorkoutSeconds() / 60);
            workoutSubtitle.setText(getString(
                    R.string.home_workout_dynamic_subtitle,
                    minutes,
                    stats.getWorkoutProgressPercent()));
        });
    }

    private void refreshDashboardStats() {
        UserProfile profile = dbHelper.getDefaultUserProfile();
        dashboardViewModel.refreshBaseStats(profile);
    }

    private void loadPosts() {
        try {
            List<PostItem> posts = dbHelper.getAllPosts(currentUserId);
            postFeedAdapter.submitList(posts);
            emptyFeedText.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            emptyFeedText.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), R.string.post_publish_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void resolveCurrentUserId() {
        String email = sessionManager != null ? sessionManager.getUserEmail() : "";
        long resolved = dbHelper.getUserIdByEmail(email);
        if (resolved <= 0L) {
            resolved = dbHelper.getOrCreateDefaultUserId();
        }
        currentUserId = resolved;
    }

    private void handleLike(PostItem post) {
        if (post == null || currentUserId <= 0L) {
            return;
        }
        dbHelper.togglePostLike(post.getId(), currentUserId);
        loadPosts();
    }

    private void showCommentDialog(PostItem post) {
        if (post == null || currentUserId <= 0L || !isAdded()) {
            return;
        }

        EditText input = new EditText(requireContext());
        input.setHint("Write a comment...");
        input.setMinLines(2);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Comment")
                .setView(input)
                .setPositiveButton("Post", (dialog, which) -> {
                    String text = input.getText() == null ? "" : input.getText().toString().trim();
                    if (text.isEmpty()) {
                        Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());
                    dbHelper.addPostComment(post.getId(), currentUserId, text, date);
                    loadPosts();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openBmiCalculator() {
        try {
            startActivity(new Intent(requireContext(), BmiCalculatorActivity.class));
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.error_action_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void openTab(int tabId) {
        if (!isAdded()) {
            return;
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);
        if (bottomNavigationView == null) {
            return;
        }
        bottomNavigationView.setSelectedItemId(tabId);
    }

    private void applyAnimatedClick(View view, Runnable action) {
        if (view == null) {
            return;
        }
        view.setClickable(true);
        view.setOnClickListener(v -> v.animate()
                .scaleX(0.97f)
                .scaleY(0.97f)
                .setDuration(70)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .withEndAction(action)
                        .start())
                .start());
    }

    /** Load and display today's workout plan */
    private void loadTodayWorkout() {
        try {
            WorkoutPlan todayWorkout = WorkoutManager.getTodayWorkout();
            if (todayWorkout != null) {
                workoutTitleText.setText(todayWorkout.getWorkoutName());
                if (workoutExercisesText != null) {
                    workoutExercisesText.setText(todayWorkout.getExercisesString());
                    workoutExercisesText.setVisibility(View.VISIBLE);
                }
                if (workoutCaloriesText != null) {
                    workoutCaloriesText.setText(
                            getString(R.string.home_workout_calories_burned, todayWorkout.getCaloriesBurned()));
                    workoutCaloriesText.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            workoutTitleText.setText(R.string.home_workout_title);
        }
    }

    private int calculateWorkoutDuration(WorkoutPlan workout) {
        int baseMinutes = 5;
        if (workout != null && workout.getExercises() != null) {
            baseMinutes += (workout.getExercises().size() * 3);
        }
        return Math.max(15, Math.min(baseMinutes, 90));
    }

    /**
     * Setup water intake tracker UI and interactions
     */
    private void setupWaterIntakeTracker(View view) {
        if (addWaterButton != null) {
            addWaterButton.setOnClickListener(v -> {
                waterIntakeManager.addWater();
                loadWaterIntake();
                Toast.makeText(requireContext(), "Glass of water added!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Load and display today's water intake
     */
    private void loadWaterIntake() {
        try {
            int currentIntake = waterIntakeManager.getTodayWaterIntake();
            int dailyGoal = waterIntakeManager.getDailyGoal();
            int progressPercentage = waterIntakeManager.getProgressPercentage();

            // Update label with current intake
            if (waterIntakeLabel != null) {
                waterIntakeLabel.setText(String.format("%d / %d glasses", currentIntake, dailyGoal));
            }

            // Update progress bar
            if (waterProgressBar != null) {
                waterProgressBar.setProgress(progressPercentage);
            }

            // Disable button if goal reached
            if (addWaterButton != null) {
                addWaterButton.setEnabled(!waterIntakeManager.isDailyGoalReached());
                if (waterIntakeManager.isDailyGoalReached()) {
                    addWaterButton.setAlpha(0.6f);
                } else {
                    addWaterButton.setAlpha(1f);
                }
            }
        } catch (Exception e) {
            // Handle error gracefully
            if (waterIntakeLabel != null) {
                waterIntakeLabel.setText("0 / 8 glasses");
            }
        }
    }

    /**
     * Load and display workout streak
     */
    private void loadWorkoutStreak() {
        try {
            int currentStreak = workoutStreakManager.getCurrentStreak();
            int bestStreak = workoutStreakManager.getBestStreak();
            
            // Update counter
            if (streakCounterText != null) {
                if (currentStreak == 0) {
                    streakCounterText.setText("0 days");
                } else if (currentStreak == 1) {
                    streakCounterText.setText("1 day");
                } else {
                    streakCounterText.setText(currentStreak + " days");
                }
            }
            
            // Update message
            if (streakMessageText != null) {
                String message = workoutStreakManager.getStreakMessage();
                if (bestStreak > 0 && currentStreak > 0) {
                    message += " (Best: " + bestStreak + " days)";
                }
                streakMessageText.setText(message);
            }
        } catch (Exception e) {
            // Handle error gracefully
            if (streakCounterText != null) {
                streakCounterText.setText("0 days");
            }
        }
    }


    /**
     * Navigate to workout timer fragment with stop button
     */
    private void navigateToWorkoutTimer() {
        try {
            WorkoutTimerFragment timerFragment = new WorkoutTimerFragment();
            
            // Pass workout details
            Bundle args = new Bundle();
            args.putString("workoutName", "Full Body Workout");
            args.putLong("duration", 30 * 60 * 1000); // 30 minutes
            timerFragment.setArguments(args);
            
            // Replace fragment with timer fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, timerFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error starting workout", Toast.LENGTH_SHORT).show();
        }
    }
}
