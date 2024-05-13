package com.tcz.listen.services;

import com.tcz.listen.models.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class AudioService {

    public long getDuration(Song song) {
        long duration = -1;

        try {
            AudioFile audioFile = AudioFileIO.read(new File(song.getPath()));
            duration = (long) (audioFile.getAudioHeader().getPreciseTrackLength() * 1000);
        } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException | IOException e) {
            System.out.println("error with audio file: " + e.getMessage());
        }

        return duration;
    }
}
