package com.tree3.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotAudioSystem {

    private static final Map<String, Path> EXTRACTED_RESOURCES = new ConcurrentHashMap<>();

    private static final AudioPlayer audioPlayer;
    private static final AudioPlayerManager audioPlayerManager;
    private static final DefaultAudioResultHandler defaultAudioResultHandler;
    private static final LavaAudioPlayerSendHandler audioPlayerSendHandler;
    private static final InputStreamAudioPlayerSendHandler inputStreamAudioPlayerSendHandler;

    static {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        audioPlayerManager = playerManager;
        AudioSourceManagers.registerLocalSource(playerManager);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
        AudioSourceManagers.registerRemoteSources(playerManager, builtInYoutubeSource());
        audioPlayer = playerManager.createPlayer();
        defaultAudioResultHandler = new DefaultAudioResultHandler(audioPlayer);
        audioPlayerSendHandler = new LavaAudioPlayerSendHandler(audioPlayer);
        inputStreamAudioPlayerSendHandler = new InputStreamAudioPlayerSendHandler();
    }

    /** Referenced only so it can be excluded from registration; LavaPlayer deprecated it itself. */
    @SuppressWarnings("deprecation")
    private static Class<? extends AudioSourceManager> builtInYoutubeSource() {
        return com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class;
    }

    public static AudioPlayer getLavaAudioPlayer() {
        return audioPlayer;
    }
    public static AudioPlayerManager getLavaAudioPlayerManager() {
        return audioPlayerManager;
    }
    public static DefaultAudioResultHandler getDefaultAudioResultHandler() {
        return defaultAudioResultHandler;
    }
    public static LavaAudioPlayerSendHandler getLavaAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }
    public static InputStreamAudioPlayerSendHandler getInputStreamAudioPlayerSendHandler() {
        return inputStreamAudioPlayerSendHandler;
    }
    public static void lavaLoadItem(String path) {
        getLavaAudioPlayerManager().loadItem(path, getDefaultAudioResultHandler());
    }
    public static void lavaLoadItem(String path, AudioLoadResultHandler resultHandler) {
        getLavaAudioPlayerManager().loadItem(path, resultHandler);
    }
    public static boolean loadInputStream(InputStream stream) {
        return getInputStreamAudioPlayerSendHandler().load(stream);
    }
    public static boolean loadInputStreamWithFFMPEG(InputStream stream) {
        return getInputStreamAudioPlayerSendHandler().loadWithFFMPEG(stream);
    }
    public static Path extractResource(String resource) {
        return EXTRACTED_RESOURCES.computeIfAbsent(resource, name -> {
            try (InputStream stream = BotAudioSystem.class.getClassLoader().getResourceAsStream(name)) {
                if (stream == null) {
                    throw new IllegalArgumentException(name + " is not on the classpath");
                }
                int dot = name.lastIndexOf('.');
                Path file = Files.createTempFile("tree3-audio-", dot < 0 ? "" : name.substring(dot));
                file.toFile().deleteOnExit();
                Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
                return file;
            } catch (IOException e) {
                throw new UncheckedIOException("Could not extract " + name, e);
            }
        });
    }
    public static void lavaLoadResource(String resource) {
        lavaLoadItem(extractResource(resource).toAbsolutePath().toString());
    }
}
