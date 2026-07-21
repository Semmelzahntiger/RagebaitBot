package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.data.UserData;
import com.tree3.integrallogic.Helperclass;
import com.tree3.integrallogic.IntegralGenerate;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

@BotCommand(name = "equationcannon", description = "nothing ever happens")
public class Nukling  implements Command {

    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "user", "Wen triffts diesmal?");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        int[] a = new IntegralGenerate().getIntegralProblem().getBounds();
        int[] b = new IntegralGenerate().getIntegralProblem().getBounds();
        User user = event.getOption("user").getAsUser();
        String id = user.getId();
        Guild guild = event.getGuild();
        Member member = guild.getMemberById(id);
        if(member == null) {
            event.reply("Error while fetching member").queue();
            return;
        }
        String reply = String.format("Löse Folgende Gleichung: %d" + "x" + "%+d" + " = " + "%d" + "x" + "%+d?", a[0], b[0], a[1], b[1]);
        List<Role> roles = member.getRoles();
        event.reply("Sendepause für " + user.getName()).queue();
        guild.modifyMemberRoles(member, List.of()).queue();
        guild.mute(user, true).queue();

        UserData.servermap.put(user, guild);
        UserData.answermap.put(user, (b[1]-b[0])/(a[0]-a[1]));
        UserData.rolemap.put(user, roles);
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage(reply).queue();
        });

    }
}
