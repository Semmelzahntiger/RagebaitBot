package com.tree3.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class SelfRemovingAudioEventListener implements AudioEventListener {
    private final AudioManager manager;
    private final AudioPlayer player;

    public SelfRemovingAudioEventListener(AudioManager manager, AudioPlayer player) {
        this.manager = manager;
        this.player = player;
    }

    @Override
    public void onEvent(AudioEvent audioEvent) {
        if(audioEvent instanceof TrackEndEvent) {
            manager.closeAudioConnection();
            manager.setSendingHandler(null);
            player.removeListener(this);
        }
    }
}
