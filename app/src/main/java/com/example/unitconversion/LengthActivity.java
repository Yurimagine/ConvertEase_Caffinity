package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LengthActivity extends AppCompatActivity {

    EditText inputValue;
    Spinner spinnerFrom, spinnerTo;
    TextView answer;
    Button btnConvert, btnReturn, btnShare; // 👈 Added btnShare

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length);

        inputValue = findViewById(R.id.inputValue);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        answer = findViewById(R.id.answer);
        btnConvert = findViewById(R.id.btnConvert);
        btnReturn = findViewById(R.id.btnReturn);
        btnShare = findViewById(R.id.btnShare);

        String[] units = {"cm", "m", "km", "inch", "ft"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertUnits();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // closes MainActivity and goes back to HomeScreen
            }
        });


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    private void convertUnits() {
        String inputText = inputValue.getText().toString().trim();

        if (inputText.isEmpty()) {
            answer.setText("Please enter a value");
            return;
        }

        double value = Double.parseDouble(inputText);
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();

        double result = convertLength(value, fromUnit, toUnit);
        answer.setText(value + " " + fromUnit + " = " + result + " " + toUnit);
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
}
