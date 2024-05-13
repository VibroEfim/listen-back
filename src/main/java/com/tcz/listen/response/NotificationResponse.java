package com.tcz.listen.response;

public class NotificationResponse extends Response {
    private String message;

    public NotificationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
