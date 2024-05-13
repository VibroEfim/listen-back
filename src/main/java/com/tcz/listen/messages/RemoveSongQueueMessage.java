package com.tcz.listen.messages;

import com.tcz.listen.models.SongQueue;

public class RemoveSongQueueMessage extends WebSocketMessageBody {
    public RemoveSongQueueMessage(SongQueue songQueue, String reason) {
        put("command", "removeSongQueue");
        put("reason", reason);
        put("id", ""+songQueue.getSong().getId());
    }
}
