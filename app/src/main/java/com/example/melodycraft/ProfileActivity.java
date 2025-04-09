package com.example.melodycraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.melodycraft.ui.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_frame_container, new ProfileFragment())
                    .commit();
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.profile);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.explore) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            return true;
        });

        applyTheme();
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}