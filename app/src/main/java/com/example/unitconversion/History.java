package com.example.unitconversion;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter adapter;
    private LinearLayout emptyStateLayout;
    private Button btnClearHistory;
    private ImageButton fabBack;
    private TextView tvTotalConversions, tvTodayConversions;

    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ThemeManager.applyBackground(this);

        historyManager = new HistoryManager(this);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        fabBack = findViewById(R.id.fabBack);
        tvTotalConversions = findViewById(R.id.tvTotalConversions);
        tvTodayConversions = findViewById(R.id.tvTodayConversions);

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, new ArrayList<>(), this::onDeleteItem);
        recyclerViewHistory.setAdapter(adapter);

        loadHistory();

        btnClearHistory.setOnClickListener(v -> showClearHistoryDialog());
        fabBack.setOnClickListener(v -> finish());

        applyThemeToUI();
    }

    private void applyThemeToUI() {
        TypedValue primaryValue = new TypedValue();
        TypedValue onPrimaryValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, onPrimaryValue, true);

        int primaryColor = primaryValue.data;
        int onPrimaryColor = onPrimaryValue.data;

        btnClearHistory.setBackgroundColor(primaryColor);
        btnClearHistory.setTextColor(onPrimaryColor);
        fabBack.setColorFilter(primaryColor);
        tvTotalConversions.setTextColor(onPrimaryColor);
        tvTodayConversions.setTextColor(onPrimaryColor);
    }

    private void loadHistory() {
        List<HistoryItem> historyList = new ArrayList<>();
        JSONArray array = historyManager.getHistory();

        try {
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject obj = array.getJSONObject(i);

                String type = obj.getString("type");
                String input = obj.getString("input");
                String output = obj.getString("output");
                String date = obj.getString("date");

                historyList.add(new HistoryItem(
                        i, type, input, "", output, "", date
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int totalCount = historyList.size();
        tvTotalConversions.setText(String.valueOf(totalCount));
        tvTodayConversions.setText(String.valueOf(getTodayCount(historyList)));

        if (historyList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
            btnClearHistory.setEnabled(false);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
            btnClearHistory.setEnabled(true);
        }

        adapter.updateData(historyList);
    }

    private int getTodayCount(List<HistoryItem> historyList) {
        int count = 0;
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault()).format(new java.util.Date());

        for (HistoryItem item : historyList) {
            if (item.getDate() != null && item.getDate().startsWith(today)) {
                count++;
            }
        }
        return count;
    }

    private void onDeleteItem(int index) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this conversion?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    historyManager.deleteItem(index);
                    loadHistory();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClearHistoryDialog() {
        int count = historyManager.getHistoryCount();

        new AlertDialog.Builder(this)
                .setTitle("Clear All History")
                .setMessage("Are you sure you want to delete all " + count + " conversions?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    historyManager.clearHistory();
                    loadHistory();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }
}
