# 🎶 MelodyCraft - AI Music Generator App

An Android application that **generates music in MIDI format** using a hybrid AI system combining **Google’s Magenta (Improv RNN)** and a **custom model from Ollama 3.2 (1B)**. Users can generate and play music on their mobile devices using just a few inputs — as simple as selecting a genre!

---

## 📱 Features

- 🎵 **AI-Powered Music Generation**
  - Built using a **modified Improv RNN model** from Google’s [Magenta]([https://magenta.tensorflow.org/](https://github.com/magenta/magenta))
  - Uses **Ollama 3.2 (1B)** custom model to auto-generate music parameters based on genre

- 🧠 **Smart Parameter Inference**
  - Users provide:  
    - `genre`  
    - `primer melody` (or MIDI)  
    - `backing chords`  
    - `total time`  
  - The app **automatically calculates**:
    - `QPM (Quarter Notes Per Minute)`  
    - `steps_per_chord`  
  - If only the `genre` is given, **Ollama generates the rest!**

- 🎹 **MIDI Playback**
  - Built-in MIDI player with support for **multiple instruments**
  - Play your generated music directly in the app

---

## 🛠️ Built With

- **Java** - Core language used
- **Android Studio** - Development environment
- **Magenta - Improv RNN (Modified)** - For music sequence generation
- **Ollama 3.2 (1B)** - Custom model to generate input parameters

---

## ⚙️ How It Works

1. User selects:
   - `Genre` (e.g., Jazz, Rock, Pop, etc.)
   - Optional: `Primer melody` or MIDI, and `backing chords`
   - `Total time` for the output music

2. App infers or generates:
   - `QPM` and `steps_per_chord` based on `total_time`
   - If only genre is selected, **Ollama model** generates all needed parameters

3. The modified **Improv RNN** generates MIDI notes using the parameters

4. Output is saved as a **MIDI file**

5. User can play the file using the built-in **MIDI player with instrument selection**

---

## 🧠 Future Plans

- Add support for exporting and sharing generated MIDI
- Add real-time editing of primer melody within the app
- Support for live instrument input via microphone or MIDI keyboard
- Cloud sync of user-generated melodies

---

## 👨‍🎤 Contributors

- [Amarnath Bhat](https://github.com/amar-nath-bhat)
- [Triyan Mukherjee](https://github.com/FallenDeity)
- [Shreyash Srivastava](https://github.com/Shreyash1919)
