package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TemperatureActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner spinnerFrom, spinnerTo;
    private TextView answer;
    private Button btnConvert, btnReturn, btnShare;

    private DatabaseHelper db;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme BEFORE super
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        // Apply background if theme has one
        ThemeManager.applyBackground(this);

        // Initialize views
        inputValue = findViewById(R.id.inputValue);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        answer = findViewById(R.id.answer);
        btnConvert = findViewById(R.id.btnConvert);
        btnReturn = findViewById(R.id.btnReturn);
        btnShare = findViewById(R.id.btnShare);

        db = new DatabaseHelper(this);

        // Get username
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    .getString("currentUser", "User");
        }

        // Set up spinner units
        String[] units = {"Celsius", "Fahrenheit", "Kelvin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Apply theme to buttons and text
        applyThemeToUI();

        // Button listeners
        btnConvert.setOnClickListener(v -> convertTemperature());
        btnReturn.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareResult());
    }

    private void applyThemeToUI() {
        TypedValue primaryValue = new TypedValue();
        TypedValue onPrimaryValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, onPrimaryValue, true);

        int primaryColor = primaryValue.data;
        int onPrimaryColor = onPrimaryValue.data != 0 ? onPrimaryValue.data : 0xFFFFFFFF; // fallback to white

        // Buttons
        Button[] buttons = {btnConvert, btnReturn, btnShare};
        for (Button btn : buttons) {
            if (btn != null) {
                btn.setBackgroundColor(primaryColor);
                btn.setTextColor(onPrimaryColor);
            }
        }

        // Answer TextView
        answer.setTextColor(primaryColor);

        // Input fields
        inputValue.setBackgroundColor(adjustAlpha(primaryColor, 0.1f));
        spinnerFrom.setBackgroundColor(adjustAlpha(primaryColor, 0.1f));
        spinnerTo.setBackgroundColor(adjustAlpha(primaryColor, 0.1f));
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(android.graphics.Color.alpha(color) * factor);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private void convertTemperature() {
        String inputText = inputValue.getText().toString().trim();
        if (inputText.isEmpty()) {
            answer.setText("Please enter a value");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(inputText);
        } catch (NumberFormatException e) {
            answer.setText("Invalid number");
            return;
        }

        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();

        double result = convertTempValue(value, fromUnit, toUnit);
        String resultText = value + " " + fromUnit + " = " + result + " " + toUnit;
        answer.setText(resultText);

        String date = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        HistoryManager history = new HistoryManager(this);
        history.addHistory("Temperature", inputText + " " + fromUnit, resultText, date);

    }

        private double convertTempValue(double value, String from, String to) {
        if (from.equals(to)) return value;

        double tempC;
        switch (from) {
            case "Fahrenheit": tempC = (value - 32) * 5/9; break;
            case "Kelvin": tempC = value - 273.15; break;
            default: tempC = value;
        }

        switch (to) {
            case "Fahrenheit": return tempC * 9/5 + 32;
            case "Kelvin": return tempC + 273.15;
            default: return tempC;
        }
    }

    private void shareResult() {
        String resultText = answer.getText().toString().trim();
        if (resultText.isEmpty() || resultText.equals("Result will appear here")) {
            resultText = "No result to share yet!";
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Temperature Conversion Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
