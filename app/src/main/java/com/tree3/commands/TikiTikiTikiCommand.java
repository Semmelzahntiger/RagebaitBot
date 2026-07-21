package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.audio.BotAudioSystem;
import com.tree3.util.ThreadUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@BotCommand(name = "tikitikitiki", description = "Tiki Tiki Tiki")
public class TikiTikiTikiCommand implements Command {

    private static final String TIKI = "tiki";

    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "target", "User to be TIKI'd", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("TIKI TIKI TIKI").queue();
        Member member = event.getMember();
        Member target = event.getOption("target").getAsMember();
        Guild guild = event.getGuild();
        if(target != null && member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
            VoiceChannel prev = target.getVoiceState().getChannel().asVoiceChannel();
            Optional<VoiceChannel> tikiChannel = guild.getVoiceChannelsByName(TIKI, true).stream().findFirst();
            if(tikiChannel.isPresent()) {
                VoiceChannel tiki = tikiChannel.get();
                runTiki(tiki, prev, target, guild);
            }
            else {
                guild.createVoiceChannel(TIKI).queue(tiki -> runTiki(tiki, prev, target, guild));
            }

        }
    }
    private void runTiki(VoiceChannel tikiChannel, VoiceChannel prevChannel, Member target, Guild guild) {
        guild.moveVoiceMember(target, tikiChannel).queue(_ -> {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(tikiChannel);
            audioManager.setSendingHandler(BotAudioSystem.getLavaAudioPlayerSendHandler());
            BotAudioSystem.lavaLoadResource("sounds/tiki.mp3");
            ThreadUtil.scheduleTask(() -> {
                BotAudioSystem.getLavaAudioPlayer().stopTrack();
                audioManager.setSendingHandler(null);
                guild.moveVoiceMember(target, prevChannel).queue();
                audioManager.closeAudioConnection();
            }, 5, TimeUnit.SECONDS);
        });
    }

}
