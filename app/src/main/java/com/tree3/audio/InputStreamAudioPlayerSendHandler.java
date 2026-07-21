package com.tree3.audio;

import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class InputStreamAudioPlayerSendHandler implements AudioSendHandler {
    private static final int BUFFER_SIZE = 3840;

    private volatile AudioInputStream audioStream;
    private final byte[] buffer = new byte[BUFFER_SIZE]; // reused, not reallocated per frame

    public synchronized boolean load(@NotNull InputStream inputStream) {
        try {
            // getAudioInputStream sniffs the header and rewinds, so it needs mark/reset;
            // streams straight off the classpath don't support it
            InputStream markable = inputStream.markSupported()
                    ? inputStream
                    : new BufferedInputStream(inputStream);
            AudioInputStream raw = AudioSystem.getAudioInputStream(markable);
            AudioFormat target = AudioSendHandler.INPUT_FORMAT;
            AudioInputStream converted = raw.getFormat().matches(target)
                    ? raw
                    : AudioSystem.getAudioInputStream(target, raw);

            closeCurrentStream(); // release whatever was previously loaded
            this.audioStream = converted;
            return true;
        } catch (IOException | UnsupportedAudioFileException e) {
            LogUtil.getLogger().error("Couldn't load audio", e);
            return false;
        }
    }

    public synchronized boolean isPlaying() {
        return audioStream != null;
    }

    public synchronized void stop() {
        closeCurrentStream();
        audioStream = null;
    }

    private void closeCurrentStream() {
        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                LogUtil.getLogger().warn("Failed to close previous audio stream", e);
            }
        }
    }

    @Override
    public boolean canProvide() {
        AudioInputStream stream = audioStream; // snapshot for this call
        if (stream == null) return false;

        try {
            int read = stream.read(buffer, 0, BUFFER_SIZE);
            if (read <= 0) {
                stop(); // auto-clear once exhausted, so load() can be called again immediately
                return false;
            }
            if (read < BUFFER_SIZE) {
                Arrays.fill(buffer, read, BUFFER_SIZE, (byte) 0);
            }
            return true;
        } catch (IOException e) {
            stop();
            return false;
        }
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(buffer);
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}
