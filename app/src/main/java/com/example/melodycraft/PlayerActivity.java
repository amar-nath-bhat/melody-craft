package com.example.melodycraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.melodycraft.ui.HomeFragment;
import com.example.melodycraft.ui.PlayerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.player_frame_container, new PlayerFragment())
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
        navigationView.setSelectedItemId(R.id.home);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            } else if (itemId == R.id.generate) {
                startActivity(new Intent(this, GenerateActivity.class));
                finish();
            } else if (itemId == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            return true;
        });

        // apply theme
        applyTheme();
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}