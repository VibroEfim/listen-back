package com.tcz.listen.messages;

import com.tcz.listen.models.SongQueue;
import com.tcz.listen.services.AudioService;

public class UpdateSongQueueMessage extends WebSocketMessageBody {
    public UpdateSongQueueMessage(SongQueue songQueue, String reason, long maxTime, long likeId) {
        put("command", "songQueueAdd");
        put("reason", reason);
        put("id", ""+songQueue.getSong().getId());
        put("position", ""+songQueue.getQueuePosition());
        put("maxTime", ""+maxTime);

        put("path", ""+songQueue.getSong().getPath());
        put("name", ""+songQueue.getSong().getName());
        put("author", ""+songQueue.getSong().getAuthor().getName());
        put("views", ""+songQueue.getSong().getViews());
        put("likeId", ""+likeId);
    }
}
