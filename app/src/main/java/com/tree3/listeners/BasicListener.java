package com.tree3.listeners;

import com.tree3.EventListener;
import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


@EventListener
public class BasicListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.getMessage().getContentRaw().toLowerCase().contains("tree")) {
            event.getChannel().sendMessage("I love TREE(3)").queue();
        }
    }
}
