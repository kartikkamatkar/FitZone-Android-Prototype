package com.example.fitzone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Reusable header component for all fragments
 */
public class SharedHeaderView extends FrameLayout {

    private TextView titleText;
    private ImageView menuIcon;
    private ImageView profileIcon;

    public SharedHeaderView(@NonNull Context context) {
        super(context);
        init();
    }

    public SharedHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SharedHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_shared_header, this, true);
        titleText = findViewById(R.id.headerTitle);
        menuIcon = findViewById(R.id.headerMenuIcon);
        profileIcon = findViewById(R.id.headerProfileIcon);
    }

    /**
     * Set header title
     */
    public void setTitle(String title) {
        if (titleText != null) {
            titleText.setText(title);
        }
    }

    /**
     * Set menu icon click listener
     */
    public void setMenuClickListener(OnClickListener listener) {
        if (menuIcon != null) {
            menuIcon.setOnClickListener(listener);
        }
    }

    /**
     * Set profile icon click listener
     */
    public void setProfileClickListener(OnClickListener listener) {
        if (profileIcon != null) {
            profileIcon.setOnClickListener(listener);
        }
    }

    /**
     * Set profile avatar image
     */
    public void setProfileImage(int drawableRes) {
        if (profileIcon != null) {
            profileIcon.setImageResource(drawableRes);
        }
    }
}

