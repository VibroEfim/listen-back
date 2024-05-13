package com.tcz.listen.response;

import com.tcz.listen.enums.QueueState;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.models.Song;
import com.tcz.listen.models.SongQueue;
import com.tcz.listen.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class LobbyResponse extends Response {
    private String code;
    private SongResponse currentSong;
    private List<SongResponse> songList;
    private QueueState queueState;
    private Long time;
    private Long maxTime;
    private boolean isPlaying;
    private List<UserResponse> users;
    private LocalDateTime createdAt;


    public LobbyResponse(Lobby lobby) {
        this(lobby, false);
    }

    public LobbyResponse(Lobby lobby, boolean detailed) {
        code = lobby.getCode();
        queueState = lobby.getQueueState();
        time = lobby.getTime();
        maxTime = lobby.getMaxTime();
        isPlaying = lobby.isPlaying();
        createdAt = lobby.getCreatedAt();

        if (lobby.getCurrentSong() != null)
            currentSong = new SongResponse(lobby.getCurrentSong().getSong());

        if (detailed) {
            songList = lobby.getSongsList().stream().map(SongResponse::new).toList();
            users = lobby.getUsers().stream().map(user -> new UserResponse(user, false)).toList();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SongResponse getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(SongResponse currentSong) {
        this.currentSong = currentSong;
    }

    public List<SongResponse> getSongList() {
        return songList;
    }

    public void setSongList(List<SongResponse> songList) {
        this.songList = songList;
    }

    public QueueState getQueueState() {
        return queueState;
    }

    public void setQueueState(QueueState queueState) {
        this.queueState = queueState;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
