package com.tcz.listen.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class SongQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long lobbyId;
    private Long queuePosition = 0L;
    @ManyToOne(targetEntity = Song.class, fetch = FetchType.EAGER)
    @JoinColumn(name="song_id")
    private Song song;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public SongQueue() {
    }

    public SongQueue(Song song, Long lobbyId) {
        this.setSong(song);
        this.setLobbyId(lobbyId);
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Long getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Long queuePosition) {
        this.queuePosition = queuePosition;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
