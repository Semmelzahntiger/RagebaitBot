package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import com.tree3.Main;
import com.tree3.json.LaTeXInterpreted;
import com.tree3.util.BotHttpClient;
import com.tree3.util.GSONSerializer;
import com.tree3.util.LaTeXParser;
import com.tree3.util.LogUtil;
import com.tree3.util.ThreadUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
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
                event.getHook().sendMessage("No Attachment found").queue();
                return;
            }
            Message.Attachment attachment = attachmentOption.getAsAttachment();
            if(!attachment.getFileExtension().equals("png")) {
                event.getHook().sendMessage("Attachment is not a PNG file").queue();
                return;
            }
            CompletableFuture<InputStream> file = attachment.getProxy().download();
            file.thenAccept(stream -> {
                ThreadUtil.runTaskAsync(() -> {
                    String response = Main.getHttpClient().interpretLatex(stream);
                    if(response.isEmpty()) {
                        event.getHook().sendMessage("Could not interpret LaTeX").queue();
                        return;
                    }
                    LaTeXInterpreted interpretation = GSONSerializer.getInterpreted(response);
                    Optional<InputStream> image = LaTeXParser.formulaToInputStream(interpretation.interpreted());
                    image.ifPresentOrElse(imageStream -> event.getHook()
                            .sendMessage("Interpreted LaTeX: " + interpretation.interpreted())
                            .addFiles(FileUpload.fromData(imageStream, UUID.randomUUID() + ".png"))
                            .queue(), () -> event.getHook().sendMessage("Could not interpret LaTeX").queue());
                });
            });
        } catch (Exception e) {
            event.getHook().sendMessage("Error while processing Request").queue();
            LogUtil.getLogger().error("Error while processing Request", e);
        }
    }
}
