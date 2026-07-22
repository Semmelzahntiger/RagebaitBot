package com.tree3.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.sound.sampled.AudioSystem;
import java.nio.ByteBuffer;

public class LavaAudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayerManager audioPlayerManager;
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;
    private double volume = 1.0f;

    public LavaAudioPlayerSendHandler(AudioPlayer audioPlayer, AudioPlayerManager audioPlayerManager) {
        this.audioPlayer = audioPlayer;
        this.audioPlayerManager = audioPlayerManager;
    }

    @Override
    public boolean canProvide() {
       lastFrame = audioPlayer.provide();
        LogUtil.getLogger().debug("canProvide" + (lastFrame != null));
       return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] bytes = lastFrame.getData();
        return ByteBuffer.wrap(bytes);
    }

    public boolean isPlaying() {
        return lastFrame != null;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }
    public void loadItem(String url) {
        getAudioPlayerManager().loadItem(url, BotAudioSystem.getDefaultAudioResultHandler(getAudioPlayer()));
    }
    public void loadItem(AudioReference reference) {
        getAudioPlayerManager().loadItem(reference, BotAudioSystem.getDefaultAudioResultHandler(getAudioPlayer()));
    }
    public void loadItem(String url, AudioLoadResultHandler handler) {
        getAudioPlayerManager().loadItem(url, handler);
    }
    public void loadItem(AudioReference reference, AudioLoadResultHandler handler) {
        getAudioPlayerManager().loadItem(reference, handler);
    }
    public void loadResource(String resource) {
        loadItem(BotAudioSystem.extractResource(resource).toAbsolutePath().toString());
    }
    public void loadResource(String resource, AudioLoadResultHandler handler) {
        loadItem(BotAudioSystem.extractResource(resource).toAbsolutePath().toString(), handler);
    }
}
