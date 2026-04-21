package com.example.fitzone;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.transition.MaterialFadeThrough;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SELECTED_TAB = "selected_tab";
    private int selectedTabId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        if (savedInstanceState != null) {
            selectedTabId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.nav_home);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> switchToTab(item.getItemId()));

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            bottomNavigationView.setSelectedItemId(selectedTabId);
        }

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, selectedTabId);
    }

    private boolean switchToTab(int tabId) {
        Fragment fragment = createFragmentForTab(tabId);
        if (fragment == null) {
            return false;
        }

        selectedTabId = tabId;

        if (getSupportFragmentManager().isStateSaved()) {
            return true;
        }

        MaterialFadeThrough transition = new MaterialFadeThrough();
        transition.setDuration(220);
        fragment.setEnterTransition(transition);
        fragment.setReturnTransition(new MaterialFadeThrough());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out,
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out)
                .replace(R.id.mainFragmentContainer, fragment)
                .commit();
        return true;
    }

    private Fragment createFragmentForTab(int tabId) {
        if (tabId == R.id.nav_home) {
            return new HomeFragment();
        }
        if (tabId == R.id.nav_workout) {
            return new WorkoutFragment();
        }
        if (tabId == R.id.nav_diet) {
            return new DietFragment();
        }
        if (tabId == R.id.nav_chat) {
            return new ChatFragment();
        }
        if (tabId == R.id.nav_profile) {
            return new ProfileFragment();
        }
        return null;
    }

    /**
     * Handle back press navigation
     * If on HomeFragment, show exit dialog
     * Otherwise, go back to previous fragment
     */
    private void handleBackPress() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);

        // If current fragment is HomeFragment, show exit dialog
        if (currentFragment instanceof HomeFragment) {
            showExitDialog();
        } else {
            // Pop back stack or go to home
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                // Go back to home
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    /**
     * Show exit confirmation dialog
     */
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit FitZone?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Exit app
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Stay in app
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
}