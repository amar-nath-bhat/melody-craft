package com.example.melodycraft;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ExploreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        addMusicBannerFragment();
        addSongsFragment();

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
