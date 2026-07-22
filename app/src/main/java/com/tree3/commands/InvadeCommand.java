package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.audio.BotAudioSystem;
import com.tree3.util.LogUtil;
import com.tree3.util.ThreadUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.TimeUnit;

@BotCommand(name = "invade", description = "invades the invokers VoiceChannel")
public class InvadeCommand implements Command {

    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.STRING, "url", "url to play", false);
        data.addOption(OptionType.INTEGER, "duration", "Duration", false);
        data.addOption(OptionType.CHANNEL, "targetchannel", "Channel to invade", false);
        data.addOption(OptionType.NUMBER, "volume", "Volume to invade", false);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("Wow").queue();

        OptionMapping url = event.getOption("url");
        OptionMapping duration = event.getOption("duration");
        int actualDuration;
        if(duration != null) {
            actualDuration = duration.getAsInt();
        }
        else {
            actualDuration = 5;
        }

        Member sender = event.getMember();
        // Check for Channel Argument or default to sender's channel
        OptionMapping channelOption = event.getOption("targetchannel");
        VoiceChannel targetChannel;
        if(channelOption != null) {
            targetChannel = channelOption.getAsChannel().asVoiceChannel();
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

        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(targetChannel);
        // LavaPlayer decodes the mp3 and emits Opus; javax.sound only reads WAVE/AU/AIFF
        audioManager.setSendingHandler(BotAudioSystem.getLavaAudioPlayerSendHandler());
        // the resource lives in resources/sounds/, so the classpath name is prefixes
        if(url != null && !url.getAsString().isEmpty()) {
            BotAudioSystem.lavaLoadItem(url.getAsString());
        }
        else {
            BotAudioSystem.lavaLoadResource("sounds/tiki.mp3");
        }
        int prevVolume = BotAudioSystem.getLavaAudioPlayer().getVolume();
        BotAudioSystem.getLavaAudioPlayer().setVolume((int) (event.getOption("volume") != null ? event.getOption("volume").getAsDouble() : 1.0) * 100);
        ThreadUtil.scheduleTask(() -> {
            BotAudioSystem.getLavaAudioPlayer().stopTrack();
            audioManager.setSendingHandler(null);
            audioManager.closeAudioConnection();
            BotAudioSystem.getLavaAudioPlayer().setVolume(prevVolume);
        }, actualDuration, TimeUnit.SECONDS);
    }
}
