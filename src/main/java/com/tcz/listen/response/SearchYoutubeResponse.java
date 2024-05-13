package com.tcz.listen.response;

public class SearchYoutubeResponse extends Response {
    private String url;
    private String title;
    private String author;
    private String length;
    private String size;

    public SearchYoutubeResponse(String url, String title, String author, String length, String size) {
        this.url = url;
        this.title = title;
        this.author = author;
        this.length = length;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
