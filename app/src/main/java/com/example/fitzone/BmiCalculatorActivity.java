package com.example.fitzone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class BmiCalculatorActivity extends AppCompatActivity {

    private TextInputLayout heightInputLayout;
    private TextInputLayout weightInputLayout;
    private TextInputEditText heightEditText;
    private TextInputEditText weightEditText;
    private View resultCard;
    private TextView bmiValueText;
    private TextView bmiCategoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        heightInputLayout = findViewById(R.id.heightInputLayout);
        weightInputLayout = findViewById(R.id.weightInputLayout);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        resultCard = findViewById(R.id.bmiResultCard);
        bmiValueText = findViewById(R.id.bmiValueText);
        bmiCategoryText = findViewById(R.id.bmiCategoryText);

        MaterialButton calculateButton = findViewById(R.id.calculateBmiButton);
        calculateButton.setOnClickListener(v -> calculateBmi());
    }

    private void calculateBmi() {
        clearErrors();

        String heightText = readText(heightEditText);
        String weightText = readText(weightEditText);

        Float heightCm = parseFloat(heightText);
        Float weightKg = parseFloat(weightText);

        boolean valid = true;
        if (heightCm == null || heightCm <= 0) {
            heightInputLayout.setError(getString(R.string.error_invalid_height));
            valid = false;
        }

        if (weightKg == null || weightKg <= 0) {
            weightInputLayout.setError(getString(R.string.error_invalid_weight));
            valid = false;
        }

        if (!valid) {
            resultCard.setVisibility(View.GONE);
            return;
        }

        float heightMeters = heightCm / 100f;
        float bmi = weightKg / (heightMeters * heightMeters);
        String category = getBmiCategory(bmi);

        bmiValueText.setText(getString(R.string.bmi_result_format, String.format(Locale.US, "%.1f", bmi)));
        bmiCategoryText.setText(getString(R.string.bmi_category_format, category));
        resultCard.setVisibility(View.VISIBLE);
    }

    private void clearErrors() {
        heightInputLayout.setError(null);
        weightInputLayout.setError(null);
    }

    private String readText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private Float parseFloat(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getBmiCategory(float bmi) {
        if (bmi < 18.5f) {
            return getString(R.string.bmi_category_underweight);
        }
        if (bmi < 25f) {
            return getString(R.string.bmi_category_normal);
        }
        if (bmi < 30f) {
            return getString(R.string.bmi_category_overweight);
        }
        return getString(R.string.bmi_category_obese);
    }
}

