package com.example.unitconversion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;
import android.util.TypedValue;
import android.view.View;

public class ThemeManager {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "current_theme";

    public static void applyTheme(Activity activity) {
        int themeResId = loadTheme(activity);
        activity.setTheme(themeResId);
    }

    public static void saveTheme(Context context, int themeResId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, themeResId).apply();
    }

    public static int loadTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, R.style.AppTheme_Light);
    }

    public static void switchTheme(Activity activity, int themeResId) {
        saveTheme(activity, themeResId);
        activity.recreate();
    }

    /**
     * Apply the theme's background drawable to the activity's root layout.
     * Call this AFTER setContentView().
     */
    public static void applyBackground(Activity activity) {
        TypedValue outValue = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.windowBackground, outValue, true)) {
            Drawable bg = AppCompatResources.getDrawable(activity, outValue.resourceId);
            View root = activity.findViewById(android.R.id.content);
            if (root != null) root.setBackground(bg);
        }
    }
}
