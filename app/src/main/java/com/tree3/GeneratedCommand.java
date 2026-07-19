package com.tree3;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.function.Consumer;

public record GeneratedCommand(String name, String description,
                               Consumer<SlashCommandData> slashCommandDataConsumer,
                               Consumer<SlashCommandInteractionEvent> eventConsumer) {
}
