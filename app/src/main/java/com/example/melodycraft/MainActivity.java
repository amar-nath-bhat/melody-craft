package com.example.melodycraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.melodycraft.ui.HomeFragment;
import com.example.melodycraft.ui.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame_container, new HomeFragment())
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
            }
            return true;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // apply theme
        applyTheme();
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // not signed in
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}