package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

@BotCommand(name = "obliterate", description = "Obliterate a user")
public class Obliterate implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "user", "Which user dou you want to obliterate", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("TIKI TIKI TIKI").queue();
        Member member = event.getMember();
        Member target = event.getOption("user").getAsMember();
        Guild guild = event.getGuild();
        VoiceChannel currentchannel = member.getVoiceState().getChannel().asVoiceChannel();
        List<VoiceChannel> channels =  guild.getVoiceChannels();
        List<VoiceChannel> treechannels = guild.getCategoriesByName("TREE(n) [Baum(n)]", true).stream().findFirst().get().getVoiceChannels();
        for (VoiceChannel channel : treechannels) {
            guild.moveVoiceMember(target, channel).queue();
        }
        guild.moveVoiceMember(target, currentchannel).queue();
    }
}
