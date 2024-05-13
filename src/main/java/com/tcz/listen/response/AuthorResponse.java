package com.tcz.listen.response;

import com.tcz.listen.models.Author;

import java.util.ArrayList;
import java.util.List;

public class AuthorResponse extends Response {

    private Long id;
    private String name;
    private List<SongResponse> songs = new ArrayList<>();

    public AuthorResponse(Author author) {
        this(author, false);
    }

    public AuthorResponse(Author author, boolean detailed) {
        id = author.getId();
        name = author.getName();

        if (detailed) {
            songs = author.getSongs().stream().map(SongResponse::new).toList();
        }
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

    public List<SongResponse> getSongs() {
        return songs;
    }

    public void setSongs(List<SongResponse> songs) {
        this.songs = songs;
    }
}
