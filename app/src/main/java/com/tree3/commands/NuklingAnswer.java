package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.data.UserData;
import com.tree3.integrallogic.Helperclass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@BotCommand(name = "answerequation", description = "answers the question")
public class NuklingAnswer implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.NUMBER, "answer", "answer goes here");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        double answer = event.getOption("answer").getAsDouble();
        User user = event.getUser();
        Guild guild = UserData.servermap.get(user);
        Member member = guild.getMember(user);
        if(Helperclass.equalInMargin(answer, UserData.answermap.get(user), 0.5) ){
            guild.mute(user, false).queue();
            event.reply("Yess").queue();
            guild.modifyMemberRoles(member, UserData.rolemap.get(user)).queue();
        }
        else {
            event.reply("Noooooo").queue();
        }

    }
}
