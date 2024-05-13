package com.tcz.listen.models;

import com.tcz.listen.enums.QueueState;
import com.tcz.listen.enums.UserState;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private Long time = 0L;
    private Long maxTime = 0L;
    private boolean addedViewForCurrentSong = false;
    private boolean isPlaying = false;
    private QueueState queueState = QueueState.NO_REPEAT;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(targetEntity = SongQueue.class, cascade = CascadeType.DETACH)
    @JoinColumn(name="current_song_id")
    private SongQueue currentSong;
    @OneToMany(targetEntity = User.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name="lobby_id", referencedColumnName = "id")
    private List<User> users = new ArrayList<>();;
    @OneToMany(targetEntity = SongQueue.class, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name="lobbyId", referencedColumnName = "id")
    @OrderBy("queuePosition")
    private List<SongQueue> songsList = new ArrayList<>();;

    public Lobby() {
    }

    public Lobby(String code) {
        this.code = code;
    }

    public List<SongQueue> getSongsList() {
        return songsList;
    }

    public HashMap<Long, SongQueue> getSongsListAsHashMap() {
        HashMap<Long, SongQueue> songQueueHashMap = new HashMap<>();

        for (SongQueue songQueue : this.getSongsList()) {
            songQueueHashMap.put(songQueue.getQueuePosition(), songQueue);
        }

        return songQueueHashMap;
    }

    // need to test
    public long getNextSongPosition() {
        List<SongQueue> queueList = this.getSongsList();

        if (queueList.isEmpty()) {
            return 0L;
        }

        // Sorting by queue pos
        queueList.sort((o1, o2) -> Math.toIntExact(o1.getQueuePosition() - o2.getQueuePosition()));
        return queueList.get(queueList.size() - 1).getQueuePosition() + 1;
    }

    public void setSongsList(List<SongQueue> songsList) {
        this.songsList = songsList;
    }

    public Long getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<User> getActiveUsers() {
        return users.stream().filter(user -> user.getState() == UserState.LISTENING).toList();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public SongQueue getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(SongQueue currentSong) {
        this.currentSong = currentSong;
    }

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    public QueueState getQueueState() {
        return queueState;
    }

    public void setQueueState(QueueState queueState) {
        this.queueState = queueState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isAddedViewForCurrentSong() {
        return addedViewForCurrentSong;
    }

    public void setAddedViewForCurrentSong(boolean addedViewForCurrentSong) {
        this.addedViewForCurrentSong = addedViewForCurrentSong;
    }
}
