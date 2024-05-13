package com.tcz.listen.repositories;

import com.tcz.listen.models.Lobby;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyRepository extends CrudRepository<Lobby, Long> {
    Optional<Lobby> findByCode(String code);
}
