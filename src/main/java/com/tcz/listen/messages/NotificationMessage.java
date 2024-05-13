package com.tcz.listen.messages;

import com.tcz.listen.enums.NotificationType;
import com.tcz.listen.models.User;

public class NotificationMessage extends WebSocketMessageBody {
    public NotificationMessage(String message) {
        this(message, NotificationType.INFO);
    }

    public NotificationMessage(String message, NotificationType type) {
        super();

        put("command", "notification");
        put("message", message);
        put("messageType", type.getValue());
    }
}
