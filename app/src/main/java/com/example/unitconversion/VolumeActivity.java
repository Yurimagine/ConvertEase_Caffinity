package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unitconversion.utils.ConversionUtils;
import com.example.unitconversion.utils.SharedPrefHelper;
import java.util.ArrayList;
import java.util.List;

public class VolumeActivity extends AppCompatActivity {

    EditText inputValue;
    Spinner spinnerFrom, spinnerTo;
    TextView answer;
    Button btnConvert, btnReturn, btnShare;
    SharedPrefHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        inputValue = findViewById(R.id.inputValue);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        answer = findViewById(R.id.answer);
        btnConvert = findViewById(R.id.btnConvert);
        btnReturn = findViewById(R.id.btnReturn);
        btnShare = findViewById(R.id.btnShare);

        prefHelper = new SharedPrefHelper(this);

        List<String> units = new ArrayList<>(ConversionUtils.volumeUnits.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        btnConvert.setOnClickListener(v -> convertUnits());

        btnReturn.setOnClickListener(v -> finish());

        btnShare.setOnClickListener(v -> {
            String resultText = answer.getText().toString().trim();
            if (resultText.isEmpty() || resultText.equals("Result will appear here")) {
                resultText = "No result to share yet!";
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Unit Conversion Result");
            shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
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
        double result = ConversionUtils.convert(value, fromUnit, toUnit, "volume");
        answer.setText(value + " " + fromUnit + " = " + result + " " + toUnit);
        // Animate result
        answer.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).withEndAction(() -> {
            answer.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
        }).start();
        // Save to history
        prefHelper.saveHistoryEntry(new SharedPrefHelper.HistoryEntry("volume", fromUnit, toUnit, value, result, System.currentTimeMillis()));
        Toast.makeText(this, "Saved to History", Toast.LENGTH_SHORT).show();
    }
}