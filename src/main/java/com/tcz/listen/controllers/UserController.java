package com.tcz.listen.controllers;

import com.tcz.listen.models.User;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import com.tcz.listen.response.SongResponse;
import com.tcz.listen.response.UserResponse;
import com.tcz.listen.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongService songService;

    @GetMapping("/{name}/")
    public ResponseEntity<Response> user(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "name")String name, @Param(value = "detailed") String detailed
    ) {
        Optional<User> userOptional = userRepository.findByName(name);

        boolean isDetailed = Objects.equals(detailed, "true");

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("User with name " + name + " is not exist."), HttpStatus.BAD_REQUEST);
        }

        Optional<User> userRequester = userRepository.findByToken(token);

        UserResponse userResponse = new UserResponse(userOptional.get(), isDetailed);

        if (userRequester.isPresent()) {
            songService.addInfoAboutSongs(userRequester.get(), userResponse.getLikedSongs());
            songService.addInfoAboutSongs(userRequester.get(), userResponse.getUploadedSongs());
        }

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/{name}/likes/")
    public ResponseEntity userLikes(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "name")String name
    ) {
        Optional<User> userOptional = userRepository.findByName(name);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("User with name " + name + " is not exist."), HttpStatus.BAD_REQUEST);
        }

        List<SongResponse> likedSongs = userOptional.get().getSongLikes().stream().map(SongResponse::new).toList();

        Optional<User> userRequester = userRepository.findByToken(token);
        userRequester.ifPresent(user -> songService.addInfoAboutSongs(user, likedSongs));

        return new ResponseEntity<>(likedSongs, HttpStatus.OK);
    }

    @GetMapping("/{name}/uploads/")
    public ResponseEntity userUploads(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "name")String name
    ) {
        Optional<User> userOptional = userRepository.findByName(name);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("User with name " + name + " is not exist."), HttpStatus.BAD_REQUEST);
        }

        List<SongResponse> songs = userOptional.get().getSongs().stream().map(SongResponse::new).toList();

        Optional<User> userRequester = userRepository.findByToken(token);
        userRequester.ifPresent(user -> songService.addInfoAboutSongs(user, songs));

        return new ResponseEntity<>(songs, HttpStatus.OK);
    }
}
