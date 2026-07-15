package com.tree3.registers;

import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CommandRegister {

    public static void register() {
        Registry.addCommand("tree", "prints tree", slash -> {
            slash.addOption(OptionType.INTEGER, "n", "n-th tree",true);
        }, getTreeBehaviour());
    }

    private static Consumer<SlashCommandInteractionEvent> getTreeBehaviour() {
        return event -> {
            String n = event.getOption("n").getAsString();
            int nth = Integer.parseInt(n);
            event.reply("Noooooo").queue();

            for (int i = 1; i <= nth; i++) {
                event.getChannel().sendMessage("I love TREE(" + i + ")").queue();

            }
        };
    }
}
