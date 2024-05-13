package com.tcz.listen.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tcz.listen.controllers.WebSocketController;
import com.tcz.listen.enums.NotificationType;
import com.tcz.listen.messages.NotificationMessage;
import com.tcz.listen.models.Author;
import com.tcz.listen.models.Song;
import com.tcz.listen.models.User;
import com.tcz.listen.repositories.AuthorRepository;
import com.tcz.listen.response.SearchResponse;
import com.tcz.listen.response.SearchYoutubeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.*;

@Service
public class YoutubeApiService {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private WebSocketService webSocketService;

    public List<SearchResponse> search(String prompt, User user) {
        RestClient defaultClient = RestClient.create();

        if (prompt.length() == 0) {
            return new ArrayList<>();
        }

        try {
            ResponseEntity<String> result = defaultClient.post()
                    .uri("http://localhost:9842/search/" + prompt)
                    .retrieve()
                    .toEntity(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();

            List<LinkedHashMap> linkedHashMaps = objectMapper.readValue(result.getBody(), typeFactory.constructType(List.class, LinkedHashMap.class));

            List<SearchResponse> searchYoutubeResponses = new ArrayList<>();
            for (LinkedHashMap data : linkedHashMaps) {
                searchYoutubeResponses.add(new SearchResponse(new SearchYoutubeResponse(
                        data.get("url").toString(),
                        data.get("name").toString(),
                        data.get("author").toString(),
                        data.get("length").toString(),
                        data.get("size").toString()
                )));
            }
            return searchYoutubeResponses;
        } catch (IOException | RestClientException e) {
            if (user != null) {
                webSocketService.send(user, new NotificationMessage("Нет подключения к YouTube API.", NotificationType.WARNING));
            }
            return new ArrayList<>();
        }
    }

    public Song getSongFromVideo(String code) {
        try {
            UUID uuid = UUID.randomUUID();
            RestClient defaultClient = RestClient.create();

            String result = defaultClient.get()
                    .uri("http://localhost:9842/data/?id="+code)
                    .retrieve()
                    .body(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();

            LinkedHashMap<String, String> data = objectMapper.readValue(result, typeFactory.constructType(LinkedHashMap.class));

            String author = parseAuthor(data.get("author"));
            String name = parseNameFromVideoName(data.get("name"), author);


            Song song = new Song();
            song.setName(name);
            song.setPath("songs/"+ uuid +".mp3");
            upload(code, song.getPath());
            song.setAuthor(authorService.getOrCreate(author));

            return song;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean upload(String code, String path) {
        RestClient defaultClient = RestClient.create();

        ResponseEntity<byte[]> responseEntity = defaultClient.get()
                .uri("http://localhost:9842/download/?id="+code)
                .retrieve().toEntity(byte[].class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            byte[] bytes = responseEntity.getBody();

            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(bytes);

                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    public String parseAuthor(String name) {
        if (name.toLowerCase().endsWith(" - topic")) {
            int topicLength = " - topic".length();
            return name.substring(0, name.length() - topicLength);
        }

        return name;
    }

    public String parseNameFromVideoName(String name, String author) {
        String[] authorLC = author.toLowerCase().split(" ");
        String[] nameWords = name.split(" ");

        StringBuilder outName = new StringBuilder();

        boolean bracketsOpened = false;
        for (String word : nameWords) {
            boolean needToAdd = true;
            if (Arrays.stream(authorLC).toList().contains(word.toLowerCase())) {
                needToAdd = false;
            }

            if (word.toLowerCase().startsWith("[") || word.toLowerCase().startsWith("(")) {
                bracketsOpened = true;
                needToAdd = false;
            }

            if (word.toLowerCase().endsWith("]") || word.toLowerCase().endsWith(")")) {
                bracketsOpened = false;
                needToAdd = false;
            }

            if (bracketsOpened || word.equalsIgnoreCase("-") || word.startsWith("—")) {
                needToAdd = false;
            }

            if (needToAdd) {
                outName.append(word).append(" ");
            }
        }

        return outName.toString();
    }

    public String oldParseNameFromVideoName(String name, String author) {
        String parsedName = name;

        if (name.toLowerCase().contains(author.toLowerCase())) {
            parsedName = parsedName.toLowerCase().replaceAll(author.toLowerCase(), "");

            while (parsedName.contains("  ")) {
                parsedName = parsedName.toLowerCase().replaceAll("  ", " ");
            }

            while (parsedName.startsWith(" ") || parsedName.startsWith("-") || parsedName.startsWith("—")) {
                parsedName = parsedName.substring(1, parsedName.length());
            }
        }

        return parsedName.substring(0, 1).toUpperCase() + parsedName.substring(1, parsedName.length()).toLowerCase();
    }
}
