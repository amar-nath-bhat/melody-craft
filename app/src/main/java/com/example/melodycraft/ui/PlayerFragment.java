package com.example.melodycraft.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.melodycraft.R;
import com.example.melodycraft.models.Track;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ProgramChange;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerFragment extends Fragment {
    private final HashMap<String, Integer> instrumentMap = new HashMap<>();
    private FirebaseAuth mAuth;
    private MaterialAutoCompleteTextView trackSelector;
    private AutoCompleteTextView instrumentSelector;
    private MaterialTextView trackTitle, trackGenre, currentTime, totalTime, volumeText;
    private Slider seekBar, volumeSlider;
    private MaterialButton playPauseButton, previousButton, nextButton;
    private View albumArt;
    private List<Track> tracks;
    private int currentTrackIndex = 0;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBarRunnable;

    private void initializeInstrumentMap() {
        // Piano
        instrumentMap.put("Acoustic Grand Piano", 0);
        instrumentMap.put("Bright Acoustic Piano", 1);
        instrumentMap.put("Electric Grand Piano", 2);
        instrumentMap.put("Honky-tonk Piano", 3);
        instrumentMap.put("Electric Piano 1", 4);
        instrumentMap.put("Electric Piano 2", 5);
        instrumentMap.put("Harpsichord", 6);
        instrumentMap.put("Clavinet", 7);

        // Chromatic Percussion
        instrumentMap.put("Celesta", 8);
        instrumentMap.put("Glockenspiel", 9);
        instrumentMap.put("Music Box", 10);
        instrumentMap.put("Vibraphone", 11);
        instrumentMap.put("Marimba", 12);
        instrumentMap.put("Xylophone", 13);
        instrumentMap.put("Tubular Bells", 14);
        instrumentMap.put("Dulcimer", 15);

        // Organ
        instrumentMap.put("Drawbar Organ", 16);
        instrumentMap.put("Percussive Organ", 17);
        instrumentMap.put("Rock Organ", 18);
        instrumentMap.put("Church Organ", 19);
        instrumentMap.put("Reed Organ", 20);
        instrumentMap.put("Accordion", 21);
        instrumentMap.put("Harmonica", 22);
        instrumentMap.put("Tango Accordion", 23);

        // Guitar
        instrumentMap.put("Acoustic Guitar (nylon)", 24);
        instrumentMap.put("Acoustic Guitar (steel)", 25);
        instrumentMap.put("Electric Guitar (jazz)", 26);
        instrumentMap.put("Electric Guitar (clean)", 27);
        instrumentMap.put("Electric Guitar (muted)", 28);
        instrumentMap.put("Overdriven Guitar", 29);
        instrumentMap.put("Distortion Guitar", 30);
        instrumentMap.put("Guitar Harmonics", 31);

        // Bass
        instrumentMap.put("Acoustic Bass", 32);
        instrumentMap.put("Electric Bass (finger)", 33);
        instrumentMap.put("Electric Bass (pick)", 34);
        instrumentMap.put("Fretless Bass", 35);
        instrumentMap.put("Slap Bass 1", 36);
        instrumentMap.put("Slap Bass 2", 37);
        instrumentMap.put("Synth Bass 1", 38);
        instrumentMap.put("Synth Bass 2", 39);

        // Strings
        instrumentMap.put("Violin", 40);
        instrumentMap.put("Viola", 41);
        instrumentMap.put("Cello", 42);
        instrumentMap.put("Contrabass", 43);
        instrumentMap.put("Tremolo Strings", 44);
        instrumentMap.put("Pizzicato Strings", 45);
        instrumentMap.put("Orchestral Harp", 46);
        instrumentMap.put("Timpani", 47);

        // Ensemble
        instrumentMap.put("String Ensemble 1", 48);
        instrumentMap.put("String Ensemble 2", 49);
        instrumentMap.put("Synth Strings 1", 50);
        instrumentMap.put("Synth Strings 2", 51);
        instrumentMap.put("Choir Aahs", 52);
        instrumentMap.put("Voice Oohs", 53);
        instrumentMap.put("Synth Voice", 54);
        instrumentMap.put("Orchestra Hit", 55);

        // Brass
        instrumentMap.put("Trumpet", 56);
        instrumentMap.put("Trombone", 57);
        instrumentMap.put("Tuba", 58);
        instrumentMap.put("Muted Trumpet", 59);
        instrumentMap.put("French Horn", 60);
        instrumentMap.put("Brass Section", 61);
        instrumentMap.put("Synth Brass 1", 62);
        instrumentMap.put("Synth Brass 2", 63);

        // Reed
        instrumentMap.put("Soprano Sax", 64);
        instrumentMap.put("Alto Sax", 65);
        instrumentMap.put("Tenor Sax", 66);
        instrumentMap.put("Baritone Sax", 67);
        instrumentMap.put("Oboe", 68);
        instrumentMap.put("English Horn", 69);
        instrumentMap.put("Bassoon", 70);
        instrumentMap.put("Clarinet", 71);

        // Pipe
        instrumentMap.put("Piccolo", 72);
        instrumentMap.put("Flute", 73);
        instrumentMap.put("Recorder", 74);
        instrumentMap.put("Pan Flute", 75);
        instrumentMap.put("Blown Bottle", 76);
        instrumentMap.put("Shakuhachi", 77);
        instrumentMap.put("Whistle", 78);
        instrumentMap.put("Ocarina", 79);

        // Synth Lead
        instrumentMap.put("Lead 1 (square)", 80);
        instrumentMap.put("Lead 2 (sawtooth)", 81);
        instrumentMap.put("Lead 3 (calliope)", 82);
        instrumentMap.put("Lead 4 (chiff)", 83);
        instrumentMap.put("Lead 5 (charang)", 84);
        instrumentMap.put("Lead 6 (voice)", 85);
        instrumentMap.put("Lead 7 (fifths)", 86);
        instrumentMap.put("Lead 8 (bass + lead)", 87);

        // Synth Pad
        instrumentMap.put("Pad 1 (new age)", 88);
        instrumentMap.put("Pad 2 (warm)", 89);
        instrumentMap.put("Pad 3 (polysynth)", 90);
        instrumentMap.put("Pad 4 (choir)", 91);
        instrumentMap.put("Pad 5 (bowed)", 92);
        instrumentMap.put("Pad 6 (metallic)", 93);
        instrumentMap.put("Pad 7 (halo)", 94);
        instrumentMap.put("Pad 8 (sweep)", 95);

        // Synth Effects
        instrumentMap.put("FX 1 (rain)", 96);
        instrumentMap.put("FX 2 (soundtrack)", 97);
        instrumentMap.put("FX 3 (crystal)", 98);
        instrumentMap.put("FX 4 (atmosphere)", 99);
        instrumentMap.put("FX 5 (brightness)", 100);
        instrumentMap.put("FX 6 (goblins)", 101);
        instrumentMap.put("FX 7 (echoes)", 102);
        instrumentMap.put("FX 8 (sci-fi)", 103);

        // Ethnic
        instrumentMap.put("Sitar", 104);
        instrumentMap.put("Banjo", 105);
        instrumentMap.put("Shamisen", 106);
        instrumentMap.put("Koto", 107);
        instrumentMap.put("Kalimba", 108);
        instrumentMap.put("Bagpipe", 109);
        instrumentMap.put("Fiddle", 110);
        instrumentMap.put("Shanai", 111);

        // Percussive
        instrumentMap.put("Tinkle Bell", 112);
        instrumentMap.put("Agogo", 113);
        instrumentMap.put("Steel Drums", 114);
        instrumentMap.put("Woodblock", 115);
        instrumentMap.put("Taiko Drum", 116);
        instrumentMap.put("Melodic Tom", 117);
        instrumentMap.put("Synth Drum", 118);
        instrumentMap.put("Reverse Cymbal", 119);

        // Sound Effects
        instrumentMap.put("Guitar Fret Noise", 120);
        instrumentMap.put("Breath Noise", 121);
        instrumentMap.put("Seashore", 122);
        instrumentMap.put("Bird Tweet", 123);
        instrumentMap.put("Telephone Ring", 124);
        instrumentMap.put("Helicopter", 125);
        instrumentMap.put("Applause", 126);
        instrumentMap.put("Gunshot", 127);
    }

    private void startUpdatingSeekBar() {
        updateSeekBarRunnable = () -> {
            if (mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                seekBar.setValue(currentPosition);
                currentTime.setText(formatTime(currentPosition));
                mainHandler.postDelayed(updateSeekBarRunnable, 1000);
            }
        };
        mainHandler.post(updateSeekBarRunnable);
    }

    private void stopUpdatingSeekBar() {
        mainHandler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        initializeInstrumentMap();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        initializeViews(view);
        setupListeners();
        loadTracks();
        // Simulate loading tracks
//        tracks = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            tracks.add(new Track("Track " + (i + 1), "Genre " + (i % 3), "Chords", 4, 120, "song" + (i + 1) + ".mid"));
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
//                android.R.layout.simple_dropdown_item_1line, new String[]{"Track 1", "Track 2", "Track 3"});
//        trackSelector.setAdapter(adapter);
        return view;
    }

    private void initializeViews(View view) {
        trackSelector = view.findViewById(R.id.track_selector);
        instrumentSelector = view.findViewById(R.id.instrument_selector);
        trackTitle = view.findViewById(R.id.track_title);
        trackGenre = view.findViewById(R.id.track_genre);
        currentTime = view.findViewById(R.id.current_time);
        totalTime = view.findViewById(R.id.total_time);
        seekBar = view.findViewById(R.id.seek_bar);
        volumeSlider = view.findViewById(R.id.volume_slider);
        playPauseButton = view.findViewById(R.id.play_pause_button);
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);
        albumArt = view.findViewById(R.id.album_art);
        volumeText = view.findViewById(R.id.volume_level_text);

        // Setup instrument selector
        ArrayAdapter<String> instrumentAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.condensed_dropdown_item, new ArrayList<>(instrumentMap.keySet()));
        instrumentSelector.setAdapter(instrumentAdapter);

        volumeSlider.setValue(1.0f);
        mediaPlayer.setVolume(1.0f, 1.0f);
        volumeText.setText(String.format(Locale.getDefault(), "%.0f%%", volumeSlider.getValue() * 100));
    }

    private void setupListeners() {
        playPauseButton.setOnClickListener(v -> togglePlayPause());
        previousButton.setOnClickListener(v -> playPreviousTrack());
        nextButton.setOnClickListener(v -> playNextTrack());

        seekBar.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                // Update playback position
                updatePlaybackPosition((int) value);
            }
        });

        volumeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                updateVolume(value);
            }
        });

        trackSelector.setOnItemClickListener((parent, view, position, id) -> {
            currentTrackIndex = position;
            loadTrack(tracks.get(position));
        });

        instrumentSelector.setOnItemClickListener((parent, view, position, id) -> {
            updateInstrument(position);
        });

        instrumentSelector.setOnItemClickListener((parent, view, position, id) -> {
            String selectedInstrument = (String) parent.getItemAtPosition(position);
            int instrumentIndex = Objects.requireNonNull(instrumentMap.getOrDefault(selectedInstrument, 0));
            updateInstrument(instrumentIndex);
        });
    }

    private void loadTracks() {
        String url = "http://192.168.240.174:8000/getAllTracks";
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
                        requireActivity().runOnUiThread(() -> handleTracksResponse(responseBuilder.toString()));
                    }

                    @Override
                    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
                        // Handle error
                    }
                }, executor);

        requestBuilder.build().start();
    }

    private void handleTracksResponse(String response) {
        try {
            JSONArray tracksArray = new JSONArray(response);
            tracks = new ArrayList<>();
            List<String> trackNames = new ArrayList<>();

            for (int i = 0; i < tracksArray.length(); i++) {
                JSONObject trackJson = tracksArray.getJSONObject(i);
                Track track = new Track(
                        trackJson.getString("song_name"),
                        trackJson.getString("genre"),
                        trackJson.getInt("total_time"),
                        trackJson.getInt("steps_per_chord"),
                        trackJson.getInt("qpm"),
                        trackJson.getString("file_name")
                );
                tracks.add(track);
                trackNames.add(track.getSongName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, trackNames);
            trackSelector.setAdapter(adapter);

            if (!tracks.isEmpty()) {
                loadTrack(tracks.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrack(Track track) {
        trackTitle.setText(track.getSongName());
        trackGenre.setText(track.getGenre());
        currentTime.setText("0:00");
        totalTime.setText(formatTime(track.getDuration()));
        GradientDrawable thumbnail = new GradientDrawable();
        Random random = new Random(track.getSongName().hashCode());
        int color1 = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int color2 = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        thumbnail.setColors(new int[]{color1, color2});
        albumArt.setBackground(thumbnail);

        // Load MIDI file
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "guest";
        String url = String.format("http://192.168.240.174:8000/getTrack/%s/%s/%s",
                userId,
                track.getGenre(),
                track.getFileName());
        CronetEngine cronetEngine = new CronetEngine.Builder(requireContext()).build();

        File midiFile = new File(requireContext().getCacheDir(), "current_track.mid");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        UrlRequest.Callback callback = new UrlRequest.Callback() {
            @Override
            public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
                request.followRedirect();
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
                outputStream.write(bytes, 0, bytes.length);
                byteBuffer.clear();
                request.read(byteBuffer);
            }

            @Override
            public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
                try {
                    FileOutputStream fos = new FileOutputStream(midiFile);
                    outputStream.writeTo(fos);
                    fos.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                requireActivity().runOnUiThread(() -> {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(midiFile.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.setOnCompletionListener(mp -> {
                            playPauseButton.setIconResource(R.drawable.ic_play);
                            seekBar.setValue(0);
                            currentTime.setText("0:00");
                            mediaPlayer.seekTo(0);
                            mediaPlayer.pause();
                            stopUpdatingSeekBar();
                        });
                        mediaPlayer.setOnPreparedListener(mp -> {
                            totalTime.setText(formatTime(mediaPlayer.getDuration() / 1000));
                            seekBar.setValueFrom(0);
                            seekBar.setValueTo(mediaPlayer.getDuration() / 1000);
                            seekBar.setValue(0);
                            startUpdatingSeekBar();
                            playPauseButton.setIconResource(R.drawable.ic_pause);
                        });
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
                error.printStackTrace();
            }
        };

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url,
                callback,
                executor
        );

        requestBuilder.build().start();
    }

    private void togglePlayPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setIconResource(R.drawable.ic_play);
            stopUpdatingSeekBar();
        } else {
            mediaPlayer.start();
            playPauseButton.setIconResource(R.drawable.ic_pause);
            startUpdatingSeekBar();
        }
    }

    private void playPreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            loadTrack(tracks.get(currentTrackIndex));
        }
    }

    private void playNextTrack() {
        if (currentTrackIndex < tracks.size() - 1) {
            currentTrackIndex++;
            loadTrack(tracks.get(currentTrackIndex));
        }
    }

    private void updatePlaybackPosition(int position) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(position * 1000);
            currentTime.setText(formatTime(position));
        }
    }

    private void updateVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
        volumeText.setText(String.format(Locale.getDefault(), "%.0f%%", volume * 100));
    }

    private void updateInstrument(int instrumentIndex) {
        try {
            File originalMidi = new File(requireContext().getCacheDir(), "current_track.mid");

            // Read and parse MIDI
            MidiFile midi = new MidiFile(originalMidi);

            // Change instrument for all tracks (you can customize this logic)
            for (MidiTrack track : midi.getTracks()) {
                // Add a ProgramChange event at the beginning
                ProgramChange programChange = new ProgramChange(0, 0, instrumentIndex);  // channel 0
                track.insertEvent(programChange);
            }

            // Save modified MIDI
            File modifiedMidi = new File(requireContext().getCacheDir(), "modified_track.mid");
            midi.writeToFile(modifiedMidi);

            // Load into MediaPlayer
            mediaPlayer.reset();
            mediaPlayer.setDataSource(modifiedMidi.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playPauseButton.setIconResource(R.drawable.ic_pause);
            startUpdatingSeekBar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int seconds) {
        return String.format(Locale.getDefault(), "%d:%02d",
                seconds / 60, seconds % 60);
    }
}