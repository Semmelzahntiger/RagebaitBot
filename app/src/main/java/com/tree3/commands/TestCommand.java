package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@BotCommand(name = "testmest", description = "oidaleckeier")
public class TestCommand implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.INTEGER, "testnr", "test", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        int nr = event.getOption("testnr").getAsInt();
        event.reply(String.valueOf(nr)).queue();
    }
}
