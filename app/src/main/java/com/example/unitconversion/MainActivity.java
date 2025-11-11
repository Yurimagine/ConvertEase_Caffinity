package com.example.unitconversion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLength, btnWeight, btnTemperature, btnVolume, btnCurrency;
    private TextView textViewWelcome;
    private Button buttonLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Check if user is logged in
        if (!isUserLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Display welcome message
        String currentUser = sharedPreferences.getString("currentUser", "User");
        textViewWelcome = findViewById(R.id.textViewWelcome);
        if (textViewWelcome != null) {
            textViewWelcome.setText("Welcome, " + currentUser + "!");
        }

        // Initialize buttons (ensure IDs match XML)
        btnLength = findViewById(R.id.btnLength);
        btnWeight = findViewById(R.id.btnWeight);
        btnTemperature = findViewById(R.id.btnTemperature);
        btnVolume = findViewById(R.id.btnVolume);
        btnCurrency = findViewById(R.id.btnCurrency);

        // Initialize logout button
        buttonLogout = findViewById(R.id.buttonLogout);
        if (buttonLogout != null) {
            buttonLogout.setOnClickListener(v -> performLogout());
        }

        // Set click listeners to launch activities
        btnLength.setOnClickListener(v -> startActivity(new Intent(this, LengthActivity.class)));
        btnWeight.setOnClickListener(v -> startActivity(new Intent(this, WeightActivity.class)));
        btnTemperature.setOnClickListener(v -> startActivity(new Intent(this, TemperatureActivity.class)));
        btnVolume.setOnClickListener(v -> startActivity(new Intent(this, VolumeActivity.class)));
        btnCurrency.setOnClickListener(v -> startActivity(new Intent(this, CurrencyActivity.class)));
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void performLogout() {
        // Clear login state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("currentUser");
        editor.apply();

        // Navigate to login
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}