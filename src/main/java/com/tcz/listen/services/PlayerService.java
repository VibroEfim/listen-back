package com.tcz.listen.services;

import com.tcz.listen.enums.UserState;
import com.tcz.listen.messages.OffsetSongQueueMessage;
import com.tcz.listen.messages.PlayerStateUpdateMessage;
import com.tcz.listen.messages.UpdateUserMessage;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.models.Song;
import com.tcz.listen.models.SongQueue;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.LobbyRepository;
import com.tcz.listen.messages.WebSocketMessageBody;
import com.tcz.listen.repositories.SongQueueRepository;
import com.tcz.listen.repositories.SongRepository;
import com.tcz.listen.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PlayerService {

    @Autowired
    public LobbyRepository lobbyRepository;

    @Autowired
    public SongQueueRepository songQueueRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public SongRepository songRepository;

    @Autowired
    public AudioService audioService;

    public void userVolume(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        float volume = Float.parseFloat(message.get("value"));

        if (volume <= 0 && user.getState() != UserState.MUTED) {
            user.setState(UserState.MUTED);
        } else if (volume > 0 && user.getState() == UserState.MUTED) {
            user.setState(UserState.LISTENING);
        }

        userRepository.save(user);
        webSocketService.sendToLobby(user.getLobby(), new UpdateUserMessage(user));
    }
    /**
     * Отправка сообщений через вебсокетсервис
     */
    public void update(WebSocketService webSocketService, Lobby lobby, Long rate) {

        if (lobby.getCurrentSong() == null) {
            loadCurrentSong(webSocketService, lobby);
        }

        if (lobby.getCurrentSong() != null && lobby.getCurrentSong().getQueuePosition() != 0L) {
            loadCurrentSong(webSocketService, lobby);
        }

        if (lobby.isPlaying() && lobby.getCurrentSong() != null) {
            lobby.setTime(lobby.getTime() + rate);

            // Достаточно долго послушали
            if (lobby.getTime() > lobby.getMaxTime() / 2 && !lobby.isAddedViewForCurrentSong()) {
                Song song = lobby.getCurrentSong().getSong();
                int amountOfUsers = lobby.getActiveUsers().size();
                song.setViews(song.getViews() + amountOfUsers);
                songRepository.save(song);
                lobby.setAddedViewForCurrentSong(true);

                System.out.println("Added " + amountOfUsers + " views to " + song.getName());
            }

            if (lobby.getTime() >= lobby.getMaxTime()) {
                moveUpQueue(webSocketService, lobby);
                loadCurrentSong(webSocketService, lobby);
            }

            lobbyRepository.save(lobby);
            webSocketService.sendToLobby(lobby, new PlayerStateUpdateMessage(lobby));
        }
    }

    // 0 <- billie eilish 1 <- lana del ray => -1 <- billie eilish 0 <- lana del ray
    public void moveUpQueue(WebSocketService webSocketService, Lobby lobby) {
        offsetQueue(webSocketService, lobby, -1);
    }

    private void offsetQueue(WebSocketService webSocketService, Lobby lobby, long length) {
        for (int i = 0; i < lobby.getSongsList().size(); i++) {
            SongQueue songQueueToMove = lobby.getSongsList().get(i);
            songQueueToMove.setQueuePosition(songQueueToMove.getQueuePosition() + length);

            songQueueRepository.save(songQueueToMove);
        }
        webSocketService.sendToLobby(lobby, new OffsetSongQueueMessage(length));
    }

    public void loadCurrentSong(WebSocketService webSocketService, Lobby lobby) {
        HashMap<Long, SongQueue> queue = lobby.getSongsListAsHashMap();

        // Empty queue
        if (queue.size() == 0) {
            return;
        }

        if (queue.get(0L) == null) {
            // ЖИДКАЯ ХУЙНЯ, МОЖЕТ НАКАЗАТЬ КАКТО ПОТОМ BUGGY THING
            if (queue.get(-1L) != null) {
                offsetQueue(webSocketService, lobby, queue.size());
            } else {
                offsetQueue(webSocketService, lobby, -queue.size());
            }

            queue = lobby.getSongsListAsHashMap();
        }

        lobby.setCurrentSong(queue.get(0L));
        lobby.setTime(0L);
        lobby.setMaxTime(audioService.getDuration(queue.get(0L).getSong()));

        lobby.setAddedViewForCurrentSong(false);
        lobbyRepository.save(lobby);
    }

    public void handle(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        switch (message.get("command")) {
            case "play" -> playPause(webSocketService, user, message);
            case "userVolume" -> userVolume(webSocketService, user, message);
            case "next" -> next(webSocketService, user, message);
            case "prev" -> prev(webSocketService, user, message);

            case "setAndPlay" -> set(webSocketService, user, message);
            case "moveTime" -> moveTime(webSocketService, user, message);
            default -> notExist();
        }
    }

    private void set(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        Lobby lobby = user.getLobby();

        if (!lobby.getSongsListAsHashMap().containsKey(Long.parseLong(message.get("position")))) {
            System.out.println("not contains");
            return;
        }

        lobby.setPlaying(true);
        offsetQueue(webSocketService, lobby, -Long.parseLong(message.get("position")));
        loadCurrentSong(webSocketService, lobby);
    }

    private void playPause(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        Lobby lobby = user.getLobby();

        lobby.setPlaying(!lobby.isPlaying());
        lobbyRepository.save(lobby);
        webSocketService.sendToLobby(lobby, new PlayerStateUpdateMessage(lobby));
    }

    private void next(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        offsetQueue(webSocketService, user.getLobby(), -1);
    }

    private void prev(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        if (user.getLobby().getTime() >= 5000) {
            Lobby lobby = user.getLobby();
            lobby.setTime(0L);
            lobbyRepository.save(lobby);

            webSocketService.sendToLobby(lobby, new PlayerStateUpdateMessage(lobby));
            return;
        }
        offsetQueue(webSocketService, user.getLobby(), 1);
    }

    private void moveTime(WebSocketService webSocketService, User user, WebSocketMessageBody message) {
        Lobby lobby = user.getLobby();
        lobby.setTime(Long.valueOf(message.get("value")));
        lobbyRepository.save(lobby);

        webSocketService.sendToLobby(lobby, new PlayerStateUpdateMessage(lobby));
    }

    private void notExist() {

    }
}
