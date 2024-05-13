package com.tcz.listen.services;

import com.tcz.listen.models.Song;
import com.tcz.listen.models.SongLike;
import com.tcz.listen.models.SongQueue;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.SongLikeRepository;
import com.tcz.listen.repositories.SongQueueRepository;
import com.tcz.listen.response.SongResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SongService {
    @Autowired
    public SongLikeRepository songLikeRepository;

    @Autowired
    public SongQueueRepository songQueueRepository;

    public String upload(MultipartFile file) {
        String path = "songs/"+ UUID.randomUUID()+".mp3";

        try {
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            return "error: " + e.getMessage();
        }

        return path;
    }

    public List<SongResponse> addInfoAboutSongs(User user, List<SongResponse> songResponses) {
        for (SongResponse songResponse : songResponses) {
            Optional<SongLike> songLikeOptional = songLikeRepository.findByUserIdAndSongId(user.getId(), songResponse.getId());
            songLikeOptional.ifPresentOrElse(songLike -> songResponse.setLikeId(songLike.getId()), () -> songResponse.setLikeId(-1L));

            if (user.getLobby() != null) {
                Optional<SongQueue> songQueueOptional = songQueueRepository.findByLobbyIdAndSongId(user.getLobby().getId(), songResponse.getId());
                songQueueOptional.ifPresent(songQueue -> songResponse.setPosition(songQueue.getQueuePosition()));
            }
        }

        return songResponses;
    }

    public long getLikeIdIfLiked(User user, Song song) {
        Optional<SongLike> songLikeOptional = songLikeRepository.findByUserIdAndSongId(user.getId(), song.getId());
        if(songLikeOptional.isPresent()) {
            return songLikeOptional.get().getId();
        }

        return -1L;
    }
}
