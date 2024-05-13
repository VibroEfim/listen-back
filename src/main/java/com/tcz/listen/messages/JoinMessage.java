package com.tcz.listen.messages;

import com.tcz.listen.models.User;

public class JoinMessage extends WebSocketMessageBody {
    public JoinMessage(User user) {
        super();

        put("command", "join");
        put("user", user.getName());
        put("state", user.getState().toString());
    }
}
