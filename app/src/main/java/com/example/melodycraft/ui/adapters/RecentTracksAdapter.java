package com.example.melodycraft.ui.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodycraft.R;
import com.example.melodycraft.models.Track;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Random;

public class RecentTracksAdapter extends RecyclerView.Adapter<RecentTracksAdapter.TrackViewHolder> {

    private final List<Track> tracks;

    public RecentTracksAdapter(List<Track> tracks) {
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.songName.setText(track.getSongName());
        holder.genre.setText(track.getGenre());
        holder.backingChords.setText(track.getBackingChords());
        holder.duration.setText(String.format("%d:%02d", track.getDuration() / 60, track.getDuration() % 60));

        // Generate a thumbnail using the song name
        GradientDrawable thumbnail = new GradientDrawable();
        thumbnail.setShape(GradientDrawable.OVAL);
        // color gradient based on songname randomly generated
        Random random = new Random(track.getSongName().hashCode());
        int color1 = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int color2 = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        thumbnail.setColors(new int[]{color1, color2});
        holder.thumbnail.setBackground(thumbnail);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void updateTracks(List<Track> newTracks) {
        tracks.clear();
        tracks.addAll(newTracks);
        notifyDataSetChanged();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView songName, genre, backingChords, duration;
        View thumbnail;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            genre = itemView.findViewById(R.id.genre);
            backingChords = itemView.findViewById(R.id.backing_chords);
            duration = itemView.findViewById(R.id.duration);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
