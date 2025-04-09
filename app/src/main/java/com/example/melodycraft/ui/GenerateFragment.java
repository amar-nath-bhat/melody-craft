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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GenerateFragment extends Fragment {
    private AutoCompleteTextView genreDropdown;
    private ChipGroup melodyChipGroup;
    private ChipGroup chordChipGroup;
    private Slider durationSlider;
    private TextView durationText;
    private List<Integer> primerMelodies = new ArrayList<>();
    private List<String> backingChords = new ArrayList<>();

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
        durationSlider.setValue(120);
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
        // Calculate QPM and steps_per_chord based on duration
        float duration = durationSlider.getValue();
        int numChords = backingChords.size();
        int stepsPerQuarter = 4; // Standard value

        // Rearrange the formula to solve for QPM × steps_per_chord
        float product = (numChords * 60) / (duration * stepsPerQuarter);
        int qpm = 90; // Default tempo
        int stepsPerChord = Math.round(product / qpm);

        JSONObject params = new JSONObject();
        try {
            params.put("genre", genreDropdown.getText().toString());
            params.put("primer_melody", new JSONArray(primerMelodies));
            params.put("backing_chords", TextUtils.join(" ", backingChords));
            params.put("qpm", qpm);
            params.put("steps_per_chord", stepsPerChord);

            // TODO: Send params to your music generation API
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}