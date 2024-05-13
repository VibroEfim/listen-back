package com.tcz.listen.services;

import com.tcz.listen.enums.UserState;
import com.tcz.listen.messages.PlayerStateUpdateMessage;
import com.tcz.listen.messages.UpdateSongQueueMessage;
import com.tcz.listen.messages.UpdateUserMessage;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.models.SongQueue;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.LobbyRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.messages.WebSocketMessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Service
public class WebSocketService {
    private static HashMap<String, WebSocketSession> webSocketSessions = new HashMap<>();
    private static HashMap<String, String> playerControllers = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AudioService audioService;
    @Autowired
    private SongService songService;

    public boolean handshake(String token, WebSocketSession webSocketSession) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        user.setState(UserState.LISTENING);
        user.setSimpSessionId(webSocketSession.getId());
        userRepository.save(user);
        webSocketSessions.put(webSocketSession.getId(), webSocketSession);

        if (user.getLobby() != null) {
            Lobby lobby = lobbyRepository.findById(user.getLobby().getId()).get();

            // for everyone
            sendToLobby(lobby, new UpdateUserMessage(user));

            for (User sendingUser : lobby.getUsers()) {
                send(user, new UpdateUserMessage(sendingUser));
            }

            for (SongQueue songQueue : lobby.getSongsList()) {
                send(user, new UpdateSongQueueMessage(
                        songQueue,
                        "join",
                        audioService.getDuration(songQueue.getSong()),
                        songService.getLikeIdIfLiked(user, songQueue.getSong()))
                );
            }

            send(user, new PlayerStateUpdateMessage(lobby));

            // update
            updateSessionsInLobby(user.getLobby());
        }

        return true;
    }

    public void handle(String simpSessionId, String message) {
        if (!webSocketSessions.containsKey(simpSessionId)) {
            return;
        }

        Optional<User> userOptional = userRepository.findBySimpSessionId(simpSessionId);

        if (userOptional.isEmpty()) {
            return;
        }

        playerService.handle(this, userOptional.get(), WebSocketMessageBody.parse(message));
    }

    public void close(String token, WebSocketSession webSocketSession) {
        Optional<User> userOptional = userRepository.findByToken(token);

        webSocketSessions.remove(webSocketSession.getId());

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();
        Lobby lobby = user.getLobby();

        user.setSimpSessionId(null);
        user.setState(UserState.DISCONNECTED);
        userRepository.save(user);

        if (lobby != null) {
            sendToLobby(lobby, new UpdateUserMessage(user));
        }
    }

    public void updateSessionsInLobby(Lobby lobby) {
        for (User user : lobby.getUsers()) {
            if (user.getSimpSessionId() != null && !webSocketSessions.containsKey(user.getSimpSessionId())) {
                //user.setSimpSessionId(null);
                //user.setState(UserState.DISCONNECTED);
                //userRepository.save(user);

                //sendToLobby(lobby, new UpdateUserMessage(user));
            }
        }
    }

    public void sendToLobby(Lobby lobby, WebSocketMessageBody message) {
        sendToLobby(lobby, message, null);
    }

    public void sendToLobby(Lobby lobby, WebSocketMessageBody message, User except) {
        Iterable<User> users = lobby.getUsers();

        for (User user : users) {
            if (user.getSimpSessionId() != null && (except == null || !Objects.equals(except.getId(), user.getId())))
                send(user, message);
        }
    }

    public void send(User user, WebSocketMessageBody message) {
        send(user.getSimpSessionId(), message);
    }

    public void send(String simpSessionId, WebSocketMessageBody message) {
        if (webSocketSessions.containsKey(simpSessionId)) {
            try {
                webSocketSessions.get(simpSessionId).sendMessage(new TextMessage(message.toString()));
            } catch (IOException e) {
                System.out.println("Error with sending message to lobby: " + e.getMessage());
            }
        }
    }
}
