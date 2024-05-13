package com.tcz.listen.response;

import com.tcz.listen.models.Song;
import com.tcz.listen.models.SongLike;
import com.tcz.listen.models.SongQueue;

import java.time.LocalDateTime;

public class SongResponse extends Response {
    private Long id;
    private String name;
    private int views;
    private String author;
    private String uploader;
    private LocalDateTime uploaded;
    private String url;
    private Long likeId;
    private Long queueId;
    private Long position;

    public SongResponse(SongQueue songQueue, SongLike songLike) {
        this(songQueue.getSong());

        queueId = songQueue.getId();
        likeId = songLike.getId();
    }

    public SongResponse(SongQueue songQueue) {
        this(songQueue.getSong());

        queueId = songQueue.getId();
    }

    public SongResponse(SongLike songLike) {
        this(songLike.getSong());

        likeId = songLike.getId();
    }

    public SongResponse(Song song) {
        id = song.getId();
        name = song.getName();
        views = song.getViews();
        author = song.getAuthor().getName();
        uploader = song.getUploader().getName();
        uploaded = song.getCreatedAt();
        url = song.getPath();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public LocalDateTime getUploaded() {
        return uploaded;
    }

    public void setUploaded(LocalDateTime uploaded) {
        this.uploaded = uploaded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLikeId() {
        return likeId;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}
