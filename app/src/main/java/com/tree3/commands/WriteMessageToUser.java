package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
@BotCommand(name = "message", description = "let the bot write a message to the user")
public class WriteMessageToUser implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "user", "which user do you want to send a message to?", true);
        data.addOption(OptionType.STRING, "message", "the message goes here", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        User user = event.getOption("user").getAsUser();
        String message = event.getOption("message").getAsString();
        user.openPrivateChannel().queue( channel -> {
                    channel.sendMessage(message).queue();
            }
        );
        event.reply("Die Nachricht wurde gesendet").queue();
    }
}
