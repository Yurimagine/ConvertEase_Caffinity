package com.example.unitconversion;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseConversionActivity extends AppCompatActivity {

    protected TextView textViewWelcome;
    protected Button btnAction1, btnAction2, btnBack; // Customize for each activity
    protected SharedPreferences sharedPreferences;
    protected String username;

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_CURRENT_USER = "currentUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before anything else
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        // setContentView(...) --> Override in subclass with proper layout

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Get current username from intent or shared preferences
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = sharedPreferences.getString(KEY_CURRENT_USER, "User");
        }
    }

    /**
     * Apply semi-transparent theme color to buttons to blend with background
     */
    protected void applyButtonTheme(Button button) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        int primaryColor = typedValue.data;
        button.setBackgroundColor((primaryColor & 0x00FFFFFF) | 0x99000000); // 60% opacity
        button.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    /**
     * Call this after initializing buttons to automatically style them
     */
    protected void styleAllButtons(Button... buttons) {
        for (Button btn : buttons) {
            if (btn != null) applyButtonTheme(btn);
        }
    }

    /**
     * Set welcome message dynamically
     */
    protected void setWelcomeMessage(TextView welcomeTextView) {
        if (welcomeTextView != null) {
            welcomeTextView.setText("Hello, " + username + "!");
        }
    }
}

