package com.tcz.listen.controllers;

import com.tcz.listen.messages.DisconnectMessage;
import com.tcz.listen.messages.JoinMessage;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.LobbyRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.*;
import com.tcz.listen.services.CodeService;
import com.tcz.listen.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/lobby")
public class LobbyController {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeService codeService;

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping("/connect/")
    public ResponseEntity<Response> connectOrCreate(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @RequestParam(value = "code", defaultValue = "NO_LOBBY_CODE")String code
    ) {
        if (Objects.equals(token, "no_token")) {
            return new ResponseEntity<>(new NotificationResponse("No token provided."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Wrong token."), HttpStatus.UNAUTHORIZED);
        }

        Lobby lobby;
        if (Objects.equals(code, "NO_LOBBY_CODE")) {
            lobby = new Lobby(codeService.random());
        } else {
            Optional<Lobby> lobbyOptional = lobbyRepository.findByCode(code);
            if (lobbyOptional.isEmpty()) {
                return new ResponseEntity<>(new NotificationResponse("Lobby with code " + code + " is not exist."), HttpStatus.BAD_REQUEST);
            }
            lobby = lobbyOptional.get();
        }

        lobby.getUsers().add(userOptional.get());
        lobbyRepository.save(lobby);
        userRepository.save(userOptional.get());
        webSocketService.sendToLobby(lobby, new JoinMessage(userOptional.get()), userOptional.get());

        return new ResponseEntity<>(new NotificationResponse(lobby.getCode()), HttpStatus.OK);
    }

    @GetMapping("/{code}/")
    public ResponseEntity<Response> get(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "code")String code
    ) {
        if (Objects.equals(token, "no_token")) {
            return new ResponseEntity<>(new NotificationResponse("No token provided."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Wrong token."), HttpStatus.UNAUTHORIZED);
        }

        Optional<Lobby> lobbyOptional = lobbyRepository.findByCode(code);
        if (lobbyOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Lobby with code " + code + " is not exist."), HttpStatus.BAD_REQUEST);
        }

        if (userOptional.get().getLobby() == null || !Objects.equals(lobbyOptional.get().getId(), userOptional.get().getLobby().getId())) {

            if (userOptional.get().getLobby() != null) {
                webSocketService.sendToLobby(userOptional.get().getLobby(), new DisconnectMessage(userOptional.get()), userOptional.get());
            }

            userOptional.get().setLobby(lobbyOptional.get());
            userRepository.save(userOptional.get());
        }

        return new ResponseEntity<>(new LobbyResponse(lobbyOptional.get(), true), HttpStatus.OK);
    }

    @GetMapping("/disconnect/")
    public ResponseEntity<Response> disconnect(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token
    ) {
        if (Objects.equals(token, "no_token")) {
            return new ResponseEntity<>(new NotificationResponse("No token provided."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Wrong token."), HttpStatus.UNAUTHORIZED);
        }

        if (userOptional.get().getLobby() == null) {
            return new ResponseEntity<>(new NotificationResponse("Already not in lobby."), HttpStatus.BAD_REQUEST);
        }

        userOptional.get().setLobby(null);
        userRepository.save(userOptional.get());

        return new ResponseEntity<>(new UserResponse(userOptional.get(), false), HttpStatus.OK);
    }
}
