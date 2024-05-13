package com.tcz.listen.controllers;

import com.tcz.listen.models.Song;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.SongRepository;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.SearchResponse;
import com.tcz.listen.response.SongResponse;
import com.tcz.listen.services.DamerauLevenshteinService;
import com.tcz.listen.services.SongService;
import com.tcz.listen.services.YoutubeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/search/")
public class SearchController {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private DamerauLevenshteinService damerauLevenshteinService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongService songService;
    @Autowired
    private YoutubeApiService youtubeApiService;

    @PostMapping("/")
    public ResponseEntity<List<SearchResponse>> search(
            @RequestHeader(value = "AuthToken", defaultValue = "no_token") String token,
            @RequestParam String prompt
    ) {
        List<Pair<Double, Song>> pairList = new ArrayList<>();
        for (Song song : songRepository.findAll()) {
            String text = song.getAuthor().getName().toLowerCase() + " " + song.getName().toLowerCase();
            double distance = damerauLevenshteinService.test(prompt.toLowerCase(), text);

            if (distance < 1)
                pairList.add(Pair.of(distance, song));
        }

        pairList.sort(Comparator.comparingDouble(Pair::getFirst));
        List<SongResponse> songResponses = pairList.stream().map(Pair::getSecond).map(SongResponse::new).toList();
        songResponses = songResponses.subList(0, Math.min(songResponses.size(), 3));
        List<SongResponse> finalSongResponses = songResponses;

        // Если отсылка с токеном -> отправляет инфу о лайке
        Optional<User> userOptional = userRepository.findByToken(token);
        userOptional.ifPresent(user -> songService.addInfoAboutSongs(user, finalSongResponses));

        List<SearchResponse> searchResponses = new ArrayList<>();
        for (SongResponse finalSongResponse : finalSongResponses) {
            searchResponses.add(new SearchResponse(finalSongResponse));
        }

        List<SearchResponse> searchYoutubeResponses = youtubeApiService.search(prompt, userOptional.get());
        searchResponses.addAll(searchYoutubeResponses);

        return new ResponseEntity<>(searchResponses, HttpStatus.OK);
    }
}
