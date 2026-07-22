package com.tree3.audio;

import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InputStreamAudioPlayerSendHandler implements AudioSendHandler {
    private static final int BUFFER_SIZE = 3840;
    private final Map<String, Runnable> onExhaustedListeners = new HashMap<>();
    private double volume = 1.0f;

    private volatile AudioInputStream audioStream;
    private final byte[] buffer = new byte[BUFFER_SIZE]; // reused, not reallocated per frame

    public synchronized boolean load(@NotNull InputStream inputStream) {
        try {
            InputStream markable = inputStream.markSupported()
                    ? inputStream
                    : new BufferedInputStream(inputStream);
            AudioInputStream raw = AudioSystem.getAudioInputStream(markable);
            AudioFormat target = AudioSendHandler.INPUT_FORMAT;
            AudioInputStream converted = raw.getFormat().matches(target)
                    ? raw
                    : AudioSystem.getAudioInputStream(target, raw);
            LogUtil.getLogger().info("Raw format: " + raw.getFormat());
            LogUtil.getLogger().info("Target format: " + target);
            LogUtil.getLogger().info("Matches: " + raw.getFormat().matches(target));
            LogUtil.getLogger().info("Converted format: " + converted.getFormat());
            closeCurrentStream(); // release whatever was previously loaded
            this.audioStream = converted;
            return true;
        } catch (IOException | UnsupportedAudioFileException e) {
            LogUtil.getLogger().error("Couldn't load audio", e);
            return false;
        }
    }
    public synchronized boolean loadWithFFMPEG(@NotNull InputStream inputStream) {
        try {
            AudioInputStream audioStream = convertWithFFMPEG(inputStream);
            if(audioStream == null) {
                return false;
            }
            this.audioStream = audioStream;
            return true;
        } catch (Exception e) {
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

    public void closeCurrentStream() {
        if (audioStream != null) {
            try {
                audioStream.close();
                audioStream = null;
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
                for (Runnable runnable : onExhaustedListeners.values()) {
                    runnable.run();
                }
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
        applyVolume(buffer, getVolume());
        return ByteBuffer.wrap(buffer);
    }

    @Override
    public boolean isOpus() {
        return false;
    }

    public void attachStreamEndedListener(String name, Runnable listener) {
        onExhaustedListeners.put(name, listener);
    }
    public void detachStreamEndedListener(String name) {
        onExhaustedListeners.remove(name);
    }
    public void setVolume(double volume) {
        this.volume = volume;
    }
    public double getVolume() {
        return volume;
    }
    public void resetVolume() {
        volume = 1.0f;
    }
    private void applyVolume(byte[] buffer, double volume) {
        for (int i = 0; i < buffer.length; i += 2) {
            // big-endian 16-bit sample
            short sample = (short) ((buffer[i] << 8) | (buffer[i + 1] & 0xFF));
            int scaled = (int) (sample * volume);
            // clamp to avoid overflow/clipping distortion
            scaled = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, scaled));
            buffer[i]     = (byte) ((scaled >> 8) & 0xFF);
            buffer[i + 1] = (byte) (scaled & 0xFF);
        }
    }
    private AudioInputStream convertWithFFMPEG(InputStream inputStream) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", "pipe:0",
                    "-f", "s16be",
                    "-ar", "48000",
                    "-ac", "2",
                    "-loglevel", "error", // suppress ffmpeg's normal chatty stderr output
                    "pipe:1"
            );
            Process process = pb.start();

            Thread feeder = new Thread(() -> {
                try (OutputStream ffmpegIn = process.getOutputStream()) {
                    inputStream.transferTo(ffmpegIn);
                } catch (IOException e) {
                    LogUtil.getLogger().error("Couldn't convert", e);
                }
            });
            feeder.setDaemon(true);
            feeder.start();

            byte[] pcmData = process.getInputStream().readAllBytes();

            int exitCode = process.waitFor();
            feeder.join();

            if (exitCode != 0) {
                LogUtil.getLogger().error("Couldn't convert FFMPEG stream");
                return null;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(pcmData);
            long frameLength = pcmData.length / AudioSendHandler.INPUT_FORMAT.getFrameSize();


            return new AudioInputStream(bais, AudioSendHandler.INPUT_FORMAT, frameLength);

        } catch (IOException | InterruptedException e) {
            LogUtil.getLogger().error("IO Exception while converting audio or interrupted", e);
            return null;
        }
    }
}
