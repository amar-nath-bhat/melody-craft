package com.example.melodycraft.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.melodycraft.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GenerateFragment extends Fragment {
    private AutoCompleteTextView genreDropdown;
    private ChipGroup melodyChipGroup;
    private ChipGroup chordChipGroup;
    private Slider durationSlider;
    private TextView durationText;
    private List<Integer> primerMelodies = new ArrayList<>();
    private List<String> backingChords = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();


    private static final String[] PRESET_GENRES = {
            "Indie Pop", "Rock", "Jazz", "Classical", "Electronic", "Hip Hop"
    };

    private static final String[] CHORD_OPTIONS = {
            "C", "D", "E", "F", "G", "A", "B",
            "Cm", "Dm", "Em", "Fm", "Gm", "Am", "Bm",
            "Cmaj7", "Dm7", "Em7", "Fmaj7", "G7", "Am7", "Bm7b5"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate, container, false);
        initializeViews(view);
        setupGenreDropdown();
        setupMelodyInput(view);
        setupChordInput(view);
        setupDurationSlider();
        setupGenerateButton(view);
        return view;
    }

    private void initializeViews(View view) {
        genreDropdown = view.findViewById(R.id.genre_dropdown);
        melodyChipGroup = view.findViewById(R.id.melody_chip_group);
        chordChipGroup = view.findViewById(R.id.chord_chip_group);
        durationSlider = view.findViewById(R.id.duration_slider);
        durationText = view.findViewById(R.id.duration_text);
    }

    private void setupGenreDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                PRESET_GENRES
        );
        genreDropdown.setAdapter(adapter);
    }

    private void setupMelodyInput(View view) {
        view.findViewById(R.id.add_melody_button).setOnClickListener(v -> {
            showMelodyInputDialog();
        });
    }

    private void setupChordInput(View view) {
        view.findViewById(R.id.add_chord_button).setOnClickListener(v -> {
            showChordSelectionDialog();
        });
    }

    private void setupDurationSlider() {
        durationSlider.addOnChangeListener((slider, value, fromUser) -> {
            updateDurationText(value);
        });
        durationSlider.setValue(30);
    }

    private void showMelodyInputDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_melody_input, null);
        NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        int minValue = -2;
        int maxValue = 127;
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxValue - minValue);
        numberPicker.setFormatter(value -> String.valueOf(value + minValue));

        builder.setView(dialogView)
                .setTitle("Add Melody Note")
                .setPositiveButton("Add", (dialog, which) -> {
                    addMelodyChip(numberPicker.getValue());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChordSelectionDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Chord")
                .setItems(CHORD_OPTIONS, (dialog, which) -> {
                    addChordChip(CHORD_OPTIONS[which]);
                })
                .show();
    }

    private void addMelodyChip(int value) {
        Chip chip = new Chip(requireContext());
        chip.setText(String.valueOf(value));
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            melodyChipGroup.removeView(chip);
            primerMelodies.remove(Integer.valueOf(value));
        });
        melodyChipGroup.addView(chip);
        primerMelodies.add(value);
    }

    private void addChordChip(String chord) {
        Chip chip = new Chip(requireContext());
        chip.setText(chord);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chordChipGroup.removeView(chip);
            backingChords.remove(chord);
        });
        chordChipGroup.addView(chip);
        backingChords.add(chord);
    }

    private void updateDurationText(float value) {
        durationText.setText(String.format("Duration: %.0f seconds", value));
    }

    private void setupGenerateButton(View view) {
        view.findViewById(R.id.generate_button).setOnClickListener(v -> {
            generateMusic();
        });
    }

    private void generateMusic() {
        float duration = durationSlider.getValue();
        int numChords = backingChords.size();
        int stepsPerQuarter = 4;
        float product = (numChords * 60) / (duration * stepsPerQuarter);
        int qpm = 90;
        int stepsPerChord = Math.round(product / qpm);

        JSONObject params = new JSONObject();
        try {
            params.put("genre", genreDropdown.getText().toString());
            params.put("primer_melody", new JSONArray(primerMelodies));
            params.put("backing_chords", TextUtils.join(" ", backingChords));
            params.put("qpm", qpm);
            params.put("steps_per_chord", stepsPerChord);

            String url = "https://api.example.com/generate";
            CronetEngine.Builder builder = new CronetEngine.Builder(requireContext());
            CronetEngine cronetEngine = builder.build();

            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, new UrlRequest.Callback() {
                        private final ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
                        private String responseString;

                        @Override
                        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
                            request.followRedirect();
                        }

                        @Override
                        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
                            if (info.getHttpStatusCode() != 200) {
                                request.cancel();
                                requireActivity().runOnUiThread(() ->
                                        showError("Server returned: " + info.getHttpStatusCode()));
                                return;
                            }
                            request.read(ByteBuffer.allocateDirect(32 * 1024));
                        }

                        @Override
                        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
                            byteBuffer.flip();
                            byte[] bytes = new byte[byteBuffer.remaining()];
                            byteBuffer.get(bytes);
                            bytesReceived.write(bytes, 0, bytes.length);
                            byteBuffer.clear();
                            request.read(byteBuffer);
                        }

                        @Override
                        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
                            responseString = bytesReceived.toString();
                            requireActivity().runOnUiThread(() -> handleResponse(responseString));
                        }

                        @Override
                        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
                            requireActivity().runOnUiThread(() ->
                                    showError("Error: " + error.getMessage()));
                        }
                    }, executor);

            requestBuilder.setHttpMethod("POST")
                    .addHeader("Content-Type", "application/json")
                    .setUploadDataProvider(
                            UploadDataProviders.create(params.toString().getBytes()), executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (JSONException e) {
            showError("Error creating request: " + e.getMessage());
        }
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            // Handle the response here
            // You might want to start playing the generated music
            // or save it to a file
        } catch (JSONException e) {
            showError("Error parsing response: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}