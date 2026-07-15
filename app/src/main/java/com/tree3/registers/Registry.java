package com.tree3.registers;

import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Registry {
    private static final Map<String, Consumer<SlashCommandInteractionEvent>> definedCommands = new HashMap<>();
    private static final List<CommandData> commands = new ArrayList<>();

    public static void addCommand(String commandName, String description, Consumer<SlashCommandData> slashCommandModifier, Consumer<SlashCommandInteractionEvent> onEvent) {
        if(definedCommands.containsKey(commandName)) {
            LogUtil.getLogger().error("Command {} is already registered!", commandName);
        }
        SlashCommandData command = Commands.slash(commandName, description);
        slashCommandModifier.accept(command);
        commands.add(command);
        definedCommands.put(commandName, onEvent);
    }

    public static List<CommandData> getCommands() {
        return commands;
    }
    public static Optional<Consumer<SlashCommandInteractionEvent>> getCommandBehaviour(String commandName) {
        return Optional.ofNullable(definedCommands.get(commandName));
    }
}
