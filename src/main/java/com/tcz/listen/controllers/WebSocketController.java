package com.tcz.listen.controllers;

import com.tcz.listen.enums.UserState;
import com.tcz.listen.models.Lobby;
import com.tcz.listen.repositories.LobbyRepository;
import com.tcz.listen.services.PlayerService;
import com.tcz.listen.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

@Controller
public class WebSocketController extends TextWebSocketHandler {
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerService playerService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String token = getToken(session);

        webSocketService.handshake(token, session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        webSocketService.handle(session.getId(), message.getPayload().toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String token = getToken(session);

        webSocketService.close(token, session);
    }

    private String getToken(WebSocketSession session) {
        if (session.getUri().toString().split("authtkn=").length > 1) {
            return session.getUri().toString().split("authtkn=")[1];
        }

        if (session.getHandshakeHeaders().get("cookie") == null) {
            return "null";
        }

        String[] cookies = Objects.requireNonNull(session.getHandshakeHeaders().get("cookie")).get(0).split("; ");

        String token = "";

        for (String cookie : cookies) {
            String[] args = cookie.split("=");

            if (args.length == 1)
                return "null";

            if (args[0].equals("token")) {
                token = args[1];
            }
        }

        return token;
    }

    @Scheduled(fixedRate = 500L)
    private void updateLobby() {
        for (Lobby lobby : lobbyRepository.findAll()) {
            //if (!lobby.isPlaying() || lobby.getUsers().stream().filter(user -> user.getState() != UserState.DISCONNECTED).toList().size() == 0) {
            //    continue;
            //}

            playerService.update(webSocketService, lobby, 500L);
        }
    }
}
