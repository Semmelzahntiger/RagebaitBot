package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.audio.BotAudioSystem;
import com.tree3.audio.InputStreamAudioPlayerSendHandler;
import com.tree3.util.LogUtil;
import com.tree3.util.ThreadUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@BotCommand(name = "invadewith", description = "Invades with the Attached mp3 file")
public class InvadeWithCommand implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.ATTACHMENT, "audiofile", "MP3 file to be played", true);
        data.addOption(OptionType.CHANNEL, "targetchannel", "Channel to invade", false);
        data.addOption(OptionType.INTEGER, "duration", "Invade Duration", false);
        data.addOption(OptionType.NUMBER, "volume", "Volume of played file", false);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("Received Command").queue();

        InputStreamAudioPlayerSendHandler audioPlayer = BotAudioSystem.getNewInputStreamAudioPlayerSendHandler();
        if(audioPlayer.isPlaying()) {
            event.getHook().sendMessage("Bot is currently busy").queue();
            return;
        }

        Member sender = event.getMember();
        // Check for Channel Argument or default to sender's channel
        OptionMapping channelOption = event.getOption("targetchannel");
        VoiceChannel targetChannel;
        if(channelOption != null) {
            targetChannel =  channelOption.getAsChannel().asVoiceChannel();
        }
        else {
            try {
                targetChannel = sender.getVoiceState().getChannel().asVoiceChannel();
            } catch (Exception e) {
                LogUtil.getLogger().warn("sender not in Channel and no channel was given");
                targetChannel = null;
            }
        }
        if(targetChannel == null) {
            event.getHook().sendMessage("Nothing to invade").queue();
            return;
        }

        double volume = event.getOption("volume") != null ? event.getOption("volume").getAsDouble() : audioPlayer.getVolume();
        int duration = event.getOption("duration") != null ? event.getOption("duration").getAsInt() : -1;

        CompletableFuture<InputStream> data = event.getOption("audiofile").getAsAttachment().getProxy().download();
        VoiceChannel finalTargetChannel = targetChannel;

        // Connect and Play Audio until audio is finished
        data.thenAccept(inputStream -> {
            boolean loaded = audioPlayer.loadWithFFMPEG(inputStream);
            if(!loaded) {
                event.getHook().sendMessage("Could not load audio file").queue();
                return;
            }
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(finalTargetChannel);
            audioPlayer.setVolume(volume);
            audioManager.setSendingHandler(audioPlayer);

            if(duration > 0) {
                ThreadUtil.scheduleTask(() -> {
                    audioManager.setSendingHandler(null);
                    audioManager.closeAudioConnection();
                    audioPlayer.stop();
                }, duration, TimeUnit.SECONDS);
            }
            else {
                audioPlayer.attachStreamEndedListener("trackEnded", () -> {
                    audioPlayer.detachStreamEndedListener("trackEnded");
                    ThreadUtil.scheduleTask(() -> {
                        audioManager.setSendingHandler(null);
                        audioManager.closeAudioConnection();
                    }, 2, TimeUnit.SECONDS);
                });
            }
        });

        data.exceptionally(throwable -> {
            event.getHook().sendMessage("File couldn't be read").queue();
            return null;
        });
    }
}
