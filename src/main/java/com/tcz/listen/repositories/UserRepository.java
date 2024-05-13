package com.tcz.listen.repositories;

import com.tcz.listen.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByToken(String token);

    Optional<User> findByName(String name);

    Optional<User> findBySimpSessionId(String simpSessionId);

    Iterable<User> findAllByLobbyId(long lobbyId);
}
