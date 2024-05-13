package com.tcz.listen.controllers;

import com.tcz.listen.models.Author;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.AuthorRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.*;
import com.tcz.listen.services.DamerauLevenshteinService;
import com.tcz.listen.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/author")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private DamerauLevenshteinService damerauLevenshteinService;
    @Autowired
    private SongService songService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{name}/")
    public ResponseEntity<Response> user(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @PathVariable(value = "name")String name,
            @Param(value = "detailed") String detailed
    ) {
        Optional<Author> authorOptional = authorRepository.findByName(name);

        boolean isDetailed = Objects.equals(detailed, "true");

        if (authorOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Author with name " + name + " is not exist."), HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOptional = userRepository.findByToken(token);
        AuthorResponse authorResponse = new AuthorResponse(authorOptional.get(), isDetailed);
        if (userOptional.isPresent() && isDetailed) {
            songService.addInfoAboutSongs(userOptional.get(), authorResponse.getSongs());
        }

        return new ResponseEntity<>(authorResponse, HttpStatus.OK);
    }

    @GetMapping("/search/")
    public ResponseEntity<List<AuthorResponse>> search(@RequestParam String search) {
        List<Pair<Integer, Author>> pairList = new ArrayList<>();
        for (Author author : authorRepository.findAll()) {
            String text = author.getName().toLowerCase();
            int distance = damerauLevenshteinService.calculateDistance(search.toLowerCase(), text);

            if (distance < text.length())
                pairList.add(Pair.of(distance, author));
        }

        pairList.sort(Comparator.comparingInt(Pair::getFirst));

        List<AuthorResponse> authors = pairList.stream().map(Pair::getSecond).map(author -> new AuthorResponse(author, false)).toList();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }
}
