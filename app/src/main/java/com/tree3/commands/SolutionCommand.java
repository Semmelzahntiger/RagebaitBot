package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
@BotCommand(name = "solution", description = "Propose your Solution")
public class SolutionCommand implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.ATTACHMENT, "solution", "Your Solution", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("Unimplemented").queue();
    }
}
