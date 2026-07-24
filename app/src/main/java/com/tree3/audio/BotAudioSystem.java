package com.tree3.audio;

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
    private static final AudioPlayerManager PLAYER_MANAGER = new DefaultAudioPlayerManager();
    static {
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
        PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager(true));
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER, builtInYoutubeSource());
    }
    private static final Map<Long, LavaAudioPlayerSendHandler> GUILD_HANDLERS = new ConcurrentHashMap<>();

    public static LavaAudioPlayerSendHandler getLavaAudioPlayerSendHandler(long guildId) {
        return GUILD_HANDLERS.computeIfAbsent(guildId, id -> {
            AudioPlayer audioPlayer = PLAYER_MANAGER.createPlayer();
            return new LavaAudioPlayerSendHandler(audioPlayer, PLAYER_MANAGER);
        });
    }
    public static InputStreamAudioPlayerSendHandler getNewInputStreamAudioPlayerSendHandler() {
        return new InputStreamAudioPlayerSendHandler();
    }

    private static Class<? extends AudioSourceManager> builtInYoutubeSource() {
        return com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class;
    }
    public static DefaultAudioResultHandler getDefaultAudioResultHandler(AudioPlayer audioPlayer) {
        return new DefaultAudioResultHandler(audioPlayer);
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
}
