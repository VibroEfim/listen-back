package com.tcz.listen.controllers;

import com.tcz.listen.models.Song;
import com.tcz.listen.models.SongLike;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.SongLikeRepository;
import com.tcz.listen.repositories.SongRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/like/")
public class LikeController {
    @Autowired
    public SongLikeRepository songLikeRepository;
    @Autowired
    public SongRepository songRepository;
    @Autowired
    public UserRepository userRepository;
    @PutMapping("/{id}/")
    public ResponseEntity<Response> like(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "id")Long id
    ) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("You need log in to like song"), HttpStatus.UNAUTHORIZED);
        }

        Optional<Song> songOptional = songRepository.findById(id);

        if (songOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Song is not exist."), HttpStatus.BAD_REQUEST);
        }

        Optional<SongLike> songLikeOptional = songLikeRepository.findByUserIdAndSongId(userOptional.get().getId(), id);

        if (songLikeOptional.isPresent()) {
            return new ResponseEntity<>(new NotificationResponse("Like already exist."), HttpStatus.BAD_REQUEST);
        }

        SongLike songLike = new SongLike(userOptional.get(), songOptional.get());
        songLikeRepository.save(songLike);

        return new ResponseEntity<>(new NotificationResponse("Like added."), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<Response> unlike(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "id")Long id
    ) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("You need log in to like song"), HttpStatus.UNAUTHORIZED);
        }

        Optional<Song> songOptional = songRepository.findById(id);

        if (songOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Song is not exist."), HttpStatus.BAD_REQUEST);
        }

        Optional<SongLike> songLikeOptional = songLikeRepository.findByUserIdAndSongId(userOptional.get().getId(), id);

        if (songLikeOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Like is not exist."), HttpStatus.BAD_REQUEST);
        }

        songLikeRepository.deleteById(songLikeOptional.get().getId());
        return new ResponseEntity<>(new NotificationResponse("Like removed."), HttpStatus.OK);
    }
}
