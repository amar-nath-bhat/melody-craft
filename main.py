from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import os
import time
import ast
from fastapi.responses import FileResponse
import uvicorn
import re
from datetime import datetime
import mido  

import tensorflow.compat.v1 as tf
from note_seq.protobuf import generator_pb2, music_pb2
import note_seq
from pathlib import Path

from fastapi import FastAPI, Request
from pydantic import BaseModel
from ollama import chat
import json
from magenta.models.improv_rnn import improv_rnn_model
from magenta.models.improv_rnn import improv_rnn_sequence_generator
from magenta.models.shared import sequence_generator_bundle
from fastapi.responses import HTMLResponse
import math

app = FastAPI()
tf.disable_v2_behavior()

CHORD_SYMBOL = music_pb2.NoteSequence.TextAnnotation.CHORD_SYMBOL
CHORD_VELOCITY = 50

class MusicParams(BaseModel):
    name: str
    genre: str
    primer_melody: str = ''
    primer_midi: str = ''
    backing_chords: str = 'C G Am F'
    steps_per_chord: int = 16
    num_outputs: int = 1
    temperature: float = 1.0
    qpm: float = 120.0

def generate_music(params: MusicParams, user_id):
    output_dir = f"generated/{user_id}/{params.genre.replace(' ', '_')}/"
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    bundle_file = 'input/chord_pitches_improv.mag'
    bundle = sequence_generator_bundle.read_bundle_file(os.path.expanduser(bundle_file))
    config_id = bundle.generator_details.id
    config = improv_rnn_model.default_configs[config_id]

    generator = improv_rnn_sequence_generator.ImprovRnnSequenceGenerator(
        model=improv_rnn_model.ImprovRnnModel(config),
        details=config.details,
        steps_per_quarter=config.steps_per_quarter,
        checkpoint=None,
        bundle=bundle
    )

    qpm = params.qpm

    if params.primer_midi:
        primer_sequence = note_seq.midi_file_to_sequence_proto(params.primer_midi)
    elif params.primer_melody:
        primer_melody = note_seq.Melody(ast.literal_eval(params.primer_melody))
        primer_sequence = primer_melody.to_sequence(qpm=qpm)
    else:
        primer_sequence = note_seq.Melody([60]).to_sequence(qpm=qpm)

    raw_chords = params.backing_chords.split()
    repeated_chords = [chord for chord in raw_chords for _ in range(params.steps_per_chord)]
    chord_prog = note_seq.ChordProgression(repeated_chords)

    seconds_per_step = 60.0 / qpm / generator.steps_per_quarter
    total_seconds = len(repeated_chords) * seconds_per_step

    generator_options = generator_pb2.GeneratorOptions()
    max_primer_time = total_seconds * 0.8
    if primer_sequence.total_time > max_primer_time:
        primer_sequence = note_seq.extract_subsequence(primer_sequence, 0, max_primer_time)

    input_sequence = primer_sequence

    last_end_time = max((n.end_time for n in primer_sequence.notes), default=0.0)
    generator_options.generate_sections.add(
        start_time=last_end_time + seconds_per_step,
        end_time=total_seconds
    )

    chord_sequence = chord_prog.to_sequence(sequence_start_time=0.0, qpm=qpm)
    for ta in chord_sequence.text_annotations:
        if ta.annotation_type == CHORD_SYMBOL:
            chord = input_sequence.text_annotations.add()
            chord.CopyFrom(ta)

    input_sequence.total_time = len(repeated_chords) * seconds_per_step
    generator_options.args['temperature'].float_value = params.temperature

    filenames = []
    timestamp = time.strftime("%Y%m%d-%H%M%S")
    name = params.name.replace(" ", "-")
    for i in range(params.num_outputs):
        generated_sequence = generator.generate(input_sequence, generator_options)
        filename = os.path.join(output_dir, f"{name}_{timestamp}_{i+1}.mid")
        note_seq.sequence_proto_to_midi_file(generated_sequence, filename)
        filenames.append(filename)

    return filenames

def ollama_chat(prompt: str) -> dict:
    stream = chat(
        model='musicgen',
        messages=[{'role': 'user', 'content': prompt}],
        stream=True,
    )

    result = ""

    for chunk in stream:
        part = chunk['message']['content']
        result += part

    try:
        # Try parsing as JSON directly
        params = json.loads(result)
    except json.JSONDecodeError:
        # Fallback: extract key-value pairs manually (naive but works if format is consistent)
        params = {}
        for line in result.splitlines():
            if ':' in line:
                key, value = line.split(':', 1)
                key = key.strip().lower().replace(" ", "_")
                value = value.strip()
                # Attempt to parse numbers and lists
                if value.startswith('[') and value.endswith(']'):
                    try:
                        value = ast.literal_eval(value)
                    except:
                        pass
                elif value.replace('.', '', 1).isdigit():
                    value = float(value) if '.' in value else int(value)
                params[key] = value
    return params

# Generate params for a given genre
@app.get("/get_params/{genre}")
async def get_params(genre: str):
    param_data = ollama_chat(genre)
    return {"params" : param_data}

# Generate music based on user input
@app.post("/generate")
async def generate(request: Request):
    body = await request.json()

    print(body)

    # Set the parameter either from the request
    user_id = str(body.get("user_id", ""))
    genre = str(body.get("genre", ""))

    params_data = ollama_chat(genre)

    primer_melody = str(body.get("primer_melody", str(params_data.get("primer_melody", ""))))
    backing_chords = body.get("backing_chords", str(params_data.get("backing_chords", "C G Am F")))
    steps_per_chord = body.get("steps_per_chord", params_data.get("steps_per_chord", 16))
    qpm = body.get("qpm", params_data.get("qpm", 120))

    # Map to expected model input structure
    params = MusicParams(
        name=params_data.get("name", "Generated Music"),
        genre=genre,
        primer_melody=primer_melody,
        primer_midi="",
        backing_chords=backing_chords,
        steps_per_chord=steps_per_chord,
        num_outputs=int(1),
        temperature=float(1.0),
        qpm=qpm,
    )

    output_files = generate_music(params, user_id)
    if output_files:
        return {"message": "Music generated successfully"}
    else:
        return {"error": "Failed to generate music"}
    
@app.get("/getAllTracks")
async def get_all_generated_files():
    """List all generated files with detailed metadata from all users"""
    base_dir = Path("generated/")
    result = []
    
    try:
        # Iterate through all user directories
        for user_dir in base_dir.iterdir():
            if user_dir.is_dir():
                safe_user_id = user_dir.name
                
                # Iterate through all genre directories for this user
                for genre_dir in user_dir.iterdir():
                    if genre_dir.is_dir():
                        genre = genre_dir.name
                        
                        # Iterate through all MIDI files in genre directory
                        for file_path in genre_dir.glob('*.mid'):
                            try:
                                # Parse filename (format: songName_timestamp.mid)
                                filename = file_path.stem
                                parts = filename.rsplit('_', 1)  # Split on last underscore
                                
                                if len(parts) == 2:
                                    song_name = parts[0].replace('_', ' ')  # Convert underscores to spaces
                                    
                                    # Initialize metadata with default values
                                    total_time = 0
                                    qpm = 120  # Default QPM
                                    steps_per_chord = 8  # Default steps
                                    
                                    try:
                                        # Read MIDI file metadata
                                        midi_file = mido.MidiFile(file_path)
                                        total_time = math.floor(midi_file.length)
                                        
                                        # Get QPM (tempo) from MIDI file
                                        for track in midi_file.tracks:
                                            for msg in track:
                                                if msg.type == 'set_tempo':
                                                    # Convert microseconds per beat to QPM
                                                    qpm = round(60000000 / msg.tempo)
                                                    break
                                        
                                        # Get time signature (steps per chord)
                                        for track in midi_file.tracks:
                                            for msg in track:
                                                if msg.type == 'time_signature':
                                                    steps_per_chord = msg.numerator
                                                    break
                                        
                                    except Exception as e:
                                        pass  # Use default values if MIDI parsing fails
                                    
                                    # Add to result
                                    result.append({
                                        "user_id": safe_user_id,
                                        "song_name": song_name,
                                        "genre": genre,
                                        "total_time": total_time,
                                        "qpm": qpm,
                                        "steps_per_chord": steps_per_chord,
                                        "file_name": str(filename)
                                    })
                                
                            except Exception as e:
                                continue  # Skip files that don't match pattern

        if result:
            return result
        return {"error": "No generated music found."}
    
    except Exception as e:
        return {"error": f"Error accessing files: {str(e)}"}

@app.get("/getTracks/{user_id}")
async def get_user_generated_files(user_id: str):
    """List all generated files for a specific user with metadata"""
    # Sanitize user_id to prevent directory traversal
    safe_user_id = "".join(c for c in user_id if c.isalnum() or c in ('-', '_'))
    if not safe_user_id:
        return {"error": "Invalid user ID"}
    
    directory = Path(f"generated/{safe_user_id}/")
    result = []
    
    try:
        if directory.exists() and directory.is_dir():
            # Iterate through all genre directories for this user
            for genre_dir in directory.iterdir():
                if genre_dir.is_dir():
                    genre = genre_dir.name
                    
                    # Iterate through all MIDI files in genre directory
                    for file_path in genre_dir.glob('*.mid'):
                        try:
                            # Parse filename (format: songName_timestamp.mid)
                            filename = file_path.stem
                            parts = filename.rsplit('_', 1)  # Split on last underscore
                            
                            if len(parts) == 2:
                                song_name = parts[0].replace('_', ' ')  # Convert underscores to spaces
                                
                                # Initialize metadata with default values
                                total_time = 0
                                qpm = 120  # Default QPM
                                steps_per_chord = 8  # Default steps
                                
                                try:
                                    # Read MIDI file metadata
                                    midi_file = mido.MidiFile(file_path)
                                    total_time = math.floor(midi_file.length)
                                    
                                    # Get QPM (tempo) from MIDI file
                                    for track in midi_file.tracks:
                                        for msg in track:
                                            if msg.type == 'set_tempo':
                                                # Convert microseconds per beat to QPM
                                                qpm = round(60000000 / msg.tempo)
                                                break
                                    
                                    # Get time signature (steps per chord)
                                    for track in midi_file.tracks:
                                        for msg in track:
                                            if msg.type == 'time_signature':
                                                steps_per_chord = msg.numerator
                                                break
                                    
                                except Exception as e:
                                    pass  # Use default values if MIDI parsing fails
                                
                                # Add to result
                                result.append({
                                    "user_id": safe_user_id,
                                    "song_name": song_name,
                                    "genre": genre,
                                    "total_time": total_time,
                                    "qpm": qpm,
                                    "steps_per_chord": steps_per_chord,
                                    "file_name": str(filename)
                                })
                            
                        except Exception as e:
                            continue  # Skip files that don't match pattern

            if result:
                return result
            return {"error": "No generated music found for this user."}
        return {"error": "User directory not found."}
    
    except Exception as e:
        return {"error": f"Error accessing files: {str(e)}"}

@app.get("/getTrack/{user_id}/{genre}/{file_name}")
async def get_user_generated_file(user_id: str, genre: str, file_name: str):
    """Get a specific generated file for a user"""
    # Sanitize inputs
    safe_user_id = "".join(c for c in user_id if c.isalnum() or c in ('-', '_'))
    safe_file_name = "".join(c for c in file_name if c.isalnum() or c in ('.', '-', '_'))
    safe_genre = "".join(c for c in genre if c.isalnum() or c in ('-', '_'))
    
    if not safe_user_id or not safe_file_name:
        return {"error": "Invalid user ID or file name"}
    
    # Construct safe path
    file_path = Path(f"generated/{safe_user_id}/{safe_genre}/{safe_file_name}")
    
    # Security checks
    try:
        if not file_path.exists():
            return {"error": "File not found."}
        if not file_path.is_file():
            return {"error": "Invalid file path."}
        # Ensure the path is within our intended directory
        if not file_path.resolve().as_posix().startswith(Path("generated").resolve().as_posix()):
            return {"error": "Access denied."}
            
        return FileResponse(
            file_path,
            media_type="audio/midi",
            filename=safe_file_name
        )
    except Exception as e:
        return {"error": "Error accessing file"}
    
if __name__ == '__main__':
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True, workers=8)
