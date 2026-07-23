package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

@BotCommand(name = "removebotfromvoice", description = "Removes the bot from any Voice Channel")
public class RemoveBotFromVoiceCommand implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {

    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        AudioManager manager = event.getGuild().getAudioManager();
        manager.closeAudioConnection();
        manager.setSendingHandler(null);
    }
}
