package com.example.fitzone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout ageInputLayout;
    private TextInputLayout heightInputLayout;
    private TextInputLayout weightInputLayout;

    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText ageEditText;
    private TextInputEditText heightEditText;
    private TextInputEditText weightEditText;

    private FitZoneDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new FitZoneDbHelper(this);
        bindViews();

        MaterialButton registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> registerUser());

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> openLogin());
    }

    private void bindViews() {
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        ageInputLayout = findViewById(R.id.ageInputLayout);
        heightInputLayout = findViewById(R.id.heightInputLayout);
        weightInputLayout = findViewById(R.id.weightInputLayout);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ageEditText = findViewById(R.id.ageEditText);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
    }

    private void registerUser() {
        clearErrors();

        String name = readText(nameEditText);
        String email = readText(emailEditText);
        String password = readText(passwordEditText);
        String ageText = readText(ageEditText);
        String heightText = readText(heightEditText);
        String weightText = readText(weightEditText);

        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError(getString(R.string.error_name_required));
            valid = false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInputLayout.setError(getString(R.string.error_invalid_password));
            valid = false;
        }

        Integer age = parseInt(ageText);
        if (age == null || age <= 0) {
            ageInputLayout.setError(getString(R.string.error_invalid_age));
            valid = false;
        }

        Float height = parseFloat(heightText);
        if (height == null || height <= 0) {
            heightInputLayout.setError(getString(R.string.error_invalid_height));
            valid = false;
        }

        Float weight = parseFloat(weightText);
        if (weight == null || weight <= 0) {
            weightInputLayout.setError(getString(R.string.error_invalid_weight));
            valid = false;
        }

        if (!valid) {
            return;
        }

        long rowId = dbHelper.insertUser(name, email, password, age, height, weight);
        if (rowId == -1L) {
            emailInputLayout.setError(getString(R.string.error_email_exists));
            Toast.makeText(this, R.string.register_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
        openLogin();
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void clearErrors() {
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        ageInputLayout.setError(null);
        heightInputLayout.setError(null);
        weightInputLayout.setError(null);
    }

    private String readText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

