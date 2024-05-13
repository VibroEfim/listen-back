package com.tcz.listen.controllers;

import com.tcz.listen.models.User;
import com.tcz.listen.repositories.UserRepository;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import com.tcz.listen.response.UserResponse;
import com.tcz.listen.services.TokenCookieService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenCookieService tokenCookieService;

    @PostMapping("login/")
    public ResponseEntity<Response> login(@RequestParam String name, @RequestParam String password, HttpServletResponse response) {
        Optional<User> userOptional = userRepository.findByName(name);

        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return new ResponseEntity<>(new NotificationResponse("Wrong name or password."), HttpStatus.BAD_REQUEST);
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();

        user.setToken(token);
        userRepository.save(user);

        response.addCookie(tokenCookieService.create(token));
        return new ResponseEntity<>(new UserResponse(user, false, true), HttpStatus.OK);
    }

    @PostMapping("register/")
    public ResponseEntity<Response> register(@RequestParam String name, @RequestParam String password, HttpServletResponse response) {
        Optional<User> userOptional = userRepository.findByName(name);

        if (!userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("User with this name already exist."), HttpStatus.BAD_REQUEST);
        }

        User user = new User(name, passwordEncoder.encode(password), UUID.randomUUID().toString());
        userRepository.save(user);

        response.addCookie(tokenCookieService.create(user.getToken()));

        return new ResponseEntity<>(new UserResponse(user, false, true), HttpStatus.OK);
    }

    @GetMapping("checkToken/")
    public ResponseEntity<Response> checkToken(@RequestHeader(value = "AuthToken", defaultValue = "no_token") String token) {
        if (Objects.equals(token, "no_token")) {
            return new ResponseEntity<>(new NotificationResponse("Token isn't present."), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new NotificationResponse("Old or wrong token. Redirecting to login..."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new UserResponse(userOptional.get(), false), HttpStatus.OK);
    }
}
