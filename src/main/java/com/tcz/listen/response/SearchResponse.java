package com.tcz.listen.response;

public class SearchResponse extends Response {
    private String type;
    private SongResponse song;
    private SearchYoutubeResponse youtubeResult;

    public SearchResponse(SongResponse song) {
        this.type = "song";
        this.song = song;
    }

    public SearchResponse(SearchYoutubeResponse youtubeResult) {
        this.type = "youtube";
        this.youtubeResult = youtubeResult;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SongResponse getSong() {
        return song;
    }

    public void setSong(SongResponse song) {
        this.song = song;
    }

    public SearchYoutubeResponse getYoutubeResult() {
        return youtubeResult;
    }

    public void setYoutubeResult(SearchYoutubeResponse youtubeResult) {
        this.youtubeResult = youtubeResult;
    }
}
