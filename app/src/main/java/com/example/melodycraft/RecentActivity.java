package com.example.melodycraft;



import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        recyclerView = findViewById(R.id.recent_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data
        recentSongs = new ArrayList<>();
        recentSongs.add(new Song("Song 1", "Artist 1"));
        recentSongs.add(new Song("Song 2", "Artist 2"));
        recentSongs.add(new Song("Song 3", "Artist 3"));

        // Set adapter
        recentAdapter = new RecentAdapter(recentSongs);
        recyclerView.setAdapter(recentAdapter);
        }
}