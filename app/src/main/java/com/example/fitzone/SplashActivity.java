package com.example.fitzone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;

    private final Runnable navigateToNextScreen = () -> {
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        ImageView logoImage = findViewById(R.id.logoImage);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        logoImage.startAnimation(pulseAnimation);

        handler.postDelayed(navigateToNextScreen, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(navigateToNextScreen);
    }
}

