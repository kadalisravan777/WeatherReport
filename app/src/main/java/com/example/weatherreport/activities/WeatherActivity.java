package com.example.weatherreport.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.weatherapp.utils.SessionManager;
import org.json.*;
import com.example.weatherreport.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";

    EditText cityInput;
    Button getWeatherBtn;
    TextView weatherResult;
    SessionManager session;
    final String API_KEY = "9ae6370955d26e2727f5175288cd0820"; // Insert your API key here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: WeatherActivity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.cityInput);
        getWeatherBtn = findViewById(R.id.getWeatherBtn);
        weatherResult = findViewById(R.id.weatherResult);
        session = new SessionManager(this);

        getWeatherBtn.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            Log.d(TAG, "User input city: " + city);
            if (!city.isEmpty()) {
                fetchWeather(city);
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        displaySearchHistory(); // Show history when activity opens
    }

    private void fetchWeather(String cityName) {
        Log.d(TAG, "fetchWeather: Starting geocoding for: " + cityName);

        String geoUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + cityName +
                "&limit=1&appid=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);
        weatherResult.setVisibility(View.VISIBLE);
        StringRequest geoRequest = new StringRequest(Request.Method.GET, geoUrl,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        if (arr.length() == 0) {
                            Log.w(TAG, "fetchWeather: No location found for input: " + cityName);

                            weatherResult.setText("Location not found.");
                            return;
                        }

                        JSONObject location = arr.getJSONObject(0);
                        double lat = location.getDouble("lat");
                        double lon = location.getDouble("lon");

                        Log.d(TAG, "Geocoding success: Lat = " + lat + ", Lon = " + lon);

                        fetchWeatherByCoordinates(lat, lon,cityName);

                    } catch (JSONException e) {
                        Log.e(TAG, "fetchWeather: Geo JSON parsing error: " + e.getMessage(), e);
                        weatherResult.setText("Error finding location.");
                    }
                },
                error -> {
                    Log.e(TAG, "fetchWeather: Geo API request failed: " + error.toString(), error);
                    weatherResult.setText("Location fetch failed.");
                });

        queue.add(geoRequest);
    }

    private void fetchWeatherByCoordinates(double lat, double lon, String villageName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

        Log.d(TAG, "fetchWeatherByCoordinates: Request URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d(TAG, "fetchWeatherByCoordinates: Weather API response received");

                        JSONObject obj = new JSONObject(response);
                        JSONObject main = obj.getJSONObject("main");
                        JSONObject wind = obj.getJSONObject("wind");

                        String cityName = obj.getString("name");
                        String weather = "Village: " + villageName + "\n" +
                                "City: " + cityName + "\n" +
                                "Temp: " + main.getDouble("temp") + "°C\n" +
                                "Humidity: " + main.getInt("humidity") + "%\n" +
                                "Wind: " + wind.getDouble("speed") + " m/s";

                        Log.d(TAG, "Parsed Weather Data:\n" + weather);

                        weatherResult.setText(weather);

                        saveToHistory(villageName);
                        displaySearchHistory();

                    } catch (JSONException e) {
                        Log.e(TAG, "fetchWeatherByCoordinates: JSON parsing error: " + e.getMessage(), e);
                        weatherResult.setText("Error parsing weather data.");
                    }
                },
                error -> {
                    Log.e(TAG, "fetchWeatherByCoordinates: Weather API request failed: " + error.toString(), error);
                    weatherResult.setText("Failed to get weather.");
                });

        queue.add(stringRequest);
    }

    private void saveToHistory(String cityName) {
        cityName = cityName.toLowerCase();
        Log.d(TAG, "saveToHistory: Saving city to history - " + cityName);

        SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
        List<String> list = new ArrayList<>();
        String joined = prefs.getString("searches", "");

        if (!joined.isEmpty()) {
            list = new ArrayList<>(Arrays.asList(joined.split(",")));
        }

        if (list.contains(cityName)) {
            Log.d(TAG, "saveToHistory: City already in history, moving to top");
            list.remove(cityName);
        }

        list.add(0,cityName);

        if (list.size() > 5) {
            list = list.subList(0, 5);
        }

        String updated = String.join(",", list);
        prefs.edit().putString("searches", updated).apply();

        Log.d(TAG, "saveToHistory: Updated search history: " + updated);
    }

    private void displaySearchHistory() {
        Log.d(TAG, "displaySearchHistory: Displaying search history");

        LinearLayout historyLayout = findViewById(R.id.historyLayout);
        TextView historyTitle = findViewById(R.id.historyTitle);
        historyLayout.removeAllViews();

        SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
        String joined = prefs.getString("searches", "");

        if (joined.isEmpty()) {
            Log.d(TAG, "displaySearchHistory: No history found");
            historyTitle.setVisibility(View.GONE);
            historyLayout.setVisibility(View.GONE);
            return;
        }

        List<String> list = Arrays.asList(joined.split(","));
        historyTitle.setVisibility(View.VISIBLE);
        historyLayout.setVisibility(View.VISIBLE);

        for (String city : list) {
            Log.d(TAG, "displaySearchHistory: Adding city to view: " + city);
            TextView item = new TextView(this);
            item.setText("📍 " + city);
            item.setTextSize(16f);
            item.setTextColor(Color.BLACK);
            item.setPadding(8, 8, 8, 8);
            historyLayout.addView(item);
        }
    }
}
