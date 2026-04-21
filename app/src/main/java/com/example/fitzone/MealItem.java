package com.example.fitzone;

/**
 * Represents a meal with nutritional information
 */
public class MealItem {
    private String mealType;  // Breakfast, Lunch, Dinner
    private String mealName;  // Protein oats + fruit, etc.
    private int calories;
    private int protein;
    private int carbs;
    private int fat;

    public MealItem(String mealType, String mealName, int calories, int protein, int carbs, int fat) {
        this.mealType = mealType;
        this.mealName = mealName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    // Getters
    public String getMealType() {
        return mealType;
    }

    public String getMealName() {
        return mealName;
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFat() {
        return fat;
    }

    // Setters
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    @Override
    public String toString() {
        return "MealItem{" +
                "mealType='" + mealType + '\'' +
                ", mealName='" + mealName + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", fat=" + fat +
                '}';
    }
}

