package com.example.melodycraft.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodycraft.R;
import com.example.melodycraft.models.Track;
import com.example.melodycraft.ui.adapters.RecentTracksAdapter;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private MaterialTextView totalTracks, favoriteGenre;
    private RecentTracksAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        totalTracks = view.findViewById(R.id.total_tracks);
        favoriteGenre = view.findViewById(R.id.favorite_genre);
        RecyclerView recentTracksRecycler = view.findViewById(R.id.recent_tracks_recycler);

        recentTracksRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecentTracksAdapter(new ArrayList<>());
        recentTracksRecycler.setAdapter(adapter);

//        fetchRecentTracks();
        // Simulate fetching data
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tracks.add(new Track("Song " + (i + 1), "Genre " + (i % 3), "C G Am F", 4, 120, "song" + (i + 1) + ".mid"));
        }
        adapter.updateTracks(tracks);

        // Update total tracks and favorite genre
        totalTracks.setText(String.valueOf(tracks.size()));
        String favoriteGenreText = "Pop"; // Placeholder, replace with actual logic
        favoriteGenre.setText(favoriteGenreText);
        return view;
    }

    private void fetchRecentTracks() {
        FirebaseUser user = mAuth.getCurrentUser();
        String url = "https://example.com/api/recent_tracks" + "?user_id=" + (user != null ? user.getUid() : "guest");
        CronetEngine cronetEngine = new CronetEngine.Builder(requireContext()).build();

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url, new UrlRequest.Callback() {
                    private final StringBuilder responseBuilder = new StringBuilder();

                    @Override
                    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {

                    }

                    @Override
                    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
                        request.read(ByteBuffer.allocateDirect(32 * 1024));
                    }

                    @Override
                    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        responseBuilder.append(new String(bytes));
                        byteBuffer.clear();
                        request.read(byteBuffer);
                    }

                    @Override
                    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
                        requireActivity().runOnUiThread(() -> parseResponse(responseBuilder.toString()));
                    }

                    @Override
                    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

                    }
                }, executor);

        requestBuilder.build().start();
    }

    private void parseResponse(String response) {
        try {
            JSONArray tracksArray = new JSONArray(response);
            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < tracksArray.length(); i++) {
                JSONObject trackJson = tracksArray.getJSONObject(i);
                Track track = new Track(
                        trackJson.getString("song_name"),
                        trackJson.getString("genre"),
                        trackJson.getString("backing_chords"),
                        trackJson.getInt("steps_per_chord"),
                        trackJson.getInt("qpm"),
                        trackJson.getString("file_name")
                );
                tracks.add(track);
            }
            adapter.updateTracks(tracks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}