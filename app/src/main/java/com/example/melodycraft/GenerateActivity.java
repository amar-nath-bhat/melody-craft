package com.example.melodycraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.melodycraft.ui.GenerateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;

public class GenerateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.generate_frame_container, new GenerateFragment())
                    .commit();
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        MaterialShapeDrawable materialShapeDrawable = (MaterialShapeDrawable) navigationView.getBackground();
        materialShapeDrawable.setShapeAppearanceModel(materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCornerSize(50)
                .setTopRightCornerSize(50)
                .build());
        navigationView.setBackground(materialShapeDrawable);
        navigationView.setSelectedItemId(R.id.generate);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
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