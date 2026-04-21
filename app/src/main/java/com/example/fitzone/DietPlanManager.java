package com.example.fitzone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a 7-day rotating daily diet plan.
 * Plans rotate automatically based on the current day of the week.
 */
public class DietPlanManager {

    private static final Map<Integer, List<MealItem>> weeklyPlans = new HashMap<>();

    static {
        initializePlans();
    }

    private static void initializePlans() {

        // Sunday — Recovery Nutrition
        List<MealItem> sunday = new ArrayList<>();
        sunday.add(new MealItem("Breakfast", "Greek yogurt parfait + granola", 380, 22, 45, 10));
        sunday.add(new MealItem("Lunch", "Tuna salad sandwich + greens", 520, 35, 48, 15));
        sunday.add(new MealItem("Dinner", "Baked cod + sweet potato + steamed broccoli", 490, 38, 44, 11));
        weeklyPlans.put(Calendar.SUNDAY, sunday);

        // Monday — Muscle Building Day
        List<MealItem> monday = new ArrayList<>();
        monday.add(new MealItem("Breakfast", "Protein oats + mixed fruit", 420, 24, 48, 12));
        monday.add(new MealItem("Lunch", "Grilled chicken + quinoa salad", 610, 38, 56, 16));
        monday.add(new MealItem("Dinner", "Baked salmon + roasted vegetables", 540, 34, 42, 22));
        weeklyPlans.put(Calendar.MONDAY, monday);

        // Tuesday — Energy & Endurance Day
        List<MealItem> tuesday = new ArrayList<>();
        tuesday.add(new MealItem("Breakfast", "Banana smoothie bowl + seeds", 400, 18, 62, 8));
        tuesday.add(new MealItem("Lunch", "Turkey wrap + avocado + spinach", 580, 32, 52, 20));
        tuesday.add(new MealItem("Dinner", "Lean beef stir-fry + brown rice", 620, 36, 58, 18));
        weeklyPlans.put(Calendar.TUESDAY, tuesday);

        // Wednesday — High Protein Day
        List<MealItem> wednesday = new ArrayList<>();
        wednesday.add(new MealItem("Breakfast", "Egg white omelette + whole wheat toast", 350, 28, 20, 14));
        wednesday.add(new MealItem("Lunch", "Chicken breast + brown rice + broccoli", 590, 44, 54, 10));
        wednesday.add(new MealItem("Dinner", "Shrimp stir-fry + edamame + veggies", 480, 38, 36, 14));
        weeklyPlans.put(Calendar.WEDNESDAY, wednesday);

        // Thursday — Balanced Macros Day
        List<MealItem> thursday = new ArrayList<>();
        thursday.add(new MealItem("Breakfast", "Whole wheat toast + peanut butter + banana", 440, 20, 50, 16));
        thursday.add(new MealItem("Lunch", "Red lentil soup + multigrain bread", 540, 26, 72, 8));
        thursday.add(new MealItem("Dinner", "Grilled chicken + whole wheat pasta + marinara", 600, 40, 58, 14));
        weeklyPlans.put(Calendar.THURSDAY, thursday);

        // Friday — Carb Loading Day (pre-weekend training)
        List<MealItem> friday = new ArrayList<>();
        friday.add(new MealItem("Breakfast", "Whole grain pancakes + maple syrup + berries", 480, 16, 72, 14));
        friday.add(new MealItem("Lunch", "Pasta primavera + side salad", 560, 22, 78, 12));
        friday.add(new MealItem("Dinner", "Grilled fish tacos + corn tortillas + salsa", 580, 32, 64, 16));
        weeklyPlans.put(Calendar.FRIDAY, friday);

        // Saturday — Lean & Clean Day
        List<MealItem> saturday = new ArrayList<>();
        saturday.add(new MealItem("Breakfast", "Avocado egg toast + cherry tomatoes", 390, 22, 32, 18));
        saturday.add(new MealItem("Lunch", "Grilled fish + quinoa + cucumber salad", 550, 36, 50, 16));
        saturday.add(new MealItem("Dinner", "Tofu veggie bowl + brown rice + tahini", 480, 28, 56, 14));
        weeklyPlans.put(Calendar.SATURDAY, saturday);
    }

    /**
     * Get meal plan for today.
     */
    public static List<MealItem> getMealsForToday() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return getMealsForDay(dayOfWeek);
    }

    /**
     * Get meal plan for a specific day (Calendar.SUNDAY through Calendar.SATURDAY).
     */
    public static List<MealItem> getMealsForDay(int dayOfWeek) {
        List<MealItem> plan = weeklyPlans.get(dayOfWeek);
        if (plan == null) {
            return weeklyPlans.get(Calendar.MONDAY);
        }
        return plan;
    }

    /**
     * Returns the display name for a Calendar day-of-week constant.
     */
    public static String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:    return "Monday";
            case Calendar.TUESDAY:   return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY:  return "Thursday";
            case Calendar.FRIDAY:    return "Friday";
            case Calendar.SATURDAY:  return "Saturday";
            case Calendar.SUNDAY:    return "Sunday";
            default:                 return "Today";
        }
    }

    /**
     * Returns the total calories for all meals in a plan.
     */
    public static int getTotalCalories(List<MealItem> meals) {
        int total = 0;
        for (MealItem m : meals) total += m.getCalories();
        return total;
    }

    /**
     * Returns the total protein for all meals in a plan.
     */
    public static int getTotalProtein(List<MealItem> meals) {
        int total = 0;
        for (MealItem m : meals) total += m.getProtein();
        return total;
    }

    /**
     * Returns the total carbs for all meals in a plan.
     */
    public static int getTotalCarbs(List<MealItem> meals) {
        int total = 0;
        for (MealItem m : meals) total += m.getCarbs();
        return total;
    }

    /**
     * Returns the total fat for all meals in a plan.
     */
    public static int getTotalFat(List<MealItem> meals) {
        int total = 0;
        for (MealItem m : meals) total += m.getFat();
        return total;
    }

    /**
     * Returns the next day of week (wraps around).
     */
    public static int nextDay(int dayOfWeek) {
        return (dayOfWeek % 7) + 1;
    }

    /**
     * Returns the previous day of week (wraps around).
     */
    public static int prevDay(int dayOfWeek) {
        return (dayOfWeek - 2 + 7) % 7 + 1;
    }
}

