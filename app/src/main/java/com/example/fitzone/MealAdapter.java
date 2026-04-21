package com.example.fitzone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter for displaying meal cards
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<MealItem> meals;

    public MealAdapter(List<MealItem> meals) {
        this.meals = meals;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealItem meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public void updateMeals(List<MealItem> newMeals) {
        this.meals = newMeals;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for meal cards
     */
    static class MealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealIcon;
        private final TextView mealTypeText;
        private final TextView mealNameText;
        private final TextView caloriesLabel;
        private final TextView caloriesValue;
        private final TextView proteinLabel;
        private final TextView proteinValue;
        private final TextView carbsLabel;
        private final TextView carbsValue;
        private final TextView fatLabel;
        private final TextView fatValue;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealIcon = itemView.findViewById(R.id.mealIcon);
            mealTypeText = itemView.findViewById(R.id.mealTypeText);
            mealNameText = itemView.findViewById(R.id.mealNameText);
            caloriesLabel = itemView.findViewById(R.id.caloriesLabel);
            caloriesValue = itemView.findViewById(R.id.caloriesValue);
            proteinLabel = itemView.findViewById(R.id.proteinLabel);
            proteinValue = itemView.findViewById(R.id.proteinValue);
            carbsLabel = itemView.findViewById(R.id.carbsLabel);
            carbsValue = itemView.findViewById(R.id.carbsValue);
            fatLabel = itemView.findViewById(R.id.fatLabel);
            fatValue = itemView.findViewById(R.id.fatValue);
        }

        public void bind(MealItem meal) {
            // Set meal type and name
            mealTypeText.setText(meal.getMealType());
            mealNameText.setText(meal.getMealName());

            // Set icon based on meal type
            int iconRes = getIconForMealType(meal.getMealType());
            mealIcon.setImageResource(iconRes);

            // Set nutritional values
            caloriesValue.setText(String.valueOf(meal.getCalories()));
            proteinValue.setText(meal.getProtein() + " g");
            carbsValue.setText(meal.getCarbs() + " g");
            fatValue.setText(meal.getFat() + " g");
        }

        private int getIconForMealType(String mealType) {
            switch (mealType) {
                case "Breakfast":
                    return android.R.drawable.ic_menu_info_details;
                case "Lunch":
                    return android.R.drawable.ic_menu_save;
                case "Dinner":
                    return android.R.drawable.ic_menu_view;
                default:
                    return android.R.drawable.ic_menu_compass;
            }
        }
    }
}

