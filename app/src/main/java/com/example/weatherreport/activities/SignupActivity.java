package com.example.weatherreport.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.R;

public class SignupActivity extends AppCompatActivity {
    EditText username, password;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {
            String u = username.getText().toString().trim();
            String p = password.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", u);
            editor.putString("password", p);
            editor.apply();
            username.setText("");
            password.setText("");
            Toast.makeText(this, "Signed up! Use credentials to login.", Toast.LENGTH_SHORT).show();
            finish(); // return to LoginActivity
        });

    }
}
