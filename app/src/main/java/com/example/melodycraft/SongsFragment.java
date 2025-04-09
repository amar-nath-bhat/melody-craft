package com.example.melodycraft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SongsFragment extends Fragment {

    private ImageView imgAlbum, btnPlay, btnOptions;
    private TextView tvSongTitle, tvArtist;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        // Initialize views
        imgAlbum = view.findViewById(R.id.imgAlbum);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnOptions = view.findViewById(R.id.btnOptions);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtist);

        // Set listeners
        btnPlay.setOnClickListener(v -> {
            // Handle play button click
            // Implement media player logic here
        });

        btnOptions.setOnClickListener(v -> {
            // Handle options button click
            // Show options menu or perform any desired action
        });

        return view;
    }
}
