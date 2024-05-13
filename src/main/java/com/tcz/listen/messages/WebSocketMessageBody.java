package com.tcz.listen.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketMessageBody {
    private final HashMap<String, String> values = new HashMap<>();

    public WebSocketMessageBody put(String field, String value) {
        values.put(field, value);

        return this;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            out.append(field).append("=").append(value).append(";");
        }

        return out.toString();
    }

    public static WebSocketMessageBody parse(String message) {
        List<String> values = List.of(message.split(";"));
        WebSocketMessageBody webSocketMessageBody = new WebSocketMessageBody();

        for (String text : values) {
            String[] args = text.split("=");

            webSocketMessageBody.put(args[0], args[1]);
        }

        return webSocketMessageBody;
    }

    public String get(String field) {
        return values.get(field);
    }
}
