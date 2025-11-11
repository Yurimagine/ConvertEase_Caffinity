package com.example.unitconversion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unitconversion.currency.ApiService;
import com.example.unitconversion.currency.AllRatesResponse;
import com.example.unitconversion.utils.SharedPrefHelper;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyActivity extends AppCompatActivity {

    private static final String TAG = "CurrencyActivity";
    private static final String API_KEY = "1769a63e03-92a2864ab8-t5jz1g";

    private EditText inputValue;
    private Spinner spinnerFrom, spinnerTo;
    private TextView answer, tvLastUpdated;
    private Button btnConvert, btnReturn, btnShare, btnRefresh;
    private ProgressBar progressBar;

    private SharedPrefHelper prefHelper;
    private final String[] currencies = {
            "USD","EUR","GBP","JPY","PHP","CNY","KRW","INR","SGD","THB",
            "MYR","IDR","VND","HKD","TWD","BND","KHR","LAK","MMK","KPW",
            "MNT","AFN","BDT","BTN","LKR","MVR","NPR","PKR","SCR","AED",
            "BHD","ILS","JOD","KWD","OMR","QAR","SAR","YER","AZN","AMD",
            "GEL","KZT","TJS","TMT","UZS","BYN","RUB","UAH","MDL","ALL",
            "MKD","RSD","BAM","HRK","CZK","HUF","PLN","RON","BGN","ISK"
    };

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        inputValue = findViewById(R.id.inputValue);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        answer = findViewById(R.id.answer);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        btnConvert = findViewById(R.id.btnConvert);
        btnReturn = findViewById(R.id.btnReturn);
        btnShare = findViewById(R.id.btnShare);
        btnRefresh = findViewById(R.id.btnRefresh);
        progressBar = findViewById(R.id.progressBar);

        prefHelper = new SharedPrefHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        apiService = buildRetrofit().create(ApiService.class);

        // Load cached rates or fetch new
        loadCachedRates();

        btnConvert.setOnClickListener(v -> convertCurrency());
        btnReturn.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareResult());
        btnRefresh.setOnClickListener(v -> fetchAllRates(safeGetSelectedBase()));
    }

    private Retrofit buildRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        return new Retrofit.Builder()
                .baseUrl("https://api.fastforex.io/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private String safeGetSelectedBase() {
        try {
            return spinnerFrom.getSelectedItem().toString();
        } catch (Exception e) {
            Log.w(TAG, "Fallback to USD", e);
        }
        return "USD";
    }

    private void loadCachedRates() {
        String base = safeGetSelectedBase();
        Map<String, Double> rates = prefHelper.readCachedRates(base);
        int age = prefHelper.cachedRatesAgeMinutes(base);
        if (rates != null && age < 30) {
            tvLastUpdated.setText("Rates last updated: " + age + " min ago");
            btnConvert.setEnabled(true);
        } else {
            tvLastUpdated.setText("Fetching latest rates...");
            fetchAllRates(base);
        }
    }

    private void fetchAllRates(String base) {
        progressBar.setVisibility(View.VISIBLE);
        btnConvert.setEnabled(false);

        Call<AllRatesResponse> call = apiService.getAllRates(API_KEY, base);
        call.enqueue(new Callback<AllRatesResponse>() {
            @Override
            public void onResponse(Call<AllRatesResponse> call, Response<AllRatesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().results != null) {
                    Map<String, Double> rates = response.body().results;
                    prefHelper.cacheRates(base, rates);
                    btnConvert.setEnabled(true);
                    tvLastUpdated.setText("Rates updated just now");
                    Toast.makeText(CurrencyActivity.this, "Rates updated", Toast.LENGTH_SHORT).show();
                } else {
                    handleOffline();
                }
            }

            @Override
            public void onFailure(Call<AllRatesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                handleOffline();
            }
        });
    }

    private void handleOffline() {
        String base = safeGetSelectedBase();
        Map<String, Double> rates = prefHelper.readCachedRates(base);
        if (rates != null) {
            int age = prefHelper.cachedRatesAgeMinutes(base);
            tvLastUpdated.setText("Offline: using cached rates (" + age + " min old)");
            btnConvert.setEnabled(true);
            Toast.makeText(this, "Using cached data", Toast.LENGTH_SHORT).show();
        } else {
            tvLastUpdated.setText("No rates available - check network");
            btnConvert.setEnabled(false);
            Toast.makeText(this, "Network error and no cache available", Toast.LENGTH_LONG).show();
        }
    }

    private void convertCurrency() {
        String inputText = inputValue.getText().toString().trim();
        if (inputText.isEmpty()) {
            answer.setText("Please enter an amount");
            return;
        }

        double amount;
        try { amount = Double.parseDouble(inputText); }
        catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        String from = safeGetSelectedBase();
        String to = spinnerTo.getSelectedItem().toString();

        Map<String, Double> rates = prefHelper.readCachedRates(from);
        if (rates != null && rates.containsKey(to)) {
            double rate = rates.get(to);
            double result = amount * rate;
            answer.setText(String.format("%.2f %s = %.2f %s", amount, from, result, to));
            answer.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).withEndAction(() -> {
                answer.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
            }).start();
        } else {
            Toast.makeText(this, "Rates not available. Please refresh.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareResult() {
        String resultText = answer.getText().toString().trim();
        if (resultText.isEmpty() || resultText.equals("Result will appear here")) {
            resultText = "No result to share yet!";
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Currency Conversion Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
