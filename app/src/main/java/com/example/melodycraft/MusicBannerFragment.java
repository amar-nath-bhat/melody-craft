package com.example.melodycraft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MusicBannerFragment extends Fragment {

    private ImageView tvImage;
    private TextView tvTitle, tvSubtitle;
    private ImageButton btnPlay;

    public MusicBannerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_banner, container, false);

        // Initialize views
        tvImage = view.findViewById(R.id.tvImage);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);
        btnPlay = view.findViewById(R.id.btnPlay);

        // Get arguments and set data
        Bundle args = getArguments();
        if (args != null) {
            tvTitle.setText(args.getString("ARG_PARAM1", "Default Title"));
            tvSubtitle.setText(args.getString("ARG_PARAM2", "Default Subtitle"));
        }

        // Set listeners
        btnPlay.setOnClickListener(v -> {
            // Handle play button click
        });

        return view;
    }
}
