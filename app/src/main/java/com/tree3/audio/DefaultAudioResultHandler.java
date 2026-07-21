package com.tree3.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tree3.util.LogUtil;

import java.util.List;

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
        // ytsearch: results arrive here rather than in trackLoaded, so without this a search
        // would load fine and then play nothing
        AudioTrack track = audioPlaylist.getSelectedTrack();
        if (track == null) {
            List<AudioTrack> tracks = audioPlaylist.getTracks();
            if (tracks.isEmpty()) {
                LogUtil.getLogger().warn("Playlist '{}' has no tracks", audioPlaylist.getName());
                return;
            }
            track = tracks.getFirst();
        }
        player.playTrack(track);
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
