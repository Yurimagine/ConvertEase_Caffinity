package com.example.unitconversion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedPrefHelper {
    private static final String PREFS_NAME = "unit_conversion_prefs";
    private static final String HISTORY_KEY = "history_entries";

    private final SharedPreferences sharedPrefs;
    private final Gson gson = new Gson();

    public SharedPrefHelper(Context context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ----------- History -----------

    public void saveHistoryEntry(HistoryEntry entry) {
        List<HistoryEntry> history = getHistory();
        history.add(0, entry);
        sharedPrefs.edit()
                .putString(HISTORY_KEY, gson.toJson(history))
                .apply();
    }

    public List<HistoryEntry> getHistory() {
        String json = sharedPrefs.getString(HISTORY_KEY, null);
        if (json == null) return new java.util.ArrayList<>();
        Type type = new TypeToken<List<HistoryEntry>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static class HistoryEntry {
        public String type, fromUnit, toUnit;
        public double fromValue, toValue;
        public long timestamp;

        public HistoryEntry(String type, String fromUnit, String toUnit, double fromValue, double toValue, long timestamp) {
            this.type = type;
            this.fromUnit = fromUnit;
            this.toUnit = toUnit;
            this.fromValue = fromValue;
            this.toValue = toValue;
            this.timestamp = timestamp;
        }
    }

    // ----------- Currency Cache -----------

    public void cacheRates(String base, Map<String, Double> rates) {
        sharedPrefs.edit()
                .putString(base + "_rates", gson.toJson(rates))
                .putLong(base + "_time", System.currentTimeMillis())
                .apply();
    }

    public Map<String, Double> readCachedRates(String base) {
        String json = sharedPrefs.getString(base + "_rates", null);
        if (json == null) return null;
        Type type = new TypeToken<Map<String, Double>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public int cachedRatesAgeMinutes(String base) {
        long lastTime = sharedPrefs.getLong(base + "_time", 0);
        if (lastTime == 0) return Integer.MAX_VALUE;
        return (int)((System.currentTimeMillis() - lastTime) / 1000 / 60);
    }
}
