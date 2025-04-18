package com.example.melodycraft.models;

public class Track {
    private final String songName;
    private final String genre;
//    private final String backingChords;
    private final int stepsPerChord;
    private final int qpm;
    private final int totalTime;
    private final String fileName;

    public Track(String songName, String genre, int totalTime, int stepsPerChord, int qpm, String fileName) {
        this.songName = songName;
        this.genre = genre;
//        this.backingChords = backingChords;
        this.stepsPerChord = stepsPerChord;
        this.qpm = qpm;
        this.totalTime = totalTime;
        this.fileName = fileName;
    }

    public String getSongName() {
        return songName;
    }

    public String getGenre() {
        return genre;
    }

    public String getFileName() {
        return fileName;
    }

//    public String getBackingChords() {
//        return backingChords;
//    }

    public int getDuration() {
//        int stepsPerQuarter = 4; // Standard value
//        return (int) ((stepsPerChord * numChords) / (qpm * stepsPerQuarter) * 60);
            return totalTime;
    }
}