package com.tree3;

import com.tree3.generated.GeneratedEventListeners;
import com.tree3.registers.CommandRegister;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String token = System.getenv("DISCORD_TOKEN");
        List<CommandData> commands = new ArrayList<>();
        CommandRegister.register(commands);

        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(GeneratedEventListeners.getListeners().toArray())
                .build();
        jda.updateCommands().addCommands(commands.toArray(new CommandData[0])).queue();
    }
}