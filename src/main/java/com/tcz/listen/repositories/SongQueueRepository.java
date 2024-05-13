package com.tcz.listen.repositories;

import com.tcz.listen.models.SongQueue;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SongQueueRepository extends CrudRepository<SongQueue, Long> {
    public Optional<SongQueue> findByLobbyIdAndSongId(Long lobbyId, Long SongId);
    public List<SongQueue> findAllByLobbyId(Long lobbyId);
}
