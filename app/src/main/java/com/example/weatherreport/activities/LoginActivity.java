package com.example.weatherreport.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.utils.SessionManager;
import com.example.weatherreport.R;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button loginBtn, signupRedirect;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);

        if (session.isLoggedIn()) {
            startActivity(new Intent(this,WeatherActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupRedirect = findViewById(R.id.signupRedirect);

        loginBtn.setOnClickListener(v -> {
            String u = username.getText().toString().trim();
            String p = password.getText().toString().trim();

            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            String savedUsername = prefs.getString("username", null);
            String savedPassword = prefs.getString("password", null);

            if (u.equals(savedUsername) && p.equals(savedPassword)) {
                session.login(u);
                username.setText("");
                password.setText("");
                startActivity(new Intent(this, WeatherActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid login!", Toast.LENGTH_SHORT).show();
            }
        });


        signupRedirect.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }
}
