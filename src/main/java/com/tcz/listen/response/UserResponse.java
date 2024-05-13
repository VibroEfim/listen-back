package com.tcz.listen.response;

import com.tcz.listen.enums.UserState;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.models.Song;
import com.tcz.listen.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponse extends Response {
    private Long id;
    private String name;
    private LobbyResponse lobby;
    private String lobbyCode;
    private LocalDateTime registered;
    private List<SongResponse> uploadedSongs;
    private List<SongResponse> likedSongs;
    private String token;
    private UserState state;

    public UserResponse(User user) {
        this(user, false);
    }

    public UserResponse(User user, boolean detailed) {
        this(user, detailed, false);
    }

    public UserResponse(User user, boolean detailed, boolean withToken) {
        name = user.getName();
        registered = user.getCreatedAt();
        id = user.getId();
        state = user.getState();

        if (detailed) {
            uploadedSongs = user.getSongs().subList(0, Math.min(user.getSongs().size(), 4)).stream().map(SongResponse::new).toList();
            likedSongs = user.getSongLikes().subList(0, Math.min(user.getSongLikes().size(), 4)).stream().map(SongResponse::new).toList();
        }

        if (user.getLobby() != null) {
            if (detailed) {
                lobby = new LobbyResponse(user.getLobby(), false);
            } else {
                lobbyCode = user.getLobby().getCode();
            }
        }

        if (withToken) {
            token = user.getToken();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LobbyResponse getLobby() {
        return lobby;
    }

    public void setLobby(LobbyResponse lobby) {
        this.lobby = lobby;
    }

    public LocalDateTime getRegistered() {
        return registered;
    }

    public void setRegistered(LocalDateTime registered) {
        this.registered = registered;
    }

    public List<SongResponse> getUploadedSongs() {
        return uploadedSongs;
    }

    public void setUploadedSongs(List<SongResponse> uploadedSongs) {
        this.uploadedSongs = uploadedSongs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public List<SongResponse> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(List<SongResponse> likedSongs) {
        this.likedSongs = likedSongs;
    }
}
