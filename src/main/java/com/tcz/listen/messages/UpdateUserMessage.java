package com.tcz.listen.messages;

import com.tcz.listen.models.User;

public class UpdateUserMessage extends WebSocketMessageBody {

    public UpdateUserMessage(User user) {
        super();

        put("command", "updateUser");
        put("user", user.getName());
        put("state", user.getState().toString());
    }
}
