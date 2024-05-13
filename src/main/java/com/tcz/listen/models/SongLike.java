package com.tcz.listen.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class SongLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.DETACH)
    private User user;

    @ManyToOne(targetEntity = Song.class, cascade = CascadeType.DETACH)
    private Song song;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public SongLike() {
    }

    public SongLike(User user, Song song) {
        this.user = user;
        this.song = song;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
