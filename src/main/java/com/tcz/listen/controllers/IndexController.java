package com.tcz.listen.controllers;

import com.tcz.listen.models.User;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import com.tcz.listen.response.UserResponse;
import com.tcz.listen.services.YoutubeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api")
public class IndexController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YoutubeApiService youtubeApiService;

    @GetMapping("/ping/")
    public ResponseEntity<Response> ping() {
        return new ResponseEntity<>(new NotificationResponse("pong"), HttpStatus.OK);
    }
}