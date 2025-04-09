package com.example.melodycraft;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class RecentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecentAdapter recentAdapter;
    private List<Song> recentSongs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

//        recyclerView = findViewById(R.id.recent_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Dummy data
//        recentSongs = new ArrayList<>();
//        recentSongs.add(new Song("Song 1", "Artist 1"));
//        recentSongs.add(new Song("Song 2", "Artist 2"));
//        recentSongs.add(new Song("Song 3", "Artist 3"));
//
//        // Set adapter
//        recentAdapter = new RecentAdapter(recentSongs);
//        recyclerView.setAdapter(recentAdapter);

        addSongsFragment();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.recent);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            } else if (itemId == R.id.explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                return true;
            }
            return true;
        });
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