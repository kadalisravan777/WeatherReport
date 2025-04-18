package com.example.weatherreport.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.R;

public class SignupActivity extends AppCompatActivity {
    EditText username, password , confirmPassword;
    Button signupBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {
            String u = username.getText().toString().trim();
            String p = password.getText().toString().trim();
            String c = confirmPassword.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty() || c.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(u.length() < 6 ) {
                Toast.makeText(this, "Username should be 6 or more characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if( p.length() < 6) {
                Toast.makeText(this, "Password should be 6 or more characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!p.equals(c)) {
                Toast.makeText(this, "Make sure you enter the same password in both fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", u);
            editor.putString("password", p);
            editor.apply();
            username.setText("");
            password.setText("");
            confirmPassword.setText("");
            Toast.makeText(this, "Signed up! Use credentials to login.", Toast.LENGTH_SHORT).show();
            finish(); // return to LoginActivity
        });

    }
}
