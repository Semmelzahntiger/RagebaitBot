package com.tree3;

import com.tree3.cli.CLIReader;
import com.tree3.generated.GeneratedEventListeners;
import com.tree3.registers.CommandRegister;
import com.tree3.registers.Registry;
import com.tree3.util.LogUtil;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        LogUtil.getLogger().info("Starting Bot...");

        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("BOT_TOKEN");

        if (token == null) {
            LogUtil.getLogger().error("Token not found, Bot cannot be started");
            System.exit(1);
        }

        LogUtil.getLogger().info("Fetching Listeners...");
        Object[] listeners = GeneratedEventListeners.getListeners().toArray();
        LogUtil.getLogger().info("Detected {} listeners" , listeners.length);

        LogUtil.getLogger().info("Registering Commands...");
        CommandRegister.register();
        CommandData[] commands = Registry.getCommands().toArray(new CommandData[0]);
        LogUtil.getLogger().info("Found {} commands" , commands.length);
        JDA jda = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(listeners)
                .setActivity(Activity.customStatus("Ragebaiting"))
                .build();
        jda.updateCommands().addCommands(commands).queue();
        LogUtil.getLogger().info("Bot started");
        CLIReader reader = new CLIReader();
        reader.start();
    }
}