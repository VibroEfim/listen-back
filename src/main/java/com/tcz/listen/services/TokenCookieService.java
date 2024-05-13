package com.tcz.listen.services;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class TokenCookieService {
    public Cookie create(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }
}
