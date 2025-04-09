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

        // Generate a thumbnail using the song name
        GradientDrawable thumbnail = new GradientDrawable();
        thumbnail.setShape(GradientDrawable.OVAL);
        thumbnail.setColor(Color.parseColor("#" + Integer.toHexString(track.getSongName().hashCode()).substring(0, 6)));
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
