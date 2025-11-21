package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLength, btnWeight, btnTemperature, btnVolume, btnCurrency, btnHistory, btnThemes;
    private TextView textViewWelcome;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme BEFORE super.onCreate
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Apply background based on theme
        ThemeManager.applyBackground(this);

        // Initialize views
        textViewWelcome = findViewById(R.id.textViewWelcome);
        btnLength = findViewById(R.id.btnLength);
        btnWeight = findViewById(R.id.btnWeight);
        btnTemperature = findViewById(R.id.btnTemperature);
        btnVolume = findViewById(R.id.btnVolume);
        btnCurrency = findViewById(R.id.btnCurrency);
        btnHistory = findViewById(R.id.btnHistory);
        btnThemes = findViewById(R.id.btnThemes);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Remove login name, just welcome user normally
        if (textViewWelcome != null) {
            textViewWelcome.setText("Welcome!");
        }

        // Set click listeners
        btnLength.setOnClickListener(v -> openConversion("LengthActivity"));
        btnWeight.setOnClickListener(v -> openConversion("WeightActivity"));
        btnTemperature.setOnClickListener(v -> openConversion("TemperatureActivity"));
        btnVolume.setOnClickListener(v -> openConversion("VolumeActivity"));
        btnCurrency.setOnClickListener(v -> openConversion("CurrencyActivity"));
        btnHistory.setOnClickListener(v -> openConversion("History"));

        if (btnThemes != null) {
            btnThemes.setOnClickListener(v -> showThemeDialog());
        }

        // Logout button is now repurposed (optional)
        if (buttonLogout != null) {
            buttonLogout.setText("Exit App");
            buttonLogout.setOnClickListener(v -> finishAffinity());
        }

        // Apply theme colors
        applyThemeToButtons();
    }

    private void applyThemeToButtons() {
        // Resolve theme colors
        TypedValue primaryValue = new TypedValue();
        TypedValue onPrimaryValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, onPrimaryValue, true);

        int primaryColor = primaryValue.data;
        int onPrimaryColor = onPrimaryValue.data != 0 ? onPrimaryValue.data : 0xFFFFFFFF;

        Button[] buttons = {
                btnLength, btnWeight, btnTemperature, btnVolume,
                btnCurrency, btnHistory, btnThemes, buttonLogout
        };

        for (Button btn : buttons) {
            if (btn != null) {
                btn.setBackgroundColor(primaryColor);
                btn.setTextColor(onPrimaryColor);
            }
        }

        if (textViewWelcome != null) {
            textViewWelcome.setTextColor(primaryColor);
        }
    }

    private void openConversion(String activityName) {
        try {
            Class<?> activityClass = Class.forName("com.example.unitconversion." + activityName);
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showThemeDialog() {
        final String[] themes = {"Light", "Dark", "Background"};
        final int[] themeResIds = {
                R.style.AppTheme_Light,
                R.style.AppTheme_Dark,
                R.style.AppTheme_WithBackground
        };

        new AlertDialog.Builder(this)
                .setTitle("Select Theme")
                .setItems(themes, (dialog, which) -> ThemeManager.switchTheme(this, themeResIds[which]))
                .show();
    }
}
