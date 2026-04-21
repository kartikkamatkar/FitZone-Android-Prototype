package com.example.fitzone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private FitZoneDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        dbHelper = new FitZoneDbHelper(this);

        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);
        TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText() == null ? "" : emailEditText.getText().toString().trim();
            String password = passwordEditText.getText() == null ? "" : passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password) || password.length() < 6) {
                Toast.makeText(this, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.validateUserCredentials(email, password)) {
                Toast.makeText(this, R.string.error_invalid_login_credentials, Toast.LENGTH_SHORT).show();
                return;
            }

            // Save login session
            sessionManager.saveLoginSession(email);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPasswordLink.setOnClickListener(v -> showResetPasswordDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void showResetPasswordDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        EditText emailInput = new EditText(this);
        emailInput.setHint(R.string.email_hint);
        emailInput.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        container.addView(emailInput);

        EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint(R.string.forgot_password_new_password_hint);
        newPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(newPasswordInput);

        EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setHint(R.string.forgot_password_confirm_password_hint);
        confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(confirmPasswordInput);

        new AlertDialog.Builder(this)
                .setTitle(R.string.forgot_password_title)
                .setView(container)
                .setPositiveButton(R.string.forgot_password_reset_button, (dialog, which) -> {
                    String email = emailInput.getText() == null ? "" : emailInput.getText().toString().trim();
                    String newPassword = newPasswordInput.getText() == null ? "" : newPasswordInput.getText().toString().trim();
                    String confirmPassword = confirmPasswordInput.getText() == null ? "" : confirmPasswordInput.getText().toString().trim();

                    if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                        Toast.makeText(this, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(this, R.string.forgot_password_error_mismatch, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!dbHelper.isUserRegistered(email)) {
                        Toast.makeText(this, R.string.forgot_password_error_email_not_found, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated = dbHelper.updateUserPasswordByEmail(email, newPassword);
                    if (updated) {
                        Toast.makeText(this, R.string.forgot_password_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.forgot_password_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}

