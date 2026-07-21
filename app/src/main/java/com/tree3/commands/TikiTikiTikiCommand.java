package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@BotCommand(name = "tikitikitiki", description = "Tiki Tiki Tiki")
public class TikiTikiTikiCommand implements Command {

    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.USER, "target", "User to be TIKI'd", true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member bot = guild.getSelfMember();
        Member victim = event.getOption("target").getAsMember();
        if(victim.getVoiceState() == null || !victim.getVoiceState().inAudioChannel()) {
            event.reply("Victim out of reach").queue();
            return;
        }
        VoiceChannel previous = victim.getVoiceState().getChannel().asVoiceChannel();
        Optional<VoiceChannel> tikiChannel = guild.
                getVoiceChannelsByName("Tiki",true)
                .stream().findFirst();
        if(tikiChannel.isPresent()) {
            boolean success = runTiki(previous, tikiChannel.get(), victim, bot, guild);
            if(success) {
                event.reply("Successfully Tiki'd Victim").queue();
            }
            else {
                event.reply("Failed to Tiki Victim").queue();
            }
        }
        else {
            guild.createVoiceChannel("Tiki").queue(newTikiChannel -> {
                boolean success = runTiki(previous, newTikiChannel, victim, bot, guild);
                if(success) {
                    event.reply("Successfully Tiki'd Victim").queue();
                }
                else {
                    event.reply("Failed to Tiki Victim").queue();
                }
            });
        }
    }
    private boolean runTiki(VoiceChannel previous, VoiceChannel tikiChannel, Member victim, Member bot, Guild guild) {
        Optional<SoundboardSound> soundboardSound = guild.getSoundboardSoundsByName("Tiki", true).stream().findFirst();
        if(soundboardSound.isPresent()) {
            SoundboardSound sound = soundboardSound.get();
            guild.moveVoiceMember(victim, tikiChannel)
                    .queue(_ ->
                    guild.moveVoiceMember(bot, tikiChannel)
                            .queueAfter(1, TimeUnit.SECONDS, _ ->
                            tikiChannel.sendSoundboardSound(sound, sound.getId())
                                    .queueAfter(5,TimeUnit.SECONDS, _ -> {
                                        guild.moveVoiceMember(victim, previous).queue();
                                        bot.kick().queue();
                            })
                    ));
            return true;
        }
        return false;
    }
}
