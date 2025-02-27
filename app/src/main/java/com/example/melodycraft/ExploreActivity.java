package com.example.melodycraft;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ExploreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        addMusicBannerFragment();
        addSongsFragment();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.explore);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            } else if (itemId == R.id.recent) {
                startActivity(new Intent(this, RecentActivity.class));
                return true;
            }
            return true;
        });
    }
    private void addMusicBannerFragment() {
        MusicBannerFragment musicBannerFragment = new MusicBannerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add fragment to the container
        transaction.replace(R.id.music_banner_container, musicBannerFragment);
        transaction.commit();
    }

    private void addSongsFragment() {
        SongsFragment songsFragment = new SongsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add fragment to the container
        transaction.replace(R.id.songs_container, songsFragment);
        transaction.commit();
    }
}
