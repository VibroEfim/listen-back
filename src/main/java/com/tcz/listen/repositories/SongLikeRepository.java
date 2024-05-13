package com.tcz.listen.repositories;

import com.tcz.listen.models.SongLike;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SongLikeRepository extends CrudRepository<SongLike, Long> {
    public Optional<SongLike> findByUserIdAndSongId(Long userId, Long SongId);
}
