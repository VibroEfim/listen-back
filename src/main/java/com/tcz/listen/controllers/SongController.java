package com.tcz.listen.controllers;

import com.tcz.listen.models.Author;
import com.tcz.listen.models.Song;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.SongLikeRepository;
import com.tcz.listen.repositories.SongRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import com.tcz.listen.response.SongResponse;
import com.tcz.listen.services.AuthorService;
import com.tcz.listen.services.DamerauLevenshteinService;
import com.tcz.listen.services.SongService;
import com.tcz.listen.services.YoutubeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("api/song")
public class SongController {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private SongService songService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongLikeRepository songLikeRepository;
    @Autowired
    private DamerauLevenshteinService damerauLevenshteinService;
    @Autowired
    private YoutubeApiService youtubeApiService;

    @GetMapping("/{id}/")
    public ResponseEntity<Response> get(@PathVariable(value = "id")Long id) {
        Optional<Song> songOptional = songRepository.findById(id);

        if (songOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Song with this id is not exist."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new SongResponse(songOptional.get()), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Response> put(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @RequestParam String name,
            @RequestParam String author,
            @RequestParam("file")MultipartFile file
    ) {
        if (token.equals("no_token")) {
            return new ResponseEntity<>(new NotificationResponse("Token isn't present."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Only authorized users can upload songs."), HttpStatus.UNAUTHORIZED);
        }

        Author authorEntity = authorService.getOrCreate(author);
        for (Song song : authorEntity.getSongs()) {
            if (Objects.equals(song.getName(), name)) {
                return new ResponseEntity<>(new NotificationResponse("Song with this name already attached to " + author), HttpStatus.BAD_REQUEST);
            }
        }

        String path = songService.upload(file);
        if (path.startsWith("error:")) {
            return new ResponseEntity<>(new NotificationResponse("Can't add song. File problem, " + path), HttpStatus.BAD_REQUEST);
        }

        Song song = new Song(userOptional.get(), authorEntity, name, path);
        songRepository.save(song);
        return new ResponseEntity<>(new SongResponse(song), HttpStatus.OK);
    }

    @PutMapping("/yt/")
    public ResponseEntity<Response> putYoutube(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @RequestParam String code
    ) {
        if (token.equals("no_token")) {
            return new ResponseEntity<>(new NotificationResponse("Token isn't present."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Only authorized users can upload songs."), HttpStatus.UNAUTHORIZED);
        }

        Song song = youtubeApiService.getSongFromVideo(code);
        song.setUploader(userOptional.get());
        songRepository.save(song);
        return new ResponseEntity<>(new NotificationResponse("Song downloaded and added from YouTube."),HttpStatus.OK);
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<FileSystemResource> fileSong(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "id")String id
    ) {
        Optional<Song> songOptional = songRepository.findById(Long.valueOf(id));

        if (songOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        FileSystemResource resource = new FileSystemResource(songOptional.get().getPath());

        MediaType mediaType = MediaTypeFactory
                .getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        ContentDisposition disposition = ContentDisposition
                .inline()
                .filename("listen-song.mp3")
                .build();
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
