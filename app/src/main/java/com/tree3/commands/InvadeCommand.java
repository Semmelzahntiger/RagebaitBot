package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.audio.BotAudioSystem;
import com.tree3.util.ThreadUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.TimeUnit;

@BotCommand(name = "invade", description = "invades the invokers VoiceChannel")
public class InvadeCommand implements Command {

    @Override
    public void registerCommandData(SlashCommandData data) {

    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if(member != null && member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
            VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
            AudioManager audioManager = event.getGuild().getAudioManager();
            event.reply("Wow").queue();
            audioManager.openAudioConnection(channel);
            // LavaPlayer decodes the mp3 and emits Opus; javax.sound only reads WAVE/AU/AIFF
            audioManager.setSendingHandler(BotAudioSystem.getLavaAudioPlayerSendHandler());
            // the resource lives in resources/sounds/, so the classpath name is prefixes
            BotAudioSystem.lavaLoadResource("sounds/tiki.mp3");
            ThreadUtil.scheduleTask(() -> {
                BotAudioSystem.getLavaAudioPlayer().stopTrack();
                audioManager.setSendingHandler(null);
                audioManager.closeAudioConnection();
            }, 5, TimeUnit.SECONDS);
        }
    }
}
