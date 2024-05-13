package com.tcz.listen.models;

import com.tcz.listen.enums.UserState;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortComparator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = Lobby.class, cascade = CascadeType.ALL)
    @JoinColumn(name="lobby_id")
    private Lobby lobby;

    @OneToMany(targetEntity = Song.class, cascade = CascadeType.ALL)
    @JoinColumn(name="users_id", referencedColumnName = "id")
    @OrderBy("created_at DESC")
    private List<Song> songs = new ArrayList<>();

    @OneToMany(targetEntity = SongLike.class, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    @OrderBy("created_at DESC")
    private List<SongLike> songLikes = new ArrayList<>();

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private String name;
    private String password;
    private String token;
    private String simpSessionId;

    private UserState state;

    public User() {
    }

    public User(String name, String password, String token) {
        this.name = name;
        this.password = password;
        this.token = token;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }

    public void setSimpSessionId(String simpSessionId) {
        this.simpSessionId = simpSessionId;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getLikedSongs() {
        return songLikes.stream().map(SongLike::getSong).toList();
    }

    public List<SongLike> getSongLikes() {
        return songLikes;
    }

    public void setSongLikes(List<SongLike> songLikes) {
        this.songLikes = songLikes;
    }
}
