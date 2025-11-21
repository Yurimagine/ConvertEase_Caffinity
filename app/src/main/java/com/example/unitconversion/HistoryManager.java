package com.example.unitconversion;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryManager {

    private static final String PREF_NAME = "history_storage";
    private static final String KEY_DATA = "history_data";

    private SharedPreferences prefs;

    public HistoryManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addHistory(String type, String input, String output, String date) {
        try {
            String json = prefs.getString(KEY_DATA, "[]");
            JSONArray array = new JSONArray(json);

            JSONObject item = new JSONObject();
            item.put("type", type);
            item.put("input", input);
            item.put("output", output);
            item.put("date", date);

            array.put(item);

            prefs.edit().putString(KEY_DATA, array.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getHistory() {
        try {
            String json = prefs.getString(KEY_DATA, "[]");
            return new JSONArray(json);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void clearHistory() {
        prefs.edit().putString(KEY_DATA, "[]").apply();
    }

    public int getHistoryCount() {
        return getHistory().length();
    }

    public void deleteItem(int index) {
        try {
            JSONArray array = getHistory();
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < array.length(); i++) {
                if (i != index) newArray.put(array.getJSONObject(i));
            }

            prefs.edit().putString(KEY_DATA, newArray.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
