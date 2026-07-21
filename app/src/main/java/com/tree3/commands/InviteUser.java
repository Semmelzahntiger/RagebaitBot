package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@BotCommand(name = "invite", description = "invite a User to your voicechannel")
public class InviteUser implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "user", "Which user do you want to invite?", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        User targeteduser = event.getOption("user").getAsUser();
        String id = user.getId();
        Guild guild = event.getGuild();
        Member member = guild.getMemberById(id);
        // Channel channel = member.getVoiceState().getChannel();
        if (member.getVoiceState() == null) {
            event.reply("Du bist ja lustig").queue();
        } else {
            targeteduser.openPrivateChannel().queue(
                    privateChannel -> {
                        privateChannel.sendMessage("test").queue(); // channel.getJDA().getInviteUrl()
                    }
            );
            event.reply("invite wurde versendet").queue();
        }
    }
}
