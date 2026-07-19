package com.tree3;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {

    void registerCommandData(SlashCommandData data);

    void onCommand(SlashCommandInteractionEvent event);
}
