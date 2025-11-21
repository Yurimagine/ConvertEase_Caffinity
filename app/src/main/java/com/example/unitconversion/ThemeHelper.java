package com.example.unitconversion;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "current_theme";

    public static void saveTheme(Context context, int themeResId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, themeResId).apply();
    }

    public static int loadTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, R.style.AppTheme_Light); // default
    }
}
