package com.tcz.listen.repositories;

import com.tcz.listen.models.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {
    Optional<Song> findByName(String name);
}
