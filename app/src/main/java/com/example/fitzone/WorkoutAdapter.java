package com.example.fitzone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    public interface OnStartClickListener {
        void onStartClicked(WorkoutItem workoutItem);
    }

    private final List<WorkoutItem> items = new ArrayList<>();
    private final OnStartClickListener onStartClickListener;

    public WorkoutAdapter(OnStartClickListener onStartClickListener) {
        this.onStartClickListener = onStartClickListener;
    }

    public void submitList(List<WorkoutItem> workoutItems) {
        items.clear();
        items.addAll(workoutItems);
        notifyDataSetChanged();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_card, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        holder.bind(items.get(position), onStartClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        private final ImageView workoutImage;
        private final TextView workoutName;
        private final TextView workoutSets;
        private final TextView workoutReps;
        private final TextView workoutStatus;
        private final MaterialButton startButton;

        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutImage = itemView.findViewById(R.id.workoutImage);
            workoutName = itemView.findViewById(R.id.workoutName);
            workoutSets = itemView.findViewById(R.id.workoutSets);
            workoutReps = itemView.findViewById(R.id.workoutReps);
            workoutStatus = itemView.findViewById(R.id.workoutStatus);
            startButton = itemView.findViewById(R.id.startWorkoutButton);
        }

        void bind(WorkoutItem workoutItem, OnStartClickListener listener) {
            workoutImage.setImageResource(workoutItem.getImageResId());
            workoutName.setText(workoutItem.getName());
            workoutSets.setText(itemView.getContext().getString(
                    R.string.workout_sets_format,
                    workoutItem.getSets()));
            workoutReps.setText(itemView.getContext().getString(
                    R.string.workout_reps_format,
                    workoutItem.getReps()));

            if (workoutItem.isStarted()) {
                int minutes = workoutItem.getRemainingSeconds() / 60;
                int seconds = workoutItem.getRemainingSeconds() % 60;
                String formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds);
                // Show running time in the status text, not on the button
                workoutStatus.setText(itemView.getContext().getString(
                        R.string.workout_status_running,
                        formattedTime));
                // Keep button icon-only and slightly dim when running
                startButton.setText("");
                startButton.setAlpha(0.75f);
            } else {
                int minutes = workoutItem.getDurationSeconds() / 60;
                int seconds = workoutItem.getDurationSeconds() % 60;
                String formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds);
                // Show ready duration in the status text, button stays icon-only
                workoutStatus.setText(itemView.getContext().getString(
                        R.string.workout_status_ready_duration,
                        formattedTime));
                startButton.setText("");
                startButton.setAlpha(1f);
            }

            // Always keep button enabled so user can tap again to stop
            startButton.setEnabled(true);
            startButton.setOnClickListener(v -> listener.onStartClicked(workoutItem));
        }
    }
}

