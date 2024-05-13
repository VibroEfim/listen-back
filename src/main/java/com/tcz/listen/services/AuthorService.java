package com.tcz.listen.services;

import com.tcz.listen.models.Author;
import com.tcz.listen.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Author getOrCreate(String name) {
        Optional<Author> authorOptional = authorRepository.findByName(name);

        if (authorOptional.isPresent()) {
            return authorOptional.get();
        }

        Author authorEntity = new Author(name);
        authorRepository.save(authorEntity);

        return authorEntity;
    }
}
