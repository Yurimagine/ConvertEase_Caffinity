package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LengthActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner spinnerFrom, spinnerTo;
    private TextView answer;
    private Button btnConvert, btnReturn, btnShare;

    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length);
        ThemeManager.applyBackground(this);

        inputValue = findViewById(R.id.inputValue);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        answer = findViewById(R.id.answer);
        btnConvert = findViewById(R.id.btnConvert);
        btnReturn = findViewById(R.id.btnReturn);
        btnShare = findViewById(R.id.btnShare);

        historyManager = new HistoryManager(this);

        String[] units = {"cm", "m", "km", "inch", "ft"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        btnConvert.setOnClickListener(v -> convertUnits());
        btnReturn.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareResult());

        applyThemeToUI();
    }

    private void applyThemeToUI() {
        TypedValue primaryValue = new TypedValue();
        TypedValue onPrimaryValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, onPrimaryValue, true);

        int primaryColor = primaryValue.data;
        int onPrimaryColor = onPrimaryValue.data;

        Button[] buttons = {btnConvert, btnReturn, btnShare};
        for (Button btn : buttons) {
            btn.setBackgroundColor(primaryColor);
            btn.setTextColor(onPrimaryColor);
        }

        answer.setTextColor(primaryColor);

        inputValue.setBackgroundColor(adjustAlpha(primaryColor, 0.1f));
        spinnerFrom.setBackgroundColor(adjustAlpha(primaryColor, 0.1f));
        spinnerTo.setBackgroundColor(adjustAlpha(primaryColor, 0.1f));
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(android.graphics.Color.alpha(color) * factor);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private void convertUnits() {
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

        double result = convertLength(value, fromUnit, toUnit);
        String resultText = value + " " + fromUnit + " = " + result + " " + toUnit;
        answer.setText(resultText);

        // Save to history
        saveHistory(value, fromUnit, result, toUnit);
    }

    private double convertLength(double value, String fromUnit, String toUnit) {
        double inMeters;

        switch (fromUnit) {
            case "cm": inMeters = value / 100; break;
            case "km": inMeters = value * 1000; break;
            case "inch": inMeters = value * 0.0254; break;
            case "ft": inMeters = value * 0.3048; break;
            default: inMeters = value;
        }

        switch (toUnit) {
            case "cm": return inMeters * 100;
            case "km": return inMeters / 1000;
            case "inch": return inMeters / 0.0254;
            case "ft": return inMeters / 0.3048;
            default: return inMeters;
        }
    }

    private void saveHistory(double inputValue, String inputUnit, double outputValue, String outputUnit) {
        String inputLabel = inputValue + " " + inputUnit;
        String outputLabel = outputValue + " " + outputUnit;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        historyManager.addHistory("Length", inputLabel, outputLabel, date);
    }

    private void shareResult() {
        String resultText = answer.getText().toString().trim();
        if (resultText.isEmpty() || resultText.equals("Result will appear here")) {
            resultText = "No result to share yet!";
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Unit Conversion Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
