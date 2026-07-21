package com.tree3;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.tree3.audio.DefaultAudioResultHandler;
import com.tree3.cli.CLIReader;
import com.tree3.generated.GeneratedCommands;
import com.tree3.generated.GeneratedEventListeners;
import com.tree3.registers.CommandRegister;
import com.tree3.registers.Registry;
import com.tree3.util.BotHttpClient;
import com.tree3.util.LogUtil;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.List;

public class Main {
    private static final String token;

    private static final String latexInterpreterUri;
    private static final BotHttpClient httpClient;

    static {
        Dotenv dotenv = Dotenv.load();

        token = dotenv.get("BOT_TOKEN");
        latexInterpreterUri = dotenv.get("LATEX_INTERPRETER_URI");
        httpClient = new BotHttpClient();


    }

    public static void main(String[] args) {
        LogUtil.getLogger().info("Starting Bot...");
        if (token == null) {
            LogUtil.getLogger().error("Token not found, Bot cannot be started");
            System.exit(1);
        }

        LogUtil.getLogger().info("Fetching Listeners...");
        Object[] listeners = GeneratedEventListeners.getListeners().toArray();
        LogUtil.getLogger().info("Detected {} listeners" , listeners.length);

        LogUtil.getLogger().info("Registering Commands...");
        CommandRegister.register();
        for (GeneratedCommand command : GeneratedCommands.getCommands()) {
            Registry.addCommand(command);
        }
        List<CommandData> commands = Registry.getCommands();
        LogUtil.getLogger().info("Found {} commands" , commands.size());
        JDA jda = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EXPRESSIONS,
                        GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(listeners)
                .setAudioModuleConfig(
                        new AudioModuleConfig().withDaveSessionFactory(new JDaveSessionFactory())
                )
                .enableCache(CacheFlag.SOUNDBOARD_SOUNDS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.customStatus("Ragebaiting"))
                .build();
        jda.updateCommands().addCommands(commands).queue();
        LogUtil.getLogger().info("Bot started");
        CLIReader reader = new CLIReader();
        reader.start();


    }
    public static String getLatexInterpreterUri() {
        return latexInterpreterUri;
    }
    public static BotHttpClient getHttpClient() {
        return httpClient;
    }

}