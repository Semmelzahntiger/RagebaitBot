package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.audio.BotAudioSystem;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

@BotCommand(name = "tikitikitiki", description = "Tiki Tiki Tiki")
public class TikiTikiTikiCommand implements Command {

    private static final String TIKI = "tiki";

    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "target", "User to be TIKI'd", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        if(member != null && member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
            VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
            AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(channel);
            audioManager.setSendingHandler(BotAudioSystem.getLavaAudioPlayerSendHandler());

        }
    }

}
