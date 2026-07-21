package com.tree3.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tree3.util.LogUtil;

public class DefaultAudioResultHandler implements AudioLoadResultHandler {
    private final AudioPlayer player;
    public DefaultAudioResultHandler(AudioPlayer audioPlayer) {
        this.player = audioPlayer;
    }
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        player.playTrack(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {

    }

    @Override
    public void noMatches() {
        LogUtil.getLogger().warn("No Audio found for File");
    }

    @Override
    public void loadFailed(FriendlyException e) {
        LogUtil.getLogger().warn("Failed to load Audio", e);
    }
}
