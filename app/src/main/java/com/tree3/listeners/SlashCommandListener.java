package com.tree3.listeners;

import com.tree3.EventListener;
import com.tree3.registers.Registry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

@EventListener
public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getUser().isBot()) return;
        String eventName = event.getName();
        Registry.getCommandBehaviour(eventName).ifPresent(behaviour -> behaviour.accept(event));
    }
}
