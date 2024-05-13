package com.tcz.listen.messages;

import com.tcz.listen.models.User;

public class DisconnectMessage extends WebSocketMessageBody {
    public DisconnectMessage(User user) {
        super();

        put("command", "disconnect");
        put("user", user.getName());
    }
}
