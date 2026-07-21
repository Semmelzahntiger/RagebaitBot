package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.Main;
import com.tree3.util.BotHttpClient;
import com.tree3.util.LogUtil;
import com.tree3.util.ThreadUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@BotCommand(name = "interpret", description = "Interprets Handwriting to LaTeX")
public class InterpretCommand implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.ATTACHMENT, "png", "png to interpret", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("Request received, processing...").queue();
        try {
            OptionMapping attachmentOption = event.getOption("png");
            if(attachmentOption == null ) {
                event.reply("No Attachment found").queue();
                return;
            }
            Message.Attachment attachment = attachmentOption.getAsAttachment();
            if(!attachment.getFileExtension().equals("png")) {
                event.reply("Attachment is not a PNG file").queue();
                return;
            }
            CompletableFuture<InputStream> file = attachment.getProxy().download();
            file.thenAccept(stream -> {
                ThreadUtil.runTaskAsync(() -> {
                    String response = Main.getHttpClient().interpretLatex(stream);
                    event.getHook().sendMessage("Response Received: " + response).queue();
                });
            });
        } catch (Exception e) {
            event.getHook().sendMessage("Error while processing Request").queue();
            LogUtil.getLogger().error("Error while processing Request", e);
        }
    }
}
