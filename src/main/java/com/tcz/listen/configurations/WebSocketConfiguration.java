package com.tcz.listen.configurations;

import com.tcz.listen.controllers.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Autowired
    private WebSocketController webSocketController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getLobbyWebSocketHandler(), "/socket/")
                .setAllowedOriginPatterns("*");
    }

    public WebSocketHandler getLobbyWebSocketHandler() {
        return webSocketController;
    }
}
