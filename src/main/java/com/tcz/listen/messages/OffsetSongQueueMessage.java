package com.tcz.listen.messages;

public class OffsetSongQueueMessage extends WebSocketMessageBody {

    public OffsetSongQueueMessage(long length, long min, long max) {
        put("command", "songQueueOffset");
        put("length", ""+length);
        put("min", ""+min);
        put("max", ""+max);

    }

    public OffsetSongQueueMessage(long length) {
        put("command", "songQueueOffset");
        put("length", ""+length);

    }
}
