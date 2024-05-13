package com.tcz.listen.controllers;

import com.tcz.listen.messages.RemoveSongQueueMessage;
import com.tcz.listen.messages.UpdateSongQueueMessage;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.models.Song;
import com.tcz.listen.models.SongQueue;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.LobbyRepository;
import com.tcz.listen.repositories.SongQueueRepository;
import com.tcz.listen.repositories.SongRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import com.tcz.listen.services.AudioService;
import com.tcz.listen.services.SongService;
import com.tcz.listen.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/queue")
public class QueueController {
    @Autowired
    private SongQueueRepository songQueueRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private AudioService audioService;
    @Autowired
    private SongService songService;
    @Autowired
    private LobbyRepository lobbyRepository;

    @PutMapping("/")
    public ResponseEntity<Response> put(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @RequestParam Long id
    ) {
        if (token.equals("no_token")) {
            return new ResponseEntity<>(new NotificationResponse("Token isn't present."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Only authorized users can push songs to queue."), HttpStatus.UNAUTHORIZED);
        }

        if (userOptional.get().getLobby() == null) {
            return new ResponseEntity<>(new NotificationResponse("You are not in lobby"), HttpStatus.BAD_REQUEST);
        }

        Optional<Song> songOptional = songRepository.findById(id);

        if (songOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Song with this id is not exist."), HttpStatus.BAD_REQUEST);
        }

        Optional<SongQueue> songQueueOptional = songQueueRepository.findByLobbyIdAndSongId(userOptional.get().getLobby().getId(), id);

        if (songQueueOptional.isPresent()) {
            return new ResponseEntity<>(new NotificationResponse("Song already in queue."), HttpStatus.BAD_REQUEST);
        }

        SongQueue songQueue = new SongQueue(songOptional.get(), userOptional.get().getLobby().getId());
        songQueue.setQueuePosition(userOptional.get().getLobby().getNextSongPosition());
        songQueueRepository.save(songQueue);

        for (User activeUser : userOptional.get().getLobby().getActiveUsers()) {
            webSocketService.send(activeUser, new UpdateSongQueueMessage(
                    songQueue,
                    "add",
                    audioService.getDuration(songOptional.get()),
                    songService.getLikeIdIfLiked(activeUser, songOptional.get())
            ));
        }

        return new ResponseEntity<>(new NotificationResponse("Song added"), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<Response> delete(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @RequestParam Long id
    ) {
        if (token.equals("no_token")) {
            return new ResponseEntity<>(new NotificationResponse("Token isn't present."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Only authorized users can push songs to queue."), HttpStatus.UNAUTHORIZED);
        }

        if (userOptional.get().getLobby() == null) {
            return new ResponseEntity<>(new NotificationResponse("You are not in lobby"), HttpStatus.BAD_REQUEST);
        }

        Optional<Song> songOptional = songRepository.findById(id);

        if (songOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Song with this id is not exist."), HttpStatus.BAD_REQUEST);
        }

        Optional<SongQueue> songQueueOptional = songQueueRepository.findByLobbyIdAndSongId(userOptional.get().getLobby().getId(), id);

        if (songQueueOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Song not in queue."), HttpStatus.BAD_REQUEST);
        }

        Long position = songQueueOptional.get().getQueuePosition();

        for (SongQueue songQueue : songQueueRepository.findAllByLobbyId(userOptional.get().getLobby().getId())) {
            if (position < 0) {
                if (songQueue.getQueuePosition() < position) {
                    songQueue.setQueuePosition(songQueue.getQueuePosition() + 1);
                    songQueueRepository.save(songQueue);
                }
            } else {
                if (songQueue.getQueuePosition() > position) {
                    songQueue.setQueuePosition(songQueue.getQueuePosition() - 1);
                    songQueueRepository.save(songQueue);
                }
            }
        }

        if (position == 0L) {
            Lobby lobby = userOptional.get().getLobby();
            lobby.setCurrentSong(null);
            lobbyRepository.save(lobby);
        }

        songQueueRepository.deleteById(songQueueOptional.get().getId());
        webSocketService.sendToLobby(userOptional.get().getLobby(), new RemoveSongQueueMessage(
                songQueueOptional.get(),
                "add"
        ));

        return new ResponseEntity<>(new NotificationResponse("Song removed."), HttpStatus.OK);
    }
}
