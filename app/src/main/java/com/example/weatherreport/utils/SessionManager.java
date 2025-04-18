package com.example.weatherapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void login(String username) {
        editor.putString("username", username);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("is_logged_in", false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public String getUsername() {
        return prefs.getString("username", null);
    }
}
