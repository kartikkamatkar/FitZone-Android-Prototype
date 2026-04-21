package com.example.fitzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DietFragment extends Fragment {

    private MealAdapter mealAdapter;
    private RecyclerView mealsRecyclerView;
    private TextView dayLabel;
    private TextView totalCaloriesValue;
    private TextView totalProteinValue;
    private TextView totalCarbsValue;
    private TextView totalFatValue;

    private int currentDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        dayLabel = view.findViewById(R.id.dietDayLabel);
        totalCaloriesValue = view.findViewById(R.id.totalCaloriesValue);
        totalProteinValue = view.findViewById(R.id.totalProteinValue);
        totalCarbsValue = view.findViewById(R.id.totalCarbsValue);
        totalFatValue = view.findViewById(R.id.totalFatValue);

        mealsRecyclerView = view.findViewById(R.id.mealsRecyclerView);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mealsRecyclerView.setNestedScrollingEnabled(false);

        mealAdapter = new MealAdapter(DietPlanManager.getMealsForDay(currentDay));
        mealsRecyclerView.setAdapter(mealAdapter);

        ImageButton prevButton = view.findViewById(R.id.dietPrevDay);
        ImageButton nextButton = view.findViewById(R.id.dietNextDay);
        prevButton.setOnClickListener(v -> changePrevDay());
        nextButton.setOnClickListener(v -> changeNextDay());

        refreshDayUI();
        return view;
    }


    private void changePrevDay() {
        currentDay = DietPlanManager.prevDay(currentDay);
        refreshDayUI();
    }

    private void changeNextDay() {
        currentDay = DietPlanManager.nextDay(currentDay);
        refreshDayUI();
    }

    private void refreshDayUI() {
        List<MealItem> meals = DietPlanManager.getMealsForDay(currentDay);
        String dayName = DietPlanManager.getDayName(currentDay);

        if (dayLabel != null) dayLabel.setText(getString(R.string.diet_day_plan_label, dayName));

        if (mealAdapter != null) mealAdapter.updateMeals(meals);

        // Update totals dynamically
        if (totalCaloriesValue != null)
            totalCaloriesValue.setText(String.format(Locale.getDefault(), "%,d", DietPlanManager.getTotalCalories(meals)));
        if (totalProteinValue != null)
            totalProteinValue.setText(String.format(Locale.getDefault(), "%d g", DietPlanManager.getTotalProtein(meals)));
        if (totalCarbsValue != null)
            totalCarbsValue.setText(String.format(Locale.getDefault(), "%d g", DietPlanManager.getTotalCarbs(meals)));
        if (totalFatValue != null)
            totalFatValue.setText(String.format(Locale.getDefault(), "%d g", DietPlanManager.getTotalFat(meals)));
    }
}
