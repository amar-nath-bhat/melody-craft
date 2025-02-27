package com.example.melodycraft;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MusicPlayerFragment extends Fragment {

    private ImageButton btnPrevious, btnPlayPause, btnNext;
    private SeekBar seekBar;
    private Button btnAddSong;
    private TextView tvSongTitle, tvArtist;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    public static MusicPlayerFragment newInstance(String param1, String param2) {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);

        // Initialize UI components
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnNext = view.findViewById(R.id.btnNext);
        seekBar = view.findViewById(R.id.seekBar);
        btnAddSong = view.findViewById(R.id.btnAddSong);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtist);

        // Initialize MediaPlayer with a sample song (replace with actual audio file)
//        mediaPlayer = MediaPlayer.create(getContext(), R.raw.sample_song);

        // SeekBar Listener
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Play/Pause button logic
        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    btnPlayPause.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.start();
//                    btnPlayPause.setImageResource(R.drawable.pause);
                }
                isPlaying = !isPlaying;
            }
        });

        // Previous button logic (implement skipping to previous track)
        btnPrevious.setOnClickListener(v -> {
            // Handle previous song action
        });

        // Next button logic (implement skipping to next track)
        btnNext.setOnClickListener(v -> {
            // Handle next song action
        });

        // Add song button logic
        btnAddSong.setOnClickListener(v -> {
            // Handle adding a new song action
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
