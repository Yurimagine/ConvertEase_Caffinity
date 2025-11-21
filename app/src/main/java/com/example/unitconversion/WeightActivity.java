package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class WeightActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_weight);

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

        // Setup units
        String[] units = {"kg", "g", "lb", "oz"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Apply theme to UI
        applyThemeToUI();

        // Button listeners
        btnConvert.setOnClickListener(v -> convertWeight());
        btnReturn.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareResult());
    }

    private void applyThemeToUI() {
        TypedValue primaryValue = new TypedValue();
        TypedValue onPrimaryValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, onPrimaryValue, true);

        int primaryColor = primaryValue.data;
        int onPrimaryColor = onPrimaryValue.data != 0 ? onPrimaryValue.data : 0xFFFFFFFF;

        // Buttons
        Button[] buttons = {btnConvert, btnReturn, btnShare};
        for (Button btn : buttons) {
            if (btn != null) {
                btn.setBackgroundColor(primaryColor);
                btn.setTextColor(onPrimaryColor);
            }
        }

        // TextViews
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

    private void convertWeight() {
        String inputText = inputValue.getText().toString().trim();
        if (inputText.isEmpty()) {
            answer.setText("Please enter a value");
            return;
        }

        double value;
        try { value = Double.parseDouble(inputText); }
        catch (NumberFormatException e) {
            answer.setText("Invalid number");
            return;
        }

        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();

        double result = convert(value, fromUnit, toUnit);
        String resultText = value + " " + fromUnit + " = " + result + " " + toUnit;
        answer.setText(resultText);

        String date = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        HistoryManager history = new HistoryManager(this);
        history.addHistory("Weight", inputText + " " + fromUnit, resultText, date);

    }

    private double convert(double value, String from, String to) {
        double inKg;
        switch (from) {
            case "g": inKg = value / 1000; break;
            case "lb": inKg = value * 0.453592; break;
            case "oz": inKg = value * 0.0283495; break;
            default: inKg = value;
        }

        switch (to) {
            case "g": return inKg * 1000;
            case "lb": return inKg / 0.453592;
            case "oz": return inKg / 0.0283495;
            default: return inKg;
        }
    }

    private void shareResult() {
        String resultText = answer.getText().toString().trim();
        if (resultText.isEmpty() || resultText.equals("Result will appear here")) {
            resultText = "No result to share yet!";
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Weight Conversion Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
