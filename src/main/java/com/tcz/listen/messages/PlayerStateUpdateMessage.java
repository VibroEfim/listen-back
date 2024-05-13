package com.tcz.listen.messages;

import com.tcz.listen.models.Lobby;

public class PlayerStateUpdateMessage extends  WebSocketMessageBody {
    public PlayerStateUpdateMessage(Lobby lobby) {
        super();

        put("command", "playerStateUpdate");
        put("time", lobby.getTime().toString());
        put("maxTime", ""+lobby.getMaxTime().toString());
        put("playing", ""+lobby.isPlaying());
        put("repeatType", ""+lobby.getQueueState().toString());

        if (lobby.getCurrentSong() != null)
            put("currentSongId", ""+lobby.getCurrentSong().getSong().getId());
    }
}
